package online.kakapapa.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tien.Chang
 */
public class Ret extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public Ret() {
        put("code", 0);
        put("msg", "success");
    }

    public static Ret fail() {
        return fail(-1, "未知异常，请联系管理员");
    }

    public static Ret fail(String msg) {
        return fail(-1, msg);
    }

    public static Ret fail(int code, String msg) {
        Ret ret = new Ret();
        ret.put("code", code);
        ret.put("msg", msg);
        return ret;
    }

    public static Ret success(String msg) {
        Ret ret = new Ret();
        ret.put("msg", msg);
        return ret;
    }

    public static Ret success(Map<String, Object> map) {
        Ret ret = new Ret();
        ret.putAll(map);
        return ret;
    }

    public static Ret success() {
        return new Ret();
    }

    @Override
    public Ret put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public int getCode() {
        return (int) this.get("code");
    }
}
