import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

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
        channel.queueDeclare("db", true, false, false, null);
        channel.queueBind("db", "mumble", "#");

        channel.basicConsume("db", false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                messages.add(new Message(new String(body), properties.getHeaders().get("dataOra").toString(),properties.getHeaders().get("mittente").toString(), properties.getHeaders().get("stanza").toString()));
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println(messages);
            }
        });

    }


}
