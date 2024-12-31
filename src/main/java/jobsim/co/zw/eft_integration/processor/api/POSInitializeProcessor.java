package jobsim.co.zw.eft_integration.processor.api;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public interface POSInitializeProcessor {
    void InitializePos() throws ParserConfigurationException, TransformerException;
}
