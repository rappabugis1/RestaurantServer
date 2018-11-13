package filters;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.Restaurant;

public class QueryBuilder {

    private ExpressionList<Restaurant> query= Restaurant.getFinder().query().where();

    private void finishQuery(int itemsPerPage, int pageNumber){
        query.setFirstRow(itemsPerPage*(pageNumber-1))
             .setMaxRows(itemsPerPage);
    }

    private ExpressionList<Restaurant> getQuery(JsonNode json) {

        //Search parameters
        int itemsPerPage= json.get("itemsPerPage").asInt();
        int pageNumber=json.get("pageNumber").asInt();
        JsonNode searchTextNode= json.get("searchText");

        //Add searchText parameter to filter
        if(searchTextNode!=null){
            String searchText= searchTextNode.asText();
            query
                    .or()
                    .contains("restaurant_name", searchText)
                    .contains("categories.name", searchText)
                    .contains("location.city", searchText)
                    .endOr();
        }
        finishQuery(itemsPerPage, pageNumber);

        return query;
    }

    public PagedList<Restaurant> executeQuery(JsonNode json) {
        return getQuery(json).findPagedList();
    }


}
