package jobsim.co.zw.eft_integration.business.api;

import jobsim.co.zw.eft_integration.business.impl.EFTTransactionService;
import jobsim.co.zw.eft_integration.utils.dto.response.EFTTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EFTTransactionServiceImpl implements EFTTransactionService {
    @Override
    public EFTTransactionResponse postTransaction(String transactionRequest) {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("http://localhost:23001/Postilion/eSocket.POS/");
            httpPost.setHeader("Content-type", "application/xml");
            StringEntity stringEntity = new StringEntity(transactionRequest);
            httpPost.setEntity(stringEntity);

            log.info("## Executing request " + httpPost.getRequestLine());

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                log.info("## Request Status P {}",status);
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw  new RuntimeException("Failed to process your request");
                }
            };
            String responseBody = httpclient.execute(httpPost, responseHandler);
            log.info("## Transaction Request Success");
            log.info("eft response-{}",responseBody);
            EFTTransactionResponse transactionResponse=new EFTTransactionResponse();
            transactionResponse.setMessage(responseBody);
            return transactionResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("## Error Transaction Post :: {}", ex.getLocalizedMessage());
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
}
