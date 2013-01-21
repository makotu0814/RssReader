package com.makotu.rss.reader.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.makotu.rss.reader.provider.RssFeeds;
import com.makotu.rss.reader.util.LayoutUtil;
import com.makotu.rssreader.R;

public class RssListActivity extends RssBaseActivity implements OnItemClickListener, OnClickListener {

    /** RSS�t�B�[�hListView */
    private ListView rssFeedList;

    /** RSS���[�_�[�̒ǉ��{�^��*/
    private Button addBtn;

    /** RSS���[�_�[�̏I���{�^��*/
    private Button endBtn;

    /** �I�v�V�������j���[ RSS�̒ǉ�*/
    private static final int MENU_ID_ADD = (Menu.FIRST + 1);

    /** �I�v�V�������j���[Rss�̃��[�_�[�̏I��*/
    private static final int MENU_ID_END = (Menu.FIRST + 2);

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

        //RSS�ꗗ���X�g
        rssFeedList = new ListView(this);
        rssFeedList.setOnItemClickListener(this);
        getArrayAdapter();

        //RSS�ǉ��{�^��
        addBtn = new Button(this);
        addBtn.setText("RSS�t�B�[�h�̒ǉ�");
        addBtn.setOnClickListener(this);

        //RSS�폜�{�^��
        endBtn = new Button(this);
        endBtn.setText("RSS���[�_�[�̏I��");
        endBtn.setOnClickListener(this);
        btnLayout.addView(addBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        btnLayout.addView(endBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));

        linearLayout.addView(btnLayout);
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
     * ListView�̕\�����e���f�[�^�x�[�X����擾�����l�ł�������
     */
    private void getArrayAdapter() {
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
     * �{�^���N���b�N���̃C�x���g���X�i�[
     */
    public void onClick(View view) {
        //RSS�t�B�[�h�ǉ��{�^��
        if (view == addBtn) {
            //RSS�t�B�[�h�ǉ���ʂ֑J��
            startRegistRssActivity();
        } else if (view == endBtn) {
            //�A�v���I������
            back();
        }
    }

    /**
     * �I�v�V�������j���[�̒ǉ�
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_ADD, Menu.NONE, "RSS�t�B�[�h�̒ǉ�");
        menu.add(Menu.NONE, MENU_ID_END, Menu.NONE, "RSS�t�B�[�h�̍폜");
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * ���j���[�{�^���N���b�N���̃C�x���g���X�i�[
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ID_ADD:
            startRegistRssActivity();
            break;
        case MENU_ID_END:
            back();
            break;
        default:
            break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * RSS�o�^��ʂ֑J��
     */
    private void startRegistRssActivity() {
        //Intent�ւ̃C���X�^���X����
        Intent intent = new Intent(this, RegistRssFeedActivity.class);
        String rssid = (String)idMap.get(new Long(selectListId));
        intent.putExtra("id", rssid);
        //�T�u��ʂ̋N��
        startActivityForResult(intent, 0);
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
