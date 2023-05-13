package com.nyokinyoki.TimeTable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.nyokinyoki.TimeTable.Course.Course;
import com.nyokinyoki.TimeTable.Course.CourseDAO;
import com.nyokinyoki.TimeTable.Course.TimeSlot.TimeSlot;

public class TimeTable {
    private final List<Course> courses;
    private final TimeTableDAO timeTableDAO;
    private final CourseDAO courseDAO;

    public TimeTable(TimeTableDAO timeTableDAO, CourseDAO courseDAO) {
        this.timeTableDAO = timeTableDAO;
        this.courseDAO = courseDAO;
        this.courses = timeTableDAO.getAll();
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        if (!this.isAvailable(course)) {
            throw new IllegalArgumentException("Course is not available");
        }

        timeTableDAO.add(course);
        courses.add(course);
    }

    public void addCourse(int id) {
        addCourse(courseDAO.getById(id));
    }

    public void removeCourse(Course course) {
        timeTableDAO.remove(course.getId());
        courses.removeIf(c -> c.equals(course));
    }

    public void removeCourse(int id) {
        removeCourse(courseDAO.getById(id));
    }

    public TimeSlot getOngoingTimeSlot(LocalDateTime timestamp) {
        return courses.stream().map(course -> course.getOngoingTimeSlot(timestamp)).filter(Objects::nonNull).findFirst()
                .orElse(null);
    }

    public List<TimeSlot> getTimeSlotsByDayOfWeek(DayOfWeek dayOfWeek) {
        return courses.stream().flatMap(course -> course.getTimeSlotsByDayOfWeek(dayOfWeek).stream())
                .collect(Collectors.toList());
    }

    public List<Course> getAvailableCourses() {
        return courseDAO.getAll().stream().filter(this::isAvailable).collect(Collectors.toList());
    }

    public List<Course> getAvailableCoursesByPeriod(int dayOfWeek, int beginPeriod) {
        return courseDAO.getByPeriod(dayOfWeek, beginPeriod).stream().filter(this::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean isAvailable(Course course) {
        return courses.stream()
                .noneMatch(existingCourse -> existingCourse.equals(course) || existingCourse.overlapsWith(course));
    }

    @Override
    public String toString() {
        return courses.stream().map(Course::toString).collect(Collectors.joining("\n"));
    }
}
