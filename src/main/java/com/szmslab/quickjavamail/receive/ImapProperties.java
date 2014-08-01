/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.receive;

import com.szmslab.quickjavamail.utils.MailProperties;

/**
 * JavaMail(IMAP4)のプロパティを格納するクラスです。
 *
 * @author szmslab
 */
public class ImapProperties extends MailProperties {

    /* (非 Javadoc)
     * @see com.szmslab.quickjavamail.utils.MailProperties#getDefaultProtocol()
     */
    @Override
    protected String getDefaultProtocol() {
        return "imap";
    }

    /* (非 Javadoc)
     * @see com.szmslab.quickjavamail.utils.MailProperties#getSslProtocol()
     */
    @Override
    protected String getSslProtocol() {
        return "imaps";
    }

}
