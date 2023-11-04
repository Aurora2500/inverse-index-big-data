package es.ulpgc.bigdata.inverse_index.indexer;

public enum IndexerState {
	// it is a regular token, but it is not finished yet, add it to the current token when it is finished
	InToken,
	// the current token is valid, but it has some trailing characters that aren't valid if they're the last ones
	InValidTokenUnknownEnd,
	// Even though we are in a token, it is not valid and thus we should ignore it and go to the next one
	InInvalidToken,
	OutOfToken,

}
