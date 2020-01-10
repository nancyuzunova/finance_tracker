package ittalents.javaee.model.dao;

import ittalents.javaee.model.pojo.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class TransactionDao {

    private static final String GET_EXPENSES_AND_INCOMES_BY_DAYS = "SELECT t.date, t. type, SUM(t.amount) AS total " +
            "FROM transactions AS t " +
            "JOIN accounts AS a ON t.account_id = a.id " +
            "WHERE a.user_id = ? AND t.date BETWEEN ? AND ? " +
            "GROUP BY t.date, t.type " +
            "ORDER BY t.date";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public Map<LocalDate, Map<Type, Double>> getDailyTransactions(long id, Date from, Date to) throws SQLException {
        Map<LocalDate, Map<Type, Double>> result = new TreeMap<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(GET_EXPENSES_AND_INCOMES_BY_DAYS)){
            statement.setLong(1, id);
            statement.setObject(2, from);
            statement.setObject(3, to);
            ResultSet set = statement.executeQuery();
            while(set.next()){
                java.sql.Date day = set.getDate("date");
                result.put(day.toLocalDate(), new HashMap<>());
                result.get(day.toLocalDate()).put(Type.valueOf(set.getString("type")), set.getDouble("total"));
            }
        }
        return result;
    }
}
