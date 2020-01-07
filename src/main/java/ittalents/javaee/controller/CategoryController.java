package ittalents.javaee.controller;

import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.sql.SQLException;
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

    @PutMapping("/categories/{categoryId}/{iconId}")
    public void changeCategoryIcon(@PathVariable("categoryId") @Positive long categoryId,
                                   @PathVariable("iconId") @Positive long iconId) throws SQLException {
        categoryService.changeCategoryIcon(categoryId, iconId);
    }

    @GetMapping("/categories/{id}/icons")
    public ResponseEntity getCategoryIcons(@PathVariable @Positive long id) throws SQLException {
        List<String> iconUrls = this.categoryService.getCategoryIcons(id);
        return ResponseEntity.ok(iconUrls);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity deleteCategory(@PathVariable @Positive long id) {
        this.categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
