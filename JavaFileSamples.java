import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class JavaFileSamples {
	
	public static void main(String[] args) throws IOException {		
		String strInput = "C://Temp//test.msg";   
		String strOutput = "C://Temp//proc.msg"; 
	    //System.out.println(stringToHex(strInput));
		//convertByteArrayToString();
        //System.out.println("Line Count = " + count(strOutput));
        processFile(strInput,strOutput);
	}	

	public static int count(String strFile){		    		
		File file =  new File(strFile);
        System.out.println("Handling " + file.getName());  
        System.out.println("Counting " + file.getName());
        int count = 0; 
        String line = new String();        
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            
            do
            {
                line = reader.readLine();                
                if (line.length() > 0)
                	count++;  
                System.out.println(line);  
                
            }
            while (reader.ready());  
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return count;
    }

    public static void processFile(String in,String out) throws FileNotFoundException{
    	
    	BufferedReader reader = new BufferedReader(new FileReader(in));
    	//DataInput f1 = new DataInputStream(new FileInputStream(in));
        DataOutput f2 = new DataOutputStream(new FileOutputStream(out));
        String line = new String();      
        
        try
        {  
            do
            {
                line = reader.readLine();
                if (//line != null
                    //&& line.indexOf("*") == -1
                    //&& line.indexOf("//") == -1
                    line.length() > -1)
                { 
                	
                	f2.writeBytes(line+"");
                    System.out.println(line);
                		
                }
                if (line == null){
                	//f2.writeBytes("\n\r");
                }
            }
            while (reader.ready());              
            //String temp = "..................ÿÿÿÿÿÿÿÿÿÿÿÿ................................................";
            //f2.write(temp.getBytes());
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    	
    }
    
    
    
    public static void convertByteArrayToString() {        
        byte[] byteArray = new byte[] {87, 79, 87, 46, 46, 46};        
        String value = new String(byteArray);        
        System.out.println(value);
    }   
    
	public static String stringToHex(String base){
	     StringBuffer buffer = new StringBuffer();
	     int intValue;
	     for(int x = 0; x < base.length(); x++)
	         {
	         int cursor = 0;
	         intValue = base.charAt(x);
	         String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
	         for(int i = 0; i < binaryChar.length(); i++)
	         {
	             if(binaryChar.charAt(i) == '1')
	             {
	                 cursor += 1;
	             }
	         }
	         if((cursor % 2) > 0)
	         {
	             intValue += 128;
	         }
	         buffer.append(Integer.toHexString(intValue) + " ");
	     }
	     return buffer.toString();
    }
	
	
}
