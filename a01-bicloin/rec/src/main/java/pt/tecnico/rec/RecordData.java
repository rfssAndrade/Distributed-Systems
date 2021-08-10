package pt.tecnico.rec;

public class RecordData implements Comparable<RecordData> {
    private String value;
    private int sequence;
    private String clientId;

    public RecordData(String value, int sequence, String clientId) {
        this.value = value;
        this.sequence = sequence;
        this.clientId = clientId;
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

    @Override
    public int compareTo(RecordData o) {
        if (sequence < o.getSequence())
            return -1;
        else if (sequence > o.getSequence())
            return 1;
        else
            return clientId.compareTo(o.getClientId());
    }
}
