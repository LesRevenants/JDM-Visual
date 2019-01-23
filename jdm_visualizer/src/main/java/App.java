import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import configuration.MasterStore;



public class App {

	public static void help() {
		System.out.println("mode config_file");
		System.out.println("mode: \n\t--start: port");
		System.out.println("mode: \n\t--init: data_dir");

	}
	
	public static void main(String[] args) throws Exception {
		if(args.length < 2) {
			help();
			return;
		}
		String mode = args[0];
		String config_file = args[1];
		System.out.println(Arrays.asList(args).toString());
		
		MasterStore masterStore = new MasterStore(config_file);
		if(mode.equals("--init")) {
			if(args.length < 3 ) {
				help();
				return;
			}
			String dataDirPath = args[2];
			masterStore.init(dataDirPath);
		}
		else if(mode.equals("--update")) {
			
		}
		else if(mode.equals("--start")) {
			if(args.length < 3 ) {
				help();
				return;
			}
			int port = Integer.parseInt(args[2]);
			runServer(port,masterStore);
		}
	}
	
	public static void runServer(int port,MasterStore masterStore)  {
		 ServerSocket listenSock; //the listening server socket
         Socket sock;             //the socket that will actually be used for communication
         try {
             listenSock = new ServerSocket(port);
             System.out.println("Waiting query ");
             while (true) {       //we want the server to run till the end of times
           	 
                sock = listenSock.accept();   //will block until connection received
                
                BufferedReader br =  new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter bw =  new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                char[] buffer = new char[4096];
                int read_status = br.read(buffer);
                if(read_status == -1 || read_status > 0)  { // ensure that read not fail
                	String query = new String(buffer);
                	String resultsStr = masterStore.query(query);
                	bw.write(resultsStr);  
	                bw.close();
	                br.close();
	                sock.close(); 
            	}

            }
        } catch (Exception ex) {
        	ex.printStackTrace();
      }
	}

}


