package com.msgtouch.framework.annotation;

import java.lang.annotation.*;

/**
 * Created by Dean on 2016/9/8.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MsgService {
    //集群名
    String value() default "";
}
