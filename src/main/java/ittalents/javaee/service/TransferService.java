package ittalents.javaee.service;

import ittalents.javaee.model.Transfer;
import ittalents.javaee.model.TransferDto;
import ittalents.javaee.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private TransferRepository transferRepository;

    @Autowired
    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public long createTransfer(TransferDto transferDto) {
        Transfer transfer = new Transfer();
        transfer.fromDto(transferDto);
        transfer.setDate(LocalDateTime.now());
        return transferRepository.save(transfer).getId();
    }

    public List<TransferDto> getTransfersByAccountId(long id) {
        List<Transfer> allTransfers = new ArrayList<>();
        allTransfers.addAll(transferRepository.findByFromAccountId(id));
        allTransfers.addAll(transferRepository.findByToAccountId(id));
        return allTransfers.stream().map(Transfer::toDto).collect(Collectors.toList());
    }

}
