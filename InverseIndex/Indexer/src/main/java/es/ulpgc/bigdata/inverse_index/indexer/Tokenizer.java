package es.ulpgc.bigdata.inverse_index.indexer;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Tokenizer {

	private static final int MAX_TOKEN_LENGTH = 20;
	private static final Set<Character> TOKEN_INNER_CHAR_SET = Set.of('\'', '-', '_');

	public static Set<String> tokenize(String document) {
		Set<String> tokens = new HashSet<>();
		int currTokenStart = 0;
		int safeTokenEnd = 0;
		IndexerState state = IndexerState.OutOfToken;

		Predicate<Character> isTokenCharacter = c -> Character.isLetter(c) || Character.isDigit(c);

		BiConsumer<Integer, Integer> addToken = (start, end) -> tokens.add(document.substring(start, end).toLowerCase());

		for (int i = 0; i < document.length(); i++) {
			char c = document.charAt(i);
			int tokenLength = i - currTokenStart;
			switch (state) {
				case OutOfToken:
					if (isTokenCharacter.test(c)) {
						currTokenStart = i;
						state = IndexerState.InToken;
					}
					break;
				case InToken:
					// the last character was valid for a token, so the current index would be a safe end.
					safeTokenEnd = i;
					if (tokenLength > MAX_TOKEN_LENGTH) {
						state = IndexerState.InInvalidToken;
					}
					if (TOKEN_INNER_CHAR_SET.contains(c)) {
						state = IndexerState.InValidTokenUnknownEnd;
					} else if (!isTokenCharacter.test(c)) {
						state = IndexerState.OutOfToken;
						addToken.accept(currTokenStart, safeTokenEnd);
						currTokenStart = i+1;
					}
					break;
				case InValidTokenUnknownEnd:
					if (tokenLength > MAX_TOKEN_LENGTH) {
						state = IndexerState.InInvalidToken;
					} else if (isTokenCharacter.test(c)) {
						state = IndexerState.InToken;
					} else if (TOKEN_INNER_CHAR_SET.contains(c)) {
						continue;
					} else {
						state = IndexerState.OutOfToken;
						addToken.accept(currTokenStart, safeTokenEnd);
						currTokenStart = i+1;
					}
					break;
				case InInvalidToken:
					if (! (isTokenCharacter.test(c) || TOKEN_INNER_CHAR_SET.contains(c))) {
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
}
