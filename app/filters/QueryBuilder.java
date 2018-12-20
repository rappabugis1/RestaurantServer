package filters;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.Ebean;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.Reservation;
import models.Restaurant;
import models.Table;
import play.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryBuilder {

    private ExpressionList<Restaurant> query = Restaurant.getFinder().query().alias("main").fetch("categoryList").fetch("reviewList").where();

    private void finishQuery(int itemsPerPage, int pageNumber) {
        query.setFirstRow(itemsPerPage * (pageNumber - 1))
                .setMaxRows(itemsPerPage);
    }

    private ExpressionList<Restaurant> getQuery(JsonNode json) throws ParseException {

        //Search parameters
        int itemsPerPage = json.get("itemsPerPage").asInt();
        int pageNumber = json.get("pageNumber").asInt();
        JsonNode searchTextNode = json.get("searchText");


        //Add searchText parameter to filter if exists
        if (!searchTextNode.isNull()) {
            String searchText = searchTextNode.asText();
            query
                    .or()
                    .icontains("restaurant_name", searchText)
                    .icontains("categoryList.name", searchText)
                    .icontains("location.name", searchText)
                    .endOr();
        }
        try{
            JsonNode reservationInfoNode = json.get("reservationInfo");
            JsonNode priceRange = json.get("priceRange");
            JsonNode mark = json.get("mark");
            JsonNode categories = json.get("categories");




            if (!priceRange.isNull() && priceRange.asInt() != 0) {
                int priceFilter = priceRange.asInt();
                query.eq("price_range", priceFilter);
            }

            if (!mark.isNull() && mark.asInt() != 0) {

                query.eq("round((Select avg(reviews.mark) From reviews where restaurant_id=main.id),0)", mark.asInt());
            }

            if(json.has("geometry") && !json.get("geometry").isNull()){
                query.raw("ST_DWithin(main.point,ST_MakePoint(?, ?)::geography, ?)",json.get("geometry").get("longitude").asDouble(),json.get("geometry").get("latitude").asDouble(), json.get("geometry").get("radius").asDouble());
            }


            //Add reservation parameters to filter if exists
            if (!reservationInfoNode.isNull()) {

                Timestamp reservationDateTime = getStampFromDate(reservationInfoNode.get("reservationDate").asText(), reservationInfoNode.get("reservationHour").asText());

                query.exists(Ebean.createQuery(Table.class).alias("t").where().raw("t.restaurant_id=main.id").ge("t.sitting_places", reservationInfoNode.get("persons").asInt()).notExists(Ebean.createQuery(Reservation.class).alias("r").where().raw("r.table_id=t.id").betweenProperties("r.reservation_date_time", "r.reservation_end_date_time", reservationDateTime).query()).query());

            }

            if (categories.isArray() && categories.size() > 0) {
                for (JsonNode catName : categories
                ) {
                    query.exists(Ebean.createQuery(Restaurant.class).where().in("categoryList.name", catName.asText()).raw("t0.id=main.id").query());
                }

            }
        } catch (Exception e){

        }


        finishQuery(itemsPerPage, pageNumber);


        return query;
    }

    public PagedList<Restaurant> executeQuery(JsonNode json) throws ParseException {
        return getQuery(json).findPagedList();
    }

    private Timestamp getStampFromDate(String reservationDate, String reservationHour) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = dateFormat.parse(reservationDate + " " + reservationHour);

        if (parsedDate.before(new Date()))
            throw new ParseException("Date is expired", 1);

        return new Timestamp(parsedDate.getTime());
    }

}
