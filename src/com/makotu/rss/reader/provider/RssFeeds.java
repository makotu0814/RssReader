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
     * DB�t�@�C����
     */
    public static final String DATEBASE_NAME = "rssfeeds.db";

    /**
     * DB�̃o�[�W����
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * DB:RSSFeeds�e�[�u���̒�`�N���X
     * @author Makoto
     *
     */
    public static final class RssFeedColumns implements BaseColumns {

        //RssFeeds�e�[�u��������Uri
        public static final Uri CONTENT_URI = Uri.parse("content://com.makotu.rss.reader/rss");

        //RssFeeds�e�[�u���̍��� �`�����l��Link
        public static final String CHANNEL_LINK = "channel_link";
        public static final String CHANNEL_LINK_TYPE = "VARCHAR";

        //RssFeeds�e�[�u���̍��� �`�����l���t�B�[�hLink
        public static final String CHANNEL_FEEDS_LINK = "channel_feeds_link";
        public static final String CHANNEL_FEEDS_LINK_TYPE = "VARCHAR";

        //RssFeeds�e�[�u���̍��� �`�����l���t�B�[�h��
        public static final String CHANNEL_NAME = "channel_name";
        public static final String CHANNEL_NAME_TYPE = "VARCHAR";

        //RssFeeds�e�[�u���̍��� �X�V�T�C�N��
        public static final String UPDATE_CYCLE = "update_cycle";
        public static final String UPDATE_CYCLE_TYPE = "INTEGER";

        //RssFeeds�e�[�u���̍��� �ŏI�X�V��
        public static final String LAST_UPDATE = "last_upd";
        public static final String LAST_UPDATE_TYPE = "INTEGER";

        //RssFeeds�e�[�u���̍��� Rss�t�B�[�h�̌���
        public static final String CHANNEL_LANG = "channel_lang";
        public static final String CHANNEL_LANG_TYPE = "VARCHAR";

        //RssFeed�e�[�u���̍��� Rss�t�B�[�h�̉��
        public static final String CHANNEL_DESC = "channel_desc";
        public static final String CHANNEL_DESC_TYPE = "TEXT";

        //RssFeeds�e�[�u���̃\�[�g�J���� _ID
        public static final String DEFAULT_SORT_ORDER = _ID;
    }

    /**
     * DB:RssFeedContents�e�[�u���̒�`�N���X
     * @author Makoto
     *
     */
    public static final class RssFeedContentColumns implements BaseColumns {
        //RssFeedContents������Uri
        public static final Uri CONTENT_URI = Uri.parse("content://com.makotu.rss.reader/rsscontents");

        //RssFeedContents�e�[�u������ �`�����l��ID
        public static final String CHANNEL_ID = "channel_id";
        public static final String CHANNEL_ID_TYPE = "INTEGER";

        //RssFeedContents�e�[�u���̍��� �L����URL
        public static final String ITEM_LINK = "item_link";
        public static final String ITEM_LINK_TYPE = "VARCHAR";

        //RssFeedsContents�e�[�u���̍��� �L���̉��
        public static final String ITEM_DESCRIPTION = "item_desc";
        public static final String ITEM_DESCRIPTION_TYPE = "text";

        //RssFeedContents�e�[�u���̍��� �L���̃^�C�g��
        public static final String ITEM_TITLE = "item_title";
        public static final String ITEM_TITLE_TYPE = "VARCHAR";

        //RssFeedContents�e�[�u���̍��� �L���̍쐬��
        public static final String ITEM_AUTHOR = "item_author";
        public static final String ITEM_AUTHOR_TYPE = "VARCHAR";

        //RssFeedContents�e�[�u���̍���  �L���̃T���l�C���p�X
        public static final String ITEM_THUMNAIL = "item_thumnail";
        public static final String ITEM_THUMNAIL_TYPE = "VARCHAR";

        //RssFeedContents�e�[�u���̍���  �L���̌��J����
        public static final String PUB_DATE = "pub_date";
        public static final String PUB_DATE_TYPE = "VARCHAR";

        //RssFeedContents�e�[�u���̃\�[�g���� �L���̍X�V���t
        public static final String DEFAUlT_SORT_ORDER = PUB_DATE + " desc";
    }

    /**
     * �N�G���𔭍s����ۂɎ擾����e�[�u�����ڂ̃}�b�v�I�u�W�F�N�g
     * Select column1, column2, column3 from XXX_table��
     * field1�`3���}�b�v�I�u�W�F�N�g�Ŋi�[���Ă���
     */
    public static final HashMap<String, String> RSSFEED_PROJECTION_MAP;
    public static final HashMap<String, String> RSSCONTENT_PROJECTION_MAP;
    static {
        //RssFeeds�e�[�u���̍��ږ��Ƒ����}�b�s���O
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

        //RssFeeds�e�[�u���̍��ږ��Ƒ����̃}�b�s���O
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
     * �R���e���c�v���o�C�_
     */
    private static ContentResolver mContentResolver;

    /**
     * DB�փf�[�^�}������
     * @param uri
     * @param values
     * @return
     */
    public static Uri insert(Uri uri, ContentValues values) {
        return mContentResolver.insert(uri, values);
    }

    /**
     * �Ώۂ̃f�[�^�����݂��Ȃ��ꍇ�̓C���T�[�g�A���݂���ꍇ�́A�Ώۂ̃f�[�^�s��Ԃ�
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
     * DB����̍폜����
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static int delete(Uri uri, String selection, String[] selectionArgs) {
        return mContentResolver.delete(uri, selection, selectionArgs);
    }

    /**
     * DB�̍X�V����
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
     * DB�̃N�G�����s���s����
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
     * �R���e���c���]���o�[�̏���
     * @return
     */
    public static ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /**
     * �R���e���c���]���o�[�̃Z�b�g
     * @param contentResolver
     */
    public static void setContentResolver(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }
}
