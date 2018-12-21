package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.HttpVerbs.POST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

public class LoginTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testIndex() {
        ObjectMapper mapper = new ObjectMapper();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/app/login")
                .bodyJson(mapper.createObjectNode().put("email", "ridvan_appa@hotmail.com").put("password", "admin"));

        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertTrue(result.header("Authorization").isPresent());
    }

}
