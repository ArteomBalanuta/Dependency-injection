package ab;

import ab.annotation.AutoWired;
import ab.annotation.DependencyInjectionEntryPoint;

@DependencyInjectionEntryPoint
public class ServiceFieldInjection {

    @AutoWired
    public ab.Test testField;

    public Test getTestField() {
        return testField;
    }
}
