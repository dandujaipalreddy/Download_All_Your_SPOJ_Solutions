import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class saveToFile {
	
	private static String filename;
	private static String content;
	public saveToFile(String filename,String content){
		this.filename=filename;
		this.content=content;
		
	}
	public static void saveit() {
	
		try {
			
			File file = new File(filename);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}