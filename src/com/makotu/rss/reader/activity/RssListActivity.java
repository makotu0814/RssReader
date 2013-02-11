package com.makotu.rss.reader.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.makotu.rss.reader.R;
import com.makotu.rss.reader.parser.RssParser;
import com.makotu.rss.reader.provider.RssFeeds;
import com.makotu.rss.reader.util.DialogUtil;
import com.makotu.rss.reader.util.LayoutUtil;
import com.makotu.rss.reader.util.LogUtil;
import com.makotu.rss.reader.util.ToastUtil;

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

        //デバッグ用
        initRssList();

        //RSS一覧リスト
        rssFeedList = new ListView(this);
        rssFeedList.setOnItemClickListener(this);
        getArrayAdapter();

        linearLayout.addView(rssFeedList, LayoutUtil.getLayoutParams(LayoutUtil.MP, LayoutUtil.WC));
    }

    private void initRssList() {
        getRssFromNetwork("http://feeds.gizmodo.jp/rss/gizmodo/index.xml", 1);
        getRssFromNetwork("http://feeds.lifehacker.jp/rss/lifehacker/index.xml", 6);
        getRssFromNetwork("http://rss.rssad.jp/rss/kotaku/index.xml", 12);
        getRssFromNetwork("http://feed.rssad.jp/rss/engadget/rss", 24);
        getRssFromNetwork("http://jp.techcrunch.com/feed/", 1);
        getRssFromNetwork("http://karapaia.livedoor.biz/index.rdf", 6);
        getRssFromNetwork("http://labaq.com/index.rdf", 12);
        getRssFromNetwork("http://matome.naver.jp/feed/hot", 24);
        getRssFromNetwork("http://www.kotaro269.com/index.rdf", 1);
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
     * ListViewの表示内容をデータベースから取得した値で更新
     */
    protected void getArrayAdapter() {
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
        inflater.inflate(R.menu.actions_rss_list, menu);
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
            //startRegistRssActivity();
            LayoutInflater inflater = LayoutInflater.from(this);
            final View view = inflater.inflate(R.layout.settings, null);
            DialogUtil.showYesNoCustomDialog(this, R.string.label_rss_feed, view, new DialogInterface.OnClickListener() {
                
                public void onClick(DialogInterface dialog, int which) {
                    //OKボタンが押された時の処理
                    String feedUrl = ((EditText)view.findViewById(R.id.txtRssFeed)).getText().toString();
                    int radioId = ((RadioGroup)view.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
                    int updHour = -1;
                    switch (radioId) {
                    case R.id.one_hour_interval:
                        updHour = Integer.valueOf(R.string.radio_button1_id);
                        break;
                    case R.id.six_hour_interval:
                        updHour = Integer.valueOf(R.string.radio_button2_id);
                        break;
                    case R.id.twelve_hour_interval:
                        updHour = Integer.valueOf(R.string.radio_button3_id);
                        break;
                    case R.id.twentyfour_hour_interval:
                        updHour = Integer.valueOf(R.string.radio_button4_id);
                        break;
                    default:
                        LogUtil.error(getClass(), "invalid radio id:" + radioId);
                        break;
                    }
                    if (updHour != -1) {
                        getRssFromNetwork(feedUrl, updHour);
                    }
                }
            });
            break;
        case R.id.action_close:
            //アプリを終了する
            back();
        default:
            break;
        }
        return true;
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
         private final WeakReference<RssListActivity> mActivity;
      
         public RssParseHandler(RssListActivity rssListActivity) {
             mActivity = new WeakReference<RssListActivity>(rssListActivity);
         }
      
         @Override
         public void handleMessage(Message msg) {
             final RssListActivity activity = mActivity.get();
             if (activity == null) {
                 return;
             }
             final int id = msg.arg1;
             final String rssUrl = (String)msg.obj;

             if (id > 0) {
                 //RSSの記事を読み込みデータベースへ登録
                 new Thread(new Runnable() {
                     
                     public void run() {
                        //別スレッドでRSSコンテンツを取得しておく
                        RssParser.parseRssContents(rssUrl, String.valueOf(id));
                     }
                 }).start();

                String successMsg = (String) activity.getResources().getText(R.string.fetch_rss_feed_success);
                ToastUtil.showToastShort(activity, successMsg);

                 //登録されたRSSフィードを画面に反映させる
                 activity.getArrayAdapter();
             } else {
                 String successFail = (String) activity.getResources().getText(R.string.fetch_rss_feed_fail);
                 ToastUtil.showToastShort(activity, successFail);
             }
         }
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
