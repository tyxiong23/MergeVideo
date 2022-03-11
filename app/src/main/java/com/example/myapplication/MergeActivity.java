package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.myapplication.utils.tools.ClearCache;
import com.example.myapplication.utils.tools.Constants;
import com.example.myapplication.utils.tools.CopyFile;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

public class MergeActivity extends AppCompatActivity {
    private VideoView videoView;
    private MediaController mediaController;
    private Button buttonExit, buttonSave;
    private String videoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        videoView = findViewById(R.id.merge_videoView);
        mediaController = new MediaController(this);
        buttonExit = findViewById(R.id.exit_button);
        buttonSave = findViewById(R.id.save_button);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCache();
                finishAffinity();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToAlbum();

            }
        });


        Bundle bundle = getIntent().getExtras();
        videoPath = bundle.getString("video_path", "");
        File file = new File(videoPath);
        Log.d("file", String.valueOf(file.exists()));
        if (file.exists()) {
            buttonSave.setEnabled(true);
            Log.d("absolute file path", file.getAbsolutePath());
            // 设置播放视频源的路径
            videoView.setVideoPath(file.getAbsolutePath());
            // 为VideoView指定MediaController
            videoView.setMediaController(mediaController);
            // 为MediaController指定控制的VideoView
            mediaController.setMediaPlayer(videoView);
            videoView.requestFocus();



            int vHeight = mediaController.getHeight();

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    int vWidth = mediaPlayer.getVideoWidth(), width = videoView.getMeasuredWidth();
                    Log.d("width", "measured:" + String.valueOf(vWidth) + " width:"+ String.valueOf(width));
                    int vHeight = mediaPlayer.getVideoHeight(), height = videoView.getMeasuredHeight();
                    Log.d("height", "measured:" + String.valueOf(vHeight) + " height:"+ String.valueOf(height));
                    if (vWidth > width || vHeight > height) {
                        //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
                        float wRatio = (float) vWidth / (float) width;
                        float hRatio = (float) vHeight / (float) height;

                        //选择大的一个进行缩放
                        float ratio = Math.max(wRatio, hRatio);

                        vWidth = (int) Math.ceil((float) vWidth / ratio);
                        vHeight = (int) Math.ceil((float) vHeight / ratio);

//                        LinearLayout ll = new LinearLayout(getApplicationContext());
//                        ll.setBackgroundColor(Color.BLACK); //设置layout背景为黑色
//                        LinearLayout.LayoutParams lp1 =  new LinearLayout.LayoutParams(width,height);
//                        lp1.gravity = Gravity.CENTER;
//                        ll.setLayoutParams(lp1);
//
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(vWidth, vHeight);
                        layoutParams.gravity = Gravity.CENTER;
//                        Log.d("original layout", videoView.getLayoutParams().toString());

                        //设置surfaceView的布局参数
                        videoView.setLayoutParams(layoutParams);
                    }
                    videoView.seekTo(1);
                }
            });

//            if (vWidth > videoView.getWidth() || vHeight > videoView.getHeight()) {
//                //如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
//                float wRatio = (float) vWidth / (float) video_view.getWidth();
//                float hRatio = (float) vHeight / (float) video_view.getHeight();
//
//                //选择大的一个进行缩放
//                float ratio = Math.max(wRatio, hRatio);
//
//                vWidth = (int) Math.ceil((float) vWidth / ratio);
//                vHeight = (int) Math.ceil((float) vHeight / ratio);
//
//                //设置surfaceView的布局参数
//                videoView.setLayoutParams(new LinearLayout.LayoutParams(vWidth, vHeight));
//
//            }
        } else {
            Toast.makeText(this,"视频路径解析失败", Toast.LENGTH_SHORT);
            buttonSave.setEnabled(false);
        }
    }

    private void clearCache() {
        Log.e("cache", getApplicationContext().getExternalCacheDir().getParent());
        String cacheDir = Constants.getCacheDir();
        int count = ClearCache.delFolder(cacheDir);
        int count1 = ClearCache.delAllFile(Constants.TEMPDIR);
        Toast.makeText(getApplicationContext(), "清空缓存 [" + count + "," + count1 + "] " + cacheDir, Toast.LENGTH_SHORT).show();
    }

    private void saveToAlbum() {
        String save_dir = Constants.getSaveDir();
        File saveFileDir = new File(save_dir);
        if (!saveFileDir.exists())
            saveFileDir.mkdirs();

        String short_path =  Constants.getId() + "_final.mp4";
        File[] media_dirs = getApplicationContext().getExternalMediaDirs();
        if (media_dirs.length > 0){
            String mediaDir = media_dirs[0].getAbsolutePath();
            System.out.println(media_dirs[0].exists());
            String save_path = mediaDir + "/" + short_path;

            System.out.println(getApplicationContext().getExternalMediaDirs().length + " length " + getApplicationContext().getExternalMediaDirs()[0]);

            boolean result = CopyFile.fileChannelCopy(videoPath, save_path);
            System.out.println("save to media " + result);

            // 扫描到相册
            if (result) {
                File saveVideo = new File(save_path);
                FileNameMap fileNameMap = URLConnection.getFileNameMap();
                String type = fileNameMap.getContentTypeFor(saveVideo.getName());
                MediaScannerConnection mMediaScanner = new MediaScannerConnection(getApplicationContext(), null);
                mMediaScanner.connect();

                Log.d("mMediaScanner", (mMediaScanner !=null) + " " + mMediaScanner.isConnected() + " " + type);
                if (mMediaScanner !=null && mMediaScanner.isConnected()) {
                    mMediaScanner.scanFile(save_path, type);
                }
                Toast.makeText(getApplicationContext(), "[成功保存到相册] " + save_path, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "[保存失败] " + save_path, Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), "[保存失败]", Toast.LENGTH_SHORT).show();
        }
    }
}