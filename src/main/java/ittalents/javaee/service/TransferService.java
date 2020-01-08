package ittalents.javaee.service;

import ittalents.javaee.model.dto.ResponseTransferDto;
import ittalents.javaee.model.pojo.Transfer;
import ittalents.javaee.model.dto.RequestTransferDto;
import ittalents.javaee.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private TransferRepository transferRepository;

    @Autowired
    public TransferService(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    public long createTransfer(RequestTransferDto requestTransferDto) {
        Transfer transfer = new Transfer();
        transfer.fromDto(requestTransferDto);
        transfer.setDate(new Date());
        transfer.setCurrency(requestTransferDto.getCurrency());
        return transferRepository.save(transfer).getId();
    }

    public List<ResponseTransferDto> getTransfersByAccountId(long id) {
        List<Transfer> allTransfers = new ArrayList<>();
        allTransfers.addAll(transferRepository.findByFromAccountId(id));
        allTransfers.addAll(transferRepository.findByToAccountId(id));
        return allTransfers.stream().map(Transfer::toDto).collect(Collectors.toList());
    }

}
