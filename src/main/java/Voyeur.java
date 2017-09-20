import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;

import static com.rabbitmq.client.BuiltinExchangeType.TOPIC;
import static java.lang.System.out;

public class Voyeur {

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.102.209");
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");

        final Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare("mumble", TOPIC);

        String queue = channel.queueDeclare().getQueue();

        channel.queueBind(queue, "mumble", "#");

        channel.basicConsume(queue, false, new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                final Map<String, Object> headers = properties.getHeaders();
                out.println("\t\t[" + headers.get("dataOra") + "] Ricevuto messaggio da " + headers.get("mittente") + " su stanza " +headers.get("stanza") + ": " + new String(body));

                try {
                    Thread.sleep(2000);
                }catch (Exception e) {
                    throw  new RuntimeException(e);
                }
                channel.basicAck(envelope.getDeliveryTag(), true);
            }
        });

    }

}
