package com.luck.pictureselector;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * Created by daoyou.huo on 2023/3/2.
 * Description:
 */
public class SaveFile2Gallery {

    /**
     * 将图片及视频文件保存到系统相册里 sdcard里路径为  /Environment.DIRECTORY_PICTURES/DevPics/ xxx
     * @param context
     * @param isVideo
     * @param filePath 文件sdcard中的路径
     * @param fileName 文件名称， 带后缀的
     */
    public static void saveFile2Gallery(Context context, boolean isVideo, String filePath, String fileName) {
        String folderName = "";
        String parentPath="";
        if (isVideo) {
            //folderName = Environment.DIRECTORY_DCIM + "/" + context.getString(R.string.android_app_name);
            parentPath="/" + "DevPics/devVideo";
            folderName = Environment.DIRECTORY_PICTURES + parentPath;
        } else {
           // folderName = Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.android_app_name);
            parentPath="/" + "DevPics/devImage";
            folderName = Environment.DIRECTORY_PICTURES + parentPath;
        }
        Uri uriSavedFile;
        File createdFile = null;
        ContentResolver resolver = context.getContentResolver();
        ContentValues valuesFile;
        valuesFile = new ContentValues();

        if (Build.VERSION.SDK_INT >= 29) {
            valuesFile.put(MediaStore.Video.Media.RELATIVE_PATH, folderName);
            valuesFile.put(MediaStore.Video.Media.TITLE, fileName);
            valuesFile.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            valuesFile.put(MediaStore.Video.Media.MIME_TYPE, getMimeType(fileName));
            valuesFile.put( MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

            Uri collection = isVideo ? MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) :
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            uriSavedFile = resolver.insert(collection, valuesFile);
        } else {
            File directory = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES);
            //File directory = Environment.getExternalStoragePublicDirectory(isVideo ? Environment.DIRECTORY_DCIM : Environment.DIRECTORY_PICTURES);
            File parent = new File(directory.getAbsolutePath() + "/" + parentPath);
            if (!parent.exists()) {
                parent.mkdir();
            }
            createdFile = new File(parent, fileName);

            valuesFile.put(MediaStore.Video.Media.TITLE, fileName);
            valuesFile.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            valuesFile.put(MediaStore.Video.Media.MIME_TYPE,getMimeType(fileName));
            valuesFile.put(
                    MediaStore.Video.Media.DATE_ADDED,
                    System.currentTimeMillis() / 1000);
            valuesFile.put(MediaStore.Video.Media.DATA, createdFile.getAbsolutePath());

            if (isVideo) {
                uriSavedFile = context.getContentResolver().insert(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        valuesFile);
            } else {
                uriSavedFile = context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        valuesFile);
            }
        }

        if (Build.VERSION.SDK_INT >= 29) {
            valuesFile.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            valuesFile.put(MediaStore.Video.Media.IS_PENDING, 1);
        }

        try {
            if (isVideo) {
                ParcelFileDescriptor pfd;
                pfd = context.getContentResolver().openFileDescriptor(uriSavedFile, "w");

                FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
                // get the already saved video as fileinputstream
                // The Directory where your file is saved
                // File storageDir = new File(uri.getPath());
                //Directory and the name of your video file to copy
                File videoFile = new File(filePath);
                FileInputStream in = new FileInputStream(videoFile);
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                pfd.close();
            } else {
                OutputStream out = context.getContentResolver().openOutputStream(uriSavedFile);
                FileInputStream in = new FileInputStream(filePath);
                writeFileFromIS(in,out);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
//                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
//                bmp.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 29) {
            valuesFile.clear();
            valuesFile.put(MediaStore.Video.Media.IS_PENDING, 0);
            context.getContentResolver().update(uriSavedFile, valuesFile, null, null);
        } else {
            Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            final Uri localUri = Uri.fromFile(createdFile);
            localIntent.setData(localUri);
            context.sendBroadcast(localIntent);
        }
    }


    public static String getMimeType(String fileName){
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileName);
        return type;
    }
    //原文链接：https://blog.csdn.net/jingzz1/article/details/101523776

    /**
     * 复制文件
     *
     * @param is 文件输入流
     * @param os 文件输出流
     * @return
     */
    public static boolean writeFileFromIS(final InputStream is, final OutputStream os) {
        OutputStream osBuffer = null;
        BufferedInputStream isBuffer = null;
        try {
            isBuffer = new BufferedInputStream(is);
            osBuffer = new BufferedOutputStream(os);
            byte[] data = new byte[2048];
            for (int len; (len = isBuffer.read(data)) != -1; ) {
                os.write(data, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (isBuffer instanceof Closeable) {
                try {
                    isBuffer.close();
                } catch (Exception e) {
                    // silence
                }
            }
            if (osBuffer instanceof Closeable) {
                try {
                    osBuffer.close();
                } catch (Exception e) {
                    // silence
                }
            }
        }
    }

}