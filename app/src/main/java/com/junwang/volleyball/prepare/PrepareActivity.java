package com.junwang.volleyball.prepare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.junwang.volleyball.R;
import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.Player;
import com.junwang.volleyball.stat.StatActivity;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class PrepareActivity extends AppCompatActivity implements PrepareContract.View, PrepareFragment.Listener {
    public static final String KEY_STAT_ID = "id";

    private PrepareFragment[] prepareFragments = new PrepareFragment[6];
    private PrepareContract.Presenter presenter;
    private boolean started;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        showCourt(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit_court_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.prepareCourtDone(((CheckBox) findViewById(R.id.faqiu)).isChecked());
            }
        });

        Court court = null;
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

        presenter = new PreparePresenter(this, this, court);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
    }

    private void showCourt(Bundle savedInstanceState) {
        int ids[] = new int[] {R.id.pos1, R.id.pos2, R.id.pos3, R.id.pos4, R.id.pos5, R.id.pos6};
        int i=0;

        for (int id : ids) {
            if (findViewById(id) != null) {
                PrepareFragment fragment = (PrepareFragment) getSupportFragmentManager().findFragmentById(id);
                if (fragment == null) {
                    fragment = new PrepareFragment();
                    getSupportFragmentManager().beginTransaction().add(id, fragment).commit();
                }
                fragment.setListener(this);

                prepareFragments[i] = fragment;
            }
            i++;
        }
    }

    private int fragmentToPositionId(PrepareFragment fragment) {
        int id = 0;
        for (; id < 6; id++) {
            if (prepareFragments[id] == fragment) {
                return id+1;
            }
        }
        return 0;
    }

    @Override
    public void setPresenter(PrepareContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public List<PlayerInfo> getPlayerCandidates() {
        return presenter.getPlayerCandidates();
    }

    @Override
    public void playerAdded(PrepareFragment fragment, Player player) {
        int id = fragmentToPositionId(fragment);
        presenter.addPlayer(id, player);
    }

    @Override
    public void showCourt(Player[] players, boolean started, boolean faqiu) {
        this.started = started;

        for (int i=1; i<7; i++) {
            prepareFragments[i-1].updatePlayer(players[i]);
        }
        ((CheckBox) findViewById(R.id.faqiu)).setEnabled(!started);
        ((CheckBox) findViewById(R.id.faqiu)).setChecked(faqiu);
    }

    @Override
    public void showStatActivity(String id) {
        Intent intent = new Intent(PrepareActivity.this, StatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(StatActivity.KEY_STAT_ID, id);
        startActivityForResult(intent, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_prepare, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                presenter.delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
