package grpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.practice.grpc.Friend;
import com.practice.grpc.HelloRequest;
import com.practice.grpc.HelloResponse;
import com.practice.grpc.HelloServiceGrpc.HelloServiceImplBase;
import com.practice.grpc.Item;
import com.practice.grpc.Message;

import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

        String greeting = new StringBuilder()
          .append("Hello, ")
          .append(request.getFirstName())
          .append(" ")
          .append(request.getLastName())
          .toString();

        HelloResponse response = HelloResponse.newBuilder()
          .setGreeting(greeting)
          .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    /**
     * Server streaming RPC
     * The server gives the friend list back to the client by sending multiple features (friends)
     * */
    
    public HelloServiceImpl() {
    	friends = new ArrayList<Friend>();
    	friends.add(Friend.newBuilder().setFirstName("Friend").setLastName("1").build());
    	friends.add(Friend.newBuilder().setFirstName("Friend").setLastName("2").build());
    	friends.add(Friend.newBuilder().setFirstName("Friend").setLastName("3").build());
    }
    
    Collection<Friend> friends;
    
    @Override
    public void getFriendList(Message request, StreamObserver<Friend> responseObserver) {
    	for(Friend f:friends) {
    		responseObserver.onNext(f);
    	}
    	responseObserver.onCompleted();
    }
    
    /**
     * Client streaming RPC
     * The client gives a restaurant order to the server by sending multiple features (items)
     * 
     * When the client calls "restaurantOrder", the server gives back to the client a StreamObserver,
     * which will be used by the client to notify the server with multiple feature (item), and then stop the communication.
     * */
    
    @Override
    public StreamObserver<Item> restaurantOrder(final StreamObserver<Message> responseObserver){
    	
    	final Collection<Item> order = new ArrayList<Item>();
		
    	return new StreamObserver<Item>() {

    		//When the client notifies the server with a new feature
			@Override
			public void onNext(Item value) {
				
				//Add it to the order
				order.add(Item.newBuilder()
						.setName(value.getName())
						.setAmount(value.getAmount()).build());
				
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCompleted() {
				
				//Give back a string representing the order
				responseObserver.onNext(Message.newBuilder().setMessage(Arrays.toString(order.toArray())).build());
				
				//End communication
				responseObserver.onCompleted();
				
			}
    		
    	};
    	
    	
    }
    
    
    
    /**
     * Bidirectionnal streaming RPC
     * Receives messages and sends them back immediately, just like a parrot.
     * */
    @Override
    public StreamObserver<Message> parrot(final StreamObserver<Message> responseObserver){
    	
    	return new StreamObserver<Message>() {

			@Override
			public void onNext(Message value) {
				
				//Send back what's received
				responseObserver.onNext(value);
				//System.out.println("ok");
				
			}

			@Override
			public void onError(Throwable t) {
				System.out.println(t.getMessage());
			}

			@Override
			public void onCompleted() {
				
				//Close communication
				responseObserver.onCompleted();
				
			}
    	};
    }
    
   
}
