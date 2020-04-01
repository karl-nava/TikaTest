import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.metadata.*;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;

import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import java.io.StringWriter;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

public class pdfToJsonVer2 {
	public static void main(String args[]) throws IOException, SAXException, TikaException {
		//ToXMLContentHandler handler = new ToXMLContentHandler();
    	//SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/tset").build();
    	 
    	BodyContentHandler  handler = new BodyContentHandler ();
    	 Metadata metadata = new Metadata();
         File folder = new File("C:\\Users\\Karl\\Desktop\\PDFResume");
         String folderName= "C:\\Users\\Karl\\Desktop\\PDFResume";
         File[] files = folder.listFiles();
         
         
         FileWriter fileWriter = new FileWriter("Z:\\UTD\\EclipseJava\\TikaTest\\info.json");
         
         SolrInputDocument doc = new SolrInputDocument();
	     FileInputStream inputstream = new FileInputStream("C:\\Users\\Karl\\Desktop\\TestResume\\Resume.pdf");
	     ParseContext pcontext = new ParseContext();
	         
	     //parsing the document using PDF parser
	     PDFParser pdfparser = new PDFParser(); 
	     pdfparser.parse(inputstream, handler, metadata,pcontext);
	         
	     //getting the content of the document
	     String wordsInHandler = handler.toString();
	     //System.out.println(wordsInHandler);
	         
	     JSONObject obj = getJson(wordsInHandler);
	     
	     if(obj == null) {
	    	 System.out.print("Empty JSON");
	    	 return;
	     }

	     System.out.println("name: "+ obj.get("name"));
	     System.out.println("location: "+ obj.get("location"));
	     System.out.println("current_position: "+ obj.get("current_position"));
	     System.out.println("summary: "+ obj.get("summary"));
	     System.out.println("experience: "+ obj.get("experience"));
	     System.out.println	("education: "+ obj.get("education"));
	     System.out.println	("references: "+ obj.get("references"));
	     System.out.println	("notes: "+ obj.get("notes"));
	         

	         
	     /*for(String name : metadataNames) {
	      	 obj.put(name,metadata.get(name));
	      	 doc.addField(name, metadata.get(name));
	       	 //System.out.println(name+ " : " + metadata.get(name));
	     }*/
	     /*StringWriter out = new StringWriter ();
	     obj.writeJSONString(out);
	         
	     String jsonText = out.toString();
	     System.out.print(jsonText);*/
	     try {
	    	 fileWriter.write(obj.toJSONString());
	    	 fileWriter.write("\n");
	    	 //System.out.println(obj.toJSONString());
	    	 //client.add(doc);
	        	 
	     }
	     catch (Exception e) {
	         
	     }
	     //System.out.println(obj);
	     //client.commit();
	     fileWriter.flush();
	}
	public static ArrayList<String> findPerson(String content) {
		//Finds the person's name, location, and current position
		ArrayList<String> output = new ArrayList<String>();
		
		Scanner scan = new Scanner(content);
		scan.nextLine();

		String firstLine = scan.nextLine();
		if(firstLine.compareTo("")==0) {
			scan.close();
			return output;
		}
		output.add(firstLine);
		
		String secondLine = scan.nextLine();
		if(secondLine.compareTo("")==0) {
			scan.close();
			return output;
		}
		output.add(secondLine);
		
		String thirdLine = "";
		while(scan.hasNextLine()) {
			String nextLine = scan.nextLine();
			if(nextLine.compareTo("")==0) {
				break;
			}
			else
				thirdLine += nextLine+" ";
		}
		if(thirdLine.compareTo("")!=0)
			output.add(thirdLine);
		
		
		scan.close();
		
		return output;
	}
	public static String findSummary(String content) {
		//Finds the person's summary
		String output = "";
		Scanner scan = new Scanner(content);
		boolean found = false; //This checks the scanner is at the summary area
		while(scan.hasNextLine()) {
			String nextLine = scan.nextLine();
			if(found && nextLine.compareTo("")!=0) {
				if(nextLine.compareTo("Experience")==0||
						nextLine.compareTo("Education")==0)
					break;
				output+=nextLine+" ";
			}
			else 
				if(nextLine.compareTo("Summary")!=0)
					continue;
				else
					found=true;
			
		}
		scan.close();
		
		
		return output;
		
	}
	public static ArrayList<String> findExperience(String content) {
		//Finds the person's experience
		ArrayList<String> output = new ArrayList<String>();
		Scanner scan = new Scanner(content);
		boolean found = false; //This checks the scanner is at the Experience area
		String paragraph = "";
		while(scan.hasNextLine()) {
			String nextLine = scan.nextLine();
			if(found) {
				if(nextLine.compareTo("")==0) {
					if(paragraph.compareTo("")!=0) {
						output.add(paragraph);
					}
					continue;
				}
				else{
					if(nextLine.compareTo("Summary")==0||
						nextLine.compareTo("Education")==0)
							break;
					paragraph+=nextLine+" ";
				}
			}
			else 
				if(nextLine.compareTo("Experience")!=0)
					continue;
				else
					found=true;
			
		}
		scan.close();
		
		
		return output;
	}
	public static ArrayList<String> findEducation(String content) {
		//Finds the person's education
		ArrayList<String> output = new ArrayList<String>();
		Scanner scan = new Scanner(content);
		boolean found = false; //This checks the scanner is at the Education area
		while(scan.hasNextLine()) {
			String nextLine = scan.nextLine();
			if(found && nextLine.compareTo("")!=0) {
				if(nextLine.compareTo("Experience")==0||
						nextLine.compareTo("Summary")==0)
					break;
				output.add(nextLine);
			}
			else 
				if(nextLine.compareTo("Education")!=0)
					continue;
				else
					found=true;
			
		}
		scan.close();
		
		
		return output;
	}
	
