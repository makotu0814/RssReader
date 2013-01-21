package com.makotu.rss.reader.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RSSFeedDBHelper extends SQLiteOpenHelper {

    /**
     * コンストラクタ
     * @param context
     */
    public RSSFeedDBHelper(Context context) {
        super(context, RssFeeds.DATEBASE_NAME, null, RssFeeds.DATABASE_VERSION);
    }

    /**
     * DBへのテーブル作成処理
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //RSSフィードテーブルの作成
        db.execSQL("CREATE TABLE rssfeeds(" + RssFeeds.RssFeedColumns._ID + " INTEGER PRIMARY KEY," + 
            RssFeeds.RssFeedColumns._COUNT + " INTEGER," + RssFeeds.RssFeedColumns.CHANNEL_LINK + " " + RssFeeds.RssFeedColumns.CHANNEL_LINK_TYPE + "," + 
            RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK + " " + RssFeeds.RssFeedColumns.CHANNEL_LINK_TYPE + "," +
            RssFeeds.RssFeedColumns.CHANNEL_NAME + " " + RssFeeds.RssFeedColumns.CHANNEL_NAME_TYPE + "," +
            RssFeeds.RssFeedColumns.CHANNEL_DESC + " " + RssFeeds.RssFeedColumns.CHANNEL_DESC_TYPE + "," +
            RssFeeds.RssFeedColumns.CHANNEL_LANG + " " + RssFeeds.RssFeedColumns.CHANNEL_LANG_TYPE + "," +
            RssFeeds.RssFeedColumns.UPDATE_CYCLE + " " + RssFeeds.RssFeedColumns.UPDATE_CYCLE_TYPE + "," +
            RssFeeds.RssFeedColumns.LAST_UPDATE  + " " + RssFeeds.RssFeedColumns.LAST_UPDATE_TYPE + ");"
        );

        //RSSフィードコンテンツテーブルの作成
        db.execSQL("CREATE TABLE rssfeedcontents(" + RssFeeds.RssFeedContentColumns._ID + " INTEGER," + RssFeeds.RssFeedContentColumns._COUNT + " INTEGER," +
            RssFeeds.RssFeedContentColumns.CHANNEL_ID + " " + RssFeeds.RssFeedContentColumns.CHANNEL_ID_TYPE + "," +
            RssFeeds.RssFeedContentColumns.ITEM_TITLE + " " + RssFeeds.RssFeedContentColumns.ITEM_TITLE_TYPE + "," + 
            RssFeeds.RssFeedContentColumns.ITEM_AUTHOR + " " + RssFeeds.RssFeedContentColumns.ITEM_AUTHOR_TYPE + "," + 
            RssFeeds.RssFeedContentColumns.ITEM_LINK + " " + RssFeeds.RssFeedContentColumns.ITEM_LINK_TYPE + "," +
            RssFeeds.RssFeedContentColumns.ITEM_DESCRIPTION + " " + RssFeeds.RssFeedContentColumns.ITEM_DESCRIPTION_TYPE + "," +
            RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL + " " + RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL_TYPE + "," +
            RssFeeds.RssFeedContentColumns.PUB_DATE + " " + RssFeeds.RssFeedContentColumns.PUB_DATE_TYPE + "," +
            "PRIMARY KEY(" + RssFeeds.RssFeedContentColumns._ID + ")" + ");"
        );
    }

    /**
     * DBの更新時
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE rssfeeds;");
        db.execSQL("DROP TABLE rssfeedcontents;");
    }
}
