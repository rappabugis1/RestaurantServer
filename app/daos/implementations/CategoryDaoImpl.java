package daos.implementations;

import daos.interfaces.CategoryDao;
import models.Category;

import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public Category createCategory(String categoryName){
        try{
            Category newCategory= new Category();
            newCategory.setName(categoryName);
            newCategory.save();

            return newCategory;
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public Category getCategoryDetails(Long id){
        return Category.finder.byId(id);
    }

    @Override
    public List<Category> getAllCategories(){ return Category.finder.all();}
}
