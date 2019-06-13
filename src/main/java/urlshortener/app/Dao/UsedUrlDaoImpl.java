package urlshortener.app.Dao;


import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import urlshortener.app.model.UsedUrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UsedUrlDaoImpl implements UserUrlDAO {


    public UsedUrlDaoImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    NamedParameterJdbcTemplate template;

    @Override
    public List<UsedUrl> findAll() {
        return template.query("select * from used_url", new UsedUrlRowMapper());
    }

    @Override
    public void insertUsedUrl(UsedUrl usedUrl) {
        final String sql = "insert into used_url(long_url, short_url ,expiry_date,hits,iscustom) values(:long_url,:short_url,:expiry_date,:hits,:iscustom)";
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("long_url", usedUrl.getLongUrl())
                .addValue("short_url", usedUrl.getShortUrl())
                .addValue("expiry_date", usedUrl.getExpiryDate())
                .addValue("hits", usedUrl.getHits())
                .addValue("iscustom",usedUrl.getCustom());

        template.update(sql, param, holder);
    }

    @Override
    public void updateHitOfUsedUrl(UsedUrl usedUrl) {
        final String sql = "update used_url set hits=:hits where id=:id";
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", usedUrl.getId())
                .addValue("hits", usedUrl.getHits()+1);
        template.update(sql, param, holder);


    }

    @Override
    public List<UsedUrl> getUsedUrlByShortUrl(UsedUrl usedUrl) {
        final String sql = "select * from used_url  where short_url=:short_url and expiry_date > now()";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("short_url", usedUrl.getShortUrl());
        return template.query(sql, param, new UsedUrlRowMapper());
    }

    @Override
    public List<UsedUrl> getUsedUrlByLongUrl(UsedUrl usedUrl) {
        final String sql = "select * from used_url  where long_url=:long_url";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("long_url", usedUrl.getLongUrl());
        return template.query(sql, param, new UsedUrlRowMapper());
    }

    @Override
    public void deleteUsedUrl(UsedUrl usedUrl) {
        final String sql = "delete from used_url where id=:id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", usedUrl.getId());
        template.execute(sql, map, (PreparedStatementCallback<Object>) ps -> ps.executeUpdate());
    }

    public List<UsedUrl> getAllExpiredUsedIUrl() {
        final String sql = "select * from used_url where expiry_date < now() and iscustom = false";
        return template.query(sql, new UsedUrlRowMapper());

    }

    public List<UsedUrl> getAllCustomUrls() {
        final String sql = "select * from used_url where expiry_date > now() and iscustom = true";
        return template.query(sql, new UsedUrlRowMapper());
    }

    public List<UsedUrl> getHits(String shortUrl) {
        final String sql = "select * from used_url where expiry_date > now() and short_url=:short_url";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("short_url", shortUrl);
        return template.query(sql,param, new UsedUrlRowMapper());
    }
}
