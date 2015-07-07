package me.everything.jerry.receivers;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import me.everything.jerry.R;
import me.everything.jerry.db.Agenda;

/**
 * Created by nitsan on 7/7/15.
 */
public class JerryViewHolder {

    private static final String TAG = JerryViewHolder.class.getSimpleName();
    private WeakReference<View> mNonClickableViewRef;
    private WeakReference<View> mClickableViewRef;

    private static JerryViewHolder sInstance;
    private Agenda mAgenda;

    private JerryViewHolder() {
    }

    public static JerryViewHolder getInstance() {
        if (sInstance == null) {
            sInstance = new JerryViewHolder();
        }
        return sInstance;
    }

    private WindowManager.LayoutParams getParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        return params;
    }

    public void addClickableView(final Context context, Agenda agenda) {
        Log.d(TAG, "addClickableView");
        removeView(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.call_overlay_layout, null);
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
        final TextView textView = (TextView) view.findViewById(R.id.jerry_button);
        if (agenda == null) {
            agenda = mAgenda;
        } else {
            mAgenda = agenda;
        }
        SpannableString ss = new SpannableString(agenda.getContactName() + "\n");
        textView.setText(ss);
        ss = new SpannableString(agenda.getAgenda());
        ss.setSpan(new TextAppearanceSpan(context, R.style.remonder_text), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(ss);
        final View icon = view.findViewById(R.id.icon);
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int translate = textView.getWidth() - icon.getWidth() / 2;
                icon.setTranslationX(translate);
                textView.setTranslationX(translate);
                view.setVisibility(View.VISIBLE);
            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick icon");
                icon.animate().translationX(0).start();
                textView.animate().translationX(0).start();
            }
        });

        mClickableViewRef = new WeakReference<>(view);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = getParams();
        wm.addView(view, params);
    }

    public void removeView(Context context) {
        Log.d(TAG, "removeView");
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (null != mClickableViewRef && mClickableViewRef.get() != null) {
            wm.removeView(mClickableViewRef.get());
            mClickableViewRef = null;
        }
        if (null != mNonClickableViewRef && mNonClickableViewRef.get() != null) {
            wm.removeView(mNonClickableViewRef.get());
            mNonClickableViewRef = null;
        }
    }

    public void addNonClickableView(final Context context, Agenda agenda) {
        Log.d(TAG, "addNonClickableView; number: " + agenda.getContactNumber());
        mAgenda = agenda;
        removeView(context);
        View view = LayoutInflater.from(context).inflate(R.layout.agenda_overlay_layout, null);
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
        TextView agendaView = (TextView) view.findViewById(R.id.agenda_text);
        agendaView.setText(agenda.getContactName() + "\n" + agenda.getAgenda());
        mNonClickableViewRef = new WeakReference<>(view);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = getParams();
        wm.addView(view, params);
    }

    public void offhook() {
        if (mClickableViewRef != null && mClickableViewRef.get() != null) {
            return;
        }
        if (mNonClickableViewRef == null) {
            return;
        }
        View view = mNonClickableViewRef.get();
        if (view == null) {
            return;
        }
        addClickableView(view.getContext(), null);
    }

}
