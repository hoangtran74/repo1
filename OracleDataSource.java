import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OracleDataSource {
	
   	public static ArrayList<String> columnNames(String tableName) throws SQLException, ClassNotFoundException{
	    ArrayList<String> columns = new ArrayList<String>();	    
	    ResultSet rs;
	    PreparedStatement stmt;
		Connection conn = oracleSettings._connection(); 		
		String query = "select * from "+tableName;
		stmt = conn.prepareStatement(query);
		rs = stmt.executeQuery();
		
		ResultSetMetaData rsMetaData = rs.getMetaData();
	    int numberOfColumns = rsMetaData.getColumnCount();
	    // get the column names; column indexes start from 1
	    for (int i = 1; i < numberOfColumns + 1; i++) {
	    	columns.add(rsMetaData.getColumnName(i));
	    }
	    stmt.close();
	    conn.close(); 
	    //System.out.print("\nColumn connection closed: " + conn.isClosed()+ "\n");   
	    return columns;
	}//end columnNames
	
	public static boolean insertRow(String tableName,Object[] testdata) throws SQLException, ClassNotFoundException, IOException{
		Connection conn = oracleSettings._connection(); 
		ArrayList<String> columns = columnNames(tableName);	
		Statement stmt;				
		stmt = conn.createStatement();	
		
		try{
			String columnList = columns.get(0);//initialize first value, skipping login_id column
			for(int i=1;i<columns.size();i++ ){
				columnList += ","+columns.get(i);//adding the rest value separate by commas
			}	
			System.out.print(columnList+"\n");
			
			String params = "'"+testdata[0]+"'";
			for (int j=1;j<testdata.length;j++){
				params += ",'"+testdata[j]+"'";
			}		
			System.out.print(params+"\n");			
			stmt.executeUpdate("INSERT INTO "+tableName+"("+columnList+") VALUES("+params+")" );
			stmt.close();
			conn.close();
		    return true;
		}
		catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		//System.out.print("\nInsert connection closed: " + conn.isClosed()+ "\n"); 
	}
	
	public static boolean updateRow(String tableName, Object[] rowData) throws SQLException, ClassNotFoundException{
		Connection conn = oracleSettings._connection();      			
		ArrayList<String> columns = columnNames(tableName);			
		String params = columns.get(0)+"='"+rowData[0]+"'";
		PreparedStatement prep;		
		for (int j=1;j<columns.size();j++){
			params += ","+columns.get(j)+"='"+rowData[j]+"'";
		}		
		try{
		    prep =  conn.prepareStatement( "UPDATE "+tableName+" SET "+params+" WHERE "+columns.get(0)+"="+rowData[0] );
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
		ArrayList<String> columns = columnNames(tableName);	
		Connection conn = oracleSettings._connection();		
		PreparedStatement prep = conn.prepareStatement("DELETE FROM "+tableName+" WHERE "+columns.get(0)+"=?");
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
		
		Connection conn = oracleSettings._connection();  
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
		Connection conn = oracleSettings._connection(); 
		
		String query = "select * from "+tableName;
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		
		ArrayList<String> tmpCol = new ArrayList<String>();
		ArrayList<String> tmpVal = new ArrayList<String>();
		for(int i=0; i<cols.size();i++){
			tmpCol.add(cols.get(i).toString()+"\t");
		}
		System.out.println(tmpCol);
		while(rs.next()){ 			
			for(int i=0; i<cols.size();i++){				 
				tmpVal.add( rs.getString(cols.get(i).toString()) +"\t");				
			}
			System.out.println(tmpVal);
			tmpVal = new ArrayList<String>();
	    }//end while loop
		
	    stmt.close();
	    conn.close();
	    //System.out.print("\nShow table connection closed: " + conn.isClosed()+ "\n"); 
	    if(conn.isClosed())
	    	return true;
	    else
	    	return false;
	}
	
	public static String RemoteLogin(String tableName, String user, String pwd) throws SQLException, ClassNotFoundException{		
		Connection conn = oracleSettings._connection();    
		String loginSuccess = "";	
		Statement stmt = conn.createStatement();
		ArrayList<String> columns = columnNames(tableName);	
		
		try {		          
		    String query = "SELECT "+columns.get(0)+" FROM "+tableName+" WHERE user_name='" + user + "' AND user_pw='" + pwd + "' ";
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
		
	public static void main(String args[])throws SQLException, ClassNotFoundException, IOException{
	    System.out.println("Copyright 2011, H.V. Tran");
	    System.out.println("Running Main...");
	    
	    
	    //Object[] Testdata = {3,"guest3","test3"};
	    //int[] ids= {2};
	    
	    //Calling public function
	    //System.out.println(RemoteLogin("guest","guest"));
	    
	    //System.out.println(insertRow("employee", Testdata));
	    //System.out.println(updateRow("employee", Testdata));	
	    //System.out.println(deleteRow("employee", ids));	    
	    System.out.print(columnNames("SQLPLUS_PRODUCT_PROFILE"));
	    //System.out.println(getTable("employee"));
	    //System.out.print(showTable("employee",0));		   
	}//end main
}
