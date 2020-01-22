from bs4 import BeautifulSoup
import requests
import json

htmlString = requests.get("https://en.wikipedia.org/wiki/List_of_countries_by_literacy_rate")
soup = BeautifulSoup(htmlString.text)
# print(soup.prettify())
soup = soup.find_all("tbody")[0]
needed = []
for row in soup.find_all("tr"):
    # print(row)
    columns = row.find_all("td")
    # print(len(columns))
    
    if (len(columns) == 9):
        if len(columns[1].get_text()) > 1:
            # print(len(columns[1].get_text()))
            needed.append([columns[0].get_text().strip(), columns[1].get_text().strip()])
            # print(columns[0].get_text().strip() + " " + columns[1].get_text().strip())
        # print(columns[1].get_text().split(',')[0] + " " + columns[2].get_text() + " " + columns[3].get_text())
print(json.dumps(needed))
