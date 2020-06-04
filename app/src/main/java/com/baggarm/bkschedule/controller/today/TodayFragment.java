package com.baggarm.bkschedule.controller.today;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.adapter.TaskAdapter;
import com.baggarm.bkschedule.adapter.util.RecyclerViewMargin;
import com.baggarm.bkschedule.api.LocalScheduleAPI;
import com.baggarm.bkschedule.database.TodaySchedule;
import com.baggarm.bkschedule.model.TaskActivityViewMode;
import com.baggarm.bkschedule.model.viewModel.TaskVM;
import com.daimajia.swipe.util.Attributes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A fragment that shows today schedule UI.
 * <p>
 * The default date is today, but user can select other dates that they want, and navigate to prev or
 * next date. User also can create new Task for that date by start NewTaskActivity (by clicking new task button).
 *
 * @author huybaonguyen
 * @version 2019.1306
 * @since 1.0
 */
public class TodayFragment extends Fragment implements TaskAdapter.TaskBlockListener {

    /**
     * Indexes of headers ("ONGOING", "FUTURE", "PAST") in arrayTask that will get from getArrayTaskInDay.
     *
     * @see #getArrayTaskInDay(Date)
     * @since 1.0
     */
    public static ArrayList<Integer> headerIndexes = new ArrayList<>();

    /**
     * An array that stores today schedules were gotten from database.
     *
     * @since 1.0
     */
    public static ArrayList<TodaySchedule> todayScheduleArrayList;

    /**
     * An instance of Calendar, this will stores the date user will select.
     *
     * @since 1.0
     */
    public static Calendar mCalendar = Calendar.getInstance();

    /**
     * Cached recycle view state.
     *
     * @since 1.0
     */
    private static Bundle mBundleRecyclerViewState;
    /**
     * A key to store and get saved mBundleRecyclerViewState.
     *
     * @see #mBundleRecyclerViewState
     * @since 1.0
     */
    private final String KEY_RECYCLER_STATE = "KEY_RECYCLER_STATE";
    /**
     * Main view of this fragment.
     *
     * @since 1.0
     */
    private View view;
    /**
     * Task Recycle view, this will show all tasks in date. Tasks are divided to 3 segments:
     * <ul>
     * <li>ONGOING</li>
     * <li>FUTURE</li>
     * <li>PAST</li>
     * </ul>
     *
     * @since 1.0
     */
    private RecyclerView recyclerView;
    /**
     * Adapter for task recycle view, is used to bind data from task array to task block.
     *
     * @see TaskVM
     * @since 1.0
     */
    private TaskAdapter mAdapter;
    /**
     * The selected date to get schedules.
     *
     * @since 1.0
     */
    private Date mDate;
    /**
     * This button is used to add new task, by start NewTaskActivity.
     *
     * @see NewTaskActivity
     * @since 1.0
     */
    private Button btnAdd;
    /**
     * This button is used to show the selected date and select new date to get schedules.
     *
     * @since 1.0
     */
    private Button btnDate;
    /**
     * This button is used to set mDate to the next date and load schedules in that date.
     *
     * @see #mDate
     * @since 1.0
     */
    private Button btnNextDate;
    /**
     * This button is used to set mDate to the prev date and load schedules in that date.
     *
     * @see #mDate
     * @since 1.0
     */
    private Button btnPreDate;
    /**
     * To show "Empty data" text to user when there is no schedule in date.
     * When this view is VISIBLE, recyclerView is INVISIBLE and vice versa.
     *
     * @see #recyclerView
     * @see #showEmptyView(boolean)
     * @since 1.0
     */
    private TextView emptyView;

    /**
     * Clear static variables, set date to currentDate.
     *
     * @since 1.0
     */
    public static void clearCache() {
        mBundleRecyclerViewState = null;
        mCalendar.setTime(new Date());
    }

    /**
     * Simply inflate view from layout and set title for activity.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @since 1.0
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_today, container, false);
        if (savedInstanceState == null) {
            mDate = mCalendar.getTime();
        }

        getActivity().setTitle("Lịch trình trong ngày");
        return view;
    }

    /**
     * Binding views from layout, and add margin to recyclerView by RecyclerViewMargin.
     *
     * @param savedInstanceState
     * @see #recyclerView
     * @see RecyclerViewMargin
     * @since 1.0
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        emptyView = view.findViewById(R.id.empty_view);
        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerViewMargin decoration = new RecyclerViewMargin(10, 1);
        recyclerView.addItemDecoration(decoration);
    }

    /**
     * Binding views, set adapter for recyclerView and add listeners for buttons.
     *
     * @since 1.0
     */
    @Override
    public void onStart() {
        super.onStart();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new TaskAdapter(getActivity(), getArrayTaskInDay(mDate));
        mAdapter.setTaskBlockListener(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setMode(Attributes.Mode.Single);

        recyclerView.setAdapter(mAdapter);

        // Show add task screen
        btnAdd = view.findViewById(R.id.btn_add1);
        btnAdd.setOnClickListener(v -> {
            final Intent intent = new Intent(getActivity(), NewTaskActivity.class);
            intent.putExtra(NewTaskActivity.START_REQUEST_CODE, TaskActivityViewMode.CREATE);
            startActivity(intent);
        });

        // Calender view
        btnDate = view.findViewById(R.id.Date1);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        btnDate.setText(formatter.format(mCalendar.getTime()));

        if (DateUtils.isToday(mCalendar.getTimeInMillis())) {
            btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        } else {
            btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.optionLessonIdColor));
        }

        btnDate.setOnClickListener(v -> selectDay());

