import json
import os
import sqlite3
from tqdm import tqdm

def document_generator(datalake):
	books = os.listdir(datalake)
	for book in tqdm(books):
		# Combinamos la ruta completa con el nombre del archivo
		archivo = os.path.join(datalake, book)

		# Verificamos si el archivo es un archivo JSON
		if book.endswith('.json'):
			doc_id = int(book[:-5])
			with open(archivo, 'r', encoding='utf-8') as archivo_json:
				yield doc_id, json.load(archivo_json)

def create_datamart(datalake_dir, datamart_path, index):
	conn = sqlite3.connect(datamart_path)
	c = conn.cursor()

	# Crear la tabla "words" con dos columnas: palabra y lista de libros
	c.execute("""
		CREATE TABLE IF NOT EXISTS inv_index (
		word TEXT,
		doc_id INTEGER
	)""")

	c.execute("""
		CREATE TABLE IF NOT EXISTS documents (
		id INTEGER,
		title TEXT,
		date TEXT,
		content TEXT
	)""")

	for word, docs in index.items():
		for doc in docs:
			c.execute("""
					INSERT OR REPLACE INTO inv_index (word, doc_id) VALUES (?, ?)
				""",
				(word, doc)
			)

	for doc_id, doc in document_generator(datalake_dir):
		c.execute(
			"""INSERT OR REPLACE INTO documents (id, title, date, content)
			VALUES (?, ?, ?, ?)
			""",
			(doc_id, doc.get("title"), doc.get("date"), doc.get("text"))
		)

	conn.commit()
	conn.close()

