package com.x4096.web.service.impl;

import com.x4096.web.mvc.annotation.Service;
import com.x4096.web.service.HiService;
import com.x4096.web.vo.resp.SupermanRespVO;

/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 11:44
 * @Description:
 */
@Service
public class HiServiceImpl implements HiService {

    @Override
    public SupermanRespVO hi(String say) {
        return SupermanRespVO.builder().username("哪吒").age(18).build();
    }

}
