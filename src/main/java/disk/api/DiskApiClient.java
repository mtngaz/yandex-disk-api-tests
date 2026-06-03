package disk.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class DiskApiClient {

    public DiskApiClient() {}

    public Response getResource(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .when()
                .get(DiskApiConfig.RESOURCES);
    }

    public Response createFolder(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .when()
                .put(DiskApiConfig.RESOURCES);
    }

    public Response moveResource(String from, String to) {
        return authorizedRequest()
                .queryParam("from", from)
                .queryParam("path", to)
                .when()
                .post(DiskApiConfig.MOVE);
    }

    public Response copyResource(String from, String to) {
        return authorizedRequest()
                .queryParam("from", from)
                .queryParam("path", to)
                .when()
                .post(DiskApiConfig.COPY);
    }

    /** Удаление в корзину (permanently=false, значение по умолчанию в API). */
    public Response deleteToTrash(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .when()
                .delete(DiskApiConfig.RESOURCES);
    }

    /** Безвозвратное удаление (permanently=true). */
    public Response deleteResource(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .queryParam("permanently", true)
                .when()
                .delete(DiskApiConfig.RESOURCES);
    }

    public Response getDiskInfo() {
        return authorizedRequest()
                .when()
                .get(DiskApiConfig.BASE_URL);
    }

    public Response getUploadUrl(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .queryParam("overwrite", true)
                .when()
                .get(DiskApiConfig.UPLOAD);
    }

    public Response getDownloadUrl(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .when()
                .get(DiskApiConfig.DOWNLOAD);
    }

    public Response uploadFile(String uploadHref, String content) {
        return RestAssured.given()
                .body(content)
                .when()
                .put(uploadHref);
    }

    public Response emptyTrash() {
        return authorizedRequest()
                .when()
                .delete(DiskApiConfig.TRASH_RESOURCES);
    }

    public Response addCustomProperties(String path, String jsonBody) {
        return authorizedRequest()
                .queryParam("path", path)
                .body(jsonBody)
                .when()
                .patch(DiskApiConfig.RESOURCES);
    }

    public Response publishResource(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .when()
                .put(DiskApiConfig.PUBLISH);
    }

    public Response unpublishResource(String path) {
        return authorizedRequest()
                .queryParam("path", path)
                .when()
                .put(DiskApiConfig.UNPUBLISH);
    }

    public static Response getResourceWithoutAuth(String path) {
        return jsonRequest()
                .queryParam("path", path)
                .when()
                .get(DiskApiConfig.RESOURCES);
    }

    public static Response getUploadUrlWithoutAuth(String path) {
        return jsonRequest()
                .queryParam("path", path)
                .when()
                .get(DiskApiConfig.UPLOAD);
    }

    private RequestSpecification authorizedRequest() {
        return jsonRequest().header("Authorization", DiskApiConfig.authHeader());
    }

    private static RequestSpecification jsonRequest() {
        return RestAssured.given()
                .header("Accept", DiskApiConfig.ACCEPT_JSON)
                .header("Content-Type", DiskApiConfig.CONTENT_TYPE_JSON);
    }
}
