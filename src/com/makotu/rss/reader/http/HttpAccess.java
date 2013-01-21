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

        //�����ɋL�q���ꂽURL�ɑ΂���GET METHOD�ł̃��N�G�X�g�𑗐M����
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse res = null;
        HttpEntity entity = null;
        try {
            res = client.execute(httpGet);
            if (res != null) {
                iRet = res.getStatusLine().getStatusCode();
                if (iRet == HttpStatus.SC_OK) {
                    //���N�G�X�g����
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
            //HttpEntity�̃��\�[�X���������
            try {
                if (entity != null) {
                    entity.consumeContent();
                }
            } catch (IOException e) {
                    LogUtil.error(getClass(), e.getMessage());
            }
            //�N���C�A���g���I��������
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

        //�����ɋL�q���ꂽURL�ɑ΂���GET METHOD�ł̃��N�G�X�g�𑗐M����
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse res = null;
        try {
            res = client.execute(httpGet);
            if (res != null) {
                iRet = res.getStatusLine().getStatusCode();
                if (iRet == HttpStatus.SC_OK) {
                    //���N�G�X�g����
                    Bitmap bmp = BitmapFactory.decodeStream(res.getEntity().getContent());
                    if (bmp != null) {
                        // �摜�ւ̃��N�G�X�g��O��ɂ���̂ŁA���X�|���X�𒼐�Bitmap�ɕϊ�����
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
            //�N���C�A���g���I��������
            client.getConnectionManager().shutdown();
        }
        return iRet;
    }

    public Bitmap getResBitmap() {
        return resBitmap;
    }
}
