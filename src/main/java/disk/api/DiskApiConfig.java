package disk.api;

public final class DiskApiConfig {
    public static final String BASE_URL = "https://cloud-api.yandex.net/v1/disk";
    public static final String RESOURCES = BASE_URL + "/resources";
    public static final String MOVE = RESOURCES + "/move";
    public static final String COPY = RESOURCES + "/copy";
    public static final String UPLOAD = RESOURCES + "/upload";
    public static final String DOWNLOAD = RESOURCES + "/download";
    public static final String PUBLISH = RESOURCES + "/publish";
    public static final String UNPUBLISH = RESOURCES + "/unpublish";
    public static final String TRASH_RESOURCES = BASE_URL + "/trash/resources";

    public static final String ACCEPT_JSON = "application/json";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static String getToken() {
        String token = System.getenv("YANDEX_DISK_TOKEN");
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Переменная YANDEX_DISK_TOKEN не задана. " +
                    "См. README.md — раздел «Получение OAuth-токена»."
            );
        }
        return token;
    }

    public static String authHeader() {
        return "OAuth " + getToken();
    }

    private DiskApiConfig() {}
}
