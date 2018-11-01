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
import java.util.List;

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

            newRestaurant.getCategories().clear();
            for (JsonNode cat : categories
            ) {
                newRestaurant.getCategories().add(catDao.getCategoryDetails(cat.asLong()));
            }

            restDao.createRestaurant(newRestaurant);


            return ok(getJsonRest(newRestaurant));


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

        try {
            return ok(getJsonRest(restaurant));
        } catch (IOException e) {
            return badRequest("Error parsing restaurant to JSON :(");
        }
    }

    public Result getRestaurantMenu() {

        JsonNode json = request().body().asJson();
        Long id = Long.valueOf(json.get("idRestaurant").toString());
        String menu_type = json.get("type").toString();

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
            return badRequest();

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

            Review newReview = new Review();

            newReview.setMark(json.get("mark").asInt());
            newReview.setComment(json.get("comment").toString());
            newReview.setInsertTime(new Timestamp(System.currentTimeMillis()).toString());
            newReview.setUser(User.finder.byId(json.get("idUser").asLong()));
            newReview.setRestaurant(Restaurant.finder.byId(json.get("idRestaurant").asLong()));

            //TODO REVIEW DAO

            newReview.save();

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

    private static String getJsonRest(Restaurant restaurant) throws IOException {
        int avg = 0;

        for (Review review : restaurant.getReviews()) {
            avg += review.getMark();
        }

        if (avg > 0 && restaurant.getReviews().size() > 0)
            avg /= restaurant.getReviews().size();
        else
            avg = 0;

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode node = mapper.createObjectNode();

        StringBuilder foodType = new StringBuilder();

        for (Category category : restaurant.getCategories()) {
            if (category.getName() != null)
                foodType.append(category.getName() + " | ");
        }

        node
                .put("id", restaurant.id)
                .put("restaurantName", restaurant.getRestaurantName())
                .put("description", restaurant.getDescription())
                .put("latitude", restaurant.getLatitude())
                .put("longitude", restaurant.getLongitude())
                .put("mark", avg)
                .put("votes", restaurant.getReviews().size())
                .put("priceRange", restaurant.getPriceRange())
                .put("imageFileName", restaurant.getImageFileName())
                .put("coverFileName", restaurant.getCoverFileName())
                .put("location", restaurant.getLocation().id)
                .put("foodType", foodType.toString());

        return (new ObjectMapper().readTree(node.toString())).toString();
    }

    private static String getJsonMenu(Menu menu, Long id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode nodeParent = mapper.createArrayNode();

        for (Dish dish : menu.getDishes()) {
            ObjectNode nodeChild = mapper.createObjectNode();

            nodeChild
                    .put("id", dish.id)
                    .put("idRestaurant", id)
                    .put("type", dish.getType())
                    .put("name", dish.getName())
                    .put("description", dish.getDescription());

            nodeParent.add(nodeChild);
        }

        return (new ObjectMapper().readTree(nodeParent.toString())).toString();

    }
}

