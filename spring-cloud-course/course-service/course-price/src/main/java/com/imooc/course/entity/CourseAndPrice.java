package com.imooc.course.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 课程和价格融合类
 */
@Getter
@Setter
@ToString
public class CourseAndPrice {
    Integer id;
    Integer courseId;
    String courseName;
    Float price;
}
