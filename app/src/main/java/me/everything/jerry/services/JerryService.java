package me.everything.jerry.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import me.everything.jerry.db.Agenda;
import me.everything.jerry.receivers.JerryViewHolder;

/**
 * Created by nitsan on 7/9/15.
 */
public class JerryService extends Service {

    private static final String TAG = JerryService.class.getSimpleName();
    private JerryViewHolder mJerryViewHolder = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mJerryViewHolder = new JerryViewHolder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return Service.START_NOT_STICKY;
        }
        switch (intent.getAction()) {
            default:
            case Actions.REMOVE_VIEW:
                Log.d(TAG, "REMOVE_VIEW");
                mJerryViewHolder.removeView();
                break;
            case Actions.ADD_VIEW:
                Log.d(TAG, "ADD_VIEW");
                Agenda agenda = intent.getParcelableExtra(Agenda.KEY);
                mJerryViewHolder.addView(agenda, true);
                break;
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mJerryViewHolder != null) {
            mJerryViewHolder.destroy();
            mJerryViewHolder = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void addView(Context context, Agenda agenda) {
        Intent intent = new Intent(context, JerryService.class);
        intent.setAction(Actions.ADD_VIEW);
        intent.putExtra(Agenda.KEY, agenda);
        context.startService(intent);
    }

    public static void removeView(Context context) {
        Intent intent = new Intent(context, JerryService.class);
        intent.setAction(Actions.REMOVE_VIEW);
        context.startService(intent);
    }

    private static final class Actions {
        private static final String ADD_VIEW = "add_view";
        private static final String REMOVE_VIEW = "remove_view";
    }

}
