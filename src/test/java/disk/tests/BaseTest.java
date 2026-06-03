package disk.tests;

import disk.api.DiskApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTest {
    protected DiskApiClient api;
    private final List<String> toDelete = new ArrayList<>();

    @BeforeEach
    void setUp() {
        api = new DiskApiClient();
    }

    @AfterEach
    void tearDown() {
        for (int i = toDelete.size() - 1; i >= 0; i--) {
            try {
                api.deleteResource(toDelete.get(i));
            } catch (Exception ignored) {}
        }
        toDelete.clear();
    }

    protected void scheduleDelete(String path) {
        toDelete.add(path);
    }

    protected void createAndSchedule(String path) {
        api.createFolder(path);
        scheduleDelete(path);
    }
}
