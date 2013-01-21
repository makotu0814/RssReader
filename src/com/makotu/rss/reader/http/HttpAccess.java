package com.makotu.rss.reader.http;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.makotu.rss.reader.util.LogUtil;

public class HttpAccess {

    private static final int UNKOWN_HOST = -1;

    private Bitmap resBitmap;
    private String resText;

    public int requestText(String url) {
        int iRet = UNKOWN_HOST;
        resText = "";

        LogUtil.debug(getClass(), url);

        //引数に記述されたURLに対してGET METHODでのリクエストを送信する
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse res = null;
        HttpEntity entity = null;
        try {
            res = client.execute(httpGet);
            if (res != null) {
                iRet = res.getStatusLine().getStatusCode();
                if (iRet == HttpStatus.SC_OK) {
                    //リクエスト成功
                    entity = res.getEntity();
                    resText = EntityUtils.toString(entity);
                }
            }
        } catch (UnknownHostException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch (ClientProtocolException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch(ParseException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch (IOException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch (Exception e) {
           LogUtil.error(getClass(), e.getMessage());
        } finally {
            //HttpEntityのリソースを解放する
            try {
                if (entity != null) {
                    entity.consumeContent();
                }
            } catch (IOException e) {
                    LogUtil.error(getClass(), e.getMessage());
            }
            //クライアントを終了させる
            client.getConnectionManager().shutdown();
        }
        return iRet;
    }

    public String getResText() {
        return resText;
    }

    public int requestImage(String url) {
        int iRet = UNKOWN_HOST;
        resBitmap = null;

        //引数に記述されたURLに対してGET METHODでのリクエストを送信する
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse res = null;
        try {
            res = client.execute(httpGet);
            if (res != null) {
                iRet = res.getStatusLine().getStatusCode();
                if (iRet == HttpStatus.SC_OK) {
                    //リクエスト成功
                    Bitmap bmp = BitmapFactory.decodeStream(res.getEntity().getContent());
                    if (bmp != null) {
                        // 画像へのリクエストを前提にするので、レスポンスを直接Bitmapに変換する
                        resBitmap = bmp;
                    }
                }
            }
        } catch (IllegalStateException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch (UnknownHostException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch (ClientProtocolException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch(ParseException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch (IOException e) {
            LogUtil.error(getClass(), e.getMessage());
        } catch (Exception e) {
           LogUtil.error(getClass(), e.getMessage());
        } finally {
            //クライアントを終了させる
            client.getConnectionManager().shutdown();
        }
        return iRet;
    }

    public Bitmap getResBitmap() {
        return resBitmap;
    }
}
