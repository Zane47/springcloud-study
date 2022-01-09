package com.imooc.course.service.impl;

import com.imooc.course.dao.CoursePriceMapper;
import com.imooc.course.entity.CoursePrice;
import com.imooc.course.service.CoursePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoursePriceServiceImpl implements CoursePriceService {

    @Autowired
    private CoursePriceMapper coursePriceMapper;

    @Override
    public CoursePrice getCoursePrice() {
        return coursePriceMapper.findCoursePrice();
    }
}
