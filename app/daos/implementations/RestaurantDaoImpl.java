package daos.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import daos.interfaces.RestaurantDao;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import models.Restaurant;

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
                "SELECT r.location_id, l.city, COUNT(r.location_id) as num\n" +
                "FROM restaurants r, locations  l \n" +
                "WHERE r.location_id=l.id\n" +
                "GROUP BY r.location_id,l.city";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();

        ObjectMapper mapper = new ObjectMapper();

        ArrayNode nodeParent = mapper.createArrayNode();

        for (SqlRow row: sqlRows) {
            ObjectNode nodeChild = mapper.createObjectNode();

            nodeChild
                    .put("id", row.getLong("location_id"))
                    .put("location", row.getString("city"))
                    .put("number", row.getInteger("num"));

            nodeParent.add(nodeChild);
        }

        return (new ObjectMapper().readTree(nodeParent.toString())).toString();

    }
    //Update methods

    //Delete methods
}
