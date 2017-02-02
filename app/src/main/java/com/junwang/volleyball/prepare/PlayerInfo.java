package com.junwang.volleyball.prepare;

import com.junwang.volleyball.model.Player;

/**
 * Created by junwang on 31/01/2017.
 */

public class PlayerInfo {
    private Player player;
    private boolean selected;

    public PlayerInfo(Player player, boolean selected) {
        this.player = player;
        this.selected = selected;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
