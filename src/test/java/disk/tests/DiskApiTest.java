package disk.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Yandex Disk API Tests")
class DiskApiTest extends BaseTest {

    // ---------------------------------------------------------- GET

    @Test
    @DisplayName("GET: корень диска возвращает 200 и тип dir")
    void getDiskRoot_returns200() {
        Response response = api.getResource("disk:/");

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("type"), equalTo("dir"));
    }

    @Test
    @DisplayName("GET: несуществующий ресурс возвращает 404")
    void getNonExistent_returns404() {
        Response response = api.getResource("disk:/no-such-folder-xyz-99999");

        assertThat(response.statusCode(), equalTo(404));
    }

    @Test
    @DisplayName("GET: запрос без токена возвращает 401")
    void getWithoutToken_returns401() {
        Response response = io.restassured.RestAssured
                .given()
                .queryParam("path", "disk:/")
                .when()
                .get(disk.api.DiskApiConfig.RESOURCES);

        assertThat(response.statusCode(), equalTo(401));
    }

    // ---------------------------------------------------------- PUT

    @Test
    @DisplayName("PUT: создание новой папки возвращает 201")
    void createFolder_returns201() {
        String path = "disk:/test-create-" + System.currentTimeMillis();

        Response response = api.createFolder(path);
        scheduleDelete(path);

        assertThat(response.statusCode(), equalTo(201));
    }

    @Test
    @DisplayName("PUT: создание уже существующей папки возвращает 409")
    void createDuplicateFolder_returns409() {
        String path = "disk:/test-duplicate-" + System.currentTimeMillis();
        createAndSchedule(path);

        Response response = api.createFolder(path);

        assertThat(response.statusCode(), equalTo(409));
    }

    // ---------------------------------------------------------- POST

    @Test
    @DisplayName("POST: переименование папки возвращает 201")
    void moveFolder_returns201() {
        String original = "disk:/test-move-src-" + System.currentTimeMillis();
        String renamed  = "disk:/test-move-dst-" + System.currentTimeMillis();
        createAndSchedule(original);
        scheduleDelete(renamed);

        Response response = api.moveResource(original, renamed);

        assertThat(response.statusCode(), equalTo(201));
    }

    @Test
    @DisplayName("POST: перемещение несуществующего ресурса возвращает 404")
    void moveNonExistent_returns404() {
        Response response = api.moveResource(
                "disk:/no-such-xyz-11111",
                "disk:/no-such-xyz-22222"
        );

        assertThat(response.statusCode(), equalTo(404));
    }

    // --------------------------------------------------------- DELETE

    @Test
    @DisplayName("DELETE: удаление существующей папки возвращает 204")
    void deleteFolder_returns204() {
        String path = "disk:/test-delete-" + System.currentTimeMillis();
        api.createFolder(path);

        Response response = api.deleteResource(path);

        assertThat(response.statusCode(), equalTo(204));
    }

    @Test
    @DisplayName("DELETE: удаление несуществующего ресурса возвращает 404")
    void deleteNonExistent_returns404() {
        Response response = api.deleteResource("disk:/no-such-xyz-33333");

        assertThat(response.statusCode(), equalTo(404));
    }
}