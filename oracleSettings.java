import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class oracleSettings { 	
	//database driver
	public static String _driver = "oracle.jdbc.driver.OracleDriver";	
	//Define URL of database server for
	//database named mysql on the localhost
	//with the default port number 3306. 
	public static String _url ="jdbc:oracle:thin:@localhost:1521:xe";	
	//database root
	public static String _username = "username";	
	//database password
	public static String _password = "password";	
	//database connection
	public static Connection _connection;	
	public static Connection _connection() throws ClassNotFoundException, SQLException{
		//Register the JDBC driver for Oracle.
		Class.forName(_driver);
		_connection = null;		
		//establish connection
		_connection = DriverManager.getConnection(_url,_username,_password);
		 System.out.println(_connection);
		return _connection;
    }
}