import sys
import nltk
from collections import defaultdict
from indexer_commons import document_generator, create_datamart

def indexer(document_list):
	index = defaultdict(set)
	for doc_id, document in document_list:
		text = document["text"]
		words = set(nltk.word_tokenize(text))
		for word in words:
			index[word].add(doc_id)
	return index

if __name__ == "__main__":
	if len(sys.argv) < 3:
		print("Usage: python nltk_indexer.py <datalake> <datamart>")
		exit(1)
	# Ścieżka do katalogu z plikami JSON
	datalake_path = sys.argv[1]

	index = indexer(document_generator(datalake_path))

	datamart_path = sys.argv[2]
	create_datamart(datalake_path, datamart_path, index)
