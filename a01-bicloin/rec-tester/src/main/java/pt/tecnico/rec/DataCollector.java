package pt.tecnico.rec;

public class DataCollector {
    private String value;
    private int sequence;
    private String clientId;

    public DataCollector(DataCollector collector) {
        this.value = collector.getValue();
        this.sequence = collector.getSequence();
        this.clientId = collector.getClientId();
    }

    public DataCollector() {
        this.value = "";
        this.sequence = -1;
        this.clientId = "";
    }

    public String getValue() {
        return value;
    }

    public int getSequence() {
        return sequence;
    }

    public String getClientId() {
        return clientId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
