package jobsim.co.zw.eft_integration.utils.dto.response;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
public class ResponseBuild<T> {
    public BiFunction<T, List<T>, Response> successResponse = (obj, objList) -> {
        var response = new Response();
        response.setData(obj);
        response.setDataList(objList);
        response.setMessage("success");
        response.setStatusCode(200);
        return response;
    };

}
