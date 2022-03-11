package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.databinding.ActivityEditBinding;
import com.example.myapplication.jni.ConcatUtils;
import com.example.myapplication.jni.FineResult;
import com.example.myapplication.jni.FinetuneUtils;
import com.example.myapplication.utils.edit.EditBlock;
import com.example.myapplication.utils.edit.EditBlockAdapter;
import com.example.myapplication.utils.edit.EditItem;
import com.example.myapplication.utils.edit.EditItemAdapter;
import com.example.myapplication.utils.tools.Constants;
import com.example.myapplication.utils.tools.FFmpegUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;
    private RecyclerView recyclerView;
    private EditBlockAdapter adapter;
    private TextView contentText, themeText;
    private FloatingActionButton next_fab;
    private TextView progress_text;
    private ProgressBar progressBar;
    private LinearLayout progress_layout;
    private List<String> roughVideos = new ArrayList<>();
    private List<String> fineVideos = new ArrayList<>();
    private List<String> startFrames = new ArrayList<>();
    private List<String> endFrames = new ArrayList<>();
    private List<Boolean> ifChoose = new ArrayList<>();
    private List<Float> videoScores = new ArrayList<>(); //每个细剪辑视频的平均得分



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = this.getIntent().getExtras();
        String content = bundle.getString("sentences");
        String theme = bundle.getString("theme");
        themeText = findViewById(R.id.theme_text);
        themeText.setText("主题：" + theme);
        themeText.setTextSize(16);
        contentText = findViewById(R.id.edit_text);
        contentText.setText(content);

        progress_layout = findViewById(R.id.progress_layout_finetune);
        progressBar = findViewById(R.id.progress_bar_finetune);
        progress_text = findViewById(R.id.progress_text_finetune);

        String[] video_strings = bundle.getStringArray("cut_files");
//        Log.d("edit videos!!", edit_files.toString());
        List<EditBlock> listBlock = new ArrayList<>();
        for (int i = 0; i < video_strings.length; ++i) {
            String videoString = video_strings[i];
            String[] videos = videoString.split("#");
            String label = "视频" +  i;

            List<EditItem> items = new ArrayList<>();
            for (String video: videos){
                if (video.length() == 0) continue;
                items.add(new EditItem(video));
                roughVideos.add(video);
                Log.d("EDIT", "add rough " + i + " " + video);
                ifChoose.add(new Boolean(false));
            }
            listBlock.add(new EditBlock(label, items));
        }
        Log.d("list", listBlock.size() + " ");

        recyclerView = findViewById(R.id.edit_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EditBlockAdapter(listBlock, new EditBlockAdapter.OnItemClick() {
            @Override
            public void onItemClick(EditItem item, boolean ifChecked) {
                Log.d("CLICK", String.valueOf(ifChecked) + " " + item.getVideopath());
                int id = roughVideos.indexOf(item.getVideopath());
                ifChoose.set(id, ifChecked);
            }
        });

        recyclerView.setAdapter(adapter);


        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        next_fab = binding.fab;
        next_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> selectedIndexes = new ArrayList<>();
                for (int i = 0; i < ifChoose.size(); ++i){
                    if (ifChoose.get(i)){
                        selectedIndexes.add(i);
                        Log.d("FINETUNE", "select " + i);
                    }
                }


                float[] videoScoresList = new float[videoScores.size()];
                for (int i = 0; i < videoScores.size(); ++i){
                    videoScoresList[i] = videoScores.get(i);
                }
                for (int i: selectedIndexes){
                    videoScoresList[i] += Constants.USER_FINETUNE_ALPHA;
                }

                List<Integer> concatResult = ConcatUtils.concat(videoScoresList,
                        startFrames.toArray(new String[0]), endFrames.toArray(new String[0]));

                List<String> selectedVideos = new ArrayList<>();
                for (int i: concatResult){
                    System.out.println("Select video " + i);
                    selectedVideos.add(fineVideos.get(i));
                }

                String finalNoMusicPath = Constants.getRunningDir() + "/final_noBGM.mp4";
                String finalPath = Constants.getRunningDir() + "/final.mp4";
                Log.d("edit merge path", finalPath);
                new Thread(){
                    @Override
                    public void run() {

                        FFmpegUtils.mergeVideos(selectedVideos, finalNoMusicPath);
                        FFmpegUtils.AddBGMForVideoDelete(finalNoMusicPath, Constants.MUSICPATH, finalPath);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent mergeIntent = new Intent(EditActivity.this, MergeActivity.class);
                                mergeIntent.putExtra("video_path", finalPath);
                                startActivity(mergeIntent);
                            }
                        });

                    }
                }.start();
            }
        });

        Finetune(roughVideos);

    }

    private void Finetune(List<String> inputs){
        new Thread(new Runnable() {
            @Override
            public void run() {
                fineVideos.clear(); startFrames.clear(); endFrames.clear(); videoScores.clear();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        next_fab.setClickable(false);
                        progress_layout.setVisibility(View.VISIBLE);
                    }
                });

                String fineDir = Constants.getRunningDir() + "/finetune";

                for (int i = 0; i < inputs.size(); ++i){
                    FineResult result = FinetuneUtils.fineTune(inputs.get(i), fineDir, i);
                    fineVideos.add(result.videoPath);
                    startFrames.add(result.startFramePath);
                    endFrames.add(result.endFramePath);
                    videoScores.add(result.score);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        next_fab.setClickable(true);
                        progress_layout.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }
}