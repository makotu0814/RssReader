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

    /** RSS�t�B�[�hListView */
    private ListView rssFeedList;

    /** �I�𒆂̃��X�gID*/
    private long selectListId;

    /** ���X�gID�ƁARSS��ID�̃}�b�s���O*/
    private HashMap<Long, String> idMap = new HashMap<Long, String>();

    /** ���X�gID�ƁARSS��URL�̃}�b�s���O*/
    private HashMap<Long, String> urlMap = new HashMap<Long, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //��ʊJ�n���ɃR���e���c�v���o�C�_���擾�ARssFeeds�N���X�ɐݒ�
        RssFeeds.setContentResolver(getContentResolver());

        //��ʃ��C�A�E�g
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linearLayout);

        //�{�^�����C�A�E�g
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        //�f�o�b�O�p
        initRssList();

        //RSS�ꗗ���X�g
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
     * ListView�̕\�����e���f�[�^�x�[�X����擾�����l�ōX�V
     */
    protected void getArrayAdapter() {
        //RSS�t�B�[�h�e�[�u�����l�����ׂăJ�[�\���Ŏ擾
        Cursor mRssCursor = RssFeeds.query(RssFeeds.RssFeedColumns.CONTENT_URI, RssFeeds.RSSFEED_PROJECTION_MAP.keySet().toArray(new String[0]), null, null, null);
        ArrayList<String> rssList = new ArrayList<String>();
        int menuId = 0;
        //�o�^����Ă���RSS�t�B�[�h�����[�v
        while (mRssCursor.moveToNext()) {
            //�e�[�u���̃J�������擾���A�l���擾
            int rssIdCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns._ID);
            String rssId = mRssCursor.getString(rssIdCol);

            //�L���̃^�C�g���̃J�����ʒu���擾���l���擾
            int rssChannelNameCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_NAME);
            String channelTitle = mRssCursor.getString(rssChannelNameCol);

            //�L���ւ̃����N(Rss�̔z�M��)�̃J�����ʒu���擾���l���擾
            int rssChannelLinkCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_LINK);
            String channelLink = mRssCursor.getString(rssChannelLinkCol);

            //RSS�t�B�[�h��URL�̃J�����ʒu���擾���l���擾
            int rssFeedUrlCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK);
            String feedUrl = mRssCursor.getString(rssFeedUrlCol);

            //ListView�̃��X�gId��rss��id���}�b�v�I�u�W�F�N�g�Ɋi�[
            idMap.put(new Long(menuId), rssId);
            urlMap.put(new Long(menuId), feedUrl);
            menuId++;

            //Rss�t�B�[�h�ꗗ�ɂ̓^�C�g����RSS�t�B�[�h��URL��\��
            rssList.add(channelTitle + System.getProperty("line.separator") + "\t" + channelLink);
        }
        String[] data = (String[])rssList.toArray(new String[0]);
        //RSS�t�B�[�h�ꗗ��ʂ��f�[�^�x�[�X��RSS�t�B�[�h�̒l�ōX�V
        rssFeedList.setAdapter(new ArrayAdapter<String>(this, R.layout.rss_list_layout, data));
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == rssFeedList) {
            selectListId = id;
            startReadRssActivity();
        }
    }

    /** 
     * �쐬�������j���[��XML�t�@�C����W�J���A�A�N�V�����o�[�ɔz�u����
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_rss_list, menu);
        return true;
    }

    /** 
     * �A�N�V�����o�[�̃��j���[���I�����ꂽ���ɌĂяo�����
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add:
            //RSS�t�B�[�h�ǉ���ʂ֑J��
            //startRegistRssActivity();
            LayoutInflater inflater = LayoutInflater.from(this);
            final View view = inflater.inflate(R.layout.settings, null);
            DialogUtil.showYesNoCustomDialog(this, R.string.label_rss_feed, view, new DialogInterface.OnClickListener() {
                
                public void onClick(DialogInterface dialog, int which) {
                    //OK�{�^���������ꂽ���̏���
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
            //�A�v�����I������
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
                 //RSS�̋L����ǂݍ��݃f�[�^�x�[�X�֓o�^
                 new Thread(new Runnable() {
                     
                     public void run() {
                        //�ʃX���b�h��RSS�R���e���c���擾���Ă���
                        RssParser.parseRssContents(rssUrl, String.valueOf(id));
                     }
                 }).start();

                String successMsg = (String) activity.getResources().getText(R.string.fetch_rss_feed_success);
                ToastUtil.showToastShort(activity, successMsg);

                 //�o�^���ꂽRSS�t�B�[�h����ʂɔ��f������
                 activity.getArrayAdapter();
             } else {
                 String successFail = (String) activity.getResources().getText(R.string.fetch_rss_feed_fail);
                 ToastUtil.showToastShort(activity, successFail);
             }
         }
     }

    /**
     * RSS�\����ʂ֑J��
     */
    private void startReadRssActivity() {
        //Intent�ւ̃C���X�^���X����
        Intent intent = new Intent(this, RssArticleListActivity.class);
        //RSS�\����ʂɃ��C�����URL��ID�𑗐M
        String rssid = (String)idMap.get(Long.valueOf(selectListId));
        String url = (String)urlMap.get(Long.valueOf(selectListId));
        intent.putExtra("url", url);
        intent.putExtra("id", rssid);
        //RSS�\����ʂ̋N��
        startActivityForResult(intent, 0);
    }

}
