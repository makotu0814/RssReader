package com.makotu.rss.reader.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * ノーティフィケーションユーティリティクラス
 * @author Makoto
 *
 */
public class NotificationUtil {

    /**
     * 空のコンストラクタ
     */
    private NotificationUtil() {}

    /**
     * ノーティフィケーション表示
     * @param context  アクティビティ
     * @param iconId    アイコンID
     * @param ticker    ティッカーテキスト
     * @param title     タイトル
     * @param message   メッセーｊ
     * @param clazz     遷移先のクラス
     */
    public static void showNotification(Context context, int iconId, String ticker, String title, String message, Class<?> clazz) {
        if (!checkArgument(context, clazz)) {
            LogUtil.error(NotificationUtil.class, "引数が不正です");
            return;
        }
        //ノーティフィケーションマネージャの取得
        NotificationManager nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //ノーティフィケーションオブジェクトの生成
        Notification notification = new Notification(iconId, ticker, System.currentTimeMillis());

        //タスクバーでクリックされた時の表示
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //PendingIntentの生成
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        //実行されるPendingIntentの生成
        notification.setLatestEventInfo(context, title, message, pIntent);

        //ノーティフィケーションの表示
        nManager.notify(iconId, notification);
    }

    public static void cancelNotification(Context context, int iconId) {
        //ノーティフィケーションマネージャの取得
        NotificationManager nManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(iconId);
    }

    private static boolean checkArgument(Context context, Class<?> clazz) {
        return context != null && clazz != null;
    }
}
