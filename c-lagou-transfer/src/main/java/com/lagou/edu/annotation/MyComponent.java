package com.lagou.edu.annotation;

import org.springframework.stereotype.Indexed;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyComponent {
}
