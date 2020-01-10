package ittalents.javaee.model.dao;

import ittalents.javaee.model.dto.ResponsePlannedPaymentDto;
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

    private static final String GET_ALL_PLANNED_PAYMENTS = "SELECT p.id, p.title, p.amount, p.status, p.date, p.account_id " +
            "FROM planned_payments AS p " +
            "JOIN accounts AS a ON p.account_id = a.id " +
            "WHERE a.user_id = ?;";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountService accountService;

    //TODO ask Krasi for the number of queries, is it ok?
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
                long accountId = rows.getLong("account_id");
                responsePlannedPaymentDto.setAccount(accountService.getAccountById(accountId).toDto());
                paymentDtos.add(responsePlannedPaymentDto);
            }
        }
        return paymentDtos;
    }
}
