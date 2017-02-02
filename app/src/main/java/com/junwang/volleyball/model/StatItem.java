package com.junwang.volleyball.model;

/**
 * Created by junwang on 19/01/2017.
 */

public class StatItem {
    private String abc;
    private Player player;
    private PlayItem playItem;
    private StatResult statResult;
    private boolean isFaqiuBefore;
    private boolean isFaqiuAfter;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PlayItem getPlayItem() {
        return playItem;
    }

    public void setPlayItem(PlayItem playItem) {
        this.playItem = playItem;
    }

    public StatResult getStatResult() {
        return statResult;
    }

    public void setStatResult(StatResult statResult) {
        this.statResult = statResult;
    }

    public boolean isFaqiuBefore() {
        return isFaqiuBefore;
    }

    public void setFaqiuBefore(boolean faqiuBefore) {
        isFaqiuBefore = faqiuBefore;
    }

    public boolean isFaqiuAfter() {
        return isFaqiuAfter;
    }

    public void setFaqiuAfter(boolean faqiuAfter) {
        isFaqiuAfter = faqiuAfter;
    }
}
