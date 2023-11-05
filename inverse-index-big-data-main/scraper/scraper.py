import sys
import os
import json
import re
import asyncio
import aiohttp

if len(sys.argv) < 2:
	print("Usage: python3 scraper.py <datalake>")
	exit(1)

datalake_dir = sys.argv[1]

os.makedirs(datalake_dir, exist_ok=True)

def transform(id, text: str) -> str:
	attributes = [
		("title", re.compile(r"Title: (.*)", flags=re.IGNORECASE)),
		("author", re.compile(r"(?:Author|Creator|Contributor): (.*)", flags=re.IGNORECASE)),
		("date", re.compile(r"Release date: (.*)", flags=re.IGNORECASE)),
		("lang", re.compile(r"Language: (.*)", flags=re.IGNORECASE))
	]
	start_re = re.compile(r"(\*\*\* START OF (THE|THIS) PROJECT GUTENBERG EBOOK.*?\*\*\*)")
	end_re = re.compile(r"(\*\*\* END OF (THE|THIS) PROJECT GUTENBERG EBOOK.*?\*\*\*)")
	data = {}
	try:
		for attr_name, attr_re in attributes:
			m = attr_re.search(text)
			if m:
				data[attr_name] = m.group(1)
			else :
				print(f"Couldn't parse {attr_name} for {id}")
		start_m = start_re.search(text)
		end_m = end_re.search(text)
		if not (start_m and end_m):
			# this happens, for example, in doc 673
			return None
		data["text"] = text[start_m.end():end_m.start()]
	except AttributeError as e:
		print(f"Error parsing text for {id}")
		print(e)
		exit(1)
	return data


async def fetch(session, id):
	async with session.get(f"https://www.gutenberg.org/cache/epub/{id}/pg{id}.txt") as response:
		return (await response.text(), response.status)

async def save_text(session, id):
		text, status = await fetch(session, id)
		if status != 200:
			print(f"{id} could not be fetched")
			return

		text = text.replace("\r\n", "\n")
		dat = transform(id, text)
		if dat is None:
			print(f"{id} is not a document")
			return

		with open(os.path.join(datalake_dir, f"{id}.json"), "w") as f:
			json.dump(dat, f)
		print(f"Saved {id}")

async def main():
	async with aiohttp.ClientSession() as session:
		#await asyncio.gather(*[save_text(session, i) for i in range(100, 500)])
		for i in range(0, 1000):
			await save_text(session, i)
		#await save_text(session, 181)

if __name__ == "__main__":
	asyncio.run(main())