package com.guo.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.guo.reggie.common.BaseContext;
import com.guo.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 检查用户是否登录成功
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response =(HttpServletResponse) servletResponse;
        //1、获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",request.getRequestURI());
        //定义不需要拦截的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2、判断本次请求是否处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        //4-1、判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            //通过线程全局常量ThreadLocal运送 id值
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            log.info("状态：用户已登录");
            return;
        }
        //4-2、判断登录状态，如果已经登录，则直接放行
        if(request.getSession().getAttribute("user")!=null){
            //通过线程全局常量ThreadLocal运送 id值
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            log.info("状态：用户已登录");
            return;
        }

        log.info("状态：用户未登录");
        //5、如果未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public boolean check(String[] urls,String requestUrl){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if(match){
                return true;
            }
        }
        return false;
    }

}
