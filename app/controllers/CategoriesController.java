package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.implementations.CategoryDaoImpl;
import daos.interfaces.CategoryDao;
import io.ebean.PagedList;
import models.Category;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;

public class CategoriesController extends Controller {

    CategoryDao catDao = new CategoryDaoImpl();


    //POST

    public Result addCategory() {

        JsonNode json = request().body().asJson();

        if (json == null || json.get("name") == null)
            return badRequest();

        Category newCategory = catDao.createCategory(json.get("name").asText());

        if (newCategory == null)
            return badRequest();

        ObjectMapper mapper = new ObjectMapper();


        try {
            ObjectNode returnNode = mapper.createObjectNode();

            returnNode
                    .put("id", newCategory.id)
                    .put("name", newCategory.getName());

            return ok(mapper.readTree(returnNode.toString()).toString());
        } catch (IOException e) {
            return badRequest("Failed to map JSON! Ovo se ne bi trebalo desiti...");
        }
    }

    public Result getCategoryDetails() {
        JsonNode json = request().body().asJson();

        if (json == null || json.get("id") == null)
            return badRequest();

        Category newCategory = catDao.getCategoryDetails(json.get("id").asLong());

        if (newCategory == null)
            return badRequest("Catergory exists!");

        ObjectMapper mapper = new ObjectMapper();

        try {
            return ok(mapper.writeValueAsString(newCategory));
        } catch (JsonProcessingException e) {
            return badRequest("Failed to map JSON! Ovo se ne bi trebalo desiti...");
        }
    }

    public Result getFilteredCategories  (){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {
            PagedList result = catDao.getFilteredCategories(json);

            ObjectNode returnNode = (new ObjectMapper()).createObjectNode();
            returnNode.put("numberOfPages", result.getTotalPageCount());


            returnNode.putArray("categories").addAll((ArrayNode) (new ObjectMapper()).valueToTree(result.getList()));

            return ok((new ObjectMapper()).writeValueAsString(returnNode));


        } catch (Exception e) {
            return badRequest(e.getMessage());
        }
    }

    public Result editCategory(){

        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {
            Category category= catDao.getCategoryDetails(json.get("id").asLong());
            category.setName(json.get("name").asText());

            catDao.updateCategory(category);

            ObjectMapper mapper = new ObjectMapper();

            return ok(mapper.writeValueAsString(category));
        }
        catch (Exception e) {
            return badRequest(e.getMessage());
        }

    }

    public Result getAllCategories() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            ArrayNode returnNode = mapper.valueToTree(catDao.getAllCategories());

            return ok(returnNode.toString());
        } catch (Exception e) {
            return badRequest("Something went wrong : " + e.getMessage());
        }
    }

}
