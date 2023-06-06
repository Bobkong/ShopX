class Loyalty:

    def connect(self):
        import pyodbc
        server = 'tcp:shopxdb.database.windows.net' 
        database = 'ShopX_DB' 
        username = 'lingshuangkong' 
        password = 'Shopxpassword.'
        driver= '{ODBC Driver 18 for SQL Server}'
        cnxn = pyodbc.connect('DRIVER='+driver+';SERVER='+server+';PORT=1443;DATABASE='+database+';UID='+username+';PWD='+ password)
        return cnxn.cursor()

    def enrollLoyalty(self, contact, accessToken):
        self.cursor = self.connect()
        command = "INSERT INTO LOYALTY (Contact, AccessToken) " + \
        f"VALUES ('{contact}', '{accessToken}')"

        self.cursor.execute(command)
        self.cursor.commit()

    def getLoyaltyRecords(self, contact):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM LOYALTY " +
            f"WHERE Contact='{contact}' ")
        res_list = self.cursor.fetchall()
        records = []
        for loyaltyInfo in res_list:
            _, accessToken = loyaltyInfo
            records.append(accessToken)
        return records