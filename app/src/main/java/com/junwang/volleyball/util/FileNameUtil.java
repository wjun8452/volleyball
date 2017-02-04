package com.junwang.volleyball.util;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by junwang on 2017/2/3.
 */

public class FileNameUtil {

    public static File createLocalStatFile(boolean court, Context context, String id) {
        File root = context.getFilesDir();
        return new File(root.getAbsolutePath() + "/" + getStatRelativePath(court, id));
    }

    public static String getStatRelativePath(boolean court, String id) {
        return (court ? "stats/" : "players/") + id;
    }

    public static String getUnsyncStatDir() {
        return "unsync/stats";
    }

    public static String getUnsyncPlayerDir() {
        return "unsync/players";
    }

    public static String getStatsLocalDir(boolean court, Context context) {
        File root = context.getFilesDir();
        return root.getAbsolutePath() + "/" + (court ? "stats" : "players");
    }

    public static void createDownloadPath(Context context) {
        File f = new File(getDownloadPath(context));
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public static void removeDownloadFiles(Context context) {
        File f = new File(getDownloadPath(context));
        for (File t : f.listFiles()) {
            if (t.isFile()) {
                t.delete();
            }
        }
    }

    public static String getDownloadPath(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + "download";
        return path;
    }

    public static String getDownloadFullPath(Context context, String id) {
        return getDownloadPath(context) + "/" + id;
    }

    public static void moveDownloadedFiles(boolean court, Context context) {
        File src = new File(getDownloadPath(context));
        for (File srcfile : src.listFiles()) {
            if (srcfile.isFile()) {
                File dest = new File(getStatsLocalDir(court, context) + "/" + srcfile.getName());
                try {
                    FileUtils.copyFile(srcfile, dest);
                } catch (IOException e) {
                    Log.e("wangjun", "copy fail, src: " + srcfile.getAbsolutePath() + "; dest: " + dest.getAbsolutePath(), e);
                }
            }
        }
    }
}
