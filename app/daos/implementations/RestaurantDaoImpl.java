package daos.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.interfaces.RestaurantDao;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import models.Restaurant;
import org.omg.CORBA.Object;
import play.Logger;

import java.io.IOException;
import java.util.List;

public class RestaurantDaoImpl implements RestaurantDao {

    //Create methods
    @Override
    public Restaurant createRestaurant (Restaurant newRest){
        try{
            newRest.save();

            return newRest;
        }
        catch (Exception e){
            return null;
        }
    }

    //Read methods

    @Override
    public List<Restaurant> getRestaurants(){
        return Restaurant.finder.all();
    }
    @Override
    public Restaurant getRestaurantbyId(Long id){
        return Restaurant.finder.byId(id);
    }

    @Override
    public Restaurant getRestaurantByName (String name){
        return Restaurant.getFinder().query()
                .where()
                .eq("restaurant_name", name)
                .findOne();
    }

    @Override
    public String locationsRestaurant () throws IOException {

        final String sql =
                "SELECT r.location_id as id, l.city as \"LOCATION\", COUNT(r.location_id) as num\n" +
                "FROM restaurants r, locations  l  TABLESAMPLE SYSTEM_ROWS(20)\n" +
                "WHERE r.location_id=l.id\n" +
                "GROUP BY r.location_id,l.city";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(sqlRows);

    }

    @Override
    public String getRandomRestaurants() throws JsonProcessingException {
         final String sql =
                "SELECT  r.id, r.restaurant_name as \"restaurantName\",count(reviews.id) as votes, coalesce(round(avg(reviews.mark),0),0) as mark ,r.description, r.price_range as \"priceRange\", r.latitude, r.longitude, r.image_file_name as \"imageFileName\", r.cover_file_name as \"coverFileName\", r.location_id, string_agg(categories.name, '|') as \"foodType\"  \n" +
                        "FROM restaurants r TABLESAMPLE SYSTEM_ROWS(6)\n" +
                        "join restaurant_categories on r.id= restaurant_categories.restaurant_id\n" +
                        "join categories on categories.id=restaurant_categories.category_id \n" +
                        "left join  reviews on reviews.restaurant_id= r.id\n" +
                        "group by r.id\n";


        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();


        ObjectMapper mapper = new ObjectMapper();


        return mapper.writeValueAsString(sqlRows);
    }
    //Update methods

    //Delete methods

}
