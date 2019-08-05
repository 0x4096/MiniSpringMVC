package com.x4096.web.service.impl;

import com.x4096.web.mvc.annotation.Service;
import com.x4096.web.service.HelloService;

/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 23:14
 * @Description:
 */
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello() {
        return "hello";
    }

}
