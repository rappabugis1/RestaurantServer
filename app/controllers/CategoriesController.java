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
import util.JWTUtil;

import java.io.IOException;
import java.util.Optional;

public class CategoriesController extends Controller {

    CategoryDao catDao = new CategoryDaoImpl();


    //POST

    public Result addCategory() {

        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if (json == null || json.get("name") == null)
            return badRequest();

        try {

            Category newCategory = catDao.createCategory(json.get("name").asText());


            ObjectMapper mapper = new ObjectMapper();

            ObjectNode returnNode = mapper.createObjectNode();

            returnNode
                    .put("id", newCategory.id)
                    .put("name", newCategory.getName());

            return ok(mapper.readTree(returnNode.toString()).toString());
        } catch (Exception e) {
            return badRequest("Category already exists!");
        }
    }

    public Result getCategoryDetails() {
        JsonNode json = request().body().asJson();

        if (json == null || json.get("id") == null)
            return badRequest();

        try {
            Category newCategory = catDao.getCategoryDetails(json.get("id").asLong());

            if (newCategory == null)
                return badRequest("Catergory exists!");

            ObjectMapper mapper = new ObjectMapper();

            try {
                return ok(mapper.writeValueAsString(newCategory));
            } catch (JsonProcessingException e) {
                return badRequest("Failed to map JSON!");
            }
        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e){
            return badRequest(e.toString());
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


        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest(e.getMessage());
        }
    }

    public Result editCategory(){

        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

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

        catch (NullPointerException e){
            return badRequest("Missing json fields...");
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
        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e) {
            return badRequest("Something went wrong : " + e.toString());
        }
    }

    public Result deleteCategory(){

        Optional<String> token = request().getHeaders().get("Authorization");
        try{
            (new JWTUtil()).verifyJWT(token.get().substring(7));

        }catch (Exception e){
            return unauthorized("Not Authorized!");
        }

        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {
            catDao.deleteCategory(catDao.getCategoryDetails(json.get("id").asLong()));
            return ok();
        }
        catch (NullPointerException e){
            return badRequest("Missing json fields...");
        }
        catch (Exception e){
            return  badRequest(e.getMessage());
        }
    }

}
