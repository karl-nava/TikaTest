import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import java.io.StringWriter;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import org.xml.sax.SAXException;
public class MyTika {
	private final Tika tika = new Tika();
    
    public MyTika() {
        super();
    }
    
    public String probeContentType(Path path) throws IOException {
        // Try to detect based on the file name only for efficiency
        String fileNameDetect = tika.detect(path.toString());
        if(!fileNameDetect.equals(MimeTypes.OCTET_STREAM)) {
            return fileNameDetect;
        }
        
        // Then check the file content if necessary
        String fileContentDetect = tika.detect(path);
        if(!fileContentDetect.equals(MimeTypes.OCTET_STREAM)) {
            return fileContentDetect;
        }
        
        // Specification says to return null if we could not 
        // conclusively determine the file type
        return null;
    }
    public static void main(String args[]) throws IOException, SAXException, TikaException, SolrServerException {
    	 //ToXMLContentHandler handler = new ToXMLContentHandler();
    	//SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/tset").build();
    	 
    	BodyContentHandler  handler = new BodyContentHandler ();
    	 Metadata metadata = new Metadata();
         File folder = new File("C:\\Users\\Karl\\Desktop\\PDFResume");
         String folderName= "C:\\Users\\Karl\\Desktop\\PDFResume";
         File[] files = folder.listFiles();
         
         
         FileWriter fileWriter = new FileWriter("Z:\\UTD\\EclipseJava\\TikaTest\\info.json");
         for(File x : files) {
        	 SolrInputDocument doc = new SolrInputDocument();
	         FileInputStream inputstream = new FileInputStream(new File(folderName +"\\"+x.getName()));
	         ParseContext pcontext = new ParseContext();
	         
	         //parsing the document using PDF parser
	         PDFParser pdfparser = new PDFParser(); 
	         pdfparser.parse(inputstream, handler, metadata,pcontext);
	         
	         //getting the content of the document
	         List<String> contents= new ArrayList<>(Arrays.asList(handler.toString().toLowerCase().split(" ")));
	         List<String> stopwords = Arrays.asList("the","to","a","?","in","of","at","for","and","an");
	         contents.removeAll(stopwords);
	         System.out.println("Contents of the PDF :" + String.join(" ", contents));
	         
	         
	         //getting metadata of the document
	         //System.out.println("Metadata of the PDF:");
	         String[] metadataNames = metadata.names();
	         
	         JSONObject obj = new JSONObject();
	         
	         for(String name : metadataNames) {
	        	 obj.put(name,metadata.get(name));
	        	 doc.addField(name, metadata.get(name));
	            //System.out.println(name+ " : " + metadata.get(name));
	         }
	         /*StringWriter out = new StringWriter ();
	         obj.writeJSONString(out);
	         
	         String jsonText = out.toString();
	         System.out.print(jsonText);*/
	         try {
	        	 /*fileWriter.write(obj.toJSONString());
	        	 fileWriter.write("\n");*/
	        	 //client.add(doc);
	        	 
	         }
	         catch (Exception e) {
	         
	         }
	         System.out.println(obj);
	         //client.commit();
	         }
         fileWriter.flush();
    }
    
    public static void inputToSolr() throws SolrServerException, IOException {
    	SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/tset").build();
    	for(int i=0;i<1000;++i) {
		      SolrInputDocument doc = new SolrInputDocument();
		      doc.addField("cat", "book");
		      doc.addField("id", "book-" + i);
		      doc.addField("name", "The Legend of the Hobbit part " + i);
		      client.add(doc);
		      if(i%100==0) client.commit();  // periodically flush
		    }
		    client.commit();
    }
    public static void deleteFiles( ) throws SolrServerException, IOException {
    	SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/tset").build();
  
        client.deleteByQuery("*");
        client.commit();
	}
    
}
