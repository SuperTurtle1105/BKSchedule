package com.baggarm.bkschedule.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.daimajia.swipe.SwipeLayout;

/**
 * This class is represent for Task cell in Task recycle view in {@link com.baggarm.bkschedule.controller.today.TodayFragment}
 *
 * @author IMBAGGAARM
 * @since 1.0
 * @version 2019.1706
 */
public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public SwipeLayout swipeLayout;
    public LinearLayout mainView;
    public TextView txtName, txtLocation, txtDetail, txtTime, txtAlert;
    public ImageView imgVAlertIcon;

    public TaskViewHolder(View itemView) {
        super(itemView);
        swipeLayout = itemView.findViewById(R.id.swipe_layout);
        txtName = itemView.findViewById(R.id.txt_name);
        txtLocation = itemView.findViewById(R.id.txt_location);
        txtDetail = itemView.findViewById(R.id.txt_description);
        txtTime = itemView.findViewById(R.id.txt_time);
        txtAlert = itemView.findViewById(R.id.txt_alert);
        imgVAlertIcon = itemView.findViewById(R.id.imgv_alert_icon);
        mainView = itemView.findViewById(R.id.main_view);
    }

    @Override
    public void onClick(View v) {
        System.out.println("hello");
        //TODO: call callback
        //callback.taskRecycleViewDeletedLastItem(v);
    }
}
