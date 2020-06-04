package com.baggarm.bkschedule.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baggarm.bkschedule.R;

/**
 * ViewHolder for Task header of recycle view in {@link com.baggarm.bkschedule.controller.today.TodayFragment}
 *
 * @author IMBAGGAARM
 * @since 1.0
 * @version 2019.1706
 */
public class TaskHeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView headerTitle;

    public TaskHeaderViewHolder(View itemView) {
        super(itemView);
        headerTitle = itemView.findViewById(R.id.header_id);
    }
}
