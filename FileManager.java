import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class FileManager {

	public boolean createDir(String path, String dir) throws IOException {
		dir = path + "/" + dir;
		File f = new File(dir);
		return f.mkdirs();
	}
	
	public boolean uploadFile(String path, String fileName, byte[] bytes) {
		
		fileName =  path + "/" + fileName;
		File f = new File(fileName);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			fos.write(bytes,0,bytes.length);
			fos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean deleteFile(String path, String fileName) {
		fileName = path + "/" + fileName;
		File f = new File(fileName);
		return f.delete();
	}
	
	public ArrayList<Map<String,String>> getFilesDetails(String path) {
		ArrayList<Map<String,String>> files = new ArrayList<Map<String,String>>();
		
		File root = new File(path);
		File[] currentTier = root.listFiles();
		
		Map<String,String> fileDetails = null;
		
		for (int i=0; i<currentTier.length; i++) {
			fileDetails = new HashMap<String,String>();
			fileDetails.put("Name",currentTier[i].getName());
			if (currentTier[i].isDirectory()) {
				fileDetails.put("Type","File Folder");
				fileDetails.put("Size","");
			} else {
				String[] components = currentTier[i].getName().split("\\.");
				String extension = (components.length<2)?"File":components[components.length-1];
				fileDetails.put("Type",extension);
				
				Long size = currentTier[i].length();
				String fileSize;
				DecimalFormat df = new DecimalFormat("#.#");
				if (size > 1048576) {
					fileSize = df.format((double)currentTier[i].length()/1048576) + " MB";
				} else if (size > 1024) {
					fileSize = df.format((double)currentTier[i].length()/1024) +" KB";
				} else {
					fileSize = currentTier[i].length() + " B";
				}
				fileDetails.put("Size",fileSize);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy HH:mm z");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			fileDetails.put("Last Modified",sdf.format(currentTier[i].lastModified()));
			files.add(fileDetails);
		}
		
		return files;
	}
	
}
