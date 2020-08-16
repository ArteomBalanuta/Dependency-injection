package ab;

import ab.di.Context;
import ab.di.ContextConfig;
import ab.test.BeanFactoryImpl;
import ab.test.service.ServiceFieldInjection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestFieldInjection {
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

    @Test
    public void givenContextWithFactory_whenAnnotated_expectedInstanceFromFactory() {
        //arrange
        String expected = ((BeanFactoryImpl) context.getFactoryInstance()).getTest().toString();

        //act
        String actual = ((ServiceFieldInjection) context.getBean("ab.test.service.ServiceFieldInjection")).getTestField().toString();

        //assert
        assertEquals(expected, actual);
    }
}
