package com.makotu.rss.reader.asynctask;

import org.apache.http.HttpStatus;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.makotu.rss.reader.http.HttpAccess;
import com.makotu.rss.reader.util.LogUtil;

public class ThumbnailTask extends AsyncTask<String, Void, Integer> {

    private Bitmap mBitmap = null;
    private ImageView mImage = null;
    private String indentifyTag = null;

    public ThumbnailTask(ImageView mImage) {
        super();
        this.mImage = mImage;
        this.indentifyTag = mImage.getTag().toString();
    }

    @Override
    protected Integer doInBackground(String... params) {
        String url = params[0];
        int iRet = 0;
        if (url != null && url.length() > 0) {
            HttpAccess svr = new HttpAccess();
            iRet = svr.requestImage(url);
            if (iRet == HttpStatus.SC_OK) {
                mBitmap = svr.getResBitmap();
                if (mBitmap != null) {
                    //ImageCache.setImage(params[0], mBitmap);
                }
            }
        }
        return Integer.valueOf(iRet);
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == HttpStatus.SC_OK) {
            if (indentifyTag != null && indentifyTag.equals(mImage.getTag())) {
                LogUtil.debug(ThumbnailTask.class, "identifyTag=" +indentifyTag);
                LogUtil.debug(ThumbnailTask.class, "mImage.getTag()=" +mImage.getTag());
                mImage.setImageBitmap(mBitmap);
            }
        }
    }
}
