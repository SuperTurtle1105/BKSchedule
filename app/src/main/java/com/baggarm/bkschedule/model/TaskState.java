package com.baggarm.bkschedule.model;

/**
 * This TaskState represents three states in function Hôm nay.
 * <ul>
 * <li>PAST: the events have already happened.</li>
 * <li>ONGOING: the events is happening.</li>
 * <li>FUTURE: the event will have happened at a time in the future.</li>
 * </ul>
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public enum TaskState {
    PAST,
    ONGOING,
    FUTURE
}
