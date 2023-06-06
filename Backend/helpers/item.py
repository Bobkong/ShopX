class Items:

    def connect(self):
        import pyodbc
        server = 'tcp:shopxdb.database.windows.net' 
        database = 'ShopX_DB' 
        username = 'lingshuangkong' 
        password = 'Shopxpassword.'
        driver= '{ODBC Driver 18 for SQL Server}'
        cnxn = pyodbc.connect('DRIVER='+driver+';SERVER='+server+';PORT=1443;DATABASE='+database+';UID='+username+';PWD='+ password)
        return cnxn.cursor()


    def addItem(self, accessToken, itemName, itemDescription, itemId, itemPrice, itemImage, ARLink, pricingType, itemVariationId, AREffectLink):
        self.cursor = self.connect()
        print("add item")
        command = "INSERT INTO ITEMS (AccessToken, ItemName, ItemDescription, ItemId, ItemPrice, ItemImage, ARLink, PricingType, ItemVariationId, AREffectLink) " + \
        f"VALUES ('{accessToken}', '{itemName}', '{itemDescription}', '{itemId}', '{itemPrice}', '{itemImage}', '{ARLink}', '{pricingType}', '{itemVariationId}', '{AREffectLink}')"

        self.cursor.execute(command)
        self.cursor.commit()

    def checkItem(self, itemId): 
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT COUNT(1) " +
            "FROM ITEMS " +
            f"WHERE ItemId='{itemId}'")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        if res_list[0][0] == 0:
            return True # Item hasn't been saved before.
        return False # Item exists already.

    def getItem(self,variationId): 
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM ITEMS " +
            f"WHERE ItemVariationId='{variationId}'")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        for item in res_list:
            accessToken, itemName, itemDescription, itemId, itemPrice, itemImage, ARLink, pricingType, itemVariationId, AREffectLink = item
            return {
                "accessToken": accessToken,
                "itemName": itemName,
                "itemDescription": itemDescription,
                "itemId": itemId,
                "itemPrice": itemPrice,
                "itemImage": itemImage,
                "ARLink": ARLink,
                "pricingType": pricingType,
                "itemVariationId": itemVariationId,
                "AREffectLink": AREffectLink,
            }

    def updateItem(self, accessToken, itemName, itemDescription, itemId, itemPrice, itemImage, ARLink, pricingType, itemVariationId, AREffectLink):
        self.cursor = self.connect()
        self.cursor.execute(
            "UPDATE ITEMS " +
            f"SET AccessToken='{accessToken}', ItemName='{itemName}', ItemDescription='{itemDescription}', ItemPrice='{itemPrice}', ItemImage='{itemImage}', ARLink='{ARLink}', PricingType='{pricingType}', ItemVariationId='{itemVariationId}', AREffectLink='{AREffectLink}' " +
            f"WHERE ItemId='{itemId}'")
        self.cursor.commit()

    def getMerchantItems(self, accessToken):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM ITEMS " +
            f"WHERE AccessToken='{accessToken}'")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        items = []
        for item in res_list:
            _, itemName, itemDescription, itemId, itemPrice, itemImage, ARLink, pricingType, itemVariationId, AREffectLink  = item
            items.append(
                {
                    "itemName": itemName,
                    "itemDescription": itemDescription,
                    "itemId": itemId,
                    "itemPrice": itemPrice,
                    "itemImage": itemImage,
                    "ARLink": ARLink,
                    "pricingType": pricingType,
                    "itemVariationId": itemVariationId,
                    "AREffectLink": AREffectLink,
                }
            )
        return items