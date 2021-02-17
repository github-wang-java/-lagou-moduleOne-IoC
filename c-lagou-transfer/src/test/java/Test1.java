import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Test1 {

    private Map<String, Object> objPool = new HashMap<>();

    @Test
    public void test() throws IOException {
        ClassLoader classLoader = Test1.class.getClassLoader();
        Enumeration<URL> resources = classLoader.getResources("");
        List<Class> classList = new ArrayList();
        while (resources.hasMoreElements()){
            URL url = resources.nextElement();
            loadClassByPath(null, url.getPath(), classList, classLoader);
        }

        classList.forEach(clazz->{
//            clazz.
        });


        System.out.println("the end");
    }

    private void loadClassByPath(String rootPath, String path, List classList,ClassLoader classLoader) {
        File file = new File(path);
        if (rootPath==null){
            rootPath = file.getPath();
        }
        //判断是否是class文件
        if (file.isFile() && file.getName().matches("^.*\\.class$")){
            try {
                String classPath = file.getPath().substring(rootPath.length()+1,file.getPath().length()-6).replace("\\",".");
                Class aClass = classLoader.loadClass(classPath);
//                    objPool.put(aClass.getName(),aClass.newInstance());
//                    Field[] declaredFields = aClass.getDeclaredFields();
//                    for (Field field: declaredFields) {
//
//                    }
//                    classList.add(aClass);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            File[] files = file.listFiles();
            if (files == null){
                return;
            }else {
                for (File f: files) {
                    loadClassByPath(rootPath, f.getPath(), classList, classLoader);
                }
            }
        }
    }

}
