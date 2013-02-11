package com.makotu.rss.reader.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.makotu.rss.reader.R;
import com.makotu.rss.reader.service.RssReaderService;
import com.makotu.rss.reader.util.DialogUtil;
import com.makotu.rss.reader.util.LogUtil;

public class RssBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.debug(this, "onCreate");
        super.onCreate(savedInstanceState);

        //ActionBarを有効にするための処理
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setTitle("");

        //RSS取得Serviceの起動
        Intent intent = new Intent(this, RssReaderService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        LogUtil.debug(this, "onDestroy");	
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        LogUtil.debug(this, "onPause");
        super.onPause();

        if (isFinishing() && this instanceof RssBaseActivity) {
            ViewGroup rootView = (ViewGroup)getWindow().getDecorView().getRootView();
            cleanupView(rootView);
            rootView.removeAllViews();
        }
	}

    @Override
    protected void onResume() {
        LogUtil.debug(this, "onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        LogUtil.debug(this, "onStop");
        super.onStop();	
    }

    /**
     * 画面に表示されているViewのクリアする
     * @param view
     */
    private void cleanupView(View view) {
        if (view instanceof ImageButton) {
            ImageButton imgBtn = (ImageButton)view;
            imgBtn.setImageBitmap(null);
            imgBtn.removeCallbacks(null);
            imgBtn.setImageDrawable(null);
            
        } else if (view instanceof ImageView) {
            ImageView image = (ImageView)view;
            image.setImageBitmap(null);
            image.removeCallbacks(null);
            image.setImageDrawable(null);
        } else if (view instanceof GridView) {
            GridView grid = (GridView)view;
            grid.setAdapter(null);
        } else if (view instanceof ListView) {
            ListView list = (ListView)view;
            list.setAdapter(null);
        }

        view.setBackgroundDrawable(null);

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup)view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                cleanupView(viewGroup.getChildAt(i));
            }
        }
        
        view = null;
    }


    public void back() {
        if (isTaskRoot()) {
            DialogUtil.showYesNoDialog(this, R.string.confirm_end_app, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                back();
                break;
            default:
                break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
