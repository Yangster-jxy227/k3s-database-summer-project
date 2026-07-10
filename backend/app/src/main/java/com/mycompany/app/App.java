//Java
//----import java.io.Console;----//
import java.util.Scanner;
//import java.util.*;
//main 

public class App{
    //static final String DB_url = "jdbc:oracle:thin:@//rocordb01.cse.lehigh.edu:1522/cse241pdb";

    public static void main(String[] args){
        //Try Catch for console failure
        //----Console console = System.console();----//
        Scanner scan = new Scanner(System.in);

        DBController db = new DBController();

        User thisUser;
        int userID;

        //Connecting to Database - need to build loop to allow retries
        try{
            db.connect(scan);

        } catch(Exception e){
            System.out.println("Error - Closing Database Connection");
            db.disconnect();
        } /*finally{
            if(db.connected == true){
                db.disconnect();
            }
        }*/

        do{
            //Prompting user to login LUShop - loops within function, returns the userID
            thisUser = loginPrompt(db, scan);

            DBcatalog db_cat = new DBcatalog(thisUser, scan, db);

            //After loginPromt verifies user exists, collects rest of the data from the user

            //Calls next UI
            if(thisUser.getType().equals("manager")){
                UImanager m_ui = new UImanager(thisUser, scan, db, db_cat);
                db_cat.fetchUImanager(m_ui);
                m_ui.mainScreen();
            }else{
                UIcustomer c_ui = new UIcustomer(thisUser, scan, db, db_cat);
                db_cat.fetchUIcustomer(c_ui);
                c_ui.mainScreen();
            }
        } while(db.connected == true);
        
        db.disconnect();
    }

    ////----Handles App Logins----////
    //No seperate entity table for manager or user - loop to allow retries
    public static User loginPrompt(DBController db, Scanner scan){
        User thisUser = new User();
        int input;          //tracks user input
        String userType;    //tracks if user is customer, manager, or business
        int id;             //tracks and returns the id of the user once they input correct email and password

        System.out.println("\nWelcome to LUShop!");
        System.out.println("Please Login Below:\n");

        //Prompts user to choose if they are a individual or business
        while(true){
            System.out.println("Please indicate below if you are a individual customer or business\n"
                            + "[1] Individual\n"
                            + "[2] Business\n"
                            + "[3] Exit\n");

            //If user inputs a non-int, provides error and allows retry
            while(!scan.hasNextInt()){
                System.out.println("Invalid Input. Please input 1 or 2 to indicate whether you are an individual or business");
                scan.next();
                System.out.println("Please indicate below if you are a individual customer or business\n"
                                + "[1] Individual\n"
                                + "[2] Business\n"
                                + "[3] Exit\n");
            }

            input = scan.nextInt();
            scan.nextLine();

            //[1] is individual - displays all individuals and returns if user is 

            switch(input){
                case 1:
                    System.out.println("Individual Selected\n");
                    db.displayAllIndividuals();
                    id = determineUID(scan, db, true, thisUser);
                    //userType = db.fetchUserRole(db.conn, email, decodedPW,);
                    if(id != 0 && id != -1){
                        thisUser.setID(id);
                        //thisUser.setType(userType);
                        return thisUser;
                    }
                    else{ 
                        System.out.println("Incorrect Email or Username\n");
                        break; 
                    }
                    //break;
                case 2:
                    System.out.println("Business Selected\n");
                    db.displayAllBusinesses();
                    id = determineUID(scan, db, false, thisUser);  
                    userType = "Business";
                    if(id != 0 && id != -1){
                        System.out.println("User is a business\n");
                        thisUser.setID(id);
                        thisUser.setType(userType);
                        return thisUser;
                    }
                    else{ 
                        System.out.println("No User Found");
                        System.out.println("Incorrect Email or Username\n");
                        break; 
                    }
                    //break;
                case 3:
                    masterExit(db);
                    break;
            }

            System.out.println("Wrong integer. Please input either 1 or 2 to indicated whether you are an individual or business\n");
            //continue;
        }
    }

    public static int determineUID(Scanner scan, DBController db, boolean individual, User thisUser){
        System.out.println("Enter Email: ");
        String email = scan.nextLine();

        System.out.println("Enter Password: ");
        String decodedPW = scan.nextLine();

        ///////////////////////////////////////////////////////////////////////
        //----String email = console.readLine("Enter Email: ");----////////////
        //----char[] password = console.readPassword("Enter Password: ");----//
        //----String decodedPW = String.valueOf(password);----/////////////////
        ///////////////////////////////////////////////////////////////////////

        if(individual==true){
            thisUser.setType(db.fetchUserRole(db.conn, email, decodedPW));
        }
        else{
            thisUser.setType("business");
        }

        return db.fetchUID(db.conn, email, decodedPW, individual);
    }

    ////----Handles Program and UI changes----////
    public static void switchToCatalog(DBcatalog db_cat){
        db_cat.catalogScreen();
    }

    public static void masterExit(DBController db){
        System.out.println("Exiting...");
        db.disconnect();
        System.out.println("Successful Exit");
        System.exit(0);
    }
}
