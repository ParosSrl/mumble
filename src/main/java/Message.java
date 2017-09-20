public class Message {
    private final String body;
    private final String dataOra;
    private final String mittente;
    private final String stanza;

    public Message(String body, String  dataOra, String mittente, String stanza) {
        this.body = body;
        this.dataOra = dataOra;
        this.mittente = mittente;
        this.stanza = stanza;
    }

    @Override
    public String toString() {
        return "Message{" +
                "body='" + body + '\'' +
                ", dataOra='" + dataOra + '\'' +
                ", mittente='" + mittente + '\'' +
                ", stanza='" + stanza + '\'' +
                '}';
    }
}
