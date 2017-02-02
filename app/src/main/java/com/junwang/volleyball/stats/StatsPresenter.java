package com.junwang.volleyball.stats;

import android.content.Context;

import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Court;

import java.util.List;

/**
 * Created by junwang on 30/01/2017.
 */

public class StatsPresenter implements StatsContract.Presenter {
    StatsContract.View view;
    Context context;

    public StatsPresenter(Context context, StatsContract.View view) {
        this.view = view;
        this.context = context;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        loadStats();
    }

    @Override
    public void viewDestory() {

    }

    @Override
    public void loadStats() {
        List<Court> courts = load();
        if (courts!=null && (!courts.isEmpty())) {
            view.showCourts(courts);
        } else {
            view.showNoCourt();
        }
    }

    @Override
    public void addNewStats() {
        this.view.showAddCourt();
    }

    @Override
    public void deleteStat(Court court) {
        ModelRepoFactory.getModelRepo().deleteCourt(context, court.getId());
        loadStats();
    }

    private List<Court> load() {
        return ModelRepoFactory.getModelRepo().loadCourt(context);
    }
}
