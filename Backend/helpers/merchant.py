class Merchants:

    def connect(self):
        import pyodbc
        server = 'tcp:shopxdb.database.windows.net' 
        database = 'ShopX_DB' 
        username = 'lingshuangkong' 
        password = 'Shopxpassword.'
        driver= '{ODBC Driver 18 for SQL Server}'
        cnxn = pyodbc.connect('DRIVER='+driver+';SERVER='+server+';PORT=1443;DATABASE='+database+';UID='+username+';PWD='+ password)
        return cnxn.cursor()

    def addMerchant(self, merchantId, accessToken, logoUrl, addressLine1, locality, administrativeDistrictLevel1, postalCode, lat, lng, ifLoyalty, discountProducts, discountType, discountAmount, businessName, locationId, discountName):
        self.cursor = self.connect()
        command = "INSERT INTO MERCHANTS (MerchantId, AccessToken, LogoUrl, AddressLine1, Locality, AdministrativeDistrictLevel1, PostalCode, Lat, Lng, IfLoyalty, DiscountProducts, DiscountType, DiscountAmount, BusinessName, LocationId, DiscountName) " + \
        f"VALUES ('{merchantId}', '{accessToken}', '{logoUrl}', '{addressLine1}', '{locality}', '{administrativeDistrictLevel1}', '{postalCode}', '{lat}', '{lng}', '{ifLoyalty}', '{discountProducts}', '{discountType}', '{discountAmount}', '{businessName}', '{locationId}', '{discountName}')"

        self.cursor.execute(command)
        self.cursor.commit()

    def checkMerchant(self, merchantId): 
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT COUNT(1) " +
            "FROM MERCHANTS " +
            f"WHERE MerchantId='{merchantId}'")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        if res_list[0][0] == 0:
            return True # Merchant hasn't been saved before.
        return False # Merchant exists already.
    

    def updateMerchant(self, merchantId, accessToken, logoUrl, addressLine1, locality, administrativeDistrictLevel1, postalCode, lat, lng, ifLoyalty, discountProducts, discountType, discountAmount, businessName, locationId, discountName):
        self.cursor = self.connect()
        self.cursor.execute(
            "UPDATE MERCHANTS " +
            f"SET AccessToken='{accessToken}', LogoUrl='{logoUrl}', AddressLine1='{addressLine1}', Locality='{locality}', AdministrativeDistrictLevel1='{administrativeDistrictLevel1}', PostalCode='{postalCode}', Lat='{lat}', Lng='{lng}', IfLoyalty='{ifLoyalty}', DiscountProducts='{discountProducts}', DiscountType='{discountType}', DiscountAmount='{discountAmount}', BusinessName='{businessName}', LocationId='{locationId}', DiscountName='{discountName}' " +
            f"WHERE MerchantId='{merchantId}'")
        self.cursor.commit()

    def getMerchantInfo(self, accessToken):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM MERCHANTS " +
            f"WHERE AccessToken='{accessToken}'")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        for merchant in res_list:
            merchantId, accessToken, logoUrl, addressLine1, locality, administrativeDistrictLevel1, postalCode, lat, lng, ifLoyalty, discountProducts, discountType, discountAmount, businessName, arEnable, locationId, discountName, recommendBg, recommend, recommendText = merchant
            return {
                "merchantId": merchantId,
                "accessToken": accessToken,
                "logoUrl": logoUrl,
                "addressLine1": addressLine1,
                "locality": locality,
                "administrativeDistrictLevel1": administrativeDistrictLevel1,
                "postalCode": postalCode,
                "lat": lat,
                "lng": lng,
                "ifLoyalty": ifLoyalty,
                "discountProducts": discountProducts,
                "discountType": discountType,
                "discountAmount": discountAmount,
                "businessName": businessName,
                "arEnable": arEnable,
                "locationId": locationId,
                "discountName": discountName,
                "recommend": recommend,
                "recommendBg": recommendBg,
                "recommendText": recommendText,
            }

    def getAllMerchants(self):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM MERCHANTS ")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        merchants = []

        for merchant in res_list:
            merchantId, accessToken, logoUrl, addressLine1, locality, administrativeDistrictLevel1, postalCode, lat, lng, ifLoyalty, discountProducts, discountType, discountAmount, businessName, arEnable, locationId, discountName, recommendBg, recommend, recommendText = merchant
            merchants.append({
                "merchantId": merchantId,
                "accessToken": accessToken,
                "logoUrl": logoUrl,
                "addressLine1": addressLine1,
                "locality": locality,
                "administrativeDistrictLevel1": administrativeDistrictLevel1,
                "postalCode": postalCode,
                "lat": lat,
                "lng": lng,
                "ifLoyalty": ifLoyalty,
                "discountProducts": discountProducts,
                "discountType": discountType,
                "discountAmount": discountAmount,
                "businessName": businessName,
                "arEnable": arEnable,
                "locationId": locationId,
                "discountName": discountName,
                "recommend": recommend,
                "recommendBg": recommendBg,
                "recommendText": recommendText,
            })

        return merchants
