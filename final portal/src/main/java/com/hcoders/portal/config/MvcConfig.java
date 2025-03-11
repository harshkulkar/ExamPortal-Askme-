package com.hcoders.portal.config;

// Importing necessary Spring Framework annotations and classes for configuration and view resolution.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

// Importing Thymeleaf's template resolver for processing HTML templates.
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration // Marks this class as a source of bean definitions for the application context.
@EnableWebMvc  // Enables default Spring MVC configuration, such as handler mappings and view resolution.
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Configure static resource handling.
     * This method maps URL patterns to physical locations in the classpath where static resources reside.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map requests starting with /images/, /css/, /js/, and /vendor/ to the corresponding directories in the classpath.
        registry.addResourceHandler("/images/**", "/css/**", "/js/**", "vendor/**")
                .addResourceLocations(
                        "classpath:/static/images/",  // Location for image files.
                        "classpath:/static/css/",     // Location for CSS files.
                        "classpath:/static/js/",      // Location for JavaScript files.
                        "classpath:/static/vendor/"); // Location for vendor resources (e.g., third-party libraries).
    }

    /**
     * Define a ViewResolver bean for resolving views to HTML templates.
     * This resolver looks for HTML files under the "templates/" directory.
     */
    @Bean
    public ViewResolver getViewResolver() {
        // Create an InternalResourceViewResolver for resolving view names to actual templates.
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("templates/"); // Set the directory prefix where view files are located.
        resolver.setSuffix(".html");      // Set the file extension for view files.
        return resolver;
    }

    /**
     * Define a JSP view resolver bean.
     * This resolver is used to resolve views for JSP pages stored under /WEB-INF/view/jsp/.
     */
    @Bean
    InternalResourceViewResolver jspViewResolver() {
        // Create an InternalResourceViewResolver that resolves JSP views.
        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/jsp/"); // Set the directory prefix for JSP files.
        viewResolver.setSuffix(".jsp");               // Set the file extension for JSP files.
        viewResolver.setViewClass(JstlView.class);      // Set the view class to support JSTL.
        return viewResolver;
    }

    /**
     * Define a Thymeleaf template resolver bean.
     * This resolver is responsible for finding Thymeleaf templates in the classpath.
     */
    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        // Create a ClassLoaderTemplateResolver for loading Thymeleaf templates from the classpath.
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");      // Set the folder location of Thymeleaf templates.
        templateResolver.setCacheable(false);            // Disable caching during development for immediate reflection of changes.
        templateResolver.setSuffix(".html");             // Set the file extension for Thymeleaf templates.
        templateResolver.setTemplateMode("HTML5");       // Define the template mode (HTML5 in this case).
        templateResolver.setCharacterEncoding("UTF-8");    // Ensure that templates are processed with UTF-8 encoding.
        return templateResolver;
    }
}
