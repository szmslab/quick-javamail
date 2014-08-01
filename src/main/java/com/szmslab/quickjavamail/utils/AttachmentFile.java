/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.utils;

import javax.activation.DataSource;

/**
 * 添付ファイル情報を格納するクラスです。
 *
 * @author szmslab
 */
public class AttachmentFile {

    /**
     * ファイル名。
     */
    protected String fileName;

    /**
     * ファイルのデータソース。
     */
    protected DataSource dataSource;

    /**
     * コンストラクタです。
     *
     * @param fileName
     *            ファイル名
     * @param dataSource
     *            ファイルのデータソース
     */
    public AttachmentFile(String fileName, DataSource dataSource) {
        this.fileName = fileName;
        this.dataSource = dataSource;
    }

    /**
     * ファイル名を取得します。
     *
     * @return ファイル名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * ファイルのデータソースを取得します。
     *
     * @return データソース
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /* (非 Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return fileName;
    }

}
