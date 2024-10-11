package org.jboss.as.quickstarts.kitchensink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import jakarta.faces.webapp.FacesServlet;

@SpringBootApplication
@EnableCaching
public class KitchenSinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitchenSinkApplication.class, args);
	}
	
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        FacesServlet servlet = new FacesServlet();
        ServletRegistrationBean servletRegistrationBean = 
          new ServletRegistrationBean(servlet, "*.jsf");
        return servletRegistrationBean;
    }

}
