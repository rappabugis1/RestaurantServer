package daos.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.interfaces.RestaurantDao;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDaoImpl implements RestaurantDao {

    //Create methods
    @Override
    public void createRestaurant(Restaurant newRest) {


        newRest.save();

        //Add menus
        ArrayList<String> menuTypes = new ArrayList<>();
        menuTypes.add("Breakfast");
        menuTypes.add("Lunch");
        menuTypes.add("Dinner");

        for (String menuType : menuTypes) {
            Menu menu = new Menu(menuType, newRest);
            menu.save();
        }
    }

    //Read methods

    @Override
    public int getNumberRestaurants(){
        return Restaurant.finder.query().findCount();
    }

    @Override
    public List<Restaurant> getRestaurants() {
        return Restaurant.finder.all();
    }

    @Override
    public Restaurant getRestaurantbyId(Long id) {
        return Restaurant.finder.byId(id);
    }

    @Override
    public Restaurant getRestaurantByName(String name) {
        return Restaurant.getFinder().query()
                .where()
                .eq("restaurant_name", name)
                .findOne();
    }

    @Override
    public String locationsRestaurant() throws IOException {

        final String sql =
                "SELECT r.location_id as id, l.name as \"location\", COUNT(r.location_id) as num\n" +
                        "FROM restaurants r, locations  l  TABLESAMPLE SYSTEM_ROWS(20)\n" +
                        "WHERE r.location_id=l.id\n" +
                        "GROUP BY r.location_id,l.name";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(sqlRows);

    }

    @Override
    public String getRandomRestaurants() throws IOException {



        final String sql =
                "SELECT  r.id,coalesce(reviews.mark, 0) as mark, coalesce(reviews.votes,0) as votes, r.restaurant_name as restaurantName,r.description, r.price_range as priceRange, r.latitude, r.longitude, r.image_file_name as imageFileName, r.cover_file_name as coverFileName, r.location_id, string_agg(categories.name, ' | ') as foodType\n" +
                        "                        FROM restaurants r TABLESAMPLE SYSTEM_ROWS(6)\n" +
                        "                        join categories_restaurants on r.id= categories_restaurants.restaurants_id\n" +
                        "                        join categories on categories.id=categories_restaurants.categories_id \n" +
                        "left join (SELECT r.id as id, count(reviews.id) as votes, coalesce(round(avg(reviews.mark),0),0) as mark \n" +
                        "FROM restaurants r , reviews\n" +
                        "where r.id=reviews.restaurant_id\n" +
                        "group by r.id) as reviews on r.id=reviews.id\n" +
                        "group by r.id, r.restaurant_name, reviews.mark, reviews.votes\n";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();


        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = mapper.readTree(mapper.writeValueAsString(sqlRows));


        //Vela havle ovo je potrebno jer "JDBC POSTGRES DRIVER auto maps uppercase to lowercase because of 'CoNtInNuiTy'"

        if (node.isArray()) {

            for (JsonNode objNode : node) {


                ((ObjectNode) objNode).set("restaurantName", objNode.get("restaurantname"));
                ((ObjectNode) objNode).set("priceRange", objNode.get("pricerange"));
                ((ObjectNode) objNode).set("imageFileName", objNode.get("imagefilename"));
                ((ObjectNode) objNode).set("coverFileName", objNode.get("coverfilename"));
                ((ObjectNode) objNode).set("foodType", objNode.get("foodtype"));

                ((ObjectNode) objNode).remove("restaurantname");
                ((ObjectNode) objNode).remove("pricerange");
                ((ObjectNode) objNode).remove("imagefilename");
                ((ObjectNode) objNode).remove("coverfilename");
                ((ObjectNode) objNode).remove("foodtype");

            }
        }

        return mapper.writeValueAsString(node);
    }

    @Override
    public String getAllRestaurantComments(Long id) throws JsonProcessingException {
        List<Review> reviews = Review.getFinder().query().where().eq("restaurant.id", id).findList();

        return (new ObjectMapper()).writeValueAsString(reviews);
    }

    @Override
    public String getAllRestaurantTables (Long id) throws JsonProcessingException {
        List<Table> tables = Table.getFinder().query().where().eq("restaurant_id", id).findList();
        return (new ObjectMapper()).writeValueAsString(tables);

    }

    @Override
    public List<DishType> getAllDishTypes(){
        return DishType.getFinder().all();
    }

    @Override
    public List<Menu> getRestaurantMenus(Long id){
        return Menu.getFinder().query().where().eq("restaurant.id", id).findList();
    }

    @Override
    public Menu getMenuByType(String name){
        return Menu.getFinder().query().where().eq("type", name).findOne();
    }

    //Update methods

    //Delete methods
    @Override
    public void deleteRestaurant(Restaurant restaurant){
        restaurant.delete();
    }
}
