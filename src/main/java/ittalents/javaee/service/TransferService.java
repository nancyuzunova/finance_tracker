package ittalents.javaee.service;

import ittalents.javaee.exceptions.ElementNotFoundException;
import ittalents.javaee.model.dto.ResponseTransferDto;
import ittalents.javaee.model.pojo.Account;
import ittalents.javaee.model.pojo.Transfer;
import ittalents.javaee.model.dto.RequestTransferDto;
import ittalents.javaee.repository.AccountRepository;
import ittalents.javaee.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private TransferRepository transferRepository;
    private AccountRepository accountRepository;

    @Autowired
    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public long createTransfer(RequestTransferDto requestTransferDto) {
        Transfer transfer = new Transfer();
        transfer.fromDto(requestTransferDto);

        Optional<Account> fromAccountById = accountRepository.findById(requestTransferDto.getFromAccountId());
        Optional<Account> toAccountById = accountRepository.findById(requestTransferDto.getToAccountId());

        if (!fromAccountById.isPresent() || !toAccountById.isPresent()) {
            throw new ElementNotFoundException("Account can not be found!");
        }

        transfer.setFromAccount(fromAccountById.get());
        transfer.setToAccount(toAccountById.get());
        return transferRepository.save(transfer).getId();
    }

    public List<ResponseTransferDto> getTransfersByAccountId(long id) {
        List<Transfer> allTransfers = new ArrayList<>();
        allTransfers.addAll(transferRepository.findAllByFromAccountId(id));
        allTransfers.addAll(transferRepository.findAllByToAccountId(id));
        return allTransfers.stream().map(Transfer::toDto).collect(Collectors.toList());
    }

}
