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

def getNumberOfItems():
    collection_name = dbname["items"]
    itemlist = list()
    items = collection_name.find()
    for item in items:
        itemlist.append(item["name"])
    return itemlist

def insertHistory():
    collection_name = dbname["purchases"]

def addItem(name, x, y):
    collection_name = dbname["items"]
    temp = dict()
    temp["name"] = name
    temp["location"] = "{}, {}".format(x, y)
    temp["__v"] = 0
    print(temp)
    collection_name.insert_one(temp)

if __name__ == "__main__":
    # Get the database
    dbname = getDatabase()
    x = numpy.random.uniform(LOW_X, HIGH_X)
    x = float("{0:.4f}".format(x))
    y = numpy.random.uniform(LOW_Y, HIGH_Y)
    y = float("{0:.4f}".format(y))
    addItem(sys.argv[1], x, y)
    