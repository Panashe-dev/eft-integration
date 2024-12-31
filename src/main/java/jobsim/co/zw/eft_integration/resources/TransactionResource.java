package jobsim.co.zw.eft_integration.resources;

import jobsim.co.zw.eft_integration.processor.api.TransactionProcessor;
import jobsim.co.zw.eft_integration.utils.dto.request.TransactionRequest;
import jobsim.co.zw.eft_integration.utils.dto.response.Response;
import jobsim.co.zw.eft_integration.utils.dto.response.ResponseBuild;
import jobsim.co.zw.eft_integration.utils.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/send/transaction",
        produces = MediaType.APPLICATION_JSON_VALUE,
        headers = "Accept=application/json")
@RequiredArgsConstructor
public class TransactionResource {

    private final ResponseBuild<TransactionResponse> transactionResponseResponseBuild;

    private  final TransactionProcessor transactionProcessor;

    @PostMapping
    public ResponseEntity<Response> sendTransaction(@RequestBody TransactionRequest transactionRequest) throws Exception {
        log.info("## send transaction request {}",transactionRequest);
        return  new ResponseEntity<>(transactionResponseResponseBuild.successResponse
                .apply(transactionProcessor.sendTransactions(transactionRequest),null), HttpStatus.OK);

    }

}
