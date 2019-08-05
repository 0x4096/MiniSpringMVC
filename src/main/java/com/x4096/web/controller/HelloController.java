package com.x4096.web.controller;

import com.x4096.web.mvc.annotation.Autowired;
import com.x4096.web.mvc.annotation.Controller;
import com.x4096.web.mvc.annotation.RequestMappering;
import com.x4096.web.service.HelloService;

/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 23:13
 * @Description:
 */
@Controller
public class HelloController {

    @Autowired
    private HelloService helloService;

    @RequestMappering(value = "/test/hello")
    public String hello() {
        return helloService.hello();
    }


}
