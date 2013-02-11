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
 * RSS�t�B�[�h��URL���擾��DOM�I�u�W�F�N�g����͂�
 * �f�[�^�x�[�X�փf�[�^��ۑ�����
 * @author Makoto
 *
 */
public class RssParser {

    public static Document fetch(String url) {

        URL u = null;
        Document doc = null;
        try {
            //�擾����RSS�t�B�[�h��URL���w��
            u = new URL(url);
            //�h�L�������g�r���_�[�̐���
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            //URL����h�L�������g�I�u�W�F�N�g���擾
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
     * Rss�t�B�[�h�̒l��RssFeeds�e�[�u���֊i�[
     * @param url
     * @param updHour
     * @return
     */
    public static int parseRssFeed(String url, int updHour) {
        //ID�̏����l
        int ret = -1;
        //Rss�t�B�[�h��URL����h�L�������g�I�u�W�F�N�g���擾
        Document doc = fetch(url);
        if (doc == null) {
            LogUtil.warn(RssParser.class, "�h�L�������g�I�u�W�F�N�g���擾�ł��܂���ł���");
            return ret;
        }

        //�h�L�������g�̒l���f�[�^�x�[�X�Ɋi�[���邽�߂�ContentValues�I�u�W�F�N�g����
        ContentValues values = new ContentValues();

        //Rss�t�B�[�h��URL���i�[
        values.put(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK, url);

        //�X�V�Ԋu���~���b�ɕϊ����āA�i�[
        values.put(RssFeeds.RssFeedColumns.UPDATE_CYCLE, updHour * 60 * 60 * 1000);

        //�h�L�������g����RSS2.0�w�b�_�[�̃��[�g�m�[�h���擾
        NodeList nlHead = doc.getElementsByTagName("channel");

        for (int i = 0; i < nlHead.getLength(); i++) {
            //�m�[�h���擾
            Node item = nlHead.item(i);
            //channe�m�[�h���q�m�[�h���擾
            NodeList childs = item.getChildNodes();
            int childLen = childs.getLength();
            for (int childIndex = 0; childIndex < childLen; childIndex++) {
                //�m�[�h���擾
                Node node = childs.item(childIndex);
                //�m�[�h�����擾
                String nodeName = node.getNodeName();
                //�m�[�h�̒l���擾
                String nodeValue = "";
                Node childNode = (Node)node.getFirstChild();
                if (checkNodeType(childNode, Node.TEXT_NODE)) {
                    nodeValue = childNode.getNodeValue();
                } else {
                    LogUtil.debug(RssParser.class, "�m�[�h:" + nodeName + "�ɒl������܂���");
                }
                
                //�m�[�h�̖��̂��^�C�g���A�����N�A����A����̏ꍇ�͒l��ContenValue�ɐݒ�
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
        //���ݎ��Ԃ��擾���i�[
        long now = System.currentTimeMillis();
        values.put(RssFeeds.RssFeedColumns.LAST_UPDATE, now);

        //RssFeeds�փf�[�^��o�^���������RSS�t�B�[�h��URL���o�^����Ă��Ȃ�����
        StringBuffer whereRssFeeds = new StringBuffer(RssFeeds.RssFeedColumns.CHANNEL_FEEDS_LINK).append(" like '%").append(url).append("%'");

        Uri uri = RssFeeds.insertIfNotExists(RssFeeds.RssFeedColumns.CONTENT_URI, whereRssFeeds.toString(), null, values);

        //�X�V�������R�[�hID���擾
        ret = (int)ContentUris.parseId(uri);

        return ret;
    }

    public static void parseRssContents(String url, String id) {
        //RSS�t�B�[�h��URL����h�L�������g�I�u�W�F�N�g���擾
        Document doc = fetch(url);
        if (doc == null) {
            return;
        }
        //RSS�t�B�[�h�̋L���ł���item�^�O�̃m�[�h���擾
        NodeList nl = doc.getElementsByTagName("item");
        //item�^�O�̗v�f�����擾
        int nlen = nl.getLength();
        for (int i = 0; i < nlen; i++) {
            Node item = nl.item(i);
            //item�^�O�̎q�m�[�h���擾
            NodeList childs = item.getChildNodes();
            int childLen = childs.getLength();
            ContentValues values = new ContentValues();
            StringBuffer selection = new StringBuffer();
            selection.append(RssFeeds.RssFeedContentColumns.CHANNEL_ID);
            selection.append("=");
            selection.append(id);

            values.put(RssFeeds.RssFeedContentColumns.CHANNEL_ID, id);
            
            //RSS�L�������[�v
            for (int childIndex = 0; childIndex < childLen; childIndex++) {
                Node node = childs.item(childIndex);
                //�m�[�h�����擾
                String articleNodeName = node.getNodeName();
                LogUtil.debug(RssParser.class, "articleNodeName=" + articleNodeName);
                //�m�[�h�̒l���擾
                String articleNodeValue = "";
                Node childNode = (Node)node.getFirstChild();
                //LogUtil.debug(RssParser.class, "childNodeName=" + childNode.getNodeName());
                if (checkNodeType(childNode, Node.TEXT_NODE)) {
                    //TEXT�m�[�h�̏ꍇ
                    articleNodeValue = childNode.getNodeValue();
                } else if (checkNodeType(childNode, Node.CDATA_SECTION_NODE)) {
                    //CDATA�m�[�h�̏ꍇ
                    articleNodeValue = childNode.getNodeValue();
                    try {
                        //String imageUrl = getNewsImageUrl(articleNodeValue);
                        Pattern p = Pattern.compile("<\\s*img.*src\\s*=\\s*([\\\"'])?([^ \\\"']*)[^>]*>");
                        Matcher m = p.matcher(articleNodeValue);
                        if (m.find()) {
                            String imageUrl = m.group(2);//������URL������
                            LogUtil.debug(RssParser.class, "imageUrl:" + imageUrl);
                            values.put(RssFeeds.RssFeedContentColumns.ITEM_THUMNAIL, imageUrl);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        LogUtil.error(RssParser.class, e.toString());
                    }
                    LogUtil.debug(RssParser.class, "CDATA SECTION:" + articleNodeValue);
                } else {
                    LogUtil.debug(RssParser.class, "�m�[�h:" + articleNodeName + "�ɒl������܂���");
                }
                //�����N�A�^�C�g���A����̏ꍇ�̒l���擾
                if (articleNodeName.equalsIgnoreCase("link")) {
                    values.put(RssFeeds.RssFeedContentColumns.ITEM_LINK, articleNodeValue);

                    //RSS�L�����X�V���邽�߂�Where����쐬
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
        //���ݎ��Ԃ��擾
        ContentValues values = new ContentValues();
        values.put(RssFeeds.RssFeedColumns.LAST_UPDATE, System.currentTimeMillis());

        Uri uri = ContentUris.withAppendedId(RssFeeds.RssFeedColumns.CONTENT_URI, Long.parseLong(id));
        RssFeeds.update(uri, values, null, null);
    }

    /**
     * �j���[�X�̌��J������ContentValues�ɑ}������
     * @param values
     * @param articleNodeValue
     * @param datePattern
     */
    private static void insertNewsDate(ContentValues values,
            String articleNodeValue, String datePattern) {
        //RSS�̋L���̍X�V���Ԃ̏ꍇ
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
     * �j���[�X�̌��J�������擾����
     * @param articleNodeValue
     * @return �j���[�X�̌��J����(yyyy/MM/dd HH:mm:ss)
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
     * Node�̃^�C�v�𔻕ʂ���
     * @param childNode
     * @param nodeType
     * @return childNode�^�C�v��nodeType�����������true�A�����łȂ����false��Ԃ�
     */
    private static boolean checkNodeType(Node childNode, int nodeType) {
        return childNode != null && childNode.getNodeType() == nodeType;
    }

    /**
     * �j���[�X�摜��URL��Ԃ�
     * @param nodeValue �m�[�h�̒l
     * @return �j���[�X�摜��URL��Ԃ�
     */
    private static String getNewsImageUrl(String nodeValue) {
        int start = nodeValue.indexOf("src=\"") + 5;
        int end = nodeValue.indexOf("\"", start);
        return nodeValue.substring(start, end);
    }

    /**
     * URL��̉摜�t�@�C���̊g���q���`�F�b�N
     * @param url�@�j���[�X�摜URL
     * @return jpeg,jpg,png,gif�̂����ꂩ�ł����true��Ԃ�
     */
    private static boolean isImageUrl(String url) {
        return url.endsWith("jpeg") || url.endsWith("jpg") || url.endsWith("png") || url.endsWith("gif");
    }
}
