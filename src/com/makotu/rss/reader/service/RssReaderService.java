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
 * RSS�ǂݍ���Service
 * �K��̎��ԂɂȂ�����RSS�̓ǂݍ��݂��s��
 * @author Makoto
 *
 */
public class RssReaderService extends Service implements Runnable{

    /** �X���b�h����*/
    private boolean alive = false; 

    /** �X���b�h*/
    private Thread thread;

    /** �f�[�^�x�[�X�̃J�[�\��*/
    private Cursor  mRSSCursor;

    @Override
    public void onCreate() {
        LogUtil.debug(RssReaderService.class, "onCreate()");

        //�m�[�e�B�t�B�P�[�V�����}�l�[�W���̎擾
        //nNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationUtil.showNotification(this, R.drawable.ic_launcher, "RSS���[�_�[�����s���܂�", 
        "RSS���[�_�[�T�[�r�X", "RSS���[�_�[�T�[�r�X�J�n���܂���", RssListActivity.class);

        //�g�[�X�g�̕\��
        ToastUtil.showToastShort(getApplicationContext(), "RssReaderService���J�n���܂�");

        //Service�̋N���Ɠ����ɃX���b�h�����s�\��Ԃɂ���
        this.alive = true;

        //�X���b�h�쐬
        thread = new Thread(null, this, "RssReaderService");

        //�X���b�h�̊J�n
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Service�j����
     */
    @Override
    public void onDestroy() {
        LogUtil.debug(RssReaderService.class, "onDestroy()");
        ToastUtil.showToastShort(this, "RssReaderService���~���܂���");

        //�X���b�h�̗L���t���O��false�ɂ���
        this.alive = false;
        //�X���b�h�̒�~
        thread.stop();
        //�m�[�e�B�t�B�P�[�V�����}�l�[�W���[��j��
        NotificationUtil.cancelNotification(this, R.drawable.ic_launcher);
    }

    /**
     * RSS�t�B�[�h�̎擾����
     */
    public void run() {
        //�f�[�^�x�[�X����RssFeeds�̒l���擾
        mRSSCursor = RssFeeds.query(RssFeeds.RssFeedColumns.CONTENT_URI, RssFeeds.RSSFEED_PROJECTION_MAP.keySet().toArray(new String[0]), null, null, null);
        //Thread�t���O���L��
        while (this.alive) {
            try {
                //10���ԃX���b�h�x�~
                Thread.sleep(1 * 10 * 60 * 1000);

                //RssFeeds�e�[�u���̒l���Ď擾
                mRSSCursor.requery();

                //RssFeeds�e�[�u���̃`�����l���t�B�[�h�����N�̃J�����ʒu���擾
                int rssCFeedLinkCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK);
                //RssFeeds�e�[�u����ID�̃J�����ʒu���擾
                int rssIDCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns._ID);
                //RssFeeds�e�[�u���̍X�V�Ԋu�̃J�����ʒu���擾
                int rssUpdCyCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns.UPDATE_CYCLE);
                //RssFeeds�e�[�u���̍ŏI�X�V���̃J�����ʒu���擾
                int rssLastUpdCol = mRSSCursor.getColumnIndex(RssFeeds.RssFeedColumns.LAST_UPDATE);

                long now = System.currentTimeMillis();

                //RssFeeds�̃��R�[�h�����[�v�L�����擾
                while (mRSSCursor.moveToNext()) {
                    //�f�[�^�x�[�X���ŏI�X�V���t���擾
                    long lastUpdate = mRSSCursor.getLong(rssLastUpdCol);
                    //�f�[�^�x�[�X���X�V�Ԋu���擾
                    long updateCycle = mRSSCursor.getLong(rssUpdCyCol);

                    //�ŏI�X�V���t�ƍX�V�Ԋu�𑫂��Č��݂����Â��Ȃ��RSS�t�B�[�h��URL����l���擾
                    if (lastUpdate + updateCycle <  now) {
                        //RssFeeds�e�[�u����RSS�t�B�[�h��URL���擾
                        String channel_feed_link = (String)mRSSCursor.getString(rssCFeedLinkCol);

                        //RssFeeds�e�[�u����id���擾
                        String id = (String)mRSSCursor.getString(rssIDCol);
                        //RSS�t�B�[�h��URL����f�[�^���擾���f�[�^�x�[�X�֊i�[
                        RssParser.parseRssContents(channel_feed_link, id);
                    }
                }
                //�J�[�\����񊈐����
                mRSSCursor.deactivate();
            } catch (Exception e) {
                LogUtil.error(getClass(), e.getMessage());
            }
        }
        this.cleanUp();
    }

    /**
     * �X���b�h�I�����ɃJ�[�\�����N���[�Y����
     */
    private void cleanUp() {
        mRSSCursor.close();
    }
}
