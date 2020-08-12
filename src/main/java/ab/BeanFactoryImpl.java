package ab;

import ab.annotation.BeanFactory;

@BeanFactory
public class BeanFactoryImpl {

    public Test getTest() {
        return new Test("test 1");
    }
}
