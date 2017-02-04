package com.junwang.volleyball.model;

import android.content.Context;

import java.util.List;
import java.util.Set;

/**
 * Created by junwang on 28/01/2017.
 */

public interface ModelRepository {

    //todo how to use T
    interface LoadCourtCallback {
        void onCourtLoaded(List<Court> courts);
    }

    interface LoadPlayerCallback {
        void onPlayerLoaded(List<Player> players);
    }

    void init(Context context);

    void saveCourt(Context context,  Court court);
    void markSyncAdded(Context context, Court court);
    void loadCourt(boolean fetchServer, Context context, LoadCourtCallback courtCallback);
    Court newCourt(Context context);
    Court findCourt(Context context, String id);
    void deleteCourt(Context context, String id);
    Set<String> listLocalCouts(boolean court, Context context);

    List<Player> loadLocalPlayers(Context context);
    void loadPlayers(boolean fetchServer, Context context, LoadPlayerCallback playerCallback);
    void deletePlayer(Context context, String id);
    void savePlayer(Context context, Player player);
    void markSyncAdded(Context context, Player player);
}
