package com.taotao.order.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;

/**
 * 订单确认
 * 此类描述的是：
 * @Title: OrderCartController
 * @Description: 
 * @Company: www.itcast.cn 
 * @version 1.0
 */
@Controller
public class OrderCartController {
	@Value("${TT_CART}")
	private String TT_CART;
	@Autowired
	private OrderService orderService;
	/**
	 * 订单确认列表
	 * 此方法描述的是：
	 * @Title:showrderCart
	 * @Description: 
	 * @Company: www.itcast.cn
	 * @version 1.0
	 */
	@RequestMapping("/order/order-cart")
	public String showrderCart(HttpServletRequest request){
		//用户必须是登录的
		//取出用户id
		TbUser user = (TbUser)request.getAttribute("user");
		System.out.println(user.getUsername());
		//根据用户信息取得收货地址//使用静态数据在页面上展示
		//从cookie中取得商品列表展示到页面
		ArrayList<TbItem> items = getCartItem(request);
		request.setAttribute("cartList", items);
		//返回页面
		return "order-cart";
	}

	/***
	 * 
	 * 此方法描述的是：
	 * 
	 * @Title:getCartItem
	 * @Description:
	 * @Company: www.itcast.cn
	 * @version 1.0
	 */
	private ArrayList<TbItem> getCartItem(HttpServletRequest request) {
		String jsonStr = CookieUtils.getCookieValue(request, TT_CART, true);
		if (StringUtils.isBlank(jsonStr)) {
			return new ArrayList<TbItem>();
		}
		ArrayList<TbItem> retItems = (ArrayList<TbItem>) JsonUtils.jsonToList(jsonStr, TbItem.class);
		return retItems;
	}
	/**
	 * 生成订单处理
	 */
	@RequestMapping(value="/order/create", method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo, Model model) {
		//生成订单
		TaotaoResult result = orderService.createOrder(orderInfo);
		//返回逻辑视图
		model.addAttribute("orderId", result.getData().toString());
		model.addAttribute("payment", orderInfo.getPayment());
		//预计送达时间，预计三天后送达
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
		
		return "success";
	}
}
