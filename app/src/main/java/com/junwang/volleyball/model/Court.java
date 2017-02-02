package com.junwang.volleyball.model;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by junwang on 15/01/2017.
 */

public interface Court {
    String getId();

    Date getCreatedTime();

    boolean addListener(CourtListener listener);

    boolean removeListener(CourtListener listener);

    boolean isFaqiulun();

    boolean addPlayer(int position, Player player);

    boolean start(boolean faqiu, int totalScore);

    int getScore();

    int getScoreCompetitor();

    Player getPlayer(int position);

    Player[] getPlayers();

    CourtStatus getStatus();

    void addScore();

    void looseScore();

    void addScore(Player player, PlayItem playItem);

    void looseScore(Player player, PlayItem playItem);

    void normalStat(Player player, PlayItem playItem);

    void goodStat(Player player, PlayItem playItem);

    Collection<StatItem> getStats(Player player);

    Collection<StatItem> getAllStats();

    int getPositiveScore(Player player);

    int getNegotiveScore(Player player);

    void undo();

    boolean canUndo();

    void redo();

    boolean canRedo();

    int getPosition(Player player);

    Set<PlayItem> getPositivePlayItems(Player player);

    Set<PlayItem> getNegotivePlayItems(Player player);


}
