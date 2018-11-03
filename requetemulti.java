package websem.websem;
import java.io.*;

import org.apache.jena.query.* ;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

public class App
{
	static final String fileRes  = "res.ttl";
	static final String fileData  = "data.ttl";
    public static void main( String[] args )
    {
    	Model res = ModelFactory.createDefaultModel();
    	Model data = ModelFactory.createDefaultModel();
    	Model model = ModelFactory.createDefaultModel();

        InputStream inData = FileManager.get().open( fileData );
        if (inData == null) {
            throw new IllegalArgumentException( "File: " + fileData + " not found");
        }
        InputStream inRes = FileManager.get().open( fileRes );
        if (inRes == null) {
            throw new IllegalArgumentException( "File: " + fileRes + " not found");
        }

        // read the RDF/XML file
        model.read(inData, null, "Turtle");
        model.read(inRes, null, "Turtle");

        model.add(res);
        model.add(data);


    	String queryString = "PREFIX schema: <http://schema.org/>\n" +
    			"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
    			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
    			"SELECT ?headline ?blague ?joketimestamp ?nomvolcan ?volcanotimestamp ?delta\n" +
    			"WHERE{\n" +
    			"  ?x schema:headline ?headline;\n" +
    			"     schema:text ?blague;\n" +
    			"     schema:dateCreated ?time;\n" +
    			"     schema:text ?answer.\n" +
    			"\n" +
    			"  ?y dbo:year ?annee;\n" +
    			"     dbo:month ?mois;\n" +
    			"     dbo:day ?jour;\n" +
    			"     dbo:volcano ?nomvolcan.\n" +
    			"\n" +
    			"  FILTER(regex(?headline, \"volcano\"))\n" +
    			"  BIND(xsd:integer (strbefore(?time, \".\")) As ?joketimestamp)\n" +
    			"  BIND(xsd:integer (?annee) As ?Annee)\n" +
    			"  BIND(xsd:integer (?mois) As ?Mois)\n" +
    			"  BIND(xsd:integer (?jour) As ?Jour)\n" +
    			"  BIND( (?Annee -1970) * 31557600 + (?Mois - 1) * 2629743 + (?Jour - 1) * 86400 AS ?volcanotimestamp)\n" +
    			"  BIND( ?joketimestamp - ?volcanotimestamp AS ?delta)\n" +
    			"  FILTER(?joketimestamp > ?volcanotimestamp - 86400 && ?joketimestamp < ?volcanotimestamp + 604800)\n" +
    			"}" ;
    	Query query = QueryFactory.create(queryString) ;
    	try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
    		ResultSet results = qexec.execSelect() ;
    		ResultSetFormatter.out(System.out, results, query);
    }
  }
}
