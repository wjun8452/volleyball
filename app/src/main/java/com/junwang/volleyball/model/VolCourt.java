package com.junwang.volleyball.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

/**
 * Created by junwang on 19/01/2017.
 */

public class VolCourt implements Court {
    private UUID id =  UUID.randomUUID();
    private CourtStatus status = CourtStatus.Not_Started;
    private int yourScore;
    private int myScore;
    private int totalScore;
    private boolean isFaqiuLun;
    private Player[] players = new Player[6+1];
    private Stack<StatItem> stats = new Stack<>();
    private transient Stack<StatItem> undo = new Stack<>();
    private transient Set<CourtListener> listeners = new HashSet<>();
    private long date = new Date().getTime();

    @Override
    public boolean addPlayer(int position, Player player) {
        if (position < 1 || position > 6) {
            return false;
        }

        players[position] = player;

        playerAdded(position, player);

        return true;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public Date getCreatedTime() {
        return new Date(date);
    }

    @Override
    public boolean addListener(CourtListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(CourtListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public boolean isFaqiulun() {
        return isFaqiuLun;
    }


    @Override
    public boolean start(boolean faqiu, int totalScore) {
        if (status.equals(CourtStatus.Started)) {
            return false;
        }

        if (totalScore < 2) {
            return false;
        }

        for (int i=1; i<7; i++) {
            if (players[i] == null) {
                return false;
            }
        }

        if (duplicatedPlayers()) {
            return false;
        }

        this.isFaqiuLun = faqiu;
        this.totalScore = totalScore;

        status = CourtStatus.Started;

        statusChanged(status);
        statChanged();

        return true;
    }

    private boolean duplicatedPlayers() {
        Set<String> set = new HashSet<>();
        for (int i=1; i<7; i++) {
            Player player = players[i];
            if (player != null) {
                boolean ok = set.add(player.getName());
                if (!ok) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getScore() {
        return myScore;
    }

    @Override
    public int getScoreCompetitor() {
        return yourScore;
    }

    @Override
    public Player getPlayer(int position) {
        return players[position];
    }

    @Override
    public Player[] getPlayers() {
        return players;
    }

    @Override
    public CourtStatus getStatus() {
        return status;
    }

    @Override
    public void addScore() {
        if (!status.equals(CourtStatus.Started)) {
            return;
        }
        addStat(StatResult.ADD_SCORE, null, null);
        statChanged();
    }

    @Override
    public void looseScore() {
        if (!status.equals(CourtStatus.Started)) {
            return;
        }
        addStat(StatResult.LOOSE_SCORE, null, null);
        statChanged();
    }

    private void addScoreToMe() {
        myScore++;
        updateCourtStatus();
        scoreChanged();
    }

    private void undoAddScoreToMe() {
        myScore--;
        updateCourtStatus();
        scoreChanged();
    }

    private void addScoreCompetitor() {
        yourScore++;
        updateCourtStatus();
        scoreChanged();
    }

    private void undoAddScoreCompetitor() {
        yourScore--;
        updateCourtStatus();
        scoreChanged();
    }

    private void statChanged() {

        for(int i=1; i<7; i++) {
            Set<PlayItem> positives = getPositivePlayItems(players[i]);
            Set<PlayItem> negotives = getNegotivePlayItems(players[i]);
            int win = getPositiveScore(players[i]);
            int lost = getNegotiveScore(players[i]);
            for(CourtListener listener : listeners) {
                listener.statChanged(i, players[i], isFaqiulun()&&i==1, win, lost, positives, negotives);
            }
        }

        for(CourtListener listener : listeners) {
            listener.statChanged(0, null, false, getPositiveScore(null), getNegotiveScore(null), null, null);
        }
    }


    private void scoreChanged() {
        for(CourtListener listener : listeners) {
            listener.scoreChanged(myScore, yourScore);
        }
    }

    private void statusChanged(CourtStatus status) {
        for(CourtListener listener : listeners) {
            listener.statusChanged(status);
        }
    }

    private void playerAdded(int position, Player player) {
        for (CourtListener listener : listeners) {
            listener.onPlayerAdded(position, player);
        }
    }

    private void updateCourtStatus() {
        if (myScore >= totalScore && myScore-yourScore>=2) {
            status = CourtStatus.Win;
            statusChanged(status);
        } else if (yourScore >= totalScore && yourScore-myScore>=2) {
            status = CourtStatus.Lost;
            statusChanged(status);
        } else {
            status = CourtStatus.Started;
        }
    }

    private void goToNextPosition() {
        Player player6 = players[6];
        players[6] = players[1];
        players[1] = players[2];
        players[2] = players[3];
        players[3] = players[4];
        players[4] = players[5];
        players[5] = player6;
    }

    private void undoNextPosition() {
        Player player6 = players[6];
        players[6] = players[5];
        players[5] = players[4];
        players[4] = players[3];
        players[3] = players[2];
        players[2] = players[1];
        players[1] = player6;
    }

    @Override
    public void addScore(Player player, PlayItem playItem) {
        if (!status.equals(CourtStatus.Started)) {
            return;
        }
        addStat(StatResult.ADD_SCORE, player, playItem);
        statChanged();
    }

    private void addStat(StatResult result, Player player, PlayItem playItem) {
        boolean isGoingtoFaqiu = result.equals(StatResult.ADD_SCORE);

        if ((!isFaqiuLun) && isGoingtoFaqiu) {
            goToNextPosition();
        }

        if (result.equals(StatResult.ADD_SCORE)) {
            addScoreToMe();
        } else if (result.equals(StatResult.LOOSE_SCORE)) {
            addScoreCompetitor();
        }

        StatItem item = new StatItem();
        item.setPlayer(player);
        item.setPlayItem(playItem);
        item.setStatResult(result);
        item.setFaqiuBefore(isFaqiuLun);
        item.setFaqiuAfter(isGoingtoFaqiu);
        stats.push(item);

        isFaqiuLun = isGoingtoFaqiu;
    }

    private void undoStat() {
        if (stats.empty()) {
            return;
        }

        StatItem item = stats.pop();
        undo.push(item);

        if (item.getStatResult().equals(StatResult.ADD_SCORE)) {
            undoAddScoreToMe();

        } else if (item.getStatResult().equals(StatResult.LOOSE_SCORE)) {
            undoAddScoreCompetitor();
        }

        if ((!item.isFaqiuBefore()) && item.isFaqiuAfter()) {
            undoNextPosition();
        }

        isFaqiuLun = item.isFaqiuBefore();
    }


    @Override
    public void looseScore(Player player, PlayItem playItem) {
        if (!status.equals(CourtStatus.Started)) {
            return;
        }
        addStat(StatResult.LOOSE_SCORE, player, playItem);
        statChanged();
    }

    @Override
    public void normalStat(Player player, PlayItem playItem) {
        if (!status.equals(CourtStatus.Started)) {
            return;
        }
        addStat(StatResult.NORMAL, player, playItem);
        statChanged();
    }

    @Override
    public void goodStat(Player player, PlayItem playItem) {
        if (!status.equals(CourtStatus.Started)) {
            return;
        }
        addStat(StatResult.GOOD, player, playItem);
        statChanged();
    }

    @Override
    public Collection<StatItem> getStats(Player player) {
        Collection<StatItem> items = new ArrayList<>();
        for (StatItem item: stats) {
            if (item.getPlayer() != null && player != null && item.getPlayer().getName().equals(player.getName())) {
                items.add(item);
            } else if (player == null && item.getPlayer()==null) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public Collection<StatItem> getAllStats() {
        return stats;
    }

    @Override
    public int getPositiveScore(Player player) {
        int score = 0;
        Collection<StatItem> collections = getStats(player);
        if (collections != null) {
            for (StatItem item : collections) {
                if (item.getStatResult().equals(StatResult.ADD_SCORE)) {
                    score++;
                }
            }
        }
        return score;
    }

    @Override
    public int getNegotiveScore(Player player) {
        int score = 0;
        Collection<StatItem> collections = getStats(player);
        if (collections != null) {
            for (StatItem item : collections) {
                if (item.getStatResult().equals(StatResult.LOOSE_SCORE)) {
                    score++;
                }
            }
        }
        return score;
    }

    @Override
    public void undo() {
        undoStat();
        statChanged();
    }

    @Override
    public boolean canUndo() {
        return !stats.empty();
    }

    @Override
    public void redo() {
        redoStat();
        statChanged();
    }

    private void redoStat() {
        if (undo.empty()) {
            return;
        }

        StatItem item = undo.pop();
        addStat(item.getStatResult(), item.getPlayer(), item.getPlayItem());
    }

    @Override
    public boolean canRedo() {
        return !undo.empty();
    }

    @Override
    public int getPosition(Player player) {
        for(int i=1; i<7; i++) {
            if (players[i] != null && players[i].equals(player)) {
                return i;
            }
        }
        return 0;
    }

    public Set<PlayItem> getNegotivePlayItems(Player player) {
        if (player == null) {
            return null;
        }

        Set<PlayItem> items = new HashSet<>();
        items.add(PlayItem.JINGONG);
        items.add(PlayItem.FANGSHOU);
        if (!isFaqiulun()) {
            items.add(PlayItem.YICHUAN);
        }

        if (getPosition(player) == 1 && isFaqiulun()) {
            items.add(PlayItem.FAQIU);
        }

        if (getPosition(player) >= 2 && getPosition(player) <= 4) {
            items.add(PlayItem.LANWANG);
        }

        return items;
    }



    public Set<PlayItem> getPositivePlayItems(Player player) {
        Set<PlayItem> items = new HashSet<>();
        items.add(PlayItem.JINGONG);

        if (getPosition(player)==1 && isFaqiulun()) {
            items.add(PlayItem.FAQIU);
        }

        if (getPosition(player) >= 2 && getPosition(player) <= 4) {
            items.add(PlayItem.LANWANG);
        }

        return items;
    }

}
