package ab;

import ab.annotation.AutoWired;
import ab.annotation.DependencyInjectionEntryPoint;

@DependencyInjectionEntryPoint
public class ServiceConstructorParamInjection {

    public Test testField;

    public Test getTestField() {
        return testField;
    }

    @AutoWired
    public ServiceConstructorParamInjection(Test testField) {
        this.testField = testField;
    }
}
