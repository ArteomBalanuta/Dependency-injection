package ab.test;

import ab.annotation.BeanFactory;
import ab.test.model.Test;
import ab.test.model.TestSecond;

@BeanFactory
public class BeanFactoryImpl {

    public Test getTest() {
        return new Test("test 1");
    }

    public TestSecond getTestSecond() {
        return new TestSecond(13);
    }
}
