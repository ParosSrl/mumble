import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Map;

import static java.lang.System.out;

public class PrivateMessageConsumer extends DefaultConsumer {

    public PrivateMessageConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        final Map<String, Object> headers = properties.getHeaders();
        out.println("\t\t[" + headers.get("dataOra") + "] Ricevuto messaggio da " + headers.get("mittente") + " su stanza " +headers.get("stanza") + ": " + new String(body));
    }
}
