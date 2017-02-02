package com.junwang.volleyball.model;

import java.util.Set;

/**
 * Created by junwang on 22/01/2017.
 */

public interface CourtListener {
    void statChanged(int position, Player player, boolean faqiu, int scoreWin, int scoreLost, Set<PlayItem> availablePositives, Set<PlayItem> availableNegotives);
    void scoreChanged(int myScore, int yourScore);
    void statusChanged(CourtStatus status);
    void onPlayerAdded(int position, Player player);
}
