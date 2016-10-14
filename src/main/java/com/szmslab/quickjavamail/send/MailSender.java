/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.send;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;

import com.szmslab.quickjavamail.utils.AttachmentFile;
import com.szmslab.quickjavamail.utils.InlineImageFile;
import com.szmslab.quickjavamail.utils.MailAddress;
import com.szmslab.quickjavamail.utils.MailProperties;

/**
 * JavaMailによるメール送信を行うクラスです。
 *
 * @author szmslab
 */
public class MailSender {

    /**
     * JavaMailのプロパティ。
     */
    private MailProperties properties;

    /**
     * メール送信のデバッグログ出力有無 。
     */
    private boolean isDebug = false;

    /**
     * デフォルトのセッション使用有無 。
     */
    private boolean useDefaultSession = true;

    /**
     * メールヘッダのプロパティ。
     */
    private Properties headers = new Properties();

    /**
     * 文字セット。
     */
    private String charset;

    /**
     * メールアドレス（From）。
     */
    private MailAddress fromAddress;

    /**
     * メールアドレス（ReplyTo）。
     */
    private List<MailAddress> replyToAddressList = new ArrayList<MailAddress>();

    /**
     * メールアドレス（To）。
     */
    private List<MailAddress> toAddressList = new ArrayList<MailAddress>();

    /**
     * メールアドレス（Cc）。
     */
    private List<MailAddress> ccAddressList = new ArrayList<MailAddress>();

    /**
     * メールアドレス（Bcc）。
     */
    private List<MailAddress> bccAddressList = new ArrayList<MailAddress>();

    /**
     * 件名。
     */
    private String subject;

    /**
     * 本文(TEXT)。
     */
    private String text;

    /**
     * 本文(HTML)。
     */
    private String html;

    /**
     * 添付ファイル。
     */
    private List<AttachmentFile> attachmentFileList = new ArrayList<AttachmentFile>();

    /**
     * インライン画像ファイル。
     */
    private List<InlineImageFile> inlineImageFileList = new ArrayList<InlineImageFile>();

    /**
     * コンストラクタです。
     *
     * @param properties
     *            JavaMailのプロパティ。
     */
    public MailSender(MailProperties properties) {
        this.properties = properties;
        charset("ISO-2022-JP", "7bit");
    }

    /**
     * メール送信のデバッグログ出力有無を設定します。
     *
     * @param isDebug
     *            デバッグログを出力するかどうか
     * @return 自身のインスタンス
     */
    public MailSender debug(boolean isDebug) {
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
    public MailSender useDefaultSession(boolean useDefaultSession) {
        this.useDefaultSession = useDefaultSession;
        return this;
    }

    /**
     * メールヘッダのプロパティを設定します。
     *
     * @param key
     *            メールヘッダのプロパティ名
     * @param value
     *            対応する値
     * @return 自身のインスタンス
     */
    public MailSender header(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            if (value == null) {
                headers.remove(key);
            } else {
                headers.setProperty(key, value);
            }
        }
        return this;
    }

    /**
     * 文字セットを設定します。
     *
     * @param charset
     *            文字セット
     * @return 自身のインスタンス
     */
    public MailSender charset(String charset) {
        if (StringUtils.isNotBlank(charset)) {
            this.charset = charset;
        }
        return this;
    }

    /**
     * 文字セットを設定します。
     *
     * @param charset
     *            文字セット
     * @param contentTransferEncoding
     *            Content-Transfer-Encodingの値
     * @return 自身のインスタンス
     */
    public MailSender charset(String charset, String contentTransferEncoding) {
        charset(charset);
        header("Content-Transfer-Encoding", contentTransferEncoding);
        return this;
    }

    /**
     * メールアドレス（From）を設定します。
     *
     * @param address
     *            メールアドレス（From）
     * @return 自身のインスタンス
     */
    public MailSender from(MailAddress address) {
        fromAddress = address;
        return this;
    }

