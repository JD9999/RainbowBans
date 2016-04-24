package RainbowBans;

//Just a few imports
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import sun.security.action.GetPropertyAction;
import sun.tools.attach.BsdAttachProvider;
import sun.tools.attach.LinuxAttachProvider;
import sun.tools.attach.SolarisAttachProvider;
import sun.tools.attach.WindowsAttachProvider;
import PluginReference.MC_Server;
import PluginReference.PluginBase;
import PluginReference.PluginInfo;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.spi.AttachProvider;


public class MyPlugin extends PluginBase{

public static MC_Server server = null;	 
public static String pluginDir = "plugins_mod" + File.separatorChar;
public static String folderDir = pluginDir + "RainbowBans" + File.separatorChar;
public String OS = "Unknown!";
public String pid = "0";
public ProcessBuilder process;
public boolean isObtained = false;
public Logger errorlogger;

public void onStartup(MC_Server svr){
	errorlogger = Logger.getLogger(true);
	System.out.println("Plugin starting! Lets hope this works! :)");
	System.out.println("plugins_mod folder located at:" + new File(pluginDir).getAbsolutePath());
	System.out.println("RainbowBansTransAgent is located at: " + new File(pluginDir + "RainbowBansTransAgent.jar").getAbsolutePath());
	server = svr;	
	System.out.println("Creating files now!");
	File file = new File(folderDir);
	if(file.isDirectory()) System.out.println("Directory already exists!");
	else {
		System.out.println("Creating plugin directory");
		file.mkdir();
	}	
	File errorfile = new File(folderDir + File.separatorChar + "Rainbowbanserrors.txt");
	if(errorfile.isFile()){
		System.out.println("Stack trace file already exists!");
		errorfile.delete();
	}else{
		System.out.println("Creating stack trace file!");
	}
		try {	
			errorfile.createNewFile();
		} catch (Throwable t) {
			errorlogger.writeException(t);
		}
	
	File pidfile = new File(folderDir + File.separatorChar + "pid.txt");
	if(pidfile.isFile()) System.out.println("File pid already exists");
	else
		try {
			System.out.println("Creating pid file!");
			pidfile.createNewFile();
		} catch (Throwable t) {
			errorlogger.writeException(t);
		}
	
	File banfile = new File(folderDir + File.separatorChar + "banmessage.txt");
	if(banfile.isFile()) System.out.println("Banfile already exists!");
	else
		try {
			banfile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(banfile));
			writer.write("You are banned.");
			writer.close();
		} catch (Throwable t) {
			errorlogger.writeException(t);
		}
	File logfile = new File(folderDir + File.separatorChar + "Rainbowbanstransagentlog.txt");
	if(logfile.isFile()){
		logfile.delete();
		try{
		logfile.createNewFile();
	}catch(Throwable t){
		errorlogger.writeException(t);
	}
	}
	VirtualMachine vm;
	
	String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win") && !os.contains("darwin")) {
    	OS = "windows";
    }
    else if ((os.contains("nix")) || (os.contains("nux")) || (os.indexOf("aix") > 0)) {
    	OS = "linux";
    }
    else if (os.contains("mac")) {
    	OS = "mac";
    }
    else if (os.contains("sunos")) {
    	OS = "solaris";
    }
    else{
    }
	
	
	try {
		String realArch = architecture();
		if(realArch == "64"){
			try {
				addToLibPath(new File(folderDir + "Natives" + File.separator + "64" + File.separator + OS + File.separator).getAbsolutePath());
			} catch (/*NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e*/ Throwable t) {
				errorlogger.writeException(t);
			}
		}else if(realArch == "32"){
			try {
				addToLibPath(new File(folderDir + "Natives" + File.separator + "32" + File.separator + OS + File.separator).getAbsolutePath());
			} catch (/*NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e*/ Throwable t) {
				errorlogger.writeException(t);
			}
		}else{
			System.err.println("Unknown architecture! Please report!");
		}
		System.out.println("Current library path: " + System.getProperty("java.library.path"));
		System.loadLibrary("attach"); //They are all called attach, no need to worry about file extension or OS or architecture.
		System.out.println(observePID());
			vm = getAttachProvider().attachVirtualMachine(observePID());
		System.out.println("Loading agent!");
			vm.loadAgent(new File(pluginDir + "RainbowBansTransAgent.jar").getAbsolutePath());
	} catch (/*AttachNotSupportedException | IOException  | AgentLoadException | AgentInitializationException e*/ Throwable t) {
		errorlogger.writeException(t);
	}
	//Before using the attach agent, this is how I started the premain in the external JAR (RainbowBansTransAgent.jar)//
	/*File transagent = new File(pluginDir + File.separatorChar + "RainbowBansTransAgent.jar");
	ProcessBuilder pb = new ProcessBuilder("java", "-javaagent:" + transagent.getAbsolutePath(), "RainbowBansTransAgent/TransAgent");
    pb.redirectError(Redirect.appendTo(errorfile));
    pb.redirectOutput(Redirect.appendTo(logfile));
    try{
        pb.start();
    }catch(IOException e){
        e.printStackTrace();
    }
    */
	//Make the GUI window
		JFrame frame = startDialog();
		JMenuBar menu = createMenu();
		frame.setJMenuBar(menu);
		frame.add(getSplitPane());
		frame.setVisible(true);
}
/**
 * Obtains Rainbow's PID
 * @return the string representing the PID
 */
