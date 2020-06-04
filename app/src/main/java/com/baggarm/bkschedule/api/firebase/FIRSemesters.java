package com.baggarm.bkschedule.api.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.baggarm.bkschedule.helper.SharedPreferencesManager;
import com.baggarm.bkschedule.model.Semester;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * This class represent for <b>semesters</b> node in realtime database
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class FIRSemesters {
    private static final FIRSemesters ourInstance = new FIRSemesters();
    static private final String TAG = FIRSemesters.class.getName();

    /**
     * Current firebase node key
     *
     * @since 1.0
     */
    static private final String kSEMESTERS = "semesters";
    private final DatabaseReference REF = FIRAPI.CONFIG.databaseRef.child(kSEMESTERS);

    /**
     * A Semesters list was got from firebase
     *
     * @since 1.0
     */
    private List<Semester> semesters;

    //single ton
    private FIRSemesters() {
    }

    public static FIRSemesters getInstance() {
        return ourInstance;
    }

    //getters
    public List<Semester> getSemesters() {
        return semesters;
    }

    /**
     * Get semesters from firebase realtime database
     *
     * @param valueEventListener
     * @since 1.0
     */
    public void getSemesters(ValueEventListener valueEventListener) {
        REF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //logic code
                System.out.println(dataSnapshot);
                Map<String, String> value = (Map<String, String>) dataSnapshot.getValue();
                JSONObject jsonObject = new JSONObject(value);
                setSemestersFromJson(jsonObject);
                System.out.println(jsonObject);
                //set to shared preference to use when offline
                SharedPreferencesManager.setSemesters(jsonObject.toString());
                //callback
                valueEventListener.onDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                valueEventListener.onCancelled(databaseError);
            }
        });
    }

    /**
     * Set semesters when get data from local
     *
     * @param string
     * @since 1.0
     */
    public void setSemestersFromLocal(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            setSemestersFromJson(jsonObject);
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Set semesters from data in jsonObject
     *
     * @param jsonObject
     * @since 1.0
     */
    private void setSemestersFromJson(JSONObject jsonObject) {
        //set to FIRSemesters
        semesters = Semester.semestersFrom(jsonObject);
    }

}