        btnNextDate = view.findViewById(R.id.btn_next_date);
        btnPreDate = view.findViewById(R.id.btn_pre_date);

        btnNextDate.setOnClickListener(v -> {
            mCalendar.add(Calendar.DAY_OF_YEAR, 1);
            btnDate.setText(formatter.format(mCalendar.getTime()));
            mDate = mCalendar.getTime();
            if (DateUtils.isToday(mCalendar.getTimeInMillis())) {
                btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            } else {
                btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.optionLessonIdColor));
            }
            mAdapter = new TaskAdapter(getActivity(), getArrayTaskInDay(mDate));
            recyclerView.setAdapter(mAdapter);
        });

        btnPreDate.setOnClickListener(v -> {
            mCalendar.add(Calendar.DAY_OF_YEAR, -1);
            btnDate.setText(formatter.format(mCalendar.getTime()));
            mDate = mCalendar.getTime();
            if (DateUtils.isToday(mCalendar.getTimeInMillis())) {
                btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            } else {
                btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.optionLessonIdColor));
            }
            mAdapter = new TaskAdapter(getActivity(), getArrayTaskInDay(mDate));
            recyclerView.setAdapter(mAdapter);
        });
    }

    /**
     * This method will be triggered when btnDate is clicked. A DatePicker will be showed for user
     * to select date.
     *
     * @since 1.0
     */
    private void selectDay() {

        int day = mCalendar.get(Calendar.DATE);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);

        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), (view, mYear, mMonth, mDay) -> {
            mCalendar.set(mYear, mMonth, mDay);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            btnDate.setText(formatter.format(mCalendar.getTime()));

            if (DateUtils.isToday(mCalendar.getTimeInMillis())) {
                btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            } else {
                btnDate.setTextColor(ContextCompat.getColor(getContext(), R.color.optionLessonIdColor));
            }

            mDate = mCalendar.getTime();
            mAdapter = new TaskAdapter(getActivity(), getArrayTaskInDay(mDate));
            recyclerView.setAdapter(mAdapter);
        }, year, month, day);

        pickerDialog.show();
    }

    /**
     * This method will get all schedules in "date" that user select.
     *
     * @param date the selected date.
     * @return an array of TaskVM instances (also contains headers).
     * @since 1.0
     */
    public ArrayList<TaskVM> getArrayTaskInDay(Date date) {
        ArrayList<TaskVM> arrayList = new ArrayList<>();
        //remove all indexes in header
        headerIndexes.clear();
        todayScheduleArrayList = LocalScheduleAPI.getInstance().getSchedulesInDay(getContext(), date);

        ArrayList<TaskVM> ongoingTasks = new ArrayList<>();
        ArrayList<TaskVM> futureTasks = new ArrayList<>();
        ArrayList<TaskVM> pastTasks = new ArrayList<>();

        for (int i = 0; i < todayScheduleArrayList.size(); i++) {
            TaskVM taskVM = new TaskVM(todayScheduleArrayList.get(i), i);
            switch (todayScheduleArrayList.get(i).state) {
                case PAST:
                    pastTasks.add(taskVM);
                    break;
                case ONGOING:
                    ongoingTasks.add(taskVM);
                    break;
                case FUTURE:
                    futureTasks.add(taskVM);
                    break;
            }
        }

        TaskVM ongoingHeader = new TaskVM("Đang diễn ra");
        TaskVM futureHeader = new TaskVM("Sắp tới");
        TaskVM pastHeader = new TaskVM("Đã hoàn thành");

        if (!ongoingTasks.isEmpty()) {
            headerIndexes.add(arrayList.size());
            arrayList.add(ongoingHeader);
            arrayList.addAll(ongoingTasks);
        }

        if (!futureTasks.isEmpty()) {
            headerIndexes.add(arrayList.size());
            arrayList.add(futureHeader);
            arrayList.addAll(futureTasks);
        }

        if (!pastTasks.isEmpty()) {
            headerIndexes.add(arrayList.size());
            arrayList.add(pastHeader);
            arrayList.addAll(pastTasks);
        }

        showEmptyView(arrayList.isEmpty());
        return arrayList;
    }

    /**
     * Save state of recyclerView to mBundleRecyclerViewState to restore later.
     *
     * @see #mBundleRecyclerViewState
     * @since 1.0
     */
    @Override
    public void onPause() {
        super.onPause();
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    /**
     * Get the mBundleRecyclerViewState that saved before and restore recyclerView state.
     *
     * @see #mBundleRecyclerViewState
     * @since 1.0
     */
    @Override
    public void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    /**
     * This method will clear the headerIndexes.
     *
     * @since 1.0
     */
    private void clearHeaderIndexes() {
        headerIndexes.clear();
    }

    /**
     * Show empty view when there is no schedule in selected date or reshow recyclerView
     * when there are schedules in selected date.
     *
     * @param isEmpty is data empty or not.
     * @since 1.0
     */
    private void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * When the last item is deleted, this method will be triggered,
     * this method will show emptyView and clear all header indexes.
     *
     * @param v the last TaskVM instance.
     * @since 1.0
     */
    @Override
    public void taskRecycleViewDeletedLastItem(View v) {
        showEmptyView(true);
        clearHeaderIndexes();
    }

    /**
     * Refresh data of recyclerView.
     *
     * @see #recyclerView
     * @since 1.0
     */
    @Override
    public void taskRecyclerViewNeedRefresh() {
        mAdapter.setArrBlock(getArrayTaskInDay(mCalendar.getTime()));
        mAdapter.notifyDataSetChanged();
    }

}
