

import java.io.BufferedReader;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {


	private static ServerSocket socket;
    private static Socket connection;
    private static String command;
	private static String responseStr ;

	public static void main(String[] args) {

		connectJava2Php(Integer.parseInt(args[2]),null);

	}

	public static void connectJava2Php(int port, Properties properties) {
		          ServerSocket listenSock; //the listening server socket
		          Socket sock;             //the socket that will actually be used for communication
		          try {
		              listenSock = new ServerSocket(port);
		              System.out.println("Waiting query ");
		              while (true) {       //we want the server to run till the end of times
		            	 
		                 sock = listenSock.accept();             //will block until connection recieved
		                 
		                 BufferedReader br =  new BufferedReader(new InputStreamReader(sock.getInputStream()));
		                 BufferedWriter bw =  new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		                 String article = br.readLine();
		                 if(article.equals("Ok")) 
		                 {
			                 int max_size=1000;
//			                 String content = FileUtils.readFileToString(new File("././Visual/json/query.json"),StandardCharsets.UTF_8);
//			                 System.out.println(content);
//			                 Relation[] relations = dostuff();
//			                 bw.write(relations);
			                 bw.close();
			                 br.close();
			                 sock.close(); 
		             	}

		             }
		         } catch (IOException ex) {
             ex.printStackTrace();
		       }
		    
		}

	

}