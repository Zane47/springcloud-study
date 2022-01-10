package com.imooc.course.service;

import com.imooc.course.entity.CourseAndPrice;
import com.imooc.course.entity.CoursePrice;

import java.util.List;

/**
 * 课程价格服务
 */
public interface CoursePriceService {

    public CoursePrice getCoursePrice(Integer courseId);

    public List<CourseAndPrice> getCourseAndPrice();

}
