package RainbowBansTransAgent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.*;

public class Transformationer implements ClassFileTransformer {
	
	/**
	 * Parse my class visitor to be written to the joebkt package for transforming.
	 * @param arg1 "joebkt.PlayerList" the class name to transform
	 * @param arg2 the current bytes of the joebkt package.
	 * @return cw.toByteArray() returns the new bytes for the joebkt package to write to
	 */
	public byte[] transform(String arg1, byte[] arg2){
		ClassReader cr = new ClassReader(arg2);
	    ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
	    ClassVisitor cv = new RBClassChecker(Opcodes.ASM5, cw, true);
	    cr.accept(new RBClassVisitor(cv), 0); 
	    return cw.toByteArray();
	}

	@Override
	public byte[] transform(ClassLoader arg0, String className, Class<?> arg2,
			ProtectionDomain arg3, byte[] arg4)
			throws IllegalClassFormatException {
		if(BooleanKeys.returned_bytes) return null;
		BooleanKeys.transformer_loaded = true;
		byte[] b = null;
		String realName = className.replaceAll("/", ".");
		if(realName.equals("joebkt.PlayerList")){
			TransAgent.logger.logString("Found class! Transformer ready!");
			BooleanKeys.found_class = true;
			b =  transform(realName, arg4);
			if(b !=null){
				BooleanKeys.returned_bytes = true;
				TransAgent.main(new String[0]);
		return b;
			}
			}
		return null;
	}

}
