/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.utils;

import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.StringUtils;

import com.sun.mail.util.MailSSLSocketFactory;

/**
 * JavaMailのプロパティを格納する抽象クラスです。
 *
 * @author szmslab
 */
abstract public class MailProperties {

    /**
     * JavaMailのプロパティ。
     */
    private Properties properties = new Properties();

    /**
     * プロトコル。
     */
    private String protocol;

    /**
     * 認証情報。
     */
    private Authenticator authenticator;

    /**
     * コンストラクタです。
     */
    public MailProperties() {
        this.protocol = getDefaultProtocol();
        connectiontimeout("60000");
        timeout("60000");
    }

    /**
     * JavaMailのプロパティを取得します。
     *
     * @return JavaMailのプロパティ
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * プロトコルを取得します。
     *
     * @return プロトコル
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 認証情報を取得します。
     *
     * @return 認証情報
     */
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * ホスト名を設定します。
     *
     * @param host
     *            ホスト名
     * @return 自身のインスタンス
     */
    public MailProperties host(String host) {
        setString("mail.host", host);
        setString(String.format("mail.%s.host", protocol), host);
        return this;
    }

    /**
     * ポート番号を設定します。
     *
     * @param port
     *            ポート番号
     * @return 自身のインスタンス
     */
    public MailProperties port(String port) {
        // SMTP：25、SMTP over SSL：465、STARTTLS：587
        // POP3(STARTTLS):110、POP3 over SSL:995、IMAP(STARTTLS):143、IMAP over SSL:993
        setString(String.format("mail.%s.port", protocol), port);
        return this;
    }

    /**
     * 接続タイムアウト時間（秒）を設定します。
     *
     * @param connectiontimeout
     *            接続タイムアウト時間（秒）
     * @return 自身のインスタンス
     */
    public MailProperties connectiontimeout(String connectiontimeout) {
        setString(String.format("mail.%s.connectiontimeout", protocol), connectiontimeout);
        return this;
    }

    /**
     * 読み取りタイムアウト時間（秒）を設定します。
     *
     * @param timeout
     *            読み取りタイムアウト時間（秒）
     * @return 自身のインスタンス
     */
    public MailProperties timeout(String timeout) {
        setString(String.format("mail.%s.timeout", protocol), timeout);
        return this;
    }

    /**
     * 認証情報を設定します。
     *
     * @param userName
     *            ユーザ名
     * @param password
     *            パスワード
     * @return 自身のインスタンス
     */
    public MailProperties authenticate(final String userName, final String password) {
        if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            };
            if (protocol.startsWith("smtp")) {
                setString(String.format("mail.%s.auth", protocol), Boolean.TRUE.toString());
            }
        }
        return this;
    }

    /**
     * STARTTLS情報を設定します。
     *
     * @return 自身のインスタンス
     */
    public MailProperties starttls() {
        starttls(false);
        return this;
    }

    /**
     * STARTTLS情報を設定します。
     *
     * @param isTrustedHost
     *            接続先が信頼されているホストかどうか
     * @return 自身のインスタンス
     */
    public MailProperties starttls(boolean isTrustedHost) {
        setString(String.format("mail.%s.starttls.enable", protocol), Boolean.TRUE.toString());
        if (isTrustedHost) {
            setString(String.format("mail.%s.ssl.trust", protocol), getString(String.format("mail.%s.host", protocol)));
        }
        return this;
    }

    /**
     * SSL情報を設定します。
     *
     * @return 自身のインスタンス
     * @throws GeneralSecurityException
     */
    public MailProperties ssl() throws GeneralSecurityException {
        ssl(false);
        return this;
    }

    /**
     * SSL情報を設定します。
     *
     * @param isTrustedHost
     *            接続先が信頼されているホストかどうか
     * @return 自身のインスタンス
     * @throws GeneralSecurityException
     */
    public MailProperties ssl(boolean isTrustedHost) throws GeneralSecurityException {
        String beforeProtocol = protocol;
        protocol = getSslProtocol();
        Properties newProperties = new Properties();
        for (Iterator<Map.Entry<Object, Object>> itr = properties.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<Object, Object> entry = itr.next();
            String key = (String)entry.getKey();
            key = key.replaceAll(beforeProtocol, protocol);
            newProperties.put(key, entry.getValue());
        }
        properties = newProperties;

        setString(String.format("mail.%s.ssl.enable", protocol), Boolean.TRUE.toString());
        if (isTrustedHost) {
            MailSSLSocketFactory factory = new MailSSLSocketFactory();
            factory.setTrustedHosts(new String[] {getString(String.format("mail.%s.host", protocol))});
            setObject(String.format("mail.%s.socketFactory", protocol), factory);
        } else {
            setString(String.format("mail.%s.socketFactory.class", protocol), SSLSocketFactory.class.getName());
        }
        setString(String.format("mail.%s.socketFactory.fallback", protocol), Boolean.FALSE.toString());
        setString(String.format("mail.%s.socketFactory.port", protocol), getString(String.format("mail.%s.port", protocol)));
        return this;
    }

    /**
     * デフォルトのプロトコルを取得します。
     *
     * @return デフォルトのプロトコル
     */
    abstract protected String getDefaultProtocol();

    /**
     * SSLのプロトコルを取得します。
     *
     * @return SSLのプロトコル
     */
    abstract protected String getSslProtocol();

    /**
     * JavaMailのプロパティ（文字列）を設定します。
     *
     * @param key
     *            JavaMailのプロパティ名
     * @param value
     *            対応する値
     */
    protected void setString(String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            properties.setProperty(key, value);
        }
    }

    /**
     * JavaMailのプロパティ（文字列）を取得します。
     *
     * @param key
     *            JavaMailのプロパティ名
     * @return 対応する値
     */
    protected String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * JavaMailのプロパティ（オブジェクト）を設定します。
     *
     * @param key
     *            JavaMailのプロパティ名
     * @param value
     *            対応する値
     */
    protected void setObject(String key, Object value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    /**
     * JavaMailのプロパティ（オブジェクト）を取得します。
     *
     * @param key
     *            JavaMailのプロパティ名
     * @return 対応する値
     */
    protected Object getObject(String key) {
        return properties.get(key);
    }

    /* (非 Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return properties.toString();
    }

}
