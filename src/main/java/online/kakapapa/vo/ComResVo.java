package online.kakapapa.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tien.Chang
 */
public class ComResVo extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public ComResVo() {
        put("code", 0);
        put("msg", "success");
    }

    public static ComResVo fail() {
        return fail(-1, "Unknown Exception");
    }

    public static ComResVo fail(String msg) {
        return fail(-1, msg);
    }

    public static ComResVo fail(int code, String msg) {
        ComResVo comResVo = new ComResVo();
        comResVo.put("code", code);
        comResVo.put("msg", msg);
        return comResVo;
    }

    public static ComResVo success(String msg) {
        ComResVo comResVo = new ComResVo();
        comResVo.put("msg", msg);
        return comResVo;
    }

    public static ComResVo success(Map<String, Object> map) {
        ComResVo comResVo = new ComResVo();
        comResVo.putAll(map);
        return comResVo;
    }

    public static ComResVo success() {
        return new ComResVo();
    }

    @Override
    public ComResVo put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public int getCode() {
        return (int) this.get("code");
    }
}
