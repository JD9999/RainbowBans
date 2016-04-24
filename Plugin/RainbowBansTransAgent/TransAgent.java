package RainbowBansTransAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.jar.JarFile;

import joebkt.PlayerList;
import sun.security.action.GetPropertyAction;


public class TransAgent{

	public static TransAgent ta;
	public static File dir = new File("plugins_mod" + File.separator + "RainbowBans");
	public static ArrayList<Long> ln = new ArrayList<Long>();
	public static Exception randomexception;
	
	public static Logger logger = null;
	public static Logger errorLogger = null;

	public static void main(String[] args){
		System.out.println("JarFile looking at: " + getRainbowPath() + "Rainbow.jar");
		if(BooleanKeys.premain) logger.logString("Premain agent loaded"); 
		else{
			System.out.println("Premain agent not loaded!");
			ln.add(16185131914L);
			BooleanKeys.error = true;
			}
		if(BooleanKeys.transformer_loaded) logger.logString("Transformation agent loaded!");
		else{
			System.out.println("Transformation agent not loaded!");
			ln.add(16185131914L);
			BooleanKeys.error = true;
		}
		if(BooleanKeys.returned_bytes && BooleanKeys.error == false) logger.logString("Transformation worked successfully");
		else if (BooleanKeys.returned_bytes /*&& (IntegerKeys.line_Inserted_At != 409)*/) logger.logString("Transformation worked successfully, but code inserted at the wrong line");
		else if (BooleanKeys.error){
			errorLogger.logString("There was an error in the transformation! Please report the following:");
			errorLogger.logString("Error codes: ");
			for (int i = 0; i < ln.size(); i++){
				errorLogger.logString(String.valueOf(ln.get(i).longValue()));
			}
			if(ln.contains(14152061521144L)){
				errorLogger.logString("This indicates that the joebkt.PlayerList class could not be located!");		
			}else if (ln.contains(69125L)){	
				errorLogger.logString("This indicates that there was an error returning the inserted bytecode to the joebkt.PlayerList class!");
			}else if (ln.contains(16185131914L)){
				errorLogger.logString("This indicates that the transformation stopped either at the premain or the transformation agent!");
			}else if (ln.contains(31513169125L)){
				errorLogger.logString("This indicates an error in adding the code into the joebkt.PLayerList class! Stack trace:");
				errorLogger.writeException(randomexception);
			}else if (ln.contains(31211919161208L)){
				errorLogger.logString("This indicates an error with javassist getting to Rainbow.jar!");
			}else if (ln.contains(131549625L)){
				errorLogger.logString("This indicates the joebkt.PlayerList class could not be retransformed!");
			}else if(ln.contains(514201825L)){
				errorLogger.logString("This indicates that the JarEntry for the joebkt.PlayerList class could not be made!");
			}
			else{
				errorLogger.logString("Error code misspelt! Please report");
			}
		}else if (BooleanKeys.found_class){
			errorLogger.logString("At least the class was found. But there were errors in the process.");
			logger.logString("At least the class was found. But there were errors in the process.");
		}else{
			errorLogger.logString("NO IDEA WHAT HAPPENED!!!");
		}
		BooleanKeys.premain = false;
		BooleanKeys.fully_done = true;
	}
	public TransAgent(){
		ta = this;
	}
	
	public static void agentmain(String args, Instrumentation inst){
		premain(args, inst);
	}
	public static void premain(String agentArgs, Instrumentation inst) {
		BooleanKeys.premain = true;	
		logger = Logger.getLogger(false);
		errorLogger = Logger.getLogger(true);
		logger.logString("Loaded agent!");
		try {
			inst.appendToSystemClassLoaderSearch(new JarFile(getRainbowPath() + "Rainbow.jar", true));
		} catch (IOException e1) {
			errorLogger.writeException(e1);
		}
		try {
			inst.addTransformer(new Transformationer(), true);
			Class<?> classs = Class.forName("joebkt.PlayerList", false, PlayerList.class.getClassLoader());
			Class<?>[] classes = {classs};
			inst.retransformClasses(classes);
			BooleanKeys.premain_done = true;
			logger.logString("Agent transformed!");
		}catch (UnmodifiableClassException | ClassNotFoundException e){
			errorLogger.writeException(e);
		}
	}
	public static String getRainbowPath() {
		return new File(".").getAbsoluteFile().getParent() + File.separator;
	}
}
class BooleanKeys{
	public static boolean premain = false;
	public static boolean transformer_loaded = false;
	public static boolean premain_done = false;
	public static boolean error = false;
	public static boolean found_class = false;
	public static boolean returned_bytes = false;
	public static boolean fully_done = false;
	public static boolean startup = true;
}
class Logger{

	public static File logFile = new File(TransAgent.dir + File.separator + "Rainbowbanstransagentlog.txt");
	public static File errorFile = new File(TransAgent.dir + File.separator + "Rainbowbanserrors.txt");
	private File file;
	
	public Logger(File logFile2) {
		file = logFile2;
	}

	public PrintWriter getPrintWriter() throws FileNotFoundException{
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), Charset.forName("UTF-8")), false);
	}
	
	public static Logger getLogger(boolean error){
		if(error){
				Logger l = new Logger(errorFile);
				return l;
		}else{
				Logger l = new Logger(logFile);
				return l;		
		}
	}

	public void writeException(Throwable t) {
			logString(t.getMessage());
			StackTraceElement[] s = t.getStackTrace();
			for(int i = 0; i< s.length; i++){
				StackTraceElement e = s[i];
				logString("at    " + e.toString());
			}	
	}
	
	public void logString(String s){
		try {
			OutputStream out = new FileOutputStream(obtainFile(), true);
			OutputStreamWriter writer = new OutputStreamWriter(out, Charset.forName("UTF8"));
			writer.write(s);
			writer.write(((String)AccessController.doPrivileged(new GetPropertyAction("line.separator"))));
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	private File obtainFile() {
		return file;
	}
	
}
