package com.baggarm.bkschedule.model;

import android.util.Log;

import com.baggarm.bkschedule.helper.Extensions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * This activity for getting data from server. The data includes information about the test (Hoc ky).
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public class Test {

    private static final String TAG = Test.class.getName();

    /**
     * There are some attributes to describe the information about the test.
     *
     * @since 1.0
     */
    public String ma_mh;
    public String ten_mh;
    public String nhomto;
    public String ngaykt;
    public String tiet_bd_ktra;
    public String gio_kt;
    public String phong_ktra;
    public String ngaythi;
    public String tiet_bd_thi;
    public String gio_thi;
    public String phong_thi;
    public String mssv;
    public String ma_nhom;
    public String ma_to;
    public String kq;
    public String hk_nh;
    public String ten_hk_nh;
    public String ten_file;
    public String ngay_cap_nhat;
    public String ghi_chu;
    public String trang_thai;
    //

    //used for UI
    public String strNhomTo;
    public String strGioKt;
    public String strPhongKt;
    public String strNgayKt;
    public String strNgayThi;
    public String strGioThi;
    public String strPhongThi;
    //

    /**
     * This method gets data from jsonObject and handles some attributes.
     * if map could not be passed to JSON, an exception will be throw.
     * if map is different from null, the attributes will be updated.
     *
     * @param jsonObject
     * @since 1.0
     */
    public Test(JSONObject jsonObject) {
        Map<String, Object> map = null;

        try {
            map = Extensions.toMap(jsonObject);
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (map == null)
            return;

        ma_mh = map.get("ma_mh").toString();
        ten_mh = map.get("ten_mh").toString();
        nhomto = map.get("nhomto").toString();
        ngaykt = map.get("ngaykt").toString();
        tiet_bd_ktra = map.get("tiet_bd_ktra").toString();
        gio_kt = map.get("gio_kt").toString();
        phong_ktra = map.get("phong_ktra").toString();
        ngaythi = map.get("ngaythi").toString();
        tiet_bd_thi = map.get("tiet_bd_thi").toString();
        gio_thi = map.get("gio_thi").toString();
        phong_thi = map.get("phong_thi").toString();
        mssv = map.get("mssv").toString();
        ma_nhom = map.get("ma_nhom").toString();
        ma_to = map.get("ma_to").toString();
        kq = map.get("kq").toString();
        hk_nh = map.get("hk_nh").toString();
        ten_hk_nh = map.get("ten_hk_nh").toString();
        ten_file = map.get("ten_file").toString();
        ngay_cap_nhat = map.get("ngay_cap_nhat").toString();
        ghi_chu = map.get("ghi_chu").toString();
        trang_thai = map.get("trang_thai").toString();

        strNhomTo = "Nhóm\n" + nhomto;
        strNgayKt = "Ngày\n" + ngaykt;
        strNgayThi = "Ngày\n" + ngaythi;

        if (gio_kt.equals("null"))
            strGioKt = "Giờ\n" + "--";
        else
            strGioKt = "Giờ\n" + gio_kt;

        if (phong_ktra.equals("null"))
            strPhongKt = "Phòng\n" + "--";
        else
            strPhongKt = "Phòng\n" + phong_ktra;

        if (gio_thi.equals("null"))
            strGioThi = "Giờ\n" + "--";
        else
            strGioThi = "Giờ\n" + gio_thi;

        if (phong_thi.equals("null"))
            strPhongThi = "Phòng\n" + "--";
        else
            strPhongThi = "Phòng\n" + phong_thi;

    }

    /**
     * An override function.
     * It returns a string about the test's information.
     *
     * @since 1.0
     */
    @Override
    public String toString() {
        return "Test{" +
                "ten_mh='" + ten_mh + '\'' +
                ", phong_ktra='" + phong_ktra + '\'' +
                ", phong_thi='" + phong_thi + '\'' +
                ", strPhongKt='" + strPhongKt + '\'' +
                ", strPhongThi='" + strPhongThi + '\'' +
                '}';
    }
}
