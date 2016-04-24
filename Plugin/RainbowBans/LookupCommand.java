package RainbowBans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import joebkt.PlayerList;
import PluginReference.MC_Command;
import PluginReference.MC_Player;

public class LookupCommand implements MC_Command {

	@Override
	public List<String> getAliases() {
		ArrayList<String> ls = new ArrayList<String>();
		ls.add("rblookup");
		return ls;
	}

	@Override
	public String getCommandName() {
		return "lookup";
	}

	@Override
	public String getHelpLine(MC_Player plr) {
		return "/lookup help for more lookup information";
	}

	@Override
	public List<String> getTabCompletionList(MC_Player plr, String[] array) {
		List<String> ls = new ArrayList<String>();
		if(array.length < 1){
			if(array[1].startsWith("n") || array[1].startsWith("N") || array[1].startsWith("a") || array[1].startsWith("A"))ls.add(array[0] + " name");
			else if(array[1].startsWith("i") || array[1].startsWith("I") || array[1].startsWith("p") || array[1].startsWith("P"))ls.add(array[0] + " ip");
			else {} //Ignore
		}else{
			//Ignore
		}
		return ls;
	}

	@Override
	public void handleCommand(MC_Player plr, String[] args) {
		if(args.length > 0){
		if(args[0].equals("help")){
			showHelpPage(plr);
		}
		else if(args[0].equals("test")){
			if(testFileExistanceAndConnections())parseMessage("Method works!", plr);
			else parseMessage("Method has an error!", plr);
		}else{
			if(args.length > 1){
				//Can proceed with command
				System.out.println("[RainbowBans] Processing lookup command");
				String similar = findSimilarOrSame(args[1]);
				if(similar !=null){
				if(similar.equalsIgnoreCase("ip")){
					try {
						String ips = new String(Files.readAllBytes(Paths.get(PlayerList.fileBannedIPs.toURI())));
						if(ips.contains(args[0])){
							parseMessage("IP " + args[0] + " is banned!", plr);
						}else{
							parseMessage("IP " + args[0] + "is not banned!", plr);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else if(similar.equalsIgnoreCase("name")){
					try {
						String ips = new String(Files.readAllBytes(Paths.get(PlayerList.fileBannedPlayers.toURI())));
						if(ips.contains(args[0])){
							parseMessage("Player " + args[0] + " is banned!", plr);
						}else{
							parseMessage("Player " + args[0] + "is not banned!", plr);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					showHelpPage(plr);
				}
				}
			}else{
				parseMessage("One argument short!", plr);
				showHelpPage(plr);
			}
		}
		}else{
			parseMessage("No arguments!", plr);
			showHelpPage(plr);
		}
	}
	@Override
	public boolean hasPermissionToUse(MC_Player plr) {
		return parsePermission("RainbowBans.lookup", plr);
	}

	private String findSimilarOrSame(String chk) {
		//First check is to see if the order of the letters is wrong.
		//Remember, they could've typed it correctly! So the following just makes sure all the right letters are there
		//It also means that if the user accidently doubles up on a letter or types an extra letter that shouldn't be there, its still parsed correctly anyway
		if(chk.equalsIgnoreCase("name")) return "name";
		if(chk.equalsIgnoreCase("ip")) return "ip";
		if(chk.contains("i") && chk.contains("p")){
			return "ip";
		}else if(chk.contains("n") && chk.contains("a") && chk.contains("m") && chk.contains("e")){
			return "name";
		}else{
			if(checkKeyboardRadius("ip", chk)){
				return "ip";
			}else if(checkKeyboardRadius("name", chk)){
				return "name";
			}else{
				return null;
			}
		}
	}

	/**
	 * Checks a string by looking at the letters around it on a standard keyboard and seeing if switching one of the letters with one near the keyboard gives the target word.
	 * If more than one of the letters are wrong, it should still be interpreted correctly.
	 * Though if it's not that's still your fault for spelling it wrong.
	 * @param check the string that the method is trying to bring the incorrect method to.
	 * @param input the gibberish the method is trying to decipher
	 * @return true if the input can be converted successfully, false if it is truly bad spelling
	 */
	private boolean checkKeyboardRadius(String check, String input) {
		for(int ii = 1; ii < input.length(); ii++){
			String real = check.substring(ii, ii + 1);
			String maybe = input.substring(ii, ii + 1);
			if(real.equalsIgnoreCase(maybe)) continue;
			else{
				//It's probably better to put it in top-bottom order, but I'll put it in alphabetical order so you know what I mean
				if(maybe.equalsIgnoreCase("A")){
					String[] a = {"q", "w", "s", "z"};
					for(int e = 0; e < a.length; e++){
					if(real.equalsIgnoreCase(a[e])) continue;
					else return false;
					}
				}else if(maybe.equalsIgnoreCase("B")){
					String[] b = {"v", "g", "h", "n"}; //To make it easier for anyone who decides to read this code, I'll even name the variables for you!
					for(int e = 0; e < b.length; e++){
						if(real.equalsIgnoreCase(b[e])) continue;
						else return false;
				}
				}else if(maybe.equalsIgnoreCase("C")){
					String[] c = {"x", "d", "f", "v"};
					for(int e = 0; e < c.length; e++){
						if(real.equalsIgnoreCase(c[e])) continue;
						else return false;
				}
				}else if(maybe.equalsIgnoreCase("D")){
					String[] d = {"s", "e", "r", "f", "c", "x"};
					for(int e = 0; e < d.length; e++){
						if(real.equalsIgnoreCase(d[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("E")){
					String[] e = {"3", "4", "r", "d", "s", "w"}; //YES I check numbers too.
					for(int a = 0; a < e.length; a++){
						if(real.equalsIgnoreCase(e[a])) continue;
						else return false;
					}
					
				}else if(maybe.equalsIgnoreCase("F")){
					String[] f = {"d", "r", "t", "g", "v", "c"};
					for(int e = 0; e < f.length; e++){
						if(real.equalsIgnoreCase(f[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("G")){
					String[] g = {"t", "y", "h", "b", "v", "f"};
					for(int e = 0; e < g.length; e++){
						if(real.equalsIgnoreCase(g[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("H")){
					String[] h = {"y", "u", "j", "n", "b", "g"};
					for(int e = 0; e < h.length; e++){
						if(real.equalsIgnoreCase(h[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("I")){
					String[] i = {"t", "y", "h", "b", "v", "f"};
					for(int e = 0; e < i.length; e++){
						if(real.equalsIgnoreCase(i[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("J")){
					String[] j = {"h", "u", "i", "k", "m", "n"};
					for(int e = 0; e < j.length; e++){
						if(real.equalsIgnoreCase(j[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("K")){
					String[] k = {"i", "o", "l", ",", "m", "j"}; //Punctuation is also checked
					for(int e = 0; e < k.length; e++){
						if(real.equalsIgnoreCase(k[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("L")){
					String[] l = {"o", "p", ";", ".", ",", "k"};
					for(int e = 0; e < l.length; e++){
						if(real.equalsIgnoreCase(l[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("M")){
					String[] m = {"j", "k", ",", "n"};
					for(int e = 0; e < m.length; e++){
						if(real.equalsIgnoreCase(m[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("N")){
					String[] n = {"b", "h", "j", "m"};
					for(int e = 0; e < n.length; e++){
						if(real.equalsIgnoreCase(n[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("O")){
					String[] o = {"9", "0", "p", "l", "k", "i"};
					for(int e = 0; e < o.length; e++){
						if(real.equalsIgnoreCase(o[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("P")){
					String[] p = {"0", "-", "[", ";", "l", "o"};
					for(int e = 0; e < p.length; e++){
						if(real.equalsIgnoreCase(p[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("Q")){
					String[] q = {"1", "2", "w", "a"};
					for(int e = 0; e < q.length; e++){
						if(real.equalsIgnoreCase(q[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("R")){
					String[] r = {"4", "5", "t", "f", "d", "e"};
					for(int e = 0; e < r.length; e++){
						if(real.equalsIgnoreCase(r[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("S")){
					String[] s = {"w", "e", "d", "x", "z", "a"};
					for(int e = 0; e < s.length; e++){
						if(real.equalsIgnoreCase(s[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("T")){
					String[] t = {"5", "6", "y", "g", "f", "r"};
					for(int e = 0; e < t.length; e++){
						if(real.equalsIgnoreCase(t[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("U")){
					String[] g = {"7", "8", "i", "j", "h", "y"};
					for(int e = 0; e < g.length; e++){
						if(real.equalsIgnoreCase(g[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("V")){
					String[] v = {"f", "g", "b", "c"};
					for(int e = 0; e < v.length; e++){
						if(real.equalsIgnoreCase(v[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("W")){
					String[] w = {"2", "3", "e", "s", "a", "q"};
					for(int e = 0; e < w.length; e++){
						if(real.equalsIgnoreCase(w[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("X")){
					String[] x = {"s", "d", "c", "z"};
					for(int e = 0; e < x.length; e++){
						if(real.equalsIgnoreCase(x[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("Y")){
					String[] y = {"6", "7", "u", "h", "g", "t"};
					for(int e = 0; e < y.length; e++){
						if(real.equalsIgnoreCase(y[e])) continue;
						else return false;
					}
				}else if(maybe.equalsIgnoreCase("Z")){
					String[] z = {"a", "s", "x"};
					for(int e = 0; e < z.length; e++){
						if(real.equalsIgnoreCase(z[e])) continue;
						else return false;
					}
				}
			}
		}
		return true;
	}

	private boolean parsePermission(String perm, MC_Player plr) {
		if(plr !=null){
			return plr.hasPermission(perm);
		}else{
			return true;
		}
	}

	private boolean testFileExistanceAndConnections() {
		return PlayerList.fileBannedPlayers.canRead() && PlayerList.fileBannedIPs.canRead();
	}

	private void showHelpPage(MC_Player plr) {
		parseMessage("The command syntax is /lookup <name:IP> 'name':'IP'", plr, "/");
		parseMessage("For example, if I wanted to find ban details for user myname1325, I would do '/lookup myname1325 name'.", plr, "/");
		parseMessage("The server would then look up whether that player is banned or not.", plr);
		parseMessage("In future versions, you can get the server to give you more information.", plr);
	}

	private void parseMessage(String mes, MC_Player plr, String character) {
		if(plr !=null){
			plr.sendMessage(mes);
		}else{
			System.out.println(mes.replace(character, "")); //Remove the character
		}
		
	}

	private void parseMessage(String mes, MC_Player plr) {
		parseMessage(mes, plr, "");
	}

}
