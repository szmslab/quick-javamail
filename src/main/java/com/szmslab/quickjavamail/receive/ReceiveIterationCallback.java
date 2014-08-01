/*
 * Copyright (c) 2014 szmslab
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package com.szmslab.quickjavamail.receive;

/**
 * メール受信結果を1件ずつ反復するための抽象クラスです。
 *
 * @author szmslab
 */
public interface ReceiveIterationCallback {

    /**
     * メール受信結果1件ごとに通知されます。
     *
     * @param loader
     *            受信メール情報
     * @return 次のメッセージを処理するかどうか（true: continue、false: break）
     * @throws Exception
     */
    boolean iterate(MessageLoader loader) throws Exception;

}
