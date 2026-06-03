package disk.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Yandex Disk API Tests")
@EnabledIfEnvironmentVariable(named = "YANDEX_DISK_TOKEN", matches = ".+")
class DiskApiTest extends BaseTest {

    // ---------------------------------------------------------- GET

    @Test
    @DisplayName("GET: корень диска возвращает 200 и тип dir")
    void getDiskRoot_returns200() {
        Response response = api.getResource("disk:/");

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("type"), equalTo("dir"));
        assertThat(response.jsonPath().getString("path"), equalTo("disk:/"));
    }

    @Test
    @DisplayName("GET: несуществующий ресурс возвращает 404")
    void getNonExistent_returns404() {
        Response response = api.getResource("disk:/no-such-folder-xyz-99999");

        assertThat(response.statusCode(), equalTo(404));
    }

    // ---------------------------------------------------------- PUT

    @Test
    @DisplayName("PUT: создание новой папки возвращает 201")
    void createFolder_returns201() {
        String path = "disk:/test-create-" + System.currentTimeMillis();

        Response response = api.createFolder(path);
        scheduleDelete(path);

        assertThat(response.statusCode(), equalTo(201));
        assertThat(response.jsonPath().getString("method"), equalTo("GET"));
        assertThat(response.jsonPath().getString("href"), not(emptyOrNullString()));
        assertThat(response.jsonPath().getBoolean("templated"), equalTo(false));
    }

    @Test
    @DisplayName("PUT: создание уже существующей папки возвращает 409 Conflict")
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
        assertThat(response.jsonPath().getString("method"), equalTo("GET"));
        assertThat(response.jsonPath().getString("href"), not(emptyOrNullString()));
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
    @DisplayName("DELETE: безвозвратное удаление папки возвращает 204")
    void deleteFolderPermanently_returns204() {
        String path = "disk:/test-delete-" + System.currentTimeMillis();
        api.createFolder(path);

        Response response = api.deleteResource(path);

        assertThat(response.statusCode(), equalTo(204));
    }

    @Test
    @DisplayName("DELETE: удаление в корзину (permanently=false) возвращает 204")
    void deleteFolderToTrash_returns204() {
        String path = "disk:/test-trash-" + System.currentTimeMillis();
        api.createFolder(path);

        Response response = api.deleteToTrash(path);

        assertThat(response.statusCode(), equalTo(204));
        api.emptyTrash();
    }

    @Test
    @DisplayName("DELETE: удаление несуществующего ресурса возвращает 404")
    void deleteNonExistent_returns404() {
        Response response = api.deleteResource("disk:/no-such-xyz-33333");

        assertThat(response.statusCode(), equalTo(404));
    }

    // --------------------------------------------------------- COPY

    @Test
    @DisplayName("POST copy: копирование папки возвращает 201")
    void copyFolder_returns201() {
        String original = "disk:/test-copy-src-" + System.currentTimeMillis();
        String copy     = "disk:/test-copy-dst-" + System.currentTimeMillis();
        createAndSchedule(original);
        scheduleDelete(copy);

        Response response = api.copyResource(original, copy);

        assertThat(response.statusCode(), equalTo(201));
        assertThat(response.jsonPath().getString("method"), equalTo("GET"));
        assertThat(response.jsonPath().getString("href"), not(emptyOrNullString()));
    }

    @Test
    @DisplayName("POST copy: копирование несуществующего ресурса возвращает 404")
    void copyNonExistent_returns404() {
        Response response = api.copyResource(
                "disk:/no-such-xyz-44444",
                "disk:/no-such-xyz-55555"
        );

        assertThat(response.statusCode(), equalTo(404));
    }

    // -------------------------------------------------- DISK INFO

    @Test
    @DisplayName("GET disk info: возвращает 200 и total_space")
    void getDiskInfo_returns200() {
        Response response = api.getDiskInfo();

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getLong("total_space"), greaterThan(0L));
        assertThat(response.jsonPath().getLong("used_space"), greaterThanOrEqualTo(0L));
    }

    // -------------------------------------------------- UPLOAD

    @Test
    @DisplayName("GET upload url: возвращает 200 и ссылку для загрузки")
    void getUploadUrl_returns200() {
        String path = "disk:/test-upload-" + System.currentTimeMillis() + ".txt";
        scheduleDelete(path);

        Response response = api.getUploadUrl(path);

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("href"), not(emptyOrNullString()));
        assertThat(response.jsonPath().getString("method"), equalTo("PUT"));
        assertThat(response.jsonPath().getString("operation_id"), not(emptyOrNullString()));
    }

    // ------------------------------------------------- DOWNLOAD

    @Test
    @DisplayName("GET download url: возвращает 200 и ссылку для скачивания")
    void getDownloadUrl_returns200() {
        String filePath = "disk:/test-download-" + System.currentTimeMillis() + ".txt";
        scheduleDelete(filePath);

        String uploadHref = api.getUploadUrl(filePath).jsonPath().getString("href");
        Response uploadResponse = api.uploadFile(uploadHref, "test content");

        assertThat(uploadResponse.statusCode(), equalTo(201));

        Response response = api.getDownloadUrl(filePath);

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("href"), not(emptyOrNullString()));
        assertThat(response.jsonPath().getString("method"), equalTo("GET"));
    }

    @Test
    @DisplayName("GET download url: несуществующий файл возвращает 404")
    void getDownloadUrlNonExistent_returns404() {
        Response response = api.getDownloadUrl("disk:/no-such-file-xyz-99999.txt");

        assertThat(response.statusCode(), equalTo(404));
    }

    // ---------------------------------------------------- TRASH

    @Test
    @DisplayName("DELETE trash: очистка корзины возвращает 204 или 202")
    void emptyTrash_returns204or202() {
        Response response = api.emptyTrash();

        assertThat(response.statusCode(), anyOf(equalTo(204), equalTo(202)));
    }

    // ---------------------------------------------------- PATCH

    @Test
    @DisplayName("PATCH: добавление custom_properties возвращает 200")
    void addCustomProperties_returns200() {
        String path = "disk:/test-patch-" + System.currentTimeMillis();
        createAndSchedule(path);

        Response response = api.addCustomProperties(path,
                "{\"custom_properties\": {\"foo\": \"bar\"}}");

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("custom_properties.foo"), equalTo("bar"));
        assertThat(response.jsonPath().getString("type"), equalTo("dir"));
    }

    @Test
    @DisplayName("PATCH: добавление атрибутов к несуществующему ресурсу возвращает 404")
    void addCustomPropertiesToNonExistent_returns404() {
        Response response = api.addCustomProperties(
                "disk:/no-such-xyz-66666",
                "{\"custom_properties\": {\"foo\": \"bar\"}}");

        assertThat(response.statusCode(), equalTo(404));
    }

    // ----------------------------------------------- PUBLISH

    @Test
    @DisplayName("PUT publish: публикация папки возвращает 200 и ссылку Link")
    void publishFolder_returns200() {
        String path = "disk:/test-publish-" + System.currentTimeMillis();
        createAndSchedule(path);

        Response response = api.publishResource(path);

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("method"), equalTo("GET"));
        assertThat(response.jsonPath().getString("href"), not(emptyOrNullString()));
    }

    @Test
    @DisplayName("GET: после публикации ресурс содержит public_url и public_key")
    void getPublishedResource_hasPublicFields() {
        String path = "disk:/test-public-meta-" + System.currentTimeMillis();
        createAndSchedule(path);
        api.publishResource(path);

        Response meta = api.getResource(path);

        assertThat(meta.statusCode(), equalTo(200));
        assertThat(meta.jsonPath().getString("public_url"), not(emptyOrNullString()));
        assertThat(meta.jsonPath().getString("public_key"), not(emptyOrNullString()));
    }

    @Test
    @DisplayName("PUT publish: публикация несуществующего ресурса возвращает 404")
    void publishNonExistent_returns404() {
        Response response = api.publishResource("disk:/no-such-xyz-77777");

        assertThat(response.statusCode(), equalTo(404));
    }

    // --------------------------------------------- UNPUBLISH

    @Test
    @DisplayName("PUT unpublish: закрытие доступа к папке возвращает 200")
    void unpublishFolder_returns200() {
        String path = "disk:/test-unpublish-" + System.currentTimeMillis();
        createAndSchedule(path);
        api.publishResource(path);

        Response response = api.unpublishResource(path);

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("method"), equalTo("GET"));
        assertThat(response.jsonPath().getString("href"), not(emptyOrNullString()));
    }

    @Test
    @DisplayName("GET: после unpublish public_url отсутствует")
    void getUnpublishedResource_hasNoPublicUrl() {
        String path = "disk:/test-unpublish-meta-" + System.currentTimeMillis();
        createAndSchedule(path);
        api.publishResource(path);
        api.unpublishResource(path);

        Response meta = api.getResource(path);

        assertThat(meta.statusCode(), equalTo(200));
        assertThat(meta.jsonPath().getString("public_url"), anyOf(nullValue(), emptyOrNullString()));
    }

    @Test
    @DisplayName("PUT unpublish: закрытие доступа к несуществующему ресурсу возвращает 404")
    void unpublishNonExistent_returns404() {
        Response response = api.unpublishResource("disk:/no-such-xyz-88888");

        assertThat(response.statusCode(), equalTo(404));
    }
}
