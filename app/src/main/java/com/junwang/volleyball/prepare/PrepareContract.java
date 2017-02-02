package com.junwang.volleyball.prepare;

import com.junwang.volleyball.BasePresenter;
import com.junwang.volleyball.BaseView;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.PlayItem;
import com.junwang.volleyball.model.Player;

import java.util.List;
import java.util.Set;

/**
 * Created by junwang on 28/01/2017.
 */

public interface PrepareContract {
    interface View extends BaseView<Presenter> {
        void showCourt(Player[] players, boolean started, boolean faqiu);
        void showStatActivity(String id);
        void finish();
    }

    interface Presenter extends BasePresenter {
        void addPlayer(int position, Player player);
        void prepareCourtDone(boolean faqiu);
        void loadCourt();
        List<PlayerInfo> getPlayerCandidates();
        void delete();
    }
}
