package com.yu.lib.annotations.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynaPermission {
    String value() default "";
    String[] values() default {};
    String failInfo() default "";
    boolean isFailDialogClickFinishActivity() default false;
}
