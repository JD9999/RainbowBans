package joebkt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import com.mojang.authlib.GameProfile;

public class ClassToVisit {

	public static String getCauseOfBan(String playername){
		String cause = "Server";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getRainbowPath() + "banned-players.json")));
			Path path = Paths.get(getRainbowPath(), "banned-players.json");;
			byte[] filebytes = java.nio.file.Files.readAllBytes(path);
			String ln = new String(filebytes);
			System.out.println(ln);
			int beginIndex = ln.indexOf("myname1325");
			if(beginIndex < 0){
				System.out.println("could not find myname1325!");
				reader.close();
				return "Unknown";
			}
			String plrln = ln.substring(beginIndex, ln.length());
			String searchline = plrln.substring(0, plrln.indexOf("expires"));
			cause = plrln.substring((searchline.indexOf("source") + 10), (searchline.length() - 8));
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cause;	
	}
	
	private static String getRainbowPath() {
		return new File(".").getAbsoluteFile().getParentFile().getParent() + File.separator;
	}
	
	public static void checkIfShouldDisconnect(GameProfile var1, String var4){
		try{
		BufferedReader reader = new BufferedReader(new FileReader(new File("plugins_mod" + File.separator + "RainbowBans" + File.separator + "banmessage.txt")));
		var4 = reader.readLine().replaceAll("%PLAYER%", var1.getName().replaceAll("%ADMIN%", getCauseOfBan(var1.getName())).replaceAll("%CURRENTTIME%", new Date().toGMTString()));
		reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	}
