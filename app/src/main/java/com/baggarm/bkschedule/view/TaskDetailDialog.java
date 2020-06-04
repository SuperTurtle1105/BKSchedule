package com.baggarm.bkschedule.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.model.viewModel.TaskVM;

/**
 * This class is used to show detail information of task
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class TaskDetailDialog extends DialogFragment {
    TextView tvName;
    TextView tvLocation;
    TextView tvDescription;
    TextView tvTime;
    TextView tvAlert;
    Button btnEdit;
    Button btnDelete;
    Switch swAlert;

    public static TaskDetailDialog newInstance(TaskVM data) {
        TaskDetailDialog dialog = new TaskDetailDialog();
        Bundle args = new Bundle();
        args.putSerializable("Block", data);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_task, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TaskVM block = (TaskVM) getArguments().getSerializable("Block");

        tvName = view.findViewById(R.id.tv_Diaglog_Name);
        tvLocation = view.findViewById(R.id.tv_Diaglog_Location);
        tvDescription = view.findViewById(R.id.tv_Diaglog_Decreption);
        tvTime = view.findViewById(R.id.tv_Diaglog_Time);
        tvAlert = view.findViewById(R.id.tv_Diaglog_Aler);
        btnDelete = view.findViewById(R.id.button_Diaglog_Delete);
        btnEdit = view.findViewById(R.id.button_Diaglog_Edit);
        swAlert = view.findViewById(R.id.dialogSwitch);

        tvName.setText(block.getName());
        tvLocation.setText(block.getLocation());
        tvDescription.setText(block.getDetail());
        tvTime.setText(block.getTimeStart() + "-" + block.getTimeEnd());
        tvAlert.setText(block.getAlert());
    }
}
