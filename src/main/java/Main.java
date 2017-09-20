import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.in;
import static java.lang.System.out;

public class Main {

    public static void main(String[] args) throws Exception {

        final BufferedReader consoleInput = new BufferedReader(new InputStreamReader(in));
        out.println("Chi sei?");
        String user = consoleInput.readLine();

        final Connection connection = Context.init(user);
        final Channel consumer = connection.createChannel();

        consumer.basicConsume("stanze." + user, true, new PrivateMessageConsumer(consumer));
        consumer.basicConsume("utenti." + user, true, new PublicMessageConsumer(consumer));

        final Channel producer = connection.createChannel();
        boolean exit = false;
        while (!exit) {
            out.println("Operazione da eseguire:\n\t1) Messaggio privato;\n\t2) Messaggio stanza;\n\t3) exit;");
            final String command = consoleInput.readLine();
            String destination, message;

            switch (command) {
                case "1":
                    out.println("A chi vuoi mandare il messaggio? ");
                    destination = consoleInput.readLine();
                    out.println("Digita il messaggio da inviare e premi invio!");
                    message = consoleInput.readLine();

                    producer.basicPublish("mumble", "utenti." + destination, properties(destination, user), message.getBytes());
                    break;

                case "2":
                    out.println("Su che stanza vuoi mandare il messaggio? ");
                    destination = consoleInput.readLine();
                    out.println("Digita il messaggio da inviare e premi invio!");
                    message = consoleInput.readLine();
                    producer.basicPublish("mumble", "stanze." + destination, properties(destination, user), message.getBytes());
                    break;

                default:
                    exit = true;
                    break;
            }

            out.println("Sei stata bravissima!");
        }

        consoleInput.close();
        connection.close();
    }

    private static AMQP.BasicProperties properties(String stanza, String mittente) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("stanza", stanza);
        headers.put("dataOra", LocalDateTime.now().toString());
        headers.put("mittente", mittente);
        return new AMQP.BasicProperties.Builder().appId("mumble").deliveryMode(2).headers(headers).build();
    }


}
