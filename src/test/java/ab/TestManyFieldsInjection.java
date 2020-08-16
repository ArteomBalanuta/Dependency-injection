package ab;

import ab.di.Context;
import ab.di.ContextConfig;
import ab.test.BeanFactoryImpl;
import ab.test.service.ServiceManyFieldsInjection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestManyFieldsInjection {
    private static final String PACKAGE_NAME = "ab";
    private static final boolean IS_FIELD_INJECT = true;
    private static final boolean IS_CONSTRUCTOR_INJECT = false;
    private ContextConfig contextConfig;
    private Context context;

    @BeforeAll
    public void setUp() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        contextConfig = new ContextConfig(IS_FIELD_INJECT, IS_CONSTRUCTOR_INJECT, PACKAGE_NAME);
        context = new Context(contextConfig);
    }

    @org.junit.jupiter.api.Test
    public void givenContextWithFactory_whenAnnotated_expectedInstanceFromFactory() {
        //arrange
        String expectedTest = ((BeanFactoryImpl) context.getFactoryInstance()).getTest().toString();
        String expectedTestSecond = ((BeanFactoryImpl) context.getFactoryInstance()).getTestSecond().toString();

        //act
        String actualTest = ((ServiceManyFieldsInjection)
                context.getBean("ab.test.service.ServiceManyFieldsInjection")).getTestField().toString();
        String actualTestSecond = ((ServiceManyFieldsInjection)
                context.getBean("ab.test.service.ServiceManyFieldsInjection")).getTestSecond().toString();

        //assert
        Assertions.assertAll(() -> assertEquals(expectedTest, actualTest),
                             () -> assertEquals(expectedTestSecond, actualTestSecond));
    }
}
