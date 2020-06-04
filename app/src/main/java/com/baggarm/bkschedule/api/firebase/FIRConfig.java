package com.baggarm.bkschedule.api.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class is used to get root realtime database reference
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class FIRConfig {

    private static final FIRConfig ourInstance = new FIRConfig();

    /**
     * Root database reference
     *
     * @since 1.0
     */
    static DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    private FIRConfig() {
    }

    public static FIRConfig getInstance() {
        return ourInstance;
    }
}
