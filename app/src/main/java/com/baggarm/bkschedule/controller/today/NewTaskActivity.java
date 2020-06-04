package com.baggarm.bkschedule.controller.today;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.adapter.TaskAdapter;
import com.baggarm.bkschedule.api.LocalScheduleAPI;
import com.baggarm.bkschedule.database.TodaySchedule;
import com.baggarm.bkschedule.helper.SettingAlarmHelper;
import com.baggarm.bkschedule.model.TaskActivityViewMode;
import com.baggarm.bkschedule.model.TaskState;
import com.baggarm.bkschedule.model.viewModel.TaskVM;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This activity is used to create/edit/view Task.
 *
 * @author huybaonguyen
 * @version 2019.1306
 * @since 1.0
 */
public class NewTaskActivity extends AppCompatActivity {

    /**
     * To determine view mode of this activity, this will be set via Intent extras.
     *
     * @since 1.0
     */
    public final static String START_REQUEST_CODE = "START_REQUEST_CODE";
    /**
     * Debug Tag for use logging debug to debug out LogCat.
     *
     * @since 1.0
     */
    private final String TAG = NewTaskActivity.class.getName();
    /**
     * Main layout of this activity.
     *
     * @since 1.0
     */
    LinearLayout mainLinearLayout;
    /**
     * View mode of this activity.
     *
     * @since 1.0
     */
    private TaskActivityViewMode viewMode = TaskActivityViewMode.VIEW;
    /**
     * A button to save task.
     *
     * @since 1.0
     */
    private Button btnSave;
    /**
     * Toolbar of this activity
     *
     * @since 1.0
     */
    private Toolbar toolbar;
    /**
     * Show title of task.
     *
     * @since 1.0
     */
    private EditText title;
    /**
     * Show start time of task in format.
     *
     * @see #getTimeInFormat(int, int)
     * @since 1.0
     */
    private EditText txtStart;
    /**
     * Show end time of task in format.
     *
     * @see #getTimeInFormat(int, int)
     * @since 1.0
     */
    private EditText txtEnd;
    /**
     * Show alert time of task in format.
     *
     * @see #getTimeInFormat(int, int)
     * @since 1.0
     */
    private EditText txtAlertTime;
    /**
     * Show location of task.
     *
     * @since 1.0
     */
    private EditText txtLocation;
    /**
     * Show note of task.
     *
     * @since 1.0
     */
    private EditText txtNote;
    /**
     * A switch to turn alert on or off.
     *
     * @since 1.0
     */
    private Switch aSwitch;
    /**
     * Start time of task in timestamp (millis)
     *
     * @since 1.0
     */
    private long timestampStart = 0;
    /**
     * End time of task in timestamp (millis)
     *
     * @since 1.0
     */
    private long timestampEnd = 0;
    /**
     * Alert time of task in timestamp (millis), default is -1 (not set) to validate task if needed.
     *
     * @since 1.0
     */
    private long timestampAlert = -1;
    /**
     * Is turning on alert or not.
     *
     * @since 1.0
     */
    private boolean isAlert = false;
    /**
     * The selected date to add/edit task of TodayFragment.
     *
     * @see TodayFragment
     * @since 1.0
     */
    private Calendar selectedCalendar = TodayFragment.mCalendar;
    /**
     * The task block if user txtStart this activity by tapping edit icon or TaskVM in TodayFragment
     *
     * @see TodayFragment
     * @see TaskAdapter
     * @since 1.0
     */
    private TaskVM block;

