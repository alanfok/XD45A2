package UDP;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;



import static java.nio.channels.SelectionKey.OP_READ;

public class UDPClient {
		public String routerHost ;
		public String serverHost;
		public int routerPort;
		public int serverPort;
		public long SequenceNumber;
		public SocketAddress routerAddress;
		public InetSocketAddress serverAddress;
		public byte [] bytearr;
		public HashMap<Long, byte []> packetMap;
		public long start;
		public long end;
		public int windowSize;
		public long packetTotal;
		
		public UDPClient (String routerHost , int routerPort , String serverHost, int serverPort) 
		{
			 this.packetMap = new HashMap<Long, byte []>();
			 this.routerHost = routerHost;
			 this.routerPort = routerPort;
			 this.serverHost = serverHost;
			 this.serverPort = serverPort;
			 routerAddress = new InetSocketAddress(routerHost,routerPort);
			 serverAddress = new InetSocketAddress(serverHost, serverPort); 
		}
		
	
		
		//	SYN,SYNACK,ACK,DATA,NAK;
		
		public void run() 
		{
			try {
				//runClient(this.routerAddress, this.serverAddress);
				runClientHandShake(this.routerAddress, this.serverAddress);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run2(byte fileArray[]) 
		{
			long packet_number = 1;
			System.out.println("file bypt len : "+ fileArray.length);
			byte[] data = new byte[1013];
			int index = 0;
			System.out.println();
			for(byte by: fileArray) 
			{
				if(index==1013)
				{
					packetMap.put(packet_number, data);
					packet_number ++;
					/*
					try {
						packetMap.put(packet_number, data);
						//sendpacket(this.routerAddress, this.serverAddress,data);
						packet_number ++;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					*/
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
			if(fileArray.length%1013 !=0)
			{
				packetMap.put(packet_number, data);
				/*
				try {
					packetMap.put(packet_number, data);
					//sendpacket(this.routerAddress, this.serverAddress,data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
			/*
			for(long key : packetMap.keySet())
			{
				try {
					sendpacket(this.routerAddress,this.serverAddress,packetMap.get(key));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
			
			this.packetTotal = packet_number;
			start = 1;
			end =start +4;
			if(end > this.packetTotal)//total oacket 17 5 10 15 20 but 20 >17
			{
				end = this.packetTotal;
			}
			System.out.println("tain");
			recall(start,end);
			
		}

		public void recall(long start,long end) 
		{
			if (end > this.packetTotal)
			{
				end = this.packetTotal;
			}
			for(long i = start; i < end+1 ;i++)
			{
				try {
					sendpacket(this.routerAddress,this.serverAddress,packetMap.get(i));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!(end==this.packetTotal))
			{
				this.start = end +1;
				this.end = this.start + 4;
				recall(this.start,this.end);
			}
		}
		
		private void sendpacket(SocketAddress routerAddr, InetSocketAddress serverAddr,byte by[]) throws IOException {
	       
			try(DatagramChannel channel = DatagramChannel.open()){
				long s = SequenceNumber ;
	            //String msg = "send packet";
	            //System.out.println(msg.getBytes());
	            Packet p = new Packet.Builder()
	                    .setType(PacketType.TypeToNum(PacketType.TEST))
	                    .setSequenceNumber(SequenceNumber++ + 1L)
	                    .setPortNumber(serverAddr.getPort())
	                    .setPeerAddress(serverAddr.getAddress())
	                    .setPayload(by)
	                    .create();
	 
	            channel.send(p.toBuffer(), routerAddr);

	   //         logger.info("Sending \"{}\" to router at {}", msg, routerAddr);
	           // System.out.println("Sending "+msg+" to router at "+routerAddr);
	            // Try to receive a packet within timeout.
	            channel.configureBlocking(false);
	            Selector selector = Selector.open();
	            channel.register(selector, OP_READ);
	        //    logger.info("Waiting for the response");
	            System.out.println("Waiting for the response");
	            selector.select(5000);

	            Set<SelectionKey> keys = selector.selectedKeys();
	            if(keys.isEmpty()){
	        //        logger.error("No response after timeout");
	            	System.out.println("No response after timeout");
	                return;
	            }

	            // We just want a single response.
	            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
	            SocketAddress router = channel.receive(buf);
	            buf.flip();
	            Packet resp = Packet.fromBuffer(buf);
	            //logger.info("Packet: {}", resp);
	            //logger.info("Router: {}", router);
	            System.out.println("Packet: "+resp);
	            System.out.println("Router: "+router);
	            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
	            //logger.info("Payload: {}",  payload);
	            System.out.println("Seq "+resp.getSequenceNumber());
	            System.out.println("Payload: "+payload);
	            keys.clear();
	        }
	    }
		
	  private void runClient(SocketAddress routerAddr, InetSocketAddress serverAddr) throws IOException {
	        try(DatagramChannel channel = DatagramChannel.open()){
	            //String msg = "send packet";
	            //System.out.println(msg.getBytes());
	            Packet p = new Packet.Builder()
	                    .setType(0)
	                    .setSequenceNumber(SequenceNumber + 1L)
	                    .setPortNumber(serverAddr.getPort())
	                    .setPeerAddress(serverAddr.getAddress())
	                    .setPayload(this.bytearr)
	                    .create();
	            channel.send(p.toBuffer(), routerAddr);

	   //         logger.info("Sending \"{}\" to router at {}", msg, routerAddr);
	           // System.out.println("Sending "+msg+" to router at "+routerAddr);
	            // Try to receive a packet within timeout.
	            channel.configureBlocking(false);
	            Selector selector = Selector.open();
	            channel.register(selector, OP_READ);
	        //    logger.info("Waiting for the response");
	            System.out.println("Waiting for the response");
	            selector.select(5000);

	            Set<SelectionKey> keys = selector.selectedKeys();
	            if(keys.isEmpty()){
	        //        logger.error("No response after timeout");
	            	System.out.println("No response after timeout");
	                return;
	            }

	            // We just want a single response.
	            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
	            SocketAddress router = channel.receive(buf);
	            buf.flip();
	            Packet resp = Packet.fromBuffer(buf);
	            //logger.info("Packet: {}", resp);
	            //logger.info("Router: {}", router);
	            System.out.println("Packet: "+resp);
	            System.out.println("Router: "+router);
	            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
	            //logger.info("Payload: {}",  payload);
	            System.out.println("Seq "+resp.getSequenceNumber());
	            System.out.println("Payload: "+payload);
	            keys.clear();
	        }
	    }
	  
	  
	  private void runClientHandShake(SocketAddress routerAddr, InetSocketAddress serverAddr) throws IOException {
	        try(DatagramChannel channel = DatagramChannel.open()){
	            String msg = "Hi S";
	            Packet p = new Packet.Builder()
	                    .setType(PacketType.TypeToNum(PacketType.SYN))
	                    .setSequenceNumber(0L + SequenceNumber)
	                    .setPortNumber(serverAddr.getPort())
	                    .setPeerAddress(serverAddr.getAddress())
	                    .setPayload(msg.getBytes())
	                    .create();
	            channel.send(p.toBuffer(), routerAddr);

	   //         logger.info("Sending \"{}\" to router at {}", msg, routerAddr);
	            System.out.println("Sending "+msg+" to router at "+routerAddr);
	            // Try to receive a packet within timeout.
	            channel.configureBlocking(false);
	            Selector selector = Selector.open();
	            channel.register(selector, OP_READ);
	        //    logger.info("Waiting for the response");
	            System.out.println("Handshaking #1 SYN packet, 1/3 handshake");
	            System.out.println("Waiting for the response");
	            selector.select(5000);

	            Set<SelectionKey> keys = selector.selectedKeys();
	            if(keys.isEmpty()){
	        //        logger.error("No response after timeout");
	            	System.out.println("No response after timeout");
	                return;
	            }

	            // We just want a single response.
	            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
	            SocketAddress router = channel.receive(buf);
	            buf.flip();
	            Packet resp = Packet.fromBuffer(buf);
	            int type = resp.getType();
	            //logger.info("Packet: {}", resp);
	            //logger.info("Router: {}", router);
	            System.out.println("Packet: "+resp);
	            System.out.println("Router: "+router);
	            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
	            //logger.info("Payload: {}",  payload);
	            System.out.println("Payload: "+payload);
	            if(PacketType.NumToType(type).equals(PacketType.SYN)) 
	            {
	            	this.SequenceNumber = resp.getSequenceNumber();
	            	System.out.println("Seq "+this.SequenceNumber);
	            	System.out.println("ACK from server.");
	            	System.out.println("2/3 handshake.");
	            	//runClient(routerAddr,serverAddr);
	            	String msg1 ="3/3 handshake";
		            Packet p1 = new Packet.Builder()
		                    .setType(PacketType.TypeToNum(PacketType.ACK))
		                    .setPayload(msg1.getBytes())
		                    .setSequenceNumber(0L + SequenceNumber)
		                    .setPortNumber(serverAddr.getPort())
		                    .setPeerAddress(serverAddr.getAddress())              
		                    .create();
		            channel.send(p1.toBuffer(), routerAddr);
	            }
	            keys.clear();
	        }
	    }
	  }

