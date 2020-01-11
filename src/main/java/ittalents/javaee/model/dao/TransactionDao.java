package ittalents.javaee.model.dao;

import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.model.dto.ResponseTransactionDto;
import ittalents.javaee.model.pojo.Category;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.model.pojo.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class TransactionDao {

    private static final String GET_EXPENSES_AND_INCOMES_BY_DAYS = "SELECT t.date, t. type, SUM(t.amount) AS total " +
            "FROM transactions AS t " +
            "JOIN accounts AS a ON t.account_id = a.id " +
            "WHERE a.user_id = ? AND t.date BETWEEN ? AND ? " +
            "GROUP BY t.date, t.type " +
            "ORDER BY t.date";

    private static final String GET_MY_TRANSACTIONS = "SELECT t.id, t.amount, t.currency, t.type, t.description, t.date, " +
            "a.id AS account_id, a.name, a.balance, a.currency AS account_currency, " +
            "c.id AS category_id, c.name AS category, c.iconurl, c.type AS cat_type " +
            "FROM transactions AS t " +
            "JOIN accounts AS a " +
            "ON t.account_id = a.id " +
            "JOIN categories AS c " +
            "ON t.category_id = c.id " +
            "WHERE a.user_id = ?;";

    private static final String GET_MY_TRANSACTIONS_BY_TYPE = "SELECT t.id, t.amount, t.currency, t.type, t.description, t.date, " +
            "            a.id AS account_id, a.name, a.balance, a.currency AS account_currency, " +
            "            c.id AS category_id, c.name AS category, c.iconurl, c.type AS cat_type " +
            "            FROM transactions AS t " +
            "            JOIN accounts AS a " +
            "            ON t.account_id = a.id " +
            "            JOIN categories AS c " +
            "            ON t.category_id = c.id " +
            "            WHERE a.user_id = ? AND t.type = ?;";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AllArgsConstructor
    @Getter
    @Setter
    public
    class StatisticEntity {
        private Date date;
        private Type type;
        private double total;
    }

    public List<StatisticEntity> getDailyTransactions(long id, Date from, Date to) throws SQLException {
        List<StatisticEntity> result = new ArrayList<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GET_EXPENSES_AND_INCOMES_BY_DAYS)) {
            statement.setLong(1, id);
            statement.setObject(2, from);
            statement.setObject(3, to);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                StatisticEntity entity = new StatisticEntity(set.getDate("date"),
                        Type.valueOf(set.getString("type")), set.getDouble("total"));
                result.add(entity);
            }
            set.close();
        }
        return result;
    }

    public List<ResponseTransactionDto> getMyTransactions(long userId) throws SQLException {
        List<ResponseTransactionDto> responseTransactionDtos = new ArrayList<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GET_MY_TRANSACTIONS)) {
            statement.setLong(1, userId);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ResponseTransactionDto transactionDto = new ResponseTransactionDto();
                transactionDto.setId(set.getLong("id"));
                transactionDto.setDescription(set.getString("description"));
                transactionDto.setDate(set.getDate("date"));
                transactionDto.setAmount(set.getDouble("amount"));
                transactionDto.setCurrency(Currency.valueOf(set.getString("currency")));
                transactionDto.setType(Type.valueOf(set.getString("type")));
                AccountDto accountDto = new AccountDto();
                accountDto.setId(set.getLong("account_id"));
                accountDto.setName(set.getString("name"));
                accountDto.setCurrency(Currency.valueOf(set.getString("account_currency")));
                accountDto.setBalance(set.getDouble("balance"));
                transactionDto.setAccount(accountDto);
                CategoryDto categoryDto = new CategoryDto();
                categoryDto.setId(set.getLong("category_id"));
                categoryDto.setCategoryName(Category.CategoryName.valueOf(set.getString("category")));
                categoryDto.setType(Type.valueOf(set.getString("cat_type")));
                categoryDto.setIconURL(set.getString("iconurl"));
                transactionDto.setCategory(categoryDto);
                responseTransactionDtos.add(transactionDto);
            }
            set.close();
        }
        return responseTransactionDtos;
    }

    public List<ResponseTransactionDto> getAllTransactionsByType(long userId, Type type) throws SQLException {
        List<ResponseTransactionDto> response = new ArrayList<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GET_MY_TRANSACTIONS_BY_TYPE)) {
            statement.setLong(1, userId);
            statement.setString(2, type.toString());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                ResponseTransactionDto transactionDto = new ResponseTransactionDto();
                transactionDto.setId(set.getLong("id"));
                transactionDto.setDescription(set.getString("description"));
                transactionDto.setDate(set.getDate("date"));
                transactionDto.setAmount(set.getDouble("amount"));
                transactionDto.setCurrency(Currency.valueOf(set.getString("currency")));
                transactionDto.setType(Type.valueOf(set.getString("type")));
                AccountDto accountDto = new AccountDto();
                accountDto.setId(set.getLong("account_id"));
                accountDto.setName(set.getString("name"));
                accountDto.setCurrency(Currency.valueOf(set.getString("account_currency")));
                accountDto.setBalance(set.getDouble("balance"));
                transactionDto.setAccount(accountDto);
                CategoryDto categoryDto = new CategoryDto();
                categoryDto.setId(set.getLong("category_id"));
                categoryDto.setCategoryName(Category.CategoryName.valueOf(set.getString("category")));
                categoryDto.setType(Type.valueOf(set.getString("cat_type")));
                categoryDto.setIconURL(set.getString("iconurl"));
                transactionDto.setCategory(categoryDto);
                response.add(transactionDto);
            }
            set.close();
        }
        return response;
    }
}
