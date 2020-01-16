package ittalents.javaee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ControllerConfiguration implements WebMvcConfigurer {

    private final LoggedInterceptor interceptor;

    @Autowired
    public ControllerConfiguration(LoggedInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).excludePathPatterns("resources/static/icons/")
                .excludePathPatterns("/users/register", "/users/login");
    }
}
