/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.receive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

import com.szmslab.quickjavamail.utils.AttachmentFile;
import com.szmslab.quickjavamail.utils.InlineImageFile;
import com.szmslab.quickjavamail.utils.MailAddress;

/**
 * 受信したメール情報を読み込むクラスです。
 *
 * @author szmslab
 */
public class MessageLoader {

    /**
     * 受信したメッセージをサーバから削除するかどうか。
     */
    private boolean isDeleted;

    /**
     * 受信したメッセージ。
     */
    private Message message;

    /**
     * 受信したメッセージの内容のキャッシュ。
     */
    private MessageContent contentCashe;

    /**
     * コンストラクタです。
     *
     * @param message
     *            受信したメッセージ
     */
    public MessageLoader(Message message) {
        this.message = message;
        this.isDeleted = false;
    }

    /**
     * コンストラクタです。
     *
     * @param message
     *            受信したメッセージ
     * @param isDeleted
     *            受信したメッセージをサーバから削除するかどうか
     */
    public MessageLoader(Message message, boolean isDeleted) {
        this.message = message;
        this.isDeleted = isDeleted;
    }

    /**
     * 受信したメッセージをサーバから削除するかどうかを取得します。
     *
     * @return 受信したメッセージをサーバから削除するかどうか
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * 受信したメッセージをサーバから削除するかどうかを設定します。
     *
     * @param isDeleted
     *            受信したメッセージをサーバから削除するかどうか
     */
    public void deleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 受信したメッセージを取得します。
     *
     * @return 受信したメッセージ
     */
    public Message getOriginalMessage() {
        return message;
    }

    /**
     * Message-IDを取得します。
     *
     * @return Message-ID
     * @throws MessagingException
     */
    public String getMessageId() throws MessagingException {
        return StringUtils.defaultString(StringUtils.join(message.getHeader("Message-ID"), ","));
    }

    /**
     * MUA情報を取得します。
     *
     * @return MUA情報
     * @throws MessagingException
     */
    public String getMessageUserAgent() throws MessagingException {
        String mua = StringUtils.join(message.getHeader("User-Agent"), ",");
        if (StringUtils.isBlank(mua)) {
            mua = StringUtils.defaultString(StringUtils.join(message.getHeader("X-Mailer"), ","));
        }
        return mua;
    }

    /**
     * 送信日を取得します。
     *
     * @return 送信日
     * @throws MessagingException
     */
    public Date getSentDate() throws MessagingException {
        return message.getSentDate();
    }

    /**
     * メッセージのサイズを取得します。
     *
     * @return メッセージのサイズ
     * @throws MessagingException
     */
    public int getSize() throws MessagingException {
        return message.getSize();
    }

    /**
     * メールアドレス（From）を取得します。
     *
     * @return メールアドレス（From）
     * @throws MessagingException
     */
    public List<MailAddress> getFromAddressList() throws MessagingException {
        return toMailAddressList((InternetAddress[]) message.getFrom());
    }

    /**
     * メールアドレス（ReplyTo）を取得します。
     *
     * @return メールアドレス（ReplyTo）
     * @throws MessagingException
     */
    public List<MailAddress> getReplyToAddressList() throws MessagingException {
        return toMailAddressList((InternetAddress[]) message.getReplyTo());
    }

    /**
     * メールアドレス（To）を取得します。
     *
     * @return メールアドレス（To）
     * @throws MessagingException
     */
    public List<MailAddress> getToAddressList() throws MessagingException {
        return toMailAddressList((InternetAddress[]) message.getRecipients(RecipientType.TO));
    }

    /**
     * メールアドレス（Cc）を取得します。
     *
     * @return メールアドレス（Cc）
     * @throws MessagingException
     */
    public List<MailAddress> getCcAddressList() throws MessagingException {
        return toMailAddressList((InternetAddress[]) message.getRecipients(RecipientType.CC));
    }

    /**
     * メールヘッダのプロパティを取得します。
     *
     * @return メールヘッダのプロパティ
     * @throws MessagingException
     */
    @SuppressWarnings("unchecked")
    public Properties getHeaders() throws MessagingException {
        Properties p = new Properties();
        for (Enumeration<Header> headers = message.getAllHeaders(); headers.hasMoreElements();) {
            Header header = headers.nextElement();
            p.setProperty(header.getName(), header.getValue());
        }
        return p;
    }

    /**
     * 件名を取得します。
     *
     * @return 件名
     * @throws MessagingException
     */
    public String getSubject() throws MessagingException {
        return message.getSubject();
    }

    /**
     * 本文（TEXT）を取得します。
     *
     * @return 本文（TEXT）
     * @throws MessagingException
     * @throws IOException
     */
    public String getText() throws MessagingException, IOException {
        return getContent().text;
    }

