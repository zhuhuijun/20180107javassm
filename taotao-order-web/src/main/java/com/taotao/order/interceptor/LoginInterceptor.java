package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;

/**
 * 登录的拦截 此类描述的是：
 * 
 * @Title: LoginInterceptor
 * @Description:
 * @Company: www.itcast.cn
 * @version 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
	// 标识
	@Value("${TT_TOKEN}")
	private String TT_TOKEN;
	@Value("${LOGIN_DOMAIN}")
	private String LOGIN_DOMAIN;
	@Autowired
	private UserService userService;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception arg3) throws Exception {
		// 顶多执行异常的处理
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		// handler执行之后//modelAndView执行之前
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 执行handler之前先执行此方法
		// true放行//false拦截
		// 从cookie中取tooken
		String token = CookieUtils.getCookieValue(request, TT_TOKEN);
		String reqUrl = request.getRequestURL().toString();
		System.out.println(reqUrl);
		System.out.println(token);
		if (StringUtils.isBlank(token)) {
			response.sendRedirect(LOGIN_DOMAIN + "/page/login?url=" + reqUrl);
			return false;
		}
		TaotaoResult userinfo = userService.getUserByToken(token);
		System.out.println(userinfo.getStatus());
		if (userinfo.getStatus() != 200) {
			// 如果 取不到tooken 跳转到sso 将请求地址作为url参数传递
			response.sendRedirect(LOGIN_DOMAIN + "/page/login?url=" + reqUrl);
			return false;
		}
		// 如果登录了那就放行取得用户信息
		TbUser uu = (TbUser) userinfo.getData();
		request.setAttribute("user", uu);
		return true;
	}

}
