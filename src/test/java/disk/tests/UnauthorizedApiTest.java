package disk.tests;

import disk.api.DiskApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("Yandex Disk API — авторизация")
class UnauthorizedApiTest {

    @Test
    @DisplayName("GET resources: без токена возвращает 401")
    void getWithoutToken_returns401() {
        Response response = DiskApiClient.getResourceWithoutAuth("disk:/");

        assertThat(response.statusCode(), equalTo(401));
    }

    @Test
    @DisplayName("GET upload url: без токена возвращает 401")
    void getUploadUrlWithoutToken_returns401() {
        Response response = DiskApiClient.getUploadUrlWithoutAuth("disk:/test.txt");

        assertThat(response.statusCode(), equalTo(401));
    }
}
