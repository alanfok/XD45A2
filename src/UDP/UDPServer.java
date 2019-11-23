package UDP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

public class UDPServer {
    //private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
	public String http = "";
	public long serverSeq = 1;
	public HashMap<Long, byte []> packetMap;
	public long packetTotal;
	
    public UDPServer() 
    {
    	this.packetMap = new HashMap<Long, byte []>();
    }
     public void listenAndServe(int port) throws IOException {

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
           // logger.info("EchoServer is listening at {}", channel.getLocalAddress());
            System.out.print("EchoServer is listening at" + channel.getLocalAddress());
            ByteBuffer buf = ByteBuffer
                    .allocate(Packet.MAX_LEN)
                    .order(ByteOrder.BIG_ENDIAN);

            for (; ; ) {
                buf.clear();
                SocketAddress router = channel.receive(buf);

                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                buf.flip();
                int type = packet.getType();
              
                System.out.println("Type: "+PacketType.NumToType(type));
               
                //logger.info("Router: {}", router);

                // Send the response to the router not the client.
                // The peer address of the packet is the address of the client already.
                // We can use toBuilder to copy properties of the current packet.
                // This demonstrate how to create a new packet from an existing packet.
                Packet resp = null;
                if (PacketType.NumToType(type).equals(PacketType.SYN))
                {
                	 String payload = new String(packet.getPayload(), UTF_8);
                     System.out.println("Packet: "+packet);
                     //logger.info("Packet: {}", packet);
                     System.out.println("Payload: "+payload);
                     //logger.info("Payload: {}", payload);
                     System.out.println("Router: "+router);
                     System.out.println("seq " + packet.getSequenceNumber());
                   	resp = packet.toBuilder()
                			.setPayload(payload.getBytes())
                			.setType(PacketType.TypeToNum(PacketType.SYNACK))
                			.create();  
                   	channel.send(resp.toBuffer(), router);
                }
                else if (PacketType.NumToType(type).equals(PacketType.ACK))
                {
                	String payload = new String(packet.getPayload(), UTF_8);
                    System.out.println("Packet: "+packet);
                    //logger.info("Packet: {}", packet);
                    System.out.println("Payload: "+payload);
                    //logger.info("Payload: {}", payload);
                    System.out.println("Router: "+router);
                    System.out.println("seq " + packet.getSequenceNumber());
                }
                
                else if (PacketType.NumToType(type).equals(PacketType.FINISHREQ))
                {
                	String result = new String(packet.getPayload(), UTF_8);
                	String strArrs[] = result.split("\r\n\r\n");
                	Request.instance().setStrArr(strArrs);
                	String response = "";
                	fileserver fs = new fileserver();
                	if(Request.instance().getMethod().equalsIgnoreCase("get"))
                	{	
                		if(Request.instance().getCommand().equalsIgnoreCase("/")&&Request.instance().getCommand().length()==1) 
                		{
                			response = fs.allfilesForA3();
                			System.out.println("len "+response.getBytes().length);
                			resp = packet.toBuilder()
                					.setSequenceNumber(this.serverSeq)
                					.setPayload(response.getBytes())
                					.setType(PacketType.TypeToNum(PacketType.DATA))
                					.create();  
                			channel.send(resp.toBuffer(), router);		
                			this.serverSeq ++;
                		}
                		else if(Request.instance().getCommand().contains("/")&&Request.instance().getCommand().length()>1)
                		{
                			
                			long packet_number = 1;
                			response = fs.sendfilesForA3(Request.instance().getCommand());
                			System.out.println("len "+response.getBytes().length);
                			byte fileArray [] = response.getBytes();


                			////
                			byte[] data = new byte[1013];
                			int index = 0;
                			System.out.println();
                			for(byte by: fileArray) 
                			{
                				if(index==1013)
                				{
                					packetMap.put(packet_number, data);
                					packet_number ++;
                					data= new byte[1013];
                					index = 0;
                					data[index] = by;
                					index ++;					
                				}
                				else 
                				{
                					data[index] = by;
                					index ++;
                				}	
                			}
                			
                			// put the packet Array if something remaining
                			if(fileArray.length % 1013 !=0)
                			{
                				packetMap.put(packet_number, data);
                			}

                			this.packetTotal = packet_number;
                			////
                			long revKey = 0;
                			
                			
                			for(long key : packetMap.keySet())
                			{
                				System.out.println(key);
	                			resp = packet.toBuilder()
	                					.setSequenceNumber(key)
	                					.setPayload(packetMap.get(key))//send
	                					.setType(PacketType.TypeToNum(PacketType.DATA))
	                					.create();  
	                			channel.send(resp.toBuffer(), router);
	                			revKey = key;
	                			//this.serverSeq ++;
                			}
                			
                			
                			
                			resp = packet.toBuilder()
                					.setSequenceNumber(revKey+1)
                					.setType(PacketType.TypeToNum(PacketType.FINISHREQ))
                					.create();  
                			channel.send(resp.toBuffer(), router);
                			
                			
                			
                			
                		}
                		else
                		{
                			
                		}
                	}
                	else if(Request.instance().getMethod().equalsIgnoreCase("post"))
                	{
                		 try {
							response = fs.postGetFileForA3(Request.instance().getCommand(),Request.instance().getData());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	else
                	{
                		System.out.println("me cry cry ");
                	}
                }
                
                else if (PacketType.NumToType(type).equals(PacketType.DATA))
                {
                	http = http + new String(packet.getPayload(), UTF_8);
                	System.out.println(http);
                	resp = packet.toBuilder()
                			.setType(PacketType.TypeToNum(PacketType.ACK))
                			.setSequenceNumber(packet.getSequenceNumber())
                			.setPayload(http.getBytes())
                			.create();    
                	channel.send(resp.toBuffer(), router);
                }             
                else
                {
                	 String payload = new String(packet.getPayload(), UTF_8);
                     System.out.println("Packet: "+packet);
                     //logger.info("Packet: {}", packet);
                     System.out.println("Payload: "+payload);
                     //logger.info("Payload: {}", payload);
                     System.out.println("Router: "+router);
                     System.out.println("seq " + packet.getSequenceNumber());
                	resp = packet.toBuilder()
                			.setPayload(payload.getBytes())
                			.create();                	
                	channel.send(resp.toBuffer(), router);
                }           
            }
        }
}


}