//Interface for customer
//import java.io.Console;
import java.util.Scanner;

public class UIcustomer{
    User thisUser; 
    //Console console;
    Scanner scan;
    DBController db;
    DBcatalog db_cat;

    //Constructor
    UIcustomer(User fetchedUser, Scanner appScan, DBController db, DBcatalog db_cat){
        this.thisUser = fetchedUser;
        //this.console = appConsole;
        this.scan = appScan;
        this.db = db;
        this.db_cat = db_cat;
    }

    public void back(int menu){
        switch(menu){
            case 1: //On finance Screen
                mainScreen();        
                break;
            case 2: //New Payment Screen
                financeScreen();
                break;
            case 3: //Inputting new payment process
                createPayment();
                break;
        }
    }

    public void welcome(){
        //Refactor how DBController handles user names and userType 
        //Welcome user and Call to DBController and display userID name 
        System.out.println("\nWelcome [USER]!\n");

        mainScreen();
    }

    public void selectionPrompt(int prompt){
        switch(prompt){
            case 1: //main screen
                System.out.println("Select what you would like to do:\n"
                                    + "[1] Go to Catalog\n"
                                    + "[2] Check User Finance\n"
                                    + "[3] Purchase History\n"
                                    + "[4] Exit\n");
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
        }
    }

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

            if(input < 1 || input > 4){
                System.out.println("Input Integer between 1-4\n");
            }

        } while (input < 1 || input > 4);

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
                App.masterExit(db);
                break;
        }
    }

    public void financeScreen(){
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

            if(input < 1 || input > 6){
                System.out.println("Input Integer between 1-6\n");
            }

        } while (input < 1 || input > 6);

        switch(input){
            case 1:
                displayFinances(1);
                break;
            case 2:
                displayFinances(2);
                break;
            case 3:
                displayFinances(3);
                break;
            case 4:
                createPayment();
                //App.masterExit(db);
                break;
            case 5:
                back(1);
                //App.masterExit(db);
                break;
            case 6:
                App.masterExit(db);
                break;
        }

        financeScreen();
    }

    public void displayFinances(int type){
        switch(type){
            case 1: //credit cards
                db.fetchUserCC(db.conn, thisUser);
                break;
            case 2: //installment
                db.fetchUserInstallment(db.conn, thisUser);
                break;
            case 3: //bank account
                db.fetchUserBankAccount(db.conn, thisUser);
                break;
        }
    }

    public void createPayment(){
        int input;

        do {    
            selectionPrompt(3);

            while (!scan.hasNextInt()) {
                System.out.println("Invalid input - please enter a number\n");
                scan.next();
                selectionPrompt(3);
            }

            input = scan.nextInt();    
            scan.nextLine();    

            if(input < 1 || input > 5){
                System.out.println("Input Integer between 1-5\n");
            }

        } while (input < 1 || input > 5);

        switch(input){
            case 1:
                createCard();
                financeScreen();
                break;
            case 2:
                createInstallment();
                financeScreen();
                break;
            case 3:
                createBankAccount();
                financeScreen();
                break;
            case 4:
                back(2);
                break;
            case 5:
                App.masterExit(db);
                break;
        }
    }

    public void createCard(){
        String numInput;
        int len;
        String codeInput;
        String networkInput;
        boolean backStatus = false;
        
        System.out.println("At any point, cancel by typing 'back' to go to the previous screen or 'exit' to disconnect from program");
        System.out.println("New Credit Card Payment:");

        //Card Number
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
            back(3);
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
                back(3);
            }

        //Network
        while(true) {
            System.out.println("Enter Card Network:");
            networkInput = scan.nextLine().trim();

            if(numInput.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(networkInput.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }

            break; 
        }        

        if(backStatus == true){
            back(3);
        }

        System.out.println("\nCreating new credit card payment...");
        db.creatingCC(numInput, Integer.parseInt(codeInput), networkInput, thisUser);
        System.out.println("New payment accepted\n"
                         + "(Interest is randomly generated between 1%-25%, every new card is given $1000 and a credit score of 500)\n");
    }

    public void createInstallment(){
        String planInput;
        boolean backStatus = false;

        //Displays Installments
        db.fetchInstallments();
        
        System.out.println("At any point, cancel by typing 'back' to go to the previous screen or 'exit' to disconnect from program");
        System.out.println("New Installment Payment:");

        //planInput
        while(true){
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

            //verifies that the user does not have this plan
            if(db.verifyNewPlan(planInput, thisUser)){
                System.out.println("You already have this plan. Choose a different plan");
                continue;
            }
            break;
        } 

        if(backStatus == true){
            back(3);
        }

        System.out.println(planInput);
        System.out.println("Creating new installment payment...");
        db.creatingIM(planInput, thisUser);
        
        return;
    }

    public void createBankAccount(){
        String routingInput;
        String bankName;

        boolean backStatus = false;
        
        System.out.println("At any point, cancel by typing 'back' to go to the previous screen or 'exit' to disconnect from program");
        System.out.println("New Bank Account Payment:");

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
            back(3);
        }

        //Bank Name
        while(true){
            System.out.println("Enter Bank Name: ");
            bankName = scan.nextLine().trim();

            if(bankName.equalsIgnoreCase("back")){
                backStatus = true;
                break;
            }

            if(bankName.equalsIgnoreCase("exit")){
                App.masterExit(db);
            }
            break;
        } 

        if(backStatus == true){
            back(3);
        }

        System.out.println("Creating new credit card payment...");
        db.creatingBA(Integer.parseInt(routingInput), bankName, thisUser);
        System.out.println("New payment accepted\n"
                         + "(Every new account is given $5000)");
        return;
    }

}