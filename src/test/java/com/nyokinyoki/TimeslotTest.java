package com.nyokinyoki;

import org.junit.Test;

import com.nyokinyoki.Attend.AttendStatus;
import com.nyokinyoki.Timestamp.StampStatus;
import com.nyokinyoki.Timetable.Timeslot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TimeslotTest {

    @Test
    public void testGetStartTimeForPeriod() {
        LocalDateTime startTimePeriod1 = Timeslot.getStartTimeForPeriod(1);
        LocalDateTime startTimePeriod2 = Timeslot.getStartTimeForPeriod(2);
        LocalDateTime startTimePeriod3 = Timeslot.getStartTimeForPeriod(3);

        assertEquals(LocalDateTime.parse("2023-01-01T08:50:00"), startTimePeriod1);
        assertEquals(LocalDateTime.parse("2023-01-01T10:30:00"), startTimePeriod2);
        assertEquals(LocalDateTime.parse("2023-01-01T13:00:00"), startTimePeriod3);
    }

    @Test
    public void testGetEndTimeForPeriod() {
        LocalDateTime endTimePeriod1 = Timeslot.getEndTimeForPeriod(1);
        LocalDateTime endTimePeriod2 = Timeslot.getEndTimeForPeriod(2);
        LocalDateTime endTimePeriod3 = Timeslot.getEndTimeForPeriod(3);

        assertEquals(LocalDateTime.parse("2023-01-01T10:20:00"), endTimePeriod1);
        assertEquals(LocalDateTime.parse("2023-01-01T12:00:00"), endTimePeriod2);
        assertEquals(LocalDateTime.parse("2023-01-01T14:30:00"), endTimePeriod3);
    }

    @Test
    public void testGetStartTime() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 3);
        LocalDateTime startTime = timeslot.getStartTime();

        assertEquals(LocalDateTime.parse("2023-01-01T08:50:00"), startTime);
    }

    @Test
    public void testGetEndTime() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 3);
        LocalDateTime endTime = timeslot.getEndTime();

        assertEquals(LocalDateTime.parse("2023-01-01T14:30:00"), endTime);
    }

    @Test
    public void testGetStartStampTimeStart() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 3);
        LocalDateTime startStampTimeStart = timeslot.getStartStampTimeStart();

        assertEquals(LocalDateTime.parse("2023-01-01T08:30:00"), startStampTimeStart);
    }

    @Test
    public void testGetStartStampTimeEnd() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 3);
        LocalDateTime startStampTimeEnd = timeslot.getStartStampTimeEnd();

        assertEquals(LocalDateTime.parse("2023-01-01T09:00:00"), startStampTimeEnd);
    }

    @Test
    public void testGetEndStampTimeStart() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 3);
        LocalDateTime endStampTimeStart = timeslot.getEndStampTimeStart();

        assertEquals(LocalDateTime.parse("2023-01-01T14:20:00"), endStampTimeStart);
    }

    @Test
    public void testGetEndStampTimeEnd() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 3);
        LocalDateTime endStampTimeEnd = timeslot.getEndStampTimeEnd();

        assertEquals(LocalDateTime.parse("2023-01-01T14:40:00"), endStampTimeEnd);
    }

    @Test
    public void testIsBetweenStampStartTime() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 1);
        LocalDateTime validTime = LocalDateTime.parse("2023-05-01T08:45:00");
        LocalDateTime invalidTime = LocalDateTime.parse("2023-05-01T09:55:00");

        assertTrue(timeslot.isBetweenStampStartTime(validTime));
        assertFalse(timeslot.isBetweenStampStartTime(invalidTime));
    }

    @Test
    public void testIsBetweenStampEndTime() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 1);
        LocalDateTime validTime = LocalDateTime.parse("2023-05-01T10:20:00");
        LocalDateTime invalidTime = LocalDateTime.parse("2023-05-01T13:45:00");

        assertTrue(timeslot.isBetweenStampEndTime(validTime));
        assertFalse(timeslot.isBetweenStampEndTime(invalidTime));
    }

    @Test
    public void testIsOngoing() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 1);
        LocalDateTime validTime = LocalDateTime.parse("2023-05-01T08:55:00");
        LocalDateTime invalidTime = LocalDateTime.parse("2023-05-01T13:45:00");

        assertTrue(timeslot.isOngoing(validTime));
        assertFalse(timeslot.isOngoing(invalidTime));
    }

    @Test
    public void testGetStampStatus() {
        Timeslot timeslot = new Timeslot(1, 2606, 1, 1, 1);
        LocalDateTime beforeStartStamp = LocalDateTime.parse("2023-05-01T08:25:00");
        LocalDateTime startStamp = LocalDateTime.parse("2023-05-01T08:35:00");
        LocalDateTime inInvalidStamp = LocalDateTime.parse("2023-05-01T10:00:00");
        LocalDateTime endStamp = LocalDateTime.parse("2023-05-01T10:20:00");
        LocalDateTime afterEndStamp = LocalDateTime.parse("2023-05-01T13:45:00");

        assertEquals(StampStatus.OUT_INVALID, timeslot.getStampStatus(beforeStartStamp));
        assertEquals(StampStatus.START, timeslot.getStampStatus(startStamp));
        assertEquals(StampStatus.IN_INVALID, timeslot.getStampStatus(inInvalidStamp));
        assertEquals(StampStatus.END, timeslot.getStampStatus(endStamp));
        assertEquals(StampStatus.OUT_INVALID, timeslot.getStampStatus(afterEndStamp));
    }

    @Test
    public void testGetAttendStatus() {
        Timeslot timeslot = new Timeslot(1, 1, 1, 1, 1);
        List<LocalDateTime> timestamps = new ArrayList<>();
        timestamps.add(LocalDateTime.parse("2023-05-01T08:35:00")); // Start stamp
        timestamps.add(LocalDateTime.parse("2023-05-01T13:30:00")); // End stamp

        int attendStatus = timeslot.getAttendStatus(timestamps);
        assertEquals(AttendStatus.START_STAMP, attendStatus);
    }
}

