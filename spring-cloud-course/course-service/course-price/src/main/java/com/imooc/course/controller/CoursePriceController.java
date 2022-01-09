package com.imooc.course.controller;

import com.imooc.course.entity.CoursePrice;
import com.imooc.course.service.CoursePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CoursePriceController {

    @Autowired
    private CoursePriceService coursePriceService;

    @GetMapping("/price")
    public Float getCoursePrice(Integer courseId) {
        final CoursePrice coursePrice = coursePriceService.getCoursePrice(courseId);
        return coursePrice.getPrice();
    }

}
