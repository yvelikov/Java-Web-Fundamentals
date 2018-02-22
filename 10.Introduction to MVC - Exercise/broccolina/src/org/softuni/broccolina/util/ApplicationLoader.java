package org.softuni.broccolina.util;

import org.softuni.broccolina.solet.HttpSolet;
import org.softuni.broccolina.solet.SoletConfig;
import org.softuni.broccolina.solet.SoletConfigImpl;
import org.softuni.broccolina.solet.WebSolet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApplicationLoader {

    private static final String ROOT_APPLICATION_FOLDER_NAME = "ROOT";
    private final String APPLICATION_FOLDER_PATH;

    private JarUnzipUtil jarUnzipUtil;

    private Map<String, Object> loadedSoletsByApplicationName;
    private Object soletConfig;

    public ApplicationLoader(String serverRootPath) {
        this.APPLICATION_FOLDER_PATH = serverRootPath + "apps";
        this.jarUnzipUtil = new JarUnzipUtil();
    }

    private boolean isJarFile(File file) {
        return file.getName().endsWith(".jar");
    }

    private void unzipJar(File jarFile) throws IOException {
        this.jarUnzipUtil.unzipJar(URLDecoder.decode(jarFile.getCanonicalPath(), "UTF-8"));
    }

    private Object getSoletConfig(Class soletClazz, String applicationPath) {

        SoletConfig soletConfig = new SoletConfigImpl();

        soletConfig.setAttribute("application-path", applicationPath);

        Class[] requiredParameters = Arrays.stream(soletClazz
                .getDeclaredConstructors())
                .filter(x -> x.getParameterCount() == 1)
                .findFirst()
                .get()
                .getParameterTypes();

        Object proxySoletConfig = Proxy.newProxyInstance(soletClazz.getClassLoader(),
                new Class[]{requiredParameters[0]},
                ((proxy, method, args) -> {
                    return Arrays.stream(soletConfig
                            .getClass()
                            .getMethods())
                            .filter(x -> x
                                    .getName()
                                    .equals(method.getName()))
                            .findFirst()
                            .get()
                            .invoke(soletConfig, args);
                }));


        return proxySoletConfig;
    }

    @SuppressWarnings("unchecked")
    private void loadIfSolet(Class soletClazz, String applicationName) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class parentClass = soletClazz.getSuperclass();

        if (parentClass != null && Arrays.stream(soletClazz
                .getSuperclass()
                .getInterfaces())
                .anyMatch(x -> x
                        .getSimpleName()
                        .equals(HttpSolet.class.getSimpleName()))
                && !Modifier.isAbstract(soletClazz.getModifiers())) {

            Object soletConfigObject = this.getSoletConfig(soletClazz, this.APPLICATION_FOLDER_PATH + File.separator + applicationName);

            Class[] requiredParameters = Arrays.stream(soletClazz
                    .getDeclaredConstructors())
                    .filter(x -> x.getParameterCount() == 1)
                    .findFirst()
                    .get()
                    .getParameterTypes();

            Object soletObject = Arrays.stream(soletClazz
                    .getDeclaredConstructors())
                    .filter(x->x.getParameterCount() == 1)
                    .findFirst()
                    .get()
                    .newInstance(soletConfigObject);

            Object annotation = Arrays.stream(soletClazz.getDeclaredAnnotations())
                    .filter(x -> x
                            .annotationType()
                            .getSimpleName()
                            .equals(WebSolet.class.getSimpleName()))
                    .findFirst().orElse(null);

            if (annotation == null) return;

            if ((boolean) annotation.getClass().getMethod("loadOnStartUp").invoke(annotation)) {
                soletObject.getClass().getMethod("init").invoke(soletObject);
            }

            applicationName = applicationName.equals(ROOT_APPLICATION_FOLDER_NAME)
                    ? ""
                    : "/" + applicationName;

            this.loadedSoletsByApplicationName
                    .putIfAbsent(
                            applicationName + annotation.getClass().getMethod("route").invoke(annotation).toString(),
                            soletObject);
        }
    }

    private void loadClass(File classFile, String applicationName) {
        if (classFile.isDirectory()) {
            for (File childClassFile : classFile.listFiles()) {
                this.loadClass(childClassFile, applicationName);
            }
        } else {
            if (!classFile.getName().endsWith(".class")) {
                return;
            }

            String parentPath = classFile.getParent();

            try {
                URL[] urls = new URL[]{(new File(parentPath).toURI().toURL())};
                URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls, Thread.currentThread().getContextClassLoader());
                Thread.currentThread().setContextClassLoader(urlClassLoader);

                String className = classFile.getName().replace(".class", "").replace("/", ".");

                Class clazzFile = urlClassLoader.loadClass(className);
                this.loadIfSolet(clazzFile, applicationName);
            } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLibrary(JarFile library, String canonicalPath, String applicationName) {
        Enumeration<JarEntry> fileEntries = library.entries();

        try {
            URL[] urls = new URL[]{new URL("jar:file:" + canonicalPath + "!/")};
            URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls, Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(urlClassLoader);

            while (fileEntries.hasMoreElements()) {
                JarEntry currentFile = fileEntries.nextElement();

                if (currentFile.isDirectory() || !currentFile.getName().endsWith(".class")) {
                    continue;
                }

                String className = currentFile.getName()
                        .replace("classes/", "")
                        .replace(".class", "")
                        .replace("/", ".");

                Class soletClazz = urlClassLoader.loadClass(className);
                this.loadIfSolet(soletClazz, applicationName);
            }

        } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void loadClasses(String classFolderPath, String applicationName) {
        File classDir = new File(classFolderPath);
        if (!classDir.exists()) {
            return;
        }

        this.loadClass(classDir, applicationName);
    }

    private void loadLibraries(String libFolderPath, String applicationName) throws IOException {
        File libDirectory = new File(libFolderPath);

        if (libDirectory.exists() && libDirectory.isDirectory()) {
            for (File file : libDirectory.listFiles()) {
                if (!this.isJarFile(file)) {
                    continue;
                }

                JarFile library = new JarFile(file.getCanonicalPath());
                this.loadLibrary(library, file.getCanonicalPath(), applicationName);
            }
        }
    }

    public Map<String, Object> getSolets() {
        return Collections.unmodifiableMap(this.loadedSoletsByApplicationName);
    }

    public void loadApplications() {

        this.loadedSoletsByApplicationName = new HashMap<>();

        try {
            File appsDir = new File(APPLICATION_FOLDER_PATH);
            if (!appsDir.exists()) {
                return;
            }

            for (File file : appsDir.listFiles()) {
                if (this.isJarFile(file)) {
                    this.unzipJar(file);

                    String applicationName = file.getName().replace(".jar", "");

                    String libraryPath = URLDecoder.decode(file.getCanonicalPath().replace(".jar", ""), "UTF-8") + File.separator + "lib";
                    String classesPath = URLDecoder.decode(file.getCanonicalPath().replace(".jar", ""), "UTF-8") + File.separator + "classes";

                    this.loadLibraries(libraryPath, applicationName);
                    this.loadClasses(classesPath, applicationName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
