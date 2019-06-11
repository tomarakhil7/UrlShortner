package urlshortener.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener.app.Dao.UnusedUrlDAO;
import urlshortener.app.Dao.UsedUrlDaoImpl;
import urlshortener.app.common.IDConverter;
import urlshortener.app.model.UnusedShortUrl;
import urlshortener.app.model.UsedUrl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.LongStream;

@Component
public class UrlPopulatorCron {

    private static final Logger logger = LoggerFactory.getLogger(UrlPopulatorCron.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final UnusedUrlDAO unusedUrlDAO;
    private final UsedUrlDaoImpl usedUrlDao;

    @Autowired
    public UrlPopulatorCron(UnusedUrlDAO unusedUrlDAO, UsedUrlDaoImpl usedUrlDao) {
        this.unusedUrlDAO = unusedUrlDAO;
        this.usedUrlDao = usedUrlDao;
    }

    @Scheduled(cron = "* * * ? * *")
    public void freeExpiredShortUrls() {
        logger.info("Free Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        List<UsedUrl> usedUrlList = usedUrlDao.getAllExpiredUsedIUrl();
        usedUrlList.forEach(usedUrl -> {
            unusedUrlDAO.insertUnusedShortUrl(new UnusedShortUrl(usedUrl.getShortUrl()));
        });
    }

    @Scheduled(cron = "* * * ? * *")
    public void populateNewShortUrls() {
        logger.info("Populate Cron Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        List<UnusedShortUrl> unusedShortUrls = unusedUrlDAO.geLastUnsedShortUrl();
        Integer lastID = unusedShortUrls.get(0).getId();
        LongStream.range(1, 10).forEach(x -> {
            String uniqueID = IDConverter.INSTANCE.createUniqueID(lastID + x);
            unusedUrlDAO.insertUnusedShortUrl(new UnusedShortUrl("tiny/" + uniqueID));
        });


    }
}
