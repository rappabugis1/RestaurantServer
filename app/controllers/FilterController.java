package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import filters.QueryBuilder;
import io.ebean.PagedList;
import play.mvc.Controller;
import play.mvc.Result;

public class FilterController extends Controller {


    public Result getRestaurantsByFilter() {
        JsonNode json = request().body().asJson();

        if (json == null)
            return badRequest("Json is null");

        try {
            PagedList result = new QueryBuilder().executeQuery(json);


            ObjectNode returnNode = (new ObjectMapper()).createObjectNode();
            returnNode.put("numberOfPages", result.getTotalPageCount());


            returnNode.putArray("restaurants").addAll((ArrayNode) (new ObjectMapper()).valueToTree(result.getList()));

            return ok((new ObjectMapper()).writeValueAsString(returnNode));

        } catch (Exception e) {
            return badRequest(e.getMessage());
        }
    }
}