    /**
     * Show title for this based on viewMode
     *
     * @param intent the intent that start this activity.
     * @see #viewMode
     * @since 1.0
     */
    private void handleSetTitle(Intent intent) {
        if (intent.hasExtra(START_REQUEST_CODE)) {
            viewMode = (TaskActivityViewMode) intent.getSerializableExtra(START_REQUEST_CODE);
            switch (viewMode) {
                case VIEW:
                    setTitle("Xem lịch trình");
                    btnSave.setVisibility(View.INVISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;
                case CREATE:
                    setTitle("Thêm lịch trình");
                    mainLinearLayout.setFocusableInTouchMode(false);
                    break;
                case UPDATE:
                    setTitle("Chỉnh sửa lịch trình");
                    mainLinearLayout.setFocusableInTouchMode(false);
                    break;
            }
        }
    }

    /**
     * Get task was passed by Intent.
     * If task is not null, show data to views.
     * Otherwise, do not show anything.
     *
     * @param intent the Intent that start this activity.
     * @since 1.0
     */
    private void showData(Intent intent) {
        if (intent.hasExtra("Block")) {
            block = (TaskVM) intent.getSerializableExtra("Block");
            title.setText(block.getName());
            txtStart.setText(block.getTimeStart());
            txtEnd.setText(block.getTimeEnd());
            txtAlertTime.setText(block.getAlert());
            txtLocation.setText(block.getLocation());
            txtNote.setText(block.getDetail());
            aSwitch.setChecked(block.getIsAlert());

            TodaySchedule schedule = block.getTodaySchedule();

            timestampStart = schedule.getLongStartTime();
            timestampEnd = schedule.getLongEndTime();
            timestampAlert = schedule.getLongAlertTime();
            isAlert = schedule.isAlertOn();
            aSwitch.setChecked(isAlert);
        } else {
            aSwitch.setChecked(false);
            timestampStart = timestampEnd = 0;
            timestampAlert = -1;
        }
    }

    /**
     * Binding views show data on UI and add listeners for views.
     *
     * @param savedInstanceState the bundle that stored to restore state of this activity.
     * @since 1.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        //binding views
        mainLinearLayout = findViewById(R.id.linear_activity_task);
        title = findViewById(R.id.edit_txt_title);
        txtStart = findViewById(R.id.edit_txt_start);
        txtEnd = findViewById(R.id.edit_txt_end);
        txtAlertTime = findViewById(R.id.edit_txt_alert);
        txtLocation = findViewById(R.id.edit_txt_location);
        txtNote = findViewById(R.id.edit_txt_note);
        aSwitch = findViewById(R.id.alert_switch);
        btnSave = findViewById(R.id.btn_task_save);
        toolbar = findViewById(R.id.mtoolbar_task_detail);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        handleSetTitle(intent);
        showData(intent);

        txtStart.setOnClickListener(v -> onTxtStartClicked());
        txtEnd.setOnClickListener(v -> onTxtEndClicked());
        txtAlertTime.setOnClickListener(v -> onTxtAlertClicked());
        // save
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> isAlert = aSwitch.isChecked());
        btnSave.setOnClickListener(v -> onBtnSaveClicked());
    }

    /**
     * Handle event clicking on txtStart, a time picker dialog will be showed. After user picks start time of task,
     * timestampAlert also will be set equal with timestampStart
     *
     * @see TimePickerDialog
     * @see #timestampStart
     * @since 1.0
     */
    private void onTxtStartClicked() {
        TimePickerDialog startDialog = new TimePickerDialog(NewTaskActivity.this, (view, hourOfDay, minute) -> {
            txtStart.setText(getTimeInFormat(hourOfDay, minute));
            timestampStart = getTimestampInDay(hourOfDay, minute);

            // set alertTimeStamp
            timestampAlert = timestampStart;
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String dateString = formatter.format(new Date(timestampAlert));
            txtAlertTime.setText(dateString);
        }, 0, 0, true);

        if (timestampStart != 0) {
            startDialog.updateTime(getHourFromTimestamp(timestampStart), getMinuteFromTimestamp(timestampStart));
        }
        startDialog.show();
    }

    /**
     * Handle event clicking on txtEnd, a time picker dialog will be showed and user will choose the
     * end time of task.
     *
     * @see TimePickerDialog
     * @since 1.0
     */
    private void onTxtEndClicked() {
        TimePickerDialog startDialog = new TimePickerDialog(NewTaskActivity.this, (view, hourOfDay, minute) -> {
            txtEnd.setText(getTimeInFormat(hourOfDay, minute));
            timestampEnd = getTimestampInDay(hourOfDay, minute);
        }, 0, 0, true);

        if (timestampEnd != 0) {
            startDialog.updateTime(getHourFromTimestamp(timestampEnd), getMinuteFromTimestamp(timestampEnd));
        }
        startDialog.show();
    }

    /**
     * User will pick alert time of task, a time picker dialog will be showed to hep user on this.
     *
     * @since 1.0
     */
    private void onTxtAlertClicked() {
        TimePickerDialog startDialog = new TimePickerDialog(NewTaskActivity.this, (view, hourOfDay, minute) -> {
            txtAlertTime.setText(getTimeInFormat(hourOfDay, minute));
            isAlert = true;
            aSwitch.setChecked(true);
            timestampAlert = getTimestampInDay(hourOfDay, minute);
        }, 0, 0, true);

        if (timestampAlert != -1) {
            startDialog.updateTime(getHourFromTimestamp(timestampAlert), getMinuteFromTimestamp(timestampAlert));
        }
        startDialog.show();
    }

    /**
     * Get input of user in views, and set to a new instance of TodaySchedule, after that,
     * save this instance to database and update id of this instance.
     * <p>
     * If alert is turned on,
     * also add alert to notify user when this task is coming. A toast will be showed to alert user
     * this task was added successfully.
     *
     * @see TodaySchedule
     * @see LocalScheduleAPI
     * @see SettingAlarmHelper
     * @since 1.0
     */
    private void createNewTask() {
        // insert new task into SQLite
        TodaySchedule newTask = new TodaySchedule();
        newTask.setName(title.getText().toString());
        newTask.setLocation(txtLocation.getText().toString());
        newTask.setDetail(txtNote.getText().toString());
        newTask.setStartTime(timestampStart);
        newTask.setEndTime(timestampEnd);
        newTask.setAlertTime(timestampAlert);
        newTask.setAlertOn(isAlert);

        // TODO: check if id == 0 or not, if is 0, insert failed
        long id = LocalScheduleAPI.getInstance().insertSchedule(getApplicationContext(), newTask);
        newTask.setId((int) id);

        if (isAlert) {
            // add alert
            SettingAlarmHelper.getInstance().setAlert(newTask, this);
        }
        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
    }

    /**
     * Update existing task, if task did not have id, task will be added to database, and updateId immediately
     * after that, otherwise, just update task to database.
     * <p>
     * If alert is turning on, also set alert to notify user. A toast will be showed to alert user this
     * task was updated successfully.
     *
     * @see TodaySchedule
     * @see LocalScheduleAPI
     * @see SettingAlarmHelper
     * @since 1.0
     */
    private void updateTask() {
        // update current task by ID
        TodaySchedule currentTask = block.getTodaySchedule();

        // cancel alert before set new data
        SettingAlarmHelper.getInstance().cancelAlert(currentTask, this);

        // update currentTask
        currentTask.setName(title.getText().toString());
        currentTask.setLocation(txtLocation.getText().toString());
        currentTask.setDetail(txtNote.getText().toString());
        currentTask.setStartTime(timestampStart);
        currentTask.setEndTime(timestampEnd);
        currentTask.setAlertTime(timestampAlert);
        currentTask.setAlertOn(isAlert);

        if (currentTask.getId() == 0) {
            long newID = LocalScheduleAPI.getInstance().insertSchedule(getApplicationContext(), currentTask);
            // update ID if currenTask needs to insert to database (Test, Learning Schedule are unsaved in database before)
            block.updateID((int) newID);
            currentTask.setId((int) newID);
        } else {
            LocalScheduleAPI.getInstance().updateSchedule(getApplicationContext(), currentTask);
        }

        if (isAlert) {
            SettingAlarmHelper.getInstance().setAlert(currentTask, this);
        }
        Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
    }

    /**
     * When btnSave is clicked, this method will be triggered, it will validate the task and alert error if it exists.
     * If task is valid, createNewTask will be called if viewMode == CREATE, otherwise, updateTask will be called.
     * When task is updated or added successfully, this activity will be destroy immediately.
     *
     * @see #viewMode
     * @see #createNewTask()
     * @see #updateTask()
     * @see TaskValidation
     * @since 1.0
     */
    private void onBtnSaveClicked() {
        TaskValidation errorCode = isValid();
        switch (errorCode) {
            case ERROR_NAME_NULL:
                Toast.makeText(NewTaskActivity.this, "Tên lịch trình không được để trống.", Toast.LENGTH_SHORT).show();
                return;
            case ERROR_END:
                Toast.makeText(NewTaskActivity.this, "Thời gian kết thúc phải sau thời gian bắt đầu", Toast.LENGTH_SHORT).show();
                return;
            case ERROR_ALERT:
                Toast.makeText(NewTaskActivity.this, "Thời gian báo thức phải trước thời gian bắt đầu", Toast.LENGTH_SHORT).show();
                return;
            case ERROR_ALERT_PAST:
                Toast.makeText(NewTaskActivity.this, "Thời gian báo thức phải ở tương lai.", Toast.LENGTH_SHORT).show();
                return;
            case ERROR_PAST:
                Toast.makeText(NewTaskActivity.this, "Không được thêm lịch trình ở quá khứ.", Toast.LENGTH_SHORT).show();
                return;
            case ERROR_CHANGE_START_TIME_OF_ONGOING_TASK:
                Toast.makeText(NewTaskActivity.this, "Lịch trình đã được bắt đầu.", Toast.LENGTH_SHORT).show();
                return;
            case IS_VALID:
                Log.e(TAG, "is valid task");
                break;
        }

        if (viewMode == TaskActivityViewMode.CREATE)
            createNewTask();
        else
            updateTask();

        // destroy this fragment
        finish();
    }

    /**
     * Finish this activity when back button is pressed.
     *
     * @since 1.0
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Get timestamp by given hour and minute with selected date.
     *
     * @param hour   hour of this date
     * @param minute minute
     * @return timestamp in millis
     * @since 1.0
     */
    private long getTimestampInDay(int hour, int minute) {
        Calendar calendar = (Calendar) selectedCalendar.clone();
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));

