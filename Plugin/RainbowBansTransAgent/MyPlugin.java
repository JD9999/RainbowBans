package RainbowBansTransAgent;

import PluginReference.*;

public class MyPlugin extends PluginBase{
	
	public void onStartup(MC_Server server){
		System.out.println("Plugin starting! lets hope this works! :)");
		/*if(BooleanKeys.premain) System.out.println("TransAgent launched!");
		else System.out.println("TransAgent did NOT launch!");
		*/
	}
	public void onShutdown(){
		System.out.println("RainbowBansTransAgent shutting down!");
	}
	public PluginInfo getPluginInfo(){
		PluginInfo info = new PluginInfo();
		info.description = "a jar file helping to do bytecode manipulation - V0.1 started for RainbowBans A0.2!";
		return info;
	}
}
