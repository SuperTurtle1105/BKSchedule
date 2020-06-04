package com.baggarm.bkschedule.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.controller.schedule.TimeTableActivity;
import com.baggarm.bkschedule.model.LessonTime;

import java.util.List;

/**
 * This adapter is used to binding data to listView in TimeTable Activity and show it
 *
 * @author DuyNguyen
 * @version 2019.1506
 * @see TimeTableActivity
 * @see LessonTime
 * @since 1.0
 */
public class TimeTableAdapter extends ArrayAdapter<LessonTime> {

    FragmentActivity context;
    int resource;

    /**
     * An array that stores lesson time items.
     *
     * @since 1.0
     */

    List<LessonTime> dataSource;

    /**
     * A constructor that assign a TimeTableActivity, recouse integer and a List<LessonTime>
     * to class field.
     *
     * @param context
     * @param resource
     * @param dataSource
     */

    public TimeTableAdapter(FragmentActivity context, int resource, List<LessonTime> dataSource) {
        super(context, resource, dataSource);
        this.context = context;
        this.resource = resource;
        this.dataSource = dataSource;
    }

    /**
     * return a view to assign to ListView at current position
     * It assign a time and breaktime of the LessonTime at current
     * position to TextView in TimeTableActivity and setBacgroundColor
     * of the view base on that position
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(resource, null);
        TextView txtId = view.findViewById(R.id.txt_id);
        TextView txtTime = view.findViewById(R.id.txt_time);
        TextView txtBreakTime = view.findViewById(R.id.txt_break_time);

        txtId.setText("" + (position + 1));
        txtTime.setText(dataSource.get(position).time);
        txtBreakTime.setText(dataSource.get(position).breakTime);

        if (position == 0 || position == 15 || position == 16) {
            txtId.setBackgroundColor(ContextCompat.getColor(context, R.color.optionLessonIdColor));
            if (position == 0 || position == 16) {
                txtTime.setBackgroundColor(ContextCompat.getColor(context, R.color.oddLessonColor));
            }
            if (position == 15) {
                txtTime.setBackgroundColor(ContextCompat.getColor(context, R.color.evenLessonColor));
            }

        }
        if (position >= 1 && position <= 5) {
            txtId.setBackgroundColor(ContextCompat.getColor(context, R.color.morningLessonColor));
            txtTime.setBackgroundColor(ContextCompat.getColor(context, R.color.morningLessonColor));
        }

        if (position >= 6 && position <= 11) {
            txtId.setBackgroundColor(ContextCompat.getColor(context, R.color.afternoonLessonColor));
            txtTime.setBackgroundColor(ContextCompat.getColor(context, R.color.afternoonLessonColor));
        }

        if (position >= 12 && position <= 14) {
            txtId.setBackgroundColor(ContextCompat.getColor(context, R.color.nightLessonColor));
            txtTime.setBackgroundColor(ContextCompat.getColor(context, R.color.nightLessonColor));
        }

        if ((position + 1) % 2 == 0) {
            txtBreakTime.setBackgroundColor(ContextCompat.getColor(context, R.color.evenLessonColor));
        } else {
            txtBreakTime.setBackgroundColor(ContextCompat.getColor(context, R.color.oddLessonColor));
        }
        return view;
    }
}
