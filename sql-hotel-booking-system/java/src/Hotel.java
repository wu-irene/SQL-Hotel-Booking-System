/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

 import java.sql.DriverManager;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.io.File;
 import java.io.FileReader;
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.util.List;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.time.LocalDateTime; // Import so we keep track of when we update the hotel info 
 
 /**
  * This class defines a simple embedded SQL utility class that is designed to
  * work with PostgreSQL JDBC drivers.
  *
  */
 public class Hotel {
 
   // reference to physical database connection.
   private Connection _connection = null;
   private static String userId = ""; // stores the user ID
 
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
     new InputStreamReader(System.in));
 
   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {
 
     System.out.print("Connecting to database...");
     try {
       // constructs the connection URL
       String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
       System.out.println("Connection URL: " + url + "\n");
 
       // obtain a physical connection
       this._connection = DriverManager.getConnection(url, user, passwd);
       System.out.println("Done");
     } catch (Exception e) {
       System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
       System.out.println("Make sure you started postgres on this machine");
       System.exit(-1);
     } //end catch
   } //end Hotel
 
   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance(double lat1, double long1, double lat2, double long2) {
     double t1 = (lat1 - lat2) * (lat1 - lat2);
     double t2 = (long1 - long2) * (long1 - long2);
     return Math.sqrt(t1 + t2);
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate(String sql) throws SQLException {
     // creates a statement object
     Statement stmt = this._connection.createStatement();
 
     // issues the update instruction
     stmt.executeUpdate(sql);
 
     // close the instruction
     stmt.close();
   } //end executeUpdate
 
   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult(String query) throws SQLException {
     // creates a statement object
     Statement stmt = this._connection.createStatement();
 
     // issues the query instruction
     ResultSet rs = stmt.executeQuery(query);
 
     /*
      ** obtains the metadata object for the returned result set.  The metadata
      ** contains row and column info.
      */
     ResultSetMetaData rsmd = rs.getMetaData();
     int numCol = rsmd.getColumnCount();
     int rowCount = 0;
 
     // iterates through the result set and output them to standard out.
     boolean outputHeader = true;
     while (rs.next()) {
       if (outputHeader) {
         for (int i = 1; i <= numCol; i++) {
           System.out.printf("%-15s\t", rsmd.getColumnName(i).trim()); //Modified to fix align issue 
         }
         System.out.println();
         outputHeader = false;
       }
       for (int i = 1; i <= numCol; ++i) {
         System.out.printf("%-15s\t", rs.getString(i).trim()); //Modified to fix align issue 
       }
       System.out.println();
       ++rowCount;
     } //end while
     stmt.close();
     return rowCount;
   } //end executeQuery
 
   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List < List < String >> executeQueryAndReturnResult(String query) throws SQLException {
     // creates a statement object
     Statement stmt = this._connection.createStatement();
 
     // issues the query instruction
     ResultSet rs = stmt.executeQuery(query);
 
     /*
      ** obtains the metadata object for the returned result set.  The metadata
      ** contains row and column info.
      */
     ResultSetMetaData rsmd = rs.getMetaData();
     int numCol = rsmd.getColumnCount();
     int rowCount = 0;
 
     // iterates through the result set and saves the data returned by the query.
     boolean outputHeader = false;
     List < List < String >> result = new ArrayList < List < String >> ();
     while (rs.next()) {
       List < String > record = new ArrayList < String > ();
       for (int i = 1; i <= numCol; ++i)
         record.add(rs.getString(i));
       result.add(record);
     } //end while
     stmt.close();
     return result;
   } //end executeQueryAndReturnResult
 
   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery(String query) throws SQLException {
     // creates a statement object
     Statement stmt = this._connection.createStatement();
 
     // issues the query instruction
     ResultSet rs = stmt.executeQuery(query);
 
     int rowCount = 0;
 
     // iterates through the result set and count nuber of results.
     while (rs.next()) {
       rowCount++;
     } //end while
     stmt.close();
     return rowCount;
   }
 
   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
     Statement stmt = this._connection.createStatement();
 
     ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
     if (rs.next())
       return rs.getInt(1);
     return -1;
   }
 
   public int getNewUserID(String sql) throws SQLException {
     Statement stmt = this._connection.createStatement();
     ResultSet rs = stmt.executeQuery(sql);
     if (rs.next())
       return rs.getInt(1);
     return -1;
   }
   /**
    * Method to close the physical connection if it is open.
    */
 
   public static String getUserId() {
     return userId;
   }
 
   public static void setUserId(String userId) {
     Hotel.userId = userId;
   }
 
   public void cleanup() {
     try {
       if (this._connection != null) {
         this._connection.close();
       } //end if
     } catch (SQLException e) {
       // ignored.
     } //end try
   } //end cleanup
 
   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main(String[] args) {
     if (args.length != 3) {
       System.err.println(
         "Usage: " +
         "java [-classpath <classpath>] " +
         Hotel.class.getName() +
         " <dbname> <port> <user>");
       return;
     } //end if
 
     Greeting();
     Hotel esql = null;
     try {
       // use postgres JDBC driver.
       Class.forName("org.postgresql.Driver").newInstance();
       // instantiate the Hotel object and creates a physical
       // connection.
       String dbname = args[0];
       String dbport = args[1];
       String user = args[2];
       esql = new Hotel(dbname, dbport, user, "");
 
       boolean keepon = true;
       while (keepon) {
         // These are sample SQL statements
         System.out.println("MAIN MENU");
         System.out.println("---------");
         System.out.println("1. Create user");
         System.out.println("2. Log in");
         System.out.println("9. < EXIT");
         String authorisedUser = null;
         switch (readChoice()) {
         case 1:
           CreateUser(esql);
           break;
         case 2:
           authorisedUser = LogIn(esql);
           break;
         case 9:
           keepon = false;
           break;
         default:
           System.out.println("Unrecognized choice!");
           break;
         } //end switch
         if (authorisedUser != null) {
           boolean usermenu = true;
           while (usermenu) {
             System.out.println("MAIN MENU");
             System.out.println("---------");
             System.out.println("1. View Hotels within 30 units");
             System.out.println("2. View Rooms");
             System.out.println("3. Book a Room");
             System.out.println("4. View recent booking history");
 
             //the following functionalities basically used by managers
             System.out.println("5. Update Room Information");
             System.out.println("6. View 5 recent Room Updates Info");
             System.out.println("7. View booking history of the hotel");
             System.out.println("8. View 5 regular Customers");
             System.out.println("9. Place room repair Request to a company");
             System.out.println("10. View room repair Requests history");
             System.out.println("11. View All room repair Requests history");
 
             System.out.println(".........................");
             System.out.println("20. Log out");
             switch (readChoice()) {
             case 1:
               browseHotels(esql);
               break; // Fixed viewHotels to browseHotels
             case 2:
               viewRooms(esql);
               break;
             case 3:
               bookRooms(esql);
               break;
             case 4:
               viewRecentBookingsfromCustomer(esql);
               break;
             case 5:
               updateRoomInfo(esql);
               break;
             case 6:
               viewRecentUpdates(esql);
               break;
             case 7:
               viewBookingHistoryofHotel(esql);
               break;
             case 8:
               viewRegularCustomers(esql);
               break;
             case 9:
               placeRoomRepairRequests(esql);
               break;
             case 10:
               viewRoomRepairHistory(esql);
               break;
             case 11:
               viewAllRoomRepairHistory(esql);
               break;
             case 20:
               usermenu = false;
               break;
             default:
               System.out.println("Unrecognized choice!");
               break;
             }
           }
         }
       } //end while
     } catch (Exception e) {
       System.err.println(e.getMessage());
     } finally {
       // make sure to cleanup the created table and close the connection.
       try {
         if (esql != null) {
           System.out.print("Disconnecting from database...");
           esql.cleanup();
           System.out.println("Done\n\nBye !");
         } //end if
       } catch (Exception e) {
         // ignored.
       } //end try
     } //end try
   } //end main
 
   public static void Greeting() {
     System.out.println(
       "\n\n*******************************************************\n" +
       "              User Interface      	               \n" +
       "*******************************************************\n");
   } //end Greeting
 
   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
     int input;
     // returns only if a correct value is given.
     do {
       System.out.print("Please make your choice: ");
       try { // read the integer, parse it and break.
         input = Integer.parseInt(in.readLine());
         break;
       } catch (Exception e) {
         System.out.println("Your input is invalid!");
         continue;
       } //end try
     } while (true);
     return input;
   } //end readChoice
 
   /*
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql) {
     try {
       System.out.print("\tEnter name: ");
       String name = in.readLine();
       System.out.print("\tEnter password: ");
       String password = in.readLine();
       String type = "Customer";
       String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
       esql.executeUpdate(query);
       System.out.println("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   } //end CreateUser
 
   // =====================================================================STUDENT MODIFICATION BELOW=======================================================================
   // =====================================================================STUDENT MODIFICATION BELOW=======================================================================
   // =====================================================================STUDENT MODIFICATION BELOW=======================================================================
   // =====================================================================STUDENT MODIFICATION BELOW=======================================================================
   // =====================================================================STUDENT MODIFICATION BELOW=======================================================================
   // We modified the following code to store the user ID into a private variable.
 
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
 
   public static String LogIn(Hotel esql) {
     try {
       System.out.print("\tEnter userID: ");
       String userID = in.readLine();
       System.out.print("\tEnter password: ");
       String password = in.readLine();
 
       String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
       int userNum = esql.executeQuery(query);
       if (userNum > 0) {
         setUserId(userID);
         return userID;
       }
       System.out.println("Invalid USER ID or password. Please try again.");
       return null;
     } catch (Exception e) {
       System.err.println(e.getMessage());
       return null;
     }
   } //end
 
   // Rest of the functions definition go in here
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
   // =====================================================================STUDENT IMPLEMENTATION BELOW=====================================================================
 
   //Helper Function To Verify User
   public static boolean validateManager(Hotel esql, int hotelID) throws Exception {
 
     // Check the userType of the logged-in user
     String userTypeQuery = String.format("SELECT userType FROM Users WHERE userID = %s", getUserId());
     List < List < String >> userTypeResult = esql.executeQueryAndReturnResult(userTypeQuery);
 
     // If the user is an admin, always return true, as they are authorized for all the actions in this system.
     if (!userTypeResult.isEmpty() && userTypeResult.get(0).get(0).trim().equalsIgnoreCase("admin")) {
       return true;
     }
     // If the user is a customer, always return false, as they are NOT authorized for all the higher level actions in this system.
     else if (!userTypeResult.isEmpty() && userTypeResult.get(0).get(0).trim().equalsIgnoreCase("customer")) {
       System.out.println("Sorry, You are not authorized to access this hotel's information.");
       return false;
     }
 
     // When it reaches here, we are sure they are manager, but we still need to check if they are the manager of the given hotelID's Hotel.
     String query = String.format("SELECT * FROM Hotel WHERE managerUserID = %s AND hotelID = %d", getUserId(), hotelID);
     List < List < String >> result = esql.executeQueryAndReturnResult(query);
 
     if (result.isEmpty()) {
       System.out.println("You are not authorized to access hotels that are outside of your management.");
       return false;
     }
     return true;
   }
 
   // FUNCTION X 1
   public static void browseHotels(Hotel esql) {
     try {
       //Get User Input
       System.out.print("\tEnter latitude: ");
       double latitude = Double.parseDouble(in.readLine());
       System.out.print("\tEnter longitude: ");
       double longitude = Double.parseDouble(in.readLine());
 
       //SQL
       String query = String.format("SELECT hotelID, hotelName, latitude, longitude FROM Hotel WHERE calculate_distance(latitude, longitude, %f, %f) <= 30.0", latitude, longitude);
       int rowCount = esql.executeQueryAndPrintResult(query);
       // This query selects the required attributes from the Hotel table with WHERE clauses calling the SQL function to check the distance condition.
 
       // Some header would still be print, the only way to avoid that is to do executeQueryAndGetReuslt, first check if it is empty then call executeQueryAndPrintResult, HOWEVER, this would be doing the SQL part twice, which is very not ideal.
       if (rowCount == 0) {
         System.out.println("\nWe apologize for the inconvenience, but we regret to inform you that there are no hotels within 30.0 miles of the specified location.\n");
       }
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 2
   public static void viewRooms(Hotel esql) {
     try {
 
       System.out.print("Enter hotel ID: ");
       String hotelID = in.readLine();
       System.out.print("Enter your visiting date (YYYY-MM-DD): ");
       String date = in.readLine();
 
       //SQL
       String query = String.format("SELECT R.roomNumber, R.price, H.hotelName FROM Rooms R, Hotel H WHERE R.hotelID = '%s' AND R.hotelID = H.hotelID AND R.roomNumber NOT IN (SELECT RBOOK.roomNumber FROM RoomBookings RBOOK WHERE RBOOK.hotelID = '%s' AND RBOOK.bookingDate = '%s') ORDER BY R.roomNumber", hotelID, hotelID, date);
       // To view rooms from a certain hotel, we first select only rooms belonging to that hotel by checking with the hotelID equal to user input. Then, to check the availability of a room, we use a NOT IN in a subquery where it does almost the same thing but in the RoomBookings table (where it stores all the booking information). 
 
       List < List < String >> rooms = esql.executeQueryAndReturnResult(query);
 
       if (rooms.isEmpty()) {
         System.out.println("\nWe apologize for the inconvenience, but we regret to inform you that we have run out of available rooms for the dates you requested.");
       } else {
         String hotelName = rooms.get(0).get(2).trim(); //To get the hotel name from the list list, we trim it cause it has a long space after the name for some reason
         System.out.println("\nRooms available for " + hotelName + " on " + date);
         for (List < String > row: rooms) {
           System.out.printf("Room Number: %s, Price: %s\n", row.get(0), row.get(1)); // Output all the rooms
         }
       }
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 3
   public static void bookRooms(Hotel esql) {
     try {
       System.out.print("Enter Hotel ID: ");
       String hotelID = in.readLine();
       System.out.print("Enter Room Number: ");
       String roomNumber = in.readLine();
       System.out.print("Enter Date (YYYY-MM-DD): ");
       String date = in.readLine();
 
       //SQL
       String query = String.format("SELECT R.price FROM Rooms R WHERE R.hotelID = '%s' AND R.roomNumber = '%s'", hotelID, roomNumber);
       List < List < String >> rooms = esql.executeQueryAndReturnResult(query);
       // The first SQL query retrieves the price for the given hotel room, ensuring the input hotel room actually exists. 
 
       if (rooms.isEmpty()) {
         System.out.println("We apologize for the inconvenience, but it looks like you might have input invalid Hotel ID and/or Room Number. Please try again!");
         return;
       }
 
       //SQL
       query = String.format("SELECT * FROM RoomBookings RB WHERE RB.hotelID = '%s' AND RB.roomNumber = '%s' AND RB.bookingDate = '%s'", hotelID, roomNumber, date);
       List < List < String >> bookings = esql.executeQueryAndReturnResult(query);
       //The second query checks the same room from the RoomsBookings table; if the room exists in this table, it means the room is already booked on the given date.      
 
       if (!bookings.isEmpty()) {
         System.out.println("We apologize for the inconvenience, but the room is already booked on " + date + ".");
         return;
       }
 
       String price = rooms.get(0).get(0);
       System.out.println("Room price: " + price);
 
       query = String.format("INSERT INTO RoomBookings (bookingID, customerID, hotelID, roomNumber, bookingDate) VALUES (DEFAULT, '%s', '%s', '%s', '%s')", getUserId(), hotelID, roomNumber, date);
       esql.executeUpdate(query);
       //Finally, if it reaches this point, it means the given room is available for booking, so we simply insert the corresponding data into RoomBookings.
       System.out.println("Room booked successfully! If you want to modify the reservation, please call us directly!");
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 4
   public static void viewRecentBookingsfromCustomer(Hotel esql) {
     try {
       //SQL
       String query = String.format("SELECT bookingID, hotelID, roomNumber, bookingDate, customerID FROM RoomBookings WHERE customerID = '%s' ORDER BY bookingDate DESC LIMIT 5", getUserId());
       int rowCount = esql.executeQueryAndPrintResult(query); // This would print AT MOST 5 histories 
       //Users can only get info from RoomBookings if the attribute matches their customerID. Order by booking date in descending order, so we get a most recent sort, and limit to 5, so we only see the most recent 5 bookings.
 
       if (rowCount == 0) {
         System.out.println("You have no booking history yet."); // In the case where customer has NO bookingHistory
         return;
       }
       if (rowCount < 5) {
         System.out.println("You have reached the end of your booking history."); // This would print at the end of the X history the customer have
       }
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 5
   public static void updateRoomInfo(Hotel esql) {
     try {
       System.out.print("Enter Hotel ID: ");
       String hotelID = in.readLine();
       System.out.print("Enter Room Number: ");
       String roomNumber = in.readLine();
 
       // Calling Helper Function to Check if the "user" is authorized for this higher level action.
       if (!validateManager(esql, Integer.parseInt(hotelID))) {
         return;
       }
 
       // SQL Validate if it does exist in the system.
       String query = String.format("SELECT * FROM Rooms WHERE hotelID = '%s' AND roomNumber = '%s'", hotelID, roomNumber);
       // First, we select the room.
       List < List < String >> rooms = esql.executeQueryAndReturnResult(query);
       if (rooms.isEmpty()) {
         System.out.println("Invalid Hotel ID or Room Number. Please try again!");
         return;
       }
 
       // Display the current price and URL
       String current_Price = rooms.get(0).get(2);
       String current_Image = rooms.get(0).get(3);
       System.out.println("Current Room Information:");
       System.out.println("Price: " + current_Price);
       System.out.println("Image URL: " + current_Image);
 
       // Ask manager to input new price and/or URL 
       System.out.print("Enter NEW price (enter 'skip' to keep the same): ");
 
       //For Price
       String newPrice = in.readLine();
       if (!newPrice.equals("skip")) {
         query = String.format("UPDATE Rooms SET price = '%s' WHERE hotelID = '%s' AND roomNumber = '%s'", newPrice, hotelID, roomNumber);
         esql.executeUpdate(query);
       }
 
       //For URL
       System.out.print("Enter NEW image URL (enter 'skip' to keep the same): ");
       String newImageURL = in.readLine();
       if (!newImageURL.equals("skip")) {
         query = String.format("UPDATE Rooms SET imageURL = '%s' WHERE hotelID = '%s' AND roomNumber = '%s'", newImageURL, hotelID, roomNumber);
         esql.executeUpdate(query);
       }
       // Then, we update the price and URL
 
       // Log the update in RoomUpdatesLog table with timestamp
       String temp_time = LocalDateTime.now().toString();
       query = String.format("INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES ('%s', '%s', '%s', '%s')", getUserId(), hotelID, roomNumber, temp_time);
       esql.executeUpdate(query);
       // After that, we insert it into the log
 
       System.out.println("Room information updated successfully! Timestamp: " + temp_time + " ");
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 6
   public static void viewRecentUpdates(Hotel esql) {
     try {
       System.out.print("Enter Hotel ID: ");
       String hotelID = in.readLine();
 
       // Calling Helper Function to Check if the "user" is authorized for this higher level action.
       if (!validateManager(esql, Integer.parseInt(hotelID))) {
         return;
       }
 
       //SQL
       String query = String.format("SELECT hotelID, roomNumber, managerID, updatedOn FROM RoomUpdatesLog WHERE hotelID = '%s' ORDER BY updatedOn DESC LIMIT 5", hotelID);
       // Query the top 5 most recent rows from RoomUpdatesLog where the hotel ID matches the current user's access.
 
       // Check if it is empty
       int rowCount = esql.executeQueryAndPrintResult(query);
       if (rowCount == 0) {
         System.out.println("You have no recent updates for hotel with hotelID: " + hotelID + " ");
       }
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 7 
   public static void viewBookingHistoryofHotel(Hotel esql) {
     try {
       System.out.print("\tEnter Hotel ID: ");
       String input = in.readLine();
       int hotelID = Integer.parseInt(input);
 
       // Calling Helper Function to Check if the "user" is authorized for this higher level action.
       if (!validateManager(esql, hotelID)) {
         return;
       }
 
       // USER INPUT
       System.out.println("\tEnter the range of your desired output below");
       System.out.print("\tEnter starting date (YYYY-MM-DD): ");
       String date_Begin = in.readLine();
       System.out.print("\tEnter ending date (YYYY-MM-DD): ");
       String date_End = in.readLine();
 
       // SQL
       String query = "SELECT B.bookingID, U.name, B.hotelID, B.roomNumber, B.bookingDate FROM RoomBookings B, Users U WHERE B.hotelID = " + hotelID + " AND B.bookingDate BETWEEN \'" + date_Begin + "\' AND \'" + date_End + "\' AND B.customerID = U.userID ORDER BY B.bookingDate";
       // This print out the thing like a single quote with the date in the Where.
       // Simply get desired data from the RoomBookings table given the hotel ID and use a range to filter out undesired parts.
 
       esql.executeQueryAndPrintResult(query);
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 8
   public static void viewRegularCustomers(Hotel esql) {
     try {
 
       // USER INPUT
       System.out.print("Enter hotel ID: ");
       int hotelID = Integer.parseInt(in.readLine());
 
       // Calling Helper Function to Check if the "user" is authorized for this higher level action.
       if (!validateManager(esql, hotelID)) {
         return;
       }
 
       // SQL
       String query = String.format("SELECT U.userID, U.name, COUNT(*) AS num_bookings FROM RoomBookings B, Rooms R, Users U WHERE B.hotelID=%d AND B.roomNumber=R.roomNumber AND R.hotelID=%d AND B.customerID=U.userID AND U.userType='customer' GROUP BY U.userID, U.name ORDER BY num_bookings DESC LIMIT 5;", hotelID, hotelID);
       System.out.println("Top 5 customers who made the most bookings in the hotel:");
       esql.executeQueryAndPrintResult(query); // FIX ME the output is misaligned: FIXED
       //Use the aggregate function COUNT to count how many times a user appears in the table and filter out admin and manager, as they might reserve rooms for other purposes. Group by individual users and return the top 5 rows (sorted from max to min).
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 9
   public static void placeRoomRepairRequests(Hotel esql) {
     try {
 
       // USER INPUT
       System.out.print("Enter hotel ID: ");
       int hotelID = Integer.parseInt(in.readLine());
 
       // Calling Helper Function to Check if the "user" is authorized for this higher level action.
       if (!validateManager(esql, hotelID)) {
         return;
       }
 
       // USER INPUT
       System.out.print("Enter room number: ");
       int roomNumber = Integer.parseInt(in.readLine());
 
       System.out.print("Enter company ID: ");
       int companyID = Integer.parseInt(in.readLine());
 
       System.out.print("Enter repair date (YYYY-MM-DD): ");
       String repairDate = in.readLine();
 
       // SQL INSERT
       String query = String.format("INSERT INTO RoomRepairs (companyID, hotelID, roomNumber, repairDate) VALUES (%d, %d, %d, '%s')", companyID, hotelID, roomNumber, repairDate);
       esql.executeUpdate(query);
       // The first query directly inserts into the RoomRepairs table; it would use a predefined seq and get a repairID upon insertion. 
 
       // SQL get repair ID back
       query = String.format("SELECT repairID FROM RoomRepairs WHERE companyID = %d AND hotelID = %d AND roomNumber = %d AND repairDate = '%s'", companyID, hotelID, roomNumber, repairDate);
       List < List < String >> result = esql.executeQueryAndReturnResult(query);
       int repairID = Integer.parseInt(result.get(0).get(0));
       // We then retrieve that value using the second query. 
 
       // SQL INSERT to RoomRepairRequests
       query = String.format("INSERT INTO RoomRepairRequests (managerID, repairID) VALUES (%s, %d)", getUserId(), repairID);
       esql.executeUpdate(query);
       // After that, we insert the repairID and managerID into the RoomRepairRequests table.
 
       System.out.println("Room repair request placed successfully.");
 
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   // FUNCTION X 10
   // Check all the history of a given room repair.
   public static void viewRoomRepairHistory(Hotel esql) {
     try {
       // USER INPUT
       System.out.print("Enter hotel ID: ");
       int hotelID = Integer.parseInt(in.readLine());
 
       // Calling Helper Function to Check if the "user" is authorized for this higher level action.
       if (!validateManager(esql, hotelID)) {
         return;
       }
 
       // USER INPUT
       System.out.print("Enter room number: ");
       int roomNumber = Integer.parseInt(in.readLine());
 
       //SQL
       String query = String.format("SELECT R.companyID, R.hotelID, R.roomNumber, R.repairDate FROM RoomRepairs R, RoomRepairRequests RR WHERE R.repairID = RR.repairID AND R.hotelID = %d AND R.roomNumber = %d", hotelID, roomNumber);
       // Simply select what's in both RoomRepairs tables with the matching repairID in RoomRepairRequests.
 
       System.out.println("\nRoom repair history for the given inputs:\n");
       int rowCount = esql.executeQueryAndPrintResult(query);
       if (rowCount == 0) {
         System.out.println("No repair history found for the given inputs.");
       }
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
   //viewAllRoomRepairHistory is added to the manager can view all the repair history across all rooms given hotel number. As Option 11.
   // FUNCTION X 11.
   public static void viewAllRoomRepairHistory(Hotel esql) {
     try {
 
       // USER INPUT
       System.out.print("Enter hotel ID: ");
       int hotelID = Integer.parseInt(in.readLine());
 
       // Calling Helper Function to Check if the "user" is authorized for this higher level action.
       if (!validateManager(esql, hotelID)) {
         return;
       }
 
       //SQL
       String query = String.format("SELECT R.companyID, R.hotelID, R.roomNumber, R.repairDate FROM RoomRepairs R, RoomRepairRequests RR WHERE R.repairID = RR.repairID AND R.hotelID = %d ORDER BY R.roomNumber, R.repairDate", hotelID);
       // Simply select what's in both RoomRepairs tables with the matching repairID in RoomRepairRequests, without checking the room number and specific date.
       System.out.println("\nAll room repair history for the given hotel:\n");
       int rowCount = esql.executeQueryAndPrintResult(query);
       if (rowCount == 0) {
         System.out.println("No repair history found for the given hotel.");
       }
     } catch (Exception e) {
       System.err.println(e.getMessage());
     }
   }
 
 } //end Hotel