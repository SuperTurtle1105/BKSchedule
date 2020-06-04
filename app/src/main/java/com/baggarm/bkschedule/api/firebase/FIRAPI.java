package com.baggarm.bkschedule.api.firebase;

/**
 * To group all firebase relations class
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class FIRAPI {
    /**
     * Represent for <b>semesters</b> in firebase realtime database
     *
     * @since 1.0
     */
    public static final FIRSemesters SEMESTERS = FIRSemesters.getInstance();

    /**
     * Config for realtime database
     *
     * @since 1.0
     */
    public static final FIRConfig CONFIG = FIRConfig.getInstance();
}
