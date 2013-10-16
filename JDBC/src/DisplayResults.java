/**
 * Adam Stark
 * 10/10/2013
 * DisplayResults.java
 * Creates the GUI for user interaction. Sends results to the database.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.JButton;

public class DisplayResults extends JFrame {

   //Strings for driver, DB URL, and clients query
   public String JDBC_DRIVER;
   public String DATABASE_URL;
   public String USER_QUERY;
   
   //*** THIS CAN BE MODIFIED BY THE CLIENT TO ADD MORE DRIVERS / DATABASES ***
   private String[] drivers = {"com.mysql.jdbc.Driver"};
   private String[] databases = {"jdbc:mysql://localhost:3306/prog3"};
   
   private String command = "";
   private int rowsAffected;
   
   //Declare all of the Swing objects 
   private Font font;
   private Font italicFont;
   private JPanel informationPanel = new JPanel();
   private JPanel buttonPanel = new JPanel();
   private JPanel topPanel = new JPanel();
   private JPanel leftPanel = new JPanel();
   private JPanel rightPanel = new JPanel();
   private JPanel bottomPanel = new JPanel();
   private JButton clearCommand = new JButton("Clear Command");
   private JButton execute = new JButton("Submit Command");
   private JButton clearResults = new JButton("Clear Result(s) Window");
   private JButton connect = new JButton("Connect");
   private JLabel databaseInstruction = new JLabel("Enter Database Information");
   private JLabel connectionStatus = new JLabel("Not Connected");
   private JLabel databaseDriver = new JLabel("JDBC Driver");
   private JLabel databaseURL = new JLabel("Database URL");
   private JLabel passwordLabel = new JLabel("Password");
   private JLabel sqlCommand = new JLabel("Enter a SQL Command");
   private JLabel executionResults = new JLabel("SQL Execution Result(s)");
   private JLabel usernameLabel = new JLabel("Username");
   private JComboBox driverList = new JComboBox(drivers);
   private JComboBox databaseURLList = new JComboBox(databases);
   private JTextField usernameField = new JTextField(10);
   private JScrollPane resultsPane = new JScrollPane();
   private JPasswordField passwordField = new JPasswordField(10);
   private JTextArea queryArea = new JTextArea(6, 10);
   
   //create ResultSetTableModel object
   private CreateResults tableModel;

   // create ResultSetTableModel and GUI in this constructor
   public DisplayResults(){   
	   
	  //Layout all GUI components
      super("Stark's Database Client");
      
      setLayout(new GridLayout(2,1)); //Main frame has two main sections (top and bottom row)

      topPanel.setLayout(new GridLayout(1,2, 30, 30));
      bottomPanel.setLayout(new BorderLayout(10,10));
      leftPanel.setLayout(new BorderLayout(10,10));
      rightPanel.setLayout(new BorderLayout(10, 10));
      informationPanel.setLayout(new GridLayout(4,2,6,6));
      buttonPanel.setLayout(new GridLayout(1,3, 5, 5));
          
      queryArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      queryArea.setLineWrap(true);
      
      font = databaseInstruction.getFont();
      italicFont = new Font(font.getFontName(), Font.ITALIC, 16);
      databaseInstruction.setFont(italicFont);
      sqlCommand.setFont(italicFont);
      
      //Add three button objects to buttonPanel
      buttonPanel.add(connect);
      buttonPanel.add(execute);
      buttonPanel.add(clearCommand);
      
      //Add 8 objects to informationPanel
      informationPanel.add(databaseDriver);
      informationPanel.add(driverList);
      informationPanel.add(databaseURL);
      informationPanel.add(databaseURLList); 
      informationPanel.add(usernameLabel);
      informationPanel.add(usernameField);
      informationPanel.add(passwordLabel);
      informationPanel.add(passwordField);
      
      //Add 3 panels to the leftPanel
      leftPanel.add(databaseInstruction, BorderLayout.NORTH);
      leftPanel.add(informationPanel, BorderLayout.CENTER);      
      leftPanel.add(connectionStatus, BorderLayout.SOUTH);
      
      //Add 3 panels to the rightPanel
      rightPanel.add(sqlCommand, BorderLayout.NORTH);
      rightPanel.add(queryArea, BorderLayout.CENTER);
      rightPanel.add(buttonPanel, BorderLayout.SOUTH);
      
      //Add the previous two panels to the top container panel
      topPanel.add(leftPanel); 
      topPanel.add(rightPanel);
      
      //Add 3 objects to the bottomPanel
      bottomPanel.add(executionResults, BorderLayout.NORTH);
      bottomPanel.add(resultsPane, BorderLayout.CENTER);
      bottomPanel.add(clearResults, BorderLayout.SOUTH);
      
      //Add some padding / margins between the two main panels
      topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
      //Finally, add the top and bottom containers to the main frame
      add(topPanel);
      add(bottomPanel);
      
      //Both lists initially have nothing selected
      driverList.setSelectedItem(null);
      databaseURLList.setSelectedItem(null);
      
      //Edit the header information (blue)
      databaseInstruction.setForeground(new Color(0,68,255));
      connectionStatus.setForeground(Color.red);

      //Select JDBC driver from list
      driverList.addActionListener(new ActionListener() {
    	  public void actionPerformed(java.awt.event.ActionEvent evt){
    		  JDBC_DRIVER = (String)driverList.getSelectedItem();
    	  }});                                
      
      //Select Database URL from list
      databaseURLList.addActionListener(new ActionListener() {   
    	  public void actionPerformed(ActionEvent evt){
    		  DATABASE_URL = (String)databaseURLList.getSelectedItem();
    	  }});
      
      //actionListener for connectButton to connect to the database
      connect.addActionListener(new ActionListener() {    
    	  public void actionPerformed(ActionEvent evt) {
    		  String USERNAME = usernameField.getText(); //retrieve user name and password from fields
    		  @SuppressWarnings("deprecation")
			  String PASSWORD = passwordField.getText();
    		  try{
    			   tableModel = new CreateResults(JDBC_DRIVER, DATABASE_URL, USERNAME, PASSWORD);
    		  	} //try end
    			   catch (ClassNotFoundException classNotFound){
    		           JOptionPane.showMessageDialog(null, "MySQL driver not found. Application terminating.", "Driver not found", JOptionPane.ERROR_MESSAGE );
    		           System.exit(1); //terminate application
    		        } //end catch;
    		       catch (SQLException sqlException){
    		           JOptionPane.showMessageDialog(null, sqlException.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    		           //ensure database connection is closed
    		           tableModel.disconnectFromDatabase();
    		           System.exit(1); //terminate application
    		        } //end catch
    		       connectionStatus.setForeground(new Color(17,150,19)); //RGB green declaration
    	    	   connectionStatus.setText("Connected to " + DATABASE_URL);
    	  }}); //end connect.addActionListener                                 

      //actionListener to clear the command window
      clearCommand.addActionListener(new java.awt.event.ActionListener() {   
    	  public void actionPerformed(ActionEvent evt) {
    		  queryArea.setText("");
    	  }}); //end clearCommand.addActionListener                                        

      //actionListener to execute the SQL command
      execute.addActionListener(new java.awt.event.ActionListener() { 
    	  public void actionPerformed(java.awt.event.ActionEvent evt) {
    		  command = queryArea.getText();
    		  if (command.substring(0,6).equals("select")){
    			  try{
    				  tableModel.setQuery(command);
    				  createTable();
    			  } //try end
    			  catch (SQLException sqlException){
    				  JOptionPane.showMessageDialog(null, sqlException.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    				  //ensure database connection is closed
    				  tableModel.disconnectFromDatabase();
    				  System.exit(1); //terminate application
    			  } //end catch
    		  	}//end if
    		  
    		  if (command.substring(0,6).equals("update") || command.substring(0,6).equals("delete") || command.substring(0,6).equals("insert")){
    			  try{
    				  rowsAffected = tableModel.setUpdate(command);
    				  
    				  
    				  
    			  } //try end
    			  catch (SQLException sqlException){
    				  JOptionPane.showMessageDialog(null, sqlException.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
    				  //ensure database connection is closed
    				  tableModel.disconnectFromDatabase();
    				  System.exit(1); //terminate application
    			  } //end catch
    			  
    		  }  
	  		}}); //end execute.addActionListener                                         

      //actionListener to clear the results table after SQL command execution
      clearResults.addActionListener(new java.awt.event.ActionListener() { 
    	  public void actionPerformed(java.awt.event.ActionEvent evt) {
    		  clearTable(); //Call clearTable function
    	  }}); //end clearResults.addActionListener  
  }// end DisplayResults() constructor                      
   
   //method for clearing the JTable
   public void clearTable(){
	   JTable clear = new JTable();
	   resultsPane.getViewport().add(clear);
   		} //clearTable end
   
   //method for creating and retrieving results into the table
   public void createTable(){ //method for creating JTable
	   JTable resultTable = new JTable(tableModel); //create resultTab
	   resultsPane.getViewport().add(resultTable);
       } //createTable end
   
   //main trigger
   public static void main( String args[] ){  
     DisplayResults ResultsWindow = new DisplayResults();    
     ResultsWindow.setSize(900,300);
     ResultsWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
     ResultsWindow.setLocationRelativeTo(null);
     ResultsWindow.setMinimumSize(new Dimension(900, 400));
     ResultsWindow.setVisible(true);
   } // end main
} // end class DisplayQueryResults