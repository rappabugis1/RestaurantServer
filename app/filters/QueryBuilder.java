package filters;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.Restaurant;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryBuilder {

    private ExpressionList<Restaurant> query= Restaurant.getFinder().query().where();

    private void finishQuery(int itemsPerPage, int pageNumber){
        query.setFirstRow(itemsPerPage*(pageNumber-1))
             .setMaxRows(itemsPerPage);
    }

    private ExpressionList<Restaurant> getQuery(JsonNode json) throws ParseException {

        //Search parameters
        int itemsPerPage= json.get("itemsPerPage").asInt();
        int pageNumber=json.get("pageNumber").asInt();
        JsonNode searchTextNode= json.get("searchText");
        JsonNode reservationInfoNode= json.get("reservationInfo");

        //Add searchText parameter to filter if exists
        if(searchTextNode!=null){
            String searchText= searchTextNode.asText();
            query
                    .or()
                        .icontains("restaurant_name", searchText)
                        .icontains("categories.name", searchText)
                        .icontains("location.name", searchText)
                    .endOr();
        }

        //Add reservation parameters to filter if exists
        if(reservationInfoNode!=null){

            Timestamp reservationDateTime=getStampFromDate(json.get("reservationDate").asText(), json.get("reservationHour").asText());

            int lengthOfStay= json.get("lengthOfStay").asInt();

            //Get reservation end time
            Timestamp reservationEnd = new Timestamp(reservationDateTime.getTime() + (lengthOfStay*60)*1000);

            query
                    .and()
                        .ge("tables.sitting_places", reservationInfoNode.get("persons").asInt())
                        .and()
                            .ge("reservations.reservationDateTime", reservationDateTime)
                            .le("reservations.reservationEndDateTime", reservationEnd)
                        .endAnd()
                    .endAnd();
        }

        finishQuery(itemsPerPage, pageNumber);



        return query;
    }

    public PagedList<Restaurant> executeQuery(JsonNode json) throws ParseException {
        return getQuery(json).findPagedList();
    }

    private Timestamp getStampFromDate(String reservationDate, String reservationHour) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = dateFormat.parse(reservationDate+" "+reservationHour);

        if(parsedDate.before(new Date()))
            throw new ParseException("Date is expired", 1);

        return new Timestamp(parsedDate.getTime());
    }

}
