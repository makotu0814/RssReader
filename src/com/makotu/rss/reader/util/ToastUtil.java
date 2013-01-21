package com.makotu.rss.reader.util;

import android.content.Context;
import android.widget.Toast;

/**
 * トーストユーティリティークラス
 * @author Makoto
 *
 */
public class ToastUtil {

    /**
     * 空のコンストラクタ
     */
    private ToastUtil() {}

    /**
     * トーストを表示する(表示時間が短い)
     * @param context   コンテキスト
     * @param message   メッセージ
     */
    public static void showToastShort(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    
    /**
     * トーストを表示する(表示時間が長い)
     * @param context   コンテキスト
     * @param message   メッセージ
     */
    public static void showToastLong(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