    /**
     * 本文（HTML）を取得します。
     *
     * @return 本文（HTML）
     * @throws MessagingException
     * @throws IOException
     */
    public String getHtml() throws MessagingException, IOException {
        return getContent().html;
    }

    /**
     * 添付ファイルを取得します。
     *
     * @return 添付ファイル
     * @throws MessagingException
     * @throws IOException
     */
    public List<AttachmentFile> getAttachmentFileList() throws MessagingException, IOException {
        return getContent().attachmentFileList;
    }

    /**
     * インライン画像ファイルを取得します。
     *
     * @return インライン画像ファイル
     * @throws MessagingException
     * @throws IOException
     */
    public List<InlineImageFile> getInlineImageFileList() throws MessagingException, IOException {
        return getContent().inlineImageFileList;
    }

    /**
     * 分割メールかどうかを取得します。
     *
     * @return 分割メールかどうか
     * @throws MessagingException
     */
    public boolean isPartial() throws MessagingException {
        return message.getContentType().indexOf("message/partial") >= 0;
    }

    /**
     * 分割メールの内容を取得します。
     *
     * @return 分割メールの内容
     * @throws MessagingException
     * @throws IOException
     */
    public ByteArrayInputStream getPartialContent() throws MessagingException, IOException {
        return getContent().partialContent;
    }

    /**
     * InternetAddressの配列をMailAddressのリストに変換します。
     *
     * @param addresses
     *            InternetAddressの配列
     * @return MailAddressのリスト
     */
    private List<MailAddress> toMailAddressList(InternetAddress[] addresses) {
        List<MailAddress> list = new ArrayList<MailAddress>();
        if (addresses != null) {
            for (InternetAddress address : addresses) {
                list.add(new MailAddress(address.getAddress(), address.getPersonal()));
            }
        }
        return list;
    }

    /**
     * 受信したメッセージの内容を取得します。
     *
     * @return 受信したメッセージの内容
     * @throws MessagingException
     * @throws IOException
     */
    private MessageContent getContent() throws MessagingException, IOException {
        if (contentCashe == null) {
            MessageContent msgContent = new MessageContent();
            Object c = message.getContent();
            if (c instanceof Multipart) {
                setMultipartContent((Multipart) c, msgContent);
            } else if (c instanceof ByteArrayInputStream) {
                msgContent.partialContent = (ByteArrayInputStream) c;
            } else {
                msgContent.text = c.toString();
            }
            contentCashe = msgContent;
        }
        return contentCashe;
    }

    /**
     * マルチパートの情報を取得し、MessageContentに設定します。
     *
     * @param multiPart
     *            マルチパート
     * @param msgContent
     *            受信したメッセージの内容
     * @throws MessagingException
     * @throws IOException
     */
    private void setMultipartContent(Multipart multiPart, MessageContent msgContent) throws MessagingException, IOException {
        for (int i = 0; i < multiPart.getCount(); i++) {
            Part part = multiPart.getBodyPart(i);
            if (part.getContentType().indexOf("multipart") >= 0) {
                setMultipartContent((Multipart) part.getContent(), msgContent);
            } else if (part.isMimeType("text/html")) {
                msgContent.html = part.getContent().toString();
            } else if (part.isMimeType("text/plain")) {
                msgContent.text = part.getContent().toString();
            } else {
                String disposition = part.getDisposition();
                if (Part.ATTACHMENT.equals(disposition)) {
                    msgContent.attachmentFileList.add(
                            new AttachmentFile(MimeUtility.decodeText(part.getFileName()), part.getDataHandler().getDataSource()));
                } else if (Part.INLINE.equals(disposition)) {
                    String cid = "";
                    if (part instanceof MimeBodyPart) {
                        MimeBodyPart mimePart = (MimeBodyPart) part;
                        cid = mimePart.getContentID();
                    }
                    msgContent.inlineImageFileList.add(
                            new InlineImageFile(cid, MimeUtility.decodeText(part.getFileName()), part.getDataHandler().getDataSource()));
                }
            }
        }
    }

    /**
     * 受信したメッセージの内容を格納するクラスです。
     *
     * @author szmslab
     */
    class MessageContent {

        /**
         * 本文(TEXT)。
         */
        public String text = "";

        /**
         * 本文(HTML)。
         */
        public String html = "";

        /**
         * 添付ファイル。
         */
        public List<AttachmentFile> attachmentFileList = new ArrayList<AttachmentFile>();

        /**
         * インライン画像ファイル。
         */
        public List<InlineImageFile> inlineImageFileList = new ArrayList<InlineImageFile>();

        /**
         * 分割メールの内容。
         */
        public ByteArrayInputStream partialContent = null;

    }

}
