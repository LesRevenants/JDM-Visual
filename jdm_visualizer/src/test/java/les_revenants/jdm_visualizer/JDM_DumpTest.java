package les_revenants.jdm_visualizer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.junit.BeforeClass;
import org.junit.Test;

public class JDM_DumpTest {
	
	
	
	static String[] dataPaths = {
			
			"data/12282017-LEXICALNET-JEUXDEMOTS-FR-NOHTML.txt",
			
//			"/media/user/0fe0a46f-361a-423b-9048-f855f5456a02/"
//			+ "Work/Fac/M1 DECOL/TER M1 2018/Datas/JDM/JDM/12282017-LEXICALNET-JEUXDEMOTS-FR-NOHTML.txt",
			
//			"/media/user/0fe0a46f-361a-423b-9048-f855f5456a02/"
//			+ "Work/Fac/M1 DECOL/TER M1 2018/Datas/JDM/JDM/11102017-JEUXDEMOTS-DEFS.txt",
			
			
	};
	
	@BeforeClass
    public static void setUp() {
		
	}
	
	@Test
	public void testLoad() throws IOException {
//		int[] sizes = { 1000,4096,8192,65536,256000,1000000 };
		int[] sizes = { 65536 };

		 System.out.println("testLoad");

		
		for(int buffSize : sizes) {
			for(String dataPath : dataPaths) {
				Instant t1 = Instant.now();
				BufferedReader reader = new BufferedReader(new FileReader(dataPath),buffSize);
				String line;
				long lineNb = 0, totalSize = 0;
				boolean cond = true;
				
				while(cond && (line = reader.readLine()) != null) {
					lineNb++;
					totalSize += line.length();
					String parts[] = line.split("|");
					if(lineNb % 10000000 == 0) {
//						cond = false;	
						long ellapsedTime = Duration.between(t1,Instant.now()).toMillis();
						System.out.println("\t"+lineNb + ":" + ellapsedTime+"ms");
					}
				}
				reader.close();
				long ellapsedTime = Duration.between(t1,Instant.now()).toMillis();
				System.out.println(totalSize +":"+buffSize + ":"+dataPath+":"+ ellapsedTime+"ms");
			}
			
		}
		
	}
	
//	@Test
//	public void testLoad2() throws IOException {
//		
//		 System.out.println("testLoad2");
//		
//		 Instant t1 = Instant.now();			
//	     byte[] buffer= new byte[2048*1024*64]; 	
//	     long lineNb = 0, totalSize = 0;
//	     		 BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(dataPaths[0])));
//	     while (inputStream.read(buffer)!=-1){
//	    	 totalSize += buffer.length;
//	    	 String str = new String(buffer);
//	     } 
//	     
//		long ellapsedTime = Duration.between(t1,Instant.now()).toMillis();
//	     System.out.println(totalSize +":"+ ellapsedTime+"ms");
//	}

}
