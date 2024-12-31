package jobsim.co.zw.eft_integration.processor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.gson.Gson;
import jobsim.co.zw.eft_integration.processor.api.POSInitializeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import jobsim.co.zw.eft_integration.processor.api.TransactionProcessor;
import jobsim.co.zw.eft_integration.utils.dto.request.TransactionRequest;
import jobsim.co.zw.eft_integration.utils.dto.response.TransactionResponse;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProcessorImpl implements TransactionProcessor {


    private final TcpOutboundGateway tcpOutboundGateway;


    private final MessageChannel outputChannel;

    private final POSInitializeProcessor posInitializeProcessor;
    @Override
    public TransactionResponse sendTransactions(TransactionRequest transactionRequest) throws Exception {

        /**
         * POS Initialize
         * **/

        posInitializeProcessor.InitializePos();

        ObjectMapper objectMapper = new ObjectMapper();

        String requestBody = new Gson().toJson(transactionRequest);

        JsonNode rootNode = objectMapper.readTree(requestBody);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();


        Element rootElement = doc.createElementNS(
                "http://www.mosaicsoftware.com/Postilion/eSocket.POS/",
                "Esp:Interface"
        );
        rootElement.setAttribute("Version", "1.0");
        doc.appendChild(rootElement);


        Element transactionElement = doc.createElement("Esp:Transaction");
        transactionElement.setAttribute("TerminalId", rootNode.get("terminalId").asText());
        transactionElement.setAttribute("TransactionId", rootNode.get("transactionId").asText());
        transactionElement.setAttribute("Type", rootNode.get("type").asText());
        transactionElement.setAttribute("TransactionAmount", rootNode.get("transactionAmount").asText());
        rootElement.appendChild(transactionElement);


        Element purchasingCardData = doc.createElement("Esp:PurchasingCardData");
        purchasingCardData.setAttribute("Description", "Purchasing Card Data");
        transactionElement.appendChild(purchasingCardData);


        JsonNode lineItems = rootNode.get("lineItems");
        for (JsonNode lineItem : lineItems) {
            Element lineItemElement = doc.createElement("Esp:LineItem");
            lineItemElement.setAttribute("Description", lineItem.get("description").asText());
            if (lineItem.has("sign")) {
                lineItemElement.setAttribute("Sign", lineItem.get("sign").asText());
            }
            purchasingCardData.appendChild(lineItemElement);


            if (lineItem.has("taxAmounts")) {
                for (JsonNode taxAmount : lineItem.get("taxAmounts")) {
                    Element taxAmountElement = doc.createElement("Esp:TaxAmount");
                    taxAmountElement.setAttribute("Type", taxAmount.asText());
                    lineItemElement.appendChild(taxAmountElement);
                }
            }}


        JsonNode contacts = rootNode.get("contacts");
        for (JsonNode contact : contacts) {
            Element contactElement = doc.createElement("Esp:Contact");
            contactElement.setAttribute("Type", contact.get("type").asText());
            if (contact.has("name") && !contact.get("name").asText().isEmpty()) {
                contactElement.setAttribute("Name", contact.get("name").asText());
            }
            if (contact.has("telephone") && !contact.get("telephone").asText().isEmpty()) {
                contactElement.setAttribute("Telephone", contact.get("telephone").asText());
            }

            purchasingCardData.appendChild(contactElement);
        }

        Element taxAmountElement_01 = doc.createElement("Esp:TaxAmount");
        taxAmountElement_01.setAttribute("Type", "02");

        Element taxAmountElement_02 = doc.createElement("Esp:TaxAmount");
        taxAmountElement_02.setAttribute("Type", "03");
        purchasingCardData.appendChild(taxAmountElement_01);
        purchasingCardData.appendChild(taxAmountElement_02);


        Element structuredData = doc.createElement("Esp:PosStructuredData");
        structuredData.setAttribute("Name", rootNode.get("structuredData").get("name").asText());
        structuredData.setAttribute("Value", rootNode.get("structuredData").get("value").asText());
        transactionElement.appendChild(structuredData);

        String convertDocumentToString = convertDocumentToString(doc);
        log.info("eft transaction request {}-",convertDocumentToString);

        tcpOutboundGateway.handleMessage(new GenericMessage<>(convertDocumentToString.getBytes()));

        MessagingTemplate messagingTemplate = new MessagingTemplate();
        org.springframework.messaging.Message<?> response = messagingTemplate.receive(outputChannel);

        TransactionResponse transactionResponse1=new TransactionResponse();
        if (response != null && response.getPayload() instanceof byte[]) {
         String message= new String((byte[]) response.getPayload());
            transactionResponse1.setMessage(message);
        }
        return transactionResponse1;

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
