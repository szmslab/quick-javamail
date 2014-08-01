/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.send;

import javax.mail.internet.InternetAddress;

import com.szmslab.quickjavamail.utils.MailProperties;

/**
 * JavaMail(SMTP)のプロパティを格納するクラスです。
 *
 * @author szmslab
 */
public class SmtpProperties extends MailProperties {

    /* (非 Javadoc)
     * @see com.szmslab.quickjavamail.utils.MailProperties#getDefaultProtocol()
     */
    @Override
    protected String getDefaultProtocol() {
        String protocol = "smtp";
        transportProtocol(protocol);
        return protocol;
    }

    /* (非 Javadoc)
     * @see com.szmslab.quickjavamail.utils.MailProperties#getSslProtocol()
     */
    @Override
    protected String getSslProtocol() {
        String protocol = "smtps";
        transportProtocol(protocol);
        return protocol;
    }

    /**
     * 送信プロトコルを設定します。
     *
     * @param protocol
     *            プロトコル
     */
    private void transportProtocol(String protocol) {
        setString("mail.transport.protocol", protocol);
        setString("mail.transport.protocol." + new InternetAddress().getType(), protocol);
    }

}
