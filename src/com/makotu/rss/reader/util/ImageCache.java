package com.makotu.rss.reader.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageCache {
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageCache(Context context, ImageCacheParams cacheParams) {
        init(context, cacheParams);
    }

    public void init(Context context, ImageCacheParams cacheParams) {
        mMemoryCache = new LruCache<String, Bitmap>(cacheParams.memCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public Bitmap getBitmapFromMemCache(String data) {
        if (mMemoryCache != null) {
            final Bitmap memBitmap = mMemoryCache.get(data);
            if (memBitmap != null) {
                return memBitmap;
            }
        }
        return null;
    }

    public void addBitmapToCache(String data, Bitmap bitmap) {
        if (mMemoryCache != null && mMemoryCache.get(data) == null) {
            mMemoryCache.put(data, bitmap);
        }
    }
}
