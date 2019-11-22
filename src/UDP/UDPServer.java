package UDP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

public class UDPServer {
    //private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
	public String http = "";
    public UDPServer() 
    {
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
              
                System.out.println("Type: "+type);
               
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
                	String msg = "Hi Client";
                   	resp = packet.toBuilder()
                			.setPayload(msg.getBytes())
                			.setType(PacketType.TypeToNum(PacketType.ACK))
                			.create();  
                }
                else if (PacketType.NumToType(type).equals(PacketType.FINISHREQ))
                {
                	
                }
                else if (PacketType.NumToType(type).equals(PacketType.TEST))
                {
                	String msg = new String(packet.getPayload(), UTF_8);
                	System.out.println(msg);
                	resp = packet.toBuilder()
                			.setType(PacketType.TypeToNum(PacketType.ACK))
                			.setSequenceNumber(packet.getSequenceNumber())
                			.setPayload(msg.getBytes())
                			.create();         
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
            	
                }
                channel.send(resp.toBuffer(), router);
                
            }
        }
}
}