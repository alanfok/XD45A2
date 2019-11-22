package UDP;


public enum PacketType 
{
	SYN,SYNACK,ACK,DATA,NAK,TEST,ERROR;
	


public static int TypeToNum(PacketType packetType) 
{
	switch(packetType) 
	{
		case SYN :return 0;
		case SYNACK : return 1;
		case ACK : return 2;
		case DATA : return 3;
		case NAK : return 4;
		case TEST: return 5;
		default : return -1;
	}		
}

public static PacketType NumToType(int value) 
{
	switch(value) 
	{
		case 0 :return PacketType.SYN;
		case 1 : return PacketType.SYNACK;
		case 2 : return PacketType.ACK;
		case 3 : return PacketType.DATA;
		case 4 : return PacketType.NAK;
		case 5 : return PacketType.TEST;
		default : return PacketType.ERROR;
	}		
}

}

