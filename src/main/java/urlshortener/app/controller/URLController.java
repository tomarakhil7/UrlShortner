package urlshortener.app.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import urlshortener.app.common.URLValidator;
import urlshortener.app.service.URLConverterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
public class URLController {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLController.class);
    private final URLConverterService urlConverterService;

    public URLController(URLConverterService urlConverterService) {
        this.urlConverterService = urlConverterService;
    }

    @RequestMapping(value = "/shortener", method = RequestMethod.POST, consumes = {"application/json"})
    public String shortenUrl(@RequestBody @Valid final ShortenRequest shortenRequest, HttpServletRequest request) throws Exception {
        LOGGER.info("Received url to shorten: " + shortenRequest.getUrl());
        String longUrl = shortenRequest.getUrl();
        if (URLValidator.INSTANCE.validateURL(longUrl)) {
            String shortenedUrl = urlConverterService.shortenURL(shortenRequest.getUrl(), shortenRequest.getExpiryDate());
            LOGGER.info("Shortened url to: " + shortenedUrl);
            return shortenedUrl;
        }
        throw new Exception("Please enter a valid URL");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RedirectView redirectUrl(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException, Exception {
        LOGGER.info("Received shortened url to redirect: " + id);
        String redirectUrlString = urlConverterService.getLongURLFromID(id);
        LOGGER.info("Original URL: " + redirectUrlString);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://" + redirectUrlString);
        return redirectView;
    }

    @RequestMapping(value = "/customShortener", method = RequestMethod.POST)
    public String shortenWithCustomUrl(HttpServletRequest request, @RequestBody @Valid final CustomizableRequest customizableRequest) throws Exception {
        LOGGER.info("Received url to shorten: " + customizableRequest.getUrl() + " and customizable url: " + customizableRequest.getCustomizableUrl());
        String longUrl = customizableRequest.getUrl();
        if (URLValidator.INSTANCE.validateURL(longUrl)) {
            String shortenUrl = urlConverterService.shortenCustomURL(longUrl, customizableRequest.getExpiryDate(), customizableRequest.getCustomizableUrl());
            LOGGER.info("Shortened url to: " + shortenUrl);
            return shortenUrl;

        }
        throw new Exception("Please enter a valid URL");
    }

    @RequestMapping(value = "/c", method = RequestMethod.POST)
    public RedirectView redirectCustomUrl(@RequestBody @Valid final RedirectUrl redirectUrl,HttpServletRequest request) throws Exception {
        LOGGER.info("Received url to redirect: "+ redirectUrl.getUrl());
        String redirectUrlString = urlConverterService.getCustomLongURL(redirectUrl.getUrl());
        LOGGER.info("Original URL: " + redirectUrlString);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://" + redirectUrlString);
        return redirectView;
    }
}

class ShortenRequest {
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private String url;
    private Date expiryDate;

    @JsonCreator
    public ShortenRequest() {

    }

    @JsonCreator
    public ShortenRequest(@JsonProperty("url") String url, @JsonProperty("expiryDate") String expiryDate) throws ParseException {
        this.url = url;
        this.expiryDate = getDateFromString(expiryDate);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateFromString(String expiryDate) throws ParseException {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.parse(expiryDate);
    }
}
class RedirectUrl {
    private String url;

    @JsonCreator
    public void redirectUrl() {

    }

    @JsonCreator
    public void  redirectUrl(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

class CustomizableRequest {
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private String url;
    private Date expiryDate;
    private String customizableUrl;

    public String getCustomizableUrl() {
        return customizableUrl;
    }

    public void setCustomizableUrl(String customizableUrl) {
        this.customizableUrl = customizableUrl;
    }

    @JsonCreator
    public CustomizableRequest() {

    }

    @JsonCreator
    public CustomizableRequest(@JsonProperty("url") String url, @JsonProperty("expiryDate") String expiryDate, @JsonProperty("customizableUrl") String customizableUrl) throws ParseException {
        this.url = url;
        this.expiryDate = getDateFromString(expiryDate);
        this.customizableUrl = customizableUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateFromString(String expiryDate) throws ParseException {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.parse(expiryDate);
    }
}


