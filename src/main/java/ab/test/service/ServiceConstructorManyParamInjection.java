
package ab.test.service;

import ab.annotation.AutoWired;
import ab.annotation.DependencyInjectionEntryPoint;
import ab.test.model.Test;
import ab.test.model.TestSecond;

@DependencyInjectionEntryPoint
public class ServiceConstructorManyParamInjection {

    public Test testField;
    public TestSecond testsSecond;

    public Test getTestField() {
        return testField;
    }

    public TestSecond getTestsSecond() { return testsSecond; }

    @AutoWired
    public ServiceConstructorManyParamInjection(Test testField, TestSecond testSecond) {
        this.testField = testField;
        this.testsSecond = testSecond;
    }
}
