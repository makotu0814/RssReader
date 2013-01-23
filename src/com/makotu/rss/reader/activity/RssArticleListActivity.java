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

     /** 記事に対応するURLを格納するマップオブジェクト*/
    private HashMap<Long, String> urlMap = new HashMap<Long, String>();

    /** 記事の一覧を表示するListView*/
    private ListView articleListView;

    /** RSSフィードの削除ボタン*/
    private Button deleteBtn;

    /** RSSフィードの再取得ボタン*/
    private Button reGetBtn;
    
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //画面レイアウト 縦方向に設定
        LinearLayout displayLayout = new LinearLayout(this);
        displayLayout.setOrientation(LinearLayout.VERTICAL);

        //ボタンレイアウト 横方向に設定
        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        setContentView(displayLayout);

        //RSSフィードの削除ボタン
        deleteBtn = new Button(this);
        deleteBtn.setText("RSSフィードを削除");
        deleteBtn.setOnClickListener(this);

        //RSSフィードの再取得ボタン
        reGetBtn = new Button(this);
        reGetBtn.setText("RSSフィードを再取得");
        reGetBtn.setOnClickListener(this);

        //ボタンレイアウトにボタンを追加
        btnLayout.addView(deleteBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));
        btnLayout.addView(reGetBtn, LayoutUtil.getLayoutParams(LayoutUtil.WC, LayoutUtil.WC));

        //記事一覧ListViewの生成
        articleListView = new ListView(this);
        articleListView.setOnItemClickListener(this);

        getArrayAdapter();

        //ボタンレイアウトと、記事一覧を画面レイアウトに追加
        displayLayout.addView(btnLayout);
        displayLayout.addView(articleListView);

        //ImageLoaderのインスタンス生成
        imageLoader = new ImageLoader(this);
        imageLoader.setImageCache(new ImageCache(this, new ImageCacheParams(this)));
    }

    /**
     * RSSフィードの一覧を取得しListViewを更新
     */
    private void getArrayAdapter() {
        int listCnt = 0;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //RSSフィード一覧画面より選択されたフィードのIDを取得
        String rssId = (String)extras.get("id");

        //記事を取得する際のWhere句の作成
        StringBuffer selection = new StringBuffer(RssFeeds.RssFeedContentColumns.CHANNEL_ID).append("=").append(rssId);

        //記事のカーソルをデータベースより取得する
        Cursor mRssCursor = RssFeeds.query(RssFeeds.RssFeedContentColumns.CONTENT_URI, RssFeeds.RSSCONTENT_PROJECTION_MAP.keySet().toArray(new String[0]), selection.toString(), null, RssFeeds.RssFeedContentColumns.DEFAUlT_SORT_ORDER);

        List<Article> adapterDataList = new LinkedList<Article>();
        //RSSフィードで取得した記事数カーソルをループ
        while (mRssCursor.moveToNext()) {
            Article article = new Article();

            //タイトルカラム位置を取得し、タイトルの値をデータベースより取得
            int itemThumCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL);
            String thumnail = mRssCursor.getString(itemThumCol);
            article.setThumnail(thumnail);

            //タイトルカラム位置を取得し、タイトルの値をデータベースより取得
            int itemTitleCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.ITEM_TITLE);
            String title = mRssCursor.getString(itemTitleCol);
            article.setTitle(title);

            //記事のURLカラム位置を取得し、記事のURLの値をデータベースより取得
            int itemLinkCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.ITEM_LINK);
            String link = mRssCursor.getString(itemLinkCol);

            //記事の更新日時カラム位置を取得し、記事の更新日時の値をデータベースより取得
            int pubDateCol = mRssCursor.getColumnIndex(RssFeeds.RssFeedContentColumns.PUB_DATE);
            String pubDate = mRssCursor.getString(pubDateCol);
            article.setDate(pubDate);

            //記事のタイトルと、更新日時を表示
            adapterDataList.add(article);

            //記事一覧の選択された位置と、記事のURLをマップオブジェクトに格納
            urlMap.put(new Long(listCnt), link);
            listCnt++;
        }
        for (Article article : adapterDataList) {
            LogUtil.debug(this, "url:" + article.thumnail);
        }
        //記事一覧をデータベースから取得した値で更新
        articleListView.setAdapter(new AricleListAdapter(this, 0, adapterDataList));
    }

    /** 
     * ListViewの記事クリック時のイベントリスナー
     * @param   parent    クリックされたリストビュー
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == articleListView) {
            String url = (String)urlMap.get(new Long(id));

            //記事の表示アクティビティの指定
            Intent intent = new Intent(this, RssArticleActivity.class);
            intent.putExtra("url", url);
            startActivityForResult(intent, 0);
        }
    }

    /** 
     * ボタンクリックのイベントリスナー
     * @param   view    クリックされたボタンオブジェクト
     */
    public void onClick(View view) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //RSSフィード一覧画面から選択された、RSSフィードのIDを取得
        final String rssId = (String)bundle.get("id");
        //削除ボタンクリック時
        if (view == deleteBtn) {
            //RssFeedContentsテーブルへのDelete文のWhere句の作成
            StringBuffer whereRssFeedContents = new StringBuffer(RssFeeds.RssFeedContentColumns.CHANNEL_ID).append("=").append(rssId);

            //RSS記事の削除処理をデータベースに実行
            RssFeeds.delete(RssFeeds.RssFeedContentColumns.CONTENT_URI, whereRssFeedContents.toString(), null);

            //RssFeedsテーブルのIDを削除するWhere句を作成
            StringBuffer whereRssFeeds = new StringBuffer(RssFeeds.RssFeedColumns._ID).append("=").append(rssId);

            //RSSフィードの削除処理をデータベースに実行
            RssFeeds.delete(RssFeeds.RssFeedContentColumns.CONTENT_URI, whereRssFeeds.toString(), null);

            //画面の戻り値を設定
            setResult(RESULT_OK);

            //画面終了
            back();
        } else if (view == reGetBtn) {
            //RSSフィード一覧画面で選択されたRSSフィードのURLを取得
            final String url = (String)bundle.get("url");

            //Contentsを削除
            StringBuffer whereRssFeedContents = new StringBuffer(RssFeeds.RssFeedContentColumns.CHANNEL_ID).append("=").append(rssId);

            //RssFeedContentsテーブルより選択されたRSSフィードのIDを削除
            RssFeeds.delete(RssFeeds.RssFeedContentColumns.CONTENT_URI, whereRssFeedContents.toString(), null);

            final Handler handler = new Handler();
            //選択されたRSSフィードのURLから再度RSSフィードの値を取得しデータベースへ格納
            new Thread(new Runnable() {
                public void run() {
                    // RssContentsパース処理
                    RssParser.parseRssContents(url, rssId);
                        handler.post(new Runnable() {
                            
                            public void run() {
                                //データベースより記事一覧の値を取得し、記事一覧のListViewを更新
                                ToastUtil.showToastLong(RssArticleListActivity.this, "RSSの再取得に成功しました。");
                                getArrayAdapter();
                            }
                        }); 
                }
            }).start();

        }
    }

    /**
     * キーが押された時の処理
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
