package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.model.Category;
import ittalents.javaee.model.CategoryDto;
import ittalents.javaee.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Category> categoryById = categoryRepository.findById(id);

        if (categoryById.isPresent()) {
            return categoryById.get();
        }

        throw new ElementNotFoundException("Category with id = " + id + " does not exist!");
    }

    public List<String> getCategoryIcons(long id) {
        Category category = getCategoryById(id);
        List<String> categoryIcons = new ArrayList<>();
        // TODO iconRepository.findIconsByCategoryId(id) ?
        // category.getName().getIcons() ?
        return categoryIcons;
    }

    public void deleteCategory(long id) {
        this.categoryRepository.deleteById(id);
    }
}
