package ab.di;

public class ContextConfig {
    boolean isFieldsInject;
    boolean isConstructorInject;

    String packageScanDir;

    public String getPackageScanDir() {
        return packageScanDir;
    }

    public ContextConfig(boolean isFieldInject, boolean isConstructorInject, String packageScanDir) {
        this.isFieldsInject = isFieldInject;
        this.isConstructorInject = isConstructorInject;
        this.packageScanDir = packageScanDir;
    }
}
