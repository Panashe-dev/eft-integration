package jobsim.co.zw.eft_integration.utils.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StructuredDataRequest {
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Value") 
    public String value;
}
