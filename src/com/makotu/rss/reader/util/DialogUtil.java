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
     *��̃R���X�g���N�^
     */
    private DialogUtil(){}

    /**
     * YES/NO�_�C�A���O�\��
     * @param context   �R���e�L�X�g
     * @param resourdeId   ���b�Z�[�W
     * @param yesListener   ���X�i�[
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
     * YES/NO�J�X�^���_�C�A���O�\��
     * @param context   �R���e�L�X�g
     * @param resourdeId   ���b�Z�[�W
     * @param view   �_�C�A���O�̃��C�A�E�g�r���[
     * @param yesListener   ���X�i�[
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
     * Error�_�C�A���O�\��
     * @param context �R���e�L�X�g
     * @param title �^�C�g��
     * @param message   ���b�Z�[�W
     */
    public static void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.create().show();
    }

    /**
     * Info�_�C�A���O�\��
     * @param context   �R���e�L�X�g
     * @param title �^�C�g��
     * @param message   ���b�Z�[�W
     */
    public static void showInfoDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.create().show();
    }

}
