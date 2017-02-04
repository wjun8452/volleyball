package com.junwang.volleyball.cos;

import android.content.Context;
import android.util.Log;

import com.tencent.cos.COSClient;
import com.tencent.cos.COSClientConfig;
import com.tencent.cos.common.COSEndPoint;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.DeleteObjectRequest;
import com.tencent.cos.model.GetObjectRequest;
import com.tencent.cos.model.GetObjectResult;
import com.tencent.cos.model.ListDirRequest;
import com.tencent.cos.model.ListDirResult;
import com.tencent.cos.model.PutObjectRequest;
import com.tencent.cos.model.PutObjectResult;
import com.tencent.cos.task.listener.ICmdTaskListener;
import com.tencent.cos.task.listener.IDownloadTaskListener;
import com.tencent.cos.task.listener.IUploadTaskListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by junwang on 2017/2/2.
 */

public class CosHelper {
    private static CosHelper helper;
    private Context context;
    private final Long appid = 1253317970L;
    private COSClient cos;
    private final String bucket = "volleyball";
    private final String root = "/shared";
    private final String path_stats = "stats";
    private final String path_palyers = "players";
    private final String secretId = "AKIDNezAB1s7NQHvOfojecO1TKDvV6g4kI2k";
    private final String secretKey = "1ucBYjhiZEh0BAxYxAAF6rQ0Y6Zu41mM";
    private Credentials credentials = new Credentials(appid, secretId, secretKey);

    public enum FileType {
        Stat,
        Player
    }

    public static CosHelper newInstance(Context context) {
        if (helper == null) {
            helper = new CosHelper(context);
        }
        return helper;
    }

    public interface ListFilesListener {
        void listSucceed(Map<String, String> files);
        void listFail();
    }

    public interface DownloadFileListener {
        void downloadSucceed(String localPath);
        void downloadFail();
    }

    public interface UploadFileListener {
        void uploadSucceed();
        void uploadFail();
    }

    public interface DeleteFileListener {
        void deleteSucceed();
        void deleteFail(int code);
    }

    public CosHelper(Context context) {
        this.context = context;
        COSClientConfig config = new COSClientConfig();
        config.setEndPoint(COSEndPoint.COS_SH);
        //config.setConnectionTimeout(30 * 1000);
        //config.setSocketTimeout(5 * 1000);
        config.setMaxConnectionsCount(5);
        config.setMaxRetryCount(3);

        String peristenceId = null;
        cos = new COSClient(context, appid.toString(), config, peristenceId);
    }

    public void downloadFile(final FileType fileType, final String localDir, final String remotefileName, final DownloadFileListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String cosPath = root + "/" + (fileType.equals(FileType.Stat) ? path_stats : path_palyers) + "/" + remotefileName;
                String downloadURl = "http://" + bucket + "-" + appid + ".cossh.myqcloud.com/" + cosPath;
                Credentials credentials = new Credentials(appid, secretId, secretKey);
                String sign = null;
                try {
                    sign = Sign.getPeriodEffectiveSign(bucket, cosPath, credentials, System.currentTimeMillis() / 1000 + 3600);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("wangjun", "download: " + downloadURl);

                GetObjectRequest getObjectRequest = new GetObjectRequest(downloadURl, localDir);
                getObjectRequest.setSign(sign);
                getObjectRequest.setListener(new IDownloadTaskListener() {
                    @Override
                    public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
                        float progress = currentSize / (float) totalSize;
                        progress = progress * 100;
                        Log.w("wangjun", "download progress =" + (int) (progress) + "%");
                    }

                    @Override
                    public void onCancel(COSRequest cosRequest, COSResult cosResult) {
                        Log.w("wangjun", "onCancel");
                    }

                    @Override
                    public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
                        Log.w("wangjun", "download code =" + cosResult.code + "; msg =" + cosResult.msg + ", url: " + cosRequest.getDownloadUrl());
                        listener.downloadSucceed(cosRequest.getDownloadPath());
                    }

                    @Override
                    public void onFailed(COSRequest cosRequest, COSResult cosResult) {
                        Log.w("wangjun", "download code =" + cosResult.code + "; msg =" + cosResult.msg + ", url: " + cosRequest.getDownloadUrl());
                        listener.downloadFail();
                    }
                });

