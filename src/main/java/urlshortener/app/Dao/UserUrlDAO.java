package urlshortener.app.Dao;

import urlshortener.app.model.UsedUrl;

import java.util.List;


public interface UserUrlDAO {
    List<UsedUrl> findAll();

    void insertUsedUrl(UsedUrl usedUrl);

    void updateHitOfUsedUrl(UsedUrl usedUrl);

    List<UsedUrl> getUsedUrlByShortUrl(UsedUrl usedUrl);

    List<UsedUrl> getUsedUrlByLongUrl(UsedUrl usedUrl);

    void deleteUsedUrl(UsedUrl usedUrl);

    List<UsedUrl> getAllExpiredUsedIUrl();

    List<UsedUrl> getAllCustomUrls();

    List<UsedUrl> getHits(String shortUrl);

}