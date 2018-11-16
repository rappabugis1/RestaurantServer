package controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.implementations.CategoryDaoImpl;
import daos.implementations.LocationDaoImpl;
import daos.implementations.RestaurantDaoImpl;
import daos.interfaces.CategoryDao;
import daos.interfaces.LocationDao;
import daos.interfaces.RestaurantDao;
import models.*;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.sql.Timestamp;

public class RestaurantController extends Controller {

    RestaurantDao restDao = new RestaurantDaoImpl();

    CategoryDao catDao = new CategoryDaoImpl();

    LocationDao locDao = new LocationDaoImpl();

    public Result addRestaurant() {
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            ObjectNode node = (ObjectNode) mapper.readTree(json.toString());

            Long location_id = node.get("location").asLong();
            JsonNode categories = mapper.readTree(json.toString()).get("categories");

            Restaurant newRestaurant = mapper.convertValue(json, Restaurant.class);

            newRestaurant.setLocation(locDao.getById(location_id));

            newRestaurant.getCategoryList().clear();
            for (JsonNode cat : categories
            ) {
                newRestaurant.getCategoryList().add(catDao.getCategoryDetails(cat.asLong()));
            }

            restDao.createRestaurant(newRestaurant);

            return ok((new ObjectMapper()).valueToTree(newRestaurant).toString());


        } catch (IOException e) {
            return badRequest("Error reading tree!");
        }

    }

    public Result getRestaurantDetails() {

        JsonNode json = request().body().asJson();

        Long id = Long.valueOf(json.get("Id").toString());

        Restaurant restaurant = restDao.getRestaurantbyId(id);

        if (restaurant == null) {
            return badRequest("Restaurant doesn't exist!");
        }

        return ok((new ObjectMapper()).valueToTree(restaurant).toString());
    }

    public Result getRestaurantMenu() {

        JsonNode json = request().body().asJson();
        Long id = json.get("idRestaurant").asLong();
        String menu_type = json.get("type").asText();

        Restaurant restaurant = restDao.getRestaurantbyId(id);

        if (restaurant == null) {
            return badRequest("Restaurant doesn't exist!");
        }

        Menu returnMenu = null;

        for (Menu menu : restaurant.getMenus()) {
            if (menu.getType().equals(menu_type)) {
                returnMenu = menu;
            }
        }

        if (returnMenu == null)
            return badRequest("Menu does not exist!");

        try {
            return ok(getJsonMenu(returnMenu, id));
        } catch (IOException e) {
            return badRequest("Error parsing restaurant to JSON :(");
        }

    }

    public Result getRestaurantLocations() {
        try {
            return ok(restDao.locationsRestaurant());
        } catch (IOException e) {
            return badRequest("Error parsing restaurant to JSON :(");
        }

    }

    public Result insertComment() {
        JsonNode json = request().body().asJson();


        if (json == null)
            return badRequest("Invalid Json is null");

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

        try {

            Review newReview = Review.getFinder()
                    .query()
                    .where()
                    .eq("user_id", json.get("idUser").asLong())
                    .eq("restaurant_id" ,json.get("idRestaurant").asLong())
                    .findOne();



            if(newReview==null) {
                newReview= new Review();
                newReview.setMark(json.get("mark").asInt());
                newReview.setComment(json.get("comment").toString());
                newReview.setInsertTime(new Timestamp(System.currentTimeMillis()).toString());
                newReview.setUser(User.finder.byId(json.get("idUser").asLong()));
                newReview.setRestaurant(Restaurant.finder.byId(json.get("idRestaurant").asLong()));
                newReview.save();

            }
            else{
                newReview.setComment(json.get("comment").toString());
                newReview.setMark(json.get("mark").asInt());
                newReview.update();
            }
            //TODO REVIEW DAO

            return ok();

        } catch (Exception e) {
            return badRequest("Error in adding the review : " + e.getMessage());
        }


    }

    public Result getAllCategories() {
        try{
            ObjectMapper mapper = new ObjectMapper();

            ArrayNode returnNode = mapper.valueToTree(catDao.getAllCategories());

            return ok(returnNode.toString());
        }
        catch (Exception e) {
            return  badRequest("Something went wrong : " +e.getMessage());
        }
    }

    public Result getRandomRestaurants(){
        try {
            return ok(restDao.getRandomRestaurants());
        } catch (Exception e) {
            return badRequest(e.getMessage());
        }
    }

    private static String getJsonMenu(Menu menu, Long id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode nodeParent = mapper.createArrayNode();

        for (Dish dish : menu.getDishes()) {
            ObjectNode nodeChild = mapper.createObjectNode();

            nodeChild
                    .put("id", dish.id)
                    .put("idRestaurant", id)
                    .put("dishType", dish.getType())
                    .put("name", dish.getName())
                    .put("description", dish.getDescription())
                    .put("type", menu.getType())
                    .put("price", dish.getPrice());

            nodeParent.add(nodeChild);
        }

        return (new ObjectMapper().readTree(nodeParent.toString())).toString();

    }
}