	public static JSONObject getJson(String wordsInHandler) {
		//Converts the contents of the pdf into a json object
		JSONObject obj = new JSONObject();
		
		//Gets the person's name, location, and title
        ArrayList<String> contents;
        contents = findPerson(wordsInHandler);
        
        wordsInHandler = wordsInHandler.replaceAll("[^\\x00-\\x7F]", " ");//removes unknown characters
        
        if(contents.size()==3) {
        	obj.put("name", contents.get(0));
        	obj.put("location", contents.get(1));
        	obj.put("current_position", contents.get(2));
        }
        else
        	return null;
        
       //Checks if there are reference        
        String reference="";
        if(wordsInHandler.contains("people have recommended")) {
        	reference = wordsInHandler.substring(wordsInHandler.indexOf("people have recommended"));
        	reference = reference.substring(reference.indexOf("\""));
            wordsInHandler = wordsInHandler.substring(0,wordsInHandler.lastIndexOf(contents.get(0)));
        }
        else {
        	wordsInHandler = wordsInHandler.substring(0,wordsInHandler.lastIndexOf(contents.get(0)));
        }
        
        
        
        //Gets the person's summary
        if(wordsInHandler.contains("Summary")) {
       	 String summary = findSummary(wordsInHandler);
       	 obj.put("summary", summary);
        }
      //Gets the person's Experience
		if(wordsInHandler.contains("Experience")){
			contents = findExperience(wordsInHandler);
			obj.put("experience", contents);
			        	 
		}
		//Gets the person's Education
		if(wordsInHandler.contains("Education")) {
			contents = findEducation(wordsInHandler);
			obj.put("education", contents);
		}
		
		if(reference.contains("Profile Notes and Activity")) {
			obj.put("references", reference.substring(0,reference.lastIndexOf("Profile Notes and Activity")));
			reference = reference.substring(reference.indexOf(")")+1);
			obj.put("notes", reference);
		}
		else 
			obj.put("references", reference);
		
		
		return obj;
	}

}
