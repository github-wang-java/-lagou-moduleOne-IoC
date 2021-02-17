package com.lagou.edu.factory;

import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyComponent;
import com.lagou.edu.annotation.MyTransactional;
import com.lagou.edu.servlet.TransferServlet;
import com.lagou.edu.utils.TranscationManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

public class BeanFactory {

    private static TranscationManager transcationManager = new TranscationManager();

    private static BeanFactory beanFactory = new BeanFactory();

    private BeanFactory(){}

    public static BeanFactory getInstance(){
        return beanFactory;
    }

    private static Map<String,Object> map = new HashMap<>();

    /**
     * 任务一：获取所有对象
     */
    static {
        try {
            ClassLoader classLoader = BeanFactory.class.getClassLoader();

            //解析处理被MyComponent注解标记的对象
            List<Class> classes = queryClasses(classLoader, MyComponent.class);
            classes.forEach(clazz->{
                Object o = parseAnnotation(clazz);
                if (clazz.isAnnotationPresent(MyTransactional.class)){
                    o = parseTranscationAnnotation(clazz, o);
                }

                map.put(clazz.getName(),o);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 任务二：创建外部获取对象的方法
     */
    public Object getBean(String key){
        return map.get(key);
    }

    private static List<Class> queryClasses(ClassLoader classLoader, Class annontation) throws IOException {
        Enumeration<URL> resources = classLoader.getResources("");
        List classList = new ArrayList();
        while (resources.hasMoreElements()){
            URL url = resources.nextElement();
            loadClassByPath(null, url.getPath(), classList, classLoader, annontation);
        }
        return classList;
    }

    /**
     * 获取项目中所有被该注解标记的类
     * @param rootPath
     * @param path
     * @param classList
     * @param classLoader
     */
    private static void loadClassByPath(String rootPath, String path, List classList,ClassLoader classLoader, Class annontation) {
        File file = new File(path);
        if (rootPath==null){
            rootPath = file.getPath();
        }
        //判断是否是class文件
        if (file.isFile() && file.getName().matches("^.*\\.class$")){
            try {
                String classPath = file.getPath().substring(rootPath.length()+1,file.getPath().length()-6).replace("\\",".");
                Class<?> aClass = classLoader.loadClass(classPath);
                if (!aClass.isAnnotation() && aClass.isAnnotationPresent(annontation)){
                    classList.add(aClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            File[] files = file.listFiles();
            if (files == null){
                return;
            }else {
                for (File f: files) {
                    loadClassByPath(rootPath, f.getPath(), classList, classLoader, annontation);
                }
            }
        }
    }

    /**
     * 解析注解，将注解标记的类实例化
     * @param clazz
     */
    private static Object parseAnnotation(Class clazz) {
        try {
            Object o = null;
            if (map.get(clazz.getName())==null){
                o = clazz.newInstance();
                //该类中的属性，进行处理
                parseFields(clazz.getDeclaredFields(), clazz, o);
            }
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * 解析自動注入注解
     * @param declaredFields
     */
    private static void parseFields(Field[] declaredFields,Class clazz, Object o) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, IntrospectionException, NoSuchFieldException {
        for (Field field: declaredFields) {
            if (field.isAnnotationPresent(MyAutowired.class)){
                String autoPath = field.getAnnotation(MyAutowired.class).value();
                Object autoObj = null;
                if (map.get(autoPath)!=null){
                    autoObj = map.get(autoPath);
                }else{
                    autoObj = parseAnnotation(Class.forName(autoPath));
                    map.put(autoPath, autoObj);
                }
                Field declaredField = clazz.getDeclaredField(field.getName());
                declaredField.setAccessible(true);
                declaredField.set(o, autoObj);
            }
        }
    }

    /**
     * 解析事务注解
     * @param clazz
     */
    private static Object parseTranscationAnnotation(Class clazz, Object o) {
        if (map.get(TranscationManager.class.getName())==null){
            transcationManager = (TranscationManager) parseAnnotation(TranscationManager.class);
            map.put(TranscationManager.class.getName(),transcationManager);
        }else {
            transcationManager = (TranscationManager) map.get(TranscationManager.class.getName());
        }
        Object proxyObj = Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = null;
                try{
                    transcationManager.beginTranscation();
                    method.invoke(o, args);
                    transcationManager.commitTranscation();
                }catch (Exception e){
                    e.printStackTrace();
                    transcationManager.rollbackTranscation();
                    throw e;
                }
                return result;
            }
        });
        return proxyObj;
    }

    /**
     * 解析xml配置
     * @param path
     */
    private static void parseXmlConfig(String path, ClassLoader classLoader) throws DocumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, IntrospectionException, InvocationTargetException {
        InputStream inputStream = classLoader.getResourceAsStream(path);
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> beanList = rootElement.selectNodes("//bean");
        for (Element element: beanList) {
            String id = element.attributeValue("id");
            String classpath = element.attributeValue("class");
            Class<?> aClass = Class.forName(classpath);
            Object o = aClass.newInstance();
            map.put(id,o);
        }

        List<Element> refList = rootElement.selectNodes("//property");
        for (Element element : refList) {
            String name = element.attributeValue("name");
            String ref = element.attributeValue("ref");
            Element parent = element.getParent();
            String parentId = parent.attributeValue("id");

            Object parentObj = map.get(parentId);
            Class<?> aClass = Class.forName(parent.attributeValue("class"));
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name,aClass);
            Method writeMethod = propertyDescriptor.getWriteMethod();
            writeMethod.invoke(parentObj,map.get(ref));

            map.put(parentId, parentObj);
        }
    }

}
