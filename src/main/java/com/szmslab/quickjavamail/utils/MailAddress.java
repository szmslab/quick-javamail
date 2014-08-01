/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.utils;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

/**
 * メールアドレス情報を格納するクラスです。
 *
 * @author szmslab
 */
public class MailAddress {

    /**
     * メールアドレス。
     */
    private String address;

    /**
     * 個人名。
     */
    private String personal;

    /**
     * コンストラクタです。
     *
     * @param address
     *            メールアドレス
     */
    public MailAddress(String address) {
        this.address = address;
    }

    /**
     * コンストラクタです。
     *
     * @param address
     *            メールアドレス
     * @param personal
     *            個人名
     */
    public MailAddress(String address, String personal) {
        this.address = address;
        this.personal = personal;
    }

    /**
     * InternetAddressに変換します。
     *
     * @return InternetAddressのインスタンス
     * @throws AddressException
     * @throws UnsupportedEncodingException
     */
    public InternetAddress toInternetAddress() throws AddressException, UnsupportedEncodingException {
        return toInternetAddress(null);
    }

    /**
     * InternetAddressに変換します。
     *
     * @param charset
     *            文字セット
     * @return InternetAddressのインスタンス
     * @throws AddressException
     * @throws UnsupportedEncodingException
     */
    public InternetAddress toInternetAddress(String charset) throws AddressException, UnsupportedEncodingException {
        if (StringUtils.isBlank(personal)) {
            return new InternetAddress(address);
        } else {
            return new InternetAddress(address, personal, charset);
        }
    }

    /**
     * メールアドレスを取得します。
     *
     * @return メールアドレス
     */
    public String getAddress() {
        return address;
    }

    /**
     * 個人名を取得します。
     *
     * @return 個人名
     */
    public String getPersonal() {
        return personal;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isBlank(personal)) {
            sb.append(address);
        } else {
            sb.append(personal).append("<").append(address).append(">");
        }
        return sb.toString();
    }

}
