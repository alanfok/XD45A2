import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class fileserver {
	private File sever_file;
	private int server_port;
	private String [] strArr;
	private String path;
	public fileserver(int server_port, String path) {
		this.path = path;
		this.server_port = server_port;
		this.sever_file = new File(path);
	}
	
	
	public void run() throws IOException{
		try( ServerSocket server = new ServerSocket(server_port) ){
			
			// Server is a process that runs continously and awaits for requests from clients
			while(true){
				// Is this a blocking or non-blocking call?
				// What would you need to do to service multiple clients at the same time?
				try ( Socket client_connection = server.accept() ) 
				{		
					BufferedReader br = new BufferedReader(new InputStreamReader(client_connection.getInputStream()));
					String str = br.readLine();
					PrintWriter outbount_client = new PrintWriter(client_connection.getOutputStream(), true);
					outbount_client.println("the Folder Path is :" + path);
					outbount_client.println("Command received. Command is : " + str);
					strArr = str.split("\\s+");
					for(int i = 0 ; i < strArr.length ; i++) 
					{
						if(strArr[i].equalsIgnoreCase("Get"))
						{
							if(strArr[i+1].equalsIgnoreCase("/")) 
							{
								allfiles(outbount_client);
								//System.out.println("print all files in the Folder");
							}
							else if(!strArr[i+1].equalsIgnoreCase("/")||strArr[i+1].contains("/"))
							{
								System.out.println("find the specific file in foler");
							}
							else 
							{
								System.out.println("the get query has mistake");
							}
							
						}
						
						
						
						
					}
					client_connection.close();
	
				}
			}
		}
	}
	
	
	private void allfiles(PrintWriter out){
	System.out.println("Printing list of files in directory");
	String[] listOfFiles = sever_file.list();
	
	 for (int i = 0; i < listOfFiles.length; i++)
     {
		 //just for testing
	
		//System.out.println(listOfFiles[i]);
     	out.println(listOfFiles[i]);     	
     }      
	 out.println();
	 out.flush();
	 out.close();
	}
}
