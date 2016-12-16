package com.msgtouch.framework.annotation;

import java.lang.annotation.*;

/**
 * Created by Dean on 2016/9/8.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Unauthorization {
    //集群名
    String value() default "";
}
