package com.example.aiderchat_proj.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiderchat_proj.classes.MessageBox;
import com.example.aiderchat_proj.databinding.RecyclerviewMessageBinding;

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
        holder.rvMessageBinding.executePendingBindings();
//        View view1 = holder.rvMessageBinding.messageBoxLL.getChildAt(0);
//        View view2 = holder.rvMessageBinding.messageBoxLL.getChildAt(1);
//        if(!messageBox.getId()){
//            holder.rvMessageBinding.messageBoxLL.removeAllViews();
//            holder.rvMessageBinding.messageBoxLL.addView(view2);
//            holder.rvMessageBinding.messageBoxLL.addView(view1);
//        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return messages.size();
    }


    // view holder is the item of the recyclerview
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
//        TextView nickname;
//        TextView content;
        RecyclerviewMessageBinding rvMessageBinding;

        ViewHolder(@NonNull RecyclerviewMessageBinding binding) {
            super(binding.getRoot());
            rvMessageBinding = binding;
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
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

    public void ReorderText(View view)
    {
        LinearLayout myLinearLayout = (LinearLayout) view;
        int childcount = myLinearLayout.getChildCount();
        // create array
        View[] children = new View[childcount];

        // get children of linearlayout
        for (int i=0; i < childcount; i++){
            children[i] = myLinearLayout.getChildAt(i);
        }

        //now remove all children
        myLinearLayout.removeAllViews();

        //and resort, first position
        myLinearLayout.addView(children[1]);
        //second position
        myLinearLayout.addView(children[0]);
    }
}
