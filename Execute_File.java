import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Execute_File {
		
	public Execute_File() throws InterruptedException{ 
		
		System.out.println("Runtime");
	    Runtime r = Runtime.getRuntime();
		try {	
			Process p = r.exec("cmd /c C:\\Temp\\_FileAnalysis.py");
			p.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null)
			{                
				System.out.println(line);
			}            
			p.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}	
	
	public static void main(String args[]) throws InterruptedException{
		new Execute_File();
	}
}