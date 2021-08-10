package pt.tecnico.rec;

import java.util.Set;

public class ReadCollector extends DataCollector {
    private final int necessaryInstances;
    private final Set<String> failedInstances;
    private int onNextCounter = 0;
    private int onErrorCounter = 0;
    private Boolean success = null;

    public ReadCollector(int numberInstances, Set<String> failedInstances) {
        this.necessaryInstances = numberInstances / 2 + 1;
        this.failedInstances = failedInstances;
    }

    public synchronized Boolean wasSuccessful() {
        return success;
    }

    public synchronized void onNextUpdate(String value, int sequence, String clientId) {
        if (onNextCounter >= necessaryInstances)
            return;

        if (this.getSequence() < sequence || this.getSequence() == sequence && this.getClientId().compareTo(clientId) < 0) {
            this.setValue(value);
            this.setSequence(sequence);
            this.setClientId(clientId);
        }
        onNextCounter++;
        if (onNextCounter >= necessaryInstances) {
            success = true;
            this.notifyAll();
        }
    }

    public synchronized void onErrorUpdate(String path) {
        synchronized (failedInstances) {
            failedInstances.add(path);
        }

        if (onErrorCounter >= necessaryInstances)
            return;

        onErrorCounter++;
        if (onErrorCounter >= necessaryInstances) {
            success = false;
            this.notifyAll();
        }
    }
}
