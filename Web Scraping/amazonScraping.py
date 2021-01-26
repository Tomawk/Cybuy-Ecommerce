import json
import random
import time
from datetime import datetime
from bs4 import BeautifulSoup

from selenium import webdriver


def extract_record(product):
    """Extract and return data from a single record"""

    # description and url
    atag = product.h2.a
    description = atag.text.strip()
    prod_url = 'https://www.amazon.com' + atag.get('href')

    try:
        # price
        price_parent = product.find('span', 'a-price')
        price = price_parent.find('span', 'a-offscreen').text
    except AttributeError:
        return

    # image
    image = product.find('img')['src']

    result = {'description': description, 'url': prod_url, 'price': price, 'image': image}

    return result


def extract_product_details(details_of_product, details_dictionary):
    """Extract and return data from the product page. Change fields as needed
    """
    for tr in details_of_product.find_all('tr'):
        th = tr.th.text.strip()
        if th == 'Manufacturer':
            details_dictionary.update({"Manufacturer": tr.td.text.strip()})
        elif th == 'ASIN':
            details_dictionary.update({"ASIN": tr.td.text.strip()})
        elif th == 'Item Weight':
            details_dictionary.update({"Weight": tr.td.text.strip()})
        elif th == 'Product Dimensions':
            details_dictionary.update({"Product_Dimensions": tr.td.text.strip()})
        elif th == 'Item model number':
            details_dictionary.update({"Model_Number": tr.td.text.strip()})
        elif th == 'Brand':
            details_dictionary.update({"Brand": tr.td.text.strip()})
        elif th == 'Batteries':
            details_dictionary.update({"Batteries": tr.td.text.strip()})
        elif th == 'Package Dimensions':
            details_dictionary.update({"Package_Dimensions": tr.td.text.strip()})

    return details_dictionary


caps = webdriver.DesiredCapabilities.CHROME.copy()
caps['binary'] = "C:\Program Files (x86)\Google\Chrome\Application"  # folder containing Chrome
driver = webdriver.Chrome(desired_capabilities=caps)

num_pages = 150  # number of pages to be scraped
items_in_page = 0  # number of items scraped in the current page
total_items = 0  # number of items scraped in total

records = []  # list that will contain the results of the scraping
url = 'https://www.amazon.com/s?i=electronics-intl-ship&bbn=16225009011&rh=n%3A667846011%2Cn%3A172563&dc&qid' \
      '=1609152996&rnid=667846011&ref=sr_nr_n_5'  # starting page

random.seed()  # initialization of the random number generator

for page in range(1, num_pages+1):
    items_in_page = 0
    if page != 1:
        url = 'https://www.amazon.com/s?i=electronics-intl-ship&bbn=16225009011&rh=n%3A667846011%2Cn%3A172563&dc&page' \
              '={0}&qid=1609153003&rnid=667846011&ref=sr_pg_{1}'.format(str(page), str(page))
    driver.get(url)
    soup = BeautifulSoup(driver.page_source, 'html.parser')
    results = soup.find_all('div', {'data-component-type': 's-search-result'})

    for item in results:
        record = extract_record(item)
        if record:
            items_in_page = items_in_page + 1
            total_items = total_items + 1
            print(f'{datetime.now().time()} - page: {page} item: {items_in_page} | total items scraped: {total_items}')
            product_url = record.get('url')
            driver.get(product_url)
            soup = BeautifulSoup(driver.page_source, 'html.parser')

            techSpecDict = {}  # a dictionary that will contain the product technical details

            product_details = soup.find(id='productDetails_detailBullets_sections1')
            if product_details is not None:
                extract_product_details(product_details, techSpecDict)

            if bool(techSpecDict):
                record['Details'] = techSpecDict

            record['product_type'] = 'speaker'
            records.append(record)

            sleeping_time = random.uniform(1, 5)
            time.sleep(sleeping_time)

driver.close()

with open("speakers.json", "w") as outfile:
    json.dump(records, outfile)
