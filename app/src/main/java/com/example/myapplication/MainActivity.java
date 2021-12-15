package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.String;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.utils.FullyGridLayoutManager;
import com.example.myapplication.utils.GlideEngine;
import com.example.myapplication.utils.adapter.GridImageAdapter;
import com.example.myapplication.utils.tools.ClearCache;
import com.example.myapplication.utils.tools.Constants;
import com.example.myapplication.utils.tools.CutVideo;
import com.example.myapplication.utils.tools.SelectVideos;
import com.example.myapplication.utils.tools.VideoInfo;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.camera.CustomCameraType;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.MediaExtraInfo;
import com.luck.picture.lib.manager.PictureCacheManager;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.MediaUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.yalantis.ucrop.view.OverlayView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button next_button, clear_cache_button;
    private EditText input_text;
    private RecyclerView videoRecycle;
    private GridImageAdapter mAdapter;
    private ActivityResultLauncher<Intent> launcherResult;
    private int selectMax = 10;
    private List<VideoInfo> videoInfos;
    private int interval = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String cacheDir = getApplicationContext().getExternalCacheDir().getParent() + "/files/Movies";
        Constants.updateCacheDir(cacheDir);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        next_button = binding.buttonNext;
        clear_cache_button = binding.clearCache;
        input_text = binding.editTextTextMultiLine;
        videoRecycle = binding.recycler;
        next_button.setClickable(true);
        videoInfos = new ArrayList<>();

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentences = input_text.getText().toString();
                Intent editIntent = new Intent(MainActivity.this, EditActivity.class);
                editIntent.putExtra("sentences", sentences);
//                next_button.setClickable(false);
                String outputDir = Constants.getRunningDir();
                List<String> cutResults = CutVideo.clipVideos(videoInfos, outputDir, interval);
                String[] results = new String[cutResults.size()];
                for (int i = 0; i < cutResults.size(); ++i) {
                    results[i] = cutResults.get(i);
                }
                editIntent.putExtra("cut_files", results);
                List<LocalMedia> selectList = new ArrayList<>();
                mAdapter.setList(selectList);
                mAdapter.notifyDataSetChanged();
                input_text.setText("");
                startActivity(editIntent);
            }
        });

        clear_cache_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("cache", getApplicationContext().getExternalCacheDir().getParent());
                String cacheDir = Constants.getCacheDir();
                int count = ClearCache.delFolder(cacheDir);
                Toast.makeText(getApplicationContext(), "清空缓存 [" + count + "] " + cacheDir, Toast.LENGTH_SHORT).show();
            }
        });

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        videoRecycle.setLayoutManager(manager);

        mAdapter = new GridImageAdapter(this, onAddPicClickListener);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("selectorList") != null) {
            mAdapter.setList(savedInstanceState.getParcelableArrayList("selectorList"));
        }
        mAdapter.setSelectMax(selectMax);
        mAdapter.setOnItemClickListener((v, position) -> {
            List<LocalMedia> selectList = mAdapter.getData();
            if (selectList.size() > 0) {
                LocalMedia media = selectList.get(position);
                String mimeType = media.getMimeType();
                int mediaType = PictureMimeType.getMimeType(mimeType);
                switch (mediaType) {
                    case PictureConfig.TYPE_VIDEO:
                        // 预览视频
                        PictureSelector.create(MainActivity.this)
                                .themeStyle(R.style.picture_default_style)
                                .externalPictureVideo(TextUtils.isEmpty(media.getAndroidQToPath()) ? media.getPath() : media.getAndroidQToPath());
                        break;
                    default:
                        break;
                }
            }
        });
        videoRecycle.setAdapter(mAdapter);

        // 注册广播
        BroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);

        launcherResult = createActivityResultLauncher();


        

    }

    @Override
    protected void onStart() {
        super.onStart();
        SelectVideos.clear();
    }

    private final GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(MainActivity.this)
                    .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                    .isWeChatStyle(true)// 是否开启微信图片选择风格
                    .isUseCustomCamera(false)// 是否使用自定义相机
                    .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
//                    .setQuerySandboxDirectory(createCustomCameraOutPath())// 查询自定义相机输出目录
                    .isGetOnlySandboxDirectory(false) // 是否只显示某个目录下的资源；需与setQuerySandboxDirectory相对应
                    .maxVideoSelectNum(selectMax) // 视频最大选择数量
                    .minVideoSelectNum(1)// 视频最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .isReturnEmpty(true)// 未选择数据时点击按钮是否可以返回
                    .isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                    .isDisplayOriginalSize(true)// 是否显示原文件大小，isOriginalImageControl true有效

                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .isPreviewVideo(true)// 是否可预览视频
                    .isCompress(false)// 是否压缩
                    .synOrAsy(false)//同步true或异步false 压缩 默认同步
                    .minimumCompressSize(100)// 小于多少kb的图片不压缩
                    //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                    //.forResult(new MyResultCallback(mAdapter));
                    .forResult(launcherResult);


        }
    };


    private ActivityResultLauncher<Intent> createActivityResultLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        if (resultCode == RESULT_OK) {
                            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(result.getData());
                            videoInfos.clear();
                            // 例如 LocalMedia 里面返回五种path
                            // 1.media.getPath(); 原图path
                            // 2.media.getCutPath();裁剪后path，需判断media.isCut();切勿直接使用
                            // 3.media.getCompressPath();压缩后path，需判断media.isCompressed();切勿直接使用
                            // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                            // 5.media.getAndroidQToPath();Android Q版本特有返回的字段，但如果开启了压缩或裁剪还是取裁剪或压缩路径；注意：.isAndroidQTransform 为false 此字段将返回空
                            // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
                            for (LocalMedia media : selectList) {
                                Log.d("media", media.toString());
                                videoInfos.add(new VideoInfo(media.getAndroidQToPath(), media.getDuration()));
                                if (media.getWidth() == 0 || media.getHeight() == 0) {
                                    if (PictureMimeType.isHasImage(media.getMimeType())) {
//                                        Log.d("media path", media.getPath());
                                        MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(media.getPath());
                                        media.setWidth(imageExtraInfo.getWidth());
                                        media.setHeight(imageExtraInfo.getHeight());
                                    } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                                        MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(getParent(), media.getPath());
                                        media.setWidth(videoExtraInfo.getWidth());
                                        media.setHeight(videoExtraInfo.getHeight());
                                    }
                                }
                                // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
                            }
                            Log.d("videoinfos", videoInfos.toString());
                            mAdapter.setList(selectList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    /**
     * 清空缓存包括裁剪、压缩、AndroidQToPath所生成的文件，注意调用时机必须是处理完本身的业务逻辑后调用；非强制性
     */
    private void clearCache() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(getParent(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //PictureCacheManager.deleteCacheDirFile(this, PictureMimeType.ofImage());
            PictureCacheManager.deleteAllCacheDirRefreshFile(getParent());
        } else {
            PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (BroadcastAction.ACTION_DELETE_PREVIEW_POSITION.equals(action)) {
                // 外部预览删除按钮回调
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int position = extras.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION);
                    ToastUtils.s(getParent().getApplicationContext(), "delete image index:" + position);
                    mAdapter.remove(position);
                    mAdapter.notifyItemRemoved(position);
                }
            }
        }
    };
}