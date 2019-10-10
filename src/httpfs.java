/*
 * httpfs.java
 * Copyright (C) 2019 sebastien <sebastien@sebver>
 *
 * Distributed under terms of the MIT license.
 */


import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.nio.file.*;


public class httpfs
{

	public static void main(String[] args) throws IOException{
		int server_port = 8080; // Defines at which port the server will be listening at
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