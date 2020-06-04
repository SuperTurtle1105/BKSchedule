package com.baggarm.bkschedule.controller.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.adapter.SubjectAdapter;
import com.baggarm.bkschedule.api.LocalScheduleAPI;
import com.baggarm.bkschedule.helper.Extensions;
import com.baggarm.bkschedule.model.LearningSchedule;
import com.baggarm.bkschedule.model.Subject;
import com.baggarm.bkschedule.model.viewModel.SubjectVM;
import com.baggarm.bkschedule.model.viewModel.TermVM;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to show user's learning schedules, user can see time table and test schedules by starting
 * these activities.
 *
 * @author duynguyen
 * @version 2019.1306
 * @since 1.0
 */
public class ScheduleFragment extends Fragment {

    /**
     * Saved bundle to store <b>lvSubject</b> state, this will be used to restore when user start this fragment again.
     *
     * @see #lvSubject
     * @since 1.0
     */
    private static Bundle mBundleListViewState;
    /**
     * Key to store current in bundle.
     *
     * @see #current
     * @since 1.0
     */
    private final String KEY_CURRENT = "KEY_CURRENT";
    /**
     * Key to store lvSubject state in bundle.
     *
     * @see #lvSubject
     * @since 1.0
     */
    private final String KEY_LIST_STATE = "KEY_LIST_STATE";
    /**
     * Main view of this fragment.
     *
     * @since 1.0
     */
    private View view;
    /**
     * A text view show current term title.
     *
     * @since 1.0
     */
    private TextView txtCurrentTerm;
    /**
     * A button to move to the previous term.
     *
     * @since 1.0
     */
    private Button btnPrevTerm;
    /**
     * A button to move to the next term.
     *
     * @since 1.0
     */
    private Button btnNextTerm;
    /**
     * A button to start TestScheduleActivity to show test schedules for user.
     *
     * @see TestScheduleActivity
     * @since 1.0
     */
    private Button btnTestSchedule;
    /**
     * A button to start TimeTableActivity to show lesson time table for user.
     *
     * @see TimeTableActivity
     * @since 1.0
     */
    private Button btnTimeTable;
    /**
     * A text view show current week of year.
     *
     * @since 1.0
     */
    private TextView txtCurrentWeek;
    /**
     * A list view to show learning schedules of user, this list view will be refreshed when user change semester.
     *
     * @since 1.0
     */
    private ListView lvSubject;
    /**
     * Adapter to binding data to show on ui of <b>lvSubject</b>
     *
     * @see #lvSubject
     * @since 1.0
     */
    private SubjectAdapter adapter;
    /**
     * An array stores view models, to show on <b>lvSubject</b>
     *
     * @see #lvSubject
     * @since 1.0
     */
    private ArrayList<TermVM> termList;
    /**
     * An list stores learning schedule that were gotten in database.
     */
    private ArrayList<LearningSchedule> learningScheduleList;
    /**
     * Current selected index of object in <b>termList</b>;
     *
     * @see #termList
     * @since 1.0
     */
    private int current;

    /**
     * Clear saved state of this fragment.
     *
     * @since 1.0
     */
    public static void clearCache() {
        mBundleListViewState = null;
    }

