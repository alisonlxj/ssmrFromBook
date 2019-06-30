package com.alison.redpackets.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * 相当于加载 application的springIoC的总配置
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfig.class};
    }

    /**
     * 加载DispacherServlet配置
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class};
    }

    /**
     * 相当于 servlet-mapping
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"*.do"};
    }

    /**
     *
     * @param dynamic   serlvet上传文件配置
     */
    protected void customizeRegistration(ServletRegistration.Dynamic dynamic){
        String filepath = "D:\\Document";
        Long singleMax = (long) (5*Math.pow(2, 20));
        Long totalMax = (long) (10*Math.pow(2, 20));
        dynamic.setMultipartConfig(new MultipartConfigElement(filepath, singleMax, totalMax, 0));
    }

}
