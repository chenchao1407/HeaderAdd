import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Queue;


public class Header_Add {
	public static void find_ALL_FILES(int type, boolean add){//0 for xml, 1 for java, 2 for cpp and h
		String path = "/home/chaochen/work/2014/GVRF/backup/new_github/Gear-VR-Hybrid/GVRf/";
		File f = new File(path);
		Queue<File> file_path = new LinkedList<File>();
		file_path.add(f);
		StringBuilder header = readHeader();
		while(!file_path.isEmpty()){
			File tmp_file = file_path.poll();
			if(tmp_file.getAbsolutePath().contains("contrib"))
				continue;
			if(tmp_file.isDirectory()){
				File[] file_list = tmp_file.listFiles();
				for(File ff : file_list)
					file_path.add(ff);
			}else{
				String file_name = tmp_file.getAbsolutePath();
				String extension = getExtensionName(file_name);
				if(type == 0 && extension.contains("xml") && !file_name.contains("lint.xml")){	
			        if(add)
				    handlexmlFile(file_name, header);
			        else
			            checkFile(file_name);
				}else if(type == 1 && extension.contains("java") && !file_name.contains("R.java") && !file_name.contains("BuildConfig.java")){
				    checkFile(file_name);
				}else if(type == 2 && ((!extension.contains("class") && extension.contains("c")) || extension.contains("h") && !extension.contains("html"))){
				    checkFile(file_name);
				}else if(type == 3 && (extension.contains("mk") || extension.contains("sh"))){
				    checkFile(file_name);
				}				
			}
		}
	}
	
	public static void checkFile(String path){
	    boolean found = false;
	    try{
	        BufferedReader reader = new BufferedReader(new FileReader(path));
	        String line = null;	        
	        while((line = reader.readLine()) != null){
	            if(line.contains("Copyright 2015 Samsung Electronics Co.")){
	                found = true;
	                break;
	            }
	        }
	        reader.close();
	    }catch(IOException e){
	        e.printStackTrace();
	    }
	    if(!found)
	        System.out.println(path);
	}
	
	public static void handlexmlFile(String path, StringBuilder header){
		StringBuilder rewrite = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			int counter = 0;
			while((line = reader.readLine()) != null){

				if(counter == 0 && line.contains("version=")){
					rewrite.append(line);
					rewrite.append("\n");
					rewrite.append(header);				
					rewrite.append("\n");
				}else if(counter == 0){
					rewrite.append(header);				
					rewrite.append("\n");
					rewrite.append(line);
					rewrite.append("\n");
				}else{
					rewrite.append(line);
					rewrite.append("\n");					
				}
				counter++;
			}
			reader.close();
			RandomAccessFile mm = new RandomAccessFile(path, "rw");
			mm.writeBytes(rewrite.toString());
			mm.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public static String getExtensionName(String filename){
		int index = filename.lastIndexOf('.');
		if(index > -1 && index < filename.length() - 1){
			return filename.substring(index + 1);
		}
		return filename;
	}
	
	public static StringBuilder readHeader(){
		String path = "/home/chaochen/copyright_xml.txt";
		BufferedReader br = null;
		String line = null;
		StringBuilder sb = new StringBuilder();
		try{
			br = new BufferedReader(new FileReader(path));
			while((line = br.readLine()) != null){
				sb.append(line);
				sb.append("\n");
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		//System.out.println(sb.toString());
		return sb;
		
	}
	
	public static void main(String[] args){
	    find_ALL_FILES(2, false);
	}
}
