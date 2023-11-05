package es.ulpgc.bigdata.inverse_index;

import es.ulpgc.bigdata.inverse_index.indexer.Indexer;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IndexerTest {
		@Test
		void indexbasic() {
			String text = "This is a test!";
			Set<String> tokens = Indexer.tokenize(text);
			assertEquals(Set.of("this", "is", "a", "test"), tokens);
		}

		@Test
		void indexDash() {
			String text = "This is a test-test!";
			Set<String> tokens = Indexer.tokenize(text);
			assertEquals(Set.of("this", "is", "a", "test-test"), tokens);
		}

		@Test
		void indexUnderscorePrefixPostfix() {
			String text = "This is a test_ _test";
			Set<String> tokens = Indexer.tokenize(text);
			assertEquals(Set.of("this", "is", "a", "test"), tokens);
		}

		@Test
	void indexLotsOfWhitespace() {
		String text = "This is a test!   \n\n\nnew line!";
		Set<String> tokens = Indexer.tokenize(text);
		assertEquals(Set.of("this", "is", "a", "test", "new", "line"), tokens);
		}

		@Test
		void indexNumbers() {
			String text = "1234 7777 23";
			Set<String> tokens = Indexer.tokenize(text);
			assertEquals(Set.of("1234", "7777", "23"), tokens);
		}

		@Test
		void indexTooLongToken() {
			String text = "short _long123456789123456789_ after other________________________________long";
			Set<String> tokens = Indexer.tokenize(text);
			assertEquals(Set.of("short", "after"), tokens);
		}
}