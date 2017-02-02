package com.junwang.volleyball.players;

import com.junwang.volleyball.BasePresenter;
import com.junwang.volleyball.BaseView;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.Player;

import java.util.List;

/**
 * Created by junwang on 28/01/2017.
 */

public interface PlayersContract {
    interface View extends BaseView<Presenter> {
        void showPlayers(List<Player> playerList);
        void showNoPlayer();
    }

    interface Presenter extends BasePresenter {
        void loadPlayers();
        void addPlayer(Player player);
        void deletePlayer(Player player);
    }
}
