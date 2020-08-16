package ab.di;

import ab.annotation.AutoWired;
import ab.annotation.BeanFactory;
import ab.annotation.DependencyInjectionEntryPoint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

//https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection

//TODO use container to store created beans
public class Context {
    private ContextConfig contextConfig;
    private BeanContainer beanContainer;
    private Class<?> factory;
    private Object factoryInstance;
    private Object entryPoint;

    public Object getFactoryInstance() {
        return factoryInstance;
    }

    public Object getEntryPoint() {
        return entryPoint;
    }

    public Context(ContextConfig contextConfig) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.beanContainer = new BeanContainer();

        Class[] classes = getClasses(contextConfig.getPackageScanDir());
        setBeanFactory(classes);

        List<Class> diAnnotated = Arrays.stream(classes)
                .filter(c -> c.isAnnotationPresent(DependencyInjectionEntryPoint.class))
                .collect(Collectors.toList());

        if (contextConfig.isConstructorInject) {
            injectIntoConstructors(diAnnotated.toArray(new Class[0]));
        }
        if (contextConfig.isFieldsInject) {
            injectIntoFields(diAnnotated.toArray(new Class[0]));
        }

    }


    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    //TODO: persist EPs
    private void injectIntoConstructors(Class[] classes) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for (Class<?> clazz : classes) {
            Constructor c = clazz.getConstructors()[0];
            if (!c.isAnnotationPresent(AutoWired.class)) {
                entryPoint = c.newInstance();
                System.out.println("Instance of " + clazz.getName() + " created - constructor's parameters ARE NOT autowired");
                return;
            }
            List<Object> constructorBeans = new ArrayList<>();
            for (Class<?> typeClass : c.getParameterTypes()) {
                Object bean = getBeanFromBeanFactory(typeClass);
                constructorBeans.add(bean);
                beanContainer.add(bean.getClass().getName(), bean);
            }

            entryPoint = c.newInstance(constructorBeans.toArray());
            System.out.println("Instance of " + clazz.getName() + " created - constructor's parameters ARE autowired");
            return;
        }

        throw new RuntimeException("Can not find class with entry point annotation @DependencyInjectionEntryPoint");
    }

    private void setBeanFactory(Class[] classes) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        boolean isPresent = false;
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(BeanFactory.class)) {
                factory = clazz;
                factoryInstance = clazz.getConstructors()[0].newInstance();
                isPresent = true;
                System.out.println("Found BeanFactory");
            }
        }

        if (!isPresent) {
            throw new RuntimeException("Can not find BeanFactoryClass");
        }
    }

    //TODO: implement constructor injection instead of field injection
    //TODO: extend autowiring - to use outside entry point class
    private void injectIntoFields(Class[] classes) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Class<?> clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(AutoWired.class)) {
                    Constructor c = clazz.getConstructors()[0];
                    entryPoint = c.newInstance();
                    Class<?> beanType = f.getType();

                    Object bean = getBeanFromBeanFactory(beanType);
                    f.set(entryPoint, bean);
                }
            }
        }
    }

    private Object getBeanFromBeanFactory(Class<?> beanType) throws InvocationTargetException, IllegalAccessException {
        Method[] beanFactories = factory.getMethods();
        Object bean = null;
        for (Method m : beanFactories) {
            if (m.getReturnType() == beanType) {
                return m.invoke(factoryInstance);
            }
        }
        System.out.println("No beanFactory found for bean: " + beanType);
        return bean;
    }

}
