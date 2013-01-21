package com.makotu.rss.reader.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 空のコンストラクタ
 * @author Makoto
 *
 */
public class LayoutUtil {

    //画面に配置するオブジェクトのレイアウトパラメータ
    public final static int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    /**
     * インスタンス化禁止
     */
    private LayoutUtil() {}

    /**
     * インフレートされたViewを返す
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
     * レイアウトパラメータの取得
     * @param w レイアウトパラメータの横幅を取得
     * @param h レイアウトパラメータの縦幅を取得
     * @return  レイアウトパラメータ
     */
    public static ViewGroup.LayoutParams getLayoutParams(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }

    /**
     * @return  横幅、縦幅がMATCH_PARENTのレイアウトパラメータを返す
     */
    public static ViewGroup.LayoutParams getLayoutParamsMatch() {
        return getLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * @return  横幅、縦幅がWRAP_CONTENTのレイアウトパラメータを返す
     */
    public static ViewGroup.LayoutParams getLayoutParamsWrap() {
        return getLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
