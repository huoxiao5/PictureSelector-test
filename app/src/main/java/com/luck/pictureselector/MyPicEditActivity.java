package com.luck.pictureselector;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.luck.picture.lib.utils.DateUtils;
import com.luck.pictureselector.emoji.Emoji;
import com.luck.pictureselector.emoji.EmojiDrawer;
import com.luck.pictureselector.emoji.IEmojiCallback;
import com.vachel.editor.PictureEditActivity;
import com.vachel.editor.bean.StickerText;
import com.vachel.editor.util.Utils;

import java.io.File;

public class MyPicEditActivity extends PictureEditActivity implements IEmojiCallback {

    @Override
    public void initData() {
        mSupportEmoji = true;
    }

    @Override
    public View getStickerLayout() {
            return new EmojiDrawer(this).bindCallback(this);
    }

    @Override
    public void onEmojiClick(String emoji) {
        StickerText stickerText = new StickerText(emoji, Color.WHITE);
        onText(stickerText, false); // emoji其实也是text文本
        Utils.dismissDialog(mStickerImageDialog);
    }

    @Override
    public void onBackClick() {
        mStickerImageDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Emoji.recycleAllEmoji();
    }

    @Override
    public void onSaveSuccess(String savePath) {
        Log.e("TAG", "onSaveSuccess:   MyEditActivity-------  " );
        setResult(RESULT_OK,
                new Intent().putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(savePath))));
        finish();
    }
}
