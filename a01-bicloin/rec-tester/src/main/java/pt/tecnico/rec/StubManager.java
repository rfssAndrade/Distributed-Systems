package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.RecordServiceGrpc;

import java.util.concurrent.TimeUnit;

public class StubManager implements AutoCloseable {

    /**
     * Channel of the connection with the server.
     */
    private ManagedChannel channel;

    /**
     * Stub of the connection with the server.
     */
    private RecordServiceGrpc.RecordServiceStub stub;

    private final String path;

    private boolean active = true;

    public StubManager(String path, String target) {
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stub = RecordServiceGrpc.newStub(channel);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void read(ReadCollector collector, String key) {
        Rec.ReadRequest request = Rec.ReadRequest.newBuilder().setKey(key).build();
        stub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).read(request, new ReadObserver<>(collector, path));
    }

    public void write(WriteCollector collector, String key, String value, int sequence, String clientId) {
        Rec.WriteRequest request = Rec.WriteRequest.newBuilder().setKey(key).setValue(value).setSequence(sequence).setClientId(clientId).build();
        stub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).write(request, new WriteObserver<>(collector, path));
    }

    /**
     * Closes the connection with the server. No need to use if this instance was created in a try-with-resources.
     */
    @Override
    public void close() {
        channel.shutdownNow();
    }
}
