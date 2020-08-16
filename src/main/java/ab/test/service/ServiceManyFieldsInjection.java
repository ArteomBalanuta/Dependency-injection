package ab.test.service;

import ab.annotation.AutoWired;
import ab.annotation.DependencyInjectionEntryPoint;
import ab.test.model.Test;
import ab.test.model.TestSecond;

@DependencyInjectionEntryPoint
public class ServiceManyFieldsInjection {

    @AutoWired
    public Test testField;

    @AutoWired
    public TestSecond testSecond;

    public Test getTestField() {
        return testField;
    }

    public TestSecond getTestSecond() {
        return testSecond;
    }
}
