package com.baggarm.bkschedule.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.model.viewModel.TestVM;

import java.util.List;

/**
 * This adapter is used to binding data to listView in TestScheduleActivity and show it
 *
 * @author DuyNguyen
 * @version 2019.1506
 * @see com.baggarm.bkschedule.controller.schedule.TestScheduleActivity
 * @see TestViewModel
 * @since 1.0
 */

public class TestAdapter extends ArrayAdapter<TestVM> {
    private FragmentActivity context;
    private int resource;
    /**
     * An array that stores TestViewModel items.
     *
     * @see TestVM
     * @since 1.0
     */
    private List<TestVM> objects;

    /**
     * A constructor that assign a FragmentActivity, recouse integer and a List<TestViewModel>
     * to class field.
     *
     * @param context
     * @param resource
     * @param objects
     */

    public TestAdapter(FragmentActivity context, int resource, List<TestVM> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    /**
     * return a view to assign to ListView at current position
     * It assign a subjectTestName,subjectTestDay,subjectTestRoom
     * and subjectTestTime of a TestViewModel at current
     * position to the TextView and three button in TestScheduleActivity
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @see com.baggarm.bkschedule.controller.schedule.TestScheduleActivity
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(resource, null);

        TextView txtSubjectTest = view.findViewById(R.id.txtSubjectTest);
        Button btnTimeTest = view.findViewById(R.id.btnTimeTest);
        Button btnRoomTest = view.findViewById(R.id.btnRoomTest);
        Button btnDayTest = view.findViewById(R.id.btnDayTest);

        TestVM subject = objects.get(position);
        txtSubjectTest.setText(subject.getSubjectTestName());
        btnRoomTest.setText(subject.getSubjectTestRoom());
        btnDayTest.setText(subject.getSubjectTestDay());
        btnTimeTest.setText(subject.getSubjectTestTime());

        return view;
    }
}
