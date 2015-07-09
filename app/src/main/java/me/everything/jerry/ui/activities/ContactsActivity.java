package me.everything.jerry.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import me.everything.jerry.R;
import me.everything.jerry.db.Agenda;
import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.ui.dialogs.AgendaEditorDialog;
import me.everything.jerry.utils.ContactsUtils;
import me.everything.jerry.utils.InitialsFinder;
import me.everything.jerry.utils.StringUtils;


public class ContactsActivity extends Activity {

    private static final String TAG = ContactsActivity.class.getSimpleName();
    private ListView mList;
    private Adapter mAdapter;
    private String mSharedText;
    private String mSharedSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mList = (ListView) findViewById(R.id.list);
        mAdapter = new Adapter(this);
        mList.setAdapter(mAdapter);
        new GetContactsTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactsUtils.Contact contact = (ContactsUtils.Contact) mAdapter.getItem(position);
                DialogFragment fragment;
                if (mSharedText != null) {
                    fragment = AgendaEditorDialog.newInstance(contact, mSharedText, mSharedSubject);
                } else {
                    fragment = AgendaEditorDialog.newInstance(contact);
                }
                fragment.show(getFragmentManager(), null);
            }
        });
        AgendaDbHelper.getInstance(this).printAllDb();
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        for (String key : extras.keySet()) {
            Log.d(TAG, "extra " + key + ": " + extras.get(key).toString());
        }
        mSharedText = extras.getString(Intent.EXTRA_TEXT);
        mSharedSubject = extras.getString(Intent.EXTRA_SUBJECT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private static class Holder {
        TextView name;
        CircleImageView image;
        View badge;
    }

    private static class Adapter extends BaseAdapter {

        private List<ContactsUtils.Contact> mContacts;
        private final WeakReference<Context> mContextRef;
        private Random mRandom = new Random();
        private int mImageSize;
        private int mInitialsTextSize;

        public Adapter(Context context) {
            mContextRef = new WeakReference<>(context);
            Resources resources = context.getResources();
            mImageSize = resources.getDimensionPixelSize(R.dimen.contact_image_size);
            mInitialsTextSize = resources.getDimensionPixelSize(R.dimen.initials_text_size);
        }

        @Override
        public int getCount() {
            if (mContacts == null)
                return 0;
            return mContacts.size();
        }

        @Override
        public Object getItem(int position) {
            if (mContacts == null || position >= mContacts.size()) {
                return null;
            }
            return mContacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (mContacts == null || position >= mContacts.size()) {
                return 0L;
            }
            return Long.decode(mContacts.get(position).getId());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            Context context = mContextRef.get();
            if (context == null) {
                return null;
            }
            ContactsUtils.Contact contact = (ContactsUtils.Contact) getItem(position);
            String name = contact.getName();
            Drawable initialsDrawable;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.contact_layout, null);
                holder = new Holder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.image = (CircleImageView) convertView.findViewById(R.id.image);
                holder.badge = convertView.findViewById(R.id.badge);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.name.setText(name);

            // Create initials place holder
            Agenda agenda = contact.getAgenda();
            boolean isAgenda = agenda != null && !StringUtils.isNullOrEmpty(agenda.getAgenda());
            boolean isTitle = agenda != null && !StringUtils.isNullOrEmpty(agenda.getAgendaSubject());


            if (isAgenda || isTitle) {
                holder.badge.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                if (isTitle) {
                    sb.append(agenda.getAgendaSubject() + " ");
                }
                if (isAgenda) {
                    sb.append(agenda.getAgenda());
                }
                String subText = sb.toString().replace("\n", "; ");
                SpannableString ss = new SpannableString("\n" + subText);
                TextAppearanceSpan span = new TextAppearanceSpan(context, R.style.remonder_text);
                ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.name.append(ss);
            } else {
                holder.badge.setVisibility(View.GONE);
            }
            // Get contact image URI
            Uri uri = ContactsUtils.getPhotoUriFromID(context, contact.getId());

            initialsDrawable = InitialsFinder.getInitialsDrawable(name,
                    contact.getColor(mRandom),
                    mImageSize,
                    mImageSize,
                    mInitialsTextSize);

            // Fetch
            Picasso.with(context)
                    .load(uri)
                    .placeholder(initialsDrawable)
                    .error(initialsDrawable)
                    .into(holder.image);

            return convertView;
        }

        public void put(List<ContactsUtils.Contact> contacts) {
            mContacts = contacts;
            notifyDataSetChanged();

        }
    }

    private static class GetContactsTask extends AsyncTask<Void, Void, List<ContactsUtils.Contact>> {

        private final WeakReference<ContactsActivity> mActivityRef;

        public GetContactsTask(ContactsActivity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        protected List<ContactsUtils.Contact> doInBackground(Void... params) {
            ContactsActivity activity = mActivityRef.get();
            if (null == activity) {
                return null;
            }
            return ContactsUtils.readContacts(activity);
        }

        @Override
        protected void onPostExecute(List<ContactsUtils.Contact> contacts) {
            ContactsActivity activity = mActivityRef.get();
            if (null == activity) {
                return;
            }
            Collections.sort(contacts, new Comparator<ContactsUtils.Contact>() {
                @Override
                public int compare(ContactsUtils.Contact lhs, ContactsUtils.Contact rhs) {
                    Agenda lagenda = lhs.getAgenda();
                    Agenda ragenda = rhs.getAgenda();
                    if (null == lagenda && ragenda == null) {
                        return 0;
                    }
                    if (null == lagenda) {
                        return 1;
                    }
                    if (null == ragenda) {
                        return -1;
                    }
                    int lseen = lagenda.getSeen();
                    int rseen = ragenda.getSeen();
                    if (lseen == rseen) {
                        return 0;
                    }
                    return lseen > rseen ? -1 : 1;
                }
            });
            activity.mAdapter.put(contacts);
            final View splash = activity.findViewById(R.id.splash_image);
            splash.animate().alpha(0.0f).setDuration(800).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    splash.setVisibility(View.GONE);
                }
            }).start();
        }
    }

}
