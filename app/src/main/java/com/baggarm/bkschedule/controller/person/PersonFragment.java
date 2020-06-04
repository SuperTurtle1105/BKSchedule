package com.baggarm.bkschedule.controller.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.adapter.StudentInfoAdapter;
import com.baggarm.bkschedule.controller.person.settings.SettingsActivity;
import com.baggarm.bkschedule.model.Student;
import com.baggarm.bkschedule.model.viewModel.StudentInfoItemVM;

import java.util.ArrayList;

/**
 * Peron Fragment contains avatar of student, and items of GPA,
 * learning progress, number of creadit, setting.
 *
 * @author PXThanhLam
 * @version 2019.1306
 * @since 1.0
 */
public class PersonFragment extends Fragment {

    /**
     * ID of menu in files html
     *
     * @since 1.0
     */
    public static String ids;
    /**
     * Index of array items in list view
     *
     * @since 1.0
     */
    public static int index;
    /**
     * Fragment view.
     *
     * @since 1.0
     */
    private View view;
    /**
     * Avatar of student
     *
     * @since 1.0
     */
    private ImageView imgVProfilePicture;

    /**
     * Name of Student
     *
     * @since 1.0
     */
    private TextView txtName;

    /**
     * Name of faculty of student which student is studying
     *
     * @since 1.0
     */
    private TextView txtFaculty;

    /**
     * Current student logged in this app.
     *
     * @since 1.0
     */
    private Student student = Student.getCurrentUserAccount();

    /**
     * List View to show array items.
     *
     * @since 1.0
     */
    private ListView lvPerson;

    /**
     * Array list of items
     *
     * @since 1.0
     */
    private ArrayList<StudentInfoItemVM> dataList;

    /**
     * Student info Adapter
     *
     * @since 1.0
     */
    private StudentInfoAdapter adapter;

    /**
     * Create and return view associated with the PersonFragment.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_person, null);
        getActivity().setTitle("Cá nhân");
        return view;
    }

    /**
     * Handle onClick item in list view. First this method call setDataForListView() method to
     * add StudentInfoItemVM to datalist and set name, avatar and Faculity name of Student
     * to View. Then it listen click on item in list view.
     * <p>
     * There are four item to view:
     * <ul>
     * <li> GPA
     * <li> Learning progress
     * <li> Number of credicts
     * <li> Setting
     * </ul>
     */
    @Override
    public void onStart() {
        super.onStart();

        setDataForListView();

        imgVProfilePicture = view.findViewById(R.id.img_profile);
        txtName = view.findViewById(R.id.txt_title);
        txtFaculty = view.findViewById(R.id.txt_detail);
        adapter = new StudentInfoAdapter(getActivity(), dataList);
        lvPerson.setAdapter(adapter);

        //set student data to view
        imgVProfilePicture.setImageBitmap(student.avtBitmap);
        txtName.setText(student.name);
        txtFaculty.setText("Khoa " + student.faculty);

        lvPerson.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: {
                    ids = "menu-582";
                    index = 0;
                    Intent intent = new Intent(getActivity(), StudentDetailInfoActivity.class);
                    startActivity(intent);
                    break;
                }
                case 1: {
                    Intent intent = new Intent(getActivity(), StudentDetailInfoActivity.class);
                    ids = "menu-190";
                    index = 1;
                    startActivity(intent);
                    break;
                }
                case 2: {
                    Intent intent = new Intent(getActivity(), StudentDetailInfoActivity.class);
                    ids = "menu-583";
                    index = 2;
                    startActivity(intent);
                    break;
                }
                case 3: {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    index = 3;
                    startActivity(intent);
                    break;
                }

            }
        });
    }

    /**
     * Add item GPA, learning progress, number of credict
     * and setting into Array List datalist.
     *
     * @since 1.0
     */
    private void setDataForListView() {
        lvPerson = view.findViewById(R.id.LVPerson);
        dataList = new ArrayList<>();
        dataList.add(new StudentInfoItemVM("Điểm trung bình ", R.drawable.ic_school_black_24dp, R.drawable.arrow_right_24dp));
        dataList.add(new StudentInfoItemVM("Tiến trình học tập", R.drawable.ic_timeline_black_24dp, R.drawable.arrow_right_24dp));
        dataList.add(new StudentInfoItemVM("Số tín chỉ", R.drawable.ic_poll_black_24dp, R.drawable.arrow_right_24dp));
        dataList.add(new StudentInfoItemVM("Cài đặt", R.drawable.ic_settings_black_24dp, R.drawable.arrow_right_24dp));
    }

}
