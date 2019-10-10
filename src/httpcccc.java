import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class httpcccc {
	private static String command = "";
	public static void main(String[] args) throws Exception {
		input();
		Socket socket = null;
	

		try {

		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			// Closing the socket
			try {
				String host = "localhost";
	            int port = 8080;
	            InetAddress address = InetAddress.getByName(host);
	            socket = new Socket(host, port);
	 

				// Send the message to the server
				java.io.OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);

		
				
				String sendMessage = getCommand().toString() +"\n";
				System.out.println("Here is the message:" + sendMessage);
				bw.write(sendMessage);
				
				
				bw.flush();
				System.out.println("Message sent to the server : " + sendMessage);
				System.out.println("End of message");
				
				

				// Get the return message from the server
				java.io.InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				//String message = br.readLine();

				//reading stream input
			
				String line = br.readLine();
				while (line != null) {
					System.out.println(line);
					line = br.readLine();
					
				}
				
				bw.flush();
				br.close();
				bw.close();
				System.out.println("End of message");
				
				//System.out.println("Message received from the server : " + message);
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				// Closing the socket
				try {
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void input() {
		Scanner scan = new Scanner(System.in);
		String input = "";
		System.out.println("Please input your command:");
		input = scan.nextLine();
		setCommand(input);
	}

	private static void setCommand(String c) {
		command = c;

	}

	static String getCommand() {
		return command;
	}

}
