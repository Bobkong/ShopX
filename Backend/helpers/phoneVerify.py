# Download the helper library from https://www.twilio.com/docs/python/install
import os
from twilio.rest import Client
import string
import random


class PhoneVerify:

    def sendSMS(self, phoneNumber):
        
        # Find your Account SID and Auth Token at twilio.com/console
        # and set the environment variables. See http://twil.io/secure
        account_sid = 'Replace Me'
        auth_token = 'Replace Me'
        client = Client(account_sid, auth_token)

        code = self.generate_code()
        bodyString = "Your ShopX verification code is: " + code
        print("code: ", code)

        message = client.messages.create(
        from_='+18557592243',
        body=bodyString,
        to=phoneNumber
        )
        return code
    
    def generate_code(self):
        seeds = string.digits
        random_str = []
        for i in range(4):
            random_str.append(random.choice(seeds))
        return "".join(random_str)