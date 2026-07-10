//Handles catalog interface info
//import java.io.Console;
import java.util.Scanner;
import java.sql.*; 
import java.util.List;
import java.util.ArrayList;

public class DBcatalog{
    User thisUser; 
    //Console console;
    Scanner scan;
    DBController db;
    Connection conn;

    UIcustomer UIc;
    UImanager UIm;
    String ui;

    //i=0: Product ID, i=1: Name, i=2 Price, (Only for Items) i=3: Stock
    //Expand to be a list of list<Object> to allow for multiple objects being bought
    List<Object> purchaseCache = new ArrayList<>();

    //Constructor
    DBcatalog(User fetchedUser, Scanner appScan, DBController db){
        this.thisUser = fetchedUser;
        //this.console = appConsole;
        this.scan = appScan;
        this.db = db;
        this.conn = db.getConnection();
    }

    public void fetchUIcustomer(UIcustomer UIc){
        this.UIc = UIc;
        ui = "customer";
    }

    public void fetchUImanager(UImanager UIm){
        this.UIm = UIm;
        ui = "manager";
    }

    ////----Handles Screen/App work----////
    public void back(int menu){
        switch(menu){
            case 1: //On catalog Screen
                if(ui.equals("customer")){
                    UIc.mainScreen(); 
                }
                if(ui.equals("manager")){
                    //UIc.mainScreen(); 
                    UIm.mainScreen();
                }  
                break;
            case 2: //promptBuy() or completion of a purchase
                catalogScreen();
                break;
            case 3: //Inputting new payment process - promptItemPurchase()/promptServicePurchase
                promptBuy();
                break;
            case 4: //
                promptItemPayment();
                break;
            case 5:
                promptServicePayment();
                break;
        }
    }

    public void selectionPrompt(int prompt){
        switch(prompt){
            case 1: //catalog screen
                System.out.println("LUShop Catalog:\nChoose what you wish to do below\n"
                                + "[1] Display Items\n"
                                + "[2] Display Services\n"
                                + "[3] Display All\n"
                                + "[4] Buy (Goods will be displayed before buying)\n"
                                + "[5] Back\n"
                                + "[6] Exit\n");
                break;
            case 2: //promptBuy()
                System.out.println("Would you like to buy an Item or a Service?\n"
                                + "[1] Item\n"
                                + "[2] Service\n"
                                + "[3] Back\n"
                                + "[4] Exit\n");
                break;
            case 3: //promptItemPayment
                System.out.println("How do you want pay today?\n"
                                + "[1] Credit Card\n"
                                + "[2] Installment\n"
                                + "[3] Back\n"
                                + "[4] Exit\n");
                break;
            case 4: //promptServicePayment
                System.out.println("How do you want pay today?\n"
                                + "[1] Bank Account\n"
                                + "[2] Back\n"
                                + "[3] Exit\n");
                break;
            case 5: //promptBusinessBuy()
                System.out.println("Would you like to buy an Item or a Service?\n"
                                + "[1] Service\n"
                                + "[2] Back\n"
                                + "[3] Exit\n");
                break;
        }
    }

    public void catalogScreen(){
        int input;

        do {    
            selectionPrompt(1);

            while (!scan.hasNextInt()) {
                System.out.println("Invalid input - please enter a number\n");
                scan.next();
                selectionPrompt(1);
            }

            input = scan.nextInt();
            scan.nextLine();

            if(input < 1 || input > 6){
                System.out.println("Input Integer between 1-6\n");
            }

        } while (input < 1 || input > 6);

        switch(input){
            case 1:
                displayItems();
                catalogScreen();
                break;
            case 2:
                displayServices();
                catalogScreen();
                break;
            case 3:
                displayItems();
                System.out.println("\n");
                displayServices();
                catalogScreen();
                break;
            case 4:
                if(thisUser.getType() == "Business"){
                    promptBusinessBuy();
                }
                else{
                    promptBuy();
                }
                App.masterExit(db);
                break;
            case 5:
                back(1);
                App.masterExit(db);
                break;
            case 6:
                App.masterExit(db);
                break;
        }
    } 

