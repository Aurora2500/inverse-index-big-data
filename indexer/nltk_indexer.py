import os
import sys
import json
import re
import nltk

from collections import defaultdict

def indexer(document_list):
    index = defaultdict(set)
    for doc_id, document in document_list:
        text = document["text"]
        words = set(nltk.word_tokenize(text))
        for word in words:
            index[word].add(doc_id)
    return index

def document_generator(datalake):
    books = os.listdir(datalake)
    for book in (books):
        # Combinamos la ruta completa con el nombre del archivo
        archivo = os.path.join(datalake, book)

        # Verificamos si el archivo es un archivo JSON
        if book.endswith('.json'):
            doc_id = int(book[:-5])
            with open(archivo, 'r', encoding='utf-8') as archivo_json:
                yield doc_id, json.load(archivo_json)



if len(sys.argv) < 2:
    print("Use: indexer.py <datalake>")
    exit(1)
# Ścieżka do katalogu z plikami JSON
directory_path = sys.argv[1]

index = indexer(document_generator(directory_path))

# Stwórz indeks słów z plików JSON
# Otwórz plik "index.txt" do zapisu
with open("index.txt", "w", encoding="utf-8") as output_file:
    for word, books in index.items():
        books_str = ', '.join(books)
        output_file.write(f"{word} - {books_str}\n")

# Wyświetl wyniki w terminalu
for word, books in index.items():
    books_str = ', '.join(books)
    print(f"{word} - {books_str}")
