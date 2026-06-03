package disk.tests;

import disk.api.DiskApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class BaseTest {
    protected DiskApiClient api;
    private final List<String> toDelete = new ArrayList<>();

    @BeforeEach
    void setUp() {
        String token = System.getenv("YANDEX_DISK_TOKEN");
        assumeTrue(token != null && !token.isBlank(),
                "Пропуск: задайте YANDEX_DISK_TOKEN (см. README.md)");
        api = new DiskApiClient();
    }

    @AfterEach
    void tearDown() {
        for (int i = toDelete.size() - 1; i >= 0; i--) {
            try {
                api.deleteResource(toDelete.get(i));
            } catch (Exception ignored) {
                // ресурс уже удалён в тесте
            }
        }
        toDelete.clear();
    }

    protected void scheduleDelete(String path) {
        toDelete.add(path);
    }

    protected void createAndSchedule(String path) {
        assertThat(api.createFolder(path).statusCode(), equalTo(201));
        scheduleDelete(path);
    }
}
