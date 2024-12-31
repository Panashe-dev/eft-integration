package jobsim.co.zw.eft_integration.business.impl;

import jobsim.co.zw.eft_integration.utils.dto.response.EFTTransactionResponse;

public interface EFTTransactionService {

    EFTTransactionResponse postTransaction(String transactionRequest);
}
