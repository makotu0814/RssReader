package com.makotu.rss.reader.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * ��̃R���X�g���N�^
 * @author Makoto
 *
 */
public class LayoutUtil {

    //��ʂɔz�u����I�u�W�F�N�g�̃��C�A�E�g�p�����[�^
    public final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * �C���X�^���X���֎~
     */
    private LayoutUtil() {}

    /**
     * �C���t���[�g���ꂽView��Ԃ�
     * @param context
     * @param resourceId
     * @return View
     */
    public static View getInflatedLayout(Context context, int resourceId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(resourceId, null);
        return view;
    }

    /**
     * ���C�A�E�g�p�����[�^�̎擾
     * @param w ���C�A�E�g�p�����[�^�̉������擾
     * @param h ���C�A�E�g�p�����[�^�̏c�����擾
     * @return  ���C�A�E�g�p�����[�^
     */
    public static ViewGroup.LayoutParams getLayoutParams(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    /**
     * @return  �����A�c����MATCH_PARENT�̃��C�A�E�g�p�����[�^��Ԃ�
     */
    public static ViewGroup.LayoutParams getLayoutParamsMatch() {
        return getLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * @return  �����A�c����WRAP_CONTENT�̃��C�A�E�g�p�����[�^��Ԃ�
     */
    public static ViewGroup.LayoutParams getLayoutParamsWrap() {
        return getLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
