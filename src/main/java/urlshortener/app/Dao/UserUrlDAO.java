package urlshortener.app.Dao;
import java.util.List;
import urlshortener.app.model.UsedUrl;


public interface UserUrlDAO {
    List<UsedUrl> findAll();
    void insertUsedUrl(UsedUrl usedUrl);
    void updateHitOfUsedUrl(UsedUrl usedUrl);
    List<UsedUrl> getUsedUrlByShortUrl(UsedUrl usedUrl);
    List<UsedUrl> getUsedUrlByLongUrl(UsedUrl usedUrl);
    public void deleteUsedUrl(UsedUrl usedUrl);
}