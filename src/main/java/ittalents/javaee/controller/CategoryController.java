package ittalents.javaee.controller;

import ittalents.javaee.model.CategoryDto;
import ittalents.javaee.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories() {
        return this.categoryService.getAllCategories();
    }

    @PostMapping("/categories")
    public void createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        this.categoryService.createCategory(categoryDto);
    }

    @PutMapping("/categories/{id}")
    public void updateCategory(@PathVariable long id, @RequestBody @Valid CategoryDto categoryDto) {
        // TODO
        // edit iconURL and type?
    }

    @GetMapping("/categories/{id}/icons")
    public List<String> getCategoryIcons(@PathVariable long id) {
        return this.categoryService.getCategoryIcons(id);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable long id) {
        this.categoryService.deleteCategory(id);
    }
}
