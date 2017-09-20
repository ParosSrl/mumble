import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

        final Connection connection = Context.init(user);
        final Channel channel = connection.createChannel();

        channel.basicPublish("mumble", "stanze." + stanza, PERSISTENT_BASIC, message.getBytes());

        System.out.println("Molto bravissima, il tuo messaggio del tuo cuore è stato forse inviato!");

        channel.close();
        connection.close();
    }


}
