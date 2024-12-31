package jobsim.co.zw.eft_integration.utils.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineItemRequest {
    @JsonProperty("Description")
    public String description;
    @JsonProperty("Sign") 
    public String sign;
    @JsonProperty("TaxAmounts") 
    public List<String> taxAmounts;
}
