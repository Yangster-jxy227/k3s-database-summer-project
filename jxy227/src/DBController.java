//Connects to Oracle Database
import java.sql.*; 
import java.util.*;
import java.io.*;
import java.util.Random;
import java.sql.Timestamp;

public class DBController{

    ////////////////////////////////////////////
    ////----Refactoring url for postgres----////
    ////////////////////////////////////////////
    static final String DB_url = "jdbc:postgresql://localhost:5432/k3s_app_test"; //Dell Laptop
    //static final String DB_url = "jdbc:postgresql://localhost:5432/cse241pdb"; //Lenovo Laptop - trouble signing in to DB
    //static final String DB_url = "jdbc:oracle:thin:@//rocordb01.cse.lehigh.edu:1522/cse241pdb";

    Connection conn;
    String DB_username;
    String DB_password;
    boolean connected;

    //Constructor
    public DBController(){
        this.conn = null;
        this.DB_username = null;
        this.DB_password = null;
        this.connected = false;
    }

    /**
     * Connects to the Database
     */
    public void connect(Scanner scan){
        //Scanner in = new Scanner(System.in);
        
        while(connected == false){
            try{
                //Opens Console to provide secure input
                //Console console = System.console();
                if (scan == null) {
                    System.err.println("No scanner available");
                    System.exit(1);
                }

                //Prompts User for Environment Variables to connect to Database
                //----DB_username = console.readLine("Enter Postgres user id: ");----//
                //----DB_password = console.readPassword("Enter your Postgres password: ");----//

                System.out.println("Enter Postgres user id: ");
                DB_username = scan.nextLine();

                System.out.println("Enter your Postgres password: ");
                DB_password = scan.nextLine();


                //Opens Connection to Lehigh Database
                conn = DriverManager.getConnection(DB_url, DB_username, String.valueOf(DB_password));
                if(conn != null){
                    System.out.println("Connected to Database");
                }
                else{
                    System.out.println("Did not connect to Database");
                }

                this.connected = true;

            }catch (Exception se) {
                se.printStackTrace();
                System.out.println("[Error]: Connect error. Re-enter login data:");
            }/*finally {
                try{
                    conn.close();
                } catch (Exception exe){
                    System.out.println("Connection already closed");
                }
            }*/
        }
    }

    public void disconnect(){
        try{
            if(conn == null){
                //System.err.println("No console available");
                throw new ClassNotFoundException("Connnection Class never created");
            }
            //Closes Connection
            conn.close();
            System.out.println("Disconnection Successful");
            connected = false;
        }  
          catch(ClassNotFoundException exe){
            System.out.println(exe.toString());
        }catch(Exception exe){
            System.out.println("Connection already closed");
        }
    }

    public Connection getConnection(){
        if(conn != null){
            return conn;
        }
        else{
            System.out.println("Error: Connection is Null");
            return null;
        }
    }

    ////-----HANDLES INITIAL LOGIN-----////
    /** 
     * userType = 1 - individual
     * userType = 2 - business
     * userType = 3 - 
    */
    public void displayAllIndividuals(){
        String SQL_statement = "SELECT first_name, last_name, email, password FROM Customer natural join individual";

        try{
            PreparedStatement SQLRole = conn.prepareStatement(SQL_statement);

            ResultSet foundRole = SQLRole.executeQuery();

            System.out.printf("%-15s | %-20s | %-15s\n", "Name", "Email", "Password");
            System.out.printf("-------------------------------------------------------\n");

            if(!foundRole.next()){
                System.out.println("No Users Found");
                return;
            }

            do{
                String name = foundRole.getString(1) + " " + foundRole.getString(2);
                String email = foundRole.getString(3);
                String password = foundRole.getString(4);
                System.out.printf("%-15s | %-20s | %-15s\n", name, email, password);
            } while(foundRole.next());
            System.out.printf("-------------------------------------------------------\n");

            System.out.println("\nFor Managers, test the first person - John Doe");
            System.out.println("For regular Customers, test the last person - Ethan Price\n");

        }catch (SQLException sql){
            System.out.println(sql);
        }
    }

    public void displayAllBusinesses(){
        String SQL_statement = "SELECT company_name, email, password FROM Customer natural join Business";

        try{
            PreparedStatement SQLRole = conn.prepareStatement(SQL_statement);

            ResultSet foundRole = SQLRole.executeQuery();

            System.out.printf("%-20s | %-20s | %-15s\n", "Company", "Email", "Password");
            System.out.printf("-------------------------------------------------------\n");
            
            if(!foundRole.next()){
                System.out.println("No Users Found");
                return;
            }

            do{
                String name = foundRole.getString(1);
                String email = foundRole.getString(2);
                String password = foundRole.getString(3);
                System.out.printf("%-20s | %-20s | %-15s\n", name, email, password);
            } while(foundRole.next());
            System.out.println("-------------------------------------------------------\n");
            System.out.println("For Businesses, test the first company - Acme Inc. \n");

        }catch (SQLException sql){
            System.out.println(sql);
        }
    }
    