    /**
     * メールアドレス（ReplyTo）を設定します。
     *
     * @param address
     *            メールアドレス（ReplyTo）
     * @return 自身のインスタンス
     */
    public MailSender replyTo(MailAddress... addresses) {
        for (MailAddress address : addresses) {
            if (address != null) {
                replyToAddressList.add(address);
            }
        }
        return this;
    }

    /**
     * メールアドレス（To）を設定します。
     *
     * @param address
     *            メールアドレス（To）
     * @return 自身のインスタンス
     */
    public MailSender to(MailAddress... addresses) {
        for (MailAddress address : addresses) {
            if (address != null) {
                toAddressList.add(address);
            }
        }
        return this;
    }

    /**
     * メールアドレス（Cc）を設定します。
     *
     * @param address
     *            メールアドレス（Cc）
     * @return 自身のインスタンス
     */
    public MailSender cc(MailAddress... addresses) {
        for (MailAddress address : addresses) {
            if (address != null) {
                ccAddressList.add(address);
            }
        }
        return this;
    }

    /**
     * メールアドレス（Bcc）を設定します。
     *
     * @param address
     *            メールアドレス（Bcc）
     * @return 自身のインスタンス
     */
    public MailSender bcc(MailAddress... addresses) {
        for (MailAddress address : addresses) {
            if (address != null) {
                bccAddressList.add(address);
            }
        }
        return this;
    }

    /**
     * 件名を設定します。
     *
     * @param subject
     *            件名
     * @return 自身のインスタンス
     */
    public MailSender subject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * 本文(TEXT)を設定します。
     *
     * @param text
     *            本文(TEXT)
     * @return 自身のインスタンス
     */
    public MailSender text(String text) {
        this.text = text;
        return this;
    }

    /**
     * 本文(HTML)を設定します。
     *
     * @param html
     *            本文(HTML)
     * @return 自身のインスタンス
     */
    public MailSender html(String html) {
        this.html = html;
        return this;
    }

    /**
     * 添付ファイルを設定します。
     *
     * @param files
     *            添付ファイル
     * @return 自身のインスタンス
     */
    public MailSender attachmentFiles(AttachmentFile... files) {
        for (AttachmentFile file : files) {
            if (file != null) {
                attachmentFileList.add(file);
            }
        }
        return this;
    }

    /**
     * インライン画像ファイルを設定します。
     *
     * @param files
     *            インライン画像ファイル
     * @return 自身のインスタンス
     */
    public MailSender inlineImageFiles(InlineImageFile... files) {
        for (InlineImageFile file : files) {
            if (file != null) {
                inlineImageFileList.add(file);
            }
        }
        return this;
    }

    /**
     * メールを送信します。
     *
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public void execute() throws UnsupportedEncodingException, MessagingException {
        final Session session = useDefaultSession
                ? Session.getDefaultInstance(properties.getProperties(), properties.getAuthenticator())
                : Session.getInstance(properties.getProperties(), properties.getAuthenticator());
        session.setDebug(isDebug);

        final MimeMessage message = new MimeMessage(session);

        message.setFrom(toInternetAddress(fromAddress));
        message.setReplyTo(toInternetAddresses(replyToAddressList));
        message.addRecipients(Message.RecipientType.TO, toInternetAddresses(toAddressList));
        message.addRecipients(Message.RecipientType.CC, toInternetAddresses(ccAddressList));
        message.addRecipients(Message.RecipientType.BCC, toInternetAddresses(bccAddressList));
        message.setSubject(subject, charset);

        setContent(message);

        message.setSentDate(new Date());

        Transport.send(message);
    }

    /**
     * MailAddressのリストをInternetAddressの配列に変換します。
     *
     * @param addressList
     *            MailAddressのリスト
     * @return InternetAddressの配列
     * @throws AddressException
     * @throws UnsupportedEncodingException
     */
    private InternetAddress[] toInternetAddresses(List<MailAddress> addressList) throws AddressException, UnsupportedEncodingException {
        List<InternetAddress> list = new ArrayList<InternetAddress>();
        for (MailAddress address : addressList) {
            InternetAddress iAddress = toInternetAddress(address);
            if (iAddress != null) {
                list.add(iAddress);
            }
        }
        return list.toArray(new InternetAddress[list.size()]);
    }

