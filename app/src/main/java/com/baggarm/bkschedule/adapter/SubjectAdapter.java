package com.baggarm.bkschedule.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.model.viewModel.SubjectVM;

import java.util.List;

/**
 * This adapter is used to binding data to listView in ScheduleFragment and show it
 *
 * @author DuyNguyen
 * @version 2019.1506
 * @see com.baggarm.bkschedule.controller.schedule.ScheduleFragment
 * @see SubjectVM
 * @since 1.0
 */
public class SubjectAdapter extends ArrayAdapter<SubjectVM> {

    FragmentActivity context;

    int resource;

    /**
     * An array that stores TestViewModel items.
     *
     * @see SubjectVM
     * @since 1.0
     */
    List<SubjectVM> objects;

    /**
     * A constructor that assign a FragmentActivity, recouse integer and a List<SubjectViewModel>
     * to class field.
     *
     * @param context
     * @param resource
     * @param objects
     */
    public SubjectAdapter(FragmentActivity context, int resource, List<SubjectVM> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    /**
     * return a view to assign to ListView at current position
     * It assign field of subjectViewModel object at that position
     * to the controller text in ScheduleFragment, it also handle an
     * event when user click on buttonTime, it will convert text in that
     * button(which contain period) to time.
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
        TextView txtSubject = view.findViewById(R.id.txtSubject);
        TextView txtClass = view.findViewById(R.id.txtClass);
        TextView txtCode = view.findViewById(R.id.txtCode);
        Button btnDay = view.findViewById(R.id.btnDay);
        Button btnTime = view.findViewById(R.id.btnTime);
        Button btnRoom = view.findViewById(R.id.btnRoom);
        TextView txtWeeks = view.findViewById(R.id.txtWeeks);

        final SubjectVM subject = objects.get(position);
        txtSubject.setText(subject.getSubjectName());
        txtClass.setText(subject.getSubjectClass());
        txtCode.setText(subject.getSubjectCode());
        txtWeeks.setText(subject.getSubjectWeeks());
        btnDay.setText(subject.getSubjectDay());
        btnRoom.setText(subject.getSubjectRoom());
        btnTime.setText(subject.getSubjectTime());


        btnTime.setOnClickListener(v -> {
            String time = subject.getSubjectTime();
            if (!subject.isShowTime) {
                //show time
                time = time.replace("Tiết", "").replace(" ", "").replace("\n", "");

                String[] convert = time.split("-");
                int finish;
                if (convert.length == 3) {
                    return;
                } else {
                    try {
                        finish = Integer.parseInt(convert[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }

                int begin = Integer.parseInt(convert[0]);
                if (begin < 14) {
                    btnTime.setText((begin + 5) + "h00\n" + (finish + 5) + "h50");
                } else {
                    btnTime.setText((begin + 4) + "h" + (19 - begin) + "0\n" + (finish + 5) + "h" + (18 - finish) + "0");
                }

                subject.isShowTime = !subject.isShowTime;
            } else {
                //show lesson
                btnTime.setText(subject.getSubjectTime());
                subject.isShowTime = !subject.isShowTime;
            }

        });

        return view;
    }
}
