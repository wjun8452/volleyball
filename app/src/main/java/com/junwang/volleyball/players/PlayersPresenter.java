package com.junwang.volleyball.players;

import android.content.Context;

import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Player;

import java.util.List;

/**
 * Created by junwang on 28/01/2017.
 */

public class PlayersPresenter implements PlayersContract.Presenter {

    private PlayersContract.View view;
    private Context context;

    PlayersPresenter(Context context, PlayersContract.View view) {
        this.context = context;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void loadPlayers() {
        List<Player> playerList = ModelRepoFactory.getModelRepo().loadPlayers(context);
        if (playerList!=null && (!playerList.isEmpty())) {
            view.showPlayers(playerList);
        } else {
            view.showNoPlayer();
        }
    }

    @Override
    public void addPlayer(Player player) {
        ModelRepoFactory.getModelRepo().savePlayer(context, player);
        loadPlayers();
    }

    @Override
    public void deletePlayer(Player player) {
        ModelRepoFactory.getModelRepo().deletePlayer(context, player.getName());
        loadPlayers();
    }


    @Override
    public void start() {
        loadPlayers();
    }

    @Override
    public void viewDestory() {

    }
}
