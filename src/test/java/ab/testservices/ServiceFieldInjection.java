package ab.testservices;

import ab.Test;
import ab.annotation.AutoWired;
import ab.annotation.DependencyInjectionEntryPoint;

@DependencyInjectionEntryPoint
public class ServiceFieldInjection {

    @AutoWired
    public Test testField;

    public Test getTestField() {
        return testField;
    }
}
