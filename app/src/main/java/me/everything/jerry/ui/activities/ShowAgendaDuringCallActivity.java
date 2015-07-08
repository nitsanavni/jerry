package me.everything.jerry.ui.activities;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import me.everything.jerry.R;
import me.everything.jerry.db.Agenda;
import me.everything.jerry.utils.ContactsUtils;

public class ShowAgendaDuringCallActivity extends Activity {

    private TextView mTextView;
    private Agenda mAgenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_agenda_during_call);
        mTextView = (TextView) findViewById(R.id.textView);
        mAgenda = getIntent().getParcelableExtra(Agenda.KEY);
        mTextView.setText(mAgenda.getContactName() + "\n" + mAgenda.getAgendaSubject() + "\n" + mAgenda.getAgenda());
    }

}
