package com.imooc.course.client;


import com.imooc.course.entity.Course;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 课程列表Feign客户端
 * 根据course-list中的定义来使用
 *
 * 因为服务很多, 所以需要添加参数表明是哪个服务的
 */
@FeignClient(value = "course-list", fallback = CourseListClientHystrix.class)
@Primary
public interface CourseListClient {

    /**
     * course-list模块中的方法, 根据course-list中的定义来使用
     */
    @GetMapping("/courses")
    List<Course> getCourseList();

}
