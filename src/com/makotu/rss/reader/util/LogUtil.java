package com.makotu.rss.reader.util;

import android.util.Log;

/**
 * @author Makoto
 * ���O���[�e�B���e�B�[�N���X
 *
 */
public class LogUtil {

    /** ���O�o�͂��s�����ǂ��� */
    public static boolean DEBUG_MODE = true;

    /**
     * ��̃R���X�g���N�^
     */
    private LogUtil() {}

    /**
     * verbose�^�O�̃��O�o��
     * @param clazz �N���X
     * @param message   ���O���b�Z�[�W
     */
    public static void verb(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.v(clazz.getSimpleName(), message);
        }
    }

    /**
     * info�^�O�̏o��
     * @param clazz �N���X
     * @param message   ���O���b�Z�[�W
     */
    public static void info(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.i(clazz.getSimpleName(), message);
        }
    }

    /**
     * debug�^�O�̏o��
     * @param clazz �N���X
     * @param message   ���O���b�Z�[�W
     */
    public static void debug(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.d(clazz.getSimpleName(), message);
        }
    }

    /**
     * warn�^�O�̏o��
     * @param clazz �N���X
     * @param message   ���O���b�Z�[�W
     */
    public static void warn(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.w(clazz.getSimpleName(), message);
        }
    }

    /**
     * error�^�O�̏o��
     * @param clazz �N���X
     * @param message   ���O���b�Z�[�W
     */
    public static void error(Class<?> clazz, String message) {
        if (DEBUG_MODE) {
            Log.e(clazz.getSimpleName(), message);
        }
    }

    /**
     * verbose�^�O�̃��O���o��
     * @param object �I�u�W�F�N�g
     * @param message ���O���b�Z�[�W
     */
    public static void verb(Object object, String message) {
        if (DEBUG_MODE) {
            Log.v(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * info�^�O�̃��O���o��
     * @param object �I�u�W�F�N�g
     * @param message ���O���b�Z�[�W
     */
    public static void info(Object object, String message) {
        if (DEBUG_MODE) {
            Log.i(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * debug�^�O�̃��O���o��
     * @param object �I�u�W�F�N�g
     * @param message ���O���b�Z�[�W
     */
    public static void debug(Object object, String message) {
        if(DEBUG_MODE) {
            Log.d(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * warning�O�̃��O���o��
     * @param object �I�u�W�F�N�g
     * @param message ���O���b�Z�[�W
     */
    public static void warn(Object object, String message) {
        if (DEBUG_MODE) {
            Log.w(object.getClass().getSimpleName(), message);
        }
    }

    /**
     * error�^�O�̃��O���o��
     * @param object �I�u�W�F�N�g
     * @param message ���O���b�Z�[�W
     */
    public static void error(Object object, String message) {
        if (DEBUG_MODE) {
            Log.e(object.getClass().getSimpleName(), message);
        }
    }
}
