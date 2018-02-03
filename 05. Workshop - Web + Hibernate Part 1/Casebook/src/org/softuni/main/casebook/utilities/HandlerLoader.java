package org.softuni.main.casebook.utilities;

import org.softuni.main.casebook.annotations.ApplicationRequestHandler;
import org.softuni.main.casebook.annotations.Get;
import org.softuni.main.casebook.annotations.Post;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public final class HandlerLoader {
    private String DYNAMIC_HANDLERS_FULL_PATH = System.getProperty("user.dir") + "\\src\\org\\softuni\\main\\casebook\\handlers\\dynamic";
    private String DYNAMIC_HANDLERS_CLASS_PATH = "org.softuni.main.casebook.handlers.dynamic.";

    private Map<String, Map<String, Method>> actionsMap;

    public HandlerLoader() {
        this.actionsMap = new HashMap<>();
        this.initializeSupportedMethods();
        this.loadMaps();
    }

    private void loadMaps() {
        File directory = new File(DYNAMIC_HANDLERS_FULL_PATH);

        List<Class<?>> classes = Arrays.asList(directory.listFiles())
                .stream()
                .map(x -> {
                    try {
                        String fileFillName = String.valueOf(x);
                        String className = fileFillName.substring(fileFillName.lastIndexOf("\\") + 1).replace(".java", "");
                        Class<?> foundClass = Class.forName(DYNAMIC_HANDLERS_CLASS_PATH + className);

                        if(foundClass.getAnnotation(ApplicationRequestHandler.class) != null){
                            return foundClass;
                        }

                        return null;

                    } catch (ClassNotFoundException e1) {
                        return null;
                    }
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());

        for (Class loadedClass : classes) {
            for (Method method : loadedClass.getDeclaredMethods()) {
                method.setAccessible(true);
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    if (annotation.annotationType().getSimpleName().equals("Get")) {
                        this.actionsMap.get("GET").put(((Get) annotation).route(), method);
                    } else if (annotation.annotationType().getSimpleName().equals("Post")) {
                        this.actionsMap.get("POST").put(((Post) annotation).route(), method);
                    }
                }
            }
        }
    }

    private void initializeSupportedMethods() {


        this.actionsMap.put("GET", new HashMap<>());
        this.actionsMap.put("POST", new HashMap<>());
    }

    public Map<String, Method> retrieveActionsMap(String method) {
        if(!this.actionsMap.containsKey(method)){
            return null;
        }
        return Collections.unmodifiableMap(this.actionsMap.get(method));
    }
}
