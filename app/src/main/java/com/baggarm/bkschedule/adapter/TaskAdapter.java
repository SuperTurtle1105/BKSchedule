package com.baggarm.bkschedule.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.api.LocalScheduleAPI;
import com.baggarm.bkschedule.controller.today.NewTaskActivity;
import com.baggarm.bkschedule.controller.today.TodayFragment;
import com.baggarm.bkschedule.database.TodaySchedule;
import com.baggarm.bkschedule.helper.SettingAlarmHelper;
import com.baggarm.bkschedule.model.TaskActivityViewMode;
import com.baggarm.bkschedule.model.TaskState;
import com.baggarm.bkschedule.model.viewModel.TaskVM;
import com.baggarm.bkschedule.view.TaskHeaderViewHolder;
import com.baggarm.bkschedule.view.TaskViewHolder;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;

import java.util.ArrayList;

/**
 * Task Block Adapter contains left right swipe gestures in each task block.
 * When swiping left, show icon set alert. When swiping right, show icon edit and delete.
 *
 * @author nguyenBaoHuy
 * @version 2019.1406
 * @since 1.0
 */
public class TaskAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> implements Filterable {

    /**
     *
     */
    public interface TaskBlockListener {
        void taskRecycleViewDeletedLastItem(View v);

        void taskRecyclerViewNeedRefresh();
    }

    private static final String TAG = TaskAdapter.class.getName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<TaskVM> arrBlock;
    private SwipeItemRecyclerMangerImpl mItemManger;

    private TaskBlockListener listener;

