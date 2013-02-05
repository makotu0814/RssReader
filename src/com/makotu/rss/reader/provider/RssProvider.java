package com.makotu.rss.reader.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class RssProvider extends ContentProvider {

    private SQLiteDatabase mDB; //DBのオブジェクト

    private static final int RSSFEEDS = 1001;   //RssFeedsテーブルのIDがないURIの場合に返却される値
    private static final int RSSFEED_ID = 1002; //RssFeedsテーブルのIDがあるURIの場合に返却される値
    private static final int RSSFEED_CONTENTS = 1003;   // RssFeedContentsテーブルのIDがある場合に返却される値
    private static final int RSSFEED_CONTENT_ID = 1004; //RssFeedContentsテーブルのIDがあるURIの場合に返却される値

    /**
     * URLのマッチング変数
     */
    private static final UriMatcher URI_MATCHER;

    static {
        //UrlMatcherの生成
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        //Urlが「com.makotu.rss.readerr/rss」であればRSSFEEDS(=1001)の値を返却する
        URI_MATCHER.addURI("com.makotu.rss.reader", "rss", RSSFEEDS);
        //UrIが「com.makotu.rss.readerr/rss/数値」であればRSSFEED_ID(=1002)の値を返却する
        URI_MATCHER.addURI("com.makotu.rss.reader", "rss/#", RSSFEED_ID);
        //UrIが「com.makotu.rss.readerr/rsscontens」であればRSSFEED_CONTENTS(=1003)の値を返却する
        URI_MATCHER.addURI("com.makotu.rss.reader", "rsscontents", RSSFEED_CONTENTS);
        //UrIが「com.makotu.rss.readerr/rsscontens/数値」であればRSSFEED_CONTENT_ID(=1004)の値を返却する
        URI_MATCHER.addURI("com.makotu.rss.reader", "rsscontents/#", RSSFEED_CONTENT_ID);
    }

    /**
     * アプリケーション起動時に呼び出される
     */
    @Override
    public boolean onCreate() {
        //データベースヘルパー作成
        RSSFeedDBHelper dbHelper = new RSSFeedDBHelper(getContext());
        
        //データベースヘルパーからデータベースオブジェクトの取得
        mDB = dbHelper.getWritableDatabase();
        
        return mDB != null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * データの挿入処理
     * @param   uri Insert処理を行うテーブルを示すUri
     * @param   values  Insertを行う値
     * @return  Insertされたデータレコードを示すUri
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Uriとvalueの確認
        if (uri == null || values == null) {
            throw new IllegalArgumentException();
        }
        //UrlMatcherと、渡されたURLを比較し、どこのテーブルにアクセスしようとしているか判定
        int match = URI_MATCHER.match(uri);

        //InsertされたレコードID
        long rowID = 0;

        //UriMatcherで比較された値で分岐
        switch (match) {
        case RSSFEEDS:
            if (!values.containsKey(RssFeeds.RssFeedColumns.CHANNEL_NAME)) {
                throw new IllegalArgumentException();
            }
            //RssFeedsテーブルにContentValuesの値をInsert
            rowID = mDB.insert("rssfeeds", "", values);

            if (rowID > 0) {
                //インサートされたらUriにIDを付与して返却する
                Uri nUri = ContentUris.withAppendedId(RssFeeds.RssFeedColumns.CONTENT_URI, rowID);
                return nUri;
            }
            throw new SQLException(uri.toString());
        case RSSFEED_CONTENTS:
            //RssFeedContensテーブルにContentValuesの値を格納
            rowID = mDB.insert("rssfeedcontents", "", values);

            if (rowID > 0) {
                //インサートがされたらUriにIDを付与して返却する
                Uri nUri = ContentUris.withAppendedId(RssFeeds.RssFeedContentColumns.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(nUri, null);
                return nUri;
            }
            throw new SQLException(uri.toString());
        default:
            throw new IllegalArgumentException(uri.toString());
        }
    }

    /**
     * @param   uri Delete処理を行うテーブルを示すUri
     * @param   selection   データを削除する際のWhere句
     * @param   selectionArgs   selectionで「?」を利用した際に置換する値
     * @return  削除件数
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int res = 0;
        //Uriの確認
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        String feedID;
        int match = URI_MATCHER.match(uri);
        
        switch (match) {
        case RSSFEED_ID:
            //URIからIDを取得
            feedID = uri.getPathSegments().get(1);
            //引数のIDに指定された値でRssFeedsテーブルのデータを削除
            res = mDB.delete("rssfeeds", "_id=" + feedID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            break;
        case RSSFEEDS:
            //引数のWhere句でRssFeedsテーブルのデータを削除
            res = mDB.delete("rssfeeds", selection, selectionArgs);
            break;
        case RSSFEED_CONTENT_ID:
            //UriからIDを取得
            feedID = uri.getPathSegments().get(1);
            //引数のIDに指定された値でRssFeedContentsテーブルのデータを削除
            res = mDB.delete("rssfeedcontents", "_id=" + feedID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            break;
        case RSSFEED_CONTENTS:
            //引数のWhere句でRssFeedContentsテーブルのデータを削除
            res = mDB.delete("rssfeedcontents", selection, selectionArgs);
            break;
        default:
            break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return res;
    }

    /**
     * データの更新処理
     * @param   uri データを更新する処理を行うテーブルを示すUri
     * @param   values  updateを行う値
     * @param   selection   データを更新する際のWhere句
     * @param   selectionArgs   selectionで「?」を利用した際に置換する値
     * @return  更新件数
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int result = 0;
        switch (URI_MATCHER.match(uri)) {
        case RSSFEEDS:
            //RssFeedsテーブルの更新
            result = mDB.update("rssfeeds", values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            break;
        case RSSFEED_ID:
            //UriよりIDを取得
            String feedID = uri.getPathSegments().get(1);

            //引数のWhere句でRssFeedsテーブルを更新
            result = mDB.update("rssfeeds", values, "_id=" + feedID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            break;
        default:
            throw new IllegalArgumentException();
        }
        return result;
    }

    /**
     * クエリーの発行
     * @param uri   データを更新する処理を行うテーブルを示すUri
     * @param projection    テーブルの値を参照する項目のリスト
     * @param selection データを取得する際のWhere句
     * @param selectionArgs selectionで「？」を利用した際に置換する値
     * @param sortOrder ソート項目名
     * @return  データベースから取得したカーソルオブジェクト
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // クエリビルダーの作成
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String orderBy = null;

        //UriをUrlMtacherで判定し分岐
        switch (URI_MATCHER.match(uri)) {
        case RSSFEEDS:
            //テーブル名を設定
            qb.setTables("rssfeeds");
            //参照する項目を設定
            qb.setProjectionMap(RssFeeds.RSSFEED_PROJECTION_MAP);

            //ソート項目名が指定されていない場合は初期値のソート項目
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedColumns.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;
        case RSSFEED_ID:
            //テーブル名を指定
            qb.setTables("rssfeeds");

            //Where句にUriから取得したIDをしていｖ
            qb.appendWhere("_id=" + uri.getLastPathSegment());

            //ソート項目名が指定されていない場合は初期値のソート項目
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;
        case RSSFEED_CONTENTS:
            //テーブル名を指定
            qb.setTables("rssfeedcontents");

            //参照する項目を指定
            qb.setProjectionMap(RssFeeds.RSSCONTENT_PROJECTION_MAP);

            //ソート項目名が指定されていない場合は初期値のソート項目
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER;
            } else {
            orderBy = sortOrder;
            }
            break;
        case RSSFEED_CONTENT_ID:
            //テーブル名を指定
            qb.setTables("rssfeedcontents");

            //Where句にUriから取得したIDを指定
            qb.appendWhere("_id=" + uri.getLastPathSegment());

            //ソート項目名が指定されていない場合は初期値のソート項目
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER;
            } else {
            orderBy = sortOrder;
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
        //クエリを発行してカーソルオブジェクトを取得
        Cursor cursor = qb.query(mDB, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

}
