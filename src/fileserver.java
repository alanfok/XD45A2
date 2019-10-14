import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class fileserver {
	private File sever_file;
	private int server_port;
	private String [] strArr;
	private String path;
	private boolean isError = false;
	
	private final static String SERVER_OK = "200 OK";
	private final static String SERVER_Not_Found  = "404 Not Found";
	private final static String SERVER_Created = "201 Created";
	private final static String SERVER_Bad_Request  = "400 Bad Request";
	private final static String SERVER_Forbidden = "403 Forbidden";
	
	public fileserver(int server_port, String path) {
		this.path = path;
		this.server_port = server_port;
		
	}
	
	
	public void run() throws IOException{
		try( ServerSocket server = new ServerSocket(server_port,2,InetAddress.getLoopbackAddress()) ){
			
			// Server is a process that runs continously and awaits for requests from clients
			while(true){
				// Is this a blocking or non-blocking call?
				// What would you need to do to service multiple clients at the same time?
				try ( Socket client_connection = server.accept() ) 
				{		
					BufferedReader br = new BufferedReader(new InputStreamReader(client_connection.getInputStream()));
					int c;
					int check = 0;
					int scendcheck = 0;
					StringBuilder response= new StringBuilder();

					
					PrintWriter outbount_client = new PrintWriter(client_connection.getOutputStream(), true);
					outbount_client.println("the Folder Path is :" + path);
					
						while((c = br.read())!=-1)
						{ 
							response.append((char)c ) ; 
							
							if((char)c =='\r') {
								check ++;
							}
							else if ((char)c =='\n')
							{
								check ++;
							}
							else
							{
								check = 0;
							}
							if(check == 4 ||check == 8) 
							{
								scendcheck ++;
							}
							if(scendcheck == 2) 
							{
								break;
							}
						};

					
					String result = response.toString();
					String strs ="";
					
					
					String strArrs[] = result.split("\r\n\r\n");
					strArr = result.split("\\s+");
	
					Request.instance().setStrArr(strArrs);
					

						if(Request.instance().getMethod().equalsIgnoreCase("get"))
						{
							if(Request.instance().getCommand().equalsIgnoreCase("/")&&Request.instance().getCommand().length()==1) 
							{
								allfiles(outbount_client);
								//System.out.println("print all files in the Folder");
							}
							else if(Request.instance().getCommand().contains("/")&&Request.instance().getCommand().length()>1)
							{
								//System.out.println("find the specific file in foler");
								sendfiles(outbount_client,Request.instance().getCommand());
							}
							else 
							{	
								isError = true;
							}				
						}
						//post
						else if(Request.instance().getMethod().equalsIgnoreCase("Post")) 
						{	
							System.out.println("Post");		
							try {
								postGetFile(Request.instance().getCommand(),outbount_client,Request.instance().getData());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							Bad_request(outbount_client);
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
		String contentType = "Content-Type: text/html";
		boolean checkType = false;
		response = Request.instance().getHttp() +" "+SERVER_OK+"\r\n";
		
		if(Request.instance().getHeader().size()>0)
		{
		      for (int counter = 0; counter < Request.instance().getHeader().size(); counter++) { 	
		    	  if(Request.instance().getHeader().get(counter).toLowerCase().contains("content-type"))
		    	  {
		    		  checkType = true;
		    	  }
		    	  response = response + Request.instance().getHeader().get(counter) + "\r\n";
		      }   	
		}
		
		if(!checkType)
		{
			response = response + contentType+ "\r\n";
		}
		
		response = response + "\r\n";
		
			for (int i = 0; i < listOfFiles.length; i++)
			{
				//just for testing
				response = response + listOfFiles[i]+"\r\n";
				//System.out.println(listOfFiles[i]);
				//out.println(listOfFiles[i]);     	
			}
			response = response +"\r\n\r\n";
		}
		catch(Exception e) 
		{
			response = Request.instance().getHttp() +" "+SERVER_Not_Found+"\r\n"+   Request.instance().getHeader().get(0) +"\r\n";
			response = response + "\r\n";
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
		if(filePath.contains(".."))
		{
	            response = Request.instance().getHttp() +" "+SERVER_Forbidden+"\r\n"+ Request.instance().getUserAgent() +"\r\n\r\n";
	           	out.println(response);
		        out.println();
		        out.flush(); 
		        out.close();
		}
		else
		{
			String fullFilePath = path + filePath;
			String contentType = "Content-Type: text/html";
			Boolean checkType = false;
			response = Request.instance().getHttp() +" "+SERVER_OK+"\r\n";
			if(Request.instance().getHeader().size()>0)
			{
			      for (int counter = 0; counter < Request.instance().getHeader().size(); counter++) { 	
			    	  if(Request.instance().getHeader().get(counter).toLowerCase().contains("content-type"))
			    	  {
			    		  checkType = true;
			    	  }
			    	  if(!Request.instance().getHeader().get(counter).toLowerCase().contains("content-length"))
			    	  {	  
			    		  response = response + Request.instance().getHeader().get(counter) + "\r\n";
			    	  }
			      }   	
			}
			
			if(!checkType)
			{
				response = response + contentType + "\r\n";
			}
			
			FileReader fr = new FileReader(fullFilePath); 
	    	BufferedReader br = new BufferedReader(fr);
	    	String s;   	
		    	while((s = br.readLine()) != null) 
		    	{
		    		len = len +s.length();
		    		temp = temp + s +"\r\n";
		    	}
		    	String contextLength = "Content-Length :"+Integer.toString(len);
		    	response = response + contextLength +"\r\n\r\n";
		    	response = response + temp +"\r\n\r\n";
		}
		}
		catch(Exception e) 
		{
			response = Request.instance().getHttp() +" "+SERVER_Not_Found+"\r\n"+Request.instance().getHeader().get(0)+"\r\n";

			response = response +"\r\n";
		}
	
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
			   if(fileName.contains(".."))
			   {
				   throw new Exception();
			   }
			   else
			   {
					   Boolean checkType = false;
					   String contentType = "Content-Type: text/html";
					   String createPath = path+fileName;	
			           File file = new File(createPath);
			           file.getParentFile().mkdirs();
			           FileWriter writer = new FileWriter(file, false);
			           PrintWriter output = new PrintWriter(writer);
			           output.print(content);
			           if(!Request.instance().overwrite)
			           {
			        	   file.setReadOnly();
			           }
			           output.flush();
			           output.close();            
			           String response = Request.instance().getHttp() +" "+SERVER_Created+"\r\n";
				   		if(Request.instance().getHeader().size()>0)
				   		{
				   		      for (int counter = 0; counter < Request.instance().getHeader().size(); counter++) { 	
				   		    	  if(Request.instance().getHeader().get(counter).toLowerCase().contains("content-type"))
				   		    	  {
				   		    		  checkType = true;
				   		    	  }
				   		    	  response = response + Request.instance().getHeader().get(counter) + "\r\n";
				   		      }   	
				   		}
			   		
			   		if(!checkType)
			   		{
			   			response = response + contentType+ "\r\n";
			   		}
		           	outStream.println(response);
		       
			        outStream.println();
			        outStream.flush(); 
			        outStream.close();
			   }
		  }
		  catch(Exception e) {
	           String response = Request.instance().getHttp() +" "+SERVER_Forbidden+"\r\n"+ Request.instance().getUserAgent() +"\r\n\r\n";
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
		String response = Request.instance().getHttp() +" "+SERVER_Bad_Request+"\r\n"+ Request.instance().getUserAgent() +"\r\n\r\n";
		out.println(response);
	}
	
}
