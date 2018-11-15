package filters;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.*;
import models.Restaurant;
import play.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryBuilder {

    private ExpressionList<Restaurant> query= Restaurant.getFinder().query().alias("main").fetch("categories").where();

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
        JsonNode priceRange = json.get("priceRange");
        JsonNode mark= json.get("mark");
        JsonNode categories = json.get("categories");


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

        if(  !priceRange.isNull()){
            int priceFilter = priceRange.asInt();
            query.eq("price_range", priceFilter);
        }

        if(!mark.isNull()){
            int markFilter = mark.asInt();
            query.eq("mark", markFilter);
        }


        //Add reservation parameters to filter if exists
        if(!reservationInfoNode.isNull()){

            Timestamp reservationDateTime=getStampFromDate(reservationInfoNode.get("reservationDate").asText(), reservationInfoNode.get("reservationHour").asText());
            Logger.info(reservationDateTime.toString());

            int lengthOfStay= reservationInfoNode.get("lengthOfStay").asInt();

            //Get reservation end time
            Timestamp reservationEnd = new Timestamp(reservationDateTime.getTime() + (lengthOfStay*60)*1000);
            Logger.info(reservationEnd.toString());


            query
                    .ge("tables.sitting_places", reservationInfoNode.get("persons").asInt())
                    .or()
                    .isEmpty("reservationList")
                    .not()
                    .betweenProperties("reservationList.reservationDateTime","reservationList.reservationEndDateTime", reservationDateTime)
                    .endNot()
                    .endOr();
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
