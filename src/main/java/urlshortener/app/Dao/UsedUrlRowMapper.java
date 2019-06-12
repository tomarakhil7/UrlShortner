package urlshortener.app.Dao;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import urlshortener.app.model.UsedUrl;


public class UsedUrlRowMapper implements RowMapper<UsedUrl> {
    @Override
    public UsedUrl mapRow(ResultSet rs, int arg1) throws SQLException {
        UsedUrl usedUrl = new UsedUrl();
        usedUrl.setLongUrl(rs.getString("long_url"));
        usedUrl.setShortUrl(rs.getString("short_url"));
        usedUrl.setId(rs.getInt("id"));
        usedUrl.setHits(rs.getInt("hits"));
        usedUrl.setExpiryDate(rs.getDate("expiry_date"));
        usedUrl.setCreatedAt(rs.getDate("created_on"));
        usedUrl.setCustom(rs.getBoolean("iscustom"));
        return usedUrl;
    }
}