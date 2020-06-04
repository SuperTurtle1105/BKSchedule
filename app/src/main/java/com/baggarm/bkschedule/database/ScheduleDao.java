package com.baggarm.bkschedule.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * This interface is used to execute query insert, update and delete Schedule in DB.
 *
 * @author IMBAGGAARM
 * @version 2019.1406
 * @since 1.0
 */

@Dao
public interface ScheduleDao {

    @Query("SELECT * FROM schedule")
    List<TodaySchedule> getAll();

    @Query("SELECT * FROM schedule WHERE :startTime <= start_time AND start_time <= :endTime")
    List<TodaySchedule> findScheduleByTime(long startTime, long endTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertSchedules(TodaySchedule... todaySchedules);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSchedule(TodaySchedule todaySchedule);

    @Update
    void updateSchedules(TodaySchedule... schedules);

    @Delete
    void delete(TodaySchedule todaySchedule);

    @Query("DELETE FROM schedule WHERE id=:id")
    void deleteById(int id);

    @Query("UPDATE schedule SET is_alert_on = 1 - is_alert_on WHERE id = :id")
    void updateIsAlertOn(int id);

    @Query("SELECT * FROM schedule WHERE id = :id")
    TodaySchedule getScheduleByID(int id);

}
