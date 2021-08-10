package pt.tecnico.rec;

import java.util.Set;

public class WriteCollector {
    private final int necessaryInstances;
    private final Set<String> failedInstances;
    private int onNextCounter = 0;
    private int onErrorCounter = 0;
    private Boolean success = null;

    public WriteCollector(int numberInstances, Set<String> failedInstances) {
        this.necessaryInstances = numberInstances / 2 + 1;
        this.failedInstances = failedInstances;
    }

    public synchronized Boolean wasSuccessful() {
        return success;
    }

    public synchronized void onNextUpdate() {
        if (onNextCounter >= necessaryInstances)
            return;

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
