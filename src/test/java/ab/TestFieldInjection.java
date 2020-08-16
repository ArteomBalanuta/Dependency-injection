package ab;

import ab.di.Context;
import ab.di.ContextConfig;
import ab.testservices.ServiceFieldInjection;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

public class TestFieldInjection {
    private static final String PACKAGE_NAME = "ab";
    private static final boolean IS_FIELD_INJECT = true;
    private static final boolean IS_CONSTRUCTOR_INJECT = false;
    private ContextConfig contextConfig;
    private Context context;

    @Before
    public void setUp() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        contextConfig = new ContextConfig(IS_FIELD_INJECT, IS_CONSTRUCTOR_INJECT, PACKAGE_NAME);
        context = new Context(contextConfig);
    }

    @Test
    public void givenContextWithFactory_whenAnnotated_expectedInstanceFromFactory() {
        //arrange
        String expected = ((BeanFactoryImpl) context.getFactoryInstance()).getTest().toString();

        //act
        String actual = ((ServiceFieldInjection) context.getEntryPoint()).getTestField().toString();

        //assert
        assertEquals(expected, actual);
    }
}
