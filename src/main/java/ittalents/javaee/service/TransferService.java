package ittalents.javaee.service;

import ittalents.javaee.model.dao.TransferDao;
import ittalents.javaee.model.dto.ResponseTransferDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Transfer;
import ittalents.javaee.model.dto.RequestTransferDto;
import ittalents.javaee.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private TransferRepository transferRepository;
    private TransferDao transferDao;

    @Autowired
    public TransferService(TransferRepository transferRepository, TransferDao transferDao) {
        this.transferRepository = transferRepository;
        this.transferDao = transferDao;
    }

    public ResponseTransferDto createTransfer(Account fromAccount, Account toAccount, RequestTransferDto requestTransferDto) {
        Transfer transfer = new Transfer();
        transfer.fromDto(requestTransferDto);
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        return transferRepository.save(transfer).toDto();
    }

    public List<ResponseTransferDto> getTransfersByAccountId(long id) {
        List<Transfer> allTransfers = new ArrayList<>();
        allTransfers.addAll(transferRepository.findAllByFromAccountId(id));
        allTransfers.addAll(transferRepository.findAllByToAccountId(id));
        return allTransfers.stream().map(Transfer::toDto).collect(Collectors.toList());
    }

    public List<ResponseTransferDto> getAllTransfersForUser(long userId) throws SQLException {
        return transferDao.getLoggedUserTransfers(userId);
    }
}
