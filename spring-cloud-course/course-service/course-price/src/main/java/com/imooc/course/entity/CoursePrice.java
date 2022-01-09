package com.imooc.course.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CoursePrice {
    Integer id;
    Integer courseId;
    Float price;
}
