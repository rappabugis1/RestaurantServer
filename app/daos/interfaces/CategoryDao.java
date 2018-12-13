package daos.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.PagedList;
import models.Category;

import java.util.List;

public interface CategoryDao {

    //Create methods

    Category createCategory(String categoryName);

    //TODO
    //Read methods

    Category getCategoryDetails(Long id);

    Long getIdFromName(String name);

    List<Category> getAllCategories();

    PagedList<Category> getFilteredCategories(JsonNode json);

    List<Category> getRestaurantCategories(Long id);

    void updateCategory(Category category);

    void deleteCategory(Category category);
    //Update methods

    //Delete methods
}
