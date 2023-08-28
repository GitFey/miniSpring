package com.minis.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minis.test.HelloWorldBean;

/**
 * Servlet implementation class DispatcherServlet
 * DispatcherServlet 类是前端控制器，用于处理请求分发和处理。
 */
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String sContextConfigLocation;
    //用于储存需要扫描的package列表
    private List<String> packageNames = new ArrayList<>();
    //用于储存controller的名称与对象的映射关系
    private Map<String,Object> controllerObjs = new HashMap<>();
    //用于储存controller的名称列表
    private List<String> controllerNames = new ArrayList<>();
    //用于储存controller的名称与类的映射关系
    private Map<String,Class<?>> controllerClasses = new HashMap<>();
    //用于保存自定义的@RequestMapping名称(即url名称)的列表
    private List<String> urlMappingNames = new ArrayList<>();
    //用于保存url与对象的映射关系
    private Map<String,Object> mappingObjs = new HashMap<>();
    //用于保存url与方法的映射关系
    private Map<String,Method> mappingMethods = new HashMap<>();

    public DispatcherServlet() {
        super();
    }

    /**
     * Servlet 初始化方法
     * 初始化主要
     * 1.处理从外部传入的资源，将 XML 文件内容解析后,相应的包存入 packageNames 内。
     * 2.调用 Refresh() 函数创建 Bean
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // 获取配置文件(web.xml)中的上下文配置位置参数,即minisMVC-servlet.xml
        sContextConfigLocation = config.getInitParameter("contextConfigLocation");

        URL xmlPath = null;
        try {
            // 获取上下文资源的路径
            xmlPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //把 minisMVC-servlet.xml 里扫描出来的 package 名称存入packageNames 列表
        this.packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        //调用 Refresh() 函数创建 Bean
        Refresh();

    }

    /**
     * refresh() 方法分成两步：
     * 第一步初始化  controller
     * 第二步则是初始化  URL 映射。
     */
    protected void Refresh() {
        initController();
        initMapping();
    }

    /**
     *  initController() ，其主要功能是对扫描到的每一个类进行加载和实例化，
     *  与类的名字建立映射关系，分别存在 controllerClasses 和 controllerObjs 这两个 map 里，类名就是 key 的值。
     */
    protected void initController() {
        //扫描包，获取所有类名
        this.controllerNames = scanPackages(this.packageNames);

        for (String controllerName : this.controllerNames) {
            Object obj = null;
            Class<?> clz = null;

            try {
                //加载类
                clz = Class.forName(controllerName);
                //建立controller的名称与类的映射关系
                this.controllerClasses.put(controllerName,clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                // 创建Controller对象并保存
                obj = clz.newInstance();
                //实例化bean,建立controller的名称与对象的映射关系
                this.controllerObjs.put(controllerName, obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给定包的列表,遍历所有包,获取其中所有的类
     * @param packages
     * @return
     */
    private List<String> scanPackages(List<String> packages) {
        List<String> tempControllerNames = new ArrayList<>();
        for (String packageName : packages) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }

    /**
     * 给定包名称,获取其中所有的类
     * @param packageName
     * @return
     */
    private List<String> scanPackage(String packageName) {
        //暂时储存控制器名称
        List<String> tempControllerNames = new ArrayList<>();
        //将以.分隔的包名换成以/分隔的uri
        URL url  = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        //处理对应的文件目录
        for (File file : dir.listFiles()) {    //目录下的文件或者子目录
            //对子目录递归扫描
            if(file.isDirectory()){
                scanPackage(packageName + "." + file.getName());
            }else{
                //控制器名称
                String controllerName = packageName + "." + file.getName().replace(".class", "");
                tempControllerNames.add(controllerName);
            }
        }
        return tempControllerNames;
    }

    /**
     * initMapping() ，功能是初始化URL 映射
     * 找到使用了注解 @RequestMapping 的方法:
     * 1.URL 存放到 urlMappingNames 里
     * 2.映射的对象存放到 mappingObjs 里
     * 3.映射的方法存放到 mappingMethods 里。
     */
    protected void initMapping() {
        //controllerName为包中的一个个的类
        for (String controllerName : this.controllerNames) {
            Class<?> clazz = this.controllerClasses.get(controllerName);
            Object obj = this.controllerObjs.get(controllerName);
            Method[] methods = clazz.getDeclaredMethods();
            if(methods!=null){
                //检查所有的方法
                for(Method method : methods){
                    //有RequestMapping注解
                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                    if (isRequestMapping){
                        String methodName = method.getName();
                        //得到url路径
                        String urlmapping = method.getAnnotation(RequestMapping.class).value();
                        this.urlMappingNames.add(urlmapping);
                        //用于保存url与对象的映射关系
                        this.mappingObjs.put(urlmapping, obj);
                        //用于保存url与方法的映射关系
                        this.mappingMethods.put(urlmapping, method);
                    }
                }
            }
        }
    }

    /**
     * doGet() 由Servlet容器（如Tomcat）自动触发的方法，用于处理HTTP GET请求。
     * 当客户端发送一个HTTP GET请求到与该Servlet映射的URL时，Servlet容器会调用doGet()方法来处理这个请求。
     * 通过 Bean 的 id 获取其对应的类和方法，依赖反射机制进行调用。
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求的Servlet路径
        String sPath = request.getServletPath();
        System.out.println("in doGet(),sPath :" + sPath);
        if (!this.urlMappingNames.contains(sPath)) {
            return;
        }

        Object obj = null;
        Object objResult = null;
        try {
            // 获取与请求路径对应的方法和对象
            Method method = this.mappingMethods.get(sPath);
            obj = this.mappingObjs.get(sPath);
            // 调用方法并获取结果
            objResult = method.invoke(obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // 将方法执行结果写入响应
        response.getWriter().append(objResult.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}