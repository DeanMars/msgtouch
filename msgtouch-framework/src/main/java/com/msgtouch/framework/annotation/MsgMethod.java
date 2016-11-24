package com.msgtouch.framework.annotation;

import java.lang.annotation.*;

/**
 * Created by Dean on 2016/9/8.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MsgMethod {
    String value();
}