    /**
     * MailAddressをInternetAddressに変換します。
     *
     * @param address
     *            MailAddressのインスタンス
     * @return InternetAddressのインスタンス
     * @throws AddressException
     * @throws UnsupportedEncodingException
     */
    private InternetAddress toInternetAddress(MailAddress address) throws AddressException, UnsupportedEncodingException {
        if (address == null) {
            return null;
        }
        return address.toInternetAddress(charset);
    }

    /**
     * MimeMultipartを作成します（mulpart/mixed）。
     *
     * @return MimeMultipart（mulpart/mixed）のインスタンス
     */
    private MimeMultipart createMixedMimeMultipart() {
        return new MimeMultipart();
    }

    /**
     * MimeMultipartを作成します（mulpart/alternative）。
     *
     * @return MimeMultipart（mulpart/alternative）のインスタンス
     */
    private MimeMultipart createAlternativeMimeMultipart() {
        return new MimeMultipart("alternative");
    }

    /**
     * MimeMultipartを作成します（mulpart/related）。
     *
     * @return MimeMultipart（mulpart/related）のインスタンス
     */
    private MimeMultipart createRelatedMimeMultipart() {
        return new MimeMultipart("related");
    }

    /**
     * MimeBodyPartを作成します（text/plain）。
     *
     * @return MimeBodyPart（text/plain）のインスタンス
     * @throws MessagingException
     */
    private MimeBodyPart createTextPart() throws MessagingException {
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(StringUtils.defaultString(text), charset);
        setHeaderToPart(textPart);
        return textPart;
    }

    /**
     * MimeBodyPartを作成します（text/html）。
     *
     * @return MimeBodyPart（text/html）のインスタンス
     * @throws MessagingException
     */
    private MimeBodyPart createHtmlPart() throws MessagingException {
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setText(StringUtils.defaultString(html), charset, "html");
        setHeaderToPart(htmlPart);
        return htmlPart;
    }

