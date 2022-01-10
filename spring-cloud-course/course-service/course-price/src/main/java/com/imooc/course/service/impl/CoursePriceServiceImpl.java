package com.imooc.course.service.impl;

import com.imooc.course.client.CourseListClient;
import com.imooc.course.dao.CoursePriceMapper;
import com.imooc.course.entity.Course;
import com.imooc.course.entity.CourseAndPrice;
import com.imooc.course.entity.CoursePrice;
import com.imooc.course.service.CoursePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CoursePriceServiceImpl implements CoursePriceService {

    @Autowired
    private CoursePriceMapper coursePriceMapper;

    @Autowired
    private CourseListClient feignClient;

    @Override
    public CoursePrice getCoursePrice(Integer courseId) {
        return coursePriceMapper.findCoursePrice(courseId);
    }

    /**
     * 课程列表和课程price做融合处理
     *
     * 首先从list模块中拿到所有列表
     */
    @Override
    public List<CourseAndPrice> getCourseAndPrice() {
        List<CourseAndPrice> courseAndPriceList = new ArrayList<>();

        List<Course> courseList = feignClient.getCourseList();
        for (Course course : courseList) {
            CoursePrice coursePrice = getCoursePrice(course.getCourseId());
            Optional.ofNullable(coursePrice).ifPresent(price -> {
                CourseAndPrice courseAndPrice = new CourseAndPrice();
                courseAndPrice.setId(course.getId());
                courseAndPrice.setCourseId(course.getCourseId());
                courseAndPrice.setCourseName(course.getCourseName());
                courseAndPrice.setPrice(price.getPrice());
                courseAndPriceList.add(courseAndPrice);
            });
        }

        return courseAndPriceList;
    }
}
