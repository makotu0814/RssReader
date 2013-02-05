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

    private SQLiteDatabase mDB; //DB�̃I�u�W�F�N�g

    private static final int RSSFEEDS = 1001;   //RssFeeds�e�[�u����ID���Ȃ�URI�̏ꍇ�ɕԋp�����l
    private static final int RSSFEED_ID = 1002; //RssFeeds�e�[�u����ID������URI�̏ꍇ�ɕԋp�����l
    private static final int RSSFEED_CONTENTS = 1003;   // RssFeedContents�e�[�u����ID������ꍇ�ɕԋp�����l
    private static final int RSSFEED_CONTENT_ID = 1004; //RssFeedContents�e�[�u����ID������URI�̏ꍇ�ɕԋp�����l

    /**
     * URL�̃}�b�`���O�ϐ�
     */
    private static final UriMatcher URI_MATCHER;

    static {
        //UrlMatcher�̐���
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        //Url���ucom.makotu.rss.readerr/rss�v�ł����RSSFEEDS(=1001)�̒l��ԋp����
        URI_MATCHER.addURI("com.makotu.rss.reader", "rss", RSSFEEDS);
        //UrI���ucom.makotu.rss.readerr/rss/���l�v�ł����RSSFEED_ID(=1002)�̒l��ԋp����
        URI_MATCHER.addURI("com.makotu.rss.reader", "rss/#", RSSFEED_ID);
        //UrI���ucom.makotu.rss.readerr/rsscontens�v�ł����RSSFEED_CONTENTS(=1003)�̒l��ԋp����
        URI_MATCHER.addURI("com.makotu.rss.reader", "rsscontents", RSSFEED_CONTENTS);
        //UrI���ucom.makotu.rss.readerr/rsscontens/���l�v�ł����RSSFEED_CONTENT_ID(=1004)�̒l��ԋp����
        URI_MATCHER.addURI("com.makotu.rss.reader", "rsscontents/#", RSSFEED_CONTENT_ID);
    }

    /**
     * �A�v���P�[�V�����N�����ɌĂяo�����
     */
    @Override
    public boolean onCreate() {
        //�f�[�^�x�[�X�w���p�[�쐬
        RSSFeedDBHelper dbHelper = new RSSFeedDBHelper(getContext());
        
        //�f�[�^�x�[�X�w���p�[����f�[�^�x�[�X�I�u�W�F�N�g�̎擾
        mDB = dbHelper.getWritableDatabase();
        
        return mDB != null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * �f�[�^�̑}������
     * @param   uri Insert�������s���e�[�u��������Uri
     * @param   values  Insert���s���l
     * @return  Insert���ꂽ�f�[�^���R�[�h������Uri
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Uri��value�̊m�F
        if (uri == null || values == null) {
            throw new IllegalArgumentException();
        }
        //UrlMatcher�ƁA�n���ꂽURL���r���A�ǂ��̃e�[�u���ɃA�N�Z�X���悤�Ƃ��Ă��邩����
        int match = URI_MATCHER.match(uri);

        //Insert���ꂽ���R�[�hID
        long rowID = 0;

        //UriMatcher�Ŕ�r���ꂽ�l�ŕ���
        switch (match) {
        case RSSFEEDS:
            if (!values.containsKey(RssFeeds.RssFeedColumns.CHANNEL_NAME)) {
                throw new IllegalArgumentException();
            }
            //RssFeeds�e�[�u����ContentValues�̒l��Insert
            rowID = mDB.insert("rssfeeds", "", values);

            if (rowID > 0) {
                //�C���T�[�g���ꂽ��Uri��ID��t�^���ĕԋp����
                Uri nUri = ContentUris.withAppendedId(RssFeeds.RssFeedColumns.CONTENT_URI, rowID);
                return nUri;
            }
            throw new SQLException(uri.toString());
        case RSSFEED_CONTENTS:
            //RssFeedContens�e�[�u����ContentValues�̒l���i�[
            rowID = mDB.insert("rssfeedcontents", "", values);

            if (rowID > 0) {
                //�C���T�[�g�����ꂽ��Uri��ID��t�^���ĕԋp����
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
     * @param   uri Delete�������s���e�[�u��������Uri
     * @param   selection   �f�[�^���폜����ۂ�Where��
     * @param   selectionArgs   selection�Łu?�v�𗘗p�����ۂɒu������l
     * @return  �폜����
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int res = 0;
        //Uri�̊m�F
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        String feedID;
        int match = URI_MATCHER.match(uri);
        
        switch (match) {
        case RSSFEED_ID:
            //URI����ID���擾
            feedID = uri.getPathSegments().get(1);
            //������ID�Ɏw�肳�ꂽ�l��RssFeeds�e�[�u���̃f�[�^���폜
            res = mDB.delete("rssfeeds", "_id=" + feedID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            break;
        case RSSFEEDS:
            //������Where���RssFeeds�e�[�u���̃f�[�^���폜
            res = mDB.delete("rssfeeds", selection, selectionArgs);
            break;
        case RSSFEED_CONTENT_ID:
            //Uri����ID���擾
            feedID = uri.getPathSegments().get(1);
            //������ID�Ɏw�肳�ꂽ�l��RssFeedContents�e�[�u���̃f�[�^���폜
            res = mDB.delete("rssfeedcontents", "_id=" + feedID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            break;
        case RSSFEED_CONTENTS:
            //������Where���RssFeedContents�e�[�u���̃f�[�^���폜
            res = mDB.delete("rssfeedcontents", selection, selectionArgs);
            break;
        default:
            break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return res;
    }

    /**
     * �f�[�^�̍X�V����
     * @param   uri �f�[�^���X�V���鏈�����s���e�[�u��������Uri
     * @param   values  update���s���l
     * @param   selection   �f�[�^���X�V����ۂ�Where��
     * @param   selectionArgs   selection�Łu?�v�𗘗p�����ۂɒu������l
     * @return  �X�V����
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int result = 0;
        switch (URI_MATCHER.match(uri)) {
        case RSSFEEDS:
            //RssFeeds�e�[�u���̍X�V
            result = mDB.update("rssfeeds", values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            break;
        case RSSFEED_ID:
            //Uri���ID���擾
            String feedID = uri.getPathSegments().get(1);

            //������Where���RssFeeds�e�[�u�����X�V
            result = mDB.update("rssfeeds", values, "_id=" + feedID + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            break;
        default:
            throw new IllegalArgumentException();
        }
        return result;
    }

    /**
     * �N�G���[�̔��s
     * @param uri   �f�[�^���X�V���鏈�����s���e�[�u��������Uri
     * @param projection    �e�[�u���̒l���Q�Ƃ��鍀�ڂ̃��X�g
     * @param selection �f�[�^���擾����ۂ�Where��
     * @param selectionArgs selection�Łu�H�v�𗘗p�����ۂɒu������l
     * @param sortOrder �\�[�g���ږ�
     * @return  �f�[�^�x�[�X����擾�����J�[�\���I�u�W�F�N�g
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // �N�G���r���_�[�̍쐬
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String orderBy = null;

        //Uri��UrlMtacher�Ŕ��肵����
        switch (URI_MATCHER.match(uri)) {
        case RSSFEEDS:
            //�e�[�u������ݒ�
            qb.setTables("rssfeeds");
            //�Q�Ƃ��鍀�ڂ�ݒ�
            qb.setProjectionMap(RssFeeds.RSSFEED_PROJECTION_MAP);

            //�\�[�g���ږ����w�肳��Ă��Ȃ��ꍇ�͏����l�̃\�[�g����
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedColumns.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;
        case RSSFEED_ID:
            //�e�[�u�������w��
            qb.setTables("rssfeeds");

            //Where���Uri����擾����ID�����Ă���
            qb.appendWhere("_id=" + uri.getLastPathSegment());

            //�\�[�g���ږ����w�肳��Ă��Ȃ��ꍇ�͏����l�̃\�[�g����
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;
        case RSSFEED_CONTENTS:
            //�e�[�u�������w��
            qb.setTables("rssfeedcontents");

            //�Q�Ƃ��鍀�ڂ��w��
            qb.setProjectionMap(RssFeeds.RSSCONTENT_PROJECTION_MAP);

            //�\�[�g���ږ����w�肳��Ă��Ȃ��ꍇ�͏����l�̃\�[�g����
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER;
            } else {
            orderBy = sortOrder;
            }
            break;
        case RSSFEED_CONTENT_ID:
            //�e�[�u�������w��
            qb.setTables("rssfeedcontents");

            //Where���Uri����擾����ID���w��
            qb.appendWhere("_id=" + uri.getLastPathSegment());

            //�\�[�g���ږ����w�肳��Ă��Ȃ��ꍇ�͏����l�̃\�[�g����
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER;
            } else {
            orderBy = sortOrder;
            }
            break;
        default:
            throw new IllegalArgumentException();
        }
        //�N�G���𔭍s���ăJ�[�\���I�u�W�F�N�g���擾
        Cursor cursor = qb.query(mDB, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

}
