package com.makotu.rss.reader.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class ImageCacheParams {
    // Default memory cache size
    protected static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 2; // 2MB
    public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
    protected static final int DEFAULT_MEM_CACHE_DIVIDER = 8; // memory
    public int memoryClass = 0;

    public ImageCacheParams(Context context) {
        final ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        memoryClass = activityManager.getMemoryClass();
        memCacheSize = memoryClass / DEFAULT_MEM_CACHE_DIVIDER * 1024 * 1024;
    }
}
