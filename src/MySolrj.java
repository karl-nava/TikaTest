import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
public class MySolrj {
	public static void main(String[] args) throws IOException, SolrServerException {
	    SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/test2").build();
	    //Add files
	    //addFiles(client);
	    
	    //Remove files
	    deleteFiles(client);
	    
	  }
	public static void addFiles(SolrClient client) throws SolrServerException, IOException {
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
	public static void deleteFiles(SolrClient client) throws SolrServerException, IOException {
		//Remove files
	    //String SOLR_URL = "http://localhost:8983/solr/test2";
        //SolrClient Solr = new HttpSolrClient.Builder(SOLR_URL).build();

        //Preparing the Solr document
        //SolrInputDocument doc = new SolrInputDocument();

        //Deleting the documents from Solr
        //Solr.deleteByQuery("*");
        //Solr.commit();
        client.deleteByQuery("*");
        client.commit();
	}
}
