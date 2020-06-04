package com.baggarm.bkschedule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.model.viewModel.StudentInfoItemVM;

import java.util.List;

/**
 * This adapter is used to binding data to listView in PersonFragment and show it
 *
 * @author PXThanhLam
 * @version 2019.1506
 * @see StudentInfoItemVM
 * @since 1.0
 */

public class StudentInfoAdapter extends BaseAdapter {

    private Context context;
    /**
     * An array that stores StudentInfoLVItem items.
     *
     * @see StudentInfoItemVM
     * @since 1.0
     */
    private List<StudentInfoItemVM> dataList;

    public StudentInfoAdapter(Context context, List<StudentInfoItemVM> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * return a view to assign to ListView at current position
     * It assign field of StudentInfoLVItemModel object at that position
     * to the text view and two imageview at fragment_person.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.person_lv_item, null);
            TextView txtTitle = convertView.findViewById(R.id.txt_title);
            ImageView imgVLeftIcon = convertView.findViewById(R.id.imgv_left_icon);
            ImageView imgVRightIcon = convertView.findViewById(R.id.imgv_right_icon);

            StudentInfoItemVM data = dataList.get(position);

            txtTitle.setText(data.getTitle());
            imgVLeftIcon.setImageResource(data.getLeftIconID());
            imgVRightIcon.setImageResource(data.getRightIconID());
        }
        return convertView;
    }
}
