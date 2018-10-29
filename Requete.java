package websem.websem;
import java.io.*;

import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

public class App
{
	static final String inputFileName  = "res.ttl";
    public static void main( String[] args )
    {
    	Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }

        // read the RDF/XML file
        model.read(in, null, "Turtle");




    	String queryString =
    	"SELECT *" +
    	"WHERE { ?link <http://schema.org/upvoteCount> ?score; <http://schema.org/headline> ?question; <http://schema.org/text> ?reponse}." +
    	"ORDER BY DESC(?score) LIMIT 1";




    	Query query = QueryFactory.create(queryString) ;
    	try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
    		ResultSet results = qexec.execSelect() ;
    		ResultSetFormatter.out(System.out, results, query);
    }
  }
}
