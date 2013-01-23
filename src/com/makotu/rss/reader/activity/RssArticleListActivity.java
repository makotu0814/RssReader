package com.makotu.rss.reader.activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makotu.rss.reader.R;
import com.makotu.rss.reader.asynctask.ThumbnailTask;
import com.makotu.rss.reader.parser.RssParser;
import com.makotu.rss.reader.provider.RssFeeds;
import com.makotu.rss.reader.util.ImageCache;
import com.makotu.rss.reader.util.ImageCacheParams;
import com.makotu.rss.reader.util.ImageLoader;
import com.makotu.rss.reader.util.LayoutUtil;
import com.makotu.rss.reader.util.LogUtil;
import com.makotu.rss.reader.util.ToastUtil;

public class RssArticleListActivity extends RssBaseActivity implements OnClickListener, OnItemClickListener {

     /** �L���ɑΉ�����URL���i�[����}�b�v�I�u�W�F�N�g*/
    private HashMap<Long, String> urlMap = new HashMap<Long, String>();

    /** �L���̈ꗗ��\������ListView*/
    private ListView articleListView;

    /** RSS�t�B�[�h�̍폜�{�^��*/
    private Button deleteBtn;

    /** RSS�t�B�[�h�̍Ď擾�{�^��*/
    private Button reGetBtn;
    
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //��ʃ��C�A�E�g �c�����ɐݒ�
        LinearLayout displayLayout = new LinearLayout(this);
        displayLayout.setOrientation(LinearLayout.VERTICAL);

        //�{�^�����C�A�E�g �������ɐݒ�
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        setContentView(displayLayout);

        //RSS�t�B�[�h�̍폜�{�^��
        deleteBtn = new Button(this);
        deleteBtn.setText("RSS�t�B�[�h���폜");
        deleteBtn.setOnClickListener(this);

        //RSS�t�B�[�h�̍Ď擾�{�^��
        reGetBtn = new Button(this);
        reGetBtn.setText("RSS�t�B�[�h���Ď擾");
        reGetBtn.setOnClickListener(this);

        //�{�^�����C�A�E�g�Ƀ{�^����ǉ�
        btnLayout.addView(deleteBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        btnLayout.addView(reGetBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));

        //�L���ꗗListView�̐���
        articleListView = new ListView(this);
        articleListView.setOnItemClickListener(this);

        getArrayAdapter();

        //�{�^�����C�A�E�g�ƁA�L���ꗗ����ʃ��C�A�E�g�ɒǉ�
        displayLayout.addView(btnLayout);
        displayLayout.addView(articleListView);

