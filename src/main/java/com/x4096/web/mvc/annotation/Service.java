package com.x4096.web.mvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 11:32
 * @Description:
 */
@Documented
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Service {

    String value() default "";

}
