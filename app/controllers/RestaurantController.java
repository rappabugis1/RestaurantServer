package controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.implementations.*;
import daos.interfaces.*;
import models.*;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantController extends Controller {

    RestaurantDao restDao = new RestaurantDaoImpl();

    CategoryDao catDao = new CategoryDaoImpl();

    LocationDao locDao = new LocationDaoImpl();

    TableDao tableDao = new TableDaoImpl();

    DishDao dishDao = new DishDaoImpl();

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
        catch (Exception e) {
            return badRequest(e.getMessage());
        }

    }

    public Result getRestaurantCategories(){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try{
            ObjectMapper mapper = new ObjectMapper();

            ArrayNode returnNode = mapper.valueToTree(catDao.getRestaurantCategories(json.get("id").asLong()));

            return ok(returnNode.toString());

        }catch (Exception e){
            return badRequest(e.getMessage());
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
                    .eq("restaurant_id", json.get("idRestaurant").asLong())
                    .findOne();


            if (newReview == null) {
                newReview = new Review();
                newReview.setMark(json.get("mark").asInt());
                newReview.setComment(json.get("comment").toString());
                newReview.setInsertTime(new Timestamp(System.currentTimeMillis()).toString());
                newReview.setUser(User.finder.byId(json.get("idUser").asLong()));
                newReview.setRestaurant(Restaurant.finder.byId(json.get("idRestaurant").asLong()));
                newReview.save();

            } else {
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

    public Result getAllRestaurantComments() {
        JsonNode json = request().body().asJson();


        if (json == null)
            return badRequest("Invalid Json is null");

        try {

            Long id = json.get("idRestaurant").asLong();

            return ok(restDao.getAllRestaurantComments(id));
        } catch (Exception e) {
            return badRequest(e.getMessage());
        }
    }

    public Result getRandomRestaurants() {
        try {
            return ok(restDao.getRandomRestaurants());
        } catch (Exception e) {
            return badRequest(e.getMessage());
        }
    }

    public Result getAllRestaurantTables(){
        JsonNode json = request().body().asJson();


        if (json == null)
            return badRequest("Invalid Json is null");

        try {
            return ok(restDao.getAllRestaurantTables(json.get("id").asLong()));
        }
        catch (Exception e){return  badRequest(e.getMessage());}
    }

    public Result getDishTypes(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            return ok(mapper.writeValueAsString(restDao.getAllDishTypes()));
        } catch (Exception e){
            return badRequest();
        }
    }

    public Result adminMenuItems(){
        JsonNode json = request().body().asJson();


        if (json == null)
            return badRequest("Invalid Json is null");

        try {

            //init menu map
            Map<String, Menu> menus = new HashMap<>();

            for (Menu menu : restDao.getRestaurantMenus(json.get("restaurantId").asLong())) {
                menus.put(menu.getType(), menu);
            }

            //Init dish type map
            Map<String, DishType> dishTypesMap = new HashMap<>();
            for (DishType dishType : restDao.getAllDishTypes()) {
                dishTypesMap.put(dishType.getType(), dishType);
            }

            //Do add queue
            for (JsonNode newDish : json.get("addQueue")) {

                Dish dish = new Dish(newDish.get("name").asText(), newDish.get("description").asText(), newDish.get("price").asInt(), menus.get(newDish.get("menuType").asText()), dishTypesMap.get(newDish.get("dishType").asText()));
                dishDao.createDish(dish);
            }

            for (JsonNode deleteId : json.get("deleteQueue")) {
                dishDao.deleteDish(dishDao.getDishById(deleteId.asLong()));
            }

            return ok();
        } catch (Exception e){
            return badRequest(e.getMessage());
        }
    }

   public Result adminRestaurantTables (){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try{

            Restaurant restaurant= restDao.getRestaurantbyId(json.get("restaurantId").asLong());

            //Add Queue
            for(JsonNode newTable: json.get("addQueue")){
                int amount = newTable.get("amount").asInt();
                for(int i=0;i<amount; i++){
                    tableDao.CreateTable(new Table(newTable.get("tableType").asInt(), restaurant));
                }
            }

            return ok();

        }catch (Exception e){
            return badRequest(e.getMessage());
        }

    }

    public Result adminRestaurantReservationLengths(){
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Invalid Json is null");

        try{

            Restaurant restaurant= restDao.getRestaurantbyId(json.get("restaurantId").asLong());

            //Add Queue
            for(JsonNode length: json.get("addQueue")){
                GuestStay definedStay = new GuestStay();
                definedStay.setRestaurant(restaurant);
                definedStay.setGuestNumber(length.get("guestNumber").asInt());
                definedStay.save();

                (new StayByDayType(length.get("workday").get("morning").asInt(), length.get("workday").get("day").asInt(), length.get("workday").get("evening").asInt(), definedStay, "workday")).save();
                (new StayByDayType(length.get("weekend").get("morning").asInt(), length.get("weekend").get("day").asInt(), length.get("weekend").get("evening").asInt(), definedStay, "weekend")).save();
            }

            return ok();

        }catch (Exception e){
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

