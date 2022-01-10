package com.imooc.course.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

@Component
public class PostRequestFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // 过滤器类型
        return FilterConstants.POST_TYPE;
    }

    /**
     * 过滤器顺序, 当过滤器较多时, 需要使用
     *
     * 此处过滤器发生的时间一定是在请求返回之前运行
     */
    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        // 是否启用过滤器
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        Long startTime = (Long) currentContext.get("startTime");
        long duration = System.currentTimeMillis() - startTime;
        String requestURI = currentContext.getRequest().getRequestURI();
        System.out.println("url: " + requestURI + "|" + "duration: " + duration);

        return null;
    }
}
