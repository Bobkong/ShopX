class Customers:

    def connect(self):
            import pyodbc
            server = 'tcp:shopxdb.database.windows.net' 
            database = 'ShopX_DB' 
            username = 'lingshuangkong' 
            password = 'Shopxpassword.'
            driver= '{ODBC Driver 18 for SQL Server}'
            cnxn = pyodbc.connect('DRIVER='+driver+';SERVER='+server+';PORT=1443;DATABASE='+database+';UID='+username+';PWD='+ password)
            return cnxn.cursor()

    def addCustomer(self, contact, nickname, ifNotify, password):
        self.cursor = self.connect()
        command = "INSERT INTO CUSTOMERS (Contact, Nickname, IfNotify, Password) " + \
        f"VALUES ('{contact}', '{nickname}', '{ifNotify}', '{password}')"

        self.cursor.execute(command)
        self.cursor.commit()

    def checkCustomer(self, contact):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT COUNT(1) " +
            "FROM CUSTOMERS " +
            f"WHERE Contact='{contact}'")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        if res_list[0][0] == 0:
            return True # Contact hasn't been saved before.
        return False # Contact exists already.

    # Assume username already exists in database.
    def checkPassword(self, contact, password):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT COUNT(1) " +
            "FROM CUSTOMERS " +
            f"WHERE Contact='{contact}' " +
            f"AND Password='{password}'")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        if res_list[0][0] == 1:
            return True # Password matched.
        return False # Wrong password.

    def getCustomer(self, contact):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM CUSTOMERS " +
            f"WHERE Contact='{contact}' ")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        for customer in res_list:
            c, nickname, p, i = customer
            return {
                "nickname": nickname,
                "ifNotify": i,
            }

    def updateNotify(self, contact, ifNotify):
        self.cursor = self.connect()
        self.cursor.execute(
            "UPDATE CUSTOMERS " +
            f"SET ifNotify='{ifNotify}' " +
            f"WHERE Contact='{contact}'")
        self.cursor.commit()

    def getNickname(self, contact):
        self.cursor = self.connect()
        self.cursor.execute(
            "SELECT * " +
            "FROM CUSTOMERS " +
            f"WHERE Contact='{contact}' ")
        res_list = self.cursor.fetchall() # Should expect 1 row.
        for customer in res_list:
            c, nickname, p, i = customer
            return nickname