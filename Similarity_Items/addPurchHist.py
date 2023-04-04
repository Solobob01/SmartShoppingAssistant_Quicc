from operator import index
from random import random
from venv import create
import sys
import math
from pymongo import DeleteMany, MongoClient
import numpy

LOW_X = 33.132
HIGH_X = 35.32

LOW_Y = 34.5874
HIGH_Y = 36.4023

def getDatabase():
    CONNECTION_STRING = "mongodb+srv://quicc:Ni75sbXJNI0sZWQw@cluster0.0cpqzhx.mongodb.net/quicc-db?retryWrites=true&w=majority"
    client = MongoClient(CONNECTION_STRING)
    return client['quicc-db']

def getItems():
    collection_name = dbname["items"]
    itemlist = list()
    items = collection_name.find()
    for item in items:
        itemlist.append(item["name"])
    return itemlist
def createPurchList(items):
    numberOfItems = numpy.random.randint(2, len(items))
    purchList = set()
    while len(purchList) != numberOfItems:
        indexItem = numpy.random.randint(0, len(items))
        purchList.add(items[indexItem])
    return purchList

def writePurchList(purchList):
    collection_name = dbname["purchases"]
    temp = list()
    for iter in purchList:
        temp.append(iter)
    temp1 = dict()
    temp1["itemsPurchased"] = temp
    temp1["__v"] = 0
    print(temp1)
    collection_name.insert_one(temp1)

if __name__ == "__main__":
    # Get the database
    dbname = getDatabase()
    items = getItems()
    i = 0
    numberofLists = 20
    while i != numberofLists:
        purchList = createPurchList(items)
        writePurchList(purchList)
        i += 1
    