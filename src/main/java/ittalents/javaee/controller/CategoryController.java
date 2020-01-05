package ittalents.javaee.controller;

import ittalents.javaee.model.CategoryDto;
import ittalents.javaee.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity getAllCategories() {
        List<CategoryDto> categories = this.categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/categories")
    public void createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        // should anyone be able to create category? -> read only table categories?
        this.categoryService.createCategory(categoryDto);
    }

    @PutMapping("/categories/{id}")
    public void updateCategory(@PathVariable @Positive long id, @RequestBody @Valid CategoryDto categoryDto) {
        // TODO
        // edit iconURL and type?
    }

    @GetMapping("/categories/{id}/icons")
    public ResponseEntity getCategoryIcons(@PathVariable @Positive long id) {
        List<String> iconUrls = this.categoryService.getCategoryIcons(id);
        return ResponseEntity.ok(iconUrls);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity deleteCategory(@PathVariable @Positive long id) {
        this.categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
