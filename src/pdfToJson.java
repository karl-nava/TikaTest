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

public class pdfToJson {
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
	         FileInputStream inputstream = new FileInputStream("C:\\Users\\Karl\\Desktop\\SingleResume.pdf");
	         ParseContext pcontext = new ParseContext();
	         
	         //parsing the document using PDF parser
	         PDFParser pdfparser = new PDFParser(); 
	         pdfparser.parse(inputstream, handler, metadata,pcontext);
	         
	         //getting the content of the document
	         String wordsInHandler = handler.toString();
	         //System.out.println(wordsInHandler);
	         //wordsInHandler = wordsInHandler.replace("-", "");
	         List<String> contents= new ArrayList<>(Arrays.asList(wordsInHandler.split("\n")));
	         Scanner scan = new Scanner(new File("C:\\Users\\Karl\\Desktop\\stopwords.txt"));
	         ArrayList<String> stopwords = new ArrayList<String>();
	         while(scan.hasNextLine()) {
	        	 stopwords.add(scan.nextLine());
	         }
	         contents.removeAll(stopwords);
	         contents.remove("/n");
	         //System.out.print(contents);x
	         
	         //getting metadata of the document
	         //System.out.println("Metadata of the PDF:");
	         String[] metadataNames = metadata.names();
	         
	         System.out.println("Contents of the PDF :" + String.join("\n", contents));
	         
	         

	         String name = contents.get(1);
	         String address = contents.get(2);
	         JSONObject obj = new JSONObject();
	         obj.put("name",name);
	         obj.put("Address",address);
	         wordsInHandler = wordsInHandler.replaceAll(name," ");
	         wordsInHandler = wordsInHandler.replaceAll(address," ");
	         wordsInHandler = wordsInHandler.replaceAll("\n"," ");
	         wordsInHandler = wordsInHandler.replaceAll("\\p{Punct}"," ");
	         contents= new ArrayList<>(Arrays.asList(wordsInHandler.split(" ")));

	         System.out.println("Contents of the PDF :" + String.join(" ", contents));
	         
	         for(String x: contents) {
	        	 x = x.toLowerCase();
	        	 if(x.compareTo("")==0||x.compareTo("name")==0||x.compareTo("address")==0)
	        		 continue;
	        	 if(obj.containsKey(x)) {
	        		 obj.put(x,(int)(obj.get(x))+1);
	        	 }
	        	 else
	        		 obj.put(x,1);
	         }
	         
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
	         System.out.println(obj);
	         //client.commit();
	         fileWriter.flush();
	}
}
