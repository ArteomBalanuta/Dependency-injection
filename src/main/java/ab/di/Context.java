package ab.di;

import ab.annotation.AutoWired;
import ab.annotation.BeanFactory;
import ab.annotation.DependencyInjectionEntryPoint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

//https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection

//TODO use container to store created beans
public class Context {
    private Container container;
    private Class<?> factory;
    private Object factoryInstance;
    private Object entryPoint;

    public Object getFactoryInstance() {
        return factoryInstance;
    }

    public Object getEntryPoint() {
        return entryPoint;
    }

    public Context(String projectPath, Class<?> entryPointClass) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.container = new Container();

        Class[] classes = getClasses(projectPath);

        setBeanFactory(classes);
        injectIntoConstructors(classes, entryPointClass);
        injectIntoFields(classes, entryPointClass);
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

    private void injectIntoConstructors(Class[] classes, Class<?> entryPointClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        boolean isPresent = false;
        for (Class<?> clazz : classes) {
            if(entryPointClass != null){
                clazz = entryPointClass;
            }

            if (clazz.isAnnotationPresent(DependencyInjectionEntryPoint.class)) {
                Constructor c = clazz.getConstructors()[0];
                if (!c.isAnnotationPresent(AutoWired.class)) {
                    entryPoint = c.newInstance();
                    isPresent = true;
                    System.out.println("Instance of " + clazz.getName() + " created - constructor's parameters ARE NOT autowired");
                    return;
                }
                List<Object> beans = new ArrayList<>();
                for (Class<?> typeClass : c.getParameterTypes()) {
                    beans.add(getBeanFromBeanFactory(typeClass));
                }
                entryPoint = c.newInstance(beans.toArray());
                System.out.println("Instance of " + clazz.getName() + " created - constructor's parameters ARE autowired");
                return;
            }
        }

        if (!isPresent) {
            throw new RuntimeException("Can not find class with entry point annotation @DependencyInjectionEntryPoint");
        }
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
    private void injectIntoFields(Class[] classes) throws IllegalAccessException, InvocationTargetException {
        for (Class<?> clazz : classes) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(AutoWired.class)) {
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