                GetObjectResult getObjectResult = cos.getObject(getObjectRequest);
            }
        }).start();

    }

    public void uploadFile(final FileType fileType, final File srcFile, final UploadFileListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cosPath = root + "/" + (fileType.equals(FileType.Stat) ? path_stats : path_palyers) + "/" + srcFile.getName();

                String sign = null;
                try {
                    sign = Sign.getPeriodEffectiveSign(bucket, cosPath, credentials, System.currentTimeMillis() / 1000 + 3600);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PutObjectRequest putObjectRequest = new PutObjectRequest();
                putObjectRequest.setBucket(bucket);
                putObjectRequest.setCosPath(cosPath);
                putObjectRequest.setSrcPath(srcFile.getAbsolutePath());
                putObjectRequest.setSign(sign);
                putObjectRequest.setInsertOnly("1"); //do not overwrite server file
                putObjectRequest.setListener(new IUploadTaskListener() {
                    @Override
                    public void onSuccess(COSRequest cosRequest, COSResult cosResult) {

                        PutObjectResult result = (PutObjectResult) cosResult;
                        if (result != null) {
                            Log.w("wangjun", "upload code=" + result.code + "; msg="+result.msg + "; path=" + result.resource_path + "; url=" + result.url);
                        }

                        listener.uploadSucceed();
                    }

                    @Override
                    public void onFailed(COSRequest cosRequest, final COSResult cosResult) {
                        Log.w("wangjun", "upload code=" + cosResult.code + "; msg =" + cosResult.msg + "; path=" + cosRequest.getCosPath() + "; url=" + cosRequest.getDownloadUrl());
                        listener.uploadFail();
                    }

                    @Override
                    public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
                        float progress = (float) currentSize / totalSize;
                        progress = progress * 100;
                        Log.w("wangjun", "进度：  " + (int) progress + "%");
                    }

                    @Override
                    public void onCancel(COSRequest cosRequest, COSResult cosResult) {
                        Log.w("wangjun", "onCancel");
                        listener.uploadFail();
                    }
                });
                PutObjectResult putObjectResult = cos.putObject(putObjectRequest);
            }
        }).start();
    }

    public void listDir(final FileType fileType, final ListFilesListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("wangjun", "list dir ...");

                String cosPath = root + "/" + (fileType.equals(FileType.Stat) ? path_stats : path_palyers);
                String sign = null;
                try {
                    sign = Sign.getPeriodEffectiveSign(bucket, cosPath, credentials, System.currentTimeMillis() / 1000 + 3600);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final List<String> ok = new ArrayList<String>(); //todo: fix lower error

                final ListDirRequest listDirRequest = new ListDirRequest();
                listDirRequest.setBucket(bucket);
                listDirRequest.setCosPath(cosPath);
                listDirRequest.setNum(1000); //max is 1000
                listDirRequest.setContent("");
                listDirRequest.setSign(sign);
                listDirRequest.setListener(new ICmdTaskListener() {
                    @Override
                    public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
                        Map<String, String> listedFiles = new HashMap<String, String>();
                        ListDirResult listObjectResult = (ListDirResult) cosResult;
                        Log.i("wangjun", "list code="+listObjectResult.code + "; msg=" + listObjectResult.msg + "; path=" + cosRequest.getCosPath());
                        if (listObjectResult.infos != null && listObjectResult.infos.size() > 0) {
                            int length = listObjectResult.infos.size();
                            String str;
                            JSONObject jsonObject;
                            for (int i = 0; i < length; i++) {
                                str = listObjectResult.infos.get(i);
                                try {
                                    jsonObject = new JSONObject(str);
                                    if (jsonObject.has("sha")) {
                                        Log.i("wangjun", "file: " + jsonObject.optString("name"));
                                        listedFiles.put(jsonObject.optString("name"), jsonObject.optString("sha"));
                                    } else {
                                        Log.i("wangjun", "folder: " + jsonObject.optString("name"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }  else {
                            Log.i("wangjun", "list empty folder");
                        }
                        ok.add("ok");
                        listener.listSucceed(listedFiles);
                    }

                    @Override
                    public void onFailed(COSRequest cosRequest, COSResult cosResult) {
                        if (ok.isEmpty()) {
                            Log.e("wangjun", "list：code=" + cosResult.code + "; msg =" + cosResult.msg + "; path =" + cosRequest.getCosPath());
                            listener.listFail();
                        }
                    }
                });
                ListDirResult result = cos.listDir(listDirRequest);
            }
        }
        ).start();
    }

    public void deleteFile(final FileType fileType, final String fileName, final DeleteFileListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cosPath = root + "/" + (fileType.equals(FileType.Stat) ? path_stats : path_palyers) + "/" + fileName;
                String sign = null;
                try {
                    sign = Sign.getOneEffectiveSign(bucket, cosPath, credentials);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /**
                 * DeleteObjectRequest 请求对象
                 */
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest();
                /** 设置Bucket */
                deleteObjectRequest.setBucket(bucket);
                /** 设置cosPath :远程路径*/
                deleteObjectRequest.setCosPath(cosPath);
                /** 设置sign: 签名，此处使用单次签名 */
                deleteObjectRequest.setSign(sign);
                /** 设置listener: 结果回调 */
                deleteObjectRequest.setListener(new

                                                        ICmdTaskListener() {
                                                            @Override
                                                            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
                                                                Log.i("wangjun", "code =" + cosResult.code + "; msg =" + cosResult.msg);
                                                                listener.deleteSucceed();
                                                            }

                                                            @Override
                                                            public void onFailed(COSRequest cosRequest, COSResult cosResult) {
                                                                Log.i("wangjun", "code = " + cosResult.code + "; msg =" + cosResult.msg);
                                                                listener.deleteFail(cosResult.code);
                                                            }
                                                        }

                );

                cos.deleteObject(deleteObjectRequest);
            }
        }).start();

    }
}
