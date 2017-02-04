package com.junwang.volleyball.players;

import android.content.Context;

import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.ModelRepository;
import com.junwang.volleyball.model.Player;

import java.util.List;

/**
 * Created by junwang on 28/01/2017.
 */

public class PlayersPresenter implements PlayersContract.Presenter {

    private PlayersContract.View view;
    private Context context;
    private boolean firstTime = true;

    PlayersPresenter(Context context, PlayersContract.View view) {
        this.context = context;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void loadPlayers(final boolean fetchServer) {
        ModelRepoFactory.getModelRepo().loadPlayers(fetchServer, context,
                new ModelRepository.LoadPlayerCallback() {
                    @Override
                    public void onPlayerLoaded(List<Player> players) {
                        if (players!=null && (!players.isEmpty())) {
                            view.showPlayers(players);
                        } else {
                            view.showNoPlayer();
                        }
                    }
                });
    }

    @Override
    public void addPlayer(Player player) {
        ModelRepoFactory.getModelRepo().savePlayer(context, player);
        ModelRepoFactory.getModelRepo().markSyncAdded(context, player);
        loadPlayers(true);
    }

    @Override
    public void deletePlayer(Player player) {
        ModelRepoFactory.getModelRepo().deletePlayer(context, player.getName());
        loadPlayers(true);
    }


    @Override
    public void start() {
        if (firstTime) {
            loadPlayers(true);
        } else {
            loadPlayers(false);
        }
        firstTime = false;
    }

    @Override
    public void viewDestory() {

    }
}
