package com.imooc.course.dao;

import com.imooc.course.entity.CoursePrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CoursePriceMapper {

    @Select("select * from CoursePri")
    public CoursePrice findCoursePrice();
}
