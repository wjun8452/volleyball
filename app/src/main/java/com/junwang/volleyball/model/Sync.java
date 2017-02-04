package com.junwang.volleyball.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.junwang.volleyball.cos.CosHelper;
import com.junwang.volleyball.util.FileNameUtil;
import com.tencent.cos.utils.SHA1Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by junwang on 2017/2/3.
 */

public class Sync {
    private Context context;  //todo: release context
    private Thread workThread;
    private Set<String> unsyncedAdd = new HashSet<>();
    private Set<String> unsyncedDelete = new HashSet<>();
    private Set<String> localCache = new HashSet<>();
    private final Object syncObj = new Object();
    final private String SYNC_DELETE = "unsyncDelete.json";
    final private String SYNC_ADD = "unsyncAdd.json";
    final private String LOCAL_CACHE = "local.json";


    private Thread workThreadPlayer;
    private Set<String> unsyncedAddPlayer = new HashSet<>();
    private Set<String> unsyncedDeletePlayer = new HashSet<>();
    private final Object syncObjPlayer = new Object();
    final private String SYNC_DELETE_PLAYER = "unsyncDeletePlayer.json";
    final private String SYNC_ADD_PLAYER  = "unsyncAddPlayer.json";


    public Sync(Context context) {
        this.context = context;
        loadCourts();
        loadPlayers();
    }

    public interface SyncCourtsListener {
        void done();
    }

    public interface SyncPlayersListener {
        void done();
    }

    static private Sync sync;

    static public Sync getInstance(Context context) {
        if (sync == null) {
            sync = new Sync(context);
        }
        return sync;
    }

    public Set<String> getLocalCache() {
        return localCache;
    }

    public Set<String> getUnsyncedAddPlayer() {
        return unsyncedAddPlayer;
    }

    public void setUnsyncedAddPlayer(Set<String> unsyncedAddPlayer) {
        this.unsyncedAddPlayer = unsyncedAddPlayer;
    }

    public Set<String> getUnsyncedDeletePlayer() {
        return unsyncedDeletePlayer;
    }

    public Set<String> getUnsyncedAdd() {
        return unsyncedAdd;
    }

    public Set<String> getUnsyncedDelete() {
        return unsyncedDelete;
    }

    public void saveTempCourts() {
        File temp = new File(context.getFilesDir().getAbsolutePath() + "/" + LOCAL_CACHE);
        Gson gson = new Gson();
        String json = gson.toJson(localCache);
        saveToFile(temp, json);
    }

    public void saveUnsyncCourts() {
        File unAdd = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_ADD);
        Gson gson = new Gson();
        String json = gson.toJson(unsyncedAdd);
        saveToFile(unAdd, json);


