package com.junwang.volleyball.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.junwang.volleyball.util.FileNameUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by junwang on 28/01/2017.
 */

public class ModelRepoImpl implements ModelRepository {
    private Map<String, Court> courtMap = new HashMap<>();
    private Map<String, Player> playerMap = new HashMap<>();
    private Context context;

    public ModelRepoImpl() {
        this.context = context;
    }

    private void createStatsDirIfNeeded(boolean court, Context context) {
        String statsDir = FileNameUtil.getStatsLocalDir(court, context);
        File file = new File(statsDir);
        if (!file.exists()) {
            file.mkdir();
        }
    }


    @Override
    public void init(Context context) {
        createStatsDirIfNeeded(true, context);
        createStatsDirIfNeeded(false, context);
    }

    @Override
    public void saveCourt(Context context, Court court) {
        boolean saved = true;
        FileOutputStream outputStream = null;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(court);
            Log.i("wangjun", "save to file: " + json);

            synchronized (this) {
                outputStream = new FileOutputStream(FileNameUtil.createLocalStatFile(true, context, court.getId()));//context.openFileOutput(FileNameUtil.getStatRelativePath(court.getId()), Context.MODE_PRIVATE);outputStream.write(json.getBytes());
                outputStream.write(json.getBytes());
                outputStream.close();
            }

        } catch (FileNotFoundException e) {
            Log.e("wangjun", "save file fail", e);
            saved = false;
        } catch (IOException e) {
            Log.e("wangjun", "save file fail", e);
            saved = false;
        } catch (Exception e) {
            Log.e("wangjun", "save file fail", e);
            saved = false;
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("wangjun", "save to file done");
        }

