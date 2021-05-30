package com.example.aider_helper.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aider_helper.classes.MessageBox;
import com.example.aider_helper.databinding.RecyclerviewMessageBinding;

import java.util.List;

public class RecyclerviewChatAdapter extends RecyclerView.Adapter<RecyclerviewChatAdapter.ViewHolder> {

    private List<MessageBox> messages;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    // data is passed into the constructor
    public RecyclerviewChatAdapter(List<MessageBox> data) {
        this.messages = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerviewMessageBinding messageBinding
                = RecyclerviewMessageBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(messageBinding);
    }

    // binds the data to the TextView in each row (show the data in the view holder)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBox messageBox = messages.get(position);
        holder.rvMessageBinding.setMessageBox(messageBox);
        holder.rvMessageBinding.setFontSize(FontSizeUtils.getInstance());
        holder.rvMessageBinding.executePendingBindings();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) mClickListener.onItemClick(v, position);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return messages.size();
    }


    // view holder is the item of the recyclerview
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        RecyclerviewMessageBinding rvMessageBinding;

        ViewHolder(@NonNull RecyclerviewMessageBinding binding) {
            super(binding.getRoot());
            rvMessageBinding = binding;
        }



        @Override
        public boolean onLongClick(View view) {
            if (mLongClickListener != null) {
                mLongClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    // convenience method for getting data at click position
    MessageBox getItem(int id) {
        return messages.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // allows clicks events to be caught
    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener{
        void onItemLongClick(View view, int position);
    }
}
