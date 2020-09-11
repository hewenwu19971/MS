package com.hww.ms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MSController {

	@RequestMapping("/seckill")
	@ResponseBody
	public String seckill() {
		System.out.println("进入");
		return "哈哈哈";
	}
}
