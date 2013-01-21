package com.makotu.rss.reader.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * �m�[�e�B�t�B�P�[�V�������[�e�B���e�B�N���X
 * @author Makoto
 *
 */
public class NotificationUtil {

    /**
     * ��̃R���X�g���N�^
     */
    private NotificationUtil() {}

    /**
     * �m�[�e�B�t�B�P�[�V�����\��
     * @param context  �A�N�e�B�r�e�B
     * @param iconId    �A�C�R��ID
     * @param ticker    �e�B�b�J�[�e�L�X�g
     * @param title     �^�C�g��
     * @param message   ���b�Z�[��
     * @param clazz     �J�ڐ�̃N���X
     */
    public static void showNotification(Context context, int iconId, String ticker, String title, String message, Class<?> clazz) {
        if (!checkArgument(context, clazz)) {
            LogUtil.error(NotificationUtil.class, "�������s���ł�");
            return;
        }
        //�m�[�e�B�t�B�P�[�V�����}�l�[�W���̎擾
        NotificationManager nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //�m�[�e�B�t�B�P�[�V�����I�u�W�F�N�g�̐���
        Notification notification = new Notification(iconId, ticker, System.currentTimeMillis());

        //�^�X�N�o�[�ŃN���b�N���ꂽ���̕\��
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //PendingIntent�̐���
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        //���s�����PendingIntent�̐���
        notification.setLatestEventInfo(context, title, message, pIntent);

        //�m�[�e�B�t�B�P�[�V�����̕\��
        nManager.notify(iconId, notification);
    }

    public static void cancelNotification(Context context, int iconId) {
        //�m�[�e�B�t�B�P�[�V�����}�l�[�W���̎擾
        NotificationManager nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(iconId);
    }

    private static boolean checkArgument(Context context, Class<?> clazz) {
        return context != null && clazz != null;
    }
}
