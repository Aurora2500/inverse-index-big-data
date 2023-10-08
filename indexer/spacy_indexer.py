##Se instala el spacy y el en_core_web_sm
import json
import os
import sys
import spacy  
from tqdm import tqdm
from collections import defaultdict

from indexer_commons import document_generator, create_datamart

def indexer(documents):
	reverse_index = defaultdict(set)
	for doc_id, document in documents:
		texto = document.get("text")
		texto_sobrante = texto

		while len(texto_sobrante) > 0:
			if len(texto_sobrante) > 1_000_000:
				candidato = texto_sobrante[:1_000_000]
				index = candidato.rfind(" ")
				texto_seccion = texto_sobrante[:index]
				texto_sobrante = texto_sobrante[index:]
			else:
				texto_seccion, texto_sobrante = texto_sobrante, ""

			nlp = spacy.load("en_core_web_sm")
			doc = nlp(texto_seccion) # Crea un objeto de spacy tipo nlp
			lexical_tokens = [t.orth_ for t in doc if not (t.is_punct or t.is_stop)]
			unique_tokens = set(lexical_tokens)

			for word in unique_tokens:
				reverse_index[word].add(doc_id)
	return reverse_index

if __name__ == "__main__":
	if len(sys.argv) < 3:
		print("Usage: python spacy_index.py <datalake> <datamart>")
		exit(1)
	datalake_dir = sys.argv[1]
	index = indexer(document_generator(datalake_dir))
	datamart_path = sys.argv[2]
	create_datamart(datalake_dir, datamart_path, index)
