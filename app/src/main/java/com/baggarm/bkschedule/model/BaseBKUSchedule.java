package com.baggarm.bkschedule.model;

import android.util.Log;

import com.baggarm.bkschedule.helper.Extensions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * The activity for getting data (for function TKB) from server.
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public abstract class BaseBKUSchedule {

    private static final String TAG = BaseBKUSchedule.class.getName();
    /**
     * There are some attributes which receive from jsonObject
     *
     * @since 1.0
     */
    public String id;

    public String ten_hocky;
    public String hk_nh;
    public String ngay_cap_nhat;
    public String trang_thai;

    /**
     * This method gets data from jsonObject.
     * if map could not be passed to JSON, an exception will be throw.
     * if map is different from null, the attributes will be updated.
     *
     * @param id
     * @param jsonObject
     * @since 1.0
     */
    public BaseBKUSchedule(String id, JSONObject jsonObject) {
        this.id = id;
        Map<String, Object> map = null;
        try {
            map = Extensions.jsonToMap(jsonObject);
        } catch (JSONException e) {
            //handle error
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (map != null) {
            ten_hocky = map.get("ten_hocky").toString();
            hk_nh = map.get("hk_nh").toString();
            ngay_cap_nhat = map.get("ngay_cap_nhat").toString();
            trang_thai = map.get("ngay_cap_nhat").toString();
        }

    }

}
