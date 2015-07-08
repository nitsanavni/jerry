package me.everything.jerry.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import me.everything.jerry.R;
import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.utils.ContactsUtils;

/**
 * Created by nitsan on 7/7/15.
 */
public class AgendaEditorDialog extends DialogFragment {

    private static final String KEY_SHARED_TEXT = "shared_text";
    private static final String KEY_SHARED_SUBJECT = "shared_subject";
    private ContactsUtils.Contact mContact;
    private String mSharedText;
    private String mSharedSubject;

    public static DialogFragment newInstance(ContactsUtils.Contact contact) {
        Bundle args = new Bundle(1);
        args.putParcelable(ContactsUtils.CONTACT_KEY, contact);
        DialogFragment fragment = new AgendaEditorDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static DialogFragment newInstance(ContactsUtils.Contact contact, String sharedText, String sharedSubject) {
        Bundle args = new Bundle(2);
        args.putParcelable(ContactsUtils.CONTACT_KEY, contact);
        args.putString(KEY_SHARED_TEXT, sharedText);
        args.putString(KEY_SHARED_SUBJECT, sharedText);
        DialogFragment fragment = new AgendaEditorDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (null == args) {
            return;
        }
        mContact = args.getParcelable(ContactsUtils.CONTACT_KEY);
        mSharedText = args.getString(KEY_SHARED_TEXT);
        mSharedSubject = args.getString(KEY_SHARED_SUBJECT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View content = LayoutInflater.from(context).inflate(R.layout.agenda_dialog_content_view, null);
        final EditText field = (EditText) content.findViewById(R.id.agenda_field);
        final EditText subject = (EditText) content.findViewById(R.id.subject_field);
        if (mSharedText != null) {
            field.setText(mSharedText);
        }
        if (mSharedSubject != null) {
            subject.setText(mSharedSubject);
        }
        builder
                .setTitle(mContact.getName())
                .setView(content)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable text = field.getText();
                        AgendaDbHelper dbHelper = AgendaDbHelper.getInstance(context);
                        dbHelper.addAgendaItem(mContact, text.toString(), subject.getText().toString());
                        dbHelper.incrementSeen(mContact.getPhoneNumber());
                    }
                });
        return builder.create();
    }


}
