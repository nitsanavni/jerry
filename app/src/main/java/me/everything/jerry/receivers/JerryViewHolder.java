package me.everything.jerry.receivers;

import android.content.Context;
import android.content.Intent;
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
import me.everything.jerry.ui.activities.ShowAgendaDuringCallActivity;
import me.everything.jerry.utils.StringUtils;

/**
 * Created by nitsan on 7/7/15.
 */
public class JerryViewHolder {

    private static final String TAG = JerryViewHolder.class.getSimpleName();
    private WeakReference<View> mViewRef;

    private static JerryViewHolder sInstance;
    private Agenda mAgenda;
    private float mTranslate = 0.0f;

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

    public void addView(final Context context, Agenda agenda, boolean clickable) {
        Log.d(TAG, "addView");
        final View view = LayoutInflater.from(context).inflate(R.layout.call_overlay_layout, null);
        view.setClickable(clickable);
        final TextView textView = (TextView) view.findViewById(R.id.jerry_button);
        if (agenda == null) {
            agenda = mAgenda;
        } else {
            mAgenda = agenda;
        }
        String agendaSubject = agenda.getAgendaSubject();
        SpannableString ss;
        if (!StringUtils.isNullOrEmpty(agendaSubject)) {
            ss = new SpannableString(agendaSubject + "\n");
            textView.setText(ss);
        }
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
                mTranslate = textView.getWidth() - icon.getWidth() / 2;
                int width = view.getWidth();
                icon.setTranslationX(width);
                textView.setTranslationX(width);
                view.setVisibility(View.VISIBLE);
                icon.animate().translationX(mTranslate).start();
                textView.animate().translationX(mTranslate).start();
            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            public boolean mReminderClosed = true;

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick icon");
                if (mReminderClosed) {
                    icon.animate().translationX(0).start();
                    textView.animate().translationX(0).start();
                } else {
                    icon.animate().translationX(mTranslate).start();
                    textView.animate().translationX(mTranslate).start();
                }
                mReminderClosed = !mReminderClosed;
            }
        });
        final Agenda finalAgenda = agenda;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowAgendaDuringCallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Agenda.KEY, finalAgenda);
                context.startActivity(intent);
                removeView(context);
            }
        });

        icon.setClickable(clickable);
        textView.setClickable(clickable);

        mViewRef = new WeakReference<>(view);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = getParams();
        wm.addView(view, params);
    }

    public void removeView(Context context) {
        Log.d(TAG, "removeView");
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (null != mViewRef && mViewRef.get() != null) {
            wm.removeView(mViewRef.get());
            mViewRef = null;
        }
    }

    public void offhook() {
        if (mViewRef == null) {
            return;
        }
        View view = mViewRef.get();
        if (view == null) {
            return;
        }
        view.setClickable(true);
        View icon = view.findViewById(R.id.icon);
        if (icon != null) {
            icon.setClickable(true);
        }
        View text = view.findViewById(R.id.jerry_button);
        if (text != null) {
            text.setClickable(true);
        }
    }

}
