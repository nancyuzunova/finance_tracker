package ittalents.javaee;

import ittalents.javaee.service.CategoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(JavaeeApplication.class, args);
        CategoryService.fillCategoriesTable();
        System.out.println("category created");
    }

}
