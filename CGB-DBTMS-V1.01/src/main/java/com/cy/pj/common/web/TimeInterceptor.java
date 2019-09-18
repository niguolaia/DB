package com.cy.pj.common.web;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import com.cy.pj.sys.service.exception.ServiceException;
/**
 * 时间访问拦截
 * 1)允许时间点内访问则放行
 * 2)不允许的时间点内访问则拒绝继续执行.
 */
public class TimeInterceptor implements HandlerInterceptor {
	/**后端控制器方法执行之前执行*/
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("preHandle");
		//1.获取当前时间的日历对象
		Calendar c=Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 8);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		//获取允许访问的开始时间
		long start=c.getTimeInMillis();
		c.set(Calendar.HOUR_OF_DAY, 24);
		//获取允许访问的结束时间
		long end=c.getTimeInMillis();
		long time=System.currentTimeMillis();
		if(time<start||time>end)
		throw new ServiceException("此时间之内不允许访问");
		return true;//true表示放行,false 反之.
	}
}
