package ittalents.javaee.model.dao;

import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.CategoryDto;
import ittalents.javaee.model.dto.ResponseTransactionDto;
import ittalents.javaee.model.dto.ResponseTransferDto;
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

    private final String GET_TRANSACTIONS_BY_DESCRIPTION = "SELECT t.id, t.amount, t.currency, t.date, t.type, t.description, " +
            "a.id AS account_id, a.name, a.balance, a.currency AS account_currency, " +
            "c.id AS category_id, c.name AS category, c.iconurl, c.type AS cat_type " +
            "FROM transactions AS t " +
            "JOIN accounts AS a ON t.account_id = a.id " +
            "JOIN categories AS c ON t.category_id = c.id " +
            "WHERE a.user_id = ? AND t.description LIKE ";

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
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ResponseTransactionDto transactionDto = createResponseTransactionDto(result);
                transactionDto.setAccount(createAccountDto(result));
                transactionDto.setCategory(createCategoryDto(result));
                responseTransactionDtos.add(transactionDto);
            }
            result.close();
        }
        return responseTransactionDtos;
    }

    public List<ResponseTransactionDto> getAllTransactionsByType(long userId, Type type) throws SQLException {
        List<ResponseTransactionDto> response = new ArrayList<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GET_MY_TRANSACTIONS_BY_TYPE)) {
            statement.setLong(1, userId);
            statement.setString(2, type.toString());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ResponseTransactionDto transactionDto = createResponseTransactionDto(result);
                transactionDto.setAccount(createAccountDto(result));
                transactionDto.setCategory(createCategoryDto(result));
                response.add(transactionDto);
            }
            result.close();
        }
        return response;
    }

    public List<ResponseTransactionDto> getTransactionsByDescription(long userId, String filter) throws SQLException {
        List<ResponseTransactionDto> transactions = new ArrayList<>();
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                GET_TRANSACTIONS_BY_DESCRIPTION + "%" + filter + "%")) {
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ResponseTransactionDto transaction = createResponseTransactionDto(result);
                transaction.setAccount(createAccountDto(result));
                transaction.setCategory(createCategoryDto(result));
            }
            result.close();
        }
        return transactions;
    }

    private ResponseTransactionDto createResponseTransactionDto(ResultSet result) throws SQLException {
        ResponseTransactionDto transaction = new ResponseTransactionDto();
        transaction.setId(result.getLong("id"));
        transaction.setDescription(result.getString("description"));
        transaction.setDate(result.getDate("date"));
        transaction.setAmount(result.getDouble("amount"));
        transaction.setCurrency(Currency.valueOf(result.getString("currency")));
        transaction.setType(Type.valueOf(result.getString("type")));
        return transaction;
    }

    private AccountDto createAccountDto(ResultSet result) throws SQLException {
        AccountDto account = new AccountDto();
        account.setId(result.getLong("account_id"));
        account.setName(result.getString("name"));
        account.setCurrency(Currency.valueOf(result.getString("account_currency")));
        account.setBalance(result.getDouble("balance"));
        return account;
    }

    private CategoryDto createCategoryDto(ResultSet result) throws SQLException {
        CategoryDto category = new CategoryDto();
        category.setId(result.getLong("category_id"));
        category.setCategoryName(Category.CategoryName.valueOf(result.getString("category")));
        category.setType(Type.valueOf(result.getString("cat_type")));
        category.setIconURL(result.getString("iconurl"));
        return category;
    }
}
