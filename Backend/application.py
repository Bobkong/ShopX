from flask import Flask
from flask import request
from helpers.merchant import *
from helpers.customer import *
from helpers.phoneVerify import *
from helpers.loyalty import *
from helpers.item import *
from helpers.order import *
from square.client import Client
import uuid
import os


app = Flask(__name__)

merchant = Merchants()
customer = Customers()
phoneVerify = PhoneVerify()
items = Items()
loyalty = Loyalty()
orders = Orders()

@app.route('/')
def index():
    return '<h1>Hello ShopX!</h1>'

@app.route('/saveMerchant', methods=['POST'])
def saveMerchant():
    if request.method == 'POST':
        merchantId = request.json['merchantId'].replace("'", "''")
        accessToken = request.json['accessToken'].replace("'", "''")
        logoUrl = request.json['logo'].replace("'", "''")
        addressLine1 = request.json['address']['address_line_1'].replace("'", "''")
        locality = request.json['address']['locality'].replace("'", "''")
        administrativeDistrictLevel1 = request.json['address']['administrative_district_level_1'].replace("'", "''")
        postalCode = request.json['address']['postal_code'].replace("'", "''")
        lat = request.json['lat']
        lng = request.json['lng']
        ifLoyalty = request.json['ifLoyalty']
        locationId = request.json['locationId']
        if 'discountProducts' in request.json:
            discountProducts = request.json['discountProducts'].replace("'", "''")
        else:
            discountProducts = ''

        if 'discount' in request.json:
            discountType = request.json['discount']['discountType'].replace("'", "''")
            discountAmount = request.json['discount']['amount']
            discountName = request.json['discount']['discountName'].replace("'", "''")
        else:
            discountType = ''
            discountAmount = 0.0
            discountName = ''

        businessName = request.json['businessName'].replace("'", "''")

        if ifLoyalty == True:
            ifLoyalty = 1
        else:
            ifLoyalty = 0

        fetchItemInfo(accessToken)

        if merchant.checkMerchant(merchantId):
            print('add new merchant: ', merchantId)
            merchant.addMerchant(merchantId, accessToken, logoUrl, addressLine1, locality, administrativeDistrictLevel1, postalCode, lat, lng, ifLoyalty, discountProducts, discountType, discountAmount, businessName, locationId, discountName)
        else:
            print('update merchant: ', merchantId)
            merchant.updateMerchant(merchantId, accessToken, logoUrl, addressLine1, locality, administrativeDistrictLevel1, postalCode, lat, lng, ifLoyalty, discountProducts, discountType, discountAmount, businessName, locationId, discountName)
        return {"code": 0, "msg": "Success!"}


def fetchItemInfo(accessToken):
    client = Client(
        access_token=accessToken,
        environment='sandbox')

    result = client.catalog.list_catalog(types = "ITEM")

    if result.is_success():
        if 'objects' in result.body:
            for item in result.body['objects']:
                itemName = item['item_data']['name'].replace("'", "''")
                itemDescription = ""
                if 'description' in item['item_data']:
                    itemDescription = item['item_data']['description'].replace("'", "''")

                itemId = item['id'].replace("'", "''")
                itemVariationId = item['item_data']['variations'][0]['id']
                itemPriceType =  item['item_data']['variations'][0]['item_variation_data']['pricing_type'].replace("'", "''")

                itemPrice = 0
                if 'price_money' in item['item_data']['variations'][0]['item_variation_data']:
                    itemPrice = item['item_data']['variations'][0]['item_variation_data']['price_money']['amount']

                # if image exists for this item
                if 'image_ids' in item['item_data']:
                    itemImageId = item['item_data']['image_ids'][0].replace("'", "''")
                    result = client.catalog.retrieve_catalog_object(
                        object_id = itemImageId,
                        include_related_objects = False
                    )

                    if result.is_success():
                        itemImage = result.body['object']['image_data']['url'].replace("'", "''")
                        
                        if items.checkItem(itemId):
                            items.addItem(accessToken, itemName, itemDescription, itemId, itemPrice, itemImage, "", itemPriceType, itemVariationId, "")
                        else:
                            items.updateItem(accessToken, itemName, itemDescription, itemId, itemPrice, itemImage, "", itemPriceType, itemVariationId, "")
                    elif result.is_error():
                        print(result.errors)

                else:
                    if items.checkItem(itemId):
                        items.addItem(accessToken, itemName, itemDescription, itemId, itemPrice, "", "", itemPriceType, itemVariationId, "")
                    else:
                        items.updateItem(accessToken, itemName, itemDescription, itemId, itemPrice, "", "", itemPriceType, itemVariationId, "")

            
    elif result.is_error():
        print(result.errors)

@app.route('/getMerchantDetail', methods=['POST'])
def getMerchantDetail():
    if request.method == 'POST':
        accessToken = request.json['access_token'].replace("'", "''")
        contact =  request.json['contact'].replace("'", "''")

        # todo: get loyalty program and user loyalty points
        return {
            "code": 0,
            "msg": "Success!",
            "items": items.getMerchantItems(accessToken)
        }

