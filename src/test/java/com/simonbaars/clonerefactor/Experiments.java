package com.simonbaars.clonerefactor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.ltk.core.refactoring.Change;

import junit.framework.TestCase;

public class Experiments extends TestCase {
	public void testEclipseExtract() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(("\n" + 
				"public class Clone1 {\n" + 
				"\n" + 
				"	public static void main(String[] args) {\n" + 
				"		System.out.println(\"I'm a clone1\");\n" + 
				"		System.out.println(\"I'm a clone2\");\n" + 
				"		System.out.println(\"I'm a clone3\");\n" + 
				"		System.out.println(\"I'm a clone4\");\n" + 
				"		System.out.println(\"I'm a clone5\");\n" + 
				"		System.out.println(\"I'm a clone6\");\n" + 
				"		System.out.println(\"I'm a clone7\");\n" + 
				"		System.out.println(\"I'm a clone8\");\n" + 
				"		System.out.println(\"I'm a clone9\");\n" + 
				"		System.out.println(\"I'm a clone10\");\n" + 
				"		System.out.println(\"I'm a clone11\");\n" + 
				"		System.out.println(\"I'm a clone12\");\n" + 
				"	}\n" + 
				"\n" + 
				"}\n" + 
				"").toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final org.eclipse.jdt.core.dom.CompilationUnit cu = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);
		try {
			Change c = new ExtractMethodRefactoring(cu, 182, 293).createChange(new IProgressMonitor() {
				
				@Override
				public void worked(int arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void subTask(String arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setTaskName(String arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setCanceled(boolean arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isCanceled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void internalWorked(double arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void done() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beginTask(String arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			});
			System.out.println(c);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
