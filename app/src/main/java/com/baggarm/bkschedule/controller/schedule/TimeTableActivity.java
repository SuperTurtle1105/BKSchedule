package com.baggarm.bkschedule.controller.schedule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.adapter.TimeTableAdapter;
import com.baggarm.bkschedule.model.LessonTime;

import java.util.ArrayList;

/**
 * This activity is used for show lesson time table to user.
 *
 * @author IMBAGGAARM
 * @version 2019.1306
 * @since 1.0
 */
public class TimeTableActivity extends AppCompatActivity {

    /**
     * List view to show lessons.
     *
     * @since 1.0
     */
    ListView listView;

    /**
     * Custom toolbar to show title.
     *
     * @since 1.0
     */
    Toolbar toolbar;

    /**
     * Time table adapter to binding data and show to listView
     *
     * @see #listView
     * @since 1.0
     */
    TimeTableAdapter timeTableAdapter;

    /**
     * An array that stores lesson time items.
     *
     * @since 1.0
     */
    ArrayList<LessonTime> lessonTimes;

    /**
     * Binding data and show title for activity,
     * create and set value for lessonTimes.
     *
     * @param savedInstanceState saved bundle to restore state of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Giờ học");

        setContentView(R.layout.activity_time_table);
        toolbar = findViewById(R.id.mtoolbar_time_table);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.lv_time_table);
        lessonTimes = new ArrayList<>();
        lessonTimes.add(new LessonTime("06:00 - 06:50", "10'"));
        lessonTimes.add(new LessonTime("07:00 - 07:50", "10'"));
        lessonTimes.add(new LessonTime("08:00 - 08:50", "10'"));
        lessonTimes.add(new LessonTime("09:00 - 09:50", "10'"));
        lessonTimes.add(new LessonTime("10:00 - 10:50", "10'"));
        lessonTimes.add(new LessonTime("11:00 - 11:50", "10'"));
        lessonTimes.add(new LessonTime("12:00 - 12:50", "10'"));
        lessonTimes.add(new LessonTime("13:00 - 13:50", "10'"));
        lessonTimes.add(new LessonTime("14:00 - 14:50", "10'"));
        lessonTimes.add(new LessonTime("15:00 - 15:50", "10'"));
        lessonTimes.add(new LessonTime("16:00 - 16:50", "10'"));
        lessonTimes.add(new LessonTime("17:00 - 17:50", "10'"));
        lessonTimes.add(new LessonTime("18:00 - 18:50", "0'"));
        lessonTimes.add(new LessonTime("18:50 - 19:40", "0'"));
        lessonTimes.add(new LessonTime("19:40 - 20:30", "0'"));
        lessonTimes.add(new LessonTime("20:30 - 21:20", "0'"));
        lessonTimes.add(new LessonTime("21:20 - 22:10", "0'"));

        timeTableAdapter = new TimeTableAdapter(this, R.layout.time_table_item, lessonTimes);
        listView.setAdapter(timeTableAdapter);
    }
}
