package online.kakapapa.customize;

import online.kakapapa.customize.cache.DictCache;
import online.kakapapa.service.DictAspectService;
import org.springframework.context.annotation.Configuration;

/**
 * @author Tien.Chang
 */
@Configuration
public class CustomizeConfig {

    private DictAspectService dictAspectService;

    private DictCache dictCache;

    private String dictSuffix = "DICT";

    public DictAspectService getDictAspectService() {
        return dictAspectService;
    }

    public void setDictAspectService(DictAspectService dictAspectService) {
        this.dictAspectService = dictAspectService;
    }

    public DictCache getDictCache() {
        return dictCache;
    }

    public void setDictCache(DictCache dictCache) {
        this.dictCache = dictCache;
    }

    public String getDictSuffix() {
        return dictSuffix;
    }

    public void setDictSuffix(String dictSuffix) {
        this.dictSuffix = dictSuffix;
    }
}
