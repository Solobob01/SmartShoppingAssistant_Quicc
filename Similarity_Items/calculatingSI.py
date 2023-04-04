from venv import create
import math
from pymongo import DeleteMany, MongoClient
import numpy as np
import time

sameItem = -1000
namItem = -100

def getDatabase():
    CONNECTION_STRING = "mongodb+srv://quicc:Ni75sbXJNI0sZWQw@cluster0.0cpqzhx.mongodb.net/quicc-db?retryWrites=true&w=majority"
    client = MongoClient(CONNECTION_STRING)
    return client['quicc-db']

def getNumberOfItems():
    collection_name = dbname["items"]
    itemlist = list()
    items = collection_name.find()
    for item in items:
        itemlist.append(item)
    return itemlist, len(itemlist)

def getNumberOfHistory():
    collection_name = dbname["purchases"]
    itemlist = list()
    items = collection_name.find()
    for item in items:
        itemlist.append(item["itemsPurchased"])
    return itemlist, len(itemlist)

def createMatrix(n, m):
    basicMatrix = np.zeros((m, n))
    return basicMatrix

def createBasicMatrix(n, m, listItems, purchaseHistory):
    basicMatrix = createMatrix(n, m)
    i = 0
    for history in purchaseHistory:
        for item in history:
            basicMatrix[i][getIndexItem(listItems, item)] = 1
        i += 1
    return basicMatrix


def getIndexItem(items, name):
    index = 0
    for item in items:
        if name == item["name"]:
            return index
        index += 1
    return -1



def printHistoryByIndex(h, i):
    for history in h:
        for items in history:
            print(getIndexItem(i, items))
        print("finish")

def findB(matrix, item1, item2):
    rating1 = 0
    rating2 = 0
    n, m = matrix.shape
    for i in range(0, n):
        rating1 += matrix[i][item1] * matrix[i][item1]
    for i in range(0, n):
        rating2 += matrix[i][item2] * matrix[i][item2]
    
    return math.sqrt(rating1) * math.sqrt(rating2)

def findA(matrix, item1, item2):
    output = 0
    n, m = matrix.shape 
    for i in range(0, n):
        output += matrix[i][item1] * matrix[i][item2]
    return output

def findSimilarity(matrix, item1, item2):
    a = findA(matrix, item1, item2)
    b = findB(matrix, item1, item2)
    if b == 0:
        return namItem   
    return a/b

def calculateSimMatrix(matrix):
    output = np.zeros(matrix.shape)
    n, m = matrix.shape
    for i in range(m):
        for j in range(m):
            if i == j:
                output[i][j] = sameItem
            else:
                output[i][j] = findSimilarity(matrix, i, j)
    return output

def createRecommandation(items, simMatrix):
    allRecommandation = list()
    allRecommandationSorted = list()
    for item in items:
        allRecommandation.append((item["name"], simMatrix[:, getIndexItem(items, item["name"])]))
        allRecommandationSorted.append((item["name"], np.sort(simMatrix[:, getIndexItem(items, item["name"])])[::-1]))
    return allRecommandation, allRecommandationSorted

def sortRecommandation(recList):
    return sorted(
    recList,
    key=lambda t: t[1],
    reverse=True
)
        

def createBestItems(items, rec):
    temp = list()
    for iter in rec:
        itemName, gradeList = iter
        temp1 = list()
        for i in range(0, len(items)):
            temp1.append((items[i]["name"], gradeList[i]))
        temp.append((itemName, sortRecommandation(temp1)))
    return temp

def writeNewRecCollection(bestItems):
    collection_name = dbname["recommendations"]
    collection_name.drop()
    myList = list()
    for item in bestItems:
        temp = dict()
        itemName, gradeList = item
        temp["item"] = itemName
        temp2 = list()
        for iter in gradeList:
            itemGradeName, grade = iter
            temp2.append(itemGradeName)
        temp["recommendation_list"] = temp2
        myList.append(temp)
    collection_name.insert_many(myList)

# This is added so that many files can reuse the function get_database()
if __name__ == "__main__":
    # Get the database
    dbname = getDatabase()
    while True:
        items, numberOfItems = getNumberOfItems()
        purchaseHistory, numberOfHistory = getNumberOfHistory()
        basicMatrix = createBasicMatrix(numberOfItems, numberOfHistory, items, purchaseHistory)
        simMatrix = calculateSimMatrix(basicMatrix)
        allRecommandation, allRecommandationSorted = createRecommandation(items, simMatrix)
        bestItemsRec = createBestItems(items, allRecommandation)
        writeNewRecCollection(bestItemsRec)
        print("Recommandation list updated!")
        time.sleep(60)
    

    
