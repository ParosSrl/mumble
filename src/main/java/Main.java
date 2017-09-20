import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        final BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Chi sei?");
        String user = consoleInput.readLine();

        final Connection connection = Context.init(user);
        final Channel consumer = connection.createChannel();

        consumer.basicConsume("stanze." + user, true, new DefaultConsumer(consumer) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                final Map<String, Object> headers = properties.getHeaders();

                System.out.println("\t\t[" + headers.get("dataOra") + "] Ricevuto messaggio da " + headers.get("mittente") + " su stanza " +headers.get("stanza") + ": " + new String(body));
            }
        });

        final Channel producer = connection.createChannel();
        while (true) {
            System.out.println("Su che stanza vuoi mandare il messaggio? (Digita 'exit' per uscire)");
            String stanza = consoleInput.readLine();
            if ("exit".equals(stanza)) break;

            System.out.println("Digita il messaggio da inviare e premi invio!");
            String message = consoleInput.readLine();
            System.out.println("Sei stata bravissima!");

            producer.basicPublish("mumble", "stanze." + stanza, properties(stanza, user), message.getBytes());
            System.out.println("Molto bravissima, il tuo messaggio del tuo cuore è stato forse inviato!");
        }

        consoleInput.close();
        connection.close();
    }

    private static AMQP.BasicProperties properties(String stanza, String mittente) {
        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put("stanza", stanza);
        headers.put("dataOra", LocalDateTime.now().toString());
        headers.put("mittente", mittente);
        return new AMQP.BasicProperties.Builder().appId("mumble").deliveryMode(2).headers(headers).build();
    }


}
