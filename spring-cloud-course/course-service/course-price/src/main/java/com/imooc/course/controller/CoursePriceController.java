package com.imooc.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoursePriceController {

    @GetMapping("/price")
    public Integer getCoursePrice(Integer courseId) {
        return 0;
    }

}
