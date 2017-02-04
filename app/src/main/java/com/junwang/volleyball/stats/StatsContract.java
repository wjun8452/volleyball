package com.junwang.volleyball.stats;

import android.content.Context;

import com.junwang.volleyball.BasePresenter;
import com.junwang.volleyball.BaseView;
import com.junwang.volleyball.model.Court;

import java.util.List;

/**
 * Created by junwang on 30/01/2017.
 */

public interface StatsContract {
    interface View extends BaseView<Presenter> {
        void showCourts(final List<Court> courtList);
        void showNoCourt();
        void showAddCourt();
    }

    interface Presenter extends BasePresenter {
        void loadStats(boolean fetchServer);
        void addNewStats();
        void deleteStat(Court court);
    }
}
