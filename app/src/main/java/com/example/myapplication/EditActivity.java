package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.utils.EditItem;
import com.example.myapplication.utils.EditItemAdapter;
import com.example.myapplication.utils.tools.Constants;
import com.example.myapplication.utils.tools.MergeVideo;
import com.example.myapplication.utils.tools.SelectVideos;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityEditBinding;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;
    private RecyclerView recyclerView;
    private EditItemAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = this.getIntent().getExtras();
        String content = bundle.getString("sentences");
        String[] sentences = content.split("\n");
        String[] edit_files = bundle.getStringArray("cut_files");
        List<EditItem> list = new ArrayList<>();
        for (int j = 0; j < sentences.length; ++j) {
            String sent =  sentences[j];
            if (!sent.equals("")) {
                Log.d("test", "[" + sent + "]");
                String[] choices = new String[3];
                for (int i = 0; i < 3; ++i) {
                    if (edit_files.length > 0) {
                        final double d = Math.random();
                        final int rank = (int)(d*1000) % edit_files.length;
                        choices[i] = edit_files[rank];
                        Log.d("choice " + i + "/" + j + " " + rank, edit_files[rank]);
                    } else
                        choices[i] = "";

                }
                list.add(new EditItem(sent, choices));
            }
        }

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

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                List<String> selectedVideos = SelectVideos.getList();
                if (selectedVideos.size() == 0) {
                    Toast.makeText(getApplicationContext(), "没有选择的视频", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String finalPath = Constants.getRunningDir() + "/final.mp4";
                    Log.d("edit merge path", finalPath);
                    try{
                        MergeVideo.mergeVideos(selectedVideos, finalPath);
                        Intent mergeIntent = new Intent(EditActivity.this, MergeActivity.class);
                        mergeIntent.putExtra("video_path", finalPath);
                        startActivity(mergeIntent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}