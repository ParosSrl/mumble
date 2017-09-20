import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.rabbitmq.client.MessageProperties.PERSISTENT_BASIC;

public class Main {

    public static void main(String[] args) throws Exception {
        final BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Chi sei?");
        String user = consoleInput.readLine();

        final Connection connection = Context.init(user);
        final Channel consumer = connection.createChannel();

        consumer.basicConsume("stanze."+user, true, new DefaultConsumer(consumer) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                System.out.println("\t\tRicevuto messaggio: " +  new String(body));
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

            producer.basicPublish("mumble", "stanze." + stanza, PERSISTENT_BASIC, message.getBytes());
            System.out.println("Molto bravissima, il tuo messaggio del tuo cuore è stato forse inviato!");
        }

        consoleInput.close();
        connection.close();
    }


}
