package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.exceptions.InvalidOperationException;
import ittalents.javaee.model.dao.IconDao;
import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private IconDao iconDao;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, IconDao iconDao) {
        this.categoryRepository = categoryRepository;
        this.iconDao = iconDao;
    }

    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> categories = new ArrayList<>();
        for (Category category : categoryRepository.findAll()) {
            categories.add(category.toDto());
        }
        return categories;
    }

    public Category getCategoryById(long id) {
        Optional<Category> categoryById = categoryRepository.findById(id);

        if (categoryById.isPresent()) {
            return categoryById.get();
        }

        throw new ElementNotFoundException("Category with id = " + id + " does not exist!");
    }

    public List<String> getCategoryIcons(long id) throws SQLException {
        return iconDao.getIconsUrlsByCategoryId(id);
    }

    public void deleteCategory(long id) {
        this.categoryRepository.deleteById(id);
    }

    public void changeCategoryIcon(long categoryId, long iconId) throws SQLException {
        Category category = getCategoryById(categoryId);
        List<String> urls = getCategoryIcons(categoryId);

        if (iconId < 0 || iconId >= urls.size()) {
            throw new InvalidOperationException("There is not icon with id = " + iconId);
        }

        category.setIconURL(urls.get((int) categoryId));
    }
}
