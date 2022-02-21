package com.example.myapplication.utils.edititem;

import android.media.MediaPlayer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.chip.Chip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//public class EditItemAdapter {}
public class EditItemAdapter extends RecyclerView.Adapter<EditItemAdapter.ViewHolder> {
    private List<EditItem> list;
    private OnItemClickListener listener;




    public EditItemAdapter(List<EditItem> list) {this.list = list; }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        VideoView video;
        Chip chip;
//        private MediaController mediaController;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.edit_item_sentence);
            chip = view.findViewById(R.id.chip);
            video = view.findViewById(R.id.video);
            chip.setChecked(false);
//            mediaController = new MediaController(view.getContext());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        int position = holder.getAdapterPosition();
        EditItem editItem = list.get(position);
        String content = editItem.getSentence();
        holder.textView.setText(content);
        holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        File file = new File(editItem.getVideopath());
        VideoView tmp_video = holder.video;
        Chip tmp_chip = holder.chip;
        Log.d("file video edit", editItem.getVideopath());
        if (file.exists()) {
            Log.d("absolute path" + position + " ", file.getAbsolutePath());
            // 设置播放视频源的路径
            tmp_video.setVideoPath(file.getAbsolutePath());
            // 为VideoView指定MediaController
//                videoView.setMediaController(mediaController);
//                // 为MediaController指定控制的VideoView
//                mediaController.setMediaPlayer(videoView);
//                videoView.requestFocus();
            tmp_video.setVisibility(View.VISIBLE);
            tmp_video.setClickable(true);
            tmp_video.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    tmp_video.start();
                    Log.d("click edit video", "clicked!");
                    return true;
                }
            });
            tmp_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    tmp_video.seekTo(1);
                }
            });


            tmp_chip.setEnabled(true);
            tmp_chip.setVisibility(View.VISIBLE);
            tmp_chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "click chip " + position, Toast.LENGTH_SHORT);
                    Log.d("chip1", "position "+ position + " initial " + String.valueOf(list.get(position).getState()));
                    list.get(position).setState(tmp_chip.isChecked());
                    if (tmp_chip.isChecked()){
                        SelectVideos.insert(list.get(position).getVideopath(), position);
                    } else {
                        SelectVideos.remove(list.get(position).getVideopath(), position);
                    }

//                        Log.d("states", String.valueOf(list.get(position).getStates()[rank]));
                }
            });
        } else {
            Log.e("load edit videos", "video doesn't exists!");
            tmp_chip.setVisibility(View.INVISIBLE);
            tmp_chip.setCheckable(false);
            tmp_video.setVisibility(View.INVISIBLE);
        }


        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prints = "print position: " + String.valueOf(position) + "\n" + content;
                Log.d("recycler", prints);
                Toast.makeText(view.getContext(), prints, Toast.LENGTH_SHORT);
                listener.onItemCLick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemCLick(int position);
    }

    public void setOnItemClickListener (OnItemClickListener onItemClickListener) {
        listener = onItemClickListener;
    }

    public List<String> getSelected() {
        List<String> result = new ArrayList<>();
        for (EditItem item : list) {
            if (item.getState()) {
                result.add(item.getVideopath());
            }
        }
        return  result;
    }
}
