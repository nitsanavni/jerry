package me.everything.jerry.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import me.everything.jerry.R;
import me.everything.jerry.ui.dialogs.AgendaEditorDialog;
import me.everything.jerry.utils.ContactsUtils;


public class ContactsActivity extends Activity {

    private ListView mList;
    private Adapter mAdapter;

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
                DialogFragment fragment = AgendaEditorDialog.newInstance(contact);
                fragment.show(getFragmentManager(), null);
            }
        });
    }

    private static class Holder {
        TextView name;
    }

    private static class Adapter extends BaseAdapter {

        private List<ContactsUtils.Contact> mContacts;
        private final WeakReference<Context> mContextRef;

        public Adapter(Context context) {
            mContextRef = new WeakReference<>(context);
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
            if (convertView == null) {
                Context context = mContextRef.get();
                if (context == null) {
                    return null;
                }
                convertView = LayoutInflater.from(context).inflate(R.layout.contact_layout, null);
                holder = new Holder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            ContactsUtils.Contact contact = (ContactsUtils.Contact) getItem(position);
            String name = contact.getName();
            holder.name.setText(name);
            if (name.contains("שי")) {
                holder.name.setBackgroundColor(Color.RED);
            } else {
                holder.name.setBackgroundColor(Color.BLACK);
            }
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
            activity.mAdapter.put(contacts);
        }
    }

}
