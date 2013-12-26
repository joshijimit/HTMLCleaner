package test;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import au.com.bytecode.opencsv.CSVWriter;

public class HTMLCleaner {

	public static StringBuilder builder = null;
	public static String curQuestion = null;
	public static List<String[]> data = new ArrayList<String[]>();
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args)  {
		
		
		System.setProperty("http.proxyHost", "192.168.10.92");
		System.setProperty("http.proxyPort", "8080");
		HttpURLConnection lConn;
		try {
			URL url = new URL("http://xyz.com/test.html");
			lConn = (HttpURLConnection) url.openConnection();
			lConn.connect();
			final HtmlCleaner cleaner = new HtmlCleaner();
		    TagNode lNode = null;
		    try {
		    	lNode = cleaner.clean( lConn.getInputStream() );		    	
		    } catch (IOException e) {
		       e.printStackTrace();
		    }
		    
		    final String lTitle = "//*[@id=\"content\"]/div[1]/table/tbody";
		 
		    try {
		        Object[] info_nodes = lNode.evaluateXPath(lTitle);
		       		        
		        if (info_nodes.length > 0) {
		            
		            TagNode info_node = (TagNode) info_nodes[0];
		            if(info_node.getAllChildren().size() > 0){
		            	Iterator itr = info_node.getAllChildren().iterator();		                
		                while(itr.hasNext()){
		                	Object childNode = itr.next();		                	
		                	processNode(childNode);
		                	
		                }
		            }
		           
		        }
		        String csv = "D:\\Conexa\\Android\\test.csv";
				CSVWriter writer = new CSVWriter(new FileWriter(csv));
				
				writer.writeAll(data);
				 
				writer.close();

		    } catch (XPatherException e) {
		        e.printStackTrace();
		    }

		} catch (MalformedURLException e2) {			
			e2.printStackTrace();
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
		
	}
	
	private static void processNode(Object info_nodes) throws IOException{
		
		if(info_nodes instanceof TagNode){
			TagNode node = (TagNode)info_nodes;			
			if(node.getAllChildren().size() > 0){
				Iterator itr = node.getAllChildren().iterator();
				while(itr.hasNext()){
					
						Object childNode = itr.next();
		            	processNode(childNode);
					
	            }
			}
		}else if(info_nodes instanceof ContentNode){
			ContentNode node = (ContentNode)info_nodes;
			
			if(node.getContent().trim().startsWith("Q")){
								
				System.out.println(node.getContent().trim());				
				if(curQuestion != null){
					System.out.println(builder.toString());
					data.add(new String[] {curQuestion, builder.toString().trim().replace("'", "''")});					
				}	
				builder = new StringBuilder();
				curQuestion = node.getContent().trim().replace("'", "''");
			}else{
				if(builder != null){
					builder.append(node.getContent().trim());
					builder.append("\n");
				}
				
			}
			
		}
		
	}
}
