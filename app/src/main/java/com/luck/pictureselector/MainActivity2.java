package com.luck.pictureselector;

import static com.luck.picture.lib.config.VideoQuality.VIDEO_QUALITY_HIGH;
import static com.luck.picture.lib.config.VideoQuality.VIDEO_QUALITY_LOW;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.lib.camerax.CameraImageEngine;
import com.luck.lib.camerax.SimpleCameraX;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDeniedListener;
import com.luck.lib.camerax.listener.OnSimpleXPermissionDescriptionListener;
import com.luck.lib.camerax.permissions.SimpleXPermissionUtil;
import com.luck.picture.lib.PictureSelectorPreviewFragment;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.basic.FragmentInjectManager;
import com.luck.picture.lib.basic.IBridgePictureBehavior;
import com.luck.picture.lib.basic.IBridgeViewLifecycle;
import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.basic.PictureSelectionCameraModel;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelectionSystemModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectLimitType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.RemindDialog;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.ExtendLoaderEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.engine.VideoPlayerEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.interfaces.OnBitmapWatermarkEventListener;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.interfaces.OnCameraInterceptListener;
import com.luck.picture.lib.interfaces.OnCustomLoadingListener;
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener;
import com.luck.picture.lib.interfaces.OnGridItemSelectAnimListener;
import com.luck.picture.lib.interfaces.OnInjectActivityPreviewListener;
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnMediaEditInterceptListener;
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener;
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener;
import com.luck.picture.lib.interfaces.OnPreviewInterceptListener;
import com.luck.picture.lib.interfaces.OnQueryAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataResultListener;
import com.luck.picture.lib.interfaces.OnQueryFilterListener;
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.interfaces.OnSelectAnimListener;
import com.luck.picture.lib.interfaces.OnSelectLimitTipsListener;
import com.luck.picture.lib.interfaces.OnVideoThumbnailEventListener;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.loader.SandboxFileLoader;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.permissions.PermissionConfig;
import com.luck.picture.lib.permissions.PermissionResultCallback;
import com.luck.picture.lib.permissions.PermissionUtil;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.DownloadFileUtils;
import com.luck.picture.lib.utils.MediaUtils;
import com.luck.picture.lib.utils.PictureFileUtils;
import com.luck.picture.lib.utils.SandboxTransformUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.luck.picture.lib.utils.ValueOf;
import com.luck.picture.lib.widget.MediumBoldTextView;
import com.luck.pictureselector.adapter.GridImageAdapter;
import com.luck.pictureselector.listener.DragListener;
import com.luck.pictureselector.listener.OnItemLongClickListener;
import com.vachel.editor.PictureEditActivity;
import com.vachel.editor.PictureEditor;
import com.xujiaji.happybubble.Auto;
import com.xujiaji.happybubble.BubbleDialog;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;
import com.yalantis.ucrop.model.AspectRatio;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnNewCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * @author：luck
 * @data：2019/12/20 晚上 23:12
 * @描述: Demo
 */

