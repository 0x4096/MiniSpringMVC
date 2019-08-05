package com.x4096.web.controller;

import com.x4096.web.mvc.annotation.Autowired;
import com.x4096.web.mvc.annotation.Controller;
import com.x4096.web.mvc.annotation.RequestMappering;
import com.x4096.web.service.HiService;
import com.x4096.web.vo.resp.SupermanRespVO;


/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 11:41
 * @Description:
 */
@Controller
@RequestMappering(value = "/test")
public class HiController {

    @Autowired
    private HiService hiService;

    @RequestMappering(value = "/hi")
    public SupermanRespVO hi() {
        return hiService.hi("");
    }

}
