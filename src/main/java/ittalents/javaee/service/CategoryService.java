package ittalents.javaee.service;

import ittalents.javaee.model.Category;
import ittalents.javaee.model.CategoryDto;
import ittalents.javaee.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> categories = new ArrayList<>();
        for (Category category : categoryRepository.findAll()) {
            categories.add(category.toDto());
        }
        return categories;
    }

    public void createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.fromDto(categoryDto);
        this.categoryRepository.save(category);
    }

    public Category getCategoryById(long id) {
        return categoryRepository.getOne(id);
    }

    public List<String> getCategoryIcons(long id) {
        List<String> categoryIcons = new ArrayList<>();
        // iconRepository.findIconsByCategoryId(id) ?
        return categoryIcons;
    }

    public void deleteCategory(long id) {
        this.categoryRepository.deleteById(id);
    }
}
