package ab.di;

public class ContextConfig {
    boolean isFieldsInject;
    boolean isConstructorInject;

    String packageScanDir;

    public boolean isFieldsInject() {
        return isFieldsInject;
    }

    public void setFieldsInject(boolean fieldsInject) {
        isFieldsInject = fieldsInject;
    }

    public boolean isConstructorInject() {
        return isConstructorInject;
    }

    public void setConstructorInject(boolean constructorInject) {
        isConstructorInject = constructorInject;
    }

    public String getPackageScanDir() {
        return packageScanDir;
    }

    public void setPackageScanDir(String packageScanDir) {
        this.packageScanDir = packageScanDir;
    }

    public ContextConfig(boolean isFieldsInject, boolean isConstructorInject, String packageScanDir) {
        this.isFieldsInject = isFieldsInject;
        this.isConstructorInject = isConstructorInject;
        this.packageScanDir = packageScanDir;
    }
}
