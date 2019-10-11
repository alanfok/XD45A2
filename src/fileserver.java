import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class fileserver {
	private int server_port;
	public fileserver(int server_port) {
		this.server_port = server_port;
	}
	
	public void run() throws IOException{
		try( ServerSocket server = new ServerSocket(server_port) ){
			System.out.println("Server has been instantiated at port " + server_port);
	
			// Server is a process that runs continously and awaits for requests from clients
			while(true){
				// Is this a blocking or non-blocking call?
				// What would you need to do to service multiple clients at the same time?
				try ( Socket client_connection = server.accept() ) 
				{
					
					BufferedReader br = new BufferedReader(new InputStreamReader(client_connection.getInputStream()));
					String str = br.readLine();
					if(str.contains("GET")) 
					{
						
					}
					PrintWriter outbount_client = new PrintWriter(client_connection.getOutputStream(), true);
					outbount_client.println("Well hello to you too." + str);
					client_connection.close();
	
				}
			}
		}
	}
}
