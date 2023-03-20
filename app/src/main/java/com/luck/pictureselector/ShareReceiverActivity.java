package com.luck.pictureselector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ShareReceiverActivity extends AppCompatActivity {

    private String sandboxPath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_receiver);
        sandboxPath = getSandboxPath();
        doSave();
    }

    private void doSave(){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.e("TAG", "QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQdoSave:   action=="+action+",  type="+action  );
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            ArrayList<Uri> arrayList = new ArrayList<>();
            arrayList.add(uri);
            if (type.startsWith("image/")) {
                confirmSave(arrayList,false,false);
            } else if (type.startsWith("video/")) {
                confirmSave(arrayList,true,false);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            ArrayList<Uri> arrayList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            if (type.startsWith("image/")) {
                confirmSave(arrayList,false,false);
            } else if (type.startsWith("video/")) {
                confirmSave(arrayList,true,false);
            }else{
                confirmSave(arrayList,true,true);
            }
        }
    }

    private void confirmSave(ArrayList<Uri> list,boolean isVideo,boolean isAll){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定要将分享进来的 "+(isAll?" 图片、视频 ":(isVideo?"视频":"图片"))+" (数量："+list.size()+") 保存到沙盒中吗？");
        //监听下方button点击事件
        builder.setPositiveButton("确定保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 save(list);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }


    ProgressDialog  dialog=null;
    private void save(ArrayList<Uri> list){
        dialog = ProgressDialog.show( this, "", "正在保存", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<list.size();i++){
                    Uri uri = list.get(i);
                    saveToFile(uri);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        showSimpleDialog("保存成功！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ShareReceiverActivity.this.startActivity(new Intent(ShareReceiverActivity.this,MainActivity2.class));
                                finish();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void saveToFile(Uri uri)  {
        InputStream inputStream =null;
        FileOutputStream fos=null;
        try{
            String fileName = getFileNameFromUri(uri);

            File outFile = new File(sandboxPath + File.separator + fileName);
            inputStream = getContentResolver().openInputStream(uri);
            fos = new FileOutputStream(outFile);
            byte[] buf = new byte[2048];
            int readCount = 0;
            while ((readCount = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, readCount);
            }
            fos.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                inputStream.close();
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private String getSandboxPath() {
        File externalFilesDir = getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    private String getFileNameFromUri(Uri uri){
        String name = ""+System.currentTimeMillis();
        try{
            DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
            if (documentFile!=null){
                String fileName = documentFile.getName();
                if (fileName!=null){
                    name=fileName;
                }
            }else{
                Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                String fileName = returnCursor.getString(nameIndex);
                if (fileName!=null){
                    name=fileName;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return name;
    }

    private void showSimpleDialog(String msg, android.content.DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder;
        builder=new AlertDialog.Builder(this);
        builder.setMessage(msg);
        //监听下方button点击事件
        builder.setPositiveButton("确定",listener);
//        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(), R.string.toast_negative, Toast.LENGTH_SHORT).show();
//            }
//        });

        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

}