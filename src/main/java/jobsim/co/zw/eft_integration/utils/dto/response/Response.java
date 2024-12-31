package jobsim.co.zw.eft_integration.utils.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class Response {
    private int statusCode;
    private String message;
    private Object data;
    private List<?> dataList;
}