    public void promptBusinessBuy(){
         int input;

        do {    
            selectionPrompt(5);

            while (!scan.hasNextInt()) {
                System.out.println("Invalid input - please enter a number\n");
                scan.next();
                selectionPrompt(5);
            }

            input = scan.nextInt();
            scan.nextLine();

            if(input < 1 || input > 3){
                System.out.println("Input Integer between 1-3\n");
            }

        } while (input < 1 || input > 3);

        switch(input){
            case 1:
                goodSelection(2);   //Service
                //App.masterExit(db);
                break;
            case 2:
                back(2);
                break;
            case 3:
                App.masterExit(db);
                break;
        }
    }

    ////----Purchasing----////
    public void promptBuy(){
        int input;

        do {    
            selectionPrompt(2);

            while (!scan.hasNextInt()) {
                System.out.println("Invalid input - please enter a number\n");
                scan.next();
                selectionPrompt(2);
            }

            input = scan.nextInt();
            scan.nextLine();

            if(input < 1 || input > 4){
                System.out.println("Input Integer between 1-4\n");
            }

        } while (input < 1 || input > 4);

        switch(input){
            case 1: 
                goodSelection(1);   //Item
                //App.masterExit(db);
                break;
            case 2:
                goodSelection(2);   //Service
                //App.masterExit(db);
                break;
            case 3:
                back(2);
                break;
            case 4:
                App.masterExit(db);
                break;
        }
    }

