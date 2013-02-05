package com.makotu.rss.reader.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.makotu.rss.reader.R;
import com.makotu.rss.reader.provider.RssFeeds;
import com.makotu.rss.reader.util.LayoutUtil;

public class RssListActivity extends RssBaseActivity implements OnItemClickListener {

    /** RSSフィードListView */
    private ListView rssFeedList;

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
     * 作成したメニューのXMLファイルを展開し、アクションバーに配置する
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return true;
    }

    /** 
     * アクションバーのメニューが選択された時に呼び出される
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add:
            //RSSフィード追加画面へ遷移
            startRegistRssActivity();
            break;
        case R.id.action_close:
            //アプリ終了する
            back();
        default:
            break;
        }
        return true;
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
