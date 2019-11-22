import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import UDP.UDPClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class httpcc {
	private static String commandArr [];
	public static void main(String[] args) throws Exception {
//	    String argArr [] ;
//	   
		int routerPort = 3000;
		int serverPort = 8007;
		String routerHost = "localhost";
		String serverHost = "localhost";
//		Scanner sc = new Scanner(System.in);
//	    System.out.println("Enter arg");
//	    
//	    String arg = sc.nextLine(); 
//		URI uri;
//		String url;
//		String urlArg = null;
//	    
//	   // bytearr  = userName.getBytes();
//	    
//	   
//	   // System.out.println(bytearr);
		UDPClient client = new UDPClient(routerHost, routerPort,serverHost,serverPort);
//		//client.bytearr = bytearr;
//		//client.runClient();
//		String getCommand = "";
//		String sendMessage ="";
//
//		argArr = arg.split("\\s+");
//		for(int i = 0; i<argArr.length ;i++)
//		{
//			if(argArr[0].contains("get"))
//			{
////        		uri = new URI(commandArr[i]);
////        		url = uri.getHost();
////        		port =uri.getPort();
////        		urlArg = uri.getRawPath();
//				client.run();
//				
//				
//			}
//			else if(argArr[0].contains("post"))
//			{
//				/*
//				InputStream is = new BufferedInputStream(new FileInputStream("/Users/fokpoonkai/Desktop/test/test.jpg"));
//				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//				byte[] data = new byte[16384];
//				int nRead;
//				while ((nRead = is.read(data, 0, data.length)) != -1) {
//				  buffer.write(data, 0, nRead);
//				}
//				*/
//				client.run();
//				byte[] fileArray = Files.readAllBytes(Paths.get("/Users/fokpoonkai/Desktop/test/vvvvvvvv.txt"));
//				client.run2(fileArray);
//				
//
//				
////				
////				
////				
////				for(int index = 0; index< fileArray.length;index++)
////				{	
////					int dataIndex = 0;
////					if(dataIndex==1013)
////					{
////						data[dataIndex] = fileArray[index];
////						packetArray.add(data);
////						data = new byte[1013];
////						dataIndex = 0;
////					}
////					else
////					{
////						data[dataIndex] = fileArray[index];
////						dataIndex ++;
////					}
////				}
////				
//				
//	
//			}
//			else 
//			{
//				System.out.print("Need get or post");
//			}
//			
//
//			
//			
//		}
		
		String getCommand = "";
		String sendMessage ="";
		String method ="";
		String data = null;
		int port = 8080;
		String host = "localhost";
		URI uri;
		String url;
		String urlArg = null;
		HashMap <String,String> header = new HashMap <String,String>();
		if(args.length>0) 
		{
			commandArr = args;
		}
		else
		{
			input();
		}
		Socket socket = null;
	

		try {

		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			// Closing the socket
			try {
				// Send the message to the server
	            /*
				java.io.OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				*/
				if(commandArr.length == 0) 
				{
					System.out.println("miss query");
				}
				else
				{
					try {
				        for(int i = 0; i<commandArr.length;i++) 
				        {
				        	method = commandArr[0];
				        	
				        	if(commandArr[i].equalsIgnoreCase("-d"))
				        	{
				        			data = commandArr[i+1];				        			

				        	}
				        	if(commandArr[i].contains("http://"))
				        	{
				        		uri = new URI(commandArr[i]);
				        		url = uri.getHost();
				        		port =uri.getPort();
				        		urlArg = uri.getRawPath();
				        	}
				        	if(commandArr[i].equalsIgnoreCase("-h"))
				        	{
				        		if(commandArr[i+1].contains(":"))
				        		{
				        			String temp [] = commandArr[i+1].split(":");
				        			header.put(temp[0], temp[1]);
				        		}
				        	}
				        	if(commandArr[i].equalsIgnoreCase("-f"))
				        	{
				        		
				        	}
				        }
					}
					catch(Exception e) {
						System.out.println("query has misstake");
					}  			        
				}
				
				InetAddress address = InetAddress.getByName(host);
				//socket = new Socket(host, port);
				
				//PrintWriter wtr = new PrintWriter(socket.getOutputStream());
				if(urlArg!=null) 
				{
					getCommand = urlArg;
				}
				
				sendMessage = method + " " + getCommand + " HTTP/1.0 \r\nUser-Agent: Concordia\r\n";
				
				if(header.size()>0) 
				{
					for (String key : header.keySet()) {
						sendMessage = sendMessage + key+":"+header.get(key)+"\r\n";
						}
				}
				
				if(data!=null)
				{
					sendMessage = sendMessage + "Content-Length:" +Integer.toString(data.length()) +"\r\n\r\n"+ data;
				}
				else
				{
					sendMessage = sendMessage +"\r\n\r\n";
				}
				sendMessage = sendMessage +"\r\n\r\n";
				
				
				System.out.println("\n*************************************************");
				System.out.println("Message sent to the server:\n" + sendMessage);
				//wtr.print(sendMessage);
				//wtr.print("");
				//wtr.flush();
				client.run();
				//client.run2(sendMessage.getBytes());
				byte [] brr = sendMessage.getBytes();
				System.out.println(brr.length);
				System.out.println(brr);
				client.sendHTTP(brr);
				
//				System.out.println("*************************************************");
//				System.out.println("-----Server response-----------");
//				// Get the return message from the server
//				java.io.InputStream is = socket.getInputStream();
//				InputStreamReader isr = new InputStreamReader(is);
//				BufferedReader br = new BufferedReader(isr);
//				//String message = br.readLine();
//
//				//reading stream input
//			
//				String line = br.readLine();
//				while (line != null) {
//					System.out.println(line);
//					line = br.readLine();
//					
//				}
//				
//				
//				//bw.flush();
//				br.close();
//				wtr.close();
//				System.out.println("*************************************************");
//				System.out.println("End of message exchange!!! ");
				
				//System.out.println("Message received from the server : " + message);
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				// Closing the socket
//				try {
//					socket.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		}
	}

	public static void input() {
		Scanner scan = new Scanner(System.in);
		String input = "";
		System.out.println("Please input your command:");
		input = scan.nextLine();
		commandArr = input.split("\\s+");
		
	}


}