    public void goodSelection(int goodType){
        String input;
        boolean backStatus = false;
        
        if(goodType == 1){
            displayItems();
        }
        if(goodType == 2){
            displayServices();
        }

        System.out.println("At any point, cancel by typing 'back' to go to the previous screen or 'exit' to disconnect from program");
        System.out.println("What would you like to buy?\n");

        //Product ID
        while(true){
            System.out.println("Enter Product ID: ");
            input = scan.nextLine().trim();

            if(input.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(input.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }

            if(!input.matches("^\\d+$")){
                System.out.println("Invalid input - Product ID must be an integer");
                continue;
            }

            //As for Quantity
            //Verify Stock is greater than Quantity

            //Checking if the product is real and what the user selected earlier
            int num = Integer.parseInt(input);
            int productVerification = verifyProduct(num);

            if(productVerification != 0){
                if(goodType == 1 && goodType != productVerification){
                    System.out.println("Good chosen is an Item yet Product ID provided is a Service. Please choose an Item");
                    goodSelection(1);
                }
                if(goodType == 2 && goodType != productVerification){
                    System.out.println("Good chosen is a Service yet Product ID provided is an Item. Please choose a Service");
                    goodSelection(2);
                }
                System.out.println("\nProduct ID matches Good type choosen");
                purchaseCache.add(num);
                db.fetchProductInfo(purchaseCache, num, goodType, 1); //1 is quantity, change later
                break;
            }
        } 

        if(backStatus == true){
            back(2);
        }

        if(goodType == 1){
            promptItemPayment();
        }
        if(goodType == 2){
            promptServicePayment();
        }
    }

    public void promptItemPayment(){
        //Display Credit Card and Installment
        System.out.println("======================================");
        System.out.println("Your Credit Cards are displayed below");
        db.fetchUserCC(db.conn, thisUser);
        System.out.println("======================================");
        System.out.println("Your Installments are displayed below");
        db.fetchUserInstallment(db.conn, thisUser);
        System.out.println("\n");

        int input;
        //Prompt User for payment selection

        do {    
            selectionPrompt(3);

            while (!scan.hasNextInt()) {
                System.out.println("Invalid input - please enter a number\n");
                scan.next();
                selectionPrompt(3);
            }

            input = scan.nextInt();
            scan.nextLine();

            if(input < 1 || input > 4){
                System.out.println("Input Integer between 1-4\n");
            }

        } while (input < 1 || input > 4);

        //Separate functions for each payment
        switch(input){
            case 1: 
                makingCCPurchase();
                break;
            case 2:
                makingInstallmentPuchase();
                break;
            case 3:
                back(3);
                break;
            case 4:
                App.masterExit(db);
                break;
        }
    }

    public void promptServicePayment(){
        //Display Bank Account
        System.out.println("\nYour Bank Accounts are displayed below");
        db.fetchUserBankAccount(db.conn, thisUser);

        int input;
        do {    
            selectionPrompt(4);

            while (!scan.hasNextInt()) {
                System.out.println("Invalid input - please enter a number\n");
                scan.next();
                selectionPrompt(4);
            }

            input = scan.nextInt();
            scan.nextLine();

            if(input < 1 || input > 3){
                System.out.println("Input Integer between 1-3\n");
            }

        } while (input < 1 || input > 3);

        //Separate functions for each payment
        switch(input){
            case 1: 
                makingBAPurchase();
                break;
            case 2:
                back(3);
                break;
            case 3:
                App.masterExit(db);
                break;
        }
    }

    public void makingCCPurchase(){
        String numInput;
        String codeInput;
        String networkInput;
        boolean backStatus = false;
        boolean cardIsUsers = false;
        boolean cardCanBuy = false;
        
        //Card Number
        do{
            System.out.println("At any point, cancel by typing 'back' to go to the previous screen or 'exit' to disconnect from program");
            while(true){
                System.out.println("Enter Card Number (Must be 16 digits): ");
                numInput = scan.nextLine().trim();

                if(numInput.equalsIgnoreCase("back")){
                    backStatus = true;
                    break;
                }

                if(numInput.equalsIgnoreCase("exit")){
                    App.masterExit(db);
                }

                if(numInput.length() != 16 || !numInput.matches("^\\d{16}$")){
                    System.out.println("Invalid input - Card Number must be exactly 16 digits");
                    continue;
                }
                break;
            } 

            if(backStatus == true){
                back(4);
            }

            //Security Code
            while(true) {
                System.out.println("Enter 3-digit Security Code (CVV):");
                codeInput = scan.nextLine().trim();

                if(numInput.equalsIgnoreCase("back")){
                    backStatus = true;
                    break;
                }

                if(codeInput.equalsIgnoreCase("exit")){
                    App.masterExit(db);
                }

                if(codeInput.length() != 3 || !codeInput.matches("^\\d{3}$")) {
                    System.out.println("Invalid input - Security Code must be exactly 3 digits\n");
                    continue;
                }

                break; 
            }

            if(backStatus == true){
                back(4);
            }
            //Verifies card is tied to the user and if it has enough balance to pay for the item
            cardIsUsers = verifyCC(numInput, codeInput);
            if(cardIsUsers == true){
                cardCanBuy = checkCCBalance(numInput, codeInput);
            }
            if(cardCanBuy == false){
                back(4);
            }
        } while(cardIsUsers == false && cardCanBuy == false);
        
        //If card is tied to the user and has enough balance to pay for the item 
        dbCC_Purchase(numInput);
        back(2);
    }

    public void makingInstallmentPuchase(){
        String planInput;
        String networkInput;
        boolean backStatus = false;
        boolean cardIsUsers = false;
        boolean cardCanBuy = false;
                
        //planInput
        while(true){
            System.out.println("At any point, cancel by typing 'back' to go to the previous screen or 'exit' to disconnect from program");
            System.out.println("Select Plan: ");
            planInput = scan.nextLine().trim();
            System.out.println(planInput);

            if(planInput.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(planInput.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }

            //verifies that the plan exists 
            if(!db.verifyPlanExists(planInput)){
                System.out.println("Plan Does not Exit. Choose a valid plan");
                continue;
            }

            //verifies that the user does have this plan
            if(!db.verifyNewPlan(planInput, thisUser)){
                System.out.println("You do not have this plan. Choose your plan");
                continue;
            }
            break;
        } 
            
        if(backStatus == true){
            back(4);
        }
        
        //If card is tied to the user and has enough balance to pay for the item 
        dbInstallment_Purchase(planInput);
        back(2);
    }

    public void makingBAPurchase(){
        String routingInput;
        String bankInput;
        boolean backStatus = false;
        boolean accountIsUsers = false;
        boolean canBuy = false;

        do{
            System.out.println("At any point, cancel by typing 'back' to go to the previous screen or 'exit' to disconnect from program");
            //Routing ID
            while(true){
                System.out.println("Enter Routing ID (Must be 9 digits): ");
                routingInput = scan.nextLine().trim();

                if(routingInput.equalsIgnoreCase("back")){
                    backStatus = true;
                    break;
                }

                if(routingInput.equalsIgnoreCase("exit")){
                    App.masterExit(db);
                }

                if(routingInput.length() != 9 || !routingInput.matches("^\\d{9}$")){
                    System.out.println("Invalid input - Card Number must be exactly 9 digits");
                    continue;
                }
                break;
            } 

            if(backStatus == true){
                back(5);
            }

            //Bank Name
            while(true){
                System.out.println("Enter Bank Name: ");
                bankInput = scan.nextLine().trim();

                if(bankInput.equalsIgnoreCase("back")){
                    backStatus = true;
                    break;
                }

                if(bankInput.equalsIgnoreCase("exit")){
                    App.masterExit(db);
                }
                break;
            } 

            if(backStatus == true){
                back(5);
            }
            //Verifies bank account is tied to the user and if it has enough balance to pay for the item
            accountIsUsers = verifyBA(routingInput, bankInput);
            if(accountIsUsers == true){
                canBuy = checkBABalance(routingInput, bankInput);
            }
            if(canBuy == false){
                back(5);
            }
        } while(accountIsUsers == false && canBuy == false);

        //If card is tied to the user and has enough balance to pay for the item 
        dbBankAcc_Purchase(routingInput, bankInput);
        back(2);
    }

    ////////////////////////
    ////----Database----////
    ////////////////////////
    public void displayItems(){
        String SQL_item = "SELECT product_ID, name, price, vendor, stock FROM Goods natural join Item";

        try{
            PreparedStatement PS_item = conn.prepareStatement(SQL_item);

            ResultSet itemCatalog = PS_item.executeQuery();
            
            System.out.println("Items Displayed Below:");
            System.out.printf("%-5s |%-20s | %-10s | %-20s | %-10s\n", "ID", "Name", "Price", "Vendor", "Stock");
            System.out.printf("----------------------------------------------------------------------\n");
            
            if(!itemCatalog.next()){
                System.out.println("No Items Found");
                return;
            }

            do{
                String id = itemCatalog.getString(1);
                String name = itemCatalog.getString(2);
                String price = itemCatalog.getString(3);
                String vendor = itemCatalog.getString(4);
                String stock = itemCatalog.getString(5);
                System.out.printf("%-5s | %-20s | %-10s | %-20s | %-10s\n", id, name, price, vendor, stock);
            } while(itemCatalog.next());


        }catch (SQLException sql){
            System.out.println(sql);
        }
    }

    public void displayServices(){
        String SQL_service = "SELECT product_ID, name, price, vendor, duration_days FROM Goods natural join Service";

        try{
            PreparedStatement PS_service = conn.prepareStatement(SQL_service);

            ResultSet serviceCatalog = PS_service.executeQuery();
            
            System.out.println("Services Displayed Below:");
            System.out.printf("%-5s | %-20s | %-10s | %-20s | %-10s\n", "ID", "Name", "Price", "Vendor", "Duration");
            System.out.printf("----------------------------------------------------------------------\n");
            
            if(!serviceCatalog.next()){
                System.out.println("No Services Found");
                return;
            }

            do{
                String id = serviceCatalog.getString(1);
                String name = serviceCatalog.getString(2);
                String price = serviceCatalog.getString(3);
                String vendor = serviceCatalog.getString(4);
                String duration = serviceCatalog.getString(5);
                String days = duration + " days";
                System.out.printf("%-5s | %-20s | %-10s | %-20s | %-10s\n", id, name, price, vendor, days);
            } while(serviceCatalog.next());

            System.out.println("----------------------------------------------------------------------\n");
        }catch (SQLException sql){
            System.out.println(sql);
        }
    }

    //true if item exists
    public int verifyProduct(int id){
        //int pID = Integer.parseInt(id);
        boolean item = false;
        boolean service = false;

        //Testing if item
        String SQL_statement = "SELECT COUNT(*) FROM item WHERE product_ID = ?";
        try{
            PreparedStatement SQLproduct = conn.prepareStatement(SQL_statement);
            SQLproduct.setInt(1, id);

            ResultSet countSQL = SQLproduct.executeQuery();

            if(countSQL.next()){
                int count = countSQL.getInt(1);
                if(count == 0){
                    item = false;
                }
                else{
                    item = true;
                }
            }
            else{
                System.out.println("Error with verifying product");
                return 0;
            }
        }catch (SQLException sql){
            System.out.println(sql);
            return 0;
        }  

        ////Testing if service
        SQL_statement = "SELECT COUNT(*) FROM service WHERE product_ID = ?";
        try{
            PreparedStatement SQLproduct = conn.prepareStatement(SQL_statement);
            SQLproduct.setInt(1, id);

            ResultSet countSQL = SQLproduct.executeQuery();

            if(countSQL.next()){
                int count = countSQL.getInt(1);
                if(count == 0){
                    service = false;
                }
                else{
                    service = true;
                }   
            }
            else{
                System.out.println("Error with verifying product");
                return 0;
            }
        }catch (SQLException sql){
            System.out.println(sql);
            return 0;
        }  

        //Interpretatiojn
        if(item == true){
            System.out.println("Good chosen is an item");
            return 1;
        }
        if(service == true){
            System.out.println("Good chosen is a service");
            return 2;
        }
        if(item == false && service == false){
            System.out.println("No Good with a Product ID provided");
            return 0;
        }
        return 0;
    }

    //true if this CC is a the user's CC
    public boolean verifyCC(String cardNum, String securityCode){
        ////Testing if user has this Credit Card
        String SQL_statement = "SELECT count(*) FROM CREDIT_CARD WHERE card_num = ? AND security_code = ? AND customer_ID = ?";
        try{
            PreparedStatement SQLcc = conn.prepareStatement(SQL_statement);
            SQLcc.setString(1, cardNum);
            SQLcc.setString(2, securityCode);
            SQLcc.setInt(3, thisUser.getID());

            ResultSet countSQL = SQLcc.executeQuery();

            if(countSQL.next()){
                int count = countSQL.getInt(1);
                if(count == 0){
                    System.out.println("Either user does not have this credit card or this credit card does not exist");
                    return false;
                }
                System.out.println("User has this Credit Card");
                return true;
            }
            else{
                System.out.println("Error with verifying product");
                return false;
            }
        }catch (SQLException sql){
            System.out.println(sql);
            return false;
        }  
    }

    //true if the Credit Card has enough money to buy the good
    public boolean checkCCBalance(String cardNum, String securityCode){
        double price = (double) purchaseCache.get(2);
        int quantity = (int) purchaseCache.get(3);
        double totalCost = price * quantity;

        ////Testing if credit card has more balance than price of product
        String SQL_statement = "SELECT balance FROM CREDIT_CARD WHERE card_num = ? AND security_code = ? AND customer_ID = ?";
        try{
            PreparedStatement SQLcc = conn.prepareStatement(SQL_statement);
            SQLcc.setString(1, cardNum);
            SQLcc.setString(2, securityCode);
            SQLcc.setInt(3, thisUser.getID());

            ResultSet countSQL = SQLcc.executeQuery();

            if(countSQL.next()){
                double balance = countSQL.getDouble(1);
                if(balance == 0){
                    System.out.println("This Credit Card has no money");
                    return false;
                }
                if(totalCost <= balance){
                    return true;
                }
            }
            else{
                System.out.println("Error with verifying product");
                return false;
            }
        }catch (SQLException sql){
            System.out.println(sql);
            return false;
        }  

        //If totalCost > balance
        System.out.println("Total Cost is greater than the Balance on this card");
        return false;
    }

    public boolean verifyBA(String routingID, String bankName){
        ////Testing if user has this Credit Card
        String SQL_statement = "SELECT count(*) FROM BANK_ACCOUNT WHERE routing_ID = ? AND bank_name = ? AND customer_ID = ?";
        try{
            PreparedStatement SQLba = conn.prepareStatement(SQL_statement);
            SQLba.setString(1, routingID);
            SQLba.setString(2, bankName);
            SQLba.setInt(3, thisUser.getID());

            ResultSet countSQL = SQLba.executeQuery();

            if(countSQL.next()){
                int count = countSQL.getInt(1);
                if(count == 0){
                    System.out.println("Either user does not have an account with this bank or this bank/bank account does not exist");
                    return false;
                }
                //System.out.println("User has an account to this bank");
                return true;
            }
            else{
                System.out.println("Error with verifying product");
                return false;
            }
        }catch (SQLException sql){
            System.out.println(sql);
            return false;
        }  
    }

    public boolean checkBABalance(String routingID, String bankName){
        double price = (double) purchaseCache.get(2);
        int quantity = (int) purchaseCache.get(3);
        double totalCost = price * quantity;

        ////Testing if credit card has more balance than price of product
        String SQL_statement = "SELECT balance FROM BANK_ACCOUNT WHERE routing_ID = ? AND bank_name = ? AND customer_ID = ?";
        try{
            PreparedStatement SQLcc = conn.prepareStatement(SQL_statement);
            SQLcc.setString(1, routingID);
            SQLcc.setString(2, bankName);
            SQLcc.setInt(3, thisUser.getID());

            ResultSet countSQL = SQLcc.executeQuery();

            if(countSQL.next()){
                double balance = countSQL.getDouble(1);
                if(balance == 0){
                    System.out.println("This Bank Account has no money");
                    return false;
                }
                if(totalCost <= balance){
                    return true;
                }
            }
            else{
                System.out.println("Error with verifying product");
                return false;
            }
        }catch (SQLException sql){
            System.out.println(sql);
            return false;
        }  

        //If totalCost > balance
        System.out.println("Total Cost is greater than the Balance in this account");
        return false;
    }

    ////----DB Purchase----////
    public int createPurchase(){
        int productID = (int) (purchaseCache.get(0));
        double price  = (double) purchaseCache.get(2);
        int quantity     = (int) purchaseCache.get(3);
        int transactionID = -1;

        String SQL_statement = "INSERT INTO purchase (Price, Amount, Customer_ID, Product_ID) VALUES (?, ?, ?, ?)";

        try{
            PreparedStatement SQLpurchase = conn.prepareStatement(SQL_statement, Statement.RETURN_GENERATED_KEYS);

            SQLpurchase.setDouble(1, price);                                   
            SQLpurchase.setInt(2, quantity);        
            SQLpurchase.setInt(3, thisUser.getID());        
            SQLpurchase.setInt(4, productID);                        

            int rows = SQLpurchase.executeUpdate();

            ResultSet id = SQLpurchase.getGeneratedKeys();

            if(id.next()){
                transactionID = id.getInt(1);
            }
            System.out.println("==================");
            System.out.println("Purchase Complete!\n");
            System.out.println("==================");

        }catch (SQLException sql){
            System.out.println(sql);
            System.out.println("Error - Purchase was not made");
            back(4);
        } 
        return transactionID;
    }

    public void dbCC_Purchase(String cardNum){
        int transactionID = createPurchase();

        if(transactionID < 0){
            System.out.println("Error - Purchase was not made. Check createPurchase");
            return;
        }

        String SQL_statement = "INSERT INTO credit_card_purchase(Transaction_ID,Fee_Amount,Card_Num) VALUES (?,0,?)";

        try{
            PreparedStatement SQLpurchase = conn.prepareStatement(SQL_statement);

            SQLpurchase.setInt(1, transactionID);                                   
            SQLpurchase.setString(2, cardNum);                               

            int rows = SQLpurchase.executeUpdate();

        }catch (SQLException sql){
            System.out.println(sql);
            //System.out.println("Error - Purchase was not made");
        } 
    }

    public void dbInstallment_Purchase(String plan){
        int transactionID = createPurchase();

        if(transactionID < 0){
            System.out.println("Error - Purchase was not made. Check createPurchase");
            return;
        }

        String SQL_statement = "INSERT INTO installment_purchase (Transaction_ID, Installment_ID, Plan) " 
                             + "SELECT ?, installment_ID, UPPER(?) FROM installment WHERE Customer_ID = ?";

        try{
            PreparedStatement SQLpurchase = conn.prepareStatement(SQL_statement);

            SQLpurchase.setInt(1, transactionID);                                   
            SQLpurchase.setString(2, plan);             
            SQLpurchase.setInt(3, thisUser.getID());        

            int rows = SQLpurchase.executeUpdate();

        }catch (SQLException sql){
            System.out.println(sql);
            //System.out.println("Error - Purchase was not made");
        } 
    }

    public void dbBankAcc_Purchase(String routingID, String bankName){
        int transactionID = createPurchase();

        if(transactionID < 0){
            System.out.println("Error - Purchase was not made. Check createPurchase");
            return;
        }

        String SQL_statement = "INSERT INTO bank_purchase (Transaction_ID, Account_ID) " 
                             + "SELECT ?, Account_ID FROM bank_account WHERE Customer_ID = ? AND routing_ID = ? AND bank_name = ?";

        try{
            PreparedStatement SQLpurchase = conn.prepareStatement(SQL_statement);

            SQLpurchase.setInt(1, transactionID);                                                
            SQLpurchase.setInt(2, thisUser.getID());    
            SQLpurchase.setString(3, routingID); 
            SQLpurchase.setString(4, bankName);     

            int rows = SQLpurchase.executeUpdate();

        }catch (SQLException sql){
            System.out.println(sql);
            //System.out.println("Error - Purchase was not made");
        } 
    }
}