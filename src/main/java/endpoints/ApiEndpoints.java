package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utilities.Base;

import static io.restassured.RestAssured.given;

public class ApiEndpoints extends Base {

    public static Response requestName(RequestSpecification reqSpec, String reqBody) {

        return given()
                .spec(reqSpec)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(reqBody)

                .when()
                .post(Routes.URL1);

    }

}
