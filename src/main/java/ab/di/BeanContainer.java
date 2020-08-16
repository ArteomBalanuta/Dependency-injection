package ab.di;


import java.util.HashMap;
import java.util.Map;

//TODO make di persist beans as singletones
public class BeanContainer {
    Map<String, Object> container = new HashMap<>();

    public void add(String name, Object value) {
        container.put(name, value);
    }

    public Object get(String key) {
        return container.get(key);
    }
}