        //ImageLoader�̃C���X�^���X����
        imageLoader = new ImageLoader(this);
        imageLoader.setImageCache(new ImageCache(this, new ImageCacheParams(this)));
    }

    /**
     * RSS�t�B�[�h�̈ꗗ���擾��ListView���X�V
     */
    private void getArrayAdapter() {
        int listCnt = 0;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //RSS�t�B�[�h�ꗗ��ʂ��I�����ꂽ�t�B�[�h��ID���擾
        String rssId = (String)extras.get("id");

        //�L�����擾����ۂ�Where��̍쐬
        StringBuffer selection = new StringBuffer(RssFeeds.RssFeedContentColumns.CHANNEL_ID).append("=").append(rssId);

        //�L���̃J�[�\�����f�[�^�x�[�X���擾����
        Cursor mRssCursor = RssFeeds.query(RssFeeds.RssFeedContentColumns.CONTENT_URI, RssFeeds.RSSCONTENT_PROJECTION_MAP.keySet().toArray(new String[0]), selection.toString(), null, RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER);

        List<Article> adapterDataList = new LinkedList<Article>();
        //RSS�t�B�[�h�Ŏ擾�����L�����J�[�\�������[�v
        while (mRssCursor.moveToNext()) {
            Article article = new Article();

            //�^�C�g���J�����ʒu���擾���A�^�C�g���̒l���f�[�^�x�[�X���擾
            int itemThumCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL);
            String thumnail = mRssCursor.getString(itemThumCol);
            article.setThumnail(thumnail);

            //�^�C�g���J�����ʒu���擾���A�^�C�g���̒l���f�[�^�x�[�X���擾
            int itemTitleCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.ITEM_TITLE);
            String title = mRssCursor.getString(itemTitleCol);
            article.setTitle(title);

            //�L����URL�J�����ʒu���擾���A�L����URL�̒l���f�[�^�x�[�X���擾
            int itemLinkCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.ITEM_LINK);
            String link = mRssCursor.getString(itemLinkCol);

            //�L���̍X�V�����J�����ʒu���擾���A�L���̍X�V�����̒l���f�[�^�x�[�X���擾
            int pubDateCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.PUB_DATE);
            String pubDate = mRssCursor.getString(pubDateCol);
            article.setDate(pubDate);

            //�L���̃^�C�g���ƁA�X�V������\��
            adapterDataList.add(article);

            //�L���ꗗ�̑I�����ꂽ�ʒu�ƁA�L����URL���}�b�v�I�u�W�F�N�g�Ɋi�[
            urlMap.put(new Long(listCnt), link);
            listCnt++;
        }
        for (Article article : adapterDataList) {
            LogUtil.debug(this, "url:" + article.thumnail);
        }
        //�L���ꗗ���f�[�^�x�[�X����擾�����l�ōX�V
        articleListView.setAdapter(new AricleListAdapter(this, 0, adapterDataList));
    }

    /** 
     * ListView�̋L���N���b�N���̃C�x���g���X�i�[
     * @param   parent    �N���b�N���ꂽ���X�g�r���[
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == articleListView) {
            String url = (String)urlMap.get(new Long(id));

            //�L���̕\���A�N�e�B�r�e�B�̎w��
            Intent intent = new Intent(this, RssArticleActivity.class);
            intent.putExtra("url", url);
            startActivityForResult(intent, 0);
        }
    }

    /** 
     * �{�^���N���b�N�̃C�x���g���X�i�[
     * @param   view    �N���b�N���ꂽ�{�^���I�u�W�F�N�g
     */
    public void onClick(View view) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //RSS�t�B�[�h�ꗗ��ʂ���I�����ꂽ�ARSS�t�B�[�h��ID���擾
        final String rssId = (String)bundle.get("id");
        //�폜�{�^���N���b�N��
        if (view == deleteBtn) {
            //RssFeedContents�e�[�u���ւ�Delete����Where��̍쐬
            StringBuffer whereRssFeedContents = new StringBuffer(RssFeeds.RssFeedContentColumns.CHANNEL_ID).append("=").append(rssId);

            //RSS�L���̍폜�������f�[�^�x�[�X�Ɏ��s
            RssFeeds.delete(RssFeeds.RssFeedContentColumns.CONTENT_URI, whereRssFeedContents.toString(), null);

            //RssFeeds�e�[�u����ID���폜����Where����쐬
            StringBuffer whereRssFeeds = new StringBuffer(RssFeeds.RssFeedColumns._ID).append("=").append(rssId);

            //RSS�t�B�[�h�̍폜�������f�[�^�x�[�X�Ɏ��s
            RssFeeds.delete(RssFeeds.RssFeedContentColumns.CONTENT_URI, whereRssFeeds.toString(), null);

            //��ʂ̖߂�l��ݒ�
            setResult(RESULT_OK);

            //��ʏI��
            back();
        } else if (view == reGetBtn) {
            //RSS�t�B�[�h�ꗗ��ʂőI�����ꂽRSS�t�B�[�h��URL���擾
            final String url = (String)bundle.get("url");

            //Contents���폜
            StringBuffer whereRssFeedContents = new StringBuffer(RssFeeds.RssFeedContentColumns.CHANNEL_ID).append("=").append(rssId);

            //RssFeedContents�e�[�u�����I�����ꂽRSS�t�B�[�h��ID���폜
            RssFeeds.delete(RssFeeds.RssFeedContentColumns.CONTENT_URI, whereRssFeedContents.toString(), null);

            final Handler handler = new Handler();
            //�I�����ꂽRSS�t�B�[�h��URL����ēxRSS�t�B�[�h�̒l���擾���f�[�^�x�[�X�֊i�[
            new Thread(new Runnable() {
                public void run() {
                    // RssContents�p�[�X����
                    RssParser.parseRssContents(url, rssId);
                        handler.post(new Runnable() {
                            
                            public void run() {
                                //�f�[�^�x�[�X���L���ꗗ�̒l���擾���A�L���ꗗ��ListView���X�V
                                ToastUtil.showToastLong(RssArticleListActivity.this, "RSS�̍Ď擾�ɐ������܂����B");
                                getArrayAdapter();
                            }
                        }); 
                }
            }).start();

        }
    }

    /**
     * �L�[�������ꂽ���̏���
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        setResult(RESULT_OK);
        return super.dispatchKeyEvent(event);
    }

    private class Article {
        private String title;
        private String thumnail;
        private String date;

        public void setTitle(String title) {
            this.title = title;
        }
        public void setThumnail(String thumnail) {
            this.thumnail = thumnail;
        }
        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }
        public String getThumnail() {
            return thumnail;
        }
        public String getDate() {
            return date;
        }
    }

    private static class ViewHolder {
        ImageView imgThum;
        TextView txtTitle;
        TextView txtDate;
        ThumbnailTask task;
    }

    private class AricleListAdapter extends ArrayAdapter<Article> {

        private LayoutInflater mInflater;

        public AricleListAdapter(Context context, int textViewResourceId, List<Article> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Article article = (Article)getItem(position);
            ViewHolder holder = new ViewHolder();

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.rss_article_list_layout, null);

                holder.imgThum = (ImageView)convertView.findViewById(R.id.imgThumbnail);
                holder.txtDate = (TextView)convertView.findViewById(R.id.txt_date);
                holder.txtTitle = (TextView)convertView.findViewById(R.id.txt_title);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            String imgUrl = article.getThumnail();
            imageLoader.loadImage(imgUrl, holder.imgThum, BitmapFactory.decodeResource(getResources(), R.drawable.no_image));
            holder.txtDate.setText(article.getDate());
            holder.txtTitle.setText(article.getTitle());
            LogUtil.debug(this, "article.getDate():" + article.getDate());
            LogUtil.debug(this, "article.getTitle():" + article.getTitle());
            LogUtil.debug(this, "imgPath:" + imgUrl);

            return convertView;
        }
    }
    public static void executeTask(ViewHolder holder, String imgUrl) {
        holder.task = new ThumbnailTask(holder.imgThum);
        holder.task.execute(imgUrl);
    }
}
