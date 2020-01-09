package ittalents.javaee.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class IconDao {

    private static final String GET_ICONS_URLS_BY_CATEGORY_ID = "SELECT url FROM icons WHERE category_id = ?;";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getIconsUrlsByCategoryId(long categoryId) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        List<String> urls = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_ICONS_URLS_BY_CATEGORY_ID)) {
            statement.setLong(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                urls.add("/" + resultSet.getString("url"));
            }
        }
        return urls;
    }
}
