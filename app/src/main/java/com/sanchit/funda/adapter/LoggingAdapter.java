package com.sanchit.funda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.R;
import com.sanchit.funda.utils.ViewUtils;

import java.util.List;

public class LoggingAdapter extends RecyclerView.Adapter<LoggingAdapter.ViewHolder> {

    private final List<String> data;
    private final Context context;

    public LoggingAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cards_log_line, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        ViewHolder rcv = new ViewHolder(layoutView, context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String s = data.get(position);
        holder.textViewLog.setText(s);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewLog;
        private final Context context;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.textViewLog = itemView.findViewById(R.id.textview_log);
            this.context = context;
        }
    }
}