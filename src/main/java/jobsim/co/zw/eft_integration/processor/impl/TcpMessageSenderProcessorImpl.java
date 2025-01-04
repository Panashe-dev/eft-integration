package jobsim.co.zw.eft_integration.processor.impl;

import jobsim.co.zw.eft_integration.processor.api.TcpMessageSenderProcessor;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpMessageSenderProcessorImpl  implements TcpMessageSenderProcessor {
    @Override
    public void tcpConnect(String messageContent) {
        String serverAddress = "127.0.0.1"; // Replace with the XML interface's IP
        int serverPort = 12345; // Replace with the XML interface's port

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            // Example message
            byte[] messageBytes = messageContent.getBytes("UTF-8");

            // Create the message with the appropriate header
            byte[] messageWithHeader = createMessageWithHeader(messageBytes);

            // Send the message
            outputStream.write(messageWithHeader);
            outputStream.flush();

            System.out.println("Message sent successfully.");

            // Read the response
            byte[] response = readResponse(inputStream);
            System.out.println("Response received: " + new String(response, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] createMessageWithHeader(byte[] message) {
        int messageLength = message.length;

        if (messageLength < 65535) { // Use a two-byte header
            byte[] header = new byte[2];
            header[0] = (byte) (messageLength / 256); // First byte (quotient)
            header[1] = (byte) (messageLength % 256); // Second byte (remainder)

            return combineArrays(header, message);
        } else { // Use a six-byte header
            byte[] header = new byte[6];
            header[0] = (byte) 0xFF;
            header[1] = (byte) 0xFF;

            // Message length as a 32-bit integer (big-endian)
            header[2] = (byte) ((messageLength >> 24) & 0xFF); // Most significant byte
            header[3] = (byte) ((messageLength >> 16) & 0xFF);
            header[4] = (byte) ((messageLength >> 8) & 0xFF);
            header[5] = (byte) (messageLength & 0xFF); // Least significant byte

            return combineArrays(header, message);
        }
    }

    @Override
    public byte[] combineArrays(byte[] header, byte[] message) {
        byte[] combined = new byte[header.length + message.length];
        System.arraycopy(header, 0, combined, 0, header.length);
        System.arraycopy(message, 0, combined, header.length, message.length);
        return combined;
    }

    @Override
    public byte[] readResponse(InputStream inputStream) throws Exception {
        // Read the first two bytes to determine the response header
        byte[] header = new byte[2];
        if (inputStream.read(header) != 2) {
            throw new Exception("Failed to read the response header.");
        }

        int messageLength;
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xFF) {
            // Long message, read additional 4 bytes for length
            byte[] lengthBytes = new byte[4];
            if (inputStream.read(lengthBytes) != 4) {
                throw new Exception("Failed to read the extended response header.");
            }
            messageLength = ((lengthBytes[0] & 0xFF) << 24) |
                    ((lengthBytes[1] & 0xFF) << 16) |
                    ((lengthBytes[2] & 0xFF) << 8) |
                    (lengthBytes[3] & 0xFF);
        } else {
            // Short message
            messageLength = ((header[0] & 0xFF) * 256) + (header[1] & 0xFF);
        }

        // Read the message content based on the length
        byte[] message = new byte[messageLength];
        int bytesRead = 0;
        while (bytesRead < messageLength) {
            int result = inputStream.read(message, bytesRead, messageLength - bytesRead);
            if (result == -1) {
                throw new Exception("Unexpected end of stream while reading message content.");
            }
            bytesRead += result;
        }

        return message;
    }
}