    //Needs to handle 
    //Finds role of user - whether if they are a manager or customer
    public String fetchUserRole(Connection conn, String email, String password){
        String userRole = null;
        String SQL_statement = "SELECT role FROM Customer natural join Individual WHERE email = ? AND password = ?";

        try{
            PreparedStatement SQLRole = conn.prepareStatement(SQL_statement);
            SQLRole.setString(1, email);
            SQLRole.setString(2, password);

            ResultSet foundRole = SQLRole.executeQuery();

            if(foundRole.next()){
                userRole = foundRole.getString(1);
                System.out.println("\nUser is a " + userRole);
            }
            else{
                System.out.println("No User Found");
            }

        }catch (SQLException sql){
            System.out.println(sql);
        }
        return userRole;
    }

    //
    public int fetchUID(Connection conn, String email, String password, boolean individual){
        int userID = 0;
        String SQL_statement;

        if(individual == true){
            SQL_statement = "SELECT ID FROM Customer natural join Individual WHERE email = ? AND password = ?";
        }
        else{
            SQL_statement = "SELECT ID FROM Customer natural join Business WHERE email = ? AND password = ?";
        }

        try{
            PreparedStatement SQLRole = conn.prepareStatement(SQL_statement);
            //SQLRole.setString(1, table);
            SQLRole.setString(1, email);
            SQLRole.setString(2, password);

            ResultSet foundID = SQLRole.executeQuery();

            if(foundID.next()){
                userID = foundID.getInt(1);
                System.out.println("User ID is: " + userID );
            }
            else{
                //System.out.println("No User ID Found");
                return 0;
            }
            return userID;
        }catch (SQLException sql){
            System.out.println(sql);
            return -1;
        }  
    }

    public void fetchUserInfo(Connection conn, User thisUser){
        String SQL_statement;

        if(thisUser.getType() == "business"){
            SQL_statement = "SELECT * FROM Customer natural join Business WHERE ID = ?" ;
        }
        else{
            SQL_statement = "SELECT * FROM Customer natural join Individual WHERE ID = ?";
        }

        try{
            PreparedStatement SQLRole = conn.prepareStatement(SQL_statement);
            SQLRole.setInt(1, thisUser.getID());

            ResultSet foundID = SQLRole.executeQuery();

            if(foundID.next()){
                System.out.println(foundID.getInt(1));
            }
            else{
                //System.out.println("No User ID Found");
                return;
            }
            //return userID;
        }catch (SQLException sql){
            System.out.println(sql);
            return;
        }  
    }

    ////----Purchase History----////
    public void fetchUserPurchases(User thisUser){
        String SQL_statement = "SELECT p.transaction_ID, g.name, g.vendor, g.price, p.amount FROM Purchase p JOIN Goods g ON p.product_ID = g.product_ID WHERE p.customer_ID = ? ORDER BY p.purchase_date";

        try{
            PreparedStatement SQLRole = conn.prepareStatement(SQL_statement);
            SQLRole.setInt(1, thisUser.getID());

            ResultSet foundRole = SQLRole.executeQuery();

            if(!foundRole.next()){
                System.out.println("No Transactions Found");
                return;
            }

            do{
                System.out.println("========================================");
                String id = foundRole.getString(1);
                String name = foundRole.getString(2);
                String vendor = foundRole.getString(3);
                String charge = foundRole.getString(4);
                //Timestamp purDate = foundRole.getTimestamp(5);
                System.out.println("Transaction ID: " + id);
                System.out.println("Product: " + name);
                System.out.println("Vendor: " + vendor);
                System.out.println("Charge: " + charge);
                //System.out.println("Purchase Date: " + purDate.toString());
            } while(foundRole.next());
            System.out.println("========================================");

        }catch (SQLException sql){
            System.out.println(sql);
        }
    }

