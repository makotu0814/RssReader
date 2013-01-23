package com.makotu.rss.reader.activity;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.makotu.rss.reader.parser.RssParser;
import com.makotu.rss.reader.util.LayoutUtil;
import com.makotu.rss.reader.util.ToastUtil;

public class RegistRssFeedActivity extends RssBaseActivity implements OnClickListener {

    EditText rssFeed;   //RSSフィード登録テキスト
    Button  rssAddBtn, clearBtn;    //追加ボタン、クリアボタン
    RadioButton oneHour, sixHour, twentyHour, oneDay;    //更新間隔ラジオボタン
    RadioGroup rg;  //ラジオボタングループ

    /**
     * RSS登録画面の初期表示
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //画面レイアウトの作成、縦方向
        LinearLayout dispLayout = new LinearLayout(this);
        dispLayout.setOrientation(LinearLayout.VERTICAL);

        //RSSフィードのレイアウト 横方向に設定
        LinearLayout feedLayout = new LinearLayout(this);
        feedLayout.setOrientation(LinearLayout.HORIZONTAL);

        //ボタンレイアウト 横方向に設定
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        setContentView(dispLayout);

        //URLの入力項目生成
        TextView Urltv = (new TextView(this));
        Urltv.setText("URL:");

        rssFeed = new EditText(this);
        //------デバッグ用------
        rssFeed.setText("http://www.kotaro269.com/index.rdf");
        //-------------------

        //RSSフィードレイアウトに設定
        feedLayout.addView(Urltv, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        feedLayout.addView(rssFeed, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));

        //更新間隔のラジオボタン生成
        TextView radioTv = new TextView(this);
        radioTv.setText("更新間隔");

        //更新間隔1時間毎
        oneHour = new RadioButton(this);
        oneHour.setId(1);
        oneHour.setText("1時間毎");

        //更新間隔6時間毎
        sixHour = new RadioButton(this);
        sixHour.setId(6);
        sixHour.setText("6時間毎");

        //更新間隔12時間毎
        twentyHour = new RadioButton(this);
        twentyHour.setId(12);
        twentyHour.setText("12時間毎");

        //更新間隔24時間毎
        oneDay = new RadioButton(this);
        oneDay.setId(24);
        oneDay.setText("24時間毎");

        //ラジオグループの作成、ラジオボタン追加
        rg = new RadioGroup(this);
        rg.addView(oneHour);
        rg.addView(sixHour);
        rg.addView(twentyHour);
        rg.addView(oneDay);
        rg.check(1);
        rg.setLayoutParams(LayoutUtil.getLayoutParamsWrap());

        //追加ボタンの作成
        rssAddBtn = new Button(this);
        rssAddBtn.setText("フィードの追加");
        rssAddBtn.setOnClickListener(this);

        //クリアボタンの作成
        clearBtn = new Button(this);
        clearBtn.setText("クリア");
        clearBtn.setOnClickListener(this);

        //ボタンをボタンレイアウトに追加
        btnLayout.addView(rssAddBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        btnLayout.addView(clearBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));

        //レイアウトとラジオボタンを画面レイアウトに追加
        dispLayout.addView(feedLayout, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));
        dispLayout.addView(radioTv);
        dispLayout.addView(rg);
        dispLayout.addView(btnLayout, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));
    }

    /**
     * 追加、クリアボタンクリック時のイベントリスナー
     * @param   view    クリックされたボタンオブジェクト
     */
    public void onClick(View view) {
        // RSSフィード追加ボタンクリック時
        if (view == rssAddBtn) {
            String rssUrl = rssFeed.getText().toString();
            int updHour = rg.getCheckedRadioButtonId();

            //別スレッドでネットワークからRSSを取得し、データベースに挿入
            getRssFromNetwork(rssUrl, updHour);
        } else if (view == clearBtn) {
            //RSSフィードの値をクリア
            rssFeed.setText("");
        }
    }

    private void getRssFromNetwork(final String rssUrl, final int updHour) {
       new Thread(new Runnable() { 
            public void run() {
                Message resultMsg = new Message();
                resultMsg.arg1 = RssParser.parseRssFeed(rssUrl, updHour);
                resultMsg.obj = rssUrl;
                handler.sendMessage(resultMsg);
            }
       }).start();
    }

    Handler handler = new RssParseHandler(this);

    private static class RssParseHandler extends Handler {
        private final WeakReference<RegistRssFeedActivity> mActivity;
     
        public RssParseHandler(RegistRssFeedActivity activity) {
            mActivity = new WeakReference<RegistRssFeedActivity>(activity);
        }
     
        @Override
        public void handleMessage(Message msg) {
            final RegistRssFeedActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            final int id = msg.arg1;
            final String rssUrl = (String)msg.obj;

            if (id > 0) {
                //RSSの記事を読み込みデータベースへ登録
                new Thread(new Runnable() {
                    
                    public void run() {
                    //別スレッドでRSSコンテンツを取得
                        RssParser.parseRssContents(rssUrl, String.valueOf(id));
                            new Handler().post(new Runnable() {
                                
                                public void run() {
                                    ToastUtil.showToastLong(activity, "RSSフィードの登録に成功しました。コンテンツを取得しています。");
                                }
                            });
                    }
                });
                ToastUtil.showToastShort(activity, "RSSコンテンツを取得しました。");

                //画面の戻り値を設定
                activity.setResult(RESULT_OK);
                //画面の終了
                activity.back();
            } else {
                ToastUtil.showToastShort(activity, "RSSフィードの登録に失敗しました。すでに登録されているか、RSS2.0に対応しているか確認してください");
            }
        }
    }

    /**
     * キーイベント処理
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        setResult(RESULT_OK);
        return super.dispatchKeyEvent(event);
    }

}
