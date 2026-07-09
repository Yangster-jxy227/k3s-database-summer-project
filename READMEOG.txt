=================
    John Yang
     jxy227
     CSE 241
  Final Project
=================

=====================================
Refactored from Oracle to Postgres
due to losing access to Lehigh's 
Oracle Database after end of Class.
Functionalities largely same, but
password no longer hidden when typing
=====================================

Interfaces:
    Database Login: Use jxy227 and Oracle password to login to the database
        - Connects to Database
        - Allows for retries, but is the only time a exception error message is displayed
        - Does not have a mechanism to exit program

    User Login: Will ask for user email and password and save user ID and customer type (Manager, Customer, Business)
        - Proceeds into UIcustomer
        - Depending on customer type (Managers are Customers but with extra functionalities), will display a list of appropriate users in database
        - Displays Name, Email, Password - for Palmeri's eyes only :p
        - Allows for retries when either or both the email and password are incorrect
        - No way to make new Users
        - Does not have a mechanism to exit program 
        
    Generic: Displays options to choose from with 'Back' and 'Exit' always being the last two options. 
        - Input a number
        - [x] 'Back' leads to previous screen
        - [x] 'Exit' will end the program safely 
        - Will allow retries when a number outside the range is provided
        - Will allow retries when a 

    Input: Some inputs will require digits only, some are more flexible and ask for a string
        - Digit input: Usually for quantities
        - String input: Can be for quantities, but more usually for names 
        - Usually limits are present to prevent wrong inputs
        - Allows for retries
        - Typing "Back" will go back to previous screen
        - Typing "Exit" will end program gracefully
        - Typing "Restart" will restart process - very few have this, this was added late and not brought to every input

Files: 
    App.java:
        - Database Login and Connection
        - User Login 
        - Leads to UIcustomer or UImanager based on user login
        - Defines masterExit() which will disconnect from database and end program, can be used anywhere

    UIcustomer: 
        - Handles UI for customers
        - 4 Intial Options 
            - Go to Catalog 
                - leads to DBcatalog
            - Check User Finances
                - Can see current payment methods and add more
            - Purchase History
            - Exit 
        - Handles creating new payment methods 
        - No back method as this is the first screen to appear after login
        
    UImanager:
        - Handles UI for managers
        - Same as UIcustomer but expanded to include Reports and Adding Products
        - 6 Intial Options - 4 Same as UIcustomer and two more
            - Reports  
                - Report 1: Average charge per purchase for every customer
                - Report 2: Total Number of Purchases per Product
                - Report 3: Total Number of Subscription per Installment Plan
            - Adding Products
                - Can add services and items, able to determine all the relevant fields
            - Exit 
        - No back method as this is the first screen to appear after login
    
    DBcatalog:
        - Handles both UI and DB calls for catalog
        - 6 Intial Options 
            - Can see items, services, or all of them together
            - Buy Product 
            - Back into UIcustomer or UImanager
            - Exit
        - Purchases goods 
            - If business, can only buy services via Bank Account
            - Customers can buy everything 
            - Payment for items
                - Credit Card Payment
                - Installment Plan
            - Payment for services
                -  Bank Account Payment

    DBController:
        - Handles all SQL queries and inserts from UImanager and UIcustomer 
            - Payment and Purchase
            - Creates goods for UImanager
            - Handles UImanager reports
        - Connects to Database
        - Finds information from the user

Assumptions:
    - Managers are Customers with privilegdes 
    - Users subscribe to 1 Installment plan and cannot have multiple plans. They can only choose their plan during a purchase
    - Data generated via ChatGPT and further edited manually
