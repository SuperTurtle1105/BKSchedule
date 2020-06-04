package com.baggarm.bkschedule.controller.schedule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.adapter.TestAdapter;
import com.baggarm.bkschedule.api.LocalScheduleAPI;
import com.baggarm.bkschedule.model.Test;
import com.baggarm.bkschedule.model.TestSchedule;
import com.baggarm.bkschedule.model.viewModel.TestVM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This activity show test schedules of user.
 *
 * @author duynguyen
 * @version 2019.1306
 * @since 1.0
 */
public class TestScheduleActivity extends AppCompatActivity {
    /**
     * A radio button to load test schedules in mid term.
     * @deprecated since 1.1.8
     * @since 1.0
     */
    @Deprecated
    RadioButton rdBtnMidTerm;

    /**
     * A radio button to load test schedules in final term.
     * @deprecated since 1.1.8
     * @since 1.0
     */
    @Deprecated
    RadioButton rdBtnFinalTerm;

    /**
     * Adapter for {@link #lvTestSchedule}.
     *
     * @since 1.0
     */
    TestAdapter adapter;

    /**
     * A list view show test schedules.
     *
     * @since 1.0
     */
    ListView lvTestSchedule;

    /**
     * Contains {@link Test} objects.
     *
     * @since 1.0
     */
    ArrayList<Test> testList = new ArrayList<>();

    /**
     * A custom toolbar for this activity.
     * @deprecated since 1.1.8
     * @since 1.0
     */
    @Deprecated
    Toolbar toolbar;

    boolean isMidTermShowing = true;
    String strSemesterName;
    TextView txtSemester;
    Button btnSwapType;

    List<TestVM> arrMidterm = new ArrayList<>();
    List<TestVM> arrFinal = new ArrayList<>();

    /**
     * Setup UI, load data from database, set adapter for {@link #lvTestSchedule}, and set listeners.
     *
     * @param savedInstanceState a saved bundle to restore state of this activity
     * @since 1.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up UI
        setContentView(R.layout.activity_test_schedule);
        bindingViews();

        ///Load data
        List<TestSchedule> testScheduleList = getTestScheduleFromDatabase();

        //check if contains test schedule
        if (testScheduleList.size() == 0) {
            handleEmptyData();
            return;
        }

        //test schedule is exists
        TestSchedule currentSchedule = testScheduleList.get(0);

        strSemesterName = currentSchedule.ten_hocky;
        testList = currentSchedule.testList;



        for (Test test : testList) {
            TestVM midTerm = new TestVM();
            TestVM finalTerm = new TestVM();

            midTerm.setSubjectTestTime(test.strGioKt);
            midTerm.setSubjectTestRoom(test.strPhongKt);
            midTerm.setSubjectTestName(test.ten_mh);
            midTerm.setSubjectTestDay(test.strNgayKt);

            finalTerm.setSubjectTestTime(test.strGioThi);
            finalTerm.setSubjectTestDay(test.strNgayThi);
            finalTerm.setSubjectTestName(test.ten_mh);
            finalTerm.setSubjectTestRoom(test.strPhongThi);

            arrMidterm.add(midTerm);
            arrFinal.add(finalTerm);
        }

        //sort tests in ascending order
        Collections.sort(arrMidterm);
        Collections.sort(arrFinal);

        ArrayList<TestVM> tests = new ArrayList<>();
        tests.addAll(arrMidterm);

        //set adapter
        adapter = new TestAdapter(this, R.layout.test_schedule_lv_item, tests);
        lvTestSchedule.setAdapter(adapter);

        //set listeners
//        rdBtnMidTerm.setOnClickListener(v -> onBtnMidTermClicked(arrMidterm));
//        rdBtnFinalTerm.setOnClickListener(v -> onBtnFinalTermClicked(arrFinal));
        btnSwapType.setOnClickListener(v -> onBtnSwapTypeClicked());

        txtSemester.setText(strSemesterName + " - " + "Giữa kỳ");
    }

    /**
     * Binding views from layout
     *
     * @since 1.0
     */
    private void bindingViews() {
//        rdBtnMidTerm = findViewById(R.id.rd_giuaki);
//        rdBtnFinalTerm = findViewById(R.id.rd_cuoiki);
        lvTestSchedule = findViewById(R.id.lvLichthi);
        txtSemester = findViewById(R.id.txt_semester);
        btnSwapType = findViewById(R.id.btn_swap_type);
    }

    /**
     * Get test schedules from database.
     *
     * @return A list contains {@link TestSchedule} objects;
     * @since 1.0
     */
    private List<TestSchedule> getTestScheduleFromDatabase() {
        List<TestSchedule> testScheduleList = LocalScheduleAPI.getInstance().getTestSchedule();
        return testScheduleList;
    }

    /**
     * Alert for user there is no test schedule data and hide radio buttons.
     *
     * @since 1.0
     */
    private void handleEmptyData() {
        Toast.makeText(this, "Không có dữ liệu về lịch thi.", Toast.LENGTH_SHORT);
        btnSwapType.setVisibility(View.INVISIBLE);
//        rdBtnFinalTerm.setVisibility(View.INVISIBLE);
//        rdBtnMidTerm.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will clear {@link #adapter} and reload {@link #adapter} with mid term schedules.
     *
     * @param arrMidterm mid term schedules
     * @since 1.0
     */
    private void onBtnMidTermClicked(List<TestVM> arrMidterm) {
        adapter.clear();
        adapter.addAll(arrMidterm);
        adapter.notifyDataSetChanged();
    }

    /**
     * This method will clear {@link #adapter} and reload {@link #adapter} with final term schedules.
     *
     * @param arrFinal final test schedules
     * @since 1.0
     */
    private void onBtnFinalTermClicked(List<TestVM> arrFinal) {
        adapter.clear();
        adapter.addAll(arrFinal);
        adapter.notifyDataSetChanged();
    }

    private void onBtnSwapTypeClicked() {
        isMidTermShowing = !isMidTermShowing;
        adapter.clear();
        if (isMidTermShowing) {
            txtSemester.setText(strSemesterName + " - " + "Giữa kỳ");
            adapter.addAll(arrMidterm);
        } else {
            txtSemester.setText(strSemesterName + " - " + "Cuối kỳ");
            adapter.addAll(arrFinal);
        }
        adapter.notifyDataSetChanged();
    }
}
