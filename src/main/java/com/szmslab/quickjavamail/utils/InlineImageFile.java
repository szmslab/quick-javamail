/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.utils;

import javax.activation.DataSource;

/**
 * インライン画像ファイル情報を格納するクラスです。
 *
 * @author szmslab
 */
public class InlineImageFile extends AttachmentFile {

    /**
     * Content-ID。
     */
    protected String contentId;

    /**
     * コンストラクタです。
     *
     * @param contentId
     *            Content-ID
     * @param fileName
     *            ファイル名
     * @param dataSource
     *            データソース
     */
    public InlineImageFile(String contentId, String fileName, DataSource dataSource) {
        super(fileName, dataSource);
        this.contentId = contentId;
    }

    /**
     * Content-IDを取得します。
     *
     * @return Content-ID
     */
    public String getContentId() {
        return contentId;
    }

    /* (非 Javadoc)
     * @see com.szmslab.quickjavamail.utils.AttachmentFile#toString()
     */
    @Override
    public String toString() {
        return fileName + contentId;
    }

}
