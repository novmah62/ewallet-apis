package com.novmah.bankingapp.service;

import com.novmah.bankingapp.dto.*;

public interface UserService {

    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest enquiryRequest);

}
