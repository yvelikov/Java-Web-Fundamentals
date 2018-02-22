package org.softuni.summer.core;

import org.softuni.broccolina.solet.*;
import org.softuni.javache.http.HttpStatus;
import org.softuni.summer.api.*;
import org.softuni.summer.util.PathFormatter;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebSolet(route = "/*", loadOnStartUp = true)
public class DispatcherSolet extends BaseHttpSolet {

    private Map<String, Map<String, ControllerActionPair>> mappingTables;

    public DispatcherSolet(SoletConfig soletConfig) {
        super(soletConfig);
        this.initMappingTables();
    }

    private void initMappingTables() {
        this.mappingTables = new HashMap<>();
        this.mappingTables.putIfAbsent("GET", new HashMap<>());
        this.mappingTables.putIfAbsent("POST", new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private void loadClass(File classFile) {
        if (classFile.isDirectory()) {
            for (File childClassFile : classFile.listFiles()) {
                this.loadClass(childClassFile);
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


                if (Arrays.stream(clazzFile
                        .getAnnotations())
                        .anyMatch(x -> x
                                .annotationType()
                                .getSimpleName()
                                .equals(Controller.class.getSimpleName()))) {

                    Object controllerInstance = clazzFile.getDeclaredConstructor().newInstance();

                    for (Method method : clazzFile.getDeclaredMethods()) {
                        for (Annotation annotation : method.getAnnotations()) {

                            String annotationName = annotation.annotationType().getSimpleName();
                            String annotationMapping = annotationName.replace("Mapping", "").toUpperCase();

                            if (Pattern.compile("^(Get|Post)Mapping$").matcher(annotationName).find()
                                    && this.mappingTables.containsKey(annotationMapping)) {

                                String annotationRoute = annotation
                                        .getClass()
                                        .getDeclaredMethod("route")
                                        .invoke(annotation)
                                        .toString();

                                this.mappingTables
                                        .get(annotationMapping)
                                        .putIfAbsent(annotationRoute,
                                                new ControllerActionPair(controllerInstance, method));
                            }
                        }
                    }
                }

            } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadControllers(String classFolderPath) {
        File classDir = new File(classFolderPath);
        if (!classDir.exists()) {
            return;
        }

        this.loadClass(classDir);
    }

    private boolean isTemplate(String result) {
        return result.startsWith("template:");
    }

    private String loadTemplate(String templateName) throws IOException {
        String templateFolder = this.getSoletConfig().getAttribute("application-path")
                + File.separator
                + "resources"
                + File.separator
                + "templates"
                + File.separator;

        return String.join("", Files.readAllLines(Paths.get(templateFolder + templateName + ".html")));
    }

    private ControllerActionPair getControllerActionPair(String requestMethod, String requestUrl) {
        PathFormatter pathFormatter = new PathFormatter();

        for (Map.Entry<String, ControllerActionPair> capEntry : mappingTables.get(requestMethod).entrySet()) {
            String pathPattern = pathFormatter.formatPattern(capEntry.getKey());
            Matcher matcher = Pattern.compile(pathPattern).matcher(requestUrl);

            System.out.println("path pattern: " + pathPattern);
            System.out.println("request url: " + requestUrl);

            if (matcher.find()) {
                Parameter[] actionParams = capEntry.getValue().getAction().getParameters();
                for (int i = 0; i < matcher.groupCount() && i < actionParams.length; i++) {
                    String parameterValue = matcher.group(i + 1);

                    if(actionParams[i].getParameterizedType().equals(String.class)){
                        capEntry.getValue().addParameter(parameterValue);
                    } else if(actionParams[i].getParameterizedType().equals(int.class)){
                        capEntry.getValue().addParameter(Integer.valueOf(parameterValue));
                        System.out.println("parameter value: " + parameterValue);
                    }
                }
                return capEntry.getValue();
            }
        }
        return null;
    }

    @Override
    public void init() {
        super.init();
        String classFolderPath = this.getSoletConfig().getAttribute("application-path") + File.separator + "classes" + File.separator;
        this.loadControllers(classFolderPath);
    }

    @Override
    public void service(HttpSoletRequest request, HttpSoletResponse response) {

        String requestMethod = request.getMethod();
        String requestUrl = request.getRequestUrl();
        ControllerActionPair controllerActionPair = this.getControllerActionPair(requestMethod, requestUrl);

        if (controllerActionPair != null) {
            String result = null;
            try {

                try{
                    result = controllerActionPair
                            .getAction()
                            .invoke(controllerActionPair.getController(), controllerActionPair.getParameters().toArray())
                            .toString();
                }catch (Exception e){
                    e.printStackTrace();
                }

                result = controllerActionPair
                        .getAction()
                        .invoke(controllerActionPair.getController(), controllerActionPair.getParameters().toArray())
                        .toString();

                response.setStatusCode(HttpStatus.OK);

                if (this.isTemplate(result)) {
                    response.addHeader("Content-Type", "text/html");
                    try {
                        result = this.loadTemplate(result.split(":")[1]);
                    } catch (IOException e) {
                        response.setStatusCode(HttpStatus.NOT_FOUND);
                        result = "<h1>ERROR 404: Template not found</h1>";
                    }
                } else {
                    response.addHeader("Content-Type", "text/plain");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                response.addHeader("Content-Type", "text/plain");
                response.setContent(e.getMessage().getBytes());
            } finally {
                response.setContent(result.getBytes());
            }
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.addHeader("Content-Type", "text/plain");

            for (Map.Entry<String, ControllerActionPair> entry : this.mappingTables.get("GET").entrySet()) {
                System.out.println(new PathFormatter().formatPattern(entry.getKey()));
            }

            response.setContent("No such functionality found...".getBytes());
        }
    }
}



