package com.makotu.rss.reader.util;

import android.util.Log;

/**
 * @author Makoto
 * ログユーティリティークラス
 *
 */
public class LogUtil {

    /** ログ出力を行うかどうか */
    public static boolean DEBUG_MODE = true;

    /**
     * 空のコンストラクタ
     */
    private LogUtil() {}

    /**
     * verboseタグのログ出力
     * @param clazz クラス
     * @param message   ログメッセージ
     */
    public static void verb(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.v(clazz.getSimpleName(), message);
        }
    }

    /**
     * infoタグの出力
     * @param clazz クラス
     * @param message   ログメッセージ
     */
    public static void info(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.i(clazz.getSimpleName(), message);
        }
    }

    /**
     * debugタグの出力
     * @param clazz クラス
     * @param message   ログメッセージ
     */
    public static void debug(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.d(clazz.getSimpleName(), message);
        }
    }

    /**
     * warnタグの出力
     * @param clazz クラス
     * @param message   ログメッセージ
     */
    public static void warn(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.w(clazz.getSimpleName(), message);
        }
    }

    /**
     * errorタグの出力
     * @param clazz クラス
     * @param message   ログメッセージ
     */
    public static void error(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.e(clazz.getSimpleName(), message);
        }
    }

    /**
     * verboseタグのログを出力
     * @param object オブジェクト
     * @param message ログメッセージ
     */
    public static void verb(Object object, String message) {
        if (DEBUG_MODE) {
            Log.v(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * infoタグのログを出力
     * @param object オブジェクト
     * @param message ログメッセージ
     */
    public static void info(Object object, String message) {
        if (DEBUG_MODE) {
            Log.i(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * debugタグのログを出力
     * @param object オブジェクト
     * @param message ログメッセージ
     */
    public static void debug(Object object, String message) {
        if(DEBUG_MODE) {
            Log.d(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * warningグのログを出力
     * @param object オブジェクト
     * @param message ログメッセージ
     */
    public static void warn(Object object, String message) {
        if (DEBUG_MODE) {
            Log.w(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * errorタグのログを出力
     * @param object オブジェクト
     * @param message ログメッセージ
     */
    public static void error(Object object, String message) {
        if (DEBUG_MODE) {
            Log.e(object.getClass().getSimpleName(), message);
        }
    }
}