private String observePID() {
	String jvm = ManagementFactory.getRuntimeMXBean().getName();
	String pid = jvm.substring(0, jvm.indexOf('@'));
	return pid;
}
public void onShutdown(){
	System.out.println("RainbowBans shutting down!");
}
public PluginInfo getPluginInfo(){ 
	PluginInfo info = new PluginInfo();
	info.description = "A ban management plugin: version A0.2 started in 1.8 mode.";
	return info;
}

//I got the code from http://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
/**
 * Opens a website in the computer's default browser
 * @param url
 */
private void openWebsite(String url){
	Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(new URL(url).toURI());
        } catch (Throwable t) {
        	errorlogger.writeException(t);
        }
    }
}
/**
 * Obtains an ActionListener object for launching a URL. See method above for execution.
 * @param url the URL to open
 * @return The associated ActionListener object.
 */
private ActionListener getActionListenerForWebsites(String url) {
	ActionListener al = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			openWebsite(url);
		}
		
	};
	return al;
}
/**
 * Obtains an ActionListener object for when the server owner presses the "see message" button on the GUI console
 * @param i The box which has the text I want to change.
 * @return The associated ActionListener object
 */
private ActionListener getActionListenerForMessage(JMenuItem i) {
	ActionListener al = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(new File(folderDir + File.separatorChar + "banmessage.txt")));
					String message = reader.readLine();
					i.setText(message);
					Timer timer = new Timer();
					timer.schedule(getTimerTask(i), 5000);
					reader.close();
				} catch (Throwable t) {
					errorlogger.writeException(t);
				}
			}
			
	};
	return al;
}
/**
 * Obtains a TimerTask object for the message above.
 * @param i the box which has the text I want to temporarily change
 * @return
 */
private TimerTask getTimerTask(JMenuItem i) {
	TimerTask task = new TimerTask(){
		@Override
		public void run() {
			i.setText("See message");
		}
	};
	return task;
}

/**
 * Starts the GUI console main frame
 * @return A JFrame object representing the currently invisible window
 */
private JFrame startDialog() {
	JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(JOptionPane.YES_NO_OPTION);
	frame.setSize(800, 600);
	frame.setAlwaysOnTop(false);
	frame.setBackground(java.awt.Color.yellow);
	return frame;
}
/**
 * Creates the menu on the top of the GUI window
 * @return the JMenuBar object.
 */
private JMenuBar createMenu() {
	JMenuBar menu = new JMenuBar();
	JMenuItem i = new JMenuItem("See message");
	i.addActionListener(getActionListenerForMessage(i));
	menu.add(i);
	JMenuItem w = new JMenuItem("Go to project page");
	w.addActionListener(getActionListenerForWebsites("http://www.project-rainbow.org/site/index.php?board=9.0"));
    menu.add(w);
    JMenuItem k = new JMenuItem("Go to download page");
    k.addActionListener(getActionListenerForWebsites("http://www.project-rainbow.org/site/index.php?action=downloads;cat=3"));
    menu.add(k);
	return menu;	
}
/**
 * Gets the split pane that splits between the error logger and the information logger
 * @return JSplitPane the split pane.
 */
