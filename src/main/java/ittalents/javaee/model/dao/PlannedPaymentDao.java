package ittalents.javaee.model.dao;

import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
import ittalents.javaee.model.pojo.Currency;
import ittalents.javaee.model.pojo.PlannedPayment;
import ittalents.javaee.service.AccountService;
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
public class PlannedPaymentDao {

    private static final String GET_ALL_PLANNED_PAYMENTS = "SELECT p.id, p.title, p.amount, p.status, p.date, " +
            "a.id AS account_id, a.name, a.balance, a.currency " +
            "FROM planned_payments AS p " +
            "JOIN accounts AS a ON p.account_id = a.id " +
            "WHERE a.user_id = ?;";

    private static final String GET_PLANNED_PAYMENTS_FOR_AN_ACCOUNT = "SELECT p.id, p.title, p.amount, p.status, p.date, " +
            "a.id AS account_id, a.name, a.balance, a.currency " +
            "FROM planned_payments AS p " +
            "JOIN accounts AS a ON p.account_id = a.id " +
            "WHERE a.user_id = ? AND p.account_id = ?;";

    private static final String GET_PLANNED_PAYMENTS_BY_STATUS = "SELECT p.id, p.title, p.amount, p.status, p.date, " +
            "a.id AS account_id, a.name, a.balance, a.currency " +
            "FROM planned_payments AS p " +
            "JOIN accounts AS a ON p.account_id = a.id " +
            "WHERE a.user_id = ? AND p.status = ?;";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountService accountService;

    public List<ResponsePlannedPaymentDto> getMyPlannedPayments(long userId) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        List<ResponsePlannedPaymentDto> paymentDtos = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(GET_ALL_PLANNED_PAYMENTS)){
            statement.setLong(1, userId);
            ResultSet rows = statement.executeQuery();
            while(rows.next()){
                ResponsePlannedPaymentDto responsePlannedPaymentDto = new ResponsePlannedPaymentDto();
                responsePlannedPaymentDto.setId(rows.getLong("id"));
                responsePlannedPaymentDto.setStatus(PlannedPayment.PaymentStatus.valueOf(rows.getString("status")));
                responsePlannedPaymentDto.setDate(rows.getDate("date"));
                responsePlannedPaymentDto.setAmount(rows.getDouble("amount"));
                responsePlannedPaymentDto.setTitle(rows.getString("title"));
                AccountDto accountDto = new AccountDto();
                accountDto.setId(rows.getLong("account_id"));
                accountDto.setName(rows.getString("name"));
                accountDto.setBalance(rows.getDouble("balance"));
                accountDto.setCurrency(Currency.valueOf(rows.getString("currency")));
                responsePlannedPaymentDto.setAccount(accountDto);
                paymentDtos.add(responsePlannedPaymentDto);
            }
        }
        return paymentDtos;
    }

    public List<ResponsePlannedPaymentDto> getPlannedPaymentsByAccountId(long userId, long accountId) throws SQLException{
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        List<ResponsePlannedPaymentDto> paymentDtos = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(GET_PLANNED_PAYMENTS_FOR_AN_ACCOUNT)){
            statement.setLong(1, userId);
            statement.setLong(2, accountId);
            ResultSet rows = statement.executeQuery();
            while(rows.next()){
                ResponsePlannedPaymentDto responsePlannedPaymentDto = new ResponsePlannedPaymentDto();
                responsePlannedPaymentDto.setId(rows.getLong("id"));
                responsePlannedPaymentDto.setStatus(PlannedPayment.PaymentStatus.valueOf(rows.getString("status")));
                responsePlannedPaymentDto.setDate(rows.getDate("date"));
                responsePlannedPaymentDto.setAmount(rows.getDouble("amount"));
                responsePlannedPaymentDto.setTitle(rows.getString("title"));
                AccountDto accountDto = new AccountDto();
                accountDto.setId(accountId);
                accountDto.setName(rows.getString("name"));
                accountDto.setBalance(rows.getDouble("balance"));
                accountDto.setCurrency(Currency.valueOf(rows.getString("currency")));
                responsePlannedPaymentDto.setAccount(accountDto);
                paymentDtos.add(responsePlannedPaymentDto);
            }
        }
        return paymentDtos;
    }

    public List<ResponsePlannedPaymentDto> getPlannedPaymentsByStatus(long userId, PlannedPayment.PaymentStatus status) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        List<ResponsePlannedPaymentDto> paymentDtos = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(GET_PLANNED_PAYMENTS_BY_STATUS)){
            statement.setLong(1, userId);
            statement.setString(2, status.toString());
            ResultSet rows = statement.executeQuery();
            while(rows.next()){
                ResponsePlannedPaymentDto responsePlannedPaymentDto = new ResponsePlannedPaymentDto();
                responsePlannedPaymentDto.setId(rows.getLong("id"));
                responsePlannedPaymentDto.setStatus(PlannedPayment.PaymentStatus.valueOf(rows.getString("status")));
                responsePlannedPaymentDto.setDate(rows.getDate("date"));
                responsePlannedPaymentDto.setAmount(rows.getDouble("amount"));
                responsePlannedPaymentDto.setTitle(rows.getString("title"));
                AccountDto accountDto = new AccountDto();
                accountDto.setId(rows.getLong("account_id"));
                accountDto.setName(rows.getString("name"));
                accountDto.setBalance(rows.getDouble("balance"));
                accountDto.setCurrency(Currency.valueOf(rows.getString("currency")));
                responsePlannedPaymentDto.setAccount(accountDto);
                paymentDtos.add(responsePlannedPaymentDto);
            }
        }
        return paymentDtos;
    }
}
