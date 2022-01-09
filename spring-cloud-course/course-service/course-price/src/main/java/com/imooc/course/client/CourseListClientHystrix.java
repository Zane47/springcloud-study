package com.imooc.course.client;

import com.imooc.course.entity.Course;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 断路器实现类
 */
@Component
public class CourseListClientHystrix implements CourseListClient {

    @Override
    public List<Course> getCourseList() {
        final List<Course> defaultCourse = new ArrayList<>();
        // 实际生产中添加首页课程或者热卖课程
        Course course = new Course();
        course.setId(1);
        course.setCourseId(1);
        course.setCourseName("default course name");
        course.setValid(1);
        defaultCourse.add(course);
        return defaultCourse;

        // 或者直接返回空列表
        // return Collections.emptyList();
    }
}
