package com.novmah.bankingapp.mapper;

import com.novmah.bankingapp.dto.response.AccountInfo;
import com.novmah.bankingapp.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "accountBalance", source = "accountBalance")
    @Mapping(target = "status", source = "status")
    AccountInfo mapToDto(Account account);

}
