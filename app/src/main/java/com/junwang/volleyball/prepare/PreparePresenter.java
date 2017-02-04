package com.junwang.volleyball.prepare;

import android.content.Context;

import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.Player;
import com.junwang.volleyball.model.Sync;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junwang on 30/01/2017.
 */

public class PreparePresenter implements  PrepareContract.Presenter {
    PrepareContract.View view;
    Context context;
    Court court;

    PreparePresenter(Context context, PrepareContract.View view, Court court) {
        this.context = context;
        this.view = view;
        this.court = court;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        loadCourt();
    }

    @Override
    public void viewDestory() {

    }

    @Override
    public void addPlayer(int position, Player player) {
        court.addPlayer(position, player);
        ModelRepoFactory.getModelRepo().saveCourt(context, court);
    }

    @Override
    public void prepareCourtDone(boolean faqiu) {
        if (court.getStatus().equals(CourtStatus.Not_Started)) {
            court.start(faqiu, 25);
            ModelRepoFactory.getModelRepo().saveCourt(context, court);
            view.showStatActivity(court.getId());
        } else {
            view.showStatActivity(court.getId());
        }
    }

    @Override
    public void loadCourt() {
        if (court == null) {
            court = ModelRepoFactory.getModelRepo().newCourt(context);
            Sync.getInstance(context).getLocalCache().add(court.getId());
            ModelRepoFactory.getModelRepo().saveCourt(context, court);
        }
        view.showCourt(court.getPlayers(), court.getStatus().equals(CourtStatus.Started), court.isFaqiulun());
    }

    @Override
    public List<PlayerInfo> getPlayerCandidates() {
        List<PlayerInfo> list = new ArrayList<>();
        for (Player player : ModelRepoFactory.getModelRepo().loadLocalPlayers(context)) {
            list.add(new PlayerInfo(player, false));
        }

        Player[] onCourt = court.getPlayers();
        for (PlayerInfo info : list) {
            for (Player selected : onCourt) {
                if (selected != null && selected.getName().equals(info.getPlayer().getName())) {
                    info.setSelected(true);
                }
            }
        }

        return list;
    }

    @Override
    public void delete() {
        ModelRepoFactory.getModelRepo().deleteCourt(context, court.getId());
        Sync.getInstance(context).getLocalCache().remove(court.getId());
        view.finish();
    }

}
