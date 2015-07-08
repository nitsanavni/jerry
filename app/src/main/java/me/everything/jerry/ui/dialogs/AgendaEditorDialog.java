package me.everything.jerry.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Random;

import me.everything.jerry.R;
import me.everything.jerry.db.Agenda;
import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.utils.ContactsUtils;
import me.everything.jerry.utils.InitialsFinder;
import me.everything.jerry.utils.StringUtils;

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
        args.putString(KEY_SHARED_SUBJECT, sharedSubject);
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
        if (mContact != null && mContact.getAgenda() != null) {
            Agenda agenda = mContact.getAgenda();
            field.setText(agenda.getAgenda());
            subject.setText(agenda.getAgendaSubject());
            if (!StringUtils.isNullOrEmpty(agenda.getAgenda()) || !StringUtils.isNullOrEmpty(agenda.getAgendaSubject())) {
                content.findViewById(R.id.badge).setVisibility(View.VISIBLE);
            }
        }
        if (mSharedText != null) {
            field.append(mSharedText);
        }
        if (mSharedSubject != null) {
            subject.append(mSharedSubject);
        }
        content.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable text = field.getText();
                AgendaDbHelper dbHelper = AgendaDbHelper.getInstance(context);
                dbHelper.addAgendaItem(mContact, text.toString(), subject.getText().toString());
                dbHelper.incrementSeen(mContact.getPhoneNumber());
                dismiss();
            }
        });
        content.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((TextView) content.findViewById(R.id.name)).setText(mContact.getName());

        // Get contact image URI
        Uri uri = ContactsUtils.getPhotoUriFromID(context, mContact.getId());

        Resources resources = context.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.contact_image_size);
        int textSize = resources.getDimensionPixelSize(R.dimen.initials_text_size);
        Drawable initialsDrawable = InitialsFinder.getInitialsDrawable(mContact.getName(),
                mContact.getColor(new Random()),
                dimensionPixelSize,
                dimensionPixelSize,
                textSize);

        // Fetch
        Picasso.with(context)
                .load(uri)
                .placeholder(initialsDrawable)
                .error(initialsDrawable)
                .into((ImageView) content.findViewById(R.id.image));

        return builder.setView(content).create();
    }


}
