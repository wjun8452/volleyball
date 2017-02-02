package com.junwang.volleyball.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junwang on 28/01/2017.
 */

public class ModelRepoImpl implements ModelRepository {
    private Map<String, Court> courtMap = new HashMap<>();
    private Map<String, Player> playerMap = new HashMap<>();

    @Override
    public void saveCourt(Context context, Court court) {
        boolean saved = true;
        try {
            FileOutputStream outputStream = context.openFileOutput(getCourtFileName(court.getId()), Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = gson.toJson(court);
            Log.i("wangjun-out", json);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            saved = false;
        } catch (IOException e) {
            e.printStackTrace();
            saved = false;
        } finally {

        }

        if (saved) {
            //if (courtMap.containsKey(court.getId())) {
                courtMap.put(court.getId(), court);
            //}
        }
    }

    @Override
    public List<Court> loadCourt(Context context) {
        List<Court> courtList = new ArrayList<>();
        if (courtMap.size() != 0) {
            for (Court court : courtMap.values()) {
                courtList.add(court);
            }
        } else {

            List<String> courts = new ArrayList<>();
            File file = context.getFilesDir();
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    String extend = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                    if (extend.equals("court")) {
                        courts.add(f.getName());
                    }
                }
            }

            for (String filename : courts) {
                try {
                    FileInputStream inputStream = context.openFileInput(filename);
                    byte[] all = new byte[inputStream.available()];
                    inputStream.read(all);
                    inputStream.close();
                    String json = new String(all);
                    Log.i("wangjun-read", json);
                    Gson gson = new Gson();
                    Court court = gson.fromJson(json, VolCourt.class);
                    courtList.add(court);
                    courtMap.put(court.getId(), court);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        sortCourt(courtList);
        return courtList;
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
        if (context.deleteFile(getCourtFileName(id))) {
            courtMap.remove(id);
        }
    }

    @Override
    public List<Player> loadPlayers(Context context) {
        List<Player> playerList = new ArrayList<>();
        if (playerMap.size() != 0) {//use cached
            for (Player player : playerMap.values()) {
                playerList.add(player);
            }
        } else {

            List<String> players = new ArrayList<>();
            File file = context.getFilesDir();
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    String extend = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                    if (extend.equals("player")) {
                        players.add(f.getName());
                    }
                }
            }

            for (String filename : players) {
                try {
                    FileInputStream inputStream = context.openFileInput(filename);
                    byte[] all = new byte[inputStream.available()];
                    inputStream.read(all);
                    inputStream.close();
                    String json = new String(all);
                    Gson gson = new Gson();
                    Player player = gson.fromJson(json, Player.class);
                    playerList.add(player);
                    playerMap.put(player.getName(), player);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        sortPlayer(playerList);
        return playerList;
    }

    @Override
    public void deletePlayer(Context context, String id) {
        if (context.deleteFile(getPlayerFileName(id))) {
            playerMap.remove(id);
        }
    }

    @Override
    public void savePlayer(Context context, Player player) {
        boolean saved = true;
        try {
            FileOutputStream outputStream = context.openFileOutput(getPlayerFileName(player.getName()), Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = gson.toJson(player);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            saved = false;
        } catch (IOException e) {
            e.printStackTrace();
            saved = false;
        } finally {
        }

        if (saved) {
            playerMap.put(player.getName(), player);
        }
    }

    private String getCourtFileName(String id) {
        return id + ".court";
    }

    private String getPlayerFileName(String id) {
        return id + ".player";
    }

}
