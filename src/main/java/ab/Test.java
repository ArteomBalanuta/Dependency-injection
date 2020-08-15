package ab;

import ab.annotation.DependencyInjectionEntryPoint;

public class Test {
    private String id;

    public Test(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                '}';
    }
}