    ////----Creating new Product----////
    public int createProduct(String price, String name, String vendor){
        int productID = -1;
        String SQL_statement = "INSERT INTO goods (price, name, vendor) VALUES (?,?,?)";
        
        try{
            PreparedStatement SQLba = conn.prepareStatement(SQL_statement, Statement.RETURN_GENERATED_KEYS);
            double priceDouble = Double.parseDouble(price);
            SQLba.setDouble(1, priceDouble);
            SQLba.setString(2, name);
            SQLba.setString(3, vendor);

            int rows = SQLba.executeUpdate();
            ResultSet id = SQLba.getGeneratedKeys();

            if(id.next()){
                productID = id.getInt(1);
            }
            System.out.println("New Product Created");

        }catch (SQLException sql){
            System.out.println(sql);
            disconnect();
        }  
        return productID;
    }

    public void createItem(String price, String name, String vendor, String stock){
        int productID = createProduct(price, name, vendor);

        String SQL_statement = "INSERT INTO item(product_ID,stock) VALUES (?,?)";

        try{
            PreparedStatement SQLba = conn.prepareStatement(SQL_statement);
            SQLba.setInt(1, productID);
            SQLba.setString(2, stock);

            int rows = SQLba.executeUpdate();

            System.out.println("New Item Created");

        }catch (SQLException sql){
            System.out.println(sql);
            disconnect();
        }     
    }

    public void createService(String price, String name, String vendor, String duration){
        int productID = createProduct(price, name, vendor);

        String SQL_statement = "INSERT INTO service(Product_ID,Duration_Days) VALUES (?,?)";

        try{
            PreparedStatement SQLba = conn.prepareStatement(SQL_statement);
            SQLba.setInt(1, productID);
            SQLba.setString(2, duration);

            int rows = SQLba.executeUpdate();

            System.out.println("New Service Created");

        }catch (SQLException sql){
            System.out.println(sql);
            disconnect();
        }     
    }

    ////----Credit Cards----////
    public void fetchUserCC(Connection conn, User thisUser){
        String SQL_statement = "SELECT Card_num, security_code, balance, interest, Network FROM CREDIT_CARD WHERE customer_ID = ?";

        try{
            PreparedStatement SQLcc = conn.prepareStatement(SQL_statement);
            SQLcc.setInt(1, thisUser.getID());

            ResultSet foundCC = SQLcc.executeQuery();

            if(!foundCC.next()){
                System.out.println("You have no credit cards tied to this account\n");
                return;
            }

            do{
                System.out.println("========================================");
                System.out.println("Card Number: " + foundCC.getLong(1));
                System.out.println("Security Code: " + foundCC.getInt(2));
                System.out.println("Balance: " + foundCC.getDouble(3));
                System.out.println("Interest: " + foundCC.getString(4));
                System.out.println("Network : " + foundCC.getString(5));
            } while(foundCC.next());
            System.out.println("========================================");

        }catch (SQLException sql){
            System.out.println(sql);
            return;
        }  
    }

    public void creatingCC(String cnum, int scode, String net, User thisUser){
        String SQL_statement = "INSERT INTO credit_card(Card_Num,Security_Code,Network,Interest,Balance,Credit_Score,Customer_ID) VALUES (?,?,?,?,1000,500,?)";

        Random r = new Random();

        int randInt = r.nextInt(25) + 1;
        String interest = randInt + "%";

        try{
            PreparedStatement SQLcc = conn.prepareStatement(SQL_statement);
            SQLcc.setString(1, cnum);
            SQLcc.setInt(2, scode);
            SQLcc.setString(3, net);
            SQLcc.setString(4, interest);
            SQLcc.setInt(5, thisUser.getID());

            int rows = SQLcc.executeUpdate();

        }catch (SQLException sql){
            System.out.println(sql);
            disconnect();
        }  
    }

    ////----Installment----////
    public void fetchUserInstallment(Connection conn, User thisUser){
        String SQL_statement = "SELECT plan, interest, duration_days FROM installment natural join installment_plan WHERE customer_ID = ?";

        try{
            PreparedStatement SQLin = conn.prepareStatement(SQL_statement);
            SQLin.setInt(1, thisUser.getID());

            ResultSet foundIn = SQLin.executeQuery();

            if(!foundIn.next()){
                System.out.println("You have no installment plans tied to this account\n");
                return;
            }

            do{
                System.out.println("========================================");
                System.out.println("Plan: " + foundIn.getString(1));
                System.out.println("Interest: " + foundIn.getString(2));
                System.out.println("Duration " + foundIn.getInt(3) + " Days");

            } while(foundIn.next());
            System.out.println("========================================");

        }catch (SQLException sql){
            System.out.println(sql);
            return;
        }  
    }

