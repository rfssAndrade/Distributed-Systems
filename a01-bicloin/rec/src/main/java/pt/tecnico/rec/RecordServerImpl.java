package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.Rec.PingRequest;
import pt.tecnico.rec.grpc.Rec.PingResponse;
import pt.tecnico.rec.grpc.Rec.ReadRequest;
import pt.tecnico.rec.grpc.Rec.ReadResponse;
import pt.tecnico.rec.grpc.Rec.WriteRequest;
import pt.tecnico.rec.grpc.Rec.WriteResponse;
import pt.tecnico.rec.grpc.RecordServiceGrpc.RecordServiceImplBase;

import static io.grpc.Status.INVALID_ARGUMENT;

/**
 * Implementation of the server services.
 * 
 *
 */
public class RecordServerImpl extends RecordServiceImplBase {
	
	/**
	 * Record manager where the records are stored.
	 */
	private final RecordManager recordManager = new RecordManager();

	/**
	 * Pings the server.
	 * @param request			Protocol buffer with the request parameters
	 * @param responseObserver	Observer to send stream messages with the response
	 */
	public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
		String input = request.getInput();
		
		System.out.println("Received ping request: message=\"" + input + "\"");
		
		if (!isValidString(input)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(RecordResponseError.emptyPing()).asRuntimeException());
			return;
		}
		
		PingResponse response = PingResponse.newBuilder().setOutput(input).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	/**
	 * Returns a record from the server.
	 * @param request			Protocol buffer with the request parameters
	 * @param responseObserver	Observer to send stream messages with the response
	 */
	public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {
		String key = request.getKey();
		
		System.out.println("Received read request: key=\"" + key + "\"");
		
		if (!isValidString(key)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(RecordResponseError.emptyKey()).asRuntimeException());
			return;
		}
		RecordData data = recordManager.readRecord(key);

		ReadResponse response = ReadResponse.newBuilder().setValue(data.getValue()).setSequence(data.getSequence()).setClientId(data.getClientId()).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
	
	/**
	 * Stores a record in the server.
	 * @param request			Protocol buffer with the request parameters
	 * @param responseObserver	Observer to send stream messages with the response
	 */
	public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {
		String key = request.getKey();
		String value = request.getValue();
		int sequence = request.getSequence();
		String clientId = request.getClientId();
		
		System.out.println("Received write request: key=\"" + key + "\", value=\"" + value + "\"");
		
		if (!isValidString(key)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(RecordResponseError.emptyKey()).asRuntimeException());
			return;
		}
		
		recordManager.writeRecord(key, new RecordData(value, sequence, clientId));
		
		WriteResponse response = WriteResponse.getDefaultInstance();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * Checks if string is not null, empty or contains only white spaces codepoints.
	 * @param string	string to check
	 * @return			false if string is null, empty or contains only white spaces codepoints, otherwise true
	 */
	private boolean isValidString(String string) {
		return string != null && !string.isBlank();
	}
}
