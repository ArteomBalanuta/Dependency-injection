package ab;

import ab.annotation.AutoWired;
import ab.annotation.DependencyInjectionEntryPoint;
import ab.di.Context;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

@DependencyInjectionEntryPoint
public class AppTest {
    private static final String packageName = "ab";
    private Context context;

    public ab.Test getInjectedField() {
        return injectedField;
    }

    @AutoWired
    public ab.Test injectedField;

    @Before
    public void setUp() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        context = new Context(packageName);
    }

    @Test
    public void givenContextWithFactory_whenAnnotated_expectedInstanceFromFactory() {
        //arrange
        String expected = ((BeanFactoryImpl) context.getFactoryInstance()).getTest().toString();

        //act
        String actual = ((AppTest) context.getEntryPoint()).getInjectedField().toString();

        //assert
        assertEquals(expected, actual);
    }
}
