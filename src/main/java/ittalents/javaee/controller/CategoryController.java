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
public class CategoryController extends AbstractController{

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

    @GetMapping("/categories/{id}/icons")
    public ResponseEntity getCategoryIcons(@PathVariable @Positive long id) throws SQLException {
        List<String> iconUrls = this.categoryService.getCategoryIcons(id);
        return ResponseEntity.ok(iconUrls);
    }

    @PutMapping("/categories/{categoryId}/{iconId}")
    public ResponseEntity changeCategoryIcon(@PathVariable("categoryId") @Positive long categoryId,
                                   @PathVariable("iconId") @Positive long iconId) throws SQLException {
        categoryService.changeCategoryIcon(categoryId, iconId);
        return ResponseEntity.ok().build();
    }
}
