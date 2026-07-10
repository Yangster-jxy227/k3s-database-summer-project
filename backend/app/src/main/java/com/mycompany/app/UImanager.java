//Interface for manager
//import java.io.Console;
import java.util.Scanner;

public class UImanager extends UIcustomer{
    
    UImanager(User fetchedUser, Scanner appScan, DBController db, DBcatalog db_cat){
        super(fetchedUser, appScan, db, db_cat);
    }

    @Override
    public void back(int menu){
        switch(menu){
            case 1: //Return to main screen
                mainScreen();        
                break;
            case 2: //New Payment Screen
                financeScreen();
                break;
            case 3: //Inputting new payment process
                createPayment();
                break;
            case 4: //After a report is made, go back to reportScreen
                callReport();
                break;
        }
    }

    @Override
    public void selectionPrompt(int prompt){
        switch(prompt){
            case 1: //main screen
                System.out.println("Select what you would like to do:\n"
                                    + "[1] Go to Catalog\n"
                                    + "[2] Check User Finance\n"
                                    + "[3] Purchase History\n"
                                    + "[4] Add Products to Catalog\n"
                                    + "[5] Generate Aggregate Reports\n"
                                    + "[6] Exit\n");
                break;
            case 2: //finance screen
                System.out.println("Choose what to do with your finance's below:\n"
                                + "[1] See Credit Cards\n"
                                + "[2] See Installment Plan\n"
                                + "[3] See Bank Accounts\n"
                                + "[4] Make New Payment Method\n"
                                + "[5] Back\n"
                                + "[6] Exit\n");
                break;
            case 3: //new payment screen
                System.out.println("Select the Payment method to create:\n"
                                + "[1] Credit Card\n"
                                + "[2] Installment\n"
                                + "[3] Bank Account\n"
                                + "[4] Back\n"
                                + "[5] Exit\n");
                break;
            case 4:
                System.out.println("Select the Report to see:\n"
                                + "[1] Average charge per purchase for every customer\n"
                                + "[2] Total Number of Purchases per Product\n"
                                + "[3] Total Number of Subscription per Installment Plan\n"
                                + "[4] Back\n"
                                + "[5] Exit\n");
                break;
        }
    }

    @Override
    public void mainScreen(){
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
                App.switchToCatalog(db_cat);
                break;
            case 2:
                financeScreen();
                break;
            case 3:
                db.fetchUserPurchases(thisUser);
                back(1);
                break;
            case 4:
                addProduct();
                back(1);
                break;
            case 5:
                callReport();
                break;
            case 6:
                App.masterExit(db);
                break;
        }
    }

    public void addProduct(){
        boolean backStatus = false;
        String price;
        String name;
        String vendor;
        int input;


        //Price
        while(true){
            System.out.println("At any point, restart by typing 'restart', cancel by typing 'back' to go to the previous screen, or 'exit' to disconnect from program");
            System.out.println("Enter Price of new Product: ");

            price = scan.nextLine().trim();

            if(price.equalsIgnoreCase("restart")){
                addProduct();
            }

            if(price.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(price.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }   
            if(!price.matches("^\\d{0,8}(\\.\\d{0,2})?$")){
                System.out.println("Invalid input - must be a number and not larger than 99999999.99");
                continue;
            }
            break;
        } 

        if(backStatus == true){
            back(1);
        }

        //Name
        while(true){
            System.out.println("At any point, restart by typing 'restart', cancel by typing 'back' to go to the previous screen, or 'exit' to disconnect from program");
            System.out.println("Enter Name of new Product: ");

            name = scan.nextLine().trim();

            if(name.equalsIgnoreCase("restart")){
                addProduct();
            }

            if(name.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(name.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }
            break;
        } 

        if(backStatus == true){
            back(1);
        }

        //Vendor
        while(true){
            System.out.println("At any point, restart by typing 'restart', cancel by typing 'back' to go to the previous screen, or 'exit' to disconnect from program");
            System.out.println("Enter Vendor of new Product: ");

            vendor = scan.nextLine().trim();

            if(vendor.equalsIgnoreCase("restart")){
                addProduct();
            }

            if(vendor.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(vendor.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }
            break;
        } 

        if(backStatus == true){
            back(1);
        }

        do {
            System.out.println("Is this Product a Item or a Service?\n"
                             + "[1] Item\n"
                             + "[2] Service\n"
                             + "[3] Restart\n"
                             + "[4] Cancel\n");


            while (!scan.hasNextInt()) {
                System.out.println("Invalid input - please enter a number\n");
                scan.next();
                System.out.println("");
            }

            input = scan.nextInt();
            scan.nextLine();

            if(input < 1 || input > 4){
                System.out.println("Input Integer between 1-4\n");
            }

        } while(input < 1 || input > 4);

        switch(input){
            case 1: //Item
                //db.createProduct(price, name, vendor);
                String stock = insertStock();
                System.out.println(price);
                System.out.println(name);
                System.out.println(vendor);
                System.out.println(stock);
                db.createItem(price, name, vendor, stock);
                back(1);
                break;
            case 2: //Service
                String duration = insertDuration();
                db.createService(price, name, vendor, duration);
                back(1);
                break;
            case 3:
                addProduct();
                break;
            case 4:
                back(1);
                break;

        }
    }

    public String insertStock(){
        String input;
        boolean backStatus = false;
        //Stock
        while(true){
            System.out.println("At any point, cancel by typing 'back' to restart creating a product or 'exit' to disconnect from program");
            System.out.println("Enter Stock of Item: ");
            input = scan.nextLine().trim();

            if(input.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(input.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }

            if(!input.matches("^\\d+$")){
                System.out.println("Invalid input - Must be an Integer");
                continue;
            }

            break;
        } 

        if(backStatus == true){
            addProduct();
        }
        return input;
    }

    public String insertDuration(){
        String input;
        boolean backStatus = false;
        //Stock
        while(true){
            System.out.println("At any point, cancel by typing 'back' to restart creating a product or 'exit' to disconnect from program");
            System.out.println("Enter Duration (Days) of Service: ");
            input = scan.nextLine().trim();

            if(input.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(input.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }

            if(!input.matches("^\\d+$")){
                System.out.println("Invalid input - Must be an Integer");
                continue;
            }

            break;
        } 

        if(backStatus == true){
            addProduct();
        }
        return input;
    }

    public void callReport(){
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

            if(input < 1 || input > 5){
                System.out.println("Input Integer between 1-5\n");
            }

        } while (input < 1 || input > 5);

        switch(input){
            case 1: 
                db.fetchReportOne();
                back(4);
                break;
            case 2:
                db.fetchReportTwo();
                back(4);
                break;
            case 3:
                db.fetchReportThree();
                back(4);
                break;
            case 4:
                back(1);
                break;
            case 5:
                App.masterExit(db);
                break;
        }
    }
}