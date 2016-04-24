package RainbowBansTransAgent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.CheckClassAdapter;

public class RBClassChecker extends CheckClassAdapter {

	protected RBClassChecker(int api, ClassVisitor cv, boolean checkDataFlow) {
		super(api, cv, checkDataFlow);
		
	}

}
