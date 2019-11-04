package ecs160.visitor.astvisitors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashSet;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import ecs160.visitor.utilities.UtilReader;


public class StateCheckerVisitor extends ASTVisitor {
	private class CallCheckerVisitor extends ASTVisitor {
		private String _targetClassName = null;
		private String _methodName = null;
		private boolean _hasCalled = false;
		
		public CallCheckerVisitor(String targetClassName, String methodName) {
			this._targetClassName = targetClassName;
			this._methodName = methodName;
		}
		
		public boolean visit(MethodInvocation miv) {
			String methodName = miv.getName().toString();
			if(this._methodName.equals(methodName)) {
				this._hasCalled = true;
			}
			
			return true;
		}
		
		public boolean hasCalled() {
			return this._hasCalled;
		}
		
	}
	
	private String _contextName = null;
	private String _abstractName = null;
	private HashSet<String> _abstractMethods = new HashSet<String>();
	private boolean A_flag = false;
	private int B_answer = 0;
	
	static public StateCheckerVisitor setUpGrader(String contextPath, String contextName, String abstractPath, String abstractName) {
		File contextFile = new File(contextPath);
		File abstractFile = new File(abstractPath);
		
		String contextText = "";
		String abstractText = "";
		//TODO: exception handling convention in java?
		try {
			contextText = UtilReader.read(contextFile);
			abstractText = UtilReader.read(abstractFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
    	ASTParser contextParser = ASTParser.newParser(AST.JLS12); //Create a parser for a version of the Java language (12 here)
    	ASTParser abstractParser = ASTParser.newParser(AST.JLS12);
    	Map<String, String> options = JavaCore.getOptions(); //get the options for a type of Eclipse plugin that is the basis of Java plugins
    	options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_12); //Specify that we are on Java 12 and add it to the options...
    	
    	contextParser.setCompilerOptions(options); //forward all these options to our parser
    	abstractParser.setCompilerOptions(options);
    	
    	contextParser.setKind(ASTParser.K_COMPILATION_UNIT); //What kind of constructions will be parsed by this parser.  K_COMPILATION_UNIT means we are parsing whole files.
    	abstractParser.setKind(ASTParser.K_COMPILATION_UNIT);
    	
    	contextParser.setResolveBindings(true); //Enable looking for bindings/connections from this file to other parts of the program.
    	abstractParser.setResolveBindings(true);
    	
    	contextParser.setBindingsRecovery(true); //Also attempt to recover incomplete bindings (only can be set to true if above line is set to true).
    	abstractParser.setBindingsRecovery(true);
    	String[] classpath = { System.getProperty("java.home") + "/lib/rt.jar" }; //Link to your Java installation.
    	
    	contextParser.setEnvironment(classpath, new String[] { "" }, new String[] { "UTF-8" }, true);
    	abstractParser.setEnvironment(classpath, new String[] { "" }, new String[] { "UTF-8" }, true);
    	
    	contextParser.setSource(contextText.toCharArray()); //Load in the text of the file to parse.
    	abstractParser.setSource(abstractText.toCharArray());
    	
    	contextParser.setUnitName(contextFile.getAbsolutePath()); //Load in the absolute path of the file to parse
    	abstractParser.setUnitName(abstractFile.getAbsolutePath());
    	
    	CompilationUnit contextCU = (CompilationUnit) contextParser.createAST(null); //Create the tree and link to the root node.
    	CompilationUnit abstractCU = (CompilationUnit) abstractParser.createAST(null);
    	//We first traverse abstractCU to get all its methods except the constructor
    	//Then traverse contextCU to check matching
    	StateCheckerVisitor visitor = new StateCheckerVisitor(contextName, abstractName);
    	abstractCU.accept(visitor);
    	contextCU.accept(visitor);
    	
    	return visitor;
	}
	
	public StateCheckerVisitor(String contextName, String abstractName) {
		this._contextName = contextName;
		this._abstractName = abstractName;
	}
	
	public boolean visit(TypeDeclaration td) {
		//first check its name to decide whether it's abstract or context
		String name = td.getName().toString();
		if(name.equals(this._abstractName)) {
			//just add all the method names to the set
			MethodDeclaration[] mds = td.getMethods();
			for(MethodDeclaration md: mds) {
				this._abstractMethods.add(md.getName().toString());
			}
		}
		else if(name.equals(this._contextName)){
			MethodDeclaration[] mds = td.getMethods();
			//first check A_flag
			HashSet<String> contextMethods = new HashSet<String>();
			for(MethodDeclaration md: mds) {
				contextMethods.add(md.getName().toString());
			}
			//intersection
			contextMethods.retainAll(this._abstractMethods);
			if(contextMethods.size() == this._abstractMethods.size()) {
				//the context class has all the matching methods with those of abstract class
				this.A_flag = true;
			}
			else {
				this.A_flag = false;
			}
			
			for(MethodDeclaration md: mds) {
				if(contextMethods.contains(md.getName().toString())) {
					//just search within the intersection
					boolean hasCalled = this.callCheck(md, this._abstractName);
					if(hasCalled == true) {
						this.B_answer ++;
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean callCheck(MethodDeclaration md, String targetClassName) {
		//first initialize the singleton
		CallCheckerVisitor visitor = new CallCheckerVisitor(targetClassName, md.getName().toString());
		md.accept(visitor);
		return visitor.hasCalled();
	}
	
	public boolean gradeA() {
		return this.A_flag;
	}
	
	public int gradeB() {
		return this.B_answer;
	}
}
