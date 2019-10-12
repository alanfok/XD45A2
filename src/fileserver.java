import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class fileserver {
	private File sever_file;
	private int server_port;
	private String [] strArr;
	private String path;
	private String http = "";
	private String userAgent = "";
	private String data ="";
	private boolean isError = false;
	
	private final static String SERVER_OK = "200 OK";
	private final static String SERVER_Not_Found  = "404 Not Found";
	private final static String SERVER_Created = "201 Created";
	private final static String SERVER_Bad_Request  = "400 Bad Request";
	private final static String SERVER_Forbidden = "200 Forbidden";
	
	public fileserver(int server_port, String path) {
		this.path = path;
		this.server_port = server_port;
		
	}
	
	
	public void run() throws IOException{
		try( ServerSocket server = new ServerSocket(server_port,1,InetAddress.getLoopbackAddress()) ){
			
			// Server is a process that runs continously and awaits for requests from clients
			while(true){
				// Is this a blocking or non-blocking call?
				// What would you need to do to service multiple clients at the same time?
				try ( Socket client_connection = server.accept() ) 
				{		
					BufferedReader br = new BufferedReader(new InputStreamReader(client_connection.getInputStream()));
					String str;
					
					PrintWriter outbount_client = new PrintWriter(client_connection.getOutputStream(), true);
					outbount_client.println("the Folder Path is :" + path);
					//outbount_client.println("Command received. Command is : " + str);
					String sss ="";
						while((str = br.readLine())!=null)
						{ 
							if(str.equalsIgnoreCase("")) 
							{
								break;
							}
							
							if(str.substring(0,1).equals("\"")&&str.substring(str.length()-1,str.length()).equals("\"")) 
							{
								data = str.substring(1, str.length()-1);
							}
							outbount_client.println(str);
							sss = sss +" " +str;
						}
	
					strArr = sss.split("\\s+");
					
					for(int i = 0 ; i < strArr.length ; i++)
					{
						if(strArr[i].contains("HTTP"))
						{
							this.http = strArr[i];
						}
						if(strArr[i].contains("User-Agent"))
						{
							this.userAgent = strArr[i]+" "+strArr[i+1];
						}						
					}
					
					for(int i = 0 ; i < strArr.length ; i++) 
					{
						if(strArr[i].equalsIgnoreCase("Get"))
						{
							if(strArr[i+1].equalsIgnoreCase("/")&&strArr[i+1].length()==1) 
							{
								allfiles(outbount_client);
								//System.out.println("print all files in the Folder");
							}
							else if(strArr[i+1].contains("/")&&strArr[i+1].length()>1)
							{
								//System.out.println("find the specific file in foler");
								sendfiles(outbount_client,strArr[i+1]);
							}
							else 
							{	
								isError = true;
							}				
						}
						//post
						else if(strArr[i].equalsIgnoreCase("Post")) 
						{	
							System.out.println("Post");				
							System.out.println(strArr[i+1]);
							try {
								postGetFile(strArr[i+1],outbount_client,data);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}
					
					if(isError) 
					{
						Bad_request(outbount_client);
					}
				}
			}
		}
	}
	
	
	private void allfiles(PrintWriter out){
	String response = "";
	try{
		
		this.sever_file = new File(path);
		String[] listOfFiles = sever_file.list();	
		String contextLength = "Content-Length :"+ Integer.toString(listOfFiles.length);
		String contentType = "Content-Type: text/htm";

		response = http +" "+SERVER_OK+"\r\n"+ userAgent +"\r\n\r\n";
			for (int i = 0; i < listOfFiles.length; i++)
			{
				//just for testing
				response = response + listOfFiles[i]+"\r\n";
				//System.out.println(listOfFiles[i]);
				//out.println(listOfFiles[i]);     	
			}   	
		}
		catch(Exception e) 
		{
			response = http +" "+SERVER_Not_Found+"\r\n"+ userAgent +"\r\n\r\n";
		}
		
	 out.print(response);
	 out.println();
	 out.flush();
	 out.close();
	}
	
	private void sendfiles(PrintWriter out , String filePath){
	System.out.println("Printing list of files in directory");
	String response = "";
	int len = 0;
	String temp ="";
	try{
		String fullFilePath = path + filePath;
		//System.out.println(path);
		//System.out.println(fullFilePath);
		String contentType = "Content-Type: text/html";
		response = http +" "+SERVER_OK+"\r\n"+ userAgent +"\r\n"+ contentType+"\r\n";
		FileReader fr = new FileReader(fullFilePath); 
    	BufferedReader br = new BufferedReader(fr);
    	String s; 
    	
	    	while((s = br.readLine()) != null) 
	    	{
	    		len = len +s.length();
	    		temp = temp + s +"\r\n";
	    	} 

		}
		catch(Exception e) 
		{
			response = http +" "+SERVER_Not_Found+"\r\n"+ userAgent +"\r\n\r\n";
		}
		String contextLength = "Content-Length :"+Integer.toString(len);
		response = response + contextLength +"\r\n";
		response = response + temp +"\r\n";
		 out.print(response);
		 out.println();
		 out.flush();
		 out.close();
	}
	
	private void postGetFile(String fileName, PrintWriter outStream, String content) throws Exception
	{
		System.out.println("POST Receiving file: " + fileName);
		
        
		//need to check if file already exists then overwrite
		  try {
		   String createPath = path+fileName;	
           File file = new File(createPath);
           file.getParentFile().mkdirs();
           FileWriter writer = new FileWriter(file, false);
           PrintWriter output = new PrintWriter(writer);

           output.print(content);
           output.flush();
           output.close();            
           String response = http +" "+SERVER_Created+"\r\n"+ userAgent +"\r\n\r\n";
           	outStream.println(response);
       
	        outStream.println();
	        outStream.flush(); 
	        outStream.close();
		  }
		  catch(Exception e) {
	           String response = http +" "+SERVER_Forbidden+"\r\n"+ userAgent +"\r\n\r\n";
	           	outStream.println(response);
	       
		        outStream.println();
		        outStream.flush(); 
		        outStream.close();
		  }
        System.out.println("POST File uploaded. File name: " + fileName);
        
        if (outStream.checkError())
        {
        	throw new Exception("Error while transmitting data.");    	
        }
	}
	
	public void Bad_request(PrintWriter out) {
		String response = http +" "+SERVER_Bad_Request+"\r\n"+ userAgent +"\r\n\r\n";
		out.println(response);
	}
}
