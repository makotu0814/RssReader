package com.makotu.rss.reader.util;

import android.content.Context;
import android.widget.Toast;

/**
 * �g�[�X�g���[�e�B���e�B�[�N���X
 * @author Makoto
 *
 */
public class ToastUtil {

    /**
     * ��̃R���X�g���N�^
     */
    private ToastUtil() {}

    /**
     * �g�[�X�g��\������(�\�����Ԃ��Z��)
     * @param context   �R���e�L�X�g
     * @param message   ���b�Z�[�W
     */
    public static void showToastShort(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    
    /**
     * �g�[�X�g��\������(�\�����Ԃ�����)
     * @param context   �R���e�L�X�g
     * @param message   ���b�Z�[�W
     */
    public static void showToastLong(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
