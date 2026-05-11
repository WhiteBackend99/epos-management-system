package com.epos.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.epos.backend.enums.AuditAction;
import com.epos.backend.enums.AuditType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    AuditType type();
    AuditAction action();
    String referenceIdField() default "";
    boolean saveRequest() default true;
    boolean saveResponse() default true;

}
