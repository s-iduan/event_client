package com.example.event_cord.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.event_cord.R;
import com.example.event_cord.model.Event;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.CustomViewHolder> {
    private List<Event> mEventList;
    private OnEventClickListener mListener;
    private OnEventDeletionHandler mDeletionHandler;
    private OnEventEditHandler mOnEventEditHandler;

    public EventAdapter(List<Event> events, OnEventClickListener listener, OnEventDeletionHandler eventDeletionHandler, OnEventEditHandler onEventEditHandler) {
        mEventList = events;
        mListener = listener;
        mDeletionHandler = eventDeletionHandler;
        mOnEventEditHandler = onEventEditHandler;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.event_row, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapter.CustomViewHolder holder, int position) {
        holder.nameView.setText(mEventList.get(position).getName());
        holder.descriptionView.setText(mEventList.get(position).getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
        String timeStr = sdf.format(mEventList.get(position).getLastModifytime());
        holder.lastModifiedView.setText(timeStr);
        holder.mEvent = mEventList.get(position);
        holder.mDeletionHandler = mDeletionHandler;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(mEventList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mEventList == null) {
            return 0;
        }
        return mEventList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public final View mView;

        TextView nameView;
        TextView descriptionView;
        TextView lastModifiedView;

        Event mEvent;
        OnEventDeletionHandler mDeletionHandler;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nameView = mView.findViewById(R.id.name);
            descriptionView = mView.findViewById(R.id.description);
            lastModifiedView = mView.findViewById(R.id.last_modified);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem deleteMenuItem = menu.add("Delete");
            MenuItem editMenuItem = menu.add("Edit");
            deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(v.getContext(), "event id : " + mEvent.getId(), Toast.LENGTH_LONG).show();
                    if (mDeletionHandler != null) {
                        mDeletionHandler.deleteEvent(mEvent.getId());
                    }
                    return true;
                }
            });
            editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(v.getContext(), "event id : " + mEvent.getId(), Toast.LENGTH_LONG).show();
                    if (mOnEventEditHandler != null) {
                        mOnEventEditHandler.editEvent(mEvent);
                    }
                    return false;
                }
            });
        }
    }
}