        File unDelete = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_DELETE);
        gson = new Gson();
        json = gson.toJson(unsyncedDelete);
        saveToFile(unDelete, json);
    }

    public void saveUnsyncPlayers() {
        File unAdd = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_ADD_PLAYER);
        Gson gson = new Gson();
        String json = gson.toJson(unsyncedAddPlayer);
        saveToFile(unAdd, json);


        File unDelete = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_DELETE_PLAYER);
        gson = new Gson();
        json = gson.toJson(unsyncedDeletePlayer);
        saveToFile(unDelete, json);
    }

    private void loadCourts() {
        File unAdd = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_ADD);
        if (unAdd.exists()) {
            String json = readFromFile(unAdd);
            Log.i("wangjun_unsyncAdd", json);
            Gson gson = new Gson();
            unsyncedAdd = gson.fromJson(json, new TypeToken<HashSet<String>>() {
            }.getType());
        }

        File unDelete = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_DELETE);
        if (unDelete.exists()) {
            String json = readFromFile(unDelete);
            Log.i("wangjun_unsyncDelete", json);
            Gson gson = new Gson();
            unsyncedDelete = gson.fromJson(json, new TypeToken<HashSet<String>>() {
            }.getType());
        }

        File local = new File(context.getFilesDir().getAbsolutePath() + "/" + LOCAL_CACHE);
        if (local.exists()) {
            String json = readFromFile(local);
            Log.i("wangjun_local", json);
            Gson gson = new Gson();
            localCache = gson.fromJson(json, new TypeToken<HashSet<String>>() {
            }.getType());
        }
    }

    private void loadPlayers() {
        File unAdd = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_ADD_PLAYER);
        if (unAdd.exists()) {
            String json = readFromFile(unAdd);
            Log.i("wangjun_unsyncAdd", json);
            Gson gson = new Gson();
            unsyncedAddPlayer = gson.fromJson(json, new TypeToken<HashSet<String>>() {
            }.getType());
        }

        File unDelete = new File(context.getFilesDir().getAbsolutePath() + "/" + SYNC_DELETE_PLAYER);
        if (unDelete.exists()) {
            String json = readFromFile(unDelete);
            Log.i("wangjun_unsyncDelete", json);
            Gson gson = new Gson();
            unsyncedDeletePlayer = gson.fromJson(json, new TypeToken<HashSet<String>>() {
            }.getType());
        }
    }

    private void saveToFile(File file, String json) {
        try {
            synchronized (this) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(json.getBytes());
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] all = new byte[inputStream.available()];
            inputStream.read(all);
            inputStream.close();
            return new String(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startSyncCourts(final SyncCourtsListener listener) {
        if (workThread != null && workThread.isAlive()) {
            try {
                Log.i("wangjun", "previous sync not finish, wait ...");
                workThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        workThread = new Thread(new Runnable() {
            @Override
            public void run() {
                syncCourts(true);
                listener.done();
            }
        });

        workThread.start();
    }


    public void startSyncPlayers(final SyncPlayersListener listener) {
        if (workThreadPlayer != null && workThreadPlayer.isAlive()) {
            try {
                Log.i("wangjun", "previous sync player not finish, wait ...");
                workThreadPlayer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        workThreadPlayer = new Thread(new Runnable() {
            @Override
            public void run() {
                syncCourts(false);
                listener.done();
            }
        });

        workThreadPlayer.start();
    }

    private void syncCourts(final boolean court) {
        synchronized (syncObj) {
            Log.i("wangjun", "sync is starting ...");

            CosHelper.newInstance(context).listDir(court? CosHelper.FileType.Stat : CosHelper.FileType.Player, new CosHelper.ListFilesListener() {

                @Override
                public void listSucceed(final Map<String, String> serverFiles) {
                    Log.i("wangjun", "find remote court total : " + serverFiles.size());

                    Set<String> localFiles = ModelRepoFactory.getModelRepo().listLocalCouts(court, context);
                    Log.i("wangjun", "find local court total : " + localFiles.size());


                    final Set<String> unsyncAdd = court ? unsyncedAdd : unsyncedAddPlayer;

                    //files are deleted on server, delete them locally
                    Log.i("wangjun", "delete local files");
                    for (String toDelete : localFiles) {
                        if (localCache.contains(toDelete)) {
                            continue; //ignore the local temporairy files
                        }

                        if ((!serverFiles.containsKey(toDelete)) && (!unsyncAdd.contains(toDelete))) {
                            if (FileNameUtil.createLocalStatFile(court, context, toDelete).delete()) {
                                Log.i("wangjun", "delete local file: " + toDelete);
                            } else {
                                Log.e("wangjun", "delete local file failed!");
                            }
                        }
                    }

                    //files are deleted locally, delete them on server too
                    final Set<String> unsyncDelete = court ? unsyncedDelete : unsyncedDeletePlayer;
                    Iterator<String> it = unsyncDelete.iterator();
                    while(it.hasNext()) {
                        String toDelelte = it.next();
                        if (!serverFiles.containsKey(toDelelte)) {
                            it.remove();
                        }
                    }
                    Log.i("wangjun", "delete server files: " + unsyncDelete.size());
                    if (!unsyncDelete.isEmpty()) {
                        final Object lock = new Object();
                        synchronized (lock) {
                            final AtomicInteger countdown = new AtomicInteger(unsyncDelete.size());
                            //deal with unsynced delete
                            for (final String toDelete : unsyncDelete) {

                                CosHelper.newInstance(context).deleteFile(court ? CosHelper.FileType.Stat: CosHelper.FileType.Player, toDelete, new CosHelper.DeleteFileListener() {
                                    @Override
                                    public void deleteSucceed() {
                                        Log.i("wangjun", "delete remote succeed, " + toDelete);
                                        unsyncedDelete.remove(toDelete);

                                        synchronized (lock) {
                                            int i = countdown.decrementAndGet();
                                            if (i <=0 ) {
                                                lock.notifyAll();
                                            }
                                        }
                                    }

                                    @Override
                                    public void deleteFail(int code) {
                                        if (code == -197) { //remote file not exit
                                            Log.i("wangjun", "delete but remote file not exist, " + toDelete);
                                            unsyncedDelete.remove(toDelete);
                                        } else {
                                            Log.i("wangjun", "delete remote fail, " + toDelete);
                                        }

                                        synchronized (lock) {
                                            int i = countdown.decrementAndGet();
                                            if (i <=0 ) {
                                                lock.notifyAll();
                                            }
                                        }
                                    }
                                });
                            }

                            try {
                                Log.i("wangjun", "wait...");
                                lock.wait();
                                Log.i("wangjun", "delete server files done");
                            } catch (InterruptedException e) {
                                Log.e("wangjun", "wait", e);
                            }
                        }
                    }

                    if (court) {
                        saveUnsyncCourts();
                    } else {
                        saveUnsyncPlayers();
                    }

                    //only download changed files, local new added files do not need to download
                    Set<String> toDownload = new HashSet<String>();
                    for (Map.Entry<String, String> entry : serverFiles.entrySet()) {
                        String serverFile = entry.getKey();
                        String sha1ServerFile = entry.getValue();

                        if (unsyncAdd.contains(serverFile)) {
                            continue;
                        }

                        if (unsyncDelete.contains(serverFile)) {
                            continue;
                        }

                        if (localFiles.contains(serverFile)) {
                            String localSha1 = SHA1Utils.getFileSha1(FileNameUtil.createLocalStatFile(court, context, serverFile).getAbsolutePath());
                            if (!localSha1.equals(sha1ServerFile)) {
                                toDownload.add(serverFile);
                            }
                        } else {
                            toDownload.add(serverFile);
                        }
                    }

                    Log.i("wangjun", "download server files, count " + toDownload.size());
                    if (!toDownload.isEmpty()) {
                        //empty download folder
                        FileNameUtil.createDownloadPath(context);
                        FileNameUtil.removeDownloadFiles(context);

                        final Object lock = new Object();
                        synchronized (lock) {
                            final AtomicInteger coutdown = new AtomicInteger(toDownload.size());
                            for (String remoteName : toDownload) {
                                CosHelper.newInstance(context).downloadFile(court ? CosHelper.FileType.Stat : CosHelper.FileType.Player,
                                        FileNameUtil.getDownloadPath(context), remoteName, new CosHelper.DownloadFileListener() {
                                            @Override
                                            public void downloadSucceed(String localPath) {

                                                int i = coutdown.decrementAndGet();
                                                if (i <= 0) {
                                                    synchronized (lock) {
                                                        lock.notifyAll();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void downloadFail() {
                                                int i = coutdown.decrementAndGet();
                                                if (i <= 0) {
                                                    synchronized (lock) {
                                                        lock.notifyAll();
                                                    }
                                                }
                                            }
                                        });
                            }

                            try {
                                Log.i("wangjun", "wait...");
                                lock.wait();
                                Log.i("wangjun", "download server files, done");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //move downloaded files to local cache
                            FileNameUtil.moveDownloadedFiles(court, context);
                        }
                    }

                    //which files need to upload
                    Iterator<String> itMap = unsyncAdd.iterator();
                    while(itMap.hasNext()) {
                        String unsyncAddFile = itMap.next();
                        if (serverFiles.containsKey(unsyncAddFile)) {
                            String serverSha1 = serverFiles.get(unsyncAddFile);
                            String localSha1 = SHA1Utils.getFileSha1(FileNameUtil.createLocalStatFile(court, context, unsyncAddFile).getAbsolutePath());
                            if (serverSha1!=null && localSha1!=null && serverSha1.equals(localSha1)) {
                                Log.i("wangjun", unsyncAddFile + " has a same copy on server, no need to upload");
                                itMap.remove();
                            }
                        }
                    }

                    Log.i("wangjun", "upload files to server, count " + unsyncAdd.size());
                    if (!unsyncAdd.isEmpty()) {

                        final Object lock = new Object();
                        synchronized (lock) {

                            final AtomicInteger countdown = new AtomicInteger(unsyncAdd.size());

                            for (final String toAdd : unsyncAdd) {
                                CosHelper.newInstance(context).uploadFile(court ? CosHelper.FileType.Stat : CosHelper.FileType.Player,
                                        FileNameUtil.createLocalStatFile(court, context, toAdd),
                                        new CosHelper.UploadFileListener() {
                                            @Override
                                            public void uploadSucceed() {
                                                Log.i("wangjun", "upload remote succeed, " + toAdd);
                                                unsyncedAdd.remove(toAdd);

                                                int i = countdown.decrementAndGet();
                                                if (i <= 0) {
                                                    synchronized (lock) {
                                                        lock.notifyAll();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void uploadFail() {
                                                int i = countdown.decrementAndGet();
                                                if (i <= 0) {
                                                    synchronized (lock) {
                                                        lock.notifyAll();
                                                    }
                                                }
                                                Log.i("wangjun", "upload remote fail, " + toAdd);
                                            }
                                        });
                            }

                            try {
                                Log.i("wangjun", "wait ...");
                                lock.wait();
                                Log.i("wangjun", "upload files to server, done");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (court) {
                            saveUnsyncCourts();
                        } else {
                            saveUnsyncPlayers();
                        }
                    }


                    //notify outer
                    synchronized (syncObj) {
                        syncObj.notifyAll();
                    }
                }

                @Override
                public void listFail() {
                    Log.i("wangjun", "list remote courts fail");
                    synchronized (syncObj) {
                        syncObj.notifyAll();
                    }
                }
            });


            Log.i("wangjun", "sync is scheduled, wait ...");
            try {
                syncObj.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i("wangjun", "sync is finished");
        }
    }
}