public class MainActivity2 extends AppCompatActivity implements IBridgePictureBehavior, View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
    private final static String TAG = "PictureSelectorTag";
    private final static String TAG_EXPLAIN_VIEW = "TAG_EXPLAIN_VIEW";
    private final static int ACTIVITY_RESULT = 1;
    private final static int CALLBACK_RESULT = 2;
    private final static int LAUNCHER_RESULT = 3;
    private GridImageAdapter mAdapter;
    private int maxSelectNum = 100;
    private int maxSelectVideoNum = 100;

    private LinearLayout llSelectVideoSize;
    private int aspect_ratio_x = -1, aspect_ratio_y = -1;

    private int chooseMode = SelectMimeType.ofAll();
    private boolean isHasLiftDelete;
    private boolean needScaleBig = true;
    private boolean needScaleSmall = false;
    private int language = LanguageConfig.UNKNOWN_LANGUAGE;
    private int x = 0, y = 0;
    private int animationMode = AnimationType.DEFAULT_ANIMATION;
    private PictureSelectorStyle selectorStyle;
    private final List<LocalMedia> mData = new ArrayList<>();
    private ActivityResultLauncher<Intent> launcherResult;
    private int resultMode = LAUNCHER_RESULT;
    private ImageEngine imageEngine;
    private VideoPlayerEngine videoPlayerEngine;

    private TextView tvDeleteText;
    private Button exportBut;
    private CheckBox high_record_checkbox;
    private ImageView setImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        selectorStyle = new PictureSelectorStyle();



        tvDeleteText = findViewById(R.id.tv_delete_text);
        RecyclerView mRecyclerView = findViewById(R.id.recycler);
        ImageView left_back = findViewById(R.id.left_back);
        left_back.setOnClickListener(this);
        exportBut = findViewById(R.id.exportBut);
        exportBut.setOnClickListener(this);
        Button delBut = findViewById(R.id.delBut);
        delBut.setOnClickListener(this);

        high_record_checkbox = findViewById(R.id.high_record_checkbox);

        high_record_checkbox.setChecked(true);

        Button saveBut=findViewById(R.id.saveBut);
        saveBut.setOnClickListener(this);

        setImg=findViewById(R.id.right_setting);
        setImg.setOnClickListener(this);

        // 注册需要写在onCreate或Fragment onAttach里，否则会报java.lang.IllegalStateException异常
        launcherResult = createActivityResultLauncher();


        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
                DensityUtil.dip2px(this, 8), false));
        mAdapter = new GridImageAdapter(getContext(), mData);
        mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum);
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("selectorList") != null) {
            mData.clear();
            mData.addAll(savedInstanceState.getParcelableArrayList("selectorList"));
        }



        setWeChatStyle();

        imageEngine = GlideEngine.createGlideEngine();

        mAdapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                // 预览图片、视频、音频
                PictureSelector.create(MainActivity2.this)
                        .openPreview()
                        .setImageEngine(imageEngine)
                        .setVideoPlayerEngine(videoPlayerEngine)
                        .setSelectorUIStyle(selectorStyle)
                        .setLanguage(language)
                        .isAutoVideoPlay(false)
                        .isLoopAutoVideoPlay(false)
                        .isPreviewFullScreenMode(true)
                        .isVideoPauseResumePlay(true)
                        .setCustomLoadingListener(getCustomLoadingListener())
                        .isDisplayTimeInPreview(true)
                        .isPreviewZoomEffect(chooseMode != SelectMimeType.ofAudio()  , mRecyclerView)
                        .setAttachViewLifecycle(new IBridgeViewLifecycle() {
                            @Override
                            public void onViewCreated(Fragment fragment, View view, Bundle savedInstanceState) {
//                                PictureSelectorPreviewFragment previewFragment = (PictureSelectorPreviewFragment) fragment;
//                                MediumBoldTextView tvShare = view.findViewById(R.id.tv_share);
//                                tvShare.setVisibility(View.VISIBLE)
//                                previewFragment.addAminViews(tvShare);
//                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tvShare.getLayoutParams();
//                                layoutParams.topMargin = cb_preview_full.isChecked() ? DensityUtil.getStatusBarHeight(getContext()) : 0;
//                                tvShare.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        PicturePreviewAdapter previewAdapter = previewFragment.getAdapter();
//                                        ViewPager2 viewPager2 = previewFragment.getViewPager2();
//                                        LocalMedia media = previewAdapter.getItem(viewPager2.getCurrentItem());
//                                        ToastUtils.showToast(fragment.getContext(), "自定义分享事件:" + viewPager2.getCurrentItem());
//                                    }
//                                });
                            }

                            @Override
                            public void onDestroy(Fragment fragment) {
//                                if (cb_preview_full.isChecked()) {
//                                    // 如果是全屏预览模式且是startFragmentPreview预览，回到自己的界面时需要恢复一下自己的沉浸式状态
//                                    // 以下提供2种解决方案:
//                                    // 1.通过ImmersiveManager.immersiveAboveAPI23重新设置一下沉浸式
//                                    int statusBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    int navigationBarColor = ContextCompat.getColor(getContext(), R.color.ps_color_grey);
//                                    ImmersiveManager.immersiveAboveAPI23(MainActivity.this,
//                                            true, true,
//                                            statusBarColor, navigationBarColor, false);
//                                    // 2.让自己的titleBar的高度加上一个状态栏高度且内容PaddingTop下沉一个状态栏的高度
//                                }
                            }
                        })
                        .setInjectLayoutResourceListener(new OnInjectLayoutResourceListener() {
                            @Override
                            public int getLayoutResourceId(Context context, int resourceSource) {
                                return resourceSource == InjectResourceSource.PREVIEW_LAYOUT_RESOURCE
                                        ? R.layout.ps_custom_fragment_preview
                                        : InjectResourceSource.DEFAULT_LAYOUT_RESOURCE;
                            }
                        })
                        .setExternalPreviewEventListener(new MyExternalPreviewEventListener())
                        .setInjectActivityPreviewFragment(new OnInjectActivityPreviewListener() {
                            @Override
                            public PictureSelectorPreviewFragment onInjectPreviewFragment() {
                                return   null;
                            }
                        })
                        .startActivityPreview(position, true, mAdapter.getData());
            }

            @Override
            public void openPicture() {
                if (true) {
                    // 进入系统相册
                      toOpenPicture();
                } else {
                    // 单独拍照
                    PictureSelectionCameraModel cameraModel = PictureSelector.create(MainActivity2.this)
                            .openCamera(chooseMode)
                            .setCameraInterceptListener(getCustomCameraEvent())
                            .setRecordAudioInterceptListener(new MeOnRecordAudioInterceptListener())
                            .setCropEngine(getCropFileEngine())
                            .setCompressEngine(getCompressFileEngine())
                            .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
                            .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
                            .setVideoThumbnailListener(getVideoThumbnailEventListener())
                            .setCustomLoadingListener(getCustomLoadingListener())
                            .setLanguage(language)
                            .setSandboxFileEngine(new MeSandboxFileEngine())
                            .isOriginalControl(true)
                            .setPermissionDescriptionListener(getPermissionDescriptionListener())
                            .setOutputAudioDir(getSandboxAudioOutputPath())
                            .setSelectedData(mAdapter.getData());
                    forOnlyCameraResult(cameraModel);
                }
            }
        });

        mAdapter.setItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(RecyclerView.ViewHolder holder, int position, View v) {
                int itemViewType = holder.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    mItemTouchHelper.startDrag(holder);
                }
            }
        });
        // 绑定拖拽事件
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        // 清除缓存
//        clearCache();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toOpenPicture();
            }
        },300);
    }

    private void toOpenPicture(){
        // 进入相册
        PictureSelectionModel selectionModel = PictureSelector.create(getContext())
                .openGallery(chooseMode)
                .setSelectorUIStyle(selectorStyle)
                .setImageEngine(imageEngine)
                .setVideoPlayerEngine(videoPlayerEngine)
                .setCropEngine(getCropFileEngine())
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .setCameraInterceptListener(getCustomCameraEvent())
                .setRecordAudioInterceptListener(new MeOnRecordAudioInterceptListener())
                .setSelectLimitTipsListener(new MeOnSelectLimitTipsListener())
                .setEditMediaInterceptListener(getCustomEditMediaEvent())
                .setPermissionDescriptionListener(getPermissionDescriptionListener())
                .setPreviewInterceptListener(getPreviewInterceptListener())
                .setPermissionDeniedListener(getPermissionDeniedListener())
                .setAddBitmapWatermarkListener(getAddBitmapWatermarkListener())
                .setVideoThumbnailListener(getVideoThumbnailEventListener())
                .isAutoVideoPlay(false)
                .isLoopAutoVideoPlay(false)
                .isPageSyncAlbumCount(true)
                .setRecordVideoMaxSecond(20*60)
                .setCustomLoadingListener(getCustomLoadingListener())
                .setQueryFilterListener(new OnQueryFilterListener() {
                    @Override
                    public boolean onFilter(LocalMedia media) {
                        return false;
                    }
                })
                //.setExtendLoaderEngine(getExtendLoaderEngine())
                .setInjectLayoutResourceListener(getInjectLayoutResource())
                .setSelectionMode(  SelectModeConfig.MULTIPLE )
                .setLanguage(language)
                .setQuerySortOrder(MediaStore.MediaColumns.DATE_MODIFIED + " DESC")
                .setOutputCameraDir(chooseMode == SelectMimeType.ofAudio()
                        ? getSandboxAudioOutputPath() : getSandboxCameraOutputPath())
                .setOutputAudioDir(chooseMode == SelectMimeType.ofAudio()
                        ? getSandboxAudioOutputPath() : getSandboxCameraOutputPath())
                .setQuerySandboxDir(chooseMode == SelectMimeType.ofAudio()
                        ? getSandboxAudioOutputPath() : getSandboxCameraOutputPath())
                .isDisplayTimeAxis(true)
                .isDisplayTimeInGridItem(true)
                .isDisplayTimeInPreview(true)
                .isOnlyObtainSandboxDir(true)
                .isPageStrategy(true)
                .isOriginalControl(true)
                .isDisplayCamera(true)
                .isOpenClickSound(false)
                .setSkipCropMimeType(getNotSupportCrop())
                .isFastSlidingSelect(true)
                //.setOutputCameraImageFileName("luck.jpeg")
                //.setOutputCameraVideoFileName("luck.mp4")
                .isWithSelectVideoImage(true)
                .isPreviewFullScreenMode(true)
                .isVideoPauseResumePlay(true)
                .isPreviewZoomEffect(true)
                .isPreviewImage(true)
                .isPreviewVideo(true)
                .isPreviewAudio(false)
                .setGridItemSelectAnimListener(null)
                .setSelectAnimListener(null)
                .setVideoQuality(high_record_checkbox.isChecked()?VIDEO_QUALITY_HIGH:VIDEO_QUALITY_LOW)
                //.setQueryOnlyMimeType(PictureMimeType.ofGIF())
                .isMaxSelectEnabledMask(true)
                .isDirectReturnSingle(false)
                .setMaxSelectNum(maxSelectNum)
                .setMaxVideoSelectNum(maxSelectVideoNum)
                .setRecyclerAnimationMode(animationMode)
                .isGif(true)
                .setSelectedData(mAdapter.getData());
        forSelectResult(selectionModel);
    }

    private String[] getNotSupportCrop() {
        if (false) {
            return new String[]{PictureMimeType.ofGIF(), PictureMimeType.ofWEBP()};
        }
        return null;
    }

    private final ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(0.7f);
            }
            return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            try {
                //得到item原来的position
                int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                //得到目标position
                int toPosition = target.getAbsoluteAdapterPosition();
                int itemViewType = target.getItemViewType();
                if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(mAdapter.getData(), i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(mAdapter.getData(), i, i - 1);
                        }
                    }
                    mAdapter.notifyItemMoved(fromPosition, toPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dx, float dy, int actionState, boolean isCurrentlyActive) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (needScaleBig) {
                    needScaleBig = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.0F, 1.1F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.0F, 1.1F));
                    animatorSet.setDuration(50);
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleSmall = true;
                        }
                    });
                }
                int targetDy = tvDeleteText.getTop() - viewHolder.itemView.getBottom();
                if (dy >= targetDy) {
                    //拖到删除处
                    mDragListener.deleteState(true);
                    if (isHasLiftDelete) {
                        //在删除处放手，则删除item
                        viewHolder.itemView.setVisibility(View.INVISIBLE);
                        mAdapter.delete(viewHolder.getAbsoluteAdapterPosition());
                        resetState();
                        return;
                    }
                } else {
                    //没有到删除处
                    if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {
                        //如果viewHolder不可见，则表示用户放手，重置删除区域状态
                        mDragListener.dragState(false);
                    }
                    mDragListener.deleteState(false);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dx, dy, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            int itemViewType = viewHolder != null ? viewHolder.getItemViewType() : GridImageAdapter.TYPE_CAMERA;
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                if (ItemTouchHelper.ACTION_STATE_DRAG == actionState) {
                    mDragListener.dragState(true);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            isHasLiftDelete = true;
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridImageAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(1.0F);
                if (needScaleSmall) {
                    needScaleSmall = false;
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleX", 1.1F, 1.0F),
                            ObjectAnimator.ofFloat(viewHolder.itemView, "scaleY", 1.1F, 1.0F));
                    animatorSet.setInterpolator(new LinearInterpolator());
                    animatorSet.setDuration(50);
                    animatorSet.start();
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            needScaleBig = true;
                        }
                    });
                }
                super.clearView(recyclerView, viewHolder);
                mAdapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                resetState();
            }
        }
    });

    private final DragListener mDragListener = new DragListener() {
        @Override
        public void deleteState(boolean isDelete) {
            if (isDelete) {
                if (!TextUtils.equals(getString(R.string.app_let_go_drag_delete), tvDeleteText.getText())) {
                    tvDeleteText.setText(getString(R.string.app_let_go_drag_delete));
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_dump_delete, 0, 0);
                }
            } else {
                if (!TextUtils.equals(getString(R.string.app_drag_delete), tvDeleteText.getText())) {
                    tvDeleteText.setText(getString(R.string.app_drag_delete));
                    tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_normal_delete, 0, 0);
                }
            }

        }

        @Override
        public void dragState(boolean isStart) {
            if (isStart) {
                if (tvDeleteText.getAlpha() == 0F) {
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvDeleteText, "alpha", 0F, 1F);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.setDuration(120);
                    alphaAnimator.start();
                }
            } else {
                if (tvDeleteText.getAlpha() == 1F) {
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvDeleteText, "alpha", 1F, 0F);
                    alphaAnimator.setInterpolator(new LinearInterpolator());
                    alphaAnimator.setDuration(120);
                    alphaAnimator.start();
                }
            }
        }
    };

    private void forSelectResult(PictureSelectionModel model) {
        switch (resultMode) {
            case ACTIVITY_RESULT:
                model.forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case CALLBACK_RESULT:
                model.forResult(new MeOnResultCallbackListener());
                break;
            default:
                model.forResult(launcherResult);
                break;
        }
    }

    private void forOnlyCameraResult(PictureSelectionCameraModel model) {
        if (false) {
            switch (resultMode) {
                case ACTIVITY_RESULT:
                    model.forResultActivity(PictureConfig.REQUEST_CAMERA);
                    break;
                case CALLBACK_RESULT:
                    model.forResultActivity(new MeOnResultCallbackListener());
                    break;
                default:
                    model.forResultActivity(launcherResult);
                    break;
            }
        } else {
            if (resultMode == CALLBACK_RESULT) {
                model.forResult(new MeOnResultCallbackListener());
            } else {
                model.forResult();
            }
        }
    }

    /**
     * 重置
     */
    private void resetState() {
        isHasLiftDelete = false;
        mDragListener.deleteState(false);
        mDragListener.dragState(false);
    }

    /**
     * 外部预览监听事件
     */
    private class MyExternalPreviewEventListener implements OnExternalPreviewEventListener {

        @Override
        public void onPreviewDelete(int position) {
            mAdapter.remove(position);
            mAdapter.notifyItemRemoved(position);
        }

        @Override
        public boolean onLongPressDownload(Context context, LocalMedia media) {
            return false;
        }
    }

    /**
     * 选择结果
     */
    private class MeOnResultCallbackListener implements OnResultCallbackListener<LocalMedia> {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {
            analyticalSelectResults(result);
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "PictureSelector Cancel");
        }
    }

    /**
     * 压缩引擎
     *
     * @return
     */
    private ImageFileCompressEngine getCompressFileEngine() {
        return   null;
    }

    /**
     * 压缩引擎
     *
     * @return
     */
    @Deprecated
    private ImageCompressEngine getCompressEngine() {
        return   null;
    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private ImageFileCropEngine getCropFileEngine() {
        return   null;
    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private ImageCropEngine getCropEngine() {
        return   null;
    }

    /**
     * 自定义相机事件
     *
     * @return
     */
    private OnCameraInterceptListener getCustomCameraEvent() {
        return  null;
    }


    /**
     * 自定义数据加载器
     *
     * @return
     */
    private ExtendLoaderEngine getExtendLoaderEngine() {
        return new MeExtendLoaderEngine();
    }


    /**
     * 注入自定义布局
     *
     * @return
     */
    private OnInjectLayoutResourceListener getInjectLayoutResource() {
//        return cb_inject_layout.isChecked() ? new MeOnInjectLayoutResourceListener() : null;
        return null;
    }


    /**
     * 处理视频缩略图
     */
    private OnVideoThumbnailEventListener getVideoThumbnailEventListener() {
        //return cb_video_thumbnails.isChecked() ? new MeOnVideoThumbnailEventListener(getVideoThumbnailDir()) : null;
        return  new MeOnVideoThumbnailEventListener(getVideoThumbnailDir())  ;
    }

    /**
     * 处理视频缩略图
     */
    private static class MeOnVideoThumbnailEventListener implements OnVideoThumbnailEventListener {
        private final String targetPath;

        public MeOnVideoThumbnailEventListener(String targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public void onVideoThumbnail(Context context, String videoPath, OnKeyValueResultCallbackListener call) {
            Glide.with(context).asBitmap().sizeMultiplier(0.6F).load(videoPath).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    FileOutputStream fos = null;
                    String result = null;
                    try {
                        File targetFile = new File(targetPath, "thumbnails_" + System.currentTimeMillis() + ".jpg");
                        fos = new FileOutputStream(targetFile);
                        fos.write(stream.toByteArray());
                        fos.flush();
                        result = targetFile.getAbsolutePath();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        PictureFileUtils.close(fos);
                        PictureFileUtils.close(stream);
                    }
                    if (call != null) {
                        call.onCallback(videoPath, result);
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    if (call != null) {
                        call.onCallback(videoPath, "");
                    }
                }
            });
        }
    }

    /**
     * 自定义loading
     *
     * @return
     */
    private OnCustomLoadingListener getCustomLoadingListener() {

        return null;
    }

    /**
     * 给图片添加水印
     */
    private OnBitmapWatermarkEventListener getAddBitmapWatermarkListener() {
        return   null;
    }

    /**
     * 给图片添加水印
     */
    private static class MeBitmapWatermarkEventListener implements OnBitmapWatermarkEventListener {
        private final String targetPath;

        public MeBitmapWatermarkEventListener(String targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public void onAddBitmapWatermark(Context context, String srcPath, String mimeType, OnKeyValueResultCallbackListener call) {
            if (PictureMimeType.isHasHttp(srcPath) || PictureMimeType.isHasVideo(mimeType)) {
                // 网络图片和视频忽略，有需求的可自行扩展
                call.onCallback(srcPath, "");
            } else {
                // 暂时只以图片为例
                Glide.with(context).asBitmap().sizeMultiplier(0.6F).load(srcPath).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mark_win);
                        Bitmap watermarkBitmap = ImageUtil.createWaterMaskRightTop(context, resource, watermark, 15, 15);
                        watermarkBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                        watermarkBitmap.recycle();
                        FileOutputStream fos = null;
                        String result = null;
                        try {
                            File targetFile = new File(targetPath, DateUtils.getCreateFileName("Mark_") + ".jpg");
                            fos = new FileOutputStream(targetFile);
                            fos.write(stream.toByteArray());
                            fos.flush();
                            result = targetFile.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            PictureFileUtils.close(fos);
                            PictureFileUtils.close(stream);
                        }
                        if (call != null) {
                            call.onCallback(srcPath, result);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (call != null) {
                            call.onCallback(srcPath, "");
                        }
                    }
                });
            }
        }
    }


    /**
     * 权限拒绝后回调
     *
     * @return
     */
    private OnPermissionDeniedListener getPermissionDeniedListener() {
        return   null;
    }


    /**
     * 权限拒绝后回调
     */
    private static class MeOnPermissionDeniedListener implements OnPermissionDeniedListener {

        @Override
        public void onDenied(Fragment fragment, String[] permissionArray,
                             int requestCode, OnCallbackListener<Boolean> call) {
            String tips;
            if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能";
            } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少录音权限\n访问您设备上的音频、媒体内容和文件";
            } else {
                tips = "缺少存储权限\n访问您设备上的照片、媒体内容和文件";
            }
            RemindDialog dialog = RemindDialog.buildDialog(fragment.getContext(), tips);
            dialog.setButtonText("去设置");
            dialog.setButtonTextColor(0xFF7D7DFF);
            dialog.setContentTextColor(0xFF333333);
            dialog.setOnDialogClickListener(new RemindDialog.OnDialogClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionUtil.goIntentSetting(fragment, requestCode);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * SimpleCameraX权限拒绝后回调
     *
     * @return
     */
    private OnSimpleXPermissionDeniedListener getSimpleXPermissionDeniedListener() {
        return   null;
    }

    /**
     * SimpleCameraX添加权限说明
     */
    private static class MeOnSimpleXPermissionDeniedListener implements OnSimpleXPermissionDeniedListener {

        @Override
        public void onDenied(Context context, String permission, int requestCode) {
            String tips;
            if (TextUtils.equals(permission, Manifest.permission.RECORD_AUDIO)) {
                tips = "缺少麦克风权限\n可能会导致录视频无法采集声音";
            } else {
                tips = "缺少相机权限\n可能会导致不能使用摄像头功能";
            }
            RemindDialog dialog = RemindDialog.buildDialog(context, tips);
            dialog.setButtonText("去设置");
            dialog.setButtonTextColor(0xFF7D7DFF);
            dialog.setContentTextColor(0xFF333333);
            dialog.setOnDialogClickListener(new RemindDialog.OnDialogClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleXPermissionUtil.goIntentSetting((Activity) context, requestCode);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    /**
     * SimpleCameraX权限说明
     *
     * @return
     */
    private OnSimpleXPermissionDescriptionListener getSimpleXPermissionDescriptionListener() {
        return   null;
    }

    /**
     * SimpleCameraX添加权限说明
     */
    private static class MeOnSimpleXPermissionDescriptionListener implements OnSimpleXPermissionDescriptionListener {

        @Override
        public void onPermissionDescription(Context context, ViewGroup viewGroup, String permission) {
            addPermissionDescription(true, viewGroup, new String[]{permission});
        }

        @Override
        public void onDismiss(ViewGroup viewGroup) {
            removePermissionDescription(viewGroup);
        }
    }


    /**
     * 权限说明
     *
     * @return
     */
    private OnPermissionDescriptionListener getPermissionDescriptionListener() {
        return   null;
    }

    /**
     * 添加权限说明
     */
    private static class MeOnPermissionDescriptionListener implements OnPermissionDescriptionListener {

        @Override
        public void onPermissionDescription(Fragment fragment, String[] permissionArray) {
            View rootView = fragment.requireView();
            if (rootView instanceof ViewGroup) {
                addPermissionDescription(false, (ViewGroup) rootView, permissionArray);
            }
        }

        @Override
        public void onDismiss(Fragment fragment) {
            removePermissionDescription((ViewGroup) fragment.requireView());
        }
    }

    /**
     * 添加权限说明
     *
     * @param viewGroup
     * @param permissionArray
     */
    private static void addPermissionDescription(boolean isHasSimpleXCamera, ViewGroup viewGroup, String[] permissionArray) {
        int dp10 = DensityUtil.dip2px(viewGroup.getContext(), 10);
        int dp15 = DensityUtil.dip2px(viewGroup.getContext(), 15);
        MediumBoldTextView view = new MediumBoldTextView(viewGroup.getContext());
        view.setTag(TAG_EXPLAIN_VIEW);
        view.setTextSize(14);
        view.setTextColor(Color.parseColor("#333333"));
        view.setPadding(dp10, dp15, dp10, dp15);

        String title;
        String explain;

        if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
            title = "相机权限使用说明";
            explain = "相机权限使用说明\n用户app用于拍照/录视频";
        } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
            if (isHasSimpleXCamera) {
                title = "麦克风权限使用说明";
                explain = "麦克风权限使用说明\n用户app用于录视频时采集声音";
            } else {
                title = "录音权限使用说明";
                explain = "录音权限使用说明\n用户app用于采集声音";
            }
        } else {
            title = "存储权限使用说明";
            explain = "存储权限使用说明\n用户app写入/下载/保存/读取/修改/删除图片、视频、文件等信息";
        }
        int startIndex = 0;
        int endOf = startIndex + title.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(explain);
        builder.setSpan(new AbsoluteSizeSpan(DensityUtil.dip2px(viewGroup.getContext(), 16)), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(0xFF333333), startIndex, endOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        view.setText(builder);
        view.setBackground(ContextCompat.getDrawable(viewGroup.getContext(), R.drawable.ps_demo_permission_desc_bg));

        if (isHasSimpleXCamera) {
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = DensityUtil.getStatusBarHeight(viewGroup.getContext());
            layoutParams.leftMargin = dp10;
            layoutParams.rightMargin = dp10;
            viewGroup.addView(view, layoutParams);
        } else {
            ConstraintLayout.LayoutParams layoutParams =
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.topToBottom = R.id.title_bar;
            layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
            layoutParams.leftMargin = dp10;
            layoutParams.rightMargin = dp10;
            viewGroup.addView(view, layoutParams);
        }
    }

    /**
     * 移除权限说明
     *
     * @param viewGroup
     */
    private static void removePermissionDescription(ViewGroup viewGroup) {
        View tagExplainView = viewGroup.findViewWithTag(TAG_EXPLAIN_VIEW);
        viewGroup.removeView(tagExplainView);
    }



    /**
     * 自定义预览
     *
     * @return
     */
    private OnPreviewInterceptListener getPreviewInterceptListener() {
        return   null;
    }

    /**
     * 自定义预览
     *
     * @return
     */
    private static class MeOnPreviewInterceptListener implements OnPreviewInterceptListener {

        @Override
        public void onPreview(Context context, int position, int totalNum, int page, long currentBucketId, String currentAlbumName, boolean isShowCamera, ArrayList<LocalMedia> data, boolean isBottomPreview) {
            CustomPreviewFragment previewFragment = CustomPreviewFragment.newInstance();
            previewFragment.setInternalPreviewData(isBottomPreview, currentAlbumName, isShowCamera,
                    position, totalNum, page, currentBucketId, data);
            FragmentInjectManager.injectFragment((FragmentActivity) context, CustomPreviewFragment.TAG, previewFragment);
        }
    }

    /**
     * 拦截自定义提示
     */
    private static class MeOnSelectLimitTipsListener implements OnSelectLimitTipsListener {

        @Override
        public boolean onSelectLimitTips(Context context, @Nullable LocalMedia media, PictureSelectionConfig config, int limitType) {
            if (limitType == SelectLimitType.SELECT_MIN_SELECT_LIMIT) {
                ToastUtils.showToast(context, "图片最少不能低于" + config.minSelectNum + "张");
                return true;
            } else if (limitType == SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "视频最少不能低于" + config.minVideoSelectNum + "个");
                return true;
            } else if (limitType == SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT) {
                ToastUtils.showToast(context, "音频最少不能低于" + config.minAudioSelectNum + "个");
                return true;
            }
            return false;
        }
    }

    /**
     * 注入自定义布局UI，前提是布局View id 和 根目录Layout必须一致
     */
    private static class MeOnInjectLayoutResourceListener implements OnInjectLayoutResourceListener {

        @Override
        public int getLayoutResourceId(Context context, int resourceSource) {
            switch (resourceSource) {
                case InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_fragment_selector;
                case InjectResourceSource.PREVIEW_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_fragment_preview;
                case InjectResourceSource.MAIN_ITEM_IMAGE_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_item_grid_image;
                case InjectResourceSource.MAIN_ITEM_VIDEO_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_item_grid_video;
                case InjectResourceSource.MAIN_ITEM_AUDIO_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_item_grid_audio;
                case InjectResourceSource.ALBUM_ITEM_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_album_folder_item;
                case InjectResourceSource.PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_preview_image;
                case InjectResourceSource.PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_preview_video;
                case InjectResourceSource.PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE:
                    return R.layout.ps_custom_preview_gallery_item;
                default:
                    return 0;
            }
        }
    }

    /**
     * 自定义数据加载器
     */
    private class MeExtendLoaderEngine implements ExtendLoaderEngine {

        @Override
        public void loadAllAlbumData(Context context,
                                     OnQueryAllAlbumListener<LocalMediaFolder> query) {
            LocalMediaFolder folder = SandboxFileLoader
                    .loadInAppSandboxFolderFile(context, getSandboxPath());
            List<LocalMediaFolder> folders = new ArrayList<>();
            folders.add(folder);
            query.onComplete(folders);
        }

        @Override
        public void loadOnlyInAppDirAllMediaData(Context context,
                                                 OnQueryAlbumListener<LocalMediaFolder> query) {
            LocalMediaFolder folder = SandboxFileLoader
                    .loadInAppSandboxFolderFile(context, getSandboxPath());
            query.onComplete(folder);
        }

        @Override
        public void loadFirstPageMediaData(Context context, long bucketId, int page, int pageSize, OnQueryDataResultListener<LocalMedia> query) {
            LocalMediaFolder folder = SandboxFileLoader
                    .loadInAppSandboxFolderFile(context, getSandboxPath());
            query.onComplete(folder.getData(), false);
        }

        @Override
        public void loadMoreMediaData(Context context, long bucketId, int page, int limit, int pageSize, OnQueryDataResultListener<LocalMedia> query) {

        }
    }

    /**
     * 自定义编辑事件
     *
     * @return
     */
    private OnMediaEditInterceptListener getCustomEditMediaEvent() {
        return  new MeOnMediaEditInterceptListener(getSandboxPath(), buildOptions()) ;
    }


    /**
     * 自定义编辑
     */
    private static class MeOnMediaEditInterceptListener implements OnMediaEditInterceptListener {
        private final String outputCropPath;
        private final UCrop.Options options;

        public MeOnMediaEditInterceptListener(String outputCropPath, UCrop.Options options) {
            this.outputCropPath = outputCropPath;
            this.options = options;
        }

        @Override
        public void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode) {
            String currentEditPath = currentLocalMedia.getAvailablePath();
            Uri inputUri = PictureMimeType.isContent(currentEditPath)
                    ? Uri.parse(currentEditPath) : Uri.fromFile(new File(currentEditPath));
            Uri destinationUri = Uri.fromFile(
                    new File(outputCropPath, DateUtils.getCreateFileName("CROP_") + ".jpeg"));

            PictureEditor.getInstance().setCustomOutPath(destinationUri.getPath());

            Intent editIntent = new Intent(fragment.requireActivity(), MyPicEditActivity.class);
            editIntent.putExtra(PictureEditActivity.EXTRA_IMAGE_URI, inputUri);
            fragment.startActivityForResult(editIntent,requestCode);

            /*
            UCrop uCrop = UCrop.of(inputUri, destinationUri);
            options.setHideBottomControls(false);
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (call != null) {
                                call.onCall(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (call != null) {
                                call.onCall(null);
                            }
                        }
                    });
                }
            });
            uCrop.startEdit(fragment.requireActivity(), fragment, requestCode);*/
        }
    }

    /**
     * 录音回调事件
     */
    private static class MeOnRecordAudioInterceptListener implements OnRecordAudioInterceptListener {

        @Override
        public void onRecordAudio(Fragment fragment, int requestCode) {
            String[] recordAudio = {Manifest.permission.RECORD_AUDIO};
            if (PermissionChecker.isCheckSelfPermission(fragment.getContext(), recordAudio)) {
                startRecordSoundAction(fragment, requestCode);
            } else {
                addPermissionDescription(false, (ViewGroup) fragment.requireView(), recordAudio);
                PermissionChecker.getInstance().requestPermissions(fragment,
                        new String[]{Manifest.permission.RECORD_AUDIO}, new PermissionResultCallback() {
                            @Override
                            public void onGranted() {
                                removePermissionDescription((ViewGroup) fragment.requireView());
                                startRecordSoundAction(fragment, requestCode);
                            }

                            @Override
                            public void onDenied() {
                                removePermissionDescription((ViewGroup) fragment.requireView());
                            }
                        });
            }
        }
    }

    /**
     * 启动录音意图
     *
     * @param fragment
     * @param requestCode
     */
    private static void startRecordSoundAction(Fragment fragment, int requestCode) {
        Intent recordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (recordAudioIntent.resolveActivity(fragment.requireActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(recordAudioIntent, requestCode);
        } else {
            ToastUtils.showToast(fragment.getContext(), "The system is missing a recording component");
        }
    }

   /* 自定义沙盒文件处理
     */
    private static class MeSandboxFileEngine implements UriToFileTransformEngine {

        @Override
        public void onUriToFileAsyncTransform(Context context, String srcPath, String mineType, OnKeyValueResultCallbackListener call) {
            if (call != null) {
                call.onCallback(srcPath, SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType));
            }
        }
    }

    /**
     * 自定义裁剪
     */
    private class ImageFileCropEngine implements CropFileEngine {

        @Override
        public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
            UCrop.Options options = buildOptions();
            UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (call != null) {
                                call.onCall(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (call != null) {
                                call.onCall(null);
                            }
                        }
                    });
                }
            });
            uCrop.start(fragment.requireActivity(), fragment, requestCode);
        }
    }

    /**
     * 自定义裁剪
     */
    private class ImageCropEngine implements CropEngine {

        @Override
        public void onStartCrop(Fragment fragment, LocalMedia currentLocalMedia,
                                ArrayList<LocalMedia> dataSource, int requestCode) {
            String currentCropPath = currentLocalMedia.getAvailablePath();
            Uri inputUri;
            if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
                inputUri = Uri.parse(currentCropPath);
            } else {
                inputUri = Uri.fromFile(new File(currentCropPath));
            }
            String fileName = DateUtils.getCreateFileName("CROP_") + ".jpg";
            Uri destinationUri = Uri.fromFile(new File(getSandboxPath(), fileName));
            UCrop.Options options = buildOptions();
            ArrayList<String> dataCropSource = new ArrayList<>();
            for (int i = 0; i < dataSource.size(); i++) {
                LocalMedia media = dataSource.get(i);
                dataCropSource.add(media.getAvailablePath());
            }
            UCrop uCrop = UCrop.of(inputUri, destinationUri, dataCropSource);
            //options.setMultipleCropAspectRatio(buildAspectRatios(dataSource.size()));
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                }
            });
            uCrop.start(fragment.requireActivity(), fragment, requestCode);
        }
    }




    /**
     * 多图裁剪时每张对应的裁剪比例
     *
     * @param dataSourceCount
     * @return
     */
    private AspectRatio[] buildAspectRatios(int dataSourceCount) {
        AspectRatio[] aspectRatios = new AspectRatio[dataSourceCount];
        for (int i = 0; i < dataSourceCount; i++) {
            if (i == 0) {
                aspectRatios[i] = new AspectRatio("16:9", 16, 9);
            } else if (i == 1) {
                aspectRatios[i] = new AspectRatio("3:2", 3, 2);
            } else {
                aspectRatios[i] = new AspectRatio("原始比例", 0, 0);
            }
        }
        return aspectRatios;
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private UCrop.Options buildOptions() {
        UCrop.Options options = new UCrop.Options();

        return options;
    }

    /**
     * 自定义压缩
     */
    private static class ImageFileCompressEngine implements CompressFileEngine {

        @Override
        public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
            Luban.with(context).load(source).ignoreBy(100).setRenameListener(new OnRenameListener() {
                @Override
                public String rename(String filePath) {
                    int indexOf = filePath.lastIndexOf(".");
                    String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                    return DateUtils.getCreateFileName("CMP_") + postfix;
                }
            }).filter(new CompressionPredicate() {
                @Override
                public boolean apply(String path) {
                    if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                        return true;
                    }
                    return !PictureMimeType.isUrlHasGif(path);
                }
            }).setCompressListener(new OnNewCompressListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(String source, File compressFile) {
                    if (call != null) {
                        call.onCallback(source, compressFile.getAbsolutePath());
                    }
                }

                @Override
                public void onError(String source, Throwable e) {
                    if (call != null) {
                        call.onCallback(source, null);
                    }
                }
            }).launch();
        }
    }


    /**
     * 自定义压缩
     */
    @Deprecated
    private static class ImageCompressEngine implements CompressEngine {

        @Override
        public void onStartCompress(Context context, ArrayList<LocalMedia> list,
                                    OnCallbackListener<ArrayList<LocalMedia>> listener) {
            // 自定义压缩
            List<Uri> compress = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                LocalMedia media = list.get(i);
                String availablePath = media.getAvailablePath();
                Uri uri = PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(availablePath)
                        ? Uri.parse(availablePath)
                        : Uri.fromFile(new File(availablePath));
                compress.add(uri);
            }
            if (compress.size() == 0) {
                listener.onCall(list);
                return;
            }
            Luban.with(context)
                    .load(compress)
                    .ignoreBy(100)
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path);
                        }
                    })
                    .setRenameListener(new OnRenameListener() {
                        @Override
                        public String rename(String filePath) {
                            int indexOf = filePath.lastIndexOf(".");
                            String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                            return DateUtils.getCreateFileName("CMP_") + postfix;
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(int index, File compressFile) {
                            LocalMedia media = list.get(index);
                            if (compressFile.exists() && !TextUtils.isEmpty(compressFile.getAbsolutePath())) {
                                media.setCompressed(true);
                                media.setCompressPath(compressFile.getAbsolutePath());
                                media.setSandboxPath(SdkVersionUtils.isQ() ? media.getCompressPath() : null);
                            }
                            if (index == list.size() - 1) {
                                listener.onCall(list);
                            }
                        }

                        @Override
                        public void onError(int index, Throwable e) {
                            if (index != -1) {
                                LocalMedia media = list.get(index);
                                media.setCompressed(false);
                                media.setCompressPath(null);
                                media.setSandboxPath(null);
                                if (index == list.size() - 1) {
                                    listener.onCall(list);
                                }
                            }
                        }
                    }).launch();
        }

    }

    /**
     * 创建相机自定义输出目录
     *
     * @return
     */
    private String getSandboxCameraOutputPath() {
        if (true) {
            File externalFilesDir = getContext().getExternalFilesDir("");
            File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
            if (!customFile.exists()) {
                customFile.mkdirs();
            }
            return customFile.getAbsolutePath() + File.separator;
        } else {
            return "";
        }
    }

    /**
     * 创建音频自定义输出目录
     *
     * @return
     */
    private String getSandboxAudioOutputPath() {
        if (true) {
            File externalFilesDir = getContext().getExternalFilesDir("");
            File customFile = new File(externalFilesDir.getAbsolutePath(), "Sound");
            if (!customFile.exists()) {
                customFile.mkdirs();
            }
            return customFile.getAbsolutePath() + File.separator;
        } else {
            return "";
        }
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxPath() {
        File externalFilesDir = getContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxMarkDir() {
        File externalFilesDir = getContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Mark");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getVideoThumbnailDir() {
        File externalFilesDir = getContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Thumbnail");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_back:
                finish();
                break;
            case R.id.minus:
                if (maxSelectNum > 1) {
                    maxSelectNum--;
                }
                mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum);
                break;
            case R.id.plus:
                maxSelectNum++;
                mAdapter.setSelectMax(maxSelectNum + maxSelectVideoNum);
                break;

            case R.id.video_minus:
                if (maxSelectVideoNum > 1) {
                    maxSelectVideoNum--;
                }
                mAdapter.setSelectMax(maxSelectVideoNum + maxSelectNum);
                break;
            case R.id.video_plus:
                maxSelectVideoNum++;
                mAdapter.setSelectMax(maxSelectVideoNum + maxSelectNum);
                break;
            case R.id.exportBut:
                 export();
                break;
            case R.id.delBut:
                 delImg();
                break;
            case R.id.saveBut:
                 saveImg();
                break;
            case R.id.right_setting:
                 setting();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

    }

    private void setWeChatStyle(){
        SelectMainStyle numberSelectMainStyle = new SelectMainStyle();
        numberSelectMainStyle.setSelectNumberStyle(true);
        numberSelectMainStyle.setPreviewSelectNumberStyle(false);
        numberSelectMainStyle.setPreviewDisplaySelectGallery(true);
        numberSelectMainStyle.setSelectBackground(R.drawable.ps_default_num_selector);
        numberSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_preview_checkbox_selector);
        numberSelectMainStyle.setSelectNormalBackgroundResources(R.drawable.ps_select_complete_normal_bg);
        numberSelectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_53575e));
        numberSelectMainStyle.setSelectNormalText(getString(R.string.ps_send));
        numberSelectMainStyle.setAdapterPreviewGalleryBackgroundResource(R.drawable.ps_preview_gallery_bg);
        numberSelectMainStyle.setAdapterPreviewGalleryItemSize(DensityUtil.dip2px(getContext(), 52));
        numberSelectMainStyle.setPreviewSelectText(getString(R.string.ps_select));
        numberSelectMainStyle.setPreviewSelectTextSize(14);
        numberSelectMainStyle.setPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        numberSelectMainStyle.setPreviewSelectMarginRight(DensityUtil.dip2px(getContext(), 6));
        numberSelectMainStyle.setSelectBackgroundResources(R.drawable.ps_select_complete_bg);
        numberSelectMainStyle.setSelectText(getString(R.string.ps_send_num));
        numberSelectMainStyle.setSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
        numberSelectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
        numberSelectMainStyle.setCompleteSelectRelativeTop(true);
        numberSelectMainStyle.setPreviewSelectRelativeBottom(true);
        numberSelectMainStyle.setAdapterItemIncludeEdge(false);

        // 头部TitleBar 风格
        TitleBarStyle numberTitleBarStyle = new TitleBarStyle();
        numberTitleBarStyle.setHideCancelButton(true);
        numberTitleBarStyle.setAlbumTitleRelativeLeft(true);
        if (true) {
            numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_demo_only_album_bg);
        } else {
            numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_album_bg);
        }
        numberTitleBarStyle.setTitleDrawableRightResource(R.drawable.ps_ic_grey_arrow);
        numberTitleBarStyle.setPreviewTitleLeftBackResource(R.drawable.ps_ic_normal_back);

        // 底部NavBar 风格
        BottomNavBarStyle numberBottomNavBarStyle = new BottomNavBarStyle();
        numberBottomNavBarStyle.setBottomPreviewNarBarBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_half_grey));
        numberBottomNavBarStyle.setBottomPreviewNormalText(getString(R.string.ps_preview));
        numberBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_9b));
        numberBottomNavBarStyle.setBottomPreviewNormalTextSize(16);
        numberBottomNavBarStyle.setCompleteCountTips(false);
        numberBottomNavBarStyle.setBottomPreviewSelectText(getString(R.string.ps_preview_num));
        numberBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));


        selectorStyle.setTitleBarStyle(numberTitleBarStyle);
        selectorStyle.setBottomBarStyle(numberBottomNavBarStyle);
        selectorStyle.setSelectMainStyle(numberSelectMainStyle);



        PictureWindowAnimationStyle defaultAnimationStyle = new PictureWindowAnimationStyle();
        defaultAnimationStyle.setActivityEnterAnimation(R.anim.ps_anim_enter);
        defaultAnimationStyle.setActivityExitAnimation(R.anim.ps_anim_exit);
        selectorStyle.setWindowAnimationStyle(defaultAnimationStyle);

        videoPlayerEngine = null;

        animationMode = AnimationType.DEFAULT_ANIMATION;

        language = LanguageConfig.CHINESE;

        chooseMode = SelectMimeType.ofAll();

        imageEngine = GlideEngine.createGlideEngine();

        resultMode = 0;
    }

    //https://blog.csdn.net/a840347/article/details/102602241
    private void shareOneFile(String filePath){
        File file = new File(filePath);
        if (null != file && file.exists()) {
            Uri uri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //通过FileProvider创建一个content类型的Uri
                uri = FileProvider.getUriForFile(this,getPackageName()+".fileprovider", file);
            }
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM,uri);
            if (filePath.indexOf(".mp4")>-1){
                share.setType("video/*");
            }else{
                share.setType("image/*");
            }
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "分享单个文件"));
        }
    }

    private void shareMultipleFile(){
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i=0;i<selectedFilePathList.size();i++){
             String path=selectedFilePathList.get(i);
             File file=new File(path);
             Uri uri = Uri.fromFile(file);
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //通过FileProvider创建一个content类型的Uri
                uri = FileProvider.getUriForFile(this,getPackageName()+".fileprovider", file);
             }
            uris.add(uri);
        }
        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
        share.setType("*/*");
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, "分享多个文件"));
    }

    private void checkData(){
        selectedFilePathList.clear();
        for (LocalMedia media : mAdapter.getData() ) {
            if (media==null)continue;
            selectedFilePathList.add(media.getRealPath());
        }
    }

    private void export(){
        checkData();
        if (selectedFilePathList.size()==0){
            showSimpleDialog("没有选择文件！",null);
            return;
        }
        if (selectedFilePathList.size()>1){
            shareMultipleFile();
        }else{
            String path=selectedFilePathList.get(0);
            shareOneFile(path);
        }
    }
    private void delImg(){
        checkData();
        if (selectedFilePathList.size()==0){
            showSimpleDialog("没有选择文件！",null);
            return;
        }
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定要删除当前选择的图片/视频吗？删除后不可恢复。");
        //监听下方button点击事件
        builder.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                     try{
                         for (int i=0;i<selectedFilePathList.size();i++){
                             String path=selectedFilePathList.get(i);
                             File file = new File(path);
                             if (file.exists()){
                                 file.delete();
                             }
                         }
                         mAdapter.deleteAll();
                         deleteThumbnailFolder();
                         showSimpleDialog("删除成功！",null);
                     }catch(Exception e){
                         e.printStackTrace();
                     }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void deleteThumbnailFolder(){
        try{
            String folderPath=getVideoThumbnailDir();
            File file = new File(folderPath);
            if (file!=null && file.exists()){
                deleteAllFiles(file);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    private void saveImg(){
        checkData();
        if (selectedFilePathList.size()==0){
            showSimpleDialog("没有选择文件！",null);
            return;
        }

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定要将前选择的图片/视频保存到系统相册吗？");
        //监听下方button点击事件
        builder.setPositiveButton("确定保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFile();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();

    }


    private void saveFile(){
        if (selectedFilePathList.size()==0)return;
        try{
            for (int i=0;i<selectedFilePathList.size();i++){
                String path=selectedFilePathList.get(i);
                File file3 = new File(path);
                if (file3.exists()){
                    boolean isVideo=false;
                    if (path.contains(".mp4")){
                        isVideo=true;
                    }
                    try{
                        SaveFile2Gallery.saveFile2Gallery(this,isVideo,path,file3.getName());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            showSimpleDialog("保存成功！",null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void copyFile(@NonNull String pathFrom, @NonNull String pathTo) {
        if (pathFrom.equalsIgnoreCase(pathTo)) {
            return;
        }
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            inputChannel = new FileInputStream(pathFrom).getChannel();
            outputChannel = new FileOutputStream(pathTo).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputChannel instanceof Closeable) {
                try {
                    inputChannel.close();
                } catch (Exception e) {
                    // silence
                }
            }
            if (outputChannel instanceof Closeable) {
                try {
                    outputChannel.close();
                } catch (Exception e) {
                    // silence
                }
            }
        }
    }


    private BubbleDialog mCurrentDialog;
    private void setting(){
//https://github.com/xujiaji/HappyBubble
        //https://github.com/yangchong211/YCDialog
        //https://www.jianshu.com/p/2adaa6a5f85f?utm_campaign=maleskine&utm_content=note&utm_medium=seo_notes&utm_source=recommendation

        CustomTopMenuDialog codDialog = new CustomTopMenuDialog(this)
                .setClickedView(setImg);
        codDialog.setClickListener(new CustomTopMenuDialog.OnClickCustomButtonListener()
        {
            @Override
            public void onClick(View view,int index)
            {
                if (mCurrentDialog!=null){
                    mCurrentDialog.dismiss();
                }
                if (index==0){
                    clearCache();
                }
                if (index==1){
                    bakSandBoxFile();
                }
                if (index==2){
                    recoverSandBoxFile();
                }
                if (index==3){
                    deleteSandBoxBakFile();
                }
            }
        });
        mCurrentDialog=codDialog;
        mCurrentDialog.show();
    }


    private void clearCache(){
        confirmDialog("确定删除", "取消", "确定要删除缩略图缓存文件夹吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    deleteThumbnailFolder();
                    showSimpleDialog("删除成功！",null);
                }catch(Exception e){
                    e.printStackTrace();
                    showSimpleDialog("删除失败！",null);
                }
            }
        },null);
    }
    private void deleteSandBoxBakFile(){
        confirmDialog("确定删除", "取消", "确定要删除沙盒文件的备份吗？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    String path=sandBoxBakPath();
                    deleteAllFiles(new File(path));
                    showSimpleDialog("删除成功！",null);
                }catch(Exception e){
                    e.printStackTrace();
                    showSimpleDialog("删除失败！",null);
                }
            }
        },null);
    }

    private void bakSandBoxFile(){
        confirmDialog("确定备份", "取消", "确定要将沙盒文件备份到外部吗？如果外部存在备份文件，将会被覆盖！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{

                    File sourceFiles = new File(getSandboxPath());
                    if (sourceFiles==null || sourceFiles.listFiles().length==0){
                        showSimpleDialog("沙盒文件夹无内容，无法备份！",null);
                        return;
                    }


                    showLoading("正在备份，请稍后...");
                    String destPath  = sandBoxBakPath();
                    File destPathFile= new File(destPath);
                    String bakBakPath="";
                    if (destPathFile.exists() && destPathFile.listFiles().length>0){
                         //先重命名，然后备份成功后，再删除
                        String newPath=destPath;
                        if (destPath.endsWith("/")){
                            newPath = destPath.substring(0,destPath.length()-1)+"_bak";
                        }else{
                             newPath=destPath+"_bak";
                        }
                        File newFile=new File(newPath);
                        destPathFile.renameTo(newFile);
                        bakBakPath = newPath;
                        sandBoxBakPath();//如果重名了，就要再重新生成一个新的
                    }
                    String sourcePath= getSandboxPath();
                    bakSandBoxFile2(sourcePath,destPath,bakBakPath);
                }catch(Exception e){
                    e.printStackTrace();
                    hideLoading();
                    showSimpleDialog("备份失败！",null);
                }
            }
        },null);

    }

    private void bakSandBoxFile2(String sourcePath,String destPath,String bakBakPath) throws Exception{
      Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                CopyFileUtil.copyFolder(MainActivity2.this,sourcePath,destPath);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        showSimpleDialog("备份成功！",null);
                        //删除bakbak
                        if (bakBakPath.length()>10){
                            File file = new File(bakBakPath);
                            deleteAllFiles(file);
                            file.delete();
                        }
                    }
                });
            }
        });
      thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
          @Override
          public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      hideLoading();
                      //有异常，删除已备份的文件
                      File file = new File(destPath);
                      deleteAllFiles(file);
                      file.delete();
                      //还原刚才重命名的文件
                      if (bakBakPath.length()>10){
                          File bakBakFile = new File(bakBakPath);
                          File file2= new File(destPath);
                          bakBakFile.renameTo(file2);
                      }
                  }
              });
          }
      });
      thread.start();
    }

    private void recoverSandBoxFile(){
        confirmDialog("确定恢复", "取消", "确定要将备份文件恢复到沙盒文件吗？沙盒文件原有内容将会被覆盖！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    File sourceFiles = new File(sandBoxBakPath());
                    if (sourceFiles==null || sourceFiles.listFiles().length==0){
                        showSimpleDialog("备份文件夹无内容，无法恢复！",null);
                        return;
                    }


                    showLoading("正在恢复，请稍后...");
                    String destPath  = getSandboxPath();
                    File destPathFile= new File(destPath);
                    String bakBakPath="";
                    if (destPathFile.exists() && destPathFile.listFiles().length>0){
                        //先重命名，然后备份成功后，再删除
                        String newPath=destPath;
                        if (destPath.endsWith("/")){
                            newPath = destPath.substring(0,destPath.length()-1)+"_bak";
                        }else{
                            newPath=destPath+"_bak";
                        }
                        File newFile=new File(newPath);
                        destPathFile.renameTo(newFile);
                        bakBakPath = newPath;
                        getSandboxPath();//如果重名了，就要再重新生成一个新的
                    }
                    String sourcePath= sandBoxBakPath();
                    recoverSandBoxFile2(sourcePath,destPath,bakBakPath);
                }catch(Exception e){
                    e.printStackTrace();
                    hideLoading();
                    showSimpleDialog("恢复失败！",null);
                }
            }
        },null);
    }

    private void recoverSandBoxFile2(String sourcePath,String destPath,String bakBakPath) throws Exception{
        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                CopyFileUtil.copyFolder(MainActivity2.this,sourcePath,destPath);
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        showSimpleDialog("恢复成功！",null);
                        //删除bakbak
                        if (bakBakPath.length()>10){
                            File file = new File(bakBakPath);
                            deleteAllFiles(file);
                            file.delete();
                        }
                    }
                });
            }
        });
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        //有异常，删除已备份的文件
                        File file = new File(destPath);
                        deleteAllFiles(file);
                        file.delete();
                        //还原刚才重命名的文件
                        if (bakBakPath.length()>10){
                            File bakBakFile = new File(bakBakPath);
                            File file2= new File(destPath);
                            bakBakFile.renameTo(file2);
                        }
                    }
                });
            }
        });
        thread.start();
    }


    private String sandBoxBakPath(){
       String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
               .getAbsolutePath()+"/DevPicsBak/SandBox/";
       // Log.e(TAG, "sandBoxBakPath: path="+path );
       try{
           File file = new File(path);
           if (!file.exists()){
             boolean isok=   file.mkdirs();
              // Log.e(TAG, "sandBoxBakPath: isok="+isok+", path="+path);
           }
       }catch(Exception e){
           e.printStackTrace();
       }
       return path;
    }

    ProgressDialog dialog=null;
    private void showLoading(String msg){
        dialog = ProgressDialog.show( this, "", msg, true);
    }
    private void hideLoading(){
        if (dialog!=null){
            dialog.dismiss();
        }
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
    private void confirmDialog(String okTitle,String cancelTitle,String msg,
                               android.content.DialogInterface.OnClickListener okListener,
                               android.content.DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder;
        builder=new AlertDialog.Builder(this);
        builder.setMessage(msg);
        //监听下方button点击事件
        builder.setPositiveButton(okTitle,okListener);
        builder.setNegativeButton(cancelTitle,cancelListener);
        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


    @Override
    public void onSelectFinish(@Nullable PictureCommonFragment.SelectorResult result) {
        if (result == null) {
            return;
        }
        if (result.mResultCode == RESULT_OK) {
            ArrayList<LocalMedia> selectorResult = PictureSelector.obtainSelectorList(result.mResultData);
            analyticalSelectResults(selectorResult);
        } else if (result.mResultCode == RESULT_CANCELED) {
            Log.i(TAG, "onSelectFinish PictureSelector Cancel");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST || requestCode == PictureConfig.REQUEST_CAMERA) {
                ArrayList<LocalMedia> result = PictureSelector.obtainSelectorList(data);
                analyticalSelectResults(result);
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.i(TAG, "onActivityResult PictureSelector Cancel");
        }
    }

    /**
     * 创建一个ActivityResultLauncher
     *
     * @return
     */
    private ActivityResultLauncher<Intent> createActivityResultLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        if (resultCode == RESULT_OK) {
                            ArrayList<LocalMedia> selectList = PictureSelector.obtainSelectorList(result.getData());
                            analyticalSelectResults(selectList);
                        } else if (resultCode == RESULT_CANCELED) {
                            Log.i(TAG, "onActivityResult PictureSelector Cancel");
                        }
                    }
                });
    }


    private List<String> selectedFilePathList=new ArrayList<>();

    /**
     * 处理选择结果
     *
     * @param result
     */
    private void analyticalSelectResults(ArrayList<LocalMedia> result) {
        selectedFilePathList.clear();
        for (LocalMedia media : result) {
            if (media.getWidth() == 0 || media.getHeight() == 0) {
                if (PictureMimeType.isHasImage(media.getMimeType())) {
                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(getContext(), media.getPath());
                    media.setWidth(imageExtraInfo.getWidth());
                    media.setHeight(imageExtraInfo.getHeight());
                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(getContext(), media.getPath());
                    media.setWidth(videoExtraInfo.getWidth());
                    media.setHeight(videoExtraInfo.getHeight());
                }
            }
            selectedFilePathList.add(media.getRealPath());
            Log.i(TAG, "文件名: " + media.getFileName());
            Log.i(TAG, "是否压缩:" + media.isCompressed());
            Log.i(TAG, "压缩:" + media.getCompressPath());
            Log.i(TAG, "初始路径:" + media.getPath());
            Log.i(TAG, "绝对路径:" + media.getRealPath());
            Log.i(TAG, "是否裁剪:" + media.isCut());
            Log.i(TAG, "裁剪路径:" + media.getCutPath());
            Log.i(TAG, "是否开启原图:" + media.isOriginal());
            Log.i(TAG, "原图路径:" + media.getOriginalPath());
            Log.i(TAG, "沙盒路径:" + media.getSandboxPath());
            Log.i(TAG, "水印路径:" + media.getWatermarkPath());
            Log.i(TAG, "视频缩略图:" + media.getVideoThumbnailPath());
            Log.i(TAG, "原始宽高: " + media.getWidth() + "x" + media.getHeight());
            Log.i(TAG, "裁剪宽高: " + media.getCropImageWidth() + "x" + media.getCropImageHeight());
            Log.i(TAG, "文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(media.getSize()));
            Log.i(TAG, "文件时长: " + media.getDuration());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isMaxSize = result.size() == mAdapter.getSelectMax();
                int oldSize = mAdapter.getData().size();
                mAdapter.notifyItemRangeRemoved(0, isMaxSize ? oldSize + 1 : oldSize);
                mAdapter.getData().clear();

                mAdapter.getData().addAll(result);
                mAdapter.notifyItemRangeInserted(0, result.size());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() > 0) {
            outState.putParcelableArrayList("selectorList",
                    mAdapter.getData());
        }
    }

    public Context getContext() {
        return this;
    }
}
