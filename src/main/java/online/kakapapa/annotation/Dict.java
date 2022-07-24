package online.kakapapa.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tien.Chang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {
    /**
     * 字典所在的表名
     */
    String table() default "";

    /**
     * 字典索引的列名
     */
    String index() default "id";

    /**
     * 字典值的列名
     */
    String name() default "name";

    /**
     * 字典所在的表数据的过滤条件，sql语句where里的内容
     */
    String condition() default "";

    /**
     * 键值对，适用于固定值的字段转换，格式为 k1:v1|k2:v2
     */
    String keyVal() default "";
}
