package online.kakapapa.customize.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Tien.Chang
 */
public interface DictCache {

    /**
     * 提供类的成员属性缓存
     *
     * @return key-类的唯一标识，value-类的成员属性
     */
    default Map<String, List<Field>> getClassFieldCache() {
        Cache<String, List<Field>> classFieldCache =
                Caffeine.newBuilder().expireAfterWrite(getRoundMin() + ThreadLocalRandom.current().nextInt(getDeciMin()), getTimeUnit()).build();
        return classFieldCache.asMap();
    }

    /**
     * 成员属性的get方法缓存
     *
     * @return key-成员变量唯一标识，value-成员变量的get方法
     */
    default Map<String, Method> getFieldReadMethodCache() {
        Cache<String, Method> fieldReadMethodCache =
                Caffeine.newBuilder().expireAfterWrite(getRoundMin() + ThreadLocalRandom.current().nextInt(getDeciMin()), getTimeUnit()).build();
        return fieldReadMethodCache.asMap();
    }

    /**
     * 成员属性值的字典缓存
     *
     * @return key-字典组，value-数据的字典
     */
    default Map<String, Map<String, String>> getFieldValueDictCache() {
        Cache<String, Map<String, String>> fieldValueDictCache =
                Caffeine.newBuilder().expireAfterWrite(getRoundMin() + ThreadLocalRandom.current().nextInt(getDeciMin()), getTimeUnit()).build();
        return fieldValueDictCache.asMap();
    }

    /**
     * 设置缓存的有效期的基数
     *
     * @return 有效期的基数
     */
    default int getRoundMin() {
        return 60;
    }

    /**
     * 设置缓存的有效期的随机数
     *
     * @return 随机整数
     */
    default int getDeciMin() {
        return 10;
    }

    /**
     * 设置缓存的时间单位
     *
     * @return 时间单位
     */
    default TimeUnit getTimeUnit() {
        return TimeUnit.MINUTES;
    }
}
