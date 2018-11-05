package daos.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import daos.interfaces.RestaurantDao;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import models.Restaurant;
import org.omg.CORBA.Object;
import play.Logger;
import play.libs.Json;

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
                "SELECT r.location_id as id, l.city as \"location\", COUNT(r.location_id) as num\n" +
                "FROM restaurants r, locations  l  TABLESAMPLE SYSTEM_ROWS(20)\n" +
                "WHERE r.location_id=l.id\n" +
                "GROUP BY r.location_id,l.city";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(sqlRows);

    }

    @Override
    public String getRandomRestaurants() throws IOException {
         final String sql =
                        "SELECT  r.id,coalesce(reviews.mark, 0) as mark, coalesce(reviews.votes,0) as votes, r.restaurant_name as restaurantName,r.description, r.price_range as priceRange, r.latitude, r.longitude, r.image_file_name as imageFileName, r.cover_file_name as coverFileName, r.location_id, string_agg(categories.name, ' | ') as foodType\n" +
                                "                        FROM restaurants r TABLESAMPLE SYSTEM_ROWS(6)\n" +
                                "                        join restaurant_categories on r.id= restaurant_categories.restaurant_id\n" +
                                "                        join categories on categories.id=restaurant_categories.category_id \n" +
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

            for ( JsonNode objNode : node) {


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
    //Update methods

    //Delete methods

}