    /**
     * Inflate view and get stored bundle if it exists.
     *
     * @param inflater           to inflate view
     * @param container          container of this fragment
     * @param savedInstanceState the saved bundle
     * @return view was inflated by inflater
     * @since 1.0
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        if (mBundleListViewState != null) {
            current = mBundleListViewState.getInt(KEY_CURRENT);
        } else {
            current = 0;
        }
        return view;
    }

    /**
     * Set title for this fragment and binding views, add listeners.
     *
     * @param savedInstanceState
     * @since 1.0
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Thời khóa biểu");

        txtCurrentWeek = view.findViewById(R.id.txtCurrentWeek);
        txtCurrentTerm = view.findViewById(R.id.txtCurterm);
        btnPrevTerm = view.findViewById(R.id.btn_prev_term);
        btnNextTerm = view.findViewById(R.id.btn_next_term);
        btnTestSchedule = view.findViewById(R.id.btn_test_schedule);
        lvSubject = view.findViewById(R.id.lvSubject);
        btnTimeTable = view.findViewById(R.id.btn_time_table);

        btnTestSchedule.setOnClickListener(v -> startTestScheduleActivity());

        btnTimeTable.setOnClickListener(v -> startTimeTableActivity());
    }

    /**
     * Show current week and get data from database. If data does not exist, call <b>handleEmptyData</b>.
     * After that, show schedules in semester selected
     * by <b>current</b> in <b>termList</b> and add listeners for changing semester buttons.
     *
     * @see #handleEmptyData()
     * @see #current
     * @see #termList
     * @since 1.0
     */
    @Override
    public void onStart() {
        super.onStart();
        //set current week
        txtCurrentWeek.setText("Tuần " + Extensions.getCurrentWeekYearString());

        ArrayList<LearningSchedule> learningSchedulesList = getLearningSchedules();

        if (learningSchedulesList == null) {
            handleEmptyData();
            return;
        }

        setDataForTermList(learningSchedulesList);
        setAdapter();
        setVisibilityForChangeSemesterButtonsIfNeeded();
        //set current term title
        txtCurrentTerm.setText(termList.get(current).getTermName());

        //set listeners
        btnNextTerm.setOnClickListener(v1 -> moveToNextTerm());
        btnPrevTerm.setOnClickListener(v12 -> moveToPrevTerm());
    }

    /**
     * Save state of <b>lvSubject</b>, and <b>current</b> to <b>mBundleListViewState</b> to restore later.
     *
     * @see #lvSubject
     * @see #current
     * @see #mBundleListViewState
     * @since 1.0
     */
    @Override
    public void onPause() {
        super.onPause();

        mBundleListViewState = new Bundle();
        Parcelable listState = lvSubject.onSaveInstanceState();

        mBundleListViewState.putInt(KEY_CURRENT, current);
        mBundleListViewState.putParcelable(KEY_LIST_STATE, listState);
    }

    /**
     * Restore the old state of this fragment.
     *
     * @since 1.0
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mBundleListViewState != null) {
            Parcelable listState = mBundleListViewState.getParcelable(KEY_LIST_STATE);
            lvSubject.onRestoreInstanceState(listState);
        }
    }

    /**
     * Start <b>TestScheduleActivity</b>.
     *
     * @see TestScheduleActivity
     * @since 1.0
     */
    private void startTestScheduleActivity() {
        Intent intent = new Intent(getActivity(), TestScheduleActivity.class);
        startActivity(intent);
    }

    /**
     * Start <b>TimeTableActivity</b>
     *
     * @see TimeTableActivity
     * @since 1.0
     */
    private void startTimeTableActivity() {
        Intent intent = new Intent(getActivity(), TimeTableActivity.class);
        startActivity(intent);
    }

    /**
     * Show a toast to alert that there is no schedule data, and make views are invisible.
     *
     * @since 1.0
     */
    private void handleEmptyData() {
        //Toast that does not have data
        Toast.makeText(getContext(), "Không có dữ liệu về thời khoá biểu.", Toast.LENGTH_SHORT).show();
        btnNextTerm.setVisibility(View.INVISIBLE);
        btnPrevTerm.setVisibility(View.INVISIBLE);
        txtCurrentTerm.setVisibility(View.INVISIBLE);
    }

    /**
     * Get learning schedules from database
     *
     * @return a list of learning schedules was gotten from database (nullable).
     * @since 1.0
     */
    private ArrayList<LearningSchedule> getLearningSchedules() {
        learningScheduleList = LocalScheduleAPI.getInstance().getLearningSchedule();
        return learningScheduleList;
    }

