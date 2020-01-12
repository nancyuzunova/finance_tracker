package ittalents.javaee.model.dao;

import ittalents.javaee.model.dto.AccountDto;
import ittalents.javaee.model.dto.ResponseTransferDto;
import ittalents.javaee.model.pojo.Currency;
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
public class TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String GET_LOGGED_USER_TRANSFERS = "SELECT t.id, t.amount, t.currency, t.date, t.from_account_id, t.to_account_id, " +
            "a.name AS from_name, a.balance AS from_balance, a.currency AS from_currency, " +
            "ac.name AS to_name, ac.balance AS to_balance, ac.currency AS to_currency  " +
            "FROM transfers AS t " +
            "JOIN accounts AS a ON t.from_account_id = a.id " +
            "JOIN accounts AS ac ON t.to_account_id = ac.id " +
            "WHERE a.user_id = ?";

    public List<ResponseTransferDto> getLoggedUserTransfers(long userId) throws SQLException {
        List<ResponseTransferDto> transfers = new ArrayList<>();
        try (   Connection connection = jdbcTemplate.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_LOGGED_USER_TRANSFERS)) {
            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ResponseTransferDto transfer = new ResponseTransferDto();
                transfer.setId(result.getLong("id"));
                transfer.setAmount(result.getDouble("amount"));
                transfer.setCurrency(Currency.valueOf(result.getString("currency")));
                transfer.setDate(result.getDate("date"));
                AccountDto fromAccount = new AccountDto();
                fromAccount.setId(result.getLong("from_account_id"));
                fromAccount.setName(result.getString("from_name"));
                fromAccount.setBalance(result.getDouble("from_balance"));
                fromAccount.setCurrency(Currency.valueOf(result.getString("from_currency")));
                transfer.setFromAccount(fromAccount);
                AccountDto toAccount = new AccountDto();
                toAccount.setId(result.getLong("to_account_id"));
                toAccount.setName(result.getString("to_name"));
                toAccount.setBalance(result.getDouble("to_balance"));
                toAccount.setCurrency(Currency.valueOf(result.getString("to_currency")));
                transfer.setToAccount(toAccount);
                transfers.add(transfer);
            }
            result.close();
        }

        return transfers;
    }
}
