package jobsim.co.zw.eft_integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.messaging.MessageChannel;

@Configuration
public class TcpConfig {

    @Bean
    public TcpNetClientConnectionFactory tcpClientFactory() {
        TcpNetClientConnectionFactory factory = new TcpNetClientConnectionFactory("127.0.0.1", 23001);
        factory.setSingleUse(false);
        return factory;
    }

    @Bean
    public TcpOutboundGateway tcpOutboundGateway() {
        TcpOutboundGateway gateway = new TcpOutboundGateway();
        gateway.setConnectionFactory(tcpClientFactory());
        gateway.setOutputChannel(outputChannel());
        return gateway;
    }

    @Bean
    public MessageChannel outputChannel() {
        return new DirectChannel();
    }

}
