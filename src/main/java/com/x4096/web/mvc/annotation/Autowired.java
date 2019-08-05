package com.x4096.web.mvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 11:29
 * @Description:
 */
@Documented
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Autowired {

    String value() default "";

}
