package com.imooc.course.controller;

import com.imooc.course.client.CourseListClient;
import com.imooc.course.entity.Course;
import com.imooc.course.entity.CourseAndPrice;
import com.imooc.course.entity.CoursePrice;
import com.imooc.course.service.CoursePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CoursePriceController {

    @Autowired
    private CoursePriceService coursePriceService;

    @Autowired
    private CourseListClient feignClient;

    @GetMapping("/price")
    public Float getCoursePrice(Integer courseId) {
        final CoursePrice coursePrice = coursePriceService.getCoursePrice(courseId);
        return coursePrice.getPrice();
    }


    // course-price中调用course-list
    @GetMapping("/coursesInPrice")
    public List<Course> getCourseListInPrice() {
        return feignClient.getCourseList();
    }


    @GetMapping("/coursesAndPrice")
    public List<CourseAndPrice> getCourseAndPrice() {
        return coursePriceService.getCourseAndPrice();
    }

}
