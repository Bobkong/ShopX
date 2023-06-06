class Orders:

    def connect(self):
        import pyodbc
        server = 'tcp:shopxdb.database.windows.net' 
        database = 'ShopX_DB' 
        username = 'lingshuangkong' 
        password = 'Shopxpassword.'
        driver= '{ODBC Driver 18 for SQL Server}'
        cnxn = pyodbc.connect('DRIVER='+driver+';SERVER='+server+';PORT=1443;DATABASE='+database+';UID='+username+';PWD='+ password)
        return cnxn.cursor()

    def addOrder(self, accessToken, orderId, contact, timeStamp, finalPrice, originalPrice, discountValue, loyaltyValue, itemCount, timeString):
        self.cursor = self.connect()
        command = "INSERT INTO ORDERS (AccessToken, OrderId, Contact, TimeStamp, FinalPrice, OriginalPrice, DiscountValue, LoyaltyValue, ItemCount, TimeString) " + \
        f"VALUES ('{accessToken}', '{orderId}', '{contact}', '{timeStamp}', '{finalPrice}', '{originalPrice}', '{discountValue}', '{loyaltyValue}', '{itemCount}', '{timeString}')"

        self.cursor.execute(command)
        self.cursor.commit()

    def getAllOrders(self, contact):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM ORDERS " +
            f"WHERE Contact='{contact}' " +
            f"ORDER BY TimeStamp DESC")
        res_list = self.cursor.fetchall()
        orders = []
        for orderInfo in res_list:
            accessToken, orderId, _, finalPrice, originalPrice, discountValue, loyaltyValue, itemCount, timeStamp, timeString = orderInfo
            orders.append({
                "accessToken": accessToken,
                "orderId": orderId,
                "timeStamp": timeStamp,
                "finalPrice": finalPrice,
                "originalPrice": originalPrice,
                "discountValue": discountValue,
                "loyaltyValue": loyaltyValue,
                "itemCount": itemCount,
                "timeString": timeString,
            })
        return orders
