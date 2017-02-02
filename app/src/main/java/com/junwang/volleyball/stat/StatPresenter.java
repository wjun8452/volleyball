package com.junwang.volleyball.stat;

import android.content.Context;

import com.junwang.volleyball.model.ModelRepoFactory;
import com.junwang.volleyball.model.Court;
import com.junwang.volleyball.model.CourtListener;
import com.junwang.volleyball.model.CourtStatus;
import com.junwang.volleyball.model.PlayItem;
import com.junwang.volleyball.model.Player;

import java.util.Set;

/**
 * Created by junwang on 28/01/2017.
 */

public class StatPresenter implements StatContract.Presenter, CourtListener {

    StatContract.View view;
    Court court;
    Context context;

    public StatPresenter(Context context, Court court, StatContract.View view) {
        this.context = context;
        this.court = court;
        this.view = view;
        view.setPresenter(this);
        this.court.addListener(this);
    }

    @Override
    public String getCourtId() {
        return court.getCreatedTime().toString();
    }

    @Override
    public void loadCourt() {
        view.showCourtID(court.getCreatedTime().toString());
        view.showScore(court.getScore(), court.getScoreCompetitor());
        view.updateCourtStatus(court.getStatus());
        for (int i=0; i<7; i++) {
            Player player = court.getPlayer(i);
            view.showStatPad(i, player, court.isFaqiulun()&&i==1, court.getPositiveScore(player),
                        court.getNegotiveScore(player), court.getPositivePlayItems(player), court.getNegotivePlayItems(player));
        }
    }

    @Override
    public void addScore(int position, PlayItem item) {
        if (position != 0) {
            court.addScore(court.getPlayer(position), item);
        } else {
            court.addScore();
        }
        ModelRepoFactory.getModelRepo().saveCourt(context, court);
    }

    @Override
    public void looseScore(int position, PlayItem item) {
        if (position != 0) {
            court.looseScore(court.getPlayer(position), item);
        } else {
            court.looseScore();
        }
        ModelRepoFactory.getModelRepo().saveCourt(context, court);
    }

    @Override
    public void undo() {
        court.undo();
        ModelRepoFactory.getModelRepo().saveCourt(context, court);
    }

    @Override
    public void redo() {
        court.redo();
        ModelRepoFactory.getModelRepo().saveCourt(context, court);
    }

    @Override
    public void delete() {
        ModelRepoFactory.getModelRepo().deleteCourt(context, court.getId());
        view.finish();
    }

    @Override
    public void start() {
        loadCourt();
    }

    @Override
    public void viewDestory() {
        court.removeListener(this);
    }

    @Override
    public void statChanged(int position, Player player, boolean faqiu, int scoreWin, int scoreLost, Set<PlayItem> availablePositives, Set<PlayItem> availableNegotives) {
        view.showStatPad(position, player, faqiu, scoreWin, scoreLost, availablePositives, availableNegotives);
    }

    @Override
    public void scoreChanged(int myScore, int yourScore) {
        view.showScore(myScore, yourScore);
    }

    @Override
    public void statusChanged(CourtStatus status) {
        view.updateCourtStatus(status);
    }

    @Override
    public void onPlayerAdded(int position, Player player) {
        loadCourt();
    }
}
