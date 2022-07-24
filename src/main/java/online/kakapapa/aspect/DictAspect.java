package online.kakapapa.aspect;

import online.kakapapa.annotation.Dict;
import online.kakapapa.annotation.EnableDict;
import online.kakapapa.annotation.WrapperField;
import online.kakapapa.customize.CustomizeConfig;
import online.kakapapa.vo.PaginateVO;
import online.kakapapa.vo.Ret;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Tien.Chang
 */
@Aspect
@Component
public class DictAspect {

    private final static String CLASS_CACHE_PREFIX = "#CLS";
    private final static String FIELD_CACHE_PREFIX = "#FED";
    private final static String TABLE_CACHE_PREFIX = "#TAB";
    private final CustomizeConfig customizeConfig;
    Logger log = LoggerFactory.getLogger(DictAspect.class);

    @Autowired
    public DictAspect(CustomizeConfig customizeConfig) {
        this.customizeConfig = customizeConfig;
    }

    @AfterReturning(pointcut = "@annotation(com.kakapapa.translator.annotation.EnableDict)", returning = "returnValue")
    public void doAfterReturning(JoinPoint point, Object returnValue) {
        StopWatch stopWatch = new StopWatch("dictAspect");
        stopWatch.start("translate-the-return");
        EnableDict annotation = ((MethodSignature) point.getSignature()).getMethod().getAnnotation(EnableDict.class);
        if (Objects.isNull(annotation)) {
            return;
        }
        if (Objects.isNull(returnValue)) {
            return;
        }
        if (!(returnValue instanceof Ret)) {
            return;
        }
        Class<?> returnBean = annotation.bean();
        String returnName = annotation.value();
        Ret r = (Ret) returnValue;
        Object returnObj = r.get(returnName);
        if (Objects.isNull(returnObj)) {
            return;
        }
        if (returnObj instanceof PaginateVO) {
            PaginateVO pageObj = (PaginateVO) returnObj;
            List<?> retList = pageObj.getList();
            List<Map<String, Object>> result = this.translateListObj(retList, returnBean);
            pageObj.setList(result);
        } else if (returnObj instanceof List) {
            List<?> retList = (List<?>) returnObj;
            List<Map<String, Object>> result = this.translateListObj(retList, returnBean);
            r.put(returnName, result);
        } else if (Objects.equals(returnObj.getClass().getName(), returnBean.getName())) {
            Map<String, Object> result = this.translateObj(returnObj);
            r.put(returnName, result);
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    private List<Map<String, Object>> translateListObj(List<?> retList, Class<?> returnBean) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(retList)) {
            return result;
        }
        retList.forEach(item -> {
            if (Objects.equals(item.getClass().getName(), returnBean.getName())) {
                Map<String, Object> map = this.translateObj(item);
                result.add(map);
            }
        });
        return result;
    }

    private Map<String, Object> translateObj(Object obj) {
        Map<String, Object> result = new HashMap<>(16);
        String objClassName = obj.getClass().getName();
        String classCacheKey = String.join("-", CLASS_CACHE_PREFIX, objClassName);
        // 缓存obj类的字段
        List<Field> fields = customizeConfig.getDictCache().getClassFieldCache().get(classCacheKey);
        if (CollectionUtils.isEmpty(fields)) {
            fields = FieldUtils.getAllFieldsList(obj.getClass());
            customizeConfig.getDictCache().getClassFieldCache().put(classCacheKey, fields);
        }
        fields.forEach(field -> {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                return;
            }
            try {
                String fieldName = field.getName();
                String translatedFieldName = fieldName + customizeConfig.getDictSuffix();
                // 缓存obj字段的method
                String fieldCacheKey = String.join("-", FIELD_CACHE_PREFIX, objClassName, fieldName);
                Method fieldReadMethod = customizeConfig.getDictCache().getFieldReadMethodCache().get(fieldCacheKey);
                if (fieldReadMethod == null) {
                    PropertyDescriptor fieldDescriptor = new PropertyDescriptor(fieldName, obj.getClass());
                    fieldReadMethod = fieldDescriptor.getReadMethod();
                    customizeConfig.getDictCache().getFieldReadMethodCache().put(fieldCacheKey, fieldReadMethod);
                }
                Object fieldValue = fieldReadMethod.invoke(obj);

                WrapperField wrapperField = field.getAnnotation(WrapperField.class);
                if (Objects.nonNull(wrapperField)) {
                    if (fieldValue instanceof Collection) {
                        List<?> fieldList = (List<?>) fieldValue;
                        if (CollectionUtils.isEmpty(fieldList)) {
                            result.put(fieldName, new ArrayList<>());
                        } else {
                            result.put(fieldName, translateListObj(fieldList, ((List<?>) fieldValue).get(0).getClass()));
                        }
                    } else {
                        result.put(fieldName, translateObj(fieldValue));
                    }
                } else {
                    // 获取字段的Dict注解配置信息
                    Dict dict = field.getAnnotation(Dict.class);
                    // 没有Dict注解或者字段值为空时，不需要翻译
                    if (Objects.isNull(dict) || Objects.isNull(fieldValue)) {
                        result.put(fieldName, fieldValue);
                    } else {
                        String keyVal = dict.keyVal();
                        // 优先使用keyVal的配置内容
                        if (StringUtils.isNotBlank(keyVal)) {
                            Map<String, String> dictMap = this.convertKeyValue(keyVal);
                            result.put(fieldName, fieldValue);
                            result.put(translatedFieldName, dictMap.getOrDefault(fieldValue.toString(), ""));
                        } else {
                            String table = dict.table();
                            String index = dict.index();
                            String name = dict.name();
                            String condition = dict.condition();
                            // Dict注解的内容配置不完整时，无法进行翻译
                            if (StringUtils.isBlank(table) || StringUtils.isBlank(index) || StringUtils.isBlank(name)) {
                                result.put(fieldName, fieldValue);
                            } else {
                                // 缓存字典表
                                String tableCacheKey = String
                                        .join("-", TABLE_CACHE_PREFIX, table, index, name, StringUtils.deleteWhitespace(condition))
                                        .toUpperCase();
                                Map<String, String> dictMap = customizeConfig.getDictCache().getFieldValueDictCache().get(tableCacheKey);
                                if (Objects.isNull(dictMap) || dictMap.isEmpty()) {
                                    // 执行字典表的查询处理
                                    dictMap = customizeConfig.getDictAspectService().getDictMap(table, index, name, condition);
                                    customizeConfig.getDictCache().getFieldValueDictCache().put(tableCacheKey, dictMap);
                                }
                                String dictValue = dictMap.getOrDefault(fieldValue.toString(), "");
                                result.put(fieldName, fieldValue);
                                result.put(translatedFieldName, dictValue);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("翻译结果中的字典值时出错了。。。", ex);
            }
        });
        return result;
    }

    /**
     * 将Dict注解中keyVal转成map
     *
     * @param keyValue 格式k1:v1|k2:v2
     * @return key:value的map
     */
    private Map<String, String> convertKeyValue(String keyValue) {
        Map<String, String> result = new HashMap<>(16);
        if (StringUtils.isBlank(keyValue)) {
            return result;
        }
        String[] elements = keyValue.split("\\|");
        if (elements.length < 1) {
            return result;
        }
        int kvLen = 2;
        Arrays.stream(elements).forEach(item -> {
            String[] kv = item.split(":");
            if (kv.length != kvLen) {
                return;
            }
            result.put(kv[0], kv[1]);
        });
        return result;
    }
}
