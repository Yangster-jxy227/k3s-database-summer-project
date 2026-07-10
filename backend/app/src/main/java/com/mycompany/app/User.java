public class User {
    int userID;
    String userType;
    String name;

    User(){}

    User(int userID, String userType){
        this.userID = userID;
        this.userType = userType;
    }

    int getID(){
        return this.userID;
    }

    String getType(){
        return this.userType;
    }

    void setID(int newUserID){
        this.userID = newUserID;
    }

    void setType(String newUserType){
        this.userType = newUserType;
    }
}