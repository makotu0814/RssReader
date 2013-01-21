package com.makotu.rss.reader.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

import com.makotu.rss.reader.R;
import com.makotu.rss.reader.activity.RssListActivity;
import com.makotu.rss.reader.parser.RssParser;
import com.makotu.rss.reader.provider.RssFeeds;
import com.makotu.rss.reader.util.LogUtil;
import com.makotu.rss.reader.util.NotificationUtil;
import com.makotu.rss.reader.util.ToastUtil;

/**
 * RSS読み込みService
 * 規定の時間になったらRSSの読み込みを行う
 * @author Makoto
 *
 */
public class RssReaderService extends Service implements Runnable{

    /** スレッド制御*/
    private boolean alive = false; 

    /** スレッド*/
    private Thread thread;

    /** データベースのカーソル*/
    private Cursor  mRSSCursor;

    @Override
    public void onCreate() {
        LogUtil.debug(RssReaderService.class, "onCreate()");

        //ノーティフィケーションマネージャの取得
        //nNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationUtil.showNotification(this, R.drawable.ic_launcher, "RSSリーダーを実行します", 
        "RSSリーダーサービス", "RSSリーダーサービス開始しました", RssListActivity.class);

        //トーストの表示
        ToastUtil.showToastShort(getApplicationContext(), "RssReaderServiceを開始します");

        //Serviceの起動と同時にスレッドを実行可能状態にする
        this.alive = true;

        //スレッド作成
        thread = new Thread(null, this, "RssReaderService");

        //スレッドの開始
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Service破棄時
     */
    @Override
    public void onDestroy() {
        LogUtil.debug(RssReaderService.class, "onDestroy()");
        ToastUtil.showToastShort(this, "RssReaderServiceを停止しました");

        //スレッドの有効フラグをfalseにする
        this.alive = false;
        //スレッドの停止
        thread.stop();
        //ノーティフィケーションマネージャーを破棄
        NotificationUtil.cancelNotification(this, R.drawable.ic_launcher);
    }

    /**
     * RSSフィードの取得処理
     */
    public void run() {
        //データベースからRssFeedsの値を取得
        mRSSCursor = RssFeeds.query(RssFeeds.RssFeedColumns.CONTENT_URI, RssFeeds.RSSFEED_PROJECTION_MAP.keySet().toArray(new String[0]), null, null, null);
        //Threadフラグが有効
        while (this.alive) {
            try {
                //10分間スレッド休止
                Thread.sleep(1 * 10 * 60 * 1000);

                //RssFeedsテーブルの値を再取得
                mRSSCursor.requery();

                //RssFeedsテーブルのチャンネルフィードリンクのカラム位置を取得
                int rssCFeedLinkCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK);
                //RssFeedsテーブルのIDのカラム位置を取得
                int rssIDCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns._ID);
                //RssFeedsテーブルの更新間隔のカラム位置を取得
                int rssUpdCyCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns.UPDATE_CYCLE);
                //RssFeedsテーブルの最終更新日のカラム位置を取得
                int rssLastUpdCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns.LAST_UPDATE);

                long now = System.currentTimeMillis();

                //RssFeedsのレコード数ループ記事を取得
                while (mRSSCursor.moveToNext()) {
                    //データベースより最終更新日付を取得
                    long lastUpdate = mRSSCursor.getLong(rssLastUpdCol);
                    //データベースより更新間隔を取得
                    long updateCycle = mRSSCursor.getLong(rssUpdCyCol);

                    //最終更新日付と更新間隔を足して現在よりも古いならばRSSフィードのURLから値を取得
                    if (lastUpdate + updateCycle <  now) {
                        //RssFeedsテーブルのRSSフィードのURLを取得
                        String channel_feed_link = (String)mRSSCursor.getString(rssCFeedLinkCol);

                        //RssFeedsテーブルのidを取得
                        String id = (String)mRSSCursor.getString(rssIDCol);
                        //RSSフィードのURLからデータを取得しデータベースへ格納
                        RssParser.parseRssContents(channel_feed_link, id);
                    }
                }
                //カーソルを非活性状態
                mRSSCursor.deactivate();
            } catch (Exception e) {
                LogUtil.error(getClass(), e.getMessage());
            }
        }
        this.cleanUp();
    }

    /**
     * スレッド終了時にカーソルをクローズする
     */
    private void cleanUp() {
        mRSSCursor.close();
    }
}
