package com.junwang.volleyball.model;

import android.content.Context;

import java.util.List;

/**
 * Created by junwang on 28/01/2017.
 */

public interface ModelRepository {
    void saveCourt(Context context,  Court court);
    List<Court> loadCourt(Context context);
    Court newCourt(Context context);
    Court findCourt(Context context, String id);
    void deleteCourt(Context context, String id);

    List<Player> loadPlayers(Context context);
    void deletePlayer(Context context, String id);
    void savePlayer(Context context, Player player);
}
