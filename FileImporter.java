import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileImporter {

	private static String driver = SQLDataSource.driver;
	private static String url = SQLDataSource.url;
	private static String username = SQLDataSource.username;
	private static String password = SQLDataSource.password;
	
	public FileImporter() throws ClassNotFoundException {
		Class.forName(driver);
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	} // create connection
	
	private static String replacer(String inputStr, String replaceStr,
			String withStr) {
		Pattern pattern = Pattern.compile(replaceStr);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.replaceAll(withStr);
	} // end replacer
	
	public ArrayList<Map<String,Integer>> uploadKraftSwami(byte[] bytes, String fileName, String mainTable, String outageTable) throws Exception {
		uploadCSV(bytes, fileName, mainTable);
		OutageReportUtility oru = new OutageReportUtility();
		return oru.updateOutages(mainTable, outageTable);
	}
	
	//following code is intended to support uploading of files from the KS web version
	public void uploadCSV(byte[] bytes, String fileName, String tableName) throws Exception {

		Connection conn = getConnection();
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream(bytes)));
		String columnHeaders = null;
		
		fileName = System.getProperty("java.io.tmpdir") + "/" + fileName;
		FileWriter fw = new FileWriter(fileName);
		String currentLine = reader.readLine();
		while(currentLine != null) { // loop through input stream
			// skip empty or non-CSV rows
			if (Pattern.matches(".*,.*", currentLine)) {
				// check for existance of column headers and store separately
				if (Pattern.matches(".*CLASSIFICATION.*",currentLine)) {
					columnHeaders = currentLine;
					// replace spaces with '_', replace any other unnecessary characters
					columnHeaders = replacer(columnHeaders," ","_");
					columnHeaders = replacer(columnHeaders,"[^\\w\"\',_]","_");
				} else {
					// write data to tmp file
					fw.write(currentLine+"\n");
				}
			}
			currentLine = reader.readLine();
		} // end of input stream
		fw.close();

		// if headers could not be identified in the file
		if (columnHeaders == null)
			throw new Exception("Column headers could not be identified");

		// check for existence of table
		try {
			SQLDataSource datasource = new SQLDataSource();
			datasource.columnNames(tableName);
		} catch (SQLException e) {
			// table does not exist - create table
			String columnHeadersWithDefinition = replacer(columnHeaders,","," VARCHAR(255) NOT NULL,");
			PreparedStatement build = conn.prepareStatement("CREATE TABLE "+ tableName +
					" (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," + columnHeadersWithDefinition +
					" VARCHAR(255) NOT NULL)");
			build.execute();
			build.close();
		} 

		// load data into table
		PreparedStatement load = conn.prepareStatement("LOAD DATA LOCAL INFILE ? REPLACE INTO TABLE "+tableName+" FIELDS TERMINATED BY ',' ENCLOSED BY '\"' " +
			"LINES TERMINATED BY ? ("+columnHeaders+")");
		load.setString(1,fileName);
		load.setString(2,"\n");
		load.executeQuery();
		load.close();
		conn.close();
	} // end uploadCSV

	public ArrayList<Map<String,String>> selectDupe(String tableName, String[] ignoreColumns) throws Exception {

		ArrayList<Map<String,String>> table = new ArrayList<Map<String,String>>();
		ResultSet result;
		
		ArrayList<Map<String,String>> dupes = findDupe(tableName, ignoreColumns);
		for (int i=0; i<dupes.size(); i++) {
			dupes.get(i).remove("repetitions");
		} // remove repetitions

		Connection conn = getConnection();
		String keys[] = dupes.get(0).keySet().toArray(new String[0]);
		String query = "SELECT id FROM "+tableName+" WHERE "+keys[0]+"=?";
		for (int i=1; i<keys.length; i++) {
			query += " && "+keys[i]+"=?";
		}
		PreparedStatement prep = conn.prepareStatement(query);
		for (int i=0; i<dupes.size(); i++) {
			for (int j=0; j<keys.length; j++) {
				prep.setString(j+1,dupes.get(i).get(keys[j]));
			}
			result = prep.executeQuery();
			String id = "";
			while(result.next()) {
				id += result.getString(1)+" ";
			} // iterate over specific duplicated dataset
			dupes.get(i).put("id",id);
			table.add(dupes.get(i));
			
		} // iterate over all duplicated datasets
		return table;
	}

	public ArrayList<Map<String,String>> findDupe(String tableName, String[] ignoreColumns) throws Exception {
		Connection conn = getConnection();
		SQLDataSource datasource = new SQLDataSource();
		ArrayList<String> columns = datasource.columnNames(tableName);
		for (int i=0; i<ignoreColumns.length; i++)
			columns.remove(ignoreColumns[i]);
		String columnList = columns.get(0);
		for(int i = 1; i<columns.size(); i++)
			columnList += ","+columns.get(i);
		
		// identify and count duplicates
		PreparedStatement prep = conn.prepareStatement("SELECT COUNT(*) as repetitions,"+columnList+" FROM "+tableName
				+" GROUP BY "+columnList+" HAVING repetitions>1");
		ResultSet result = prep.executeQuery();
		ResultSetMetaData metaData = result.getMetaData();
		int numOfColumns = metaData.getColumnCount();
		ArrayList<Map<String,String>> table = new ArrayList<Map<String,String>>();
		
		while(result.next()) {
			Map<String,String> rowData = new HashMap<String,String>();

			for (int i = 1; i <= numOfColumns; i++){
				rowData.put(metaData.getColumnName(i),result.getString(i));
			} // end build rowData

			table.add(rowData);
		} // end build table
		
		return table;
	} // end find duplicates
	
	public void dedupe(String tableName, String[] ignoreColumns) throws Exception {
		Connection conn = getConnection();
		SQLDataSource datasource = new SQLDataSource();
		ArrayList<String> columns = datasource.columnNames(tableName);
		for (int i=0; i<ignoreColumns.length; i++)
			columns.remove(ignoreColumns[i]);
		String columnList = columns.get(0);
		for(int i = 1; i<columns.size(); i++)
			columnList += ","+columns.get(i);
		
//		PreparedStatement prep = conn.prepareStatement("CREATE TABLE tmp SELECT DISTINCT * FROM "+tableName+" GROUP BY "+columnList+" ORDER BY id");
//		prep.execute();
//		prep = conn.prepareStatement("ALTER IGNORE TABLE tmp MODIFY id INT UNSIGNED NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (id)");
//		prep.execute();
//		prep = conn.prepareStatement("DROP TABLE "+tableName);
//		prep.execute();
//		prep = conn.prepareStatement("ALTER TABLE tmp RENAME TO "+tableName);
//		prep.execute();
		
		PreparedStatement prep = conn.prepareStatement("TRUNCATE TABLE test");
		prep.execute();
		prep = conn.prepareStatement("INSERT INTO test ("+columnList+") SELECT DISTINCT "+columnList+" FROM "+tableName);
		prep.execute();
		prep = conn.prepareStatement("TRUNCATE TABLE "+tableName);
		prep.execute();
		prep = conn.prepareStatement("INSERT INTO "+tableName+" SELECT * FROM test");
		prep.execute();
		
	} // end de-duplicate
		
	public void emptyTable(String tableName) throws SQLException {
		Connection conn = getConnection();
		
		PreparedStatement prep = conn.prepareStatement("TRUNCATE TABLE "+tableName);
		prep.execute();

		conn.close();
	} // end emptyTable
	
	public void dropTable(String tableName) throws SQLException {
		Connection conn = getConnection();
		
		PreparedStatement prep = conn.prepareStatement("DROP TABLE "+tableName);
		prep.execute();

		conn.close();
	} // end dropTable
}