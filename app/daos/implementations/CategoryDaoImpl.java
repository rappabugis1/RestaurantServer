package daos.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import daos.interfaces.CategoryDao;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.Category;
import models.Restaurant;

import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    //Create

    @Override
    public Category createCategory(String categoryName) {
        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory.save();

        return newCategory;
    }

    //Read

    @Override
    public Category getCategoryDetails(Long id) {
        return Category.finder.byId(id);
    }

    @Override
    public Long getIdFromName(String name) {
        return Category.getFinder().query().where().eq("name", name).findOne().id;
    }

    @Override
    public List<Category> getAllCategories() {
        return Category.finder.all();
    }


    @Override
    public PagedList<Category> getFilteredCategories(JsonNode json){
        int itemsPerPage = json.get("itemsPerPage").asInt();
        int pageNumber = json.get("pageNumber").asInt();
        JsonNode searchTextNode = json.get("searchText");

        ExpressionList<Category> query = Category.getFinder().query().where();

        if (searchTextNode != null) {
            String searchText = searchTextNode.asText();
            query.icontains("name", searchText);
        }

        query.setFirstRow(itemsPerPage * (pageNumber - 1)).setMaxRows(itemsPerPage);

        return query.findPagedList();
    }

    @Override
    public List<Category> getRestaurantCategories(Long id){
        return Restaurant.getFinder().byId(id).getCategoryList();
    }

    //Update

    @Override
    public void updateCategory(Category category){
        category.update();
    }
    //Delete

}
