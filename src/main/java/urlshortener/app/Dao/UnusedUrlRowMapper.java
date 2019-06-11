package urlshortener.app.Dao;

import org.springframework.jdbc.core.RowMapper;
import urlshortener.app.model.UnusedShortUrl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UnusedUrlRowMapper implements RowMapper<UnusedShortUrl> {

    @Override
    public UnusedShortUrl mapRow(ResultSet rs, int arg1) throws SQLException {
        UnusedShortUrl unusedShortUrl = new UnusedShortUrl(rs.getString("unused_url"));
        unusedShortUrl.setId(rs.getInt("id"));
        return unusedShortUrl;
    }

}