@app.route('/getAllMerchants', methods=['POST'])
def getAllMerchants():
    if request.method == 'POST':
        return {
            "code": 0,
            "msg": "Success!",
            "merchants": merchant.getAllMerchants()
        }

@app.route('/getLoyaltyInfo', methods=['POST'])
def getLoyaltyInfo():
    if request.method == 'POST':
        contact = request.json['contact'].replace("'", "''")
        accessToken = request.json['access_token'].replace("'", "''")

        # 1. get the loyalty info
        client = Client (
            access_token=accessToken,
            environment='sandbox')

        result = client.loyalty.retrieve_loyalty_program(
            program_id = "main"
        )

        if result.is_success():
            searchCustomerResult = client.customers.search_customers(
                body = {
                    "query": {
                        "filter": {
                            "phone_number": {
                                "exact": contact
                            }
                        }
                    }
                }
            )
            # 2. get if the user has visited the business
            if searchCustomerResult.is_success():
                if 'customers' in searchCustomerResult.body:
                    # Visited
                    customerIds = []
                    customerIds.append(searchCustomerResult.body['customers'][0]['id'])
                    searchLoyaltyAccountResult = client.loyalty.search_loyalty_accounts(
                        body = {
                            "query": {
                            "customer_ids": customerIds
                            }
                        }
                    )

                    if searchLoyaltyAccountResult.is_success():
                        if 'loyalty_accounts' in searchLoyaltyAccountResult.body:
                            # 3. get the programId, loyaltyAccountId, and points
                            return {
                                "code": 0,
                                "msg": "Success!",
                                "loyalty_info": result.body,
                                "is_enrolled": 1,
                                "points": searchLoyaltyAccountResult.body['loyalty_accounts'][0]['balance'],
                                "loyalty_account": searchLoyaltyAccountResult.body['loyalty_accounts'][0]['id']
                            }
                        else:
                            # the customer hasn't enrolled the loyalty program
                            return {
                                "code": 0,
                                "msg": "Success!",
                                "loyalty_info": result.body,
                                "is_enrolled": 0,
                                "points": 0
                            }
                    elif searchLoyaltyAccountResult.is_error():
                        return {
                            "code": 1,
                            "msg": searchCustomerResult.errors[0]['detail'],
                            "loyalty_info": None,
                            "is_enrolled": 0,
                            "points": 0
                        }
                else:
                    # Not visited before
                    
                    return {
                        "code": 0,
                        "msg": "Success!",
                        "loyalty_info": result.body,
                        "is_enrolled": 0,
                        "points": 0
                    }
            elif searchCustomerResult.is_error():

                return {
                    "code": 1,
                    "msg": searchCustomerResult.errors[0]['detail'],
                    "loyalty_info": None,
                    "is_enrolled": 0,
                    "points": 0
                }

        elif result.is_error():
            return {
                "code": 1,
                "msg": result.errors[0]['detail'],
                "loyalty_info": None,
                "is_enrolled": 0,
                "points": 0
            }

@app.route('/enrollLoyalty', methods=['POST'])
def enrollLoyalty():
    if request.method == 'POST':
        contact = request.json['contact'].replace("'", "''")
        accessToken = request.json['access_token'].replace("'", "''")
        programId = request.json['program_id'].replace("'", "''")

        # 1. get the loyalty info
        client = Client (
            access_token=accessToken,
            environment='sandbox')

        result = client.customers.search_customers(
            body = {
                "query": {
                    "filter": {
                        "phone_number": {
                            "exact": contact
                        }
                    }
                }
            }
        )

        if result.is_success():
            if 'customers' in result.body:
                # the user is a customer of the business
                # create loyalty account
                return createLoyaltyAccount(contact, accessToken, client, programId, result.body['customers'][0]['id'])
            else:
                # the user isn't a customer of the business
                # create customer
                nickname = customer.getNickname(contact)
                createCustomerResult = client.customers.create_customer(
                    body = {
                        "nickname": nickname,
                        "phone_number": contact
                    }
                )
                if createCustomerResult.is_success():
                    # create loyalty account
                    return createLoyaltyAccount(contact, accessToken, client, programId, createCustomerResult.body['customer']['id'])
                elif resucreateCustomerResultlt.is_error():
                    return {
                        "code": 1,
                        "msg": createCustomerResult.errors[0]['detail'],
                    }
        elif result.is_error():
            return {
                "code": 1,
                "msg": result.errors[0]['detail'],
            }

