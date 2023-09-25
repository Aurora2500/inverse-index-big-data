def query_list_of_dictionaries(data_list, target_key):
    """
    Search for a target key in a list of dictionaries and return a list of associated values if found, or an empty list if not found.

    Args:
        data_list (list): The list of dictionaries to search in.
        target_key: The key to search for.

    Returns:
        list: A list of values associated with the target key if found, or an empty list if not found.
    """
    found_values = []
    for dictionary in data_list:
        if target_key in dictionary:
            found_values.append(dictionary[target_key])
    return found_values

# Przykład użycia:
my_list = [
    {"hello": 1},
    {"kitty": 2},
    {"tree": 3},
    {"hello": 4}
]
search_key = "hello"
results = query_list_of_dictionaries(my_list, search_key)

if results:
    print(f"Found {search_key}: {', '.join(map(str, results))}")
else:
    print(f"{search_key} not found in the list.")
