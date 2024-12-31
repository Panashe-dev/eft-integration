package jobsim.co.zw.eft_integration.processor.impl;

import jobsim.co.zw.eft_integration.processor.api.POSInitializeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class POSInitializeProcessorImpl  implements POSInitializeProcessor {

    private final TcpOutboundGateway tcpOutboundGateway;


    private final MessageChannel outputChannel;

    @Override
    public void InitializePos() throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();


        Element rootElement = doc.createElementNS(
                "http://www.mosaicsoftware.com/Postilion/eSocket.POS/",
                "Esp:Interface"
        );
        rootElement.setAttribute("Version", "1.0");
        doc.appendChild(rootElement);

        Element transactionElement = doc.createElement("Esp:Admin");
        transactionElement.setAttribute("TerminalId","12345");
        transactionElement.setAttribute("Action", "INIT");
        rootElement.appendChild(transactionElement);

        String convertDocumentToString = convertDocumentToString(doc);
        log.info("eft pos Initializing request {}-",convertDocumentToString);

        tcpOutboundGateway.handleMessage(new GenericMessage<>(convertDocumentToString.getBytes()));

        MessagingTemplate messagingTemplate = new MessagingTemplate();
        org.springframework.messaging.Message<?> response = messagingTemplate.receive(outputChannel);


        if (response != null && response.getPayload() instanceof byte[]) {
            String message= new String((byte[]) response.getPayload());
           log.info("eft pos Initializing request {}-",message);
        }

    }

    private static String convertDocumentToString(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, result);

        return writer.toString();
    }

}
