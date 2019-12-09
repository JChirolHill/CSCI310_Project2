# Bean & Leaf
### By Katherine Sing, Nathan Chang, Cerina da Graca, Yichun Kong, and Juliette Chirol Hill

![Bean & Leaf Logo](https://firebasestorage.googleapis.com/v0/b/csci310project2-3206e.appspot.com/o/logo.png?alt=media&token=f66b152b-af3f-4129-b313-f2acfdde8305)

To run this app, download the attached zip file, or, if one is not attached, go to https://github.com/JChirolHill/CSCI310_Project2 , click on the green button (labeled "Clone or download") on the right side of the page. Choose "Download Zip." Open the file location of the zip folder, and unzip it. Remember the file location of this unzipped folder. 

Open Android Studio. Click on File --> Open. In the pop-up, find the file location of the file you just unzipped. Then click "OK".

Once the project loads and finishes building properly, you will next choose the device emulator that this app will run on. At the top of the IDE, to the left of the green triangle (the play button), select "Pixel 2 API 27." After you have selected the emulator version,
hit the green play button to the right of the version selector.  This will launch an emulator. The emulator may take some time to launch, so be patient. 

If the Bean & Leaf app does not load within 60 seconds-- Go to the main menu by hitting the center bottom circle again. Hit the up arrow to bring up the menu of all the apps, then click on the "Bean and Leaf" icon. The app will then launch.

You may now log in as either a Customer, or as a Merchant. To log in as a Customer, sign in as: a@a.com, password: aaaaaa. To log in as a Merchant, sign in as z@z.com, password: zzzzzz. As a Customer, you will be able to see a list of shops in your area, and their drinks if the shop has been claimed. When you choose a store on the map, you will be able to get walking or driving directions. When you arrive at the store, you will be notified that you have arrived and can log your order at that store. As a Merchant, if you click on your profile icon in the upper left hand corner, you will see the stores you have claimed, as well as the drink for those stores. You will also be able to claim another store that is currently unclaimed in the map. If you claim a store, it will need to be verified before the store shows up on your account. When you view a store that you have claimed, you can see the total revenue for that store, as well as the best seller of the store. 

To log out, press the back button a certain number of times (depending on what view of the app you are currently located) until the app is closed. Then you can restart the app and log in again.

As a customer, when you are logging and order, click the drink item in the list to increment the number of drinks added to the log. To make the quantity of that item zero in the log, press and hold the drink item.

### Improvements added during 2.5 Sprint:
1. The Customer can view the trip information after a store is selected from the map.
2. Once the Customer starts their trip to the selected store, they will receive a popupmnotification when they reach their store.
3. Merchants can see the stats of their store-- total revenue for the store, and the best seller of the store. 
4. Improved various UI design elements.


### Note:
~~Since claiming a store (as a merchant) requires taking a picture of verification documents, the emulator will crash if you try to take a picture with it (by clicking the Capture button on the Claim Shop Activity).  If you would like to test the claiming a shop functionality, we can provide a demo or please use a real Android device to run the app.~~

### Note2:
~~When you first log in as a Customer, the only store that is currently claimed and has a drinks menu is the Philz Coffee in Palo Alto, CA. Also, because the map is calling the Yelp API for open stores, you will not see this store during the times that the shop is not actually open.~~

### Note 3:
When a Merchant adds a new shop, it must first be verified by our team before that shop is added to that Merchant. Therefore if you want to add a new shop to an existing Merchant to test this functionality, please let our team know about it, and we can approve it on our end.

