package com.makotu.rss.reader.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoader {

    public static final int IO_BUFFER_SIZE_BYTES = 1 * 1024; // 1KB

    private static final int DEFAULT_MAX_THUMBNAIL_SIZE = 100 * 1024; // 100KB
    private static final int DEFAULT_MAX_IMAGE_HEIGHT = 100;
    private static final int DEFAULT_MAX_IMAGE_WIDTH = 100;

    private static final int DEFAULT_HTTP_CACHE_SIZE = 5 * 1024 * 1024;
    private static final String DEFAULT_HTTP_CACHE_DIR = "http";

    public static class ImageFetcherParams {
        public int mImageWidth = DEFAULT_MAX_IMAGE_WIDTH;
        public int mImageHeight = DEFAULT_MAX_IMAGE_HEIGHT;
        public int mMaxThumbnailBytes = DEFAULT_MAX_THUMBNAIL_SIZE;
        public int mHttpChacheSize = DEFAULT_HTTP_CACHE_SIZE;
        public String mHttpChacheDir = DEFAULT_HTTP_CACHE_DIR;
    }

    private Context mContext;
    private ImageFetcherParams mFetcherParams;
    private ImageCache mImageCache;

    public ImageLoader(Context context, ImageFetcherParams params) {
        mContext = context;
        mFetcherParams = params;
    }

    public ImageLoader(Context context) {
        mContext = context;
        mFetcherParams = new ImageFetcherParams();
    }

    public void setImageCache(ImageCache cacheCallback) {
        mImageCache = cacheCallback;
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }

    public void loadImage(String url, ImageView imageView, int reqWidth, int reqHeight) {
        mFetcherParams.mImageHeight = reqHeight;
        mFetcherParams.mImageWidth = reqWidth;
        //loadImage(url, imageView);
    }

    public void loadImage(String url, ImageView imageView, Bitmap loadingBitmap) {
        Bitmap bitmap = null;

        // キャッシュにあるかチェック
        if (mImageCache != null && url != null) {
            bitmap = mImageCache.getBitmapFromMemCache(url);
        }

        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(url, imageView) && url != null) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), loadingBitmap, task);
            if (imageView != null) {
                imageView.setImageDrawable(asyncDrawable);
            }
            task.execute(url);
        } else {
            imageView.setImageBitmap(loadingBitmap);
        }
    }


    private boolean cancelPotentialWork(Object data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                // 以前のタスクをキャンセル
                bitmapWorkerTask.cancel(true);
            } else {
                // 同じタスクがすでに走っているので、このタスクは実行しない
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable)drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<ImageLoader.BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private Object data;
        private final WeakReference<ImageView> mImageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            mImageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;

            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null) {
                bitmap = mImageCache.getBitmapFromMemCache(dataString);
            }

            if (bitmap == null) {
                bitmap = processBitmap(params[0]);
            }

            if (bitmap != null && mImageCache != null) {
                mImageCache.addBitmapToCache(dataString, bitmap);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (bitmap != null && imageView != null) {
                setImageBitmap(imageView, bitmap);
            }
        }

        private ImageView getAttachedImageView() {
            final ImageView imageView = mImageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            
            if (this == bitmapWorkerTask && imageView != null) {
                return imageView;
            }

            return null;
        }
    }

    private Bitmap processBitmap(String url) {
        final Bitmap bitmap = decodeBitmapFromStream(url, mFetcherParams.mImageWidth, mFetcherParams.mImageHeight);
        return bitmap;
    }

    private void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private static Bitmap decodeBitmapFromStream(String url, int reqWidth, int reqHeight) {
        Bitmap resBitmap = null;

        //引数に記述されたURLに対してGET METHODでのリクエストを送信する
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse res = null;
        InputStream inStream = null;
        try {
            res = client.execute(httpGet);
            if (res != null) {
                if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    HttpEntity entity = res.getEntity();
                    BufferedHttpEntity bufEntity = new BufferedHttpEntity(entity);
                    inStream = bufEntity.getContent();
                    // inJustDecodeBounds=true で画像のサイズをチェック
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inStream, null, options);

                    //一度読み込んだので、クローズする
                    inStream.close();

                    //メモリに読み込むサイズを計算
                    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

                    inStream = bufEntity.getContent();
                    // inSampleSizeをセットしてデコード
                    options.inJustDecodeBounds = false;
                    resBitmap  = BitmapFactory.decodeStream(inStream, null, options);
                }
            }
        } catch (IllegalStateException e) {
            LogUtil.error(ImageLoader.class, e.toString());
        } catch (UnknownHostException e) {
            LogUtil.error(ImageLoader.class, e.toString());
        } catch (ClientProtocolException e) {
            LogUtil.error(ImageLoader.class, e.toString());
        } catch(ParseException e) {
            LogUtil.error(ImageLoader.class, e.toString());
        } catch (IOException e) {
            LogUtil.error(ImageLoader.class, e.toString());
        } catch (Exception e) {
           LogUtil.error(ImageLoader.class, e.toString());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    LogUtil.error(ImageLoader.class, "InputStream Close Error");
                }
            }
            //クライアントを終了させる
            client.getConnectionManager().shutdown();
        }
        return resBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // 画像の元サイズ
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
}
