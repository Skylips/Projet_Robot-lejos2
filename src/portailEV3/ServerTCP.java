package portailEV3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP {
	
	volatile boolean is_connect = false;
	
	public void connectionTCP(int port) {
		try (ServerSocket serverSocket = new ServerSocket(port)){		
			
			System.out.println("Server is listennin | Port : "+port);
			
			if (is_connect == false) {
				Socket socket = serverSocket.accept();
				is_connect = true;
				System.out.println("New Client connected");
			}
			
		}catch (IOException ex) {
			System.out.println("Server Error : "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public boolean getTransmit1() {
		return this.is_connect;
	}

}
