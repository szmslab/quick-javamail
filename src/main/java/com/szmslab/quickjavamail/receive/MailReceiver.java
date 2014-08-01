/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.receive;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.lang3.StringUtils;

import com.szmslab.quickjavamail.utils.MailProperties;

/**
 * JavaMailによるメール受信を行うクラスです。
 *
 * @author szmslab
 */
public class MailReceiver {

    /**
     * JavaMailのプロパティ。
     */
    private MailProperties properties;

    /**
     * メール受信のデバッグログ出力有無 。
     */
    private boolean isDebug = false;

    /**
     * デフォルトのセッション使用有無 。
     */
    private boolean useDefaultSession = true;

    /**
     * 受信対象のフォルダ名。
     */
    private String folderName = "INBOX";

    /**
     * 読取専用でメッセージを受信するかどうか。
     */
    private boolean readonly = true;

    /**
     * コンストラクタです。
     *
     * @param properties
     *            JavaMailのプロパティ。
     */
    public MailReceiver(MailProperties properties) {
        this.properties = properties;
    }

    /**
     * メール受信のデバッグログ出力有無を設定します。
     *
     * @param isDebug
     *            デバッグログを出力するかどうか
     * @return 自身のインスタンス
     */
    public MailReceiver debug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    /**
     * デフォルトのセッション使用有無を設定します。
     *
     * @param useDefaultSession
     *            デフォルトのセッションを使用するかどうか
     * @return 自身のインスタンス
     */
    public MailReceiver useDefaultSession(boolean useDefaultSession) {
        this.useDefaultSession = useDefaultSession;
        return this;
    }

    /**
     * 受信対象のフォルダ名を設定します。
     *
     * @param folderName
     *            受信対象のフォルダ名
     * @return 自身のインスタンス
     */
    public MailReceiver folderName(String folderName) {
        if (StringUtils.isNotBlank(folderName)) {
            this.folderName = folderName;
        }
        return this;
    }

    /**
     * 読取専用でメッセージを受信するかどうかを設定します。
     *
     * @param readonly
     *            読取専用でメッセージを受信するかどうか
     * @return 自身のインスタンス
     */
    public MailReceiver readonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    /**
     * メールを受信します。
     *
     * @param callback
     *            メール受信結果1件を処理するコールバック
     * @throws Exception
     */
    public void execute(ReceiveIterationCallback callback) throws Exception {
        final Session session = useDefaultSession
                ? Session.getDefaultInstance(properties.getProperties(), properties.getAuthenticator())
                : Session.getInstance(properties.getProperties(), properties.getAuthenticator());
        session.setDebug(isDebug);

        Store store = null;
        Folder folder = null;
        try {
            store = session.getStore(properties.getProtocol());
            store.connect();

            folder = store.getFolder(folderName);
            folder.open(readonly ? Folder.READ_ONLY : Folder.READ_WRITE);

            final Message messages[] = folder.getMessages();
            for (Message message : messages) {
                MessageLoader loader = new MessageLoader(message, !readonly);
                boolean isContinued = callback.iterate(loader);
                if (!readonly && loader.isDeleted()) {
                    message.setFlag(Flags.Flag.DELETED, loader.isDeleted());
                }
                if (!isContinued) {
                    break;
                }
            }
        } finally {
            if (folder != null) {
                try {
                    folder.close(!readonly);
                } catch (MessagingException e) {
                    System.out.println(e);
                }
            }
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    System.out.println(e);
                }
            }
        }
    }

}
