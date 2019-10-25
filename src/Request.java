
import java.util.ArrayList;
import java.util.HashMap;

public class Request {
	public static Request request = null;
	private String method = "";
	private String strArr [];
	private String http = "HTTP/1.0";
	private String userAgent = "";
	private String command = "";
	private String data= "";
	private String Content_Length ="";
	//private ArrayList<String> Header = new ArrayList<String>();
	public HashMap <String, String> mHeader = new HashMap <String, String>();
	public boolean overwrite = true;
	
	
	
	public HashMap <String, String> getHeader() {
		return mHeader;
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
		this.mHeader.clear();
		this.overwrite = true;
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
					String temp [] = strArr[0].split("\\s+");
					try {
						this.method = temp[0];
						this.command = temp[1];
						this.http = temp[2];
;					}
					catch(Exception e)
					{
						
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
					if(strArr[i].contains(":")) 
					{	
						String temp1 [];
						temp1 = strArr[i].split(":");
						this.mHeader.put(temp1[0],temp1[1]);
					}
			
				}

			}
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
