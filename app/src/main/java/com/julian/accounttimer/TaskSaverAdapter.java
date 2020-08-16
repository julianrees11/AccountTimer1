package com.julian.accounttimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.julian.accounttimer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TaskSaverAdapter extends ArrayAdapter<TaskSaver> {
    private static final String TAG = "ProductListAdapter";

    private Context mContext;
    private int mResource;
    private String currentDate;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView tvClient;
        TextView tvWorkType;
        TextView tvWork;
        TextView tvTime;
        TextView tvDate;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public TaskSaverAdapter(Context context, int resource, ArrayList<TaskSaver> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String client = getItem(position).getClient();
        String workType = getItem(position).getWorkType();
        String work = getItem(position).getWork();
        int time = getItem(position).getTimeInMillis();
        String currentDate = getItem(position).getDate();

        TaskSaver taskSaver = new TaskSaver(client, workType, work, time, currentDate);

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.tvClient = convertView.findViewById(R.id.tvClient);
            holder.tvWorkType = convertView.findViewById(R.id.tvWorkType);
            holder.tvWork = convertView.findViewById(R.id.tvWork);
            holder.tvTime = convertView.findViewById(R.id.tvTime);
            holder.tvDate = convertView.findViewById(R.id.tvDate);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvClient.setText(taskSaver.getClient());
        holder.tvWorkType.setText(taskSaver.getWorkType());
        holder.tvWork.setText(taskSaver.getWork());
        holder.tvDate.setText(taskSaver.getDate());

        int hours = taskSaver.getTimeInMillis() / 1000 / 3600;
        int minutes = ((taskSaver.getTimeInMillis() / 1000) % 3600) / 60;
        int seconds = (taskSaver.getTimeInMillis() / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
        holder.tvTime.setText(timeLeftFormatted);

        holder.tvDate.setText(currentDate);

        return convertView;
    }
}