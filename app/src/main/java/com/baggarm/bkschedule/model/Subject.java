package com.baggarm.bkschedule.model;

import android.util.Log;

import com.baggarm.bkschedule.helper.Extensions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * The activity for getting data (for the subjects) from server.
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public class Subject {

    private static final String TAG = Subject.class.getName();

    /**
     * There are some attributes to describe a subject.
     *
     * @since 1.0
     */
    //name also is key in json
    public String ma_mh;
    public String ten_mh;
    public String nhomto;
    public String tuan_hoc;
    public String thu1;
    public String tiet_bd1;
    public String tiet_kt1;
    public String phong1;
    public String tc_hp;
    public String kq;
    public String so_tin_chi;
    public String muc_hocphi;
    public String ma_nhom;
    public String mssv;
    public String hk_nh;
    public String ten_hk_nh;
    public String ten_file;
    public String ngay_cap_nhat;
    public String trang_thai;
    public String ghi_chu;

    //custom properties for used in UI
    public String strNhomTo;
    public String strTietHoc;
    public String strPhongHoc;
    public String strThu;

    /**
     * This method gets data from jsonObject and handles some attributes.
     * if map could not be passed to JSON, an exception will be throw.
     * if map is different from null, the attributes will be updated.
     *
     * @param jsonObject
     * @since 1.0
     */
    public Subject(JSONObject jsonObject) {
        Map<String, Object> map = null;

        try {
            map = Extensions.toMap(jsonObject);
        } catch (JSONException e) {
            //handle error
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (map == null)
            return;

        ma_mh = map.get("ma_mh").toString();
        ten_mh = map.get("ten_mh").toString();
        nhomto = map.get("nhomto").toString();
        tuan_hoc = map.get("tuan_hoc").toString();
        thu1 = map.get("thu1").toString();
        tiet_bd1 = map.get("tiet_bd1").toString();
        tiet_kt1 = map.get("tiet_kt1").toString();
        phong1 = map.get("phong1").toString();
        tc_hp = map.get("tc_hp").toString();
        kq = map.get("kq").toString();
        so_tin_chi = map.get("so_tin_chi").toString();
        muc_hocphi = map.get("muc_hocphi").toString();
        ma_nhom = map.get("ma_nhom").toString();
        mssv = map.get("mssv").toString();
        hk_nh = map.get("hk_nh").toString();
        ten_hk_nh = map.get("ten_hk_nh").toString();
        ten_file = map.get("ten_file").toString();
        ngay_cap_nhat = map.get("ngay_cap_nhat").toString();
        trang_thai = map.get("trang_thai").toString();
        ghi_chu = map.get("ghi_chu").toString();

        //handle exception of tiet_kt1
        if (tiet_kt1.equals("-1"))
            strTietHoc = "Tiết\n--";
        else
            strTietHoc = "Tiết\n" + tiet_bd1 + " - " + tiet_kt1;

        if (phong1.length() < 9)
            strPhongHoc = "Phòng\n" + phong1;
        else
            strPhongHoc = phong1;

        strNhomTo = "Nhóm\n" + nhomto;

        //handle "Thu 8"
        if (thu1.equals("8"))
            strThu = "Chủ nhật";
        else
            strThu = "Thứ\n" + thu1;
    }


}
