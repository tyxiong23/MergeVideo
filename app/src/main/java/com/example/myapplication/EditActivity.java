package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.jni.ConcatUtils;
import com.example.myapplication.jni.FineResult;
import com.example.myapplication.jni.FinetuneUtils;
import com.example.myapplication.utils.edititem.EditItem;
import com.example.myapplication.utils.edititem.EditItemAdapter;
import com.example.myapplication.utils.tools.Constants;
import com.example.myapplication.utils.tools.FFmpegUtils;
import com.example.myapplication.utils.edititem.SelectVideos;
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

import com.example.myapplication.databinding.ActivityEditBinding;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private String[] edit_files;
    private ActivityEditBinding binding;
    private RecyclerView recyclerView;
    private EditItemAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView contentText;
    private FloatingActionButton next_fab;
    private TextView progress_text;
    private ProgressBar progressBar;
    private LinearLayout progress_layout;
    private List<String> fineVideos = new ArrayList<>();
    private List<String> startFrames = new ArrayList<>();
    private List<String> endFrames = new ArrayList<>();
    private List<Float> videoScores = new ArrayList<>(); //每个细剪辑视频的平均得分



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = this.getIntent().getExtras();
        String content = bundle.getString("sentences");
        contentText = findViewById(R.id.edit_text);
        contentText.setText(content);
        contentText.setTextSize(18);
        progress_layout = findViewById(R.id.progress_layout_finetune);
        progressBar = findViewById(R.id.progress_bar_finetune);
        progress_text = findViewById(R.id.progress_text_finetune);

        edit_files = bundle.getStringArray("cut_files");
        Log.d("num_videos", String.valueOf(edit_files.length));
//        Log.d("edit videos!!", edit_files.toString());
        List<EditItem> list = new ArrayList<>();
        for (int j = 0; j < edit_files.length; ++j) {
            String[] ss =  edit_files[j].split("/");
            String label = ss[ss.length-1];

//            Log.d("label", "[" + label + "]");
            list.add(new EditItem(label, edit_files[j]));


        }
//        list.clear();
        recyclerView = findViewById(R.id.edit_recycler);
        layoutManager = new LinearLayoutManager(this);
        adapter = new EditItemAdapter(list);
        adapter.setOnItemClickListener(new EditItemAdapter.OnItemClickListener() {
            @Override
            public void onItemCLick(int position) {
//                Toast.makeText(this,,Toast.LENGTH_SHORT)
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        next_fab = binding.fab;
        next_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                List<Integer> selectedIndexes = SelectVideos.getIndexList();
                List<String> selectedVideos = new ArrayList<>();
                float[] scores_list = new float[fineVideos.size()];
                for (int i = 0; i < fineVideos.size(); ++i){
                    scores_list[i] = videoScores.get(i);
                }
                for (int i: selectedIndexes){
                    scores_list[i] += 0.3f;
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

                for (int i: concatResult){
                    System.out.println("Select video " + i);
                    selectedVideos.add(fineVideos.get(i));
                }
//                if (selectedIndexes.size() == 0) {
//                    Toast.makeText(getApplicationContext(), "没有选择的视频", Toast.LENGTH_SHORT).show();
//                    return;
//                }
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

        Finetune(edit_files);

    }

    private void Finetune(String[] videos){
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

                for (int i = 0; i < videos.length; ++i){
                    FineResult result = FinetuneUtils.fineTune(videos[i], fineDir, i);
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