package UDP;


public enum PacketType 
{
	SYN,SYNACK,ACK,DATA,NAK,TEST,REQUEST,ERROR,FINISHREQ ;
	


public static int TypeToNum(PacketType packetType) 
{
	switch(packetType) 
	{
		case SYN :return 0;
		case SYNACK : return 1;
		case ACK : return 2;
		case DATA : return 3;
		case NAK : return 4;
		case REQUEST: return 5;
		case TEST: return 6;
		case FINISHREQ: return 7;
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
		case 5 : return PacketType.REQUEST;
		case 6 : return PacketType.TEST;
		case 7 : return PacketType.FINISHREQ;
		default : return PacketType.ERROR;
	}		
}

}

