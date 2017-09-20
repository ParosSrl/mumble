import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.rabbitmq.client.BuiltinExchangeType.TOPIC;
import static com.rabbitmq.client.MessageProperties.PERSISTENT_BASIC;

public class Main {

    public static void main(String[] args) throws Exception {
        final BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Chi sei?");
        String user = consoleInput.readLine();
        System.out.println("Su che stanza vuoi mandare il messaggio?");
        String stanza = consoleInput.readLine();
        System.out.println("Digita il messaggio da inviare e premi invio!");
        String message = consoleInput.readLine();
        System.out.println("Sei stata bravissima!");
        consoleInput.close();

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

        channel.basicPublish("mumble", "stanze." + stanza, PERSISTENT_BASIC, message.getBytes());

        System.out.println("Molto bravissima, il tuo messaggio del tuo cuore è stato forse inviato!");
    }


}
