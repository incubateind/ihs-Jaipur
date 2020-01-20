from bs4 import BeautifulSoup
import requests
import json
htmlString = requests.get("https://www.numbeo.com/crime/rankings.jsp")
soup = BeautifulSoup(htmlString.text)
# print(soup.prettify())
crime_rate_dict = {}
for row in soup.find_all("tr"):
    # print(row)
    columns = row.find_all("td")
    if (len(columns) == 4):
        crime_rate_dict[columns[1].get_text().split(',')[0]] = {
            "crime_index": columns[2].get_text().strip(),
            "safety_index":columns[3].get_text().strip()
        }
        # print(columns[1].get_text().split(',')[0] + " " + columns[2].get_text() + " " + columns[3].get_text())
    
print(json.dumps(crime_rate_dict))