def createLoyaltyAccount(contact, accessToken, client, programId, customerId):
    createLoyaltyAccountResult = client.loyalty.create_loyalty_account(
        body = {
            "loyalty_account": {
                "program_id": programId,
                "customer_id": customerId,
                    "mapping": {
                        "phone_number": contact
                    }
            },
            "idempotency_key": uuid.uuid1().hex
        }
    )
    if createLoyaltyAccountResult.is_success():
        saveCustomerLoyaltyRecords(contact, accessToken)
        return {
            "code": 0,
            "msg": "Success!",
            "loyalty_account": createLoyaltyAccountResult.body['loyalty_account']['id']
        }
    elif createLoyaltyAccountResult.is_error():
        return {
            "code": 1,
            "msg": createLoyaltyAccountResult.errors[0]['detail'],
        }

def saveCustomerLoyaltyRecords(contact, accessToken):
    loyalty.enrollLoyalty(contact, accessToken)

@app.route('/getAllLoyaltyRecords', methods=['POST'])
def getAllLoyaltyRecords():
    if request.method == 'POST':
        contact = request.json['contact'].replace("'", "''")
        records = loyalty.getLoyaltyRecords(contact)
        recordedMerchants = []
        for accessToken in records:
            recordedMerchants.append(merchant.getMerchantInfo(accessToken))
        return {
            "code": 0,
            "msg": "Success!",
            "loyalty_merchants": recordedMerchants,
        }


@app.route('/verifyPhone', methods=['POST'])
def verifyPhone():
    if request.method == 'POST':
        phoneNumber = request.json['contact'].replace("'", "''")
        print("phone number: ", phoneNumber)
        code = phoneVerify.sendSMS(phoneNumber)
        return {"code":0, "msg": code}


@app.route('/login', methods=['POST'])
def login():
    if request.method == 'POST':
        phoneNumber = request.json['contact'].replace("'", "''")

        if customer.checkCustomer(phoneNumber):
             return {"code":1, "msg": "The phone number doesn't exist. Please sign up!"}
        return {"code":0, "msg": "Success!", "customer": customer.getCustomer(phoneNumber)}

@app.route('/updateNotify', methods=['POST'])
def loupdateNotifygin():
    if request.method == 'POST':
        phoneNumber = request.json['contact'].replace("'", "''")
        ifNotify = request.json['ifNotify']
        customer.updateNotify(phoneNumber, ifNotify)
        return {"code":0, "msg": "Success!"}


@app.route('/saveCustomer', methods=['POST'])
def saveCustomer():
    if request.method == 'POST':


        contact = request.json['contact'].replace("'", "''")
        nickname = request.json['nickname'].replace("'", "''")
        ifNotify = request.json['ifNotify']
        password = request.json['password']

        if customer.checkCustomer(contact):
            customer.addCustomer(contact, nickname, ifNotify, password)
            return {"code": 0, "msg": "Success!"}
           

        else:
            return {"code":1, "msg": "The phone number has been signed up!"}

@app.route('/checkCustomer', methods=['POST'])
def checkCustomer():
    if request.method == 'POST':
        contact = request.json['contact'].replace("'", "''")

        if customer.checkCustomer(contact):
            return {"code": 0, "msg": "Success!"}
           

        else:
            return {"code":1, "msg": "The phone number has been signed up!"}

@app.route('/placeOrder', methods=['POST'])
def placeOrder():
    if request.method == 'POST':
        contact = request.json['contact'].replace("'", "''")
        accessToken = request.json['accessToken'].replace("'", "''")
        orderId = request.json['orderId'].replace("'", "''")
        timeStamp = request.json['timeStamp']
        finalPrice = request.json['finalPrice']
        originalPrice = request.json['originalPrice']
        discountValue = request.json['discountValue']
        loyaltyValue = request.json['loyaltyValue']
        itemCount = request.json['itemCount']
        timeString = request.json['timeString'].replace("'", "''")
        orders.addOrder(accessToken, orderId, contact, timeStamp, finalPrice, originalPrice, discountValue, loyaltyValue, itemCount, timeString)
           
        return {"code":0, "msg": "success!"}

@app.route('/getAllOrders', methods=['POST'])
def getAllOrders():
    if request.method == 'POST':
        contact = request.json['contact'].replace("'", "''")
        records = orders.getAllOrders(contact)
        return {
            "code": 0,
            "msg": "Success!",
            "orders": records,
        }



@app.route('/getOrderItems', methods=['POST'])
def getOrderItems():
    if request.method == 'POST':
        orderId = request.json['orderId'].replace("'", "''")
        accessToken = request.json['accessToken'].replace("'", "''")
        
        client = Client (
            access_token=accessToken,
            environment='sandbox')

        result = client.orders.retrieve_order(
            order_id = orderId
        )

        if result.is_success():
            resultItems = []
            for item in result.body['order']['line_items']: 
                resultItems.append(items.getItem(item['catalog_object_id']))
            return {
                "code": 0,
                "msg": "Success!",
                "items": resultItems,
            }

        elif result.is_error():
            return {
                "code": 1,
                "msg": result.errors[0]['detail'],
                "items": null,
            }



if __name__ == '__main__':
    app.debug = True
    app.run(host='0.0.0.0', port=8900)