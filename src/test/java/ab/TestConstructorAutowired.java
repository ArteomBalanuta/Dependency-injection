package ab;


import ab.di.Context;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;


public class TestConstructorAutowired {
    private static final String packageName = "ab";
    private Context context;

    @Before
    public void setUp() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        context = new Context(packageName, Class.forName("ab.ServiceConstructorParamInjection"));
    }

    @Test
    public void givenContextWithFactory_whenAnnotated_expectedInstanceFromFactory() {
        //arrange
        String expected = ((BeanFactoryImpl) context.getFactoryInstance()).getTest().toString();

        //act
        String actual = ((ServiceConstructorParamInjection) context.getEntryPoint()).getTestField().toString();

        //assert
        assertEquals(expected, actual);
    }
}

