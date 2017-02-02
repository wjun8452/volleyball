package com.junwang.volleyball.stat;

import com.junwang.volleyball.BasePresenter;
import com.junwang.volleyball.BaseView;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.PlayItem;
import com.junwang.volleyball.model.Player;

import java.util.List;
import java.util.Set;

/**
 * Created by junwang on 28/01/2017.
 */

public interface StatContract {
    interface View extends BaseView<Presenter> {
        //position==0, 表示不针对性记分
        void showStatPad(int position, Player player, boolean isFaqiu, int score_win, int score_lost, Set<PlayItem> availablePositives, Set<PlayItem> availableNegotives);
        void showCourtID(String id);
        void updateCourtStatus(CourtStatus courtStatus);
        void showScore(int mine, int yours);
        void finish();
    }

    interface Presenter extends BasePresenter {
        String getCourtId();
        void loadCourt();
        void addScore(int position, PlayItem item);
        void looseScore(int position, PlayItem item);
        void undo();
        void redo();
        void delete();
    }
}
