package urlshortener.app.model;

public class UnusedShortUrl {
    String unusedUrl;
    Integer id;

    public UnusedShortUrl(String unusedUrl) {
        this.unusedUrl = unusedUrl;
    }

    public String getUnusedUrl() {
        return unusedUrl;
    }

    public void setUnusedUrl(String unusedUrl) {
        this.unusedUrl = unusedUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
