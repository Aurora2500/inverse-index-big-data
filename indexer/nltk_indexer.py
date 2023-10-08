import os
import sys
import json
import re
import nltk

from collections import defaultdict

def indexer(document_list):
    """
    Creates an inverted word index based on a list of documents.

    Args:
        document_list (generator): A generator of tuples (doc_id, document), where
            doc_id is the document identifier, and document is a dictionary containing
            a 'text' key with the document's text.

    Returns:
        defaultdict: The inverted word index where keys are words, and values are sets
        of document identifiers containing that word.
    """
    index = defaultdict(set)
    for doc_id, document in document_list:
        text = document["text"]
        # Tokenize the text into words using NLTK
        words = set(nltk.word_tokenize(text))
        for word in words:
            index[word].add(doc_id)
    return index

def document_generator(datalake):
    """
    Generates documents from a datalake directory containing JSON files.

    Args:
        datalake (str): The path to the directory containing JSON files.

    Yields:
        tuple: A tuple containing a document identifier and a document dictionary.
    """
    books = os.listdir(datalake)
    for book in books:
        # Combine the full path with the filename
        archivo = os.path.join(datalake, book)

        # Check if the file is a JSON file
        if book.endswith('.json'):
            doc_id = int(book[:-5])
            with open(archivo, 'r', encoding='utf-8') as archivo_json:
                yield doc_id, json.load(archivo_json)

if len(sys.argv) < 2:
    print("Usage: indexer.py <datalake>")
    exit(1)

# The path to the datalake directory provided as a command line argument
directory_path = sys.argv[1]

# Create an index based on documents from the datalake
index = indexer(document_generator(directory_path))

# Save the index to the "index.txt" file
with open("index.txt", "w", encoding="utf-8") as output_file:
    for word, books in index.items():
        # Convert document identifiers to strings and join them with commas
        books_str = ', '.join(str(book) for book in books)
        output_file.write(f"{word} - {books_str}\n")

# Display the results in the terminal
for word, books in index.items():
    books_str = ', '.join(map(str, books))
    print(f"{word} - {books_str}")