        return calendar.getTimeInMillis();
    }

    /**
     * Validate the task to check if user input wrong task data.
     *
     * @return an instance of TaskValidate
     * @see TaskValidation
     * @since 1.0
     */
    private TaskValidation isValid() {
        if (title.getText().toString().isEmpty())
            return TaskValidation.ERROR_NAME_NULL;
        long current = new Date().getTime();
        if (current > timestampStart && block == null)
            return TaskValidation.ERROR_PAST;
        if (timestampEnd <= timestampStart)
            return TaskValidation.ERROR_END;
        if (timestampAlert > timestampStart && isAlert)
            return TaskValidation.ERROR_ALERT;
        // alert = -1 when user does not set alert on
        if (current > timestampAlert && isAlert && (block == null || block.getState() != TaskState.ONGOING))
            return TaskValidation.ERROR_ALERT_PAST;

        return TaskValidation.IS_VALID;
    }

    /**
     * Get hour of day from given timestamp
     *
     * @param timestamp given timestamp
     * @return hour of day
     * @since 1.0
     */
    private int getHourFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Get minute of day from given timestamp
     *
     * @param timestamp given timestamp
     * @return minute
     * @since 1.0
     */
    private int getMinuteFromTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * Get time string in format from given hour and minute.
     *
     * @param hour   given hour
     * @param minute given minute
     * @return a formatted time string.
     */
    private String getTimeInFormat(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    /**
     * Is used to validate task.
     *
     * @since 1.0
     */
    private enum TaskValidation {
        ERROR_NAME_NULL,
        ERROR_PAST,
        ERROR_END,
        ERROR_ALERT,
        ERROR_ALERT_PAST,
        IS_VALID,
        ERROR_CHANGE_START_TIME_OF_ONGOING_TASK
    }
}
