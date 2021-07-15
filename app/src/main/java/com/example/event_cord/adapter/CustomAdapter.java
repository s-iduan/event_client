package com.example.event_cord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.event_cord.R;
import com.example.event_cord.model.User;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private List<User> mUserList;
    private Context mContext;

    public CustomAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_row, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.nameView.setText(mUserList.get(position).getName());
        holder.emailView.setText(mUserList.get(position).getEmail());
        holder.levelView.setText("" + mUserList.get(position).getLevel());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        TextView nameView;
        TextView emailView;
        TextView levelView;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameView = mView.findViewById(R.id.name);
            emailView = mView.findViewById(R.id.email);
            levelView = mView.findViewById(R.id.level_text);
        }
    }
}
