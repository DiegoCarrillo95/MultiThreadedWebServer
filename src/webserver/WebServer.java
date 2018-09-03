package webserver;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

	public static void main(String[] args) {
		// Set the port number.
		int port = 6789;

		// Establish the listen socket.
		try(ServerSocket serverSocket = new ServerSocket(port)) {
			
			// Process HTTP service requests in an infinite loop.
			while (true) {
				// Listen for a TCP connection request.
				Socket clientSocket = serverSocket.accept();
				// Construct an object to process the HTTP request message.
				HttpRequest request = new HttpRequest(clientSocket);
				// Create a new thread to process the request.
				Thread thread = new Thread(request);
				// Start the thread.
				thread.start();
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
