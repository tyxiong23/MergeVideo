package com.example.myapplication.utils.edit;

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
public class EditBlockAdapter extends RecyclerView.Adapter<EditBlockAdapter.ViewHolder> implements EditItemAdapter.OnChildClick{
    private List<EditBlock> listBlock;
    private OnItemClick onItemClick;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();




    public EditBlockAdapter(List<EditBlock> list, OnItemClick onItemClick) {
        this.listBlock = list;
        this.onItemClick = onItemClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private RecyclerView recyclerView;
//        private MediaController mediaController;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.edit_block_title);
            recyclerView = view.findViewById(R.id.edit_block_videos);
//            mediaController = new MediaController(view.getContext());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        int position = holder.getAdapterPosition();
        holder.textView.setText(listBlock.get(position).getSentence());
        EditBlock block = listBlock.get(position);
        holder.recyclerView.setAdapter(new EditItemAdapter(block.getBlockVideos() ,this));
        holder.recyclerView.setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return listBlock.size();
    }

    @Override
    public void onChildClick(EditItem data, boolean ifChecked) {
        onItemClick.onItemClick(data, ifChecked);
    }

    public interface OnItemClick{
        void onItemClick(EditItem item, boolean ifChecked);
    }


}
