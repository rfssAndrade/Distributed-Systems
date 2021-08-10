package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;

public class WriteObserver<R> implements StreamObserver<R> {
    private final WriteCollector collector;
    private final String path;

    public WriteObserver(WriteCollector collector, String path) {
        this.collector = collector;
        this.path = path;
    }

    @Override
    public void onNext(R value) {
        System.out.println("> DEBUG: replica " + path + " acknowledged write request");
        collector.onNextUpdate();
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("> DEBUG: replica " + path + " failed to answer write request");
        collector.onErrorUpdate(path);
    }

    @Override
    public void onCompleted() {
        /* empty */
    }
}
