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

    public String shortenURL(String longUrl, Date expiryDate) {
        LOGGER.info("Shortening {}", longUrl);

        List<UnusedShortUrl> unusedShortUrls = unusedUrlDAO.getRandomUnsedShortUrl();
        UsedUrl usedUrl = new UsedUrl();
        usedUrl.setExpiryDate(expiryDate);
        usedUrl.setHits(1);
        usedUrl.setShortUrl(unusedShortUrls.get(0).getUnusedUrl());
        usedUrl.setLongUrl(longUrl);
        usedUrl.setCustom(false);
        usedUrlDao.insertUsedUrl(usedUrl);
        unusedUrlDAO.deleteUnusedUrl(unusedShortUrls.get(0));
        return unusedShortUrls.get(0).getUnusedUrl();
    }

    public String shortenCustomURL(String longUrl, Date expiryDate, String customUrl) {
        LOGGER.info("Shortening {}", longUrl);

        UsedUrl usedUrl = new UsedUrl();
        usedUrl.setExpiryDate(expiryDate);
        usedUrl.setHits(1);
        usedUrl.setShortUrl(customUrl);
        usedUrl.setLongUrl(longUrl);
        usedUrl.setCustom(false);
        usedUrlDao.insertUsedUrl(usedUrl);
        return customUrl;
    }

    public String getLongURLFromID(String uniqueID) throws Exception {
        UsedUrl usedUrl = new UsedUrl();
        usedUrl.setShortUrl( uniqueID);
        List<UsedUrl> usedUrlList = usedUrlDao.getUsedUrlByShortUrl(usedUrl);
        if (usedUrlList.size() == 0) {
            throw new Exception("No urls for this short url");
        }
        usedUrlDao.updateHitOfUsedUrl(usedUrlList.get(0));

        LOGGER.info("Converting shortened URL back to {}", usedUrlList.get(0));
        return usedUrlList.get(0).getLongUrl();
    }

    public String getCustomLongURL (String url) throws Exception {
        final String[] longUrl = new String[1];
        List<UsedUrl> usedUrlList= usedUrlDao.getAllCustomUrls();
        LOGGER.info("usedUrl: "+usedUrlList.size());

        usedUrlList.forEach(usedUrl -> {
            LOGGER.debug("usedUrl: "+usedUrl.getShortUrl());
            if(getMatchingUrl(usedUrl,url))
            {
                 longUrl[0] =generateLongUrl(url,usedUrl);
            }
        });
        if(longUrl[0]==null)
            throw new Exception("No matching found");
        return longUrl[0];
    }

    public Boolean getMatchingUrl(UsedUrl usedUrl, String receivedUrl){

        String [] usedUrlArray =  usedUrl.getShortUrl().split("/");
        String [] urlArray = receivedUrl.split("/");
        LOGGER.info("Array for urlArray :"+ urlArray.toString());
        LOGGER.info("Array for usedUrlArray :"+ usedUrlArray.toString());


        if(urlArray.length != usedUrlArray.length)
            return false;
        for(int i =0;i<usedUrlArray.length;i++){
            if(!usedUrlArray[i].matches("<>"))
            {
                if(!usedUrlArray[i].matches(urlArray[i]))
                    return false;
            }
        }
        return true;
    }

    public String[] getVariablesFromRecievedUrl(UsedUrl usedUrl, String receivedUrl){
        String [] usedUrlArray =  usedUrl.getShortUrl().split("/");
        String [] urlArray = receivedUrl.split("/");
        StringBuilder stringBuilder = new StringBuilder();
        for(int i =0;i<usedUrlArray.length;i++){
            if(usedUrlArray[i].matches("<>"))
            {
                stringBuilder.append(" "+urlArray[i]);
            }
        }
        LOGGER.info("generate long url: "+ stringBuilder.deleteCharAt(0));

        return stringBuilder.toString().split(" ");
    }

    public String generateLongUrl(String receivedUrl,UsedUrl usedUrl){
        String longUrl=usedUrl.getLongUrl();
        String[] variables = getVariablesFromRecievedUrl(usedUrl,receivedUrl);
        StringBuilder stringBuilder = new StringBuilder(longUrl);
        LOGGER.info("variable length :" + variables.length);

        int lastIndex=0;
        for(int i =0;i<variables.length;i++){
            int nextindex=  stringBuilder.indexOf("<>",lastIndex);
            stringBuilder.deleteCharAt(nextindex);
            stringBuilder.deleteCharAt(nextindex);
            stringBuilder.insert(nextindex,variables[i]);
            lastIndex= nextindex;

            LOGGER.info("generate long url: "+ stringBuilder + " "+lastIndex);
        }
        return stringBuilder.toString();
    }

    public Integer getHits(String url) {
        return usedUrlDao.getHits(url).get(0).getHits();
    }
}
