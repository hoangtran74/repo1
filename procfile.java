import java.io.*;

import processstringclass.processstringclass;

public class procfile{

	static void processfile(String f1n, String f2n, processstringclass ps)
	{
	/*f1n=name of file to read from*/
	/*f2n=name of file to write to*/
		try{
			DataInput f1 = new DataInputStream(new FileInputStream(f1n));
			DataOutput f2 = new DataOutputStream(new FileOutputStream(f2n));
	
			//boolean writeok=true;
			String readin = null;			
			do
			{   
				readin = f1.readLine();
				if (readin != null
	                    //&& readin.indexOf("*") == -1
	                    //&& readin.indexOf("//") == -1
	                    && readin.length() > 0)				
		         //writeok =ps.processingstring(writeok);		       
		        f2.writeBytes(readin+"\r\n");
		        System.out.println(readin);		       
			}//end while
			while(readin!=null);
		}//end try
		catch (Exception ex){String err=ex.toString();
		       System.out.println(err);
		}
	}
	
	public static void main(String[] args) {
		processfile("C://Temp//test.msg", "C://Temp//proc.msg", null);

	}
}