        if (saved) {
            courtMap.put(court.getId(), court);
        }
    }

    @Override
    public void markSyncAdded(Context context, Court court) {
        Sync.getInstance(context).getUnsyncedDelete().remove(court.getId());
        Sync.getInstance(context).getUnsyncedAdd().add(court.getId());
        Sync.getInstance(context).saveUnsyncCourts();
    }

    public List<Court> loadLocalCourts(Context context) {

        List<com.junwang.volleyball.model.Court> courtList = new ArrayList<>();
        if (courtMap.size() != 0) {
            for (com.junwang.volleyball.model.Court court : courtMap.values()) {
                courtList.add(court);
            }

        } else {
            Set<String> localCouts = listLocalCouts(true, context);
            Log.i("wangjun", "load courts from local, total:  " + localCouts.size());
            for (String filename : localCouts) {
                try {
                    Log.i("wangjun", filename);
                    FileInputStream inputStream = new FileInputStream(new File(FileNameUtil.getStatsLocalDir(true, context) + "/" + filename));
                    byte[] all = new byte[inputStream.available()];
                    inputStream.read(all);
                    inputStream.close();
                    String json = new String(all);
                    Log.d("wangjun", json);
                    Gson gson = new Gson();
                    com.junwang.volleyball.model.Court court = gson.fromJson(json, VolCourt.class);
                    if (court != null) {
                        courtList.add(court);
                        courtMap.put(court.getId(), court);
                    }
                } catch (FileNotFoundException e) {
                    Log.e("wangjun", filename, e);
                } catch (IOException e) {
                    Log.e("wangjun", filename, e);
                } catch (Exception e) {
                    Log.e("wangjun", filename, e);
                }
            }
        }

        sortCourt(courtList);

        return courtList;
    }

    @Override
    public void loadCourt(boolean fetechServer, final Context context, final LoadCourtCallback courtCallback) {

        if (fetechServer) {
            Sync.getInstance(context).startSyncCourts(new Sync.SyncCourtsListener() {
                @Override
                public void done() {
                    courtMap.clear();
                    courtCallback.onCourtLoaded(loadLocalCourts(context));
                }
            });
        } else {
            courtCallback.onCourtLoaded(loadLocalCourts(context));
        }
    }

    private void sortCourt(List<Court> courts) {
        ComparatorCourt comparatorCourt = new ComparatorCourt();
        Collections.sort(courts, comparatorCourt);
    }

    private void sortPlayer(List<Player> players) {
        ComparatorPlayer comparatorCourt = new ComparatorPlayer();
        Collections.sort(players, comparatorCourt);
    }

    @Override
    public Court newCourt(Context context) {
        Court court = new VolCourt();
        saveCourt(context, court);
        return court;
    }

    @Override
    public Court findCourt(Context context, String id) {
        return courtMap.get(id);
    }

    @Override
    public void deleteCourt(Context context, String id) {
        if (FileNameUtil.createLocalStatFile(true, context, id).delete()) {
            Log.i("wangjun", "delete court and save to unsynced, court= " + id);
            courtMap.remove(id);
            Sync.getInstance(context).getUnsyncedAdd().remove(id);
            Sync.getInstance(context).getUnsyncedDelete().add(id);
            Sync.getInstance(context).saveUnsyncCourts();
        } else {
            Log.i("wangjun", "delete local court fail. court=" + id);
        }
    }

    @Override
    public Set<String> listLocalCouts(boolean court, Context context) {
        Set<String> courts = new HashSet<>();
        File file = new File(FileNameUtil.getStatsLocalDir(court, context));
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                courts.add(f.getName());
            }
        }
        return courts;
    }


    @Override
    public void loadPlayers(boolean fetchServer, final Context context, final LoadPlayerCallback playerCallback) {
        if (fetchServer) {
            Sync.getInstance(context).startSyncPlayers(new Sync.SyncPlayersListener() {
                @Override
                public void done() {
                    playerMap.clear();
                    playerCallback.onPlayerLoaded(loadLocalPlayers(context));
                }
            });
        } else {
            playerCallback.onPlayerLoaded(loadLocalPlayers(context));
        }
    }

    @Override
    public List<Player> loadLocalPlayers(Context context) {
        List<Player> playerList = new ArrayList<>();
        if (playerMap.size() != 0) {//use cached
            Log.i("wangjun", "load memory players, count: " + playerMap.size());
            for (Player player : playerMap.values()) {
                playerList.add(player);
            }
        } else {
            Set<String> players = listLocalCouts(false, context);
            Log.i("wangjun", "load local players, count: " + players.size());
            for (String filename : players) {
                try {
                    FileInputStream inputStream = new FileInputStream(new File(FileNameUtil.getStatsLocalDir(false, context) + "/" + filename));
                    byte[] all = new byte[inputStream.available()];
                    inputStream.read(all);
                    inputStream.close();
                    String json = new String(all);

                    Log.i("wangjun", json);

                    Gson gson = new Gson();

                    Player player = gson.fromJson(json, Player.class);

                    if (player != null) {
                        playerList.add(player);
                        playerMap.put(player.getName(), player);
                    }

                } catch (FileNotFoundException e) {
                    Log.e("wangjun", "", e);
                } catch (IOException e) {
                    Log.e("wangjun", "", e);
                } catch (Exception e) {
                    Log.e("wangjun", "", e);
                }
            }
        }
        sortPlayer(playerList);
        return playerList;
    }

    @Override
    public void deletePlayer(Context context, String id) {
        File file = FileNameUtil.createLocalStatFile(false, context, id);
        if (file.delete()) {
            Log.i("wangjun", "delete local player and save to unsynced, player= " + id);
            playerMap.remove(id);
            Sync.getInstance(context).getUnsyncedDeletePlayer().add(id);
            Sync.getInstance(context).getUnsyncedAddPlayer().remove(id);
            Sync.getInstance(context).saveUnsyncPlayers();
        } else {
            Log.i("wangjun", "delete local player failed, player= " + id);
        }
    }

    @Override
    public void savePlayer(Context context, Player player) {
        boolean saved = true;
        try {
            FileOutputStream outputStream = new FileOutputStream(FileNameUtil.createLocalStatFile(false, context, player.getName()));
            Gson gson = new Gson();
            String json = gson.toJson(player);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("wangjun", "", e);
            saved = false;
        } catch (IOException e) {
            Log.e("wangjun", "", e);
            saved = false;
        } catch (Exception e) {
            Log.e("wangjun", "", e);
            saved = false;
        }

        if (saved) {
            playerMap.put(player.getName(), player);
        }
    }

    @Override
    public void markSyncAdded(Context context, Player player) {
        Sync.getInstance(context).getUnsyncedAddPlayer().add(player.getName());
        Sync.getInstance(context).getUnsyncedDeletePlayer().remove(player.getName());
        Sync.getInstance(context).saveUnsyncPlayers();

    }
}