    public void fetchInstallments(){
        String SQL_statement = "SELECT * FROM installment_plan";

        try{
            PreparedStatement SQLplan = conn.prepareStatement(SQL_statement);
            ResultSet countSQL = SQLplan.executeQuery();

            if(!countSQL.next()){
                System.out.println("No Installment Plans Avaliable\n");
                return;
            }

            
            System.out.println("Current Plans to Subscribe to are: ");
            System.out.printf("%-10s | %-10s | %-10s\n", "Plan", "Interest", "Duration in Days");
            System.out.printf("------------------------------------------\n");
            do{
                //System.out.println("========================================");
                System.out.printf("%-10s | %-10s | %-10s\n", countSQL.getString(1), countSQL.getString(2), countSQL.getInt(3) + " Days");
            } while(countSQL.next());
            //System.out.println("========================================");


        } catch (SQLException sql){
            System.out.println(sql);
        }  
    }

    //true if plan exists
    public boolean verifyPlanExists(String planInput){
        String SQL_statement = "SELECT COUNT(*) FROM installment_plan WHERE UPPER(plan) = UPPER(?)";

        try{
            PreparedStatement SQLplan = conn.prepareStatement(SQL_statement);
            SQLplan.setString(1, planInput);

            ResultSet countSQL = SQLplan.executeQuery();

            if(countSQL.next()){
                int count = countSQL.getInt(1);
                if(count <= 0){
                    return false;
                }
                return true;
            }
            else{
                System.out.println("Error with verifying plan");
                return false;
            }

        }catch (SQLException sql){
            System.out.println(sql);
            return false;
        }  
    }

    //true if user already has a installment plan that they tried to input
    public boolean verifyNewPlan(String planInput, User thisUser){
        String SQL_statement = "SELECT COUNT(*) FROM installment WHERE customer_ID = ? and UPPER(plan) = UPPER(?)";

        try{
            PreparedStatement SQLplan = conn.prepareStatement(SQL_statement);
            SQLplan.setInt(1, thisUser.getID());
            SQLplan.setString(2, planInput);

            ResultSet countSQL = SQLplan.executeQuery();

            if(countSQL.next()){
                int count = countSQL.getInt(1);
                if(count <= 0){
                    return false;
                }
                return true;
            }
            else{
                System.out.println("Error with verifying plan");
                return false;
            }

        }catch (SQLException sql){
            System.out.println(sql);
            return false;
        }  
    }

    public void creatingIM(String planInput, User thisUser){
        String SQL_statement = "INSERT INTO installment(Customer_ID,Plan) VALUES (?,UPPER(?))";

        try{
            PreparedStatement SQLcc = conn.prepareStatement(SQL_statement);
            SQLcc.setInt(1, thisUser.getID());
            SQLcc.setString(2, planInput);

            int rows = SQLcc.executeUpdate();
            System.out.println("New payment accepted");

        }catch (SQLException sql){
            System.out.println(sql);
            System.out.println("Error: New payment not made due to SQL error");
            //disconnect();
        }  
    }

    ////----Bank Account----////
    public void fetchUserBankAccount(Connection conn, User thisUser){
        String SQL_statement = "SELECT Bank_name, balance, routing_ID FROM Bank_account WHERE customer_ID = ?";

        try{
            PreparedStatement SQLin = conn.prepareStatement(SQL_statement);
            SQLin.setInt(1, thisUser.getID());

            ResultSet foundIn = SQLin.executeQuery();

            if(!foundIn.next()){
                System.out.println("You have no bank account tied to this account\n");
                return;
            }

            do{
                System.out.println("========================================");
                System.out.println("Bank: " + foundIn.getString(1));
                System.out.println("Balance: " + foundIn.getDouble(2));
                System.out.println("Routing ID " + foundIn.getInt(3));

            } while(foundIn.next());
            System.out.println("========================================");

        }catch (SQLException sql){
            System.out.println(sql);
            return;
        }  
    }

    public void creatingBA(int ID, String bankName, User thisUser){
        String SQL_statement = "INSERT INTO bank_account(Routing_ID,Bank_Name,Balance,Customer_ID) VALUES (?,?,5000,?)";

        try{
            PreparedStatement SQLba = conn.prepareStatement(SQL_statement);
            SQLba.setInt(1, ID);
            SQLba.setString(2, bankName);
            SQLba.setInt(3, thisUser.getID());

            int rows = SQLba.executeUpdate();

        }catch (SQLException sql){
            System.out.println(sql);
            disconnect();
        }  
    }

