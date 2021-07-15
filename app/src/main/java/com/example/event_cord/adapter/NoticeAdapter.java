package com.example.event_cord.adapter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.event_cord.R;
import com.example.event_cord.model.Notice;

import java.text.SimpleDateFormat;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
    private List<Notice> mNotices;
    private OnNoticeClickListener mOnNoticeClickListener;
    private OnNoticeDeletionHandler mOnNoticeDeletionHandler;
    private OnNoticeEditHandler mOnNoticeEditHandler;

    public NoticeAdapter(List<Notice> notices, OnNoticeClickListener listener, OnNoticeDeletionHandler deletionHandler, OnNoticeEditHandler onNoticeEditHandler) {
        mNotices = notices;
        mOnNoticeClickListener = listener;
        mOnNoticeDeletionHandler = deletionHandler;
        mOnNoticeEditHandler = onNoticeEditHandler;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.notice_row, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoticeAdapter.NoticeViewHolder holder, int position) {
        holder.mTitleView.setText(mNotices.get(position).getTitle());
        holder.mDescriptionView.setText(mNotices.get(position).getDescription());
        holder.mCreatorView.setText((mNotices.get(position).getUserName()));
        holder.mNotice = mNotices.get(position);
        holder.mDeletionHandler = mOnNoticeDeletionHandler;

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
        String timeStr = sdf.format(mNotices.get(position).getTimestamp());
        holder.mTimeStampView.setText(timeStr);
    }

    @Override
    public int getItemCount() {
        if (mNotices == null) {
            return 0;
        }
        return mNotices.size();
    }

    class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        final View mView;

        TextView mTitleView;
        TextView mDescriptionView;
        TextView mCreatorView;
        TextView mTimeStampView;

        Notice mNotice;
        OnNoticeDeletionHandler mDeletionHandler;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mTitleView = mView.findViewById(R.id.title);
            mDescriptionView = mView.findViewById(R.id.description);
            mCreatorView = mView.findViewById(R.id.creator_name);
            mTimeStampView = mView.findViewById(R.id.last_modified);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem deleteMenuItem = menu.add("Delete");
            MenuItem editMenuItem = menu.add("Edit");
            deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(mDeletionHandler != null) {
                        mDeletionHandler.deleteNotice(mNotice.getId());
                    }
                    return true;
                }
            });
            editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (mOnNoticeEditHandler != null) {
                        mOnNoticeEditHandler.updateNotice(mNotice);
                    }
                    return false;
                }
            });
        }
    }
}
