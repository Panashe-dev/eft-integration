package jobsim.co.zw.eft_integration.processor.api;

import java.io.InputStream;

public interface TcpMessageSenderProcessor {

    void  tcpConnect(String messageContent);
    byte[] createMessageWithHeader(byte[] message);

    byte[] combineArrays(byte[] header, byte[] message);

    byte[] readResponse(InputStream inputStream) throws Exception;
}