    /**
     * Set visibility for <b>btnNextTerm</b> and <b>btnPrevTerm</b> based on <b>current</b>
     *
     * @see #btnNextTerm
     * @see #btnPrevTerm
     * @see #current
     * @since 1.0
     */
    private void setVisibilityForChangeSemesterButtonsIfNeeded() {
        if (current == 0) {
            btnNextTerm.setVisibility(View.INVISIBLE);
        }
        if (current == termList.size() - 1) {
            btnPrevTerm.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Get subjects in current semester and set adapter for <b>lvSubject</b>.
     *
     * @see #lvSubject
     * @since 1.0
     */
    private void setAdapter() {
        ArrayList<SubjectVM> subjects = new ArrayList<>();
        subjects.addAll(termList.get(current).getTermSubjects());

        adapter = new SubjectAdapter(getActivity(), R.layout.learning_schedule_lv_item, subjects);
        lvSubject.setAdapter(adapter);
    }

    /**
     * Transform data form <b>Subject</b> to <b>SubjectVM</b> and add to <b>termList</b>
     *
     * @param learningScheduleList
     * @see Subject
     * @see SubjectVM
     * @see #termList
     * @since 1.0
     */
    private void setDataForTermList(ArrayList<LearningSchedule> learningScheduleList) {
        termList = new ArrayList<>();
        for (int i = 0; i < learningScheduleList.size(); i++) {
            List<Subject> subjectList = learningScheduleList.get(i).subjectList;
            List<SubjectVM> arrSubject = new ArrayList<>();

            for (Subject subject : subjectList) {
                SubjectVM subject1 = new SubjectVM(subject);
                arrSubject.add(subject1);
            }

            termList.add(new TermVM(learningScheduleList.get(i).ten_hocky, arrSubject));
        }
    }

    /**
     * This method will be triggered when user tap on <b>btnNextTerm</b>. This method will check if
     * can move to next term (avoid exception IndexOutOfRange) or not. If can move to next term,
     * <b>lvSubject</b> will be set adapter for new data.
     * <p>
     * It also checks to show or hide <b>btnNextTerm</b> and <b>btnPrevTerm</b> and update the title
     * of <b>txtCurrentTerm</b>.
     *
     * @see #btnPrevTerm
     * @see #btnNextTerm
     * @see #txtCurrentTerm
     * @since 1.0
     */
    private void moveToNextTerm() {
        int nextCurrent = current - 1;
        if (nextCurrent < 0)
            return;

        current = nextCurrent;
        setAdapter();

        if (current == 0) {
            btnNextTerm.setVisibility(View.INVISIBLE);
        }
        if (current != termList.size() - 1) {
            btnPrevTerm.setVisibility(View.VISIBLE);
        }
        txtCurrentTerm.setText(termList.get(current).getTermName());
    }

    /**
     * This method will be triggered when user tap on <b>btnPrevTerm</b>. This method will check if
     * can move to previous term (avoid exception IndexOutOfRange) or not. If can move to previous term,
     * <b>lvSubject</b> will be set adapter for new data.
     * <p>
     * It also checks to show or hide <b>btnNextTerm</b> and <b>btnPrevTerm</b> and update the title
     * of <b>txtCurrentTerm</b>.
     *
     * @see #btnPrevTerm
     * @see #btnNextTerm
     * @see #txtCurrentTerm
     * @since 1.0
     */
    private void moveToPrevTerm() {
        int nextCurrent = current + 1;
        if (nextCurrent >= termList.size())
            return;

        current = nextCurrent;
        setAdapter();

        if (current == termList.size() - 1) {
            btnPrevTerm.setVisibility(View.INVISIBLE);
        }

        if (current != 0) {
            btnNextTerm.setVisibility(View.VISIBLE);
        }

        txtCurrentTerm.setText(termList.get(current).getTermName());
    }
}
