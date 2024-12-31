package jobsim.co.zw.eft_integration.utils.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @JsonProperty("TerminalId")
    public String terminalId;
    @JsonProperty("TransactionId") 
    public String transactionId;
    @JsonProperty("Type") 
    public String type;
    @JsonProperty("TransactionAmount") 
    public String transactionAmount;
    @JsonProperty("LineItems") 
    public List<LineItemRequest> lineItems;
    @JsonProperty("Contacts") 
    public List<ContactRequest> contacts;
    @JsonProperty("StructuredData")
    public StructuredDataRequest structuredData;
}
