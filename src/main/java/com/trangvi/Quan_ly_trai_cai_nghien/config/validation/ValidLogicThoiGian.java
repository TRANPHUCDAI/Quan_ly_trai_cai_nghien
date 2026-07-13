package com.trangvi.Quan_ly_trai_cai_nghien.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // Khai báo áp dụng cho cấp độ Class (Entity)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LogicThoiGianValidator.class)
public @interface ValidLogicThoiGian {
    String message() default "Logic ngày tháng không hợp lệ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}