package me.everything.jerry.receivers;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

import me.everything.jerry.R;

/**
 * Created by nitsan on 7/7/15.
 */
public class JerryViewHolder {

    private WeakReference<View> mViewRef;

    private static JerryViewHolder sInstance;

    private JerryViewHolder() {
    }

    public static JerryViewHolder getInstance() {
        if (sInstance == null) {
            sInstance = new JerryViewHolder();
        }
        return sInstance;
    }

    public void addView(Context context, String number) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(

                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSLUCENT;

        params.gravity = Gravity.TOP;

        View view = LayoutInflater.from(context).inflate(R.layout.test_call_overlay_layout, null);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewParent parent = v.getParent();
                if (parent == null) {
                    return false;
                }
                if (parent instanceof View) {
                    ((View) parent).onTouchEvent(event);
                    return false;
                }
                return false;
            }
        });
        mViewRef = new WeakReference<>(view);
        wm.addView(view, params);
    }

    public void removeView(Context context) {
        if (null == mViewRef) {
            return;
        }
        View view = mViewRef.get();
        if (null == view) {
            return;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (null == wm) {
            return;
        }
        wm.removeView(view);
    }
}