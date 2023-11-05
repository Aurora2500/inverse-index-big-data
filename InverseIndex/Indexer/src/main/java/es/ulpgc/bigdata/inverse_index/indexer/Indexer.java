package es.ulpgc.bigdata.inverse_index.indexer;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Indexer {
	private static final int MAX_TOKEN_LENGTH = 20;
	private static final Set<Character> PUNCTUATION_SET = Set.of('.', ',', ';', ':', '?', '!', '¡', '¿', '(', ')', '*', '"', '[', ']', '“', '$', '’', '/', '\\');
	private static final Set<Character> TOKEN_INNER_CHAR_SET = Set.of('\'', '-', '_');

	private final FileDatalake datalake;
	private final Datamart datamart;

	public Indexer(FileDatalake datalake, Datamart datamart) {
		this.datalake = datalake;
		this.datamart = datamart;
	}

	public static Set<String> tokenize(String document) {
		Set<String> tokens = new HashSet<>();
		int currTokenStart = 0;
		int safeTokenEnd = 0;
		IndexerState state = IndexerState.OutOfToken;

		Predicate<Character> isWhitespaceOrPunctuation = c -> Character.isWhitespace(c) || PUNCTUATION_SET.contains(c);
		Predicate<Character> isTokenCharacter = c -> Character.isLetter(c) || Character.isDigit(c);

		BiConsumer<Integer, Integer> addToken = (start, end) -> tokens.add(document.substring(start, end).toLowerCase());

		for (int i = 0; i < document.length(); i++) {
			char c = document.charAt(i);
			int tokenLength = i - currTokenStart;
			switch (state) {
				case OutOfToken:
					if (isTokenCharacter.test(c)) {
						state = IndexerState.InToken;
					} else if (isWhitespaceOrPunctuation.test(c) || TOKEN_INNER_CHAR_SET.contains(c)) {
						currTokenStart = i + 1;
					}
					break;
				case InToken:
					// the last character was valid for a token, so the current index would be a safe end.
					safeTokenEnd = i;
					if (tokenLength > MAX_TOKEN_LENGTH) {
						state = IndexerState.InInvalidToken;
					}
					if (isWhitespaceOrPunctuation.test(c)) {
						state = IndexerState.OutOfToken;
						addToken.accept(currTokenStart, safeTokenEnd);
						currTokenStart = i+1;
					}
					if (TOKEN_INNER_CHAR_SET.contains(c)) {
						state = IndexerState.InValidTokenUnknownEnd;
					}
					break;
				case InValidTokenUnknownEnd:
					if (isWhitespaceOrPunctuation.test(c)) {
						state = IndexerState.OutOfToken;
						addToken.accept(currTokenStart, safeTokenEnd);
						currTokenStart = i+1;
					} else if (isTokenCharacter.test(c)) {
						state = IndexerState.InToken;
					} else if (tokenLength > MAX_TOKEN_LENGTH) {
						state = IndexerState.InInvalidToken;
					}
					break;
				case InInvalidToken:
					if (isWhitespaceOrPunctuation.test(c)) {
						state = IndexerState.OutOfToken;
						currTokenStart = i+1;
					}
					break;
			}
		}
		//add last token
		if (state == IndexerState.InToken || state == IndexerState.InValidTokenUnknownEnd) {
			// if the last character was valid for the token, then the end of the token is the end of the document
			addToken.accept(currTokenStart, state == IndexerState.InToken ? document.length() : safeTokenEnd);
		}

		return tokens;
	}

	private Set<String> tokenizeDocument(int document) throws IOException {
		return tokenize(datalake.readDocument(document));
	}

	public void index() throws Exception {

		// get already indexed documents
		Set<Integer> indexedDocuments = datamart.documents();
		Set<Integer> datalakeDocuments = datalake.documents();
		datalakeDocuments.removeAll(indexedDocuments);
		System.out.println("Indexing " + datalakeDocuments.size() + " documents");
		for (Integer document: datalakeDocuments) {
			System.out.println("Indexing document " + document);
			Set<String> tokens = tokenizeDocument(document);
			System.out.println("Document " + document + " has " + tokens.size() + " tokens");
			datamart.add(document, tokens);
			System.out.println("Document " + document + " indexed");
		}

		datalake.blockAndListen((document, content) -> {
			try {
				System.out.println("Indexing document " + document);
				Set<String> tokens = tokenize(content);
				datamart.add(document, tokens);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
