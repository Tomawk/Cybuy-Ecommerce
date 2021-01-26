#!/usr/bin/env python
# coding: utf-8

# In[1]:


import csv
import numpy as np
from bs4 import BeautifulSoup
from selenium import webdriver #chrome driver
import json
import random
import time
from datetime import datetime


# ## Useful Variables and Functions

# In[2]:


def extract_record(item):
    atag = item.h2.a
    
    description = atag.text.strip()
    url = 'https://www.amazon.com' + atag.get('href')
    
    try:
        price_parent = item.find('span','a-price')
        price = price_parent.find('span', 'a-offscreen').text
    except AttributeError:
        return
        
    image = item.find('img')['src']
    
    product_type = "scanner"
    
    result = {'description': description, 'url': url, 'price': price, 'image': image, 'product_type': product_type}
        
    driver.get(url) #go to the article page
    
    soup1 = BeautifulSoup(driver.page_source, 'html.parser')
    
    techSpecDict = {}
    
    product_details = soup1.find(id='productDetails_detailBullets_sections1')
    product_details_1 = soup1.find(id='productDetails_techSpec_section_1') 
    
    if product_details is not None:
        for tr in product_details.find_all('tr'):
            th_text = tr.th.text.strip()
            td_text = tr.td.text.strip()
            if th_text == "Product Dimensions":
                techSpecDict.update({"Product Dimensions": td_text})
            if th_text == "Item Weight":
                techSpecDict.update({"Item Weight": td_text})
            if th_text == "ASIN":
                techSpecDict.update({"ASIN": td_text})
            if th_text == "Item model number":
                techSpecDict.update({"Item model number": td_text})
            if th_text == "Date First Available":
                techSpecDict.update({"Date First Available": td_text})
            if th_text == "Manufacturer":
                techSpecDict.update({"Manufacturer": td_text})
            if th_text == "Brand":
                techSpecDict.update({"Brand": td_text})
                
    if product_details_1 is not None:
        for tr in product_details.find_all('tr'):
            th_text = tr.th.text.strip()
            td_text = tr.td.text.strip()
            if th_text == "Product Dimensions":
                techSpecDict.update({"Product Dimensions": td_text})
            if th_text == "Item Weight":
                techSpecDict.update({"Item Weight": td_text})
            if th_text == "ASIN":
                techSpecDict.update({"ASIN": td_text})
            if th_text == "Item model number":
                techSpecDict.update({"Item model number": td_text})
            if th_text == "Date First Available":
                techSpecDict.update({"Date First Available": td_text})
            if th_text == "Manufacturer":
                techSpecDict.update({"Manufacturer": td_text})
            if th_text == "Brand":
                techSpecDict.update({"Brand": td_text})
                
            
    if bool(techSpecDict):
        result['Details'] = techSpecDict
    
    
    sleeping_time = random.uniform(1, 6)
    time.sleep(sleeping_time)
    
    return result


# In[3]:


items_in_page = 0
total_items = 0
random.seed()


# ## Main Execution

# In[4]:


#Chrome
driver = webdriver.Chrome()

url = 'https://www.amazon.com/s?i=specialty-aps&bbn=16225007011&rh=n%3A16225007011%2Cn%3A172584&ref=nav_em__nav_desktop_sa_intl_scanners_0_2_6_12'

num_final_page = 43 #put the number of pages do you want + 1


# In[5]:


records = []

for page in range(1,num_final_page):
    items_in_page = 0
    if page != 1:
        url = 'https://www.amazon.com/s?i=computers-intl-ship&bbn=16225007011&rh=n%3A16225007011%2Cn%3A172584&page=' + str(
            page) + '&qid=1607272427&ref=sr_pg_' + str(page)
    driver.get(url)
    soup = BeautifulSoup(driver.page_source, 'html.parser')
    try:
        results = soup.find_all('div', {'data-component-type': 's-search-result'})
    except AttributeError:
        with open("scanners.json", "w") as outfile:
            json.dump(records, outfile)
        
    for item in results:
        record = extract_record(item)
        if record:
            items_in_page = items_in_page + 1
            total_items = total_items + 1
            print(f'{datetime.now().time().hour}:{datetime.now().time().minute}:{datetime.now().time().second} '
                  f'- page: {page} item: {items_in_page} | total items scraped: {total_items}')
        records.append(record)

driver.close()

with open("scanners.json", "w") as outfile:
    json.dump(records, outfile)


# In[ ]:




