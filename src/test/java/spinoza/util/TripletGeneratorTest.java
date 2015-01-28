package spinoza.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spinoza.util.TripletGenerator.extractDBpediaTypes;
import static spinoza.util.TripletGenerator.queryDBpediaTypes;
import static spinoza.util.TripletGenerator.queryOntology;
import static spinoza.util.TripletGenerator.abbr;
import static spinoza.util.TripletGenerator.sanitize;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSenseSource;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetSource;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import edu.mit.jwi.item.POS;

public class TripletGeneratorTest {

	@Test
	public void testSparql() {
		String[] expectedTypes = new String[] {
				"http://dbpedia.org/ontology/City",
				"http://dbpedia.org/ontology/Place",
				"http://dbpedia.org/ontology/PopulatedPlace",
				"http://dbpedia.org/ontology/Settlement",
				"http://dbpedia.org/ontology/Wikidata:Q532", };
		List<String> types = queryDBpediaTypes(
				"http://dbpedia.org/resource/The_Hague", "http://dbpedia.org/sparql");
		assertEquals(Arrays.asList(expectedTypes), types);
	}
	
	@Test
	public void testExtractDBpediaTypes() {
		String expectedOutput = "bn:00000002n	rdf:type	dbpedia-owl:City\n"
				+ "bn:00000002n	rdf:type	dbpedia-owl:Place\n"
				+ "bn:00000002n	rdf:type	dbpedia-owl:PopulatedPlace\n"
				+ "bn:00000002n	rdf:type	dbpedia-owl:Settlement\n"
				+ "bn:00000002n	rdf:type	dbpedia-owl:Wikidata:Q532";
		BabelSense theHagueSense = new BabelSense(Language.EN, "The_Hague", 
				POS.NOUN, BabelSenseSource.WIKI, null, null, 0, null, null);
		@SuppressWarnings("unchecked")
		final BabelSynset theHague = new BabelSynset("bn:00000002n", POS.NOUN,
				BabelSynsetSource.WIKI, Collections.EMPTY_LIST,  
				Collections.singletonList(theHagueSense), 
				null, null, null, null, null);
		final Iterator<BabelSynset> it = Collections.singletonList(theHague).iterator();
		String output = captureOutput(new Runnable() {
			public void run() {
				extractDBpediaTypes(it, "http://dbpedia.org/sparql", 0);				
			}
		});
		assertEquals(expectedOutput, output.trim());
	}
	
	@Test
	public void testQueryOntology() {
		List<String[]> pairs = queryOntology("http://dbpedia.org/sparql", 5, 0);
		assertEquals(5, pairs.size());
		for (String[] pair : pairs) {
			assertTrue(pair[0].startsWith("http://dbpedia.org/ontology"));
			assertTrue(pair[1].startsWith("http://dbpedia.org/ontology"));
			System.out.printf("%s\t%s\n", abbr(pair[0]), abbr(pair[1]));
		}
	}
	
	@Test
	public void testSanitize() {
		assertEquals("http://dbpedia.org/resource/Irwin_%22Ike%22_H._Hoover", 
				sanitize("http://dbpedia.org/resource/Irwin_\"Ike\"_H._Hoover"));
		assertEquals("http://dbpedia.org/resource/Jang_%60Ali",
				sanitize("http://dbpedia.org/resource/Jang_`Ali"));
	}
	
	private static String captureOutput(Runnable runnable) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		System.setOut(new PrintStream(out));
		runnable.run();
		System.setOut(stdout);
		String output = new String(out.toByteArray(), Charset.forName("UTF-8"));
		return output;
	}
	
}
