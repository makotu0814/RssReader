package com.makotu.rss.reader.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;

import com.makotu.rss.reader.consts.Consts;
import com.makotu.rss.reader.provider.RssFeeds;
import com.makotu.rss.reader.util.LogUtil;

/**
 * RSSフィードのURLを取得しDOMオブジェクトを解析し
 * データベースへデータを保存する
 * @author Makoto
 *
 */
public class RssParser {

    public static Document fetch(String url) {

        URL u = null;
        Document doc = null;
        try {
            //取得したRSSフィードのURLを指定
            u = new URL(url);
            //ドキュメントビルダーの生成
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            //URLからドキュメントオブジェクトを取得
            doc = db.parse(u.openStream());
        } catch (MalformedURLException ex) {
            LogUtil.error(RssParser.class, ex.getMessage());
        } catch (ParserConfigurationException ex) {
            LogUtil.error(RssParser.class, ex.getMessage());
        } catch (SAXException ex) {
            LogUtil.error(RssParser.class, ex.getMessage());
        } catch (IOException ex) {
            LogUtil.error(RssParser.class, ex.getMessage());
        }
        return doc;
    }

    /**
     * Rssフィードの値をRssFeedsテーブルへ格納
     * @param url
     * @param updHour
     * @return
     */
    public static int parseRssFeed(String url, int updHour) {
        //IDの初期値
        int ret = -1;
        //RssフィードのURLからドキュメントオブジェクトを取得
        Document doc = fetch(url);
        if (doc == null) {
            LogUtil.warn(RssParser.class, "ドキュメントオブジェクトを取得できませんでした");
            return ret;
        }

        //ドキュメントの値をデータベースに格納するためにContentValuesオブジェクト生成
        ContentValues values = new ContentValues();

        //RssフィードのURLを格納
        values.put(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK, url);

        //更新間隔をミリ秒に変換して、格納
        values.put(RssFeeds.RssFeedColumns.UPDATE_CYCLE, updHour * 60 * 60 * 1000);

        //ドキュメントからRSS2.0ヘッダーのルートノードを取得
        NodeList nlHead = doc.getElementsByTagName("channel");

        for (int i = 0; i < nlHead.getLength(); i++) {
            //ノードを取得
            Node item = nlHead.item(i);
            //channeノードが子ノードを取得
            NodeList childs = item.getChildNodes();
            int childLen = childs.getLength();
            for (int childIndex = 0; childIndex < childLen; childIndex++) {
                //ノードを取得
                Node node = childs.item(childIndex);
                //ノード名を取得
                String nodeName = node.getNodeName();
                //ノードの値を取得
                String nodeValue = "";
                Node childNode = (Node)node.getFirstChild();
                if (checkNodeType(childNode, Node.TEXT_NODE)) {
                    nodeValue = childNode.getNodeValue();
                } else {
                    LogUtil.debug(RssParser.class, "ノード:" + nodeName + "に値がありません");
                }
                
                //ノードの名称がタイトル、リンク、解説、言語の場合は値をContenValueに設定
                if (nodeName.equals("title")) {
                    values.put(RssFeeds.RssFeedColumns.CHANNEL_NAME, nodeValue);
                } else if (nodeName.equals("link")) {
                    values.put(RssFeeds.RssFeedColumns.CHANNEL_LINK, nodeValue);
                } else if (nodeName.equals("description")) {
                    values.put(RssFeeds.RssFeedColumns.CHANNEL_DESC, nodeValue);
                } else if (nodeName.equals("language")) {
                    values.put(RssFeeds.RssFeedColumns.CHANNEL_LANG, nodeValue);
                }
            }
        }
        //現在時間を取得し格納
        long now = System.currentTimeMillis();
        values.put(RssFeeds.RssFeedColumns.LAST_UPDATE, now);

        //RssFeedsへデータを登録する条件はRSSフィードのURLが登録されていないこと
        StringBuffer whereRssFeeds = new StringBuffer(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK).append(" like '%").append(url).append("%'");

        Uri uri = RssFeeds.insertIfNotExists(RssFeeds.RssFeedColumns.CONTENT_URI, whereRssFeeds.toString(), null, values);

        //更新したレコードIDを取得
        ret = (int)ContentUris.parseId(uri);

        return ret;
    }

