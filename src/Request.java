
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
	private ArrayList<String> Content_Type = new ArrayList<String>();
	
	
	
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
		try
		{
			method = strArr[0];
			command = strArr[1];
			
				for(int i = 0 ; i < strArr.length ; i++)
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
		
				}
			}
			catch(Exception e)
			{
					
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
