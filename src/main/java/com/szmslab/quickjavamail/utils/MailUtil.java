/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import com.szmslab.quickjavamail.receive.MessageLoader;

/**
 * メール関連のユーティリティクラスです。
 *
 * @author szmslab
 */
public class MailUtil {

    /**
     * Windows固有の機種依存文字を使用するように文字セットマッピングを上書きします。
     */
    public static void overrideCsMapForWindows() {
        // [ISO-2022-JP → x-windows-iso2022jp], [Shift_JIS → Windows-31J]
        System.setProperty("sun.nio.cs.map", "x-windows-iso2022jp/ISO-2022-JP,Windows-31J/Shift_JIS");
    }

    /**
     * 指定したファイルの親ディレクトリを作成します。
     *
     * @param file
     *            対象のファイル
     */
    public static void createParentDirs(File file) {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    /**
     * データソースをファイルにエクスポートします。
     *
     * @param ds
     *            データソース
     * @param exportFile
     *            エクスポートファイル
     * @throws IOException
     */
    public static void exportFile(DataSource ds, File exportFile) throws IOException {
        createParentDirs(exportFile);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(ds.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(exportFile));
            byte [] buffer = new byte[8096];
            int len = 0;
            while ((len = bis.read(buffer)) >= 0) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * データソースをファイルにエクスポートします。
     *
     * @param ds
     *            データソース
     * @param exportFilePath
     *            エクスポートファイルパス
     * @throws IOException
     */
    public static void exportFile(DataSource ds, String exportFilePath) throws IOException {
        exportFile(ds, new File(exportFilePath));
    }

    /**
     * メッセージをファイルにエクスポートします。
     *
     * @param message
     *            メッセージ
     * @param exportFile
     *            エクスポートファイル
     * @throws IOException
     * @throws MessagingException
     */
    public static void exportFile(Part message, File exportFile) throws IOException, MessagingException {
        createParentDirs(exportFile);

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(exportFile));
            message.writeTo(bos);
            bos.flush();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * メッセージをファイルにエクスポートします。
     *
     * @param message
     *            メッセージ
     * @param exportFilePath
     *            エクスポートファイルパス
     * @throws IOException
     * @throws MessagingException
     */
    public static void exportFile(Part message, String exportFilePath) throws IOException, MessagingException {
        exportFile(message, new File(exportFilePath));
    }

    /**
     * ファイルをインポートしてメッセージを生成します。
     *
     * @param importFile
     *            インポートファイル
     * @return メッセージ
     * @throws FileNotFoundException
     * @throws MessagingException
     */
    public static Message importFile(File importFile) throws FileNotFoundException, MessagingException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(importFile);
            return new MimeMessage(null, fis);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ファイルをインポートしてメッセージを生成します。
     *
     * @param importFilePath
     *            インポートファイルパス
     * @return メッセージ
     * @throws FileNotFoundException
     * @throws MessagingException
     */
    public static Message importFile(String importFilePath) throws FileNotFoundException, MessagingException {
        return importFile(new File(importFilePath));
    }

    /**
     * 分割ファイルをインポートして、連結したメッセージを生成します。（引数のリストに格納された順番に連結します）
     *
     * @param partialFiles
     *            分割ファイル
     * @return 連結したメッセージ
     * @throws MessagingException
     * @throws IOException
     */
    public static Message importPartialFiles(File... partialFiles) throws MessagingException, IOException {
        SequenceInputStream sis = null;
        try {
            List<InputStream> streamList = new ArrayList<InputStream>();
            for (File file : partialFiles) {
                MessageLoader loader = new MessageLoader(new MimeMessage(null, new FileInputStream(file)));
                if (loader.isPartial()) {
                    streamList.add(loader.getPartialContent());
                } else {
                    streamList.add(loader.getOriginalMessage().getInputStream());
                }
            }
            sis = new SequenceInputStream(Collections.enumeration(streamList));
            return new MimeMessage(null, sis);
        } finally {
            if (sis != null) {
                try {
                    sis.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * 分割ファイルをインポートして、連結したメッセージを生成します。（引数のリストに格納された順番に連結します）
     *
     * @param partialFilePaths
     *            分割ファイルパス
     * @return 連結したメッセージ
     * @throws MessagingException
     * @throws IOException
     */
    public static Message importPartialFiles(String... partialFilePaths) throws MessagingException, IOException {
        File[] partialFiles = new File[partialFilePaths.length];
        for (int i = 0; i < partialFilePaths.length; i++) {
            partialFiles[i] = new File(partialFilePaths[i]);
        }
        return importPartialFiles(partialFiles);
    }

    /**
     * データソースをバイト配列に変換します。
     *
     * @param ds
     *            データソース
     * @return バイト配列
     * @throws IOException
     */
    public static byte[] toByteArray(DataSource ds) throws IOException {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bis = new BufferedInputStream(ds.getInputStream());
            byte [] buffer = new byte[8096];
            int len = 0;
            while ((len = bis.read(buffer)) >= 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
