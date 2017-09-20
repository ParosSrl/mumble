import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.rabbitmq.client.BuiltinExchangeType.FANOUT;
import static com.rabbitmq.client.BuiltinExchangeType.TOPIC;
import static java.lang.Thread.sleep;

public class DbPersistencyConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.102.209");
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        List<Message> messages = new ArrayList<>();
        final Connection connection = connectionFactory.newConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare("mumble", TOPIC);
        channel.exchangeDeclare("mumble.dl", FANOUT);

        final Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "mumble.dl");
        channel.queueDeclare("db", true, false, false, arguments);

        channel.queueDeclare("dlq", true, false, false, null);

        channel.queueBind("db", "mumble", "#");
        channel.queueBind("dlq", "mumble.dl", "");

        channel.basicQos(1);
        channel.basicConsume("db", false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    messages.add(new Message(new String(body), properties.getHeaders().get("dataOra").toString(), properties.getHeaders().get("mittente").toString(), properties.getHeaders().get("stanza").toString()));
                    sleep(2000);
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
                    channel.basicNack(envelope.getDeliveryTag(), false, false);
                }

                System.out.println(messages);
            }
        });

    }


}
