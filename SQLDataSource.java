import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLDataSource {
			
   	public static ArrayList<String> columnNames(String tableName) throws SQLException, ClassNotFoundException{
	    ArrayList<String> columns = new ArrayList<String>();	    
	    Connection conn = sqlSettings.conn();    
	    PreparedStatement prep = conn.prepareStatement("DESCRIBE " + tableName);
	    ResultSet rs = prep.executeQuery();	    
	    while(rs.next()){
	    	columns.add(rs.getString(1));
	    }
	    rs.close();
	    prep.close();
	    conn.close(); 
	    //System.out.print("\nColumn connection closed: " + conn.isClosed()+ "\n");   
	    return columns;
	}//end columnNames
	
	public static String insertRow(String tableName,String[] rowData) throws SQLException, ClassNotFoundException{
		Connection conn = sqlSettings.conn();    
		String id = "0";		
		ArrayList<String> columns = columnNames(tableName);		
		String columnList = columns.get(1);//initialize first value, skipping login_id column
		for(int i=2;i<columns.size();i++ ){
			columnList += ","+columns.get(i);//adding the rest value separate by commas
		}		
		String params = "'"+rowData[1]+"'";
		for (int j=2;j<rowData.length;j++){
			params += ",'"+rowData[j]+"'";
		}				
		Statement stmt;
		stmt = conn.createStatement();
		stmt.executeUpdate( "INSERT INTO "+tableName+"("+columnList+") VALUES("+params+")" );
		
	    PreparedStatement prep = conn.prepareStatement("SELECT LAST_INSERT_ID()");
		ResultSet rs = prep.executeQuery();		
		if(rs.next()) id = rs.getString(1);			
		rs.close();
		stmt.close();
		prep.close();
		conn.close();
		//System.out.print("\nInsert connection closed: " + conn.isClosed()+ "\n");    
		return id;
	}
	
	public static boolean updateRow(String tableName, String[] rowData) throws SQLException, ClassNotFoundException{
		Connection conn = sqlSettings.conn();      			
		ArrayList<String> columns = columnNames(tableName);			
		String params = columns.get(1)+"='"+rowData[1]+"'";
		PreparedStatement prep;		
		for (int j=2;j<columns.size();j++){
			params += ","+columns.get(j)+"='"+rowData[j]+"'";
		}		
		try{
		    prep =  conn.prepareStatement( "UPDATE "+tableName+" SET "+params+" WHERE ref_index="+rowData[0] );
		    prep.execute();
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		prep.close();
		conn.close();
		//System.out.print("\nUpdate connection closed: " + conn.isClosed()+ "\n");     
		if(conn.isClosed())
	    	return true;
	    else
	    	return false;
	}
	
	public static boolean deleteRow(String tableName, int[] ids) throws SQLException, ClassNotFoundException{
		Connection conn = sqlSettings.conn();		
		PreparedStatement prep = conn.prepareStatement("DELETE FROM "+tableName+" WHERE ref_index=?");
		try{
			for(int i=0;i<ids.length;i++){
				prep.setInt(1,ids[i]);
				prep.execute();
			}
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		prep.close();
		conn.close();
		//System.out.print("\nSelect Delete connection closed: " + conn.isClosed()+ "\n"); 
		if(conn.isClosed())
	    	return true;
	    else
	    	return false;
	}
	
	public static ArrayList<Map<String,String>> getTable(String tableName) throws SQLException, ClassNotFoundException{
		String[] empty = new String[0];
		return select(tableName,empty,new HashMap<String,String>());
	}
	
	public static ArrayList<Map<String,String>> select(String tableName, 
		String columns[], Map<String,String> criteria) throws SQLException, ClassNotFoundException{
		
		Connection conn = sqlSettings.conn();  
		ArrayList<String> allColumns = columnNames(tableName);
		ArrayList<Map<String,String>> table = new ArrayList<Map<String,String>>();
		
		String columnList = allColumns.get(0);//initialize first value, skipping login_id column
		for(int i=1;i<allColumns.size();i++ ){
			columnList += ","+allColumns.get(i);//adding the rest value separate by commas
		}		
		//String keys[] = criteria.keySet().toArray(new String[0]);
		//String criteriaString = "";		
		PreparedStatement prep = conn.prepareStatement("SELECT "+columnList+" FROM "+tableName);
		ResultSet rs = prep.executeQuery();
		
		//convert to ArrayList<Map>
		ResultSetMetaData metaData = rs.getMetaData();
		int numOfColumns = metaData.getColumnCount();
		Map<String,String> rowData = null;
		while(rs.next()){
			rowData = new HashMap<String,String>();
			for(int j=1;j<=numOfColumns;j++){
				rowData.put(metaData.getColumnName(j), rs.getString(j));
			}
			table.add(rowData);
		}
		//System.out.println(table.toString());
		rs.close();
		prep.close();
		conn.close();
		//System.out.print("\nSelect connection closed: " + conn.isClosed()+ "\n");     
		return table;
	}
	
	public static boolean showTable(String tableName, int index) throws SQLException, ClassNotFoundException{
		ArrayList<String> cols = columnNames(tableName);	
		ResultSet rs;
		Statement stmt;
		Connection conn = sqlSettings.conn();   
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT * " + "FROM "+tableName+" ORDER BY "+cols.get(index));   
		while(rs.next()){        
	    	 System.out.println("\t" + rs.getInt(cols.get(0))+ 
		           "\t" + rs.getString(cols.get(1))+ 
		           "\t" + rs.getString(cols.get(2))+
		           "\t" + rs.getString(cols.get(3))+
		           "\t" + rs.getString(cols.get(4)));
	    }//end while loop
	    stmt.close();
	    conn.close();
	    //System.out.print("\nShow table connection closed: " + conn.isClosed()+ "\n"); 
	    if(conn.isClosed())
	    	return true;
	    else
	    	return false;
	}
	
	public static String RemoteLogin(String tbname, String email, String pwd) throws SQLException, ClassNotFoundException{		
		Connection conn = sqlSettings.conn();    
		String loginSuccess = "";	
		Statement stmt = conn.createStatement();;	
		
		try {		          
		    //String query = "SELECT user_id FROM "+tbname+" WHERE user_email='" + email + "' AND user_pw='" + pwd + "' ";
		    String query = "SELECT ref_index FROM "+tbname+" WHERE user_name='" + email + "' AND user_key='" + pwd + "' ";
		    ResultSet rs = stmt.executeQuery(query);			  	  
		  	if (rs.next())
		  	   loginSuccess = "yes";			  	  
		  	else 
		  	   loginSuccess = "no";			  	
		  	rs.close();
			stmt.close();
			conn.close();
			//System.out.print("\nLogin Connection closed: " + conn.isClosed()+ "\n"); 
		}catch( Exception e ) {
		    e.printStackTrace();
		}//end catch		
		return loginSuccess;	
	}
	
	
	public static void main(String args[])throws SQLException, ClassNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException{
	    System.out.println("Copyright 2009, H.V. Tran");
	    System.out.println("Running Main...");
	    String table_name = "logintb";
	    
	    //String[] Testdata = {"2","guest","guest","Tom","Nguyen"};
	    //int[] ids= {3,7,10,12};
	    
	    //Calling public function
	    System.out.println(RemoteLogin(table_name,"yours_007","7144149045"));
	    //System.out.println(getTable(table_name));
	    //System.out.println(insertRow(table_name, Testdata));
	    //System.out.println(updateRow(table_name, Testdata));	
	    //System.out.println(deleteRow(table_name, ids));	    
	    //System.out.print(columnNames(table_name)+"\n");	
	    //System.out.print(showTable(table_name,0));
	    
	    //String Url = table_name;  System.out.println( DigestUtils.shaHex(  Url ) ); 
	}//end main
}
