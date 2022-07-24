package online.kakapapa.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tien.Chang
 */
public interface DictAspectService {

    /**
     * 获取字典的map对象
     *
     * @param table     所在表
     * @param index     字典索引的列名
     * @param name      字典值的列名
     * @param condition 字典组的过滤条件
     * @return 字典的map对象
     */
    default Map<String, String> getDictMap(String table, String index, String name, String condition) {
        return new HashMap<>(16);
    }
}
