package disk.api;

public final class DiskApiConfig {
    public static final String BASE_URL = "https://cloud-api.yandex.net/v1/disk";
    public static final String RESOURCES = BASE_URL + "/resources";
    public static final String MOVE = RESOURCES + "/move";

    public static String getToken() {
        String token = System.getenv("YANDEX_DISK_TOKEN");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Переменная YANDEX_DISK_TOKEN не задана. " +
                    "Выполни: export YANDEX_DISK_TOKEN=твой_токен"
            );
        }
        return token;
    }

    public static String authHeader() {
        return "OAuth " + getToken();
    }

    private DiskApiConfig() {};
}
