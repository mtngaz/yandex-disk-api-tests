package disk.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DiskApiClient {
    private final String authHeader;

    public DiskApiClient() {
        this.authHeader = DiskApiConfig.authHeader();
    }

    public Response getResource(String path) {
        return RestAssured.given()
                .header("Authorization", authHeader)
                .queryParam("path", path)
                .when()
                .get(DiskApiConfig.RESOURCES);
    }

    // PUT — создать папку
    public Response createFolder(String path) {
        return RestAssured.given()
                .header("Authorization", authHeader)
                .queryParam("path", path)
                .when()
                .put(DiskApiConfig.RESOURCES);
    }

    // POST — переместить/переименовать ресурс
    public Response moveResource(String from, String to) {
        return RestAssured.given()
                .header("Authorization", authHeader)
                .queryParam("from", from)
                .queryParam("path", to)
                .when()
                .post(DiskApiConfig.MOVE);
    }

    // DELETE — удалить ресурс навсегда
    public Response deleteResource(String path) {
        return RestAssured.given()
                .header("Authorization", authHeader)
                .queryParam("path", path)
                .queryParam("permanently", true)
                .when()
                .delete(DiskApiConfig.RESOURCES);
    }
}
