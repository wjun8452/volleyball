package com.junwang.volleyball.stat;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.junwang.volleyball.R;
import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.PlayItem;
import com.junwang.volleyball.model.Player;
import com.junwang.volleyball.model.VolCourt;
import com.junwang.volleyball.prepare.PrepareActivity;
import com.junwang.volleyball.report.ReportActivity;

import java.util.Set;


public class StatActivity extends AppCompatActivity implements View.OnClickListener, StatContract.View, StatFragment.StatFragmentListener {
    public static final String KEY_STAT_ID = "id";
    private StatContract.Presenter presenter;
    private StatFragment[] statFragments = new StatFragment[6];
    private CourtStatus courtStatus;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        int ids[] = new int[]{R.id.pos1, R.id.pos2, R.id.pos3, R.id.pos4, R.id.pos5, R.id.pos6};
        int i = 0;
        for (int id : ids) {
            if (findViewById(id) != null) {
                StatFragment fragment = (StatFragment) getSupportFragmentManager().findFragmentById(id);
                if (fragment == null) {
                    fragment = new StatFragment();
                    getSupportFragmentManager().beginTransaction().add(id, fragment).commit();
                }
                fragment.setListener(this);

                statFragments[i] = fragment;
            }
            i++;
        }

        ((Button) findViewById(R.id.competitor_error)).setOnClickListener(this);
        ((Button) findViewById(R.id.self_error)).setOnClickListener(this);


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

        presenter = new StatPresenter(this, court, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_stat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = (MenuItem) menu.getItem(0);
        item.setEnabled(courtStatus.equals(CourtStatus.Not_Started) || courtStatus.equals(CourtStatus.Started));

        item = (MenuItem) menu.getItem(1);
        item.setEnabled(courtStatus.equals(CourtStatus.Started));

        item = (MenuItem) menu.getItem(2);
        item.setEnabled(courtStatus.equals(CourtStatus.Started));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.competitor_error:
                presenter.addScore(0, null);
                break;
            case R.id.self_error:
                presenter.looseScore(0, null);
                break;
        }
    }

    @Override
    public void setPresenter(StatContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showStatPad(int position, Player player, boolean isFaqiu, int score_win, int score_lost, Set<PlayItem> availablePositives, Set<PlayItem> availableNegotives) {
        if (position > 0 && position < 7) {
            StatFragment fragment = statFragments[position - 1];
            if (fragment != null) {
                String name = (player == null) ? "???" : player.getName();
                fragment.showStatPad(name, isFaqiu, score_win, score_lost, availablePositives, availableNegotives);
            }
        } else if (position == 0) {
            ((Button)findViewById(R.id.competitor_error)).setText("对方失误" + Integer.valueOf(score_win).toString()) ;
            ((Button)findViewById(R.id.self_error)).setText("我方失误" + Integer.valueOf(score_lost).toString()) ;
        }

    }

    @Override
    public void showCourtID(String id) {
        ((TextView) findViewById(R.id.createdTime)).setText(id);
    }

    @Override
    public void updateCourtStatus(CourtStatus courtStatus) {
        ((TextView) findViewById(R.id.status)).setText(courtStatus.name());

        if (!courtStatus.equals(CourtStatus.Started)) {
            findViewById(R.id.competitor_error).setEnabled(false);
            findViewById(R.id.self_error).setEnabled(false);
            for (StatFragment fragment : statFragments) {
                fragment.disableStat();
            }
        } else {
            findViewById(R.id.competitor_error).setEnabled(true);
            findViewById(R.id.self_error).setEnabled(true);
            for (StatFragment fragment : statFragments) {
                fragment.enableStat();
            }
        }

        this.courtStatus = courtStatus;
    }

    @Override
    public void showScore(int mine, int yours) {
        ((TextView) findViewById(R.id.my_score)).setText(Integer.valueOf(mine).toString());
        ((TextView) findViewById(R.id.your_score)).setText(Integer.valueOf(yours).toString());
    }


    private int findId(StatFragment fragment) {
        int id = 0;
        for (; id < 6; id++) {
            if (statFragments[id] == fragment) {
                return id + 1;
            }
        }
        return 0;
    }

    @Override
    public void onStatItemSelected(StatFragment fragment, PlayItem item, boolean positive) {
        int position = findId(fragment);
        if (positive) {
            presenter.addScore(position, item);
        } else {
            presenter.looseScore(position, item);
        }
    }

    protected void onDestory() {
        super.onDestroy();
        presenter.viewDestory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_undo:
                presenter.undo();
                break;
            case R.id.menu_redo:
                presenter.redo();
                break;
            case R.id.menu_huanren:
                Intent intent = new Intent(StatActivity.this, PrepareActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(PrepareActivity.KEY_STAT_ID, presenter.getCourtId());
                startActivityForResult(intent, 100);
                break;
            case R.id.menu_delete:
                presenter.delete();
                break;
            case R.id.menu_report:
                intent = new Intent(StatActivity.this, ReportActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(ReportActivity.KEY_STAT_ID, presenter.getCourtId());
                startActivityForResult(intent, 100);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
