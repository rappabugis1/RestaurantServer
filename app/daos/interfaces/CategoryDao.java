package daos.interfaces;

import models.Category;

import java.util.List;

public interface CategoryDao {

    //Create methods

    Category createCategory (String categoryName);

    //TODO
    //Read methods

    Category getCategoryDetails(Long id);

    Long getIdFromName(String name);

    List<Category> getAllCategories();
    //Update methods

    //Delete methods
}