    ////----MISC----////
    public void fetchProductInfo(List<Object> purchaseCache, int product_ID, int goodType, int quantity){
        //Good is an item
        if(goodType == 1){
            String SQL_statement = "SELECT name, price, stock FROM goods natural join item WHERE product_ID = ?";
            try{
                PreparedStatement SQLproduct = conn.prepareStatement(SQL_statement);
                SQLproduct.setInt(1, product_ID);

                ResultSet countSQL = SQLproduct.executeQuery();

                if(countSQL.next()){
                    purchaseCache.add(countSQL.getString("name"));
                    System.out.println("Product: " + purchaseCache.get(1));
                    purchaseCache.add(countSQL.getDouble("price"));
                    System.out.println("Price: " + purchaseCache.get(2));
                    purchaseCache.add(quantity);
                    System.out.println("Quantity Buying: " + purchaseCache.get(3));
                    purchaseCache.add(countSQL.getInt("stock"));
                    System.out.println("Stock: " + purchaseCache.get(4) + "\n");
                } else{
                    System.out.println("No matching Good with Product ID: " + product_ID);
                }

            } catch (SQLException sql){
                System.out.println(sql);
                return;
            } 
        }
        //Good is a service
        else{
            String SQL_statement = "SELECT name, price FROM goods natural join service WHERE product_ID = ?";
            try{
                PreparedStatement SQLproduct = conn.prepareStatement(SQL_statement);
                SQLproduct.setInt(1, product_ID);

                ResultSet countSQL = SQLproduct.executeQuery();

                if(countSQL.next()){
                    purchaseCache.add(countSQL.getString("name"));
                    System.out.println("Product: " + purchaseCache.get(1));
                    purchaseCache.add(countSQL.getDouble("price"));
                    System.out.println("Price: " + purchaseCache.get(2) +"\n");
                    purchaseCache.add(quantity);
                } else{
                    System.out.println("No matching Good with Product ID: " + product_ID);
                }
            } catch (SQLException sql){
                System.out.println(sql);
                return;
            }  
        }

    }

    public void fetchReportOne(){
        String SQL_statement = "SELECT p.customer_ID, i.first_name, i.last_name, ROUND(avg(p.price::DECIMAL), 2) "
                             + "FROM purchase p join individual i on customer_ID = ID "
                             + "GROUP BY i.first_name, i.last_name, p.customer_ID "
                             + "ORDER by customer_ID";

        try{
            PreparedStatement SQLin = conn.prepareStatement(SQL_statement);

            ResultSet foundIn = SQLin.executeQuery();

            if(!foundIn.next()){
                System.out.println("No Customer has made a purchase yet");
            }

            System.out.println("Average Charge for each Customer per Purchase:");
            
            do{
                String name = foundIn.getString(2) + " " + foundIn.getString(3);
                System.out.println("============================================");
                System.out.println("Customer ID: " + foundIn.getString(1));
                System.out.println("Name: " + name);
                System.out.println("Average Charge per Purchase: " + foundIn.getString(4));
            } while(foundIn.next());

            System.out.println("============================================");

        }catch (SQLException sql){
            System.out.println(sql);
            return;
        }  
    }

    public void fetchReportTwo(){
        String SQL_statement = "SELECT g.product_ID, g.name, g.vendor, count(*) "
                             + "FROM purchase p JOIN goods g ON p.product_ID = g.product_ID "
                             + "GROUP BY g.name, g.vendor, g.product_ID "
                             + "ORDER by g.product_ID";

        try{
            PreparedStatement SQLin = conn.prepareStatement(SQL_statement);

            ResultSet foundIn = SQLin.executeQuery();

            if(!foundIn.next()){
                System.out.println("No Items Here");
            }

            System.out.println("Total Number of Purchases per Product");

            do{
                System.out.println("============================================");
                System.out.println("Product ID: " + foundIn.getInt(1));
                System.out.println("Product Name: " + foundIn.getString(2));
                System.out.println("Vendor: " + foundIn.getString(3));
                System.out.println("Total Purchases - All Time: " + foundIn.getInt(4));
            } while(foundIn.next());

            System.out.println("============================================");

        }catch (SQLException sql){
            System.out.println(sql);
            return;
        }  
    }

    public void fetchReportThree(){
        String SQL_statement = "SELECT plan, count(*) FROM customer join installment on customer_ID = ID natural join installment_plan "
                             + "GROUP BY plan ORDER by plan";

        try{
            PreparedStatement SQLin = conn.prepareStatement(SQL_statement);

            ResultSet foundIn = SQLin.executeQuery();

            if(!foundIn.next()){
                System.out.println("No Plans");
            }

            System.out.println("Total Number of Subscription per Installment Plan");

            do{
                System.out.println("============================================");
                System.out.println("Plan: " + foundIn.getString(1));
                System.out.println("Subscriptions: " + foundIn.getInt(2));
            } while(foundIn.next());

            System.out.println("============================================");

        }catch (SQLException sql){
            System.out.println(sql);
            return;
        }  
    }
}
