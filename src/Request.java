
import java.util.ArrayList;

public class Request {
	public static Request request = null;
	private String method = "";
	private String strArr [];
	private String http = "HTTP/1.0";
	private String userAgent = "";
	private String command = "";
	private String data= "";
	private String Content_Length ="";
	private ArrayList<String> Header = new ArrayList<String>();
	
	
	
	public ArrayList<String> getHeader() {
		return Header;
	}
	public String getContent_Length() {
		return Content_Length;
	}
	public String getHttp() {
		return http;
	}
	public String getData() {
		return data;
	}
	public String getCommand() {
		return command;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public String[] getStrArr() {
		return strArr;
	}
	
	
	public void setStrArr(String[] strArr) {
		this.strArr = strArr;
		if(strArr.length>1) 
		{
			this.data = strArr[1];
		}
		strArr = strArr[0].split("\r\n");
		try
		{		
			for(int i = 0 ; i < strArr.length ; i++)
			{
				if(strArr[i].contains("HTTP/"))
				{
					String temp [] = strArr[i].split("\\s+");
					try {
						this.method = temp[0];
						this.command = temp[1];
						this.http = temp[2];
;					}
					catch(Exception e)
					{
						
					}
				}
				if(strArr[i].contains("User-Agent"))
				{
					this.userAgent = strArr[i];
				}
				if(strArr[i].contains("Content-Length"))
				{
					this.Content_Length = strArr[i];
				}
				if(i!=0)
				{
					this.Header.add(strArr[i]);
				}
//				if(strArr[i].substring(0, 1).equalsIgnoreCase("\"")&&strArr[i].substring(strArr[i].length()-1, strArr[i].length()).equalsIgnoreCase("\"")) 
//				{
//					this.data = strArr[i].substring(1, strArr[i].length()-1);
//				}
				
			}
			/*
			    method = strArr[0];
				command = strArr[1];
			
				for(int i = 2 ; i < strArr.length ; i++)
				{
					if(strArr[i].contains("HTTP"))
					{
						this.http = strArr[i];
					}
					if(strArr[i].contains("User-Agent"))
					{
						this.userAgent = strArr[i]+" "+strArr[i+1];
					}
					if(strArr[i].contains("Content-Length"))
					{
						this.Content_Length = strArr[i];
					}
					if(strArr[i].contains("Content-Type"))
					{
						Content_Type.add(strArr[i]);
					}
					if(strArr[i].substring(0, 1).equalsIgnoreCase("\"")&&strArr[i].substring(strArr[i].length()-1, strArr[i].length()).equalsIgnoreCase("\"")) 
					{
						this.data = strArr[i].substring(1, strArr[i].length()-1);
					}
					if(strArr[i].substring(0, 1).equalsIgnoreCase("\""))
					{
						this.data = "";
						for(int j = i ; j<strArr.length ; j++) 
						{
							this.data = this.data +" "+strArr[j];
							if(strArr[j].substring(strArr[j].length()-1, strArr[j].length()).equalsIgnoreCase("\"")) 
							{
								break;
							}
						}
						this.data = this.data.substring(1, strArr[i].length()-1);
					}
				
		
				}
				*/
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	
	
	public String getMethod() {
		return method;
	}

	
	public static Request instance() 
	{
		if(request == null) 
		{
			request = new Request();
		}
		return request;		
	}
	
	
}
