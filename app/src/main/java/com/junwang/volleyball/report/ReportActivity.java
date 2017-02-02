package com.junwang.volleyball.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.junwang.volleyball.R;
import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.VolCourt;

public class ReportActivity extends AppCompatActivity {
    public static final String KEY_STAT_ID = "id";

    private ReportPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        Court court = new VolCourt();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String data = bundle.getString(KEY_STAT_ID);
                if (data != null) {
                    court = ModelRepoFactory.getModelRepo().findCourt(this, data);
                }
            }
        }

        ReportActivityFragment fragment = (ReportActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = new ReportActivityFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, fragment).commit();
        }

        presenter = new ReportPresenter(court, fragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
