##Se instala el spacy y el en_core_web_sm
import json
import os
import sys
import spacy  
import sqlite3
from tqdm import tqdm

from collections import defaultdict

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
            lexical_tokens = [t.orth_ for t in doc if not t.is_punct | t.is_stop]

            unique_tokens = list(set(lexical_tokens))

                
            for word in unique_tokens:
                reverse_index[word].add(doc_id)
    return reverse_index
        
def document_generator(ruta):
    libros = os.listdir(ruta)
    for libro in tqdm(libros):
        # Combinamos la ruta completa con el nombre del archivo
        archivo = os.path.join(ruta, libro)

        # Verificamos si el archivo es un archivo JSON
        if libro.endswith('.json'):
            doc_id = int(libro[:-5])
            with open(archivo, 'r', encoding='utf-8') as archivo_json:
                yield doc_id, json.load(archivo_json)


if __name__ == "__main__":
    ruta = sys.argv[1]
    index = indexer(document_generator(ruta))

    conn = sqlite3.connect('words1.db')
    c = conn.cursor()

    # Crear la tabla "words" con dos columnas: palabra y lista de libros
    c.execute("""CREATE TABLE IF NOT EXISTS words1 (
                Word TEXT,
                Books INTEGER
    )""")

    c.execute("""CREATE TABLE IF NOT EXISTS document (
                id INTEGER,
                title TEXT,
                date TEXT,
                content TEXT
    )""")

    for word, docs in index.items():
        for doc in docs:
            c.execute("""
                INSERT OR REPLACE INTO words1 (Word, Books) VALUES (?, ?)
                """,
                (word, doc)
            )

    for doc_id, doc in document_generator(ruta):
        c.execute(
            """INSERT OR REPLACE INTO document (id, title, date, content)
            VALUES (?, ?, ?, ?)
            """,
            (doc_id, doc.get("title"), doc.get("date"), doc.get("text"))
        )

    conn.commit()
    conn.close()