package jobsim.co.zw.eft_integration.processor.api;

import jobsim.co.zw.eft_integration.utils.dto.request.TransactionRequest;
import jobsim.co.zw.eft_integration.utils.dto.response.TransactionResponse;


public interface TransactionProcessor {
    TransactionResponse sendTransactions(TransactionRequest transactionRequest) throws Exception;
}
