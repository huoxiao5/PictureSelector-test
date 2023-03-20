package com.luck.pictureselector;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/2/21 0021.
 */

public class CopyFileUtil {

    private static final String TAG = "CopyFileUtil";

    private static File sourceFile;
    private static File targetFile;
    private static String fileName;

    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel();
             FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    /**
     *  复制文件夹内容，调用这个方法最好是放在子线程中运行
     * @param context
     * @param sourceFolder
     * @param destFolder
     */
    public static void  copyFolder(final Context context,String sourceFolder , final String destFolder) {

        if (!new File(sourceFolder).isDirectory()){
            Log.e(TAG, "simpleCopy:  source必须是文件夹" );
            return;
        }

        File[] sourceFiles=new File(sourceFolder).listFiles();
        List<File> fileList= new ArrayList<>(sourceFiles.length);

        Collections.addAll(fileList,sourceFiles);

        Log.e(TAG, "copyFolder: fileList.len= "+fileList.size() );
        copyFolder2(context,fileList,destFolder);
    }

    private static void  copyFolder2(final Context context,List<File> fileList , final String destFolder) {
        for (int i = 0; i < fileList.size(); i++) {
            sourceFile = fileList.get(i);
            //第一步先判断目标路径是否存在同名文件/文件夹
            fileName = sourceFile.getName();
            targetFile = new File(destFolder + "/" + fileName);
            sameCopy(context);
        }
        Log.d(TAG, "simpleCopy: 复制完毕");
    }
    private static void sameCopy(Context context) {
        //第二步 判断是文件还是文件夹
        if (sourceFile.isDirectory()) {
            //文件夹,将文件夹的所有内容加入subFileList
            if (targetFile.exists()) {
                //如果文件夹存在，用户选择的是覆盖，就没必要重新创建文件夹
                List<File> subFileList = new ArrayList<>();
                Collections.addAll(subFileList, sourceFile.listFiles());
                copyFolder2(context, subFileList, targetFile.getAbsolutePath());
            } else {
                if (targetFile.mkdir()) {
                    List<File> subFileList = new ArrayList<>();
                    Collections.addAll(subFileList, sourceFile.listFiles());
                    copyFolder2(context, subFileList, targetFile.getAbsolutePath());
                } else {
                    Log.d(TAG, "simpleCopy: 创建文件夹失败！");
                    Log.d(TAG, "simpleCopy: 要么没有权限，要么目标路径有问题");
                }
            }
        } else {
            try {
                copyFileUsingFileChannels(sourceFile, targetFile);
             } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
