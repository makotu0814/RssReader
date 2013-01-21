package com.makotu.rss.reader.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.makotu.rss.reader.provider.RssFeeds;
import com.makotu.rss.reader.util.LayoutUtil;
import com.makotu.rssreader.R;

public class RssListActivity extends RssBaseActivity implements OnItemClickListener, OnClickListener {

    /** RSSフィードListView */
    private ListView rssFeedList;

    /** RSSリーダーの追加ボタン*/
    private Button addBtn;

    /** RSSリーダーの終了ボタン*/
    private Button endBtn;

    /** オプションメニュー RSSの追加*/
    private static final int MENU_ID_ADD = (Menu.FIRST + 1);

    /** オプションメニューRssのリーダーの終了*/
    private static final int MENU_ID_END = (Menu.FIRST + 2);

    /** 選択中のリストID*/
    private long selectListId;

    /** リストIDと、RSSのIDのマッピング*/
    private HashMap<Long, String> idMap = new HashMap<Long, String>();

    /** リストIDと、RSSのURLのマッピング*/
    private HashMap<Long, String> urlMap = new HashMap<Long, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //画面開始時にコンテンツプロバイダを取得、RssFeedsクラスに設定
        RssFeeds.setContentResolver(getContentResolver());

        //画面レイアウト
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linearLayout);

        //ボタンレイアウト
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        //RSS一覧リスト
        rssFeedList = new ListView(this);
        rssFeedList.setOnItemClickListener(this);
        getArrayAdapter();

        //RSS追加ボタン
        addBtn = new Button(this);
        addBtn.setText("RSSフィードの追加");
        addBtn.setOnClickListener(this);

        //RSS削除ボタン
        endBtn = new Button(this);
        endBtn.setText("RSSリーダーの終了");
        endBtn.setOnClickListener(this);
        btnLayout.addView(addBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        btnLayout.addView(endBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));

        linearLayout.addView(btnLayout);
        linearLayout.addView(rssFeedList, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            getArrayAdapter();
        }
    }

    /**
     * ListViewの表示内容をデータベースから取得した値でこうｓン
     */
    private void getArrayAdapter() {
        //RSSフィードテーブルより値をすべてカーソルで取得
        Cursor mRssCursor = RssFeeds.query(RssFeeds.RssFeedColumns.CONTENT_URI, RssFeeds.RSSFEED_PROJECTION_MAP.keySet().toArray(new String[0]), null, null, null);
        ArrayList<String> rssList = new ArrayList<String>();
        int menuId = 0;
        //登録されているRSSフィード数ループ
        while (mRssCursor.moveToNext()) {
            //テーブルのカラムを取得し、値を取得
            int rssIdCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns._ID);
            String rssId = mRssCursor.getString(rssIdCol);

            //記事のタイトルのカラム位置を取得し値を取得
            int rssChannelNameCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_NAME);
            String channelTitle = mRssCursor.getString(rssChannelNameCol);

            //記事へのリンク(Rssの配信元)のカラム位置を取得し値を取得
            int rssChannelLinkCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_LINK);
            String channelLink = mRssCursor.getString(rssChannelLinkCol);

            //RSSフィードのURLのカラム位置を取得し値を取得
            int rssFeedUrlCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK);
            String feedUrl = mRssCursor.getString(rssFeedUrlCol);

            //ListViewのリストIdとrssのidをマップオブジェクトに格納
            idMap.put(new Long(menuId), rssId);
            urlMap.put(new Long(menuId), feedUrl);
            menuId++;

            //Rssフィード一覧にはタイトルとRSSフィードのURLを表示
            rssList.add(channelTitle + System.getProperty("line.separator") + "\t" + channelLink);
        }
        String[] data = (String[])rssList.toArray(new String[0]);
        //RSSフィード一覧画面をデータベースのRSSフィードの値で更新
        rssFeedList.setAdapter(new ArrayAdapter<String>(this, R.layout.rss_list_layout, data));
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == rssFeedList) {
            selectListId = id;
            startReadRssActivity();
        }
    }

    /**
     * ボタンクリック時のイベントリスナー
     */
    public void onClick(View view) {
        //RSSフィード追加ボタン
        if (view == addBtn) {
            //RSSフィード追加画面へ遷移
            startRegistRssActivity();
        } else if (view == endBtn) {
            //アプリ終了する
            back();
        }
    }

    /**
     * オプションメニューの追加
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_ADD, Menu.NONE, "RSSフィードの追加");
        menu.add(Menu.NONE, MENU_ID_END, Menu.NONE, "RSSフィードの削除");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * メニューボタンクリック時のイベントリスナー
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ID_ADD:
            startRegistRssActivity();
            break;
        case MENU_ID_END:
            back();
            break;
        default:
            break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * RSS登録画面へ遷移
     */
    private void startRegistRssActivity() {
        //Intentへのインスタンス生成
        Intent intent = new Intent(this, RegistRssFeedActivity.class);
        String rssid = (String)idMap.get(new Long(selectListId));
        intent.putExtra("id", rssid);
        //サブ画面の起動
        startActivityForResult(intent, 0);
    }

    /**
     * RSS表示画面へ遷移
     */
    private void startReadRssActivity() {
        //Intentへのインスタンス生成
        Intent intent = new Intent(this, RssArticleListActivity.class);
        //RSS表示画面にメイン画面URLとIDを送信
        String rssid = (String)idMap.get(Long.valueOf(selectListId));
        String url = (String)urlMap.get(Long.valueOf(selectListId));
        intent.putExtra("url", url);
        intent.putExtra("id", rssid);
        //RSS表示画面の起動
        startActivityForResult(intent, 0);
    }
}
