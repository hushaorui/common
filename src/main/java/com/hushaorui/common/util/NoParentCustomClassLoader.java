package com.hushaorui.common.util;

import java.io.*;
import java.util.Collection;
import java.util.Objects;

/**
 * 非双亲委托机制的类加载器
 */
public class NoParentCustomClassLoader extends ClassLoader {
    public static final String CLASS_EXT = ".class";
    public static final String JAVA_EXT = ".java";
    private String classpath;
    // 所有需要此类加载器加载的类名
    private Collection<String> classNames;
    public NoParentCustomClassLoader(String classpath, Collection<String> classNames) {
        this.classpath = classpath;
        this.classNames = classNames;
    }
    public NoParentCustomClassLoader(Collection<String> classNames) {
        classpath = Objects.requireNonNull(this.getResource("")).getPath();
        this.classNames = classNames;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        byte[] b;
        try {
            if (classNames != null && classNames.contains(name)) {
                b = loadClassFile(name);
            } else {
                // 委派给父类加载器
                return getClass().getClassLoader().loadClass(name);
            }
        } catch (IOException e) {
            // 加载失败，委派给父类加载器
            return getClass().getClassLoader().loadClass(name);
        }

        // 检查该类是否被当前类加载器加载过（只检查当前类加载器，不会检查父类加载器） 
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }
        return this.defineClass(name, b, 0, b.length);
    }
    private byte[] loadClassFile(String name) throws IOException {
        String classFile = getClassFile(name);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream input = new FileInputStream(classFile);
        int count;
        byte[] temp = new byte[1024];
        while ((count = input.read(temp)) > -1) {
            out.write(temp, 0, count);
        }
        out.close();
        input.close();
        return out.toByteArray();
    }
    private String getClassFile(String name) {
        String pathName = name.replace(".", File.separator);
        if (classpath.endsWith("/") || classpath.endsWith("\\")) {
            return classpath + pathName + CLASS_EXT;
        }
        return classpath + File.separator + pathName + CLASS_EXT;
    }
}