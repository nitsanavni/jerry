package me.everything.jerry.ui.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import me.everything.jerry.R;
import me.everything.jerry.db.Agenda;
import me.everything.jerry.db.AgendaDbHelper;
import me.everything.jerry.utils.StringUtils;

public class ShowAgendaDuringCallActivity extends Activity {

    private Agenda mAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda_dialog_content_view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mAgenda = getIntent().getParcelableExtra(Agenda.KEY);
        if (mAgenda == null) {
            return;
        }
        final EditText field = (EditText) findViewById(R.id.agenda_field);
        final EditText subject = (EditText) findViewById(R.id.subject_field);
        field.setText(mAgenda.getAgenda());
        subject.setText(mAgenda.getAgendaSubject());

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable text = field.getText();
                AgendaDbHelper dbHelper = AgendaDbHelper.getInstance(ShowAgendaDuringCallActivity.this);
                dbHelper.addAgendaItem(mAgenda.getContactName(), mAgenda.getContactNumber(), text.toString(), subject.getText().toString());
                dbHelper.incrementSeen(mAgenda.getContactNumber());
                finish();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.name)).setText(mAgenda.getContactName());

        View clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                field.setText(StringUtils.EMPTY_STRING);
                subject.setText(StringUtils.EMPTY_STRING);
            }
        });
        clear.setVisibility(View.VISIBLE);


    }

}