    public static void parseRssContents(String url, String id) {
        //RSSフィードのURLからドキュメントオブジェクトを取得
        Document doc = fetch(url);
        if (doc == null) {
            return;
        }
        //RSSフィードの記事であるitemタグのノードを取得
        NodeList nl = doc.getElementsByTagName("item");
        //itemタグの要素数を取得
        int nlen = nl.getLength();
        for (int i = 0; i < nlen; i++) {
            Node item = nl.item(i);
            //itemタグの子ノードを取得
            NodeList childs = item.getChildNodes();
            int childLen = childs.getLength();
            ContentValues values = new ContentValues();
            StringBuffer selection = new StringBuffer();
            selection.append(RssFeeds.RssFeedContentColumns.CHANNEL_ID);
            selection.append("=");
            selection.append(id);

            values.put(RssFeeds.RssFeedContentColumns.CHANNEL_ID, id);
            
            //RSS記事数ループ
            for (int childIndex = 0; childIndex < childLen; childIndex++) {
                Node node = childs.item(childIndex);
                //ノード名を取得
                String articleNodeName = node.getNodeName();
                LogUtil.debug(RssParser.class, "articleNodeName=" + articleNodeName);
                //ノードの値を取得
                String articleNodeValue = "";
                Node childNode = (Node)node.getFirstChild();
                //LogUtil.debug(RssParser.class, "childNodeName=" + childNode.getNodeName());
                if (checkNodeType(childNode, Node.TEXT_NODE)) {
                    //TEXTノードの場合
                    articleNodeValue = childNode.getNodeValue();
                } else if (checkNodeType(childNode, Node.CDATA_SECTION_NODE)) {
                    //CDATAノードの場合
                    articleNodeValue = childNode.getNodeValue();
                    try {
                        //String imageUrl = getNewsImageUrl(articleNodeValue);
                        Pattern p = Pattern.compile("<\\s*img.*src\\s*=\\s*([\\\"'])?([^ \\\"']*)[^>]*>");
                        Matcher m = p.matcher(articleNodeValue);
                        if (m.find()) {
                            String imageUrl = m.group(2);//ここにURLが入る
                            LogUtil.debug(RssParser.class, "imageUrl:" + imageUrl);
                            values.put(RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL, imageUrl);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        LogUtil.error(RssParser.class, e.toString());
                    }
                    LogUtil.debug(RssParser.class, "CDATA SECTION:" + articleNodeValue);
                } else {
                    LogUtil.debug(RssParser.class, "ノード:" + articleNodeName + "に値がありません");
                }
                //リンク、タイトル、解説の場合の値を取得
                if (articleNodeName.equalsIgnoreCase("link")) {
                    values.put(RssFeeds.RssFeedContentColumns.ITEM_LINK, articleNodeValue);

                    //RSS記事を更新するためのWhere句を作成
                    selection.append(" AND ")
                    .append(RssFeeds.RssFeedContentColumns.ITEM_LINK)
                    .append("=")
                    .append("'")
                    .append(articleNodeValue)
                    .append("' ");
                } else if (articleNodeName.equalsIgnoreCase("title")) {
                    values.put(RssFeeds.RssFeedContentColumns.ITEM_TITLE, articleNodeValue);
                } else if (articleNodeName.equalsIgnoreCase("description")) {
                    values.put(RssFeeds.RssFeedContentColumns.ITEM_DESCRIPTION, articleNodeValue);
                } else if (articleNodeName.equalsIgnoreCase("media:thumbnail")) {
                    articleNodeValue = node.getAttributes().getNamedItem("url").getNodeValue();
                    values.put(RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL, articleNodeValue);
                } else if (articleNodeName.equalsIgnoreCase("pubDate")) {
                    insertNewsDate(values, articleNodeValue, Consts.PUBDATEPATTERN_STRING);
                } else if (articleNodeName.equalsIgnoreCase("dc:date")) {
                    insertNewsDate(values, articleNodeValue, Consts.DCDATEPATTERN_STRING);
                }
            }
            RssFeeds.insertIfNotExists(RssFeeds.RssFeedContentColumns.CONTENT_URI, selection.toString(), null, values);
        }
        //現在時間を取得
        ContentValues values = new ContentValues();
        values.put(RssFeeds.RssFeedColumns.LAST_UPDATE, System.currentTimeMillis());

        Uri uri = ContentUris.withAppendedId(RssFeeds.RssFeedColumns.CONTENT_URI, Long.parseLong(id));
        RssFeeds.update(uri, values, null, null);
    }

    /**
     * ニュースの公開日時をContentValuesに挿入する
     * @param values
     * @param articleNodeValue
     * @param datePattern
     */
    private static void insertNewsDate(ContentValues values,
            String articleNodeValue, String datePattern) {
        //RSSの記事の更新時間の場合
        Date date = getNewsDate(articleNodeValue, datePattern);
        String pubDate = null;
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            pubDate = sdf.format(date);
        } else {
            pubDate = "";
        }
        values.put(RssFeeds.RssFeedContentColumns.PUB_DATE, pubDate);
    }

    /**
     * ニュースの公開日時を取得する
     * @param articleNodeValue
     * @return ニュースの公開日時(yyyy/MM/dd HH:mm:ss)
     * @throws ParseException
     */
    private static Date getNewsDate(String articleNodeValue, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
        Date date = null;
        try {
            date = (Date) sdf.parse(articleNodeValue);
        } catch (ParseException e) {
            try {
                sdf.applyPattern(pattern.replace("Z", "'Z'"));
                date = sdf.parse(articleNodeValue);
            } catch (ParseException e2) {
                LogUtil.error(RssParser.class, e2.toString());
            }
        }
        return date;
    }

    /**
     * Nodeのタイプを判別する
     * @param childNode
     * @param nodeType
     * @return childNodeタイプとnodeTypeが等しければtrue、そうでなければfalseを返す
     */
    private static boolean checkNodeType(Node childNode, int nodeType) {
        return childNode != null && childNode.getNodeType() == nodeType;
    }

    /**
     * ニュース画像のURLを返す
     * @param nodeValue ノードの値
     * @return ニュース画像のURLを返す
     */
    private static String getNewsImageUrl(String nodeValue) {
        int start = nodeValue.indexOf("src=\"") + 5;
        int end = nodeValue.indexOf("\"", start);
        return nodeValue.substring(start, end);
    }

    /**
     * URL先の画像ファイルの拡張子をチェック
     * @param url　ニュース画像URL
     * @return jpeg,jpg,png,gifのいずれかであればtrueを返す
     */
    private static boolean isImageUrl(String url) {
        return url.endsWith("jpeg") || url.endsWith("jpg") || url.endsWith("png") || url.endsWith("gif");
    }
}
