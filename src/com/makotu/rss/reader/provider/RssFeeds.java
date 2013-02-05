package com.makotu.rss.reader.provider;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public abstract class RssFeeds {

    /**
     * DBファイル名
     */
    public static final String DATEBASE_NAME = "rssfeeds.db";

    /**
     * DBのバージョン
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * DB:RSSFeedsテーブルの定義クラス
     * @author Makoto
     *
     */
    public static final class RssFeedColumns implements BaseColumns {

        //RssFeedsテーブルを示すUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.makotu.rss.reader/rss");

        //RssFeedsテーブルの項目 チャンネルLink
        public static final String CHANNEL_LINK = "channel_link";
        public static final String CHANNEL_LINK_TYPE = "VARCHAR";

        //RssFeedsテーブルの項目 チャンネルフィードLink
        public static final String CHANNEL_FEEDS_LINK = "channel_feeds_link";
        public static final String CHANNEL_FEEDS_LINK_TYPE = "VARCHAR";

        //RssFeedsテーブルの項目 チャンネルフィード名
        public static final String CHANNEL_NAME = "channel_name";
        public static final String CHANNEL_NAME_TYPE = "VARCHAR";

        //RssFeedsテーブルの項目 更新サイクル
        public static final String UPDATE_CYCLE = "update_cycle";
        public static final String UPDATE_CYCLE_TYPE = "INTEGER";

        //RssFeedsテーブルの項目 最終更新日
        public static final String LAST_UPDATE = "last_upd";
        public static final String LAST_UPDATE_TYPE = "INTEGER";

        //RssFeedsテーブルの項目 Rssフィードの言語
        public static final String CHANNEL_LANG = "channel_lang";
        public static final String CHANNEL_LANG_TYPE = "VARCHAR";

        //RssFeedテーブルの項目 Rssフィードの解説
        public static final String CHANNEL_DESC = "channel_desc";
        public static final String CHANNEL_DESC_TYPE = "TEXT";

        //RssFeedsテーブルのソートカラム _ID
        public static final String DEFAULT_SORT_ORDER = _ID;
    }

    /**
     * DB:RssFeedContentsテーブルの定義クラス
     * @author Makoto
     *
     */
    public static final class RssFeedContentColumns implements BaseColumns {
        //RssFeedContentsを示すUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.makotu.rss.reader/rsscontents");

        //RssFeedContentsテーブル項目 チャンネルID
        public static final String CHANNEL_ID = "channel_id";
        public static final String CHANNEL_ID_TYPE = "INTEGER";

        //RssFeedContentsテーブルの項目 記事のURL
        public static final String ITEM_LINK = "item_link";
        public static final String ITEM_LINK_TYPE = "VARCHAR";

        //RssFeedsContentsテーブルの項目 記事の解説
        public static final String ITEM_DESCRIPTION = "item_desc";
        public static final String ITEM_DESCRIPTION_TYPE = "text";

        //RssFeedContentsテーブルの項目 記事のタイトル
        public static final String ITEM_TITLE = "item_title";
        public static final String ITEM_TITLE_TYPE = "VARCHAR";

        //RssFeedContentsテーブルの項目 記事の作成者
        public static final String ITEM_AUTHOR = "item_author";
        public static final String ITEM_AUTHOR_TYPE = "VARCHAR";

        //RssFeedContentsテーブルの項目  記事のサムネイルパス
        public static final String ITEM_THUMNAIL = "item_thumnail";
        public static final String ITEM_THUMNAIL_TYPE = "VARCHAR";

        //RssFeedContentsテーブルの項目  記事の公開日時
        public static final String PUB_DATE = "pub_date";
        public static final String PUB_DATE_TYPE = "VARCHAR";

        //RssFeedContentsテーブルのソート項目 記事の更新日付
        public static final String DEFAUlT_SORT_ORDER = PUB_DATE + " desc";
    }

    /**
     * クエリを発行する際に取得するテーブル項目のマップオブジェクト
     * Select column1, column2, column3 from XXX_tableの
     * field1～3をマップオブジェクトで格納している
     */
    public static final HashMap<String, String> RSSFEED_PROJECTION_MAP;
    public static final HashMap<String, String> RSSCONTENT_PROJECTION_MAP;
    static {
        //RssFeedsテーブルの項目名と属性マッピング
        RSSFEED_PROJECTION_MAP = new HashMap<String, String>();
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns._ID, RssFeeds.RssFeedColumns._ID);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns._COUNT, RssFeeds.RssFeedColumns._COUNT);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns.CHANNEL_NAME, RssFeeds.RssFeedColumns.CHANNEL_NAME);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns.CHANNEL_LINK, RssFeeds.RssFeedColumns.CHANNEL_LINK);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK, RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns.CHANNEL_DESC, RssFeeds.RssFeedColumns.CHANNEL_DESC);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns.CHANNEL_LANG, RssFeeds.RssFeedColumns.CHANNEL_LANG);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns.UPDATE_CYCLE, RssFeeds.RssFeedColumns.UPDATE_CYCLE);
        RSSFEED_PROJECTION_MAP.put(RssFeeds.RssFeedColumns.LAST_UPDATE, RssFeeds.RssFeedColumns.LAST_UPDATE);

        //RssFeedsテーブルの項目名と属性のマッピング
        RSSCONTENT_PROJECTION_MAP = new HashMap<String, String>();
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns._ID, RssFeeds.RssFeedContentColumns._ID);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns._COUNT, RssFeeds.RssFeedContentColumns._COUNT);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns.CHANNEL_ID, RssFeeds.RssFeedContentColumns.CHANNEL_ID);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns.ITEM_TITLE, RssFeeds.RssFeedContentColumns.ITEM_TITLE);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns.ITEM_AUTHOR, RssFeeds.RssFeedContentColumns.ITEM_AUTHOR);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns.ITEM_LINK, RssFeeds.RssFeedContentColumns.ITEM_LINK);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns.ITEM_DESCRIPTION, RssFeeds.RssFeedContentColumns.ITEM_DESCRIPTION);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL, RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL);
        RSSCONTENT_PROJECTION_MAP.put(RssFeeds.RssFeedContentColumns.PUB_DATE, RssFeeds.RssFeedContentColumns.PUB_DATE);
    }

    /**
     * コンテンツプロバイダ
     */
    private static ContentResolver mContentResolver;

    /**
     * DBへデータ挿入処理
     * @param uri
     * @param values
     * @return
     */
    public static Uri insert(Uri uri, ContentValues values) {
        return mContentResolver.insert(uri, values);
    }

    /**
     * 対象のデータが存在しない場合はインサート、存在する場合は、対象のデータ行を返す
     * @param uri
     * @param selection
     * @param selectionArgs
     * @param values
     * @return
     */
    public static Uri insertIfNotExists(Uri uri, String selection, String[] selectionArgs, ContentValues values) {
        Uri u = null;
        
        String[] projection = {BaseColumns._ID};
        Cursor cursor = mContentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor.getCount() <= 0) {
            u = insert(uri, values);
        } else {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
            u = ContentUris.withAppendedId(uri, Long.parseLong(id));
        }
        cursor.close();
        return u;
    }

    /**
     * DBからの削除処理
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static int delete(Uri uri, String selection, String[] selectionArgs) {
        return mContentResolver.delete(uri, selection, selectionArgs);
    }

    /**
     * DBの更新処理
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mContentResolver.update(uri, values, selection, selectionArgs);
    }

    /**
     * DBのクエリ発行発行処理
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public static Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mContentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * コンテンツリゾルバーの処理
     * @return
     */
    public static ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /**
     * コンテンツリゾルバーのセット
     * @param contentResolver
     */
    public static void setContentResolver(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }
}