    /**
     * MimeBodyPartを作成します（Content-Disposition: attachment）。
     *
     * @param file
     *            添付ファイル
     * @return MimeBodyPart（Content-Disposition: attachment）のインスタンス
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private MimeBodyPart createAttachmentPart(AttachmentFile file) throws MessagingException, UnsupportedEncodingException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setFileName(MimeUtility.encodeText(file.getFileName(), charset, null));
        attachmentPart.setDataHandler(new DataHandler(file.getDataSource()));
        attachmentPart.setDisposition(MimeBodyPart.ATTACHMENT);
        return attachmentPart;
    }

    /**
     * MimeBodyPartを作成します（Content-Disposition: inline）。
     *
     * @param file
     *            インライン画像ファイル
     * @return MimeBodyPart（Content-Disposition: inline）のインスタンス
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private MimeBodyPart createImagePart(InlineImageFile file) throws MessagingException, UnsupportedEncodingException {
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.setContentID(file.getContentId());
        imagePart.setFileName(MimeUtility.encodeText(file.getFileName(), charset, null));
        imagePart.setDataHandler(new DataHandler(file.getDataSource()));
        imagePart.setDisposition(MimeBodyPart.INLINE);
        return imagePart;
    }

    /**
     * メールのヘッダと本文を設定します。
     *
     * @param message
     *            メッセージ
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private void setContent(MimeMessage message) throws MessagingException, UnsupportedEncodingException {
        if (StringUtils.isEmpty(html)) {
            if (attachmentFileList.isEmpty()) {
                /*
                 * text/plain
                 */
                message.setText(StringUtils.defaultString(text), charset);
            } else {
                /*
                 * multipart/mixed
                 * ├ text/plain
                 * └ attachment
                 */
                Multipart mixedMultipart = createMixedMimeMultipart();
                mixedMultipart.addBodyPart(createTextPart());
                for (AttachmentFile file : attachmentFileList) {
                    mixedMultipart.addBodyPart(createAttachmentPart(file));
                }
                message.setContent(mixedMultipart);
            }
        } else {
            if (attachmentFileList.isEmpty()) {
                if (inlineImageFileList.isEmpty()) {
                    /*
                     * multipart/alternative
                     * ├ text/plain
                     * └ text/html
                     */
                    Multipart alternativeMultipart = createAlternativeMimeMultipart();
                    alternativeMultipart.addBodyPart(createTextPart());
                    alternativeMultipart.addBodyPart(createHtmlPart());
                    message.setContent(alternativeMultipart);
                } else {
                    /*
                     * multipart/alternative
                     * ├ text/plain
                     * └ mulpart/related
                     *   ├ text/html
                     *   └ image
                     */
                    Multipart relatedMultipart = createRelatedMimeMultipart();
                    relatedMultipart.addBodyPart(createHtmlPart());
                    for (InlineImageFile file : inlineImageFileList) {
                        relatedMultipart.addBodyPart(createImagePart(file));
                    }
                    MimeBodyPart relatedPart = new MimeBodyPart();
                    relatedPart.setContent(relatedMultipart);

                    Multipart alternativeMultipart = createAlternativeMimeMultipart();
                    alternativeMultipart.addBodyPart(createTextPart());
                    alternativeMultipart.addBodyPart(relatedPart);
                    message.setContent(alternativeMultipart);
                }
            } else {
                if (inlineImageFileList.isEmpty()) {
                    /*
                     * multipart/mixed
                     * ├ mulpart/alternative
                     * │ ├ text/plain
                     * │ └ text/html
                     * └ attachment
                     */
                    Multipart alternativeMultipart = createAlternativeMimeMultipart();
                    alternativeMultipart.addBodyPart(createTextPart());
                    alternativeMultipart.addBodyPart(createHtmlPart());
                    MimeBodyPart alternativePart = new MimeBodyPart();
                    alternativePart.setContent(alternativeMultipart);

                    Multipart mixedMultipart = createMixedMimeMultipart();
                    mixedMultipart.addBodyPart(alternativePart);
                    for (AttachmentFile file : attachmentFileList) {
                        mixedMultipart.addBodyPart(createAttachmentPart(file));
                    }
                    message.setContent(mixedMultipart);
                } else {
                    /*
                     * multipart/mixed
                     * ├ mulpart/alternative
                     * │ ├ text/plain
                     * │ └ mulpart/related
                     * │   ├ text/html
                     * │   └ image
                     * └ attachment
                     */
                    Multipart relatedMultipart = createRelatedMimeMultipart();
                    relatedMultipart.addBodyPart(createHtmlPart());
                    for (InlineImageFile file : inlineImageFileList) {
                        relatedMultipart.addBodyPart(createImagePart(file));
                    }
                    MimeBodyPart relatedPart = new MimeBodyPart();
                    relatedPart.setContent(relatedMultipart);

                    Multipart alternativeMultipart = createAlternativeMimeMultipart();
                    alternativeMultipart.addBodyPart(createTextPart());
                    alternativeMultipart.addBodyPart(relatedPart);
                    MimeBodyPart alternativePart = new MimeBodyPart();
                    alternativePart.setContent(alternativeMultipart);

                    Multipart mixedMultipart = createMixedMimeMultipart();
                    mixedMultipart.addBodyPart(alternativePart);
                    for (AttachmentFile file : attachmentFileList) {
                        mixedMultipart.addBodyPart(createAttachmentPart(file));
                    }
                    message.setContent(mixedMultipart);
                }
            }
        }
        setHeaderToPart(message);
    }

    /**
     * メッセージのパートにヘッダの内容を設定します。
     *
     * @param part
     *            メッセージのパート
     * @throws MessagingException
     */
    private void setHeaderToPart(Part part) throws MessagingException {
        for (Iterator<Map.Entry<Object, Object>> itr = headers.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<Object, Object> head = itr.next();
            part.setHeader(head.getKey().toString(), head.getValue().toString());
        }
    }

}
