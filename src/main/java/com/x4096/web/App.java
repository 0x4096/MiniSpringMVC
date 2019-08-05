package com.x4096.web;

import com.x4096.web.mvc.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;

/**
 * Hello world!
 */
public class App {

    private static final int PORT = 8080;
    private static final String CONTEXT_PATH = "/";
    private static final String SERVLET_NAME = "dispatcherServlet";

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();

        Context ctx = tomcat.addContext(CONTEXT_PATH, null);

        try {
            Tomcat.addServlet(ctx, SERVLET_NAME, new DispatcherServlet()).load();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        ctx.addServletMappingDecoded("/*", SERVLET_NAME);

        /* 创建连接 */
        Connector connector = new Connector();
        connector.setPort(PORT);
        tomcat.setConnector(connector);
        try {
            tomcat.init();
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
