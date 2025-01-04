package jobsim.co.zw.eft_integration.processor.api;

public interface TcpMessageSenderProcessor {

    void  tcpConnect(String messageContent);
    byte[] createMessageWithHeader(byte[] message);

    byte[] combineArrays(byte[] header, byte[] message);
}
