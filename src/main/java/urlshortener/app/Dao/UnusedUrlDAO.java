package urlshortener.app.Dao;

import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import urlshortener.app.model.UnusedShortUrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UnusedUrlDAO {
    public UnusedUrlDAO(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    NamedParameterJdbcTemplate template;

    public List<UnusedShortUrl> findAll() {
        return template.query("select * from unused_short_url", new UnusedUrlRowMapper());
    }


    public void insertUnusedShortUrl(UnusedShortUrl unusedShortUrl) {
        final String sql = "insert into unused_short_url( unused_url) values(:unused_url)";
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("unused_url", unusedShortUrl.getUnusedUrl());
        template.update(sql, param, holder);
    }


    @Transactional
    public List<UnusedShortUrl> getRandomUnsedShortUrl() {
        final String sql = "select * from unused_short_url  order by RANDOM() LIMIT 1";
        SqlParameterSource param = new MapSqlParameterSource();
        return template.query(sql, param, new UnusedUrlRowMapper());
    }

    public List<UnusedShortUrl> geLastUnsedShortUrl() {
        final String sql = "select * from unused_short_url  order by id desc LIMIT 1";
        SqlParameterSource param = new MapSqlParameterSource();
        return template.query(sql, param, new UnusedUrlRowMapper());
    }

    @Transactional
    public void deleteUnusedUrl(UnusedShortUrl unusedShortUrl) {
        final String sql = "delete from unused_short_url where id=:id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", unusedShortUrl.getId());
        template.execute(sql, map, (PreparedStatementCallback<Object>) ps -> ps.executeUpdate());
    }
}
