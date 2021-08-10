package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.Rec;

public class ReadObserver<R> implements StreamObserver<R> {
    private ReadCollector collector;
    private final String path;

    public ReadObserver(ReadCollector collector, String path) {
        this.collector = collector;
        this.path = path;
    }

    @Override
    public void onNext(R value) {
        System.out.println("> DEBUG: replica " + path + " acknowledged read request");
        Rec.ReadResponse response = (Rec.ReadResponse) value;
        collector.onNextUpdate(response.getValue(), response.getSequence(), response.getClientId());
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("> DEBUG: replica " + path + " failed to answer read request");
        collector.onErrorUpdate(path);
    }

    @Override
    public void onCompleted() {
        /* empty */
    }
}
