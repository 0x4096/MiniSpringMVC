package com.x4096.web.mvc.annotation;

import java.lang.annotation.*;

/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 11:35
 * @Description:
 */
@Documented
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RequestMappering {

    String value() default "";

}
