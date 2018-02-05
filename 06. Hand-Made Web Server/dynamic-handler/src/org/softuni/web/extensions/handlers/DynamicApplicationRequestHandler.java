package org.softuni.web.extensions.handlers;

import org.softuni.javache.http.*;
import org.softuni.javache.lib.handler.AbstractRequestHandler;
import org.softuni.web.extensions.handlers.lib.Application;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DynamicApplicationRequestHandler extends AbstractRequestHandler {

    private static final String APPLICATIONS_FOLDER = "apps";

    private Collection<Class<? extends Application>> applicationClasses;

    private final HttpSessionStorage sessionStorage;

    public DynamicApplicationRequestHandler(String serverPath) {
        super(serverPath);
        this.applicationClasses = new ArrayList<>();
        this.sessionStorage = new HttpSessionStorageImpl();
        this.applicationScan(this.serverPath + File.separator + APPLICATIONS_FOLDER);
    }

    @Override
    public byte[] handleRequest(String requestContent, String serverRootPath) {

        HttpContext httpContext = new HttpContextImpl(new HttpRequestImpl(requestContent), new HttpResponseImpl());

        for (Class<? extends Application> applicationClass : this.applicationClasses) {
            try {
                ClassLoader appsClassLoader = new URLClassLoader(new URL[]{
                        new File((super.serverPath + File.separator + APPLICATIONS_FOLDER + File.separator)
                                .replaceAll("%20", " "))
                                .toURI()
                                .toURL(),
                        new File((super.serverPath + File.separator + "handlers" + File.separator)
                                .replaceAll("%20", " "))
                                .toURI()
                                .toURL()
                }
                );

                Object inst = appsClassLoader
                        .loadClass(applicationClass.getName())
                        .getConstructor()
                        .newInstance();

                byte[] response = (byte[]) Arrays.stream(inst.getClass().getDeclaredMethods())
                        .filter(m->m.getName().equals("handleRequest"))
                        .findFirst()
                        .get()
                        .invoke(inst,httpContext);

                if(response != null){
                    return response;
                }

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | MalformedURLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void applicationScan(String path) {

        path = path.replaceAll("%20", " ");
        File folderCandidate = new File(path);

        for (File file : folderCandidate.listFiles()) {
            if (file.isDirectory()) {
                applicationScan(file.getPath());
            } else {
                if (file.getPath().endsWith(".class")) {
                    String fullPath = file.getPath();

                    String classPath = fullPath
                            .substring(0, fullPath.lastIndexOf('.'))
                            .replace((super.serverPath + File.separator + APPLICATIONS_FOLDER + File.separator).replaceAll("%20", " "), "");
                    String className = classPath.replace(File.separatorChar, '.');

                    ClassLoader appsClassLoader = null;

                    try {
                        appsClassLoader = new URLClassLoader(new URL[]{
                                new File((super.serverPath + File.separator + APPLICATIONS_FOLDER + File.separator)
                                        .replaceAll("%20", " "))
                                        .toURI()
                                        .toURL(),
                                new File((super.serverPath + File.separator + "handlers" + File.separator)
                                        .replaceAll("%20", " "))
                                        .toURI()
                                        .toURL()
                        }
                        );
                    } catch (MalformedURLException e) {
                        appsClassLoader = ClassLoader.getSystemClassLoader();
                        e.printStackTrace();
                    }

                    try {
                        Class clazz = appsClassLoader.loadClass(className);
                        if (Arrays.stream(clazz.getInterfaces()).anyMatch(i-> i.getName().contains("Application"))) {
                            System.out.println("class ok");
                            this.applicationClasses.add(clazz);
                        }
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
