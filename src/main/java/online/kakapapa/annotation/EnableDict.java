package online.kakapapa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tien.Chang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableDict {
    /**
     * 待转对象的类型
     */
    Class<?> bean();

    /**
     * 返回值R中的对象key
     */
    String value() default "list";
}
