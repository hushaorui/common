package com.hushaorui.common.util;

import com.hushaorui.common.AppHotFixClass;
import com.hushaorui.common.CacheClassDescription;
import com.hushaorui.common.CommonFilter;
import com.hushaorui.common.WebConstants;
import org.apache.commons.io.FileUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageUtil {

	/**
	 * 获取某包下（包括该包的所有子包）所有类
	 *
	 * @param packageName 包名
	 * @return 类的完整名称
	 */
	public static List<AppHotFixClass> getClassName(String packageName) {
		return getClassName(packageName, true, null);
	}

	/**
	 * 获取某包下所有类
	 *
	 * @param packageName  包名
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	public static List<AppHotFixClass> getClassName(String packageName, boolean childPackage, CommonFilter<String> filter) {
		List<AppHotFixClass> appHotFixClassList = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/");
		URL url = loader.getResource(packagePath);
		if (url != null) {
			String type = url.getProtocol();
			if (type.equals("file")) {
				appHotFixClassList = getClassNameByFile(new File(url.getPath()), null, childPackage, filter);
			} else if (type.equals("jar")) {
				appHotFixClassList = getClassNameByJar(url.getPath(), packageName, childPackage, filter);
			}
		} else {
			appHotFixClassList = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage, filter);
		}
		return appHotFixClassList;
	}

	/**
	 * 从项目文件获取某包下所有类
	 *
	 * @param dir     文件夹
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	public static List<AppHotFixClass> getClassNameByFile(File dir, boolean childPackage, CommonFilter<String> filter) {
		return getClassNameByFile(dir, null, childPackage, filter);
	}
	/**
	 * 从项目文件获取某包下所有类
	 *
	 * @param dir     文件夹
	 * @param classNameList    类名集合
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<AppHotFixClass> getClassNameByFile(File dir, List<AppHotFixClass> classNameList, boolean childPackage, CommonFilter<String> filter) {
		if (classNameList == null) {
			classNameList = new ArrayList<>();
		}
		File[] childFiles = dir.listFiles();
		if (childFiles != null) {
			for (File childFile : childFiles) {
				if (childFile.isDirectory()) {
					if (childPackage) {
						getClassNameByFile(childFile, classNameList, childPackage, filter);
					}
				} else {
					String childFilePath = childFile.getPath();
					if (childFilePath.endsWith(".class")) {
						childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
						childFilePath = childFilePath.replace("\\", ".");
						if (filter == null || filter.check(childFilePath)) {
							AppHotFixClass appHotFixClass = new AppHotFixClass();
							appHotFixClass.setAbsolutePath(childFile.getAbsolutePath());
							appHotFixClass.setClassName(childFilePath);
							appHotFixClass.setMd5Value(MD5Utils.encodingFile(childFile.getAbsolutePath()));
							classNameList.add(appHotFixClass);
						}
					}
				}
			}
		}

		return classNameList;
	}

	/**
	 * 从jar获取某包下所有类
	 *
	 * @param jarPath      jar文件路径
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<AppHotFixClass> getClassNameByJar(String jarPath, String packageName, boolean childPackage, CommonFilter<String> filter) {
		List<AppHotFixClass> appHotFixClassList = new ArrayList<>();
		String[] jarInfo = jarPath.split("!");
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath = jarInfo[1].substring(1);
		String prefix = packageName + ".";
		try {
			JarFile jarFile = new JarFile(jarFilePath);
			Enumeration<JarEntry> entryEnum = jarFile.entries();
			while (entryEnum.hasMoreElements()) {
				JarEntry jarEntry = entryEnum.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					if (childPackage) {
						if (entryName.startsWith(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							// 这行是因为spring和maven打出来的jar包外层多嵌套了一层
							entryName = entryName.replaceAll("BOOT-INF\\.classes\\.", "");
							// 包括子包内
							if (! entryName.startsWith(prefix)) {
								continue;
							}
							if (filter != null && ! filter.check(entryName)) {
								continue;
							}
							AppHotFixClass appHotFixClass = new AppHotFixClass();
							appHotFixClass.setAbsolutePath(jarFilePath);
							appHotFixClass.setClassName(entryName);
							appHotFixClass.setMd5Value(WebConstants.DEFAULT);
							appHotFixClassList.add(appHotFixClass);
						}
					} else {
						// 不包括子包
						/*String startStringWithDot = AppStringUtils.getStartStringWithDot(entryName);
						if (! prefix.equals(startStringWithDot)) {
							continue;
						}*/
						int index = entryName.lastIndexOf("/");
						String myPackagePath;
						if (index != -1) {
							myPackagePath = entryName.substring(0, index);
						} else {
							myPackagePath = entryName;
						}
						if (myPackagePath.equals(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							// 这行是因为spring和maven打出来的jar包外层多嵌套了一层
							entryName = entryName.replaceAll("BOOT-INF\\.classes\\.", "");
							AppHotFixClass appHotFixClass = new AppHotFixClass();
							appHotFixClass.setAbsolutePath(jarFilePath);
							appHotFixClass.setClassName(entryName);
							appHotFixClass.setMd5Value(WebConstants.DEFAULT);
							appHotFixClassList.add(appHotFixClass);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appHotFixClassList;
	}

	/**
	 * 从所有jar中搜索该包，并获取该包下所有类
	 *
	 * @param urls         URL集合
	 * @param packagePath  包路径
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<AppHotFixClass> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage, CommonFilter<String> filter) {
		List<AppHotFixClass> appHotFixClassList = new ArrayList<>();
		if (urls != null) {
			for (URL url : urls) {
				String urlPath = url.getPath();
				// 不必搜索classes文件夹
				if (urlPath.endsWith("classes/")) {
					continue;
				}
				String jarPath = urlPath + "!/" + packagePath;
				appHotFixClassList.addAll(getClassNameByJar(jarPath, packagePath.replace("/", "."), childPackage, filter));
			}
		}
		return appHotFixClassList;
	}

	/** 获取字段的泛型(前提得保证字段有泛型) */
	public static Class<?> getFieldGenericType(Field field, int index) {
		Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			try {
				// Map的第一个参数类型
				return Class.forName(parameterizedType.getActualTypeArguments()[index].getTypeName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/** 获取类的泛型 */
	public static <T> Class<T> getGenericType(Class<T> clazz, int index) {
        try {
            Type[] genericInterfaces = clazz.getGenericInterfaces();
			Type firstType;
            if (genericInterfaces.length == 0) {
				firstType = clazz.getGenericSuperclass();
			} else {
				firstType = genericInterfaces[0];
			}
            if (firstType instanceof Class) {
				firstType = ((Class) firstType).getGenericInterfaces()[0];
			}
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) firstType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (index >= actualTypeArguments.length) {
                index = actualTypeArguments.length - 1;
            }
            Type type = actualTypeArguments[index];
            return (Class<T>) Class.forName(type.getTypeName());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	/**
	 * 通过反射获取对象中某个字段的值
	 * @param object 对象
	 * @param fieldName 字段名称
	 * @param fieldValue 字段的值
	 */
	public static void setFieldValue(Object object, String fieldName, Object fieldValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Class<?> clazz = object.getClass();
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, fieldValue);
		} catch (Exception ignore) {
			String methodName;
			try {
				methodName = getSetMethodNameByFieldName(clazz.getDeclaredField(fieldName));
			} catch (NoSuchFieldException ignore2) {
				return;
			}
			Method method = clazz.getMethod(methodName);
			method.invoke(object);
		}
	}

	/**
	 * 通过反射获取对象中某个字段的值
	 * @param object 对象
	 * @param fieldName 字段名称
	 * @param <T> 字段类型
	 * @return 字段的值
	 */
    public static <T> T getFieldValue(Object object, String fieldName) {
		Class<?> clazz = object.getClass();
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception ignore) {
			String methodName;
			try {
				methodName = getGetMethodNameByFieldName(clazz.getDeclaredField(fieldName));
			} catch (NoSuchFieldException ignore2) {
				return null;
			}
			try {
				Method method = clazz.getMethod(methodName);
				return (T) method.invoke(object);
			} catch (Exception ignore2) {
				return null;
			}
		}
	}

	/**
	 * 查找某个目录下的所有符合条件的文件
	 * @param dir 所要查找的目录
	 * @param fileList 存放找到的文件的集合
	 * @param filter 文件过滤器
	 */
	public static void findAllFiles(File dir, Collection<File> fileList, CommonFilter<File> filter) {
		if (! dir.isDirectory()) {
			return;
		}
		// 这里可以进行过滤文件，文件夹不能过滤掉
		File[] files;
		if (filter == null) {
			files = dir.listFiles();
		} else {
			files = dir.listFiles(file -> filter.check(file) || file.isDirectory());
		}
		if (files == null) {
			// 没有符合条件的文件
			return;
		}
		for (File fileOrDir : files) {
			if (fileOrDir.isDirectory()) {
				// 递归
				findAllFiles(fileOrDir, fileList, filter);
			} else if (filter == null || filter.check(fileOrDir)) {
				fileList.add(fileOrDir);
			}
		}
	}

    /**
     * 从类路径下获取文件的输入流(jar包内的也可获取到)
     * @param path 文件相对于类路径的绝对路径
     * @return 输入流
     */
    public static InputStream getInputStreamFromClasspath(String path) {
        return PackageUtil.class.getResourceAsStream(path);
    }
	/**
	 * 从类路径下获取文件的内容(jar包内的也可获取到)
	 * @param path 文件相对于类路径的绝对路径
	 * @return 文件内容
	 * @throws IOException 文件读取异常
	 */
	public static String getFileContentFromClasspath(String path) throws IOException {
		StringBuilder builder = new StringBuilder();
		InputStream inputStream = getInputStreamFromClasspath(path);
		if (inputStream == null) {
		    return "";
        }
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			builder.append(line);
			if (bufferedReader.ready()) {
				builder.append(System.lineSeparator());
			}
		}
		return builder.toString();
	}

	/**
	 * 获取字段对应的set方法方法名
	 * @param field 字段
	 * @return set方法名
	 */
	public static String getSetMethodNameByFieldName(Field field) {
		String fieldName = field.getName();
		String setMethodName;
		if (fieldName.length() == 1) {
			setMethodName = "set" + fieldName.toUpperCase();
		} else {
			setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		}
		return setMethodName;
	}

	/**
	 * 获取字段对应的get方法方法名
	 * @param field 字段
	 * @return get方法名
	 */
	public static String getGetMethodNameByFieldName(Field field) {
		String fieldName = field.getName();
		String getMethodName;
		if (boolean.class.equals(field.getType())) {
			if (fieldName.startsWith("is")) {
				// 字段命名一般不要以is开头，但是既然命名了，就不能让它出错
				getMethodName = fieldName;
			} else if (fieldName.length() == 1) {
				getMethodName = "is" + fieldName.toUpperCase();
			} else {
				getMethodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			}
		} else if (fieldName.length() == 1) {
			getMethodName = "get" + fieldName.toUpperCase();
		} else {
			getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		}
		return getMethodName;
	}

	/**
	 * 类的描述对象
	 */
	private static Map<Class<?>, CacheClassDescription> classDescriptionMap = new HashMap<>();


	/**
	 * 获取文件的内容
	 * @param filePath 文件路径
	 * @return 文件内容，如果文件不存在返回null
	 */
	public static String getFileContent(String filePath) {
		try {
			File file = new File(filePath);
			if (! file.exists() || ! file.isFile()) {
				return null;
			}
			return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (Exception ignore) {
			return null;
		}
	}

	/** 创建文件并写入内容 */
	public static void createFile(String filePath, String content) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				if (! file.isFile()) {
					file.delete();
					file.createNewFile();
				}
			} else {
				file.createNewFile();
			}
			if (content != null) {
				FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
			}
		} catch (Exception ignore) {}
	}

	/** 删除文件 */
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		file.delete();
	}
}