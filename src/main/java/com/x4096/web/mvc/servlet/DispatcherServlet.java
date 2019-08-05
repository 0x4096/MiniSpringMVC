package com.x4096.web.mvc.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x4096.web.mvc.annotation.Autowired;
import com.x4096.web.mvc.annotation.Controller;
import com.x4096.web.mvc.annotation.RequestMappering;
import com.x4096.web.mvc.annotation.Service;
import com.x4096.web.mvc.utils.ClassUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 0x4096.peng@gmail.com
 * @Project: Mini-MVC
 * @DateTime: 2019-08-04 11:48
 * @Description: 使用 DispatcherServlet 获取所有 http 请求,然后在做分发,具体请求做具体事情
 */
public class DispatcherServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(DispatcherServlet.class);


    /**
     * 含有 @Controller @Service 注解的类
     */
    private static List<Class<?>> beanClassList = new ArrayList<>();

    /**
     * URL 对于具体方法处理
     */
    private static Map<String, Method> urlMappingMap = new HashMap<>();

    /**
     * 1. 初始化对象 用于调用相应类对方法
     * 2. 实现接口
     */
    private static Map<String, Object> instanceMap = new HashMap<>();


    public DispatcherServlet() {
    }

    /**
     * 初始化核心操作
     * 1. 扫描包,获取定义的注解 @Controller @Service
     * 2. 注入容器
     * 3. 进行依赖注入
     * 4. 构建 HandlerMapping 映射关系,将 URI 映射一个方法
     * 5. 用户请求,根据 URI 找到映射关系,执行相应的方法处理
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {

        /**
         * 1. 扫描包,获取定义的注解 @Controller @Service
         */
        List<Class<?>> classList = initScanPackage("com.x4096.web");

        /**
         * 2. 注入容器
         */
        initIoc(classList);

        /**
         * 3. 进行依赖注入
         */
        initDi();

        /**
         * 4. 构建 HandlerMapping 映射关系,将 URI 映射一个方法
         */
        initHandleMapping();
    }


    /**
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }


    /**
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        Method method = urlMappingMap.get(uri.replace(contextPath, ""));

        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter printWriter = resp.getWriter();

        if (method == null) {
            printWriter.println("404");
            printWriter.flush();
            printWriter.close();
            return;
        }

        String name = toLowerCaseFristChar(method.getDeclaringClass().getSimpleName());

        Object obj = instanceMap.get(name);


        Object resultMethod = null;
        try {
            resultMethod = method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        /* 这里有一个问题,如果返回结果已经是字符串了,则会在字符串上加双引号 */
        ObjectMapper objectMapper = new ObjectMapper();
        printWriter.println(objectMapper.writeValueAsString(resultMethod));
        printWriter.flush();
        printWriter.close();
    }


    /**
     * 扫描当前包下所有类
     *
     * @param pageName
     */
    private List<Class<?>> initScanPackage(String pageName) {
        return ClassUtils.getAllClassByPackageName(pageName);
    }


    /**
     * 初始化 IOC 容器
     * 存放 @Controller @Service 的对象
     *
     * @param classList
     */
    private void initIoc(List<Class<?>> classList) {
        classList.forEach(clazz -> {
            if (ClassUtils.isAnnotation(clazz, Controller.class)) {
                beanClassList.add(clazz);
                try {
                    instanceMap.put(toLowerCaseFristChar(clazz.getSimpleName()), clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (ClassUtils.isAnnotation(clazz, Service.class)) {
                beanClassList.add(clazz);
                // 需要区分 service 因为 service 有很多实现,需要注意自定义的名称
                Service service = clazz.getAnnotation(Service.class);
                String beanName = service.value();
                if ("".equals(beanName)) {
                    beanName = clazz.getSimpleName();
                }
                try {
                    instanceMap.put(toLowerCaseFristChar(beanName), clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    /**
     * 注入对象
     * 1. 从含有 @Controller @Service 类中获取所有的 @Autowired 的属性
     * 2. 注入
     * <p>
     * bean规则:
     * 1. 实现同一个接口 beanName 必须不一样,默认为实现接口的类名切首字母小写
     * 2. 同一接口多个实现,但是 @Autowired 没有指定具体实现,异常情况
     * 2. @Autowired 不存在的实现,异常情况
     */
    private void initDi() {
        instanceMap.forEach((k, v) -> {
            Field[] fields = v.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    /* 获取接口对所有实现 */
                    List<Class<?>> insclassList = ClassUtils.getAllClassByInterface(field.getType());
                    /* 注入接口没有具体实现 */
                    if (insclassList == null || insclassList.size() == 0) {
                        try {
                            throw new IllegalAccessException("当前接口" + field.getType() + "没有具体实现！");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String beanName = autowired.value();

                    if ("".equals(beanName)) {
                        if (insclassList.size() > 1) {
                            throw new IllegalArgumentException("当前接口存在多个实现,请指定注入具体实现");
                        } else {
                            beanName = toLowerCaseFristChar(insclassList.get(0).getSimpleName());
                        }
                    }

                    Object bean = instanceMap.get(beanName);

                    if (bean == null) {
                        throw new IllegalArgumentException("注入 bean 不存在");
                    }

                    field.setAccessible(true);
                    try {
                        field.set(v, bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        });


    }


    /**
     *
     */
    private void initHandleMapping() {
        beanClassList.forEach(clazz -> {
            /* 只有在 @Controller 的注解才会有 @RequestMapping  */
            if (ClassUtils.isAnnotation(clazz, Controller.class)) {
                String clazzUri = null;

                /* 获取类上 @RequestMapping 中的路径 */
                if (ClassUtils.isAnnotation(clazz, RequestMappering.class)) {
                    RequestMappering clazzRequestMappering = clazz.getAnnotation(RequestMappering.class);
                    clazzUri = clazzRequestMappering.value();
                }


                /* 遍历方法获取方法上的 @RequestMapping 中的路径 */
                List<Method> methodList = ClassUtils.getAnnotationMethod(clazz, RequestMappering.class);

                for (Method method : methodList) {
                    RequestMappering requestMappering = method.getAnnotation(RequestMappering.class);
                    String methodUri = requestMappering.value();
                    if (urlMappingMap.containsKey(clazzUri == null ? methodUri : clazzUri + methodUri)) {
                        throw new IllegalArgumentException("存在相同 URI 映射");
                    } else {
                        String urlMapping = clazzUri == null ? methodUri : clazzUri + methodUri.replaceAll("/+", "/");
                        urlMappingMap.put(urlMapping, method);
                        log.info("urlMapping: " + urlMapping);
                    }
                }
            }
        });


    }


    /**
     * 首字符转小写
     *
     * @param string
     * @return
     */
    private static String toLowerCaseFristChar(String string) {
        char[] charArray = string.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

}
