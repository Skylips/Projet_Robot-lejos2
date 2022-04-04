package portailEV3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP extends Thread{
	
	volatile boolean is_connect = false;
	volatile boolean disconnect = false;
	private int port=-1;
	
	@Override
	public void run() {
		while(true) {
			if(is_connect == false){
				if(port!=-1) {
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
			}	
		}
	}
	
	public void connectionTCP(int port) {
		this.port = port;
	}
	
	public void disconnect() {
		this.disconnect = true;
	}
	
	public boolean getTransmit1() {
		return this.is_connect;
	}

}
