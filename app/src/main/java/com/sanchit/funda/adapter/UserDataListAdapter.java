package com.sanchit.funda.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanchit.funda.MainActivity;
import com.sanchit.funda.R;
import com.sanchit.funda.activity.UserDataCaptureActivity;
import com.sanchit.funda.dao.UserDataDao;
import com.sanchit.funda.dao.entity.UserDataModel;
import com.sanchit.funda.database.AppDatabase;
import com.sanchit.funda.database.DatabaseHelper;
import com.sanchit.funda.utils.ViewUtils;

import java.util.List;

public class UserDataListAdapter extends RecyclerView.Adapter<UserDataListAdapter.ViewHolder> {

    private final Activity activity;
    private final List<UserDataModel> itemList;

    public UserDataListAdapter(Activity activity, List<UserDataModel> model) {
        this.activity = activity;
        this.itemList = model;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_user_data_entries, null);
        ViewUtils.setRecyclerViewItemLayoutParams(layoutView);
        UserDataListAdapter.ViewHolder rcv = new UserDataListAdapter.ViewHolder(layoutView, activity);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDataModel p = itemList.get(position);

        holder.PAN = p.pan;
        holder.filePath = p.uri;

        holder.textViewName.setText(p.name);
        holder.textViewPAN.setText(p.pan.substring(0, 3) + "*");
        holder.textViewId.setText(String.valueOf(p.id));
        holder.textViewFileType.setText(p.fileType);

        String fileName = p.uri.substring(p.uri.lastIndexOf("/") + 1);
        holder.textViewFile.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewName;
        private final TextView textViewPAN;
        private final TextView textViewFile;
        private final TextView textViewId;
        private final TextView textViewFileType;

        private final Button buttonSelect;
        private final Button buttonDelete;
        private final SharedPreferences sharedPref;

        private final Context context;

        private String filePath;
        private String PAN;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textViewName = itemView.findViewById(R.id.card_text_view_user_detail_name);
            this.textViewPAN = itemView.findViewById(R.id.card_text_view_user_detail_pan);
            this.textViewFile = itemView.findViewById(R.id.card_text_view_user_detail_file);
            this.textViewId = itemView.findViewById(R.id.card_text_view_user_detail_id);
            this.textViewFileType = itemView.findViewById(R.id.card_text_view_user_detail_file_type);

            this.buttonSelect = itemView.findViewById(R.id.card_button_user_detail_select);
            this.buttonDelete = itemView.findViewById(R.id.card_button_user_detail_delete);

            this.context = context;
            sharedPref = context.getSharedPreferences(context.getString(R.string.main_preference_file_key), Context.MODE_PRIVATE);

            this.buttonSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = textViewName.getText().toString();
                    String fileType = textViewFileType.getText().toString();

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(context.getString(R.string.investor_name), name);
                    editor.putString(context.getString(R.string.investor_PAN), PAN);
                    editor.putString(context.getString(R.string.investor_ecas_file_path), filePath);
                    editor.putString(context.getString(R.string.investor_ecas_file_type), fileType);
                    editor.commit();

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("uri", Uri.parse(filePath));
                    i.putExtra("PAN", PAN);
                    i.putExtra("name", name);
                    i.putExtra("fileType", fileType);
                    context.startActivity(i);
                }
            });

            this.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = textViewName.getText().toString();
                    String PAN = textViewPAN.getText().toString();
                    String uri = textViewFile.getText().toString();
                    String id = textViewId.getText().toString();
                    String fileType = textViewFileType.getText().toString();

                    UserDataDao dao = AppDatabase.getInstance(context).userDataDao();
                    UserDataModel item = new UserDataModel(name, PAN, uri, fileType);
                    item.id = Long.valueOf(id);
                    DatabaseHelper.delete(dao, item);

                    reloadUserProfiles();
                }
            });
        }

        @Override
        public void onClick(View v) {
        }

        public void reloadUserProfiles() {
            UserDataCaptureActivity userDataCaptureActivity = (UserDataCaptureActivity) context;
            userDataCaptureActivity.loadSavedProfiles();
        }
    }


}