private JSplitPane getSplitPane(){
	JSplitPane frame = new JSplitPane();
	JScrollPane errorPane = new JScrollPane(getErrorLogger());
	JScrollPane loggerPane = new JScrollPane(getTransAgentLogger());
	frame.setLeftComponent(errorPane);
	frame.setRightComponent(loggerPane);
	frame.setDividerLocation(0.5);
	return frame;
}
/**
 * Gets the transagent logger as a JTextPane
 * @return JTextPane the information logger's contents in a text pane
 */
private JTextPane getTransAgentLogger() {
	JTextPane text = new JTextPane();
	text.setEditable(false);
	text.setText(readTransAgentLogger());
	JLabel label = new JLabel();
	label.setText("RainbowBansTransAgent logger - logger information");
	text.add(label);
	return text;
}
/**
 * Gets the contents of the Rainbowbanstransagentlog.txt
 * @return The contents of the file.
 */
private String readTransAgentLogger() {
	Path p = Paths.get(new File(folderDir + File.separator + "Rainbowbanstransagentlog.txt").toURI());
	byte[] bytes;
	try {
		bytes = Files.readAllBytes(p);
		return new String(bytes);
	} catch (IOException e) {
		return e.getMessage();
	}
}
/**
 * Gets the error logger as a JTextPane
 * @return JTextPane the error logger's contents in a text pane
 */
private JTextPane getErrorLogger() {
	JTextPane text = new JTextPane();
	text.setEditable(false);
	text.setText(readTransAgentErrors());
	JLabel label = new JLabel();
	label.setText("RainbowBansTransAgent logger - error information");
	text.add(label);
	return text;
}
/**
 * Gets the contents of the Rainbowbanerrors.txt
 * @return The contents of the file.
 */
private String readTransAgentErrors() {
	Path p = Paths.get(new File(folderDir + File.separator + "Rainbowbanserrors.txt").toURI());
	byte[] bytes;
	try {
		bytes = Files.readAllBytes(p);
		return new String(bytes);
	} catch (IOException e) {
		return e.getMessage();
	}
}
//Copied from CodeCrafter's MultiWorld project.
/**
 * Gets the AttachProvider for the OS
 * @return An AttachProvider object
 */
private AttachProvider getAttachProvider() {
	String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win") && !os.contains("darwin")) {
    	OS = "windows";
        return new WindowsAttachProvider();
    }
    else if ((os.contains("nix")) || (os.contains("nux")) || (os.indexOf("aix") > 0)) {
    	OS = "linux";
        return new LinuxAttachProvider();
    }
    else if (os.contains("mac")) {
    	OS = "mac";
    	return new BsdAttachProvider();
    }
    else if (os.contains("sunos")) {
    	OS = "solaris";
    	return new SolarisAttachProvider();
    }
    else{
    	return null;
    }
}
	/**
	 * Adds a path to the library path
	 * @param path The path to add
	 * @throws NoSuchFieldException if Field called "sys_paths" does not exist
	 * @throws SecurityException if Field called "sys_path" cannot be accessed or if we cannot make it accessible
	 * @throws IllegalArgumentException if we cannot set "sys_path" to null
	 * @throws IllegalAccessException if we cannot set "sys_path" to null
	 */
    private void addToLibPath(String path) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
    	  {
    	    if (System.getProperty("java.library.path") != null) {
    	      System.setProperty("java.library.path", path + System.getProperty("path.separator") + System.getProperty("java.library.path"));
    	    } else {
    	      System.setProperty("java.library.path", path);
    	    }
    	    Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
    	    fieldSysPath.setAccessible(true);
    	    fieldSysPath.set(null, null);
    	  }
}
    
    
//Copied from Stack Overflow
    /**
     * Obtains the architecture for the computer
     * @return A string representing the architecture
     */
private String architecture() {
	String arch = System.getenv("PROCESSOR_ARCHITECTURE");
	String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

	String realArch = arch.endsWith("64")
	                  || wow64Arch != null && wow64Arch.endsWith("64")
	                      ? "64" : "32";
	return realArch;
}
}

class Logger{

	public static File logFile = new File(MyPlugin.folderDir + File.separator + "Rainbowbanstransagentlog.txt");
	public static File errorFile = new File(MyPlugin.folderDir + File.separator + "Rainbowbanserrors.txt");
	private File file;
	
	public Logger(File logFile2) {
		file = logFile2;
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

