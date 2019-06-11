package urlshortener.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import urlshortener.app.Dao.UnusedUrlDAO;
import urlshortener.app.Dao.UsedUrlDaoImpl;
import urlshortener.app.model.UnusedShortUrl;
import urlshortener.app.model.UsedUrl;

import java.util.Date;
import java.util.List;

@Service
public class URLConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLConverterService.class);
    private final UnusedUrlDAO unusedUrlDAO;
    private final UsedUrlDaoImpl usedUrlDao;

    @Autowired
    public URLConverterService(UsedUrlDaoImpl usedUrlDao, UnusedUrlDAO unusedUrlDAO) {
        this.unusedUrlDAO = unusedUrlDAO;
        this.usedUrlDao = usedUrlDao;

    }

    public String shortenURL(String localURL, String longUrl, Date expiryDate) {
        LOGGER.info("Shortening {}", longUrl);

        List<UnusedShortUrl> unusedShortUrls =unusedUrlDAO.getRandomUnsedShortUrl();
        UsedUrl usedUrl= new UsedUrl();
        usedUrl.setExpiryDate(expiryDate);
        usedUrl.setHits(1);
        usedUrl.setShortUrl(unusedShortUrls.get(0).getUnusedUrl());
        usedUrl.setLongUrl(longUrl);
        usedUrlDao.insertUsedUrl(usedUrl);
        unusedUrlDAO.deleteUnusedUrl(unusedShortUrls.get(0));
        return unusedShortUrls.get(0).getUnusedUrl();
    }

    public String getLongURLFromID(String uniqueID) throws Exception {
        UsedUrl usedUrl = new UsedUrl();
        usedUrl.setShortUrl("tiny/"+uniqueID);
        List<UsedUrl> usedUrlList= usedUrlDao.getUsedUrlByShortUrl(usedUrl);
        if(usedUrlList.size() == 0)
        {
            throw new Exception("No urls for this short url");
        }
        usedUrlDao.updateHitOfUsedUrl(usedUrlList.get(0));

        LOGGER.info("Converting shortened URL back to {}", usedUrlList.get(0));
        return usedUrlList.get(0).getLongUrl();
    }

}
