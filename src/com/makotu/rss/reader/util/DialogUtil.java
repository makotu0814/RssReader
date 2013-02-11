package com.makotu.rss.reader.util;

import android.R;
import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;

public class DialogUtil {

    /**
     *空のコンストラクタ
     */
    private DialogUtil(){}

    /**
     * YES/NOダイアログ表示
     * @param context   コンテキスト
     * @param resourdeId   メッセージ
     * @param yesListener   リスナー
     */
    public static void showYesNoDialog(Context context, int resourdeId, OnClickListener yesListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(resourdeId));
        builder.setPositiveButton(R.string.yes, yesListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    
    /**
     * YES/NOカスタムダイアログ表示
     * @param context   コンテキスト
     * @param resourdeId   メッセージ
     * @param view   ダイアログのレイアウトビュー
     * @param yesListener   リスナー
     */
    public static void showYesNoCustomDialog(Context context, int resourdeId, View view, OnClickListener yesListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(resourdeId));
        builder.setView(view);
        builder.setPositiveButton(R.string.yes, yesListener);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * Errorダイアログ表示
     * @param context コンテキスト
     * @param title タイトル
     * @param message   メッセージ
     */
    public static void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.create().show();
    }

    /**
     * Infoダイアログ表示
     * @param context   コンテキスト
     * @param title タイトル
     * @param message   メッセージ
     */
    public static void showInfoDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.create().show();
    }

}