    public void setTaskBlockListener(TaskBlockListener listener) {
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public void setArrBlock(ArrayList<TaskVM> taskVMS) {
        int size = arrBlock.size();
        arrBlock = taskVMS;
        notifyItemRangeChanged(0, size);
    }

    public TaskAdapter(Context context, ArrayList<TaskVM> arrBlock) {
        this.mContext = context;
        this.arrBlock = arrBlock;
        this.mLayoutInflater = LayoutInflater.from(context);
        mItemManger = new SwipeItemRecyclerMangerImpl(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return TodayFragment.headerIndexes.contains(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_HEADER) {
            View item = mLayoutInflater.inflate(R.layout.task_header_layout, viewGroup, false);
            return new TaskHeaderViewHolder(item);
        } else if (viewType == TYPE_ITEM) {
            View item = mLayoutInflater.inflate(R.layout.task_block, viewGroup, false);
            return new TaskViewHolder(item);
        }
        throw new RuntimeException(("No match for " + viewType) + ".");
    }

    /**
     * This method first set color corresponding block's state for each task block
     * and icon alert for each block if that block had been setting alarm. Then listener onClick Update.
     * User only can edit task block ONGOING or FUTURE. Finally listener on onClick Delete. If user delete
     * one task block, it will remove alarm setting.
     *
     * @param taskViewHolder
     * @param block
     * @param position
     * @since 1.0
     */
    private void bindTaskViewHolder(TaskViewHolder taskViewHolder, TaskVM block, int position) {
        taskViewHolder.txtName.setText(block.getName());
        String location = block.getLocation().isEmpty() ? "Không có địa điểm" : block.getLocation();
        String detail = block.getDetail().isEmpty() ? "Không có ghi chú" : block.getDetail();

        taskViewHolder.txtLocation.setText(location);
        taskViewHolder.txtDetail.setText(detail);
        switch (block.getState()) {
            case PAST:
                taskViewHolder.mainView.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_corner_gray, null));
                break;
            case ONGOING:
                taskViewHolder.mainView.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_corner_blue, null));
                break;
            case FUTURE:
                taskViewHolder.mainView.setBackground(mContext.getResources().getDrawable(R.drawable.rounded_corner_red_orange, null));
                break;
        }

        String time = block.getTimeStart() + " -- " + block.getTimeEnd();
        taskViewHolder.txtTime.setText(time);
        taskViewHolder.txtAlert.setText(block.getAlert());
        if (block.getIsAlert()) {
            taskViewHolder.txtAlert.setVisibility(View.VISIBLE);
            taskViewHolder.imgVAlertIcon.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        } else {
            taskViewHolder.txtAlert.setVisibility(View.INVISIBLE);
            taskViewHolder.imgVAlertIcon.setImageResource(R.drawable.ic_notifications_off_black_24dp);
        }

        mItemManger.bindView(taskViewHolder.itemView, position);
        taskViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                mItemManger.closeAllExcept(layout);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.edit));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.imgv_alert_icon));
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
            }
        });

        taskViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        taskViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, taskViewHolder.swipeLayout.findViewWithTag("Bottom1"));
        taskViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, taskViewHolder.swipeLayout.findViewWithTag("Bottom2"));


        taskViewHolder.swipeLayout.findViewById(R.id.imgv_alert_icon).setOnClickListener(v -> {
            TaskState state = block.getState();
            if (state == TaskState.PAST) {
                Toast.makeText(mContext, "Bạn không thể chỉnh sửa báo thức của lịch trình trong quá khứ.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (state == TaskState.ONGOING) {
                Toast.makeText(mContext, "Bạn không thể chỉnh sửa báo thức của lịch trình đang diễn ra.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (block.getID() == 0) {
                //always turn on alert
                TaskVM insertBlock = arrBlock.get(position);
                //get todaySchedule
                TodaySchedule todaySchedule = insertBlock.getTodaySchedule();
                //setAlert
                todaySchedule.setAlertOn(true);
                //insert to database
                long id = LocalScheduleAPI.getInstance().insertSchedule(mContext, todaySchedule);
                //update id
                insertBlock.updateID((int) id);
                todaySchedule.setId((int) id);
                //update alert on
                insertBlock.setIsAlert(true);
                //TODO: alert
                SettingAlarmHelper.getInstance().setAlert(todaySchedule, mContext);
                taskViewHolder.imgVAlertIcon.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                taskViewHolder.txtAlert.setVisibility(View.VISIBLE);
                //toast
                Toast.makeText(mContext, "Đặt báo thức thành công", Toast.LENGTH_SHORT).show();
            } else {
                TodaySchedule todaySchedule = block.getTodaySchedule();
                LocalScheduleAPI.getInstance().updateIsAlertOn(mContext, arrBlock.get(position).getID());
                if (block.getIsAlert()) {
                    block.setIsAlert(false);
                    taskViewHolder.imgVAlertIcon.setImageResource(R.drawable.ic_notifications_off_black_24dp);
                    taskViewHolder.txtAlert.setVisibility(View.INVISIBLE);

                    //change model
                    block.setIsAlert(false);
                    todaySchedule.setAlertOn(false);
                    SettingAlarmHelper.getInstance().cancelAlert(todaySchedule, mContext);
                    //toast
                    Toast.makeText(mContext, "Đã huỷ báo thức.", Toast.LENGTH_SHORT).show();
                } else {
                    block.setIsAlert(true);
                    taskViewHolder.imgVAlertIcon.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                    taskViewHolder.txtAlert.setVisibility(View.VISIBLE);

                    //change model
                    block.setIsAlert(true);
                    todaySchedule.setAlertOn(true);
                    //TODO: alert
                    SettingAlarmHelper.getInstance().setAlert(todaySchedule, mContext);
                    //toast
                    Toast.makeText(mContext, "Đặt báo thức thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xóa một công việc
        taskViewHolder.swipeLayout.findViewById(R.id.trash).setOnClickListener(v -> {
            if (arrBlock.get(position).getID() != 0) {
                removeTask(v, position);
            } else {
                Toast.makeText(mContext, "Bạn không thể xóa lịch học", Toast.LENGTH_SHORT).show();
            }
        });

        // Chỉnh sửa một công việc
        taskViewHolder.swipeLayout.findViewById(R.id.edit).setOnClickListener(v -> {
            if (block.getState() == TaskState.PAST) {
                Toast.makeText(mContext, "Bạn không thể chỉnh sửa công việc ở quá khứ.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mContext, NewTaskActivity.class);
            intent.putExtra("Block", arrBlock.get(position));
            intent.putExtra(NewTaskActivity.START_REQUEST_CODE, TaskActivityViewMode.UPDATE);
            mContext.startActivity(intent);
        });

        //Xem công việc
        taskViewHolder.mainView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, NewTaskActivity.class);
            intent.putExtra("Block", arrBlock.get(position));
            intent.putExtra(NewTaskActivity.START_REQUEST_CODE, TaskActivityViewMode.VIEW);
            mContext.startActivity(intent);
        });
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final TaskVM block = arrBlock.get(position);

        if (holder instanceof TaskHeaderViewHolder) {
            //header
            ((TaskHeaderViewHolder) holder).headerTitle.setText(block.getName());
        } else if (holder instanceof TaskViewHolder) {
            bindTaskViewHolder((TaskViewHolder) holder, block, position);
        }
    }

    @Override
    public int getItemCount() {
        return arrBlock.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    /**
     * This method is used for remove a task block. When user click on delete icon,
     * it will show a dialog in order to ensure user want to remove. CLick "xóa" to
     * remove that task or "Huỷ" to cancel action.
     *
     * @param v
     * @param position
     * @since 1.0
     */
    private void removeTask(View v, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Xóa công việc");
        builder.setMessage("Bạn muốn xóa công việc?");
        builder.setCancelable(true);
        builder.setPositiveButton("Huỷ", (dialog, which) -> dialog.dismiss());

        builder.setNegativeButton("Xóa", (dialog, which) -> {
            //delete notification
            TodaySchedule todaySchedule = arrBlock.get(position).getTodaySchedule();
            SettingAlarmHelper.getInstance().cancelAlert(todaySchedule, mContext);
            //delete row in database
            LocalScheduleAPI.getInstance().deleteScheduleById(mContext, todaySchedule.getId());
            //delete view
            //arrayBlock contains header and the last element
            if (arrBlock.size() == 2) {
                //delete all items and headerIndexes
                mItemManger.closeItem(position);
                int size = arrBlock.size();
                arrBlock.clear();
                notifyItemRangeRemoved(0, size);

                listener.taskRecycleViewDeletedLastItem(v);
            } else {
                mItemManger.closeItem(position);
                boolean isDeleted = deleteHeaderIfNeeded(position);
                if (!isDeleted) {
                    arrBlock.remove(position);
                    notifyItemRemoved(position);
                }
            }
            dialog.dismiss();
            Toast.makeText(mContext, "Đã xoá công việc.", Toast.LENGTH_SHORT).show();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method will remove header if any group has no more task block.
     *
     * @param deletePosition
     * @since 1.0
     */
    private boolean deleteHeaderIfNeeded(int deletePosition) {
        //2
        if (TodayFragment.headerIndexes.size() == 1)
            return false;

        ArrayList<Integer> arr = TodayFragment.headerIndexes;
        if (deletePosition - 1 == arr.get(0) && deletePosition + 1 == arr.get(1)) {
            //delete cell
            //delete header 0
            listener.taskRecyclerViewNeedRefresh();
            return true;
        }
        if (deletePosition - 1 == arr.get(arr.size() - 1) && deletePosition + 1 == arrBlock.size()) {
            //delete cell
            //delete header[size - 1]
            listener.taskRecyclerViewNeedRefresh();
            return true;
        }

        if (arr.size() == 2) {
            if (deletePosition > arr.get(0) && deletePosition < arr.get(1)) {
                TodayFragment.headerIndexes.set(1, arr.get(1) - 1);
            }
            return false;
        }

        //arr.size() = 3
        if (deletePosition - 1 == arr.get(1) && deletePosition + 1 == arr.get(2)) {
            //delete cell
            //delete header[1]
            listener.taskRecyclerViewNeedRefresh();
            return true;
        }

        if (deletePosition > arr.get(0) && deletePosition < arr.get(1)) {
            TodayFragment.headerIndexes.set(1, arr.get(1) - 1);
            TodayFragment.headerIndexes.set(2, arr.get(2) - 1);
            return false;
        }

        if (deletePosition > arr.get(1) && deletePosition < arr.get(2)) {
            TodayFragment.headerIndexes.set(2, arr.get(2) - 1);
            return false;
        }

        return false;
    }
}
