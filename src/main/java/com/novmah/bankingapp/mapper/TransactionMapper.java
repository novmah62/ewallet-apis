package com.novmah.bankingapp.mapper;

import com.novmah.bankingapp.dto.request.TransactionRequest;
import com.novmah.bankingapp.dto.response.TransactionResponse;
import com.novmah.bankingapp.dto.response.TransferResponse;
import com.novmah.bankingapp.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "transactionType", source = "type")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "amount", source = "transactionRequest.amount")
    @Mapping(target = "status", constant = "SUCCESS")
    @Mapping(target = "message", source = "transactionRequest.message")
    Transaction mapToEntity(TransactionRequest transactionRequest, String type, String accountNumber);

    @Mapping(target = "createdAt", source = "transaction.createdAt")
    @Mapping(target = "modifiedAt", source = "transaction.modifiedAt")
    TransactionResponse mapToDto(Transaction transaction);

    @Mapping(target = "sourceAccountNumber", source = "transaction.accountNumber")
    @Mapping(target = "destinationAccountNumber", source = "destinationAccountNumber")
    @Mapping(target = "createdAt", source = "transaction.createdAt")
    @Mapping(target = "modifiedAt", source = "transaction.modifiedAt")
    TransferResponse mapToDto(Transaction transaction, String destinationAccountNumber);

}
