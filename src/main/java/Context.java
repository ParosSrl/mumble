import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import static com.rabbitmq.client.BuiltinExchangeType.TOPIC;

public class Context {

    public static Connection init(String user) {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("192.168.102.209");
            connectionFactory.setUsername("test");
            connectionFactory.setPassword("test");

            final Connection connection = connectionFactory.newConnection();
            final Channel channel = connection.createChannel();

            channel.exchangeDeclare("mumble", TOPIC);

            channel.queueDeclare("stanze." + user, true, false, false, null);
            channel.queueDeclare("utenti." + user, true, false, false, null);

            channel.queueBind("stanze." + user, "mumble", "stanze.#");
            channel.queueBind("utenti." + user, "mumble", "utenti." + user);

            return connection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
