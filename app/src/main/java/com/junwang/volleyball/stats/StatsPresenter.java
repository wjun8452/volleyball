package com.junwang.volleyball.stats;

import android.content.Context;

import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.ModelRepository;

import java.util.List;

/**
 * Created by junwang on 30/01/2017.
 */

public class StatsPresenter implements StatsContract.Presenter {
    StatsContract.View view;
    Context context;
    private boolean firstTime = true;

    public StatsPresenter(Context context, StatsContract.View view) {
        this.view = view;
        this.context = context;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        ModelRepoFactory.getModelRepo().init(context);
        if (firstTime) {
            loadStats(true);
        } else {
            loadStats(false);
        }
        firstTime = false;
    }

    @Override
    public void viewDestory() {

    }

    @Override
    public void loadStats(final boolean fetchServer) {
        ModelRepoFactory.getModelRepo().loadCourt(fetchServer, context, new ModelRepository.LoadCourtCallback() {
            @Override
            public void onCourtLoaded(List<Court> courts) {
                if (courts!=null && (!courts.isEmpty())) {
                    view.showCourts(courts);
                } else {
                    view.showNoCourt();
                }
            }
        });
    }

    @Override
    public void addNewStats() {
        this.view.showAddCourt();
    }

    @Override
    public void deleteStat(Court court) {
        ModelRepoFactory.getModelRepo().deleteCourt(context, court.getId());
        loadStats(true);

    }
}
