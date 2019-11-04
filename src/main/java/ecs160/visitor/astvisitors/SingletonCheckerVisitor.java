package ecs160.visitor.astvisitors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;


import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;


import ecs160.visitor.utilities.UtilReader;

public class SingletonCheckerVisitor extends ASTVisitor {
	//assume field declarations come before any method declarations in any files you are testing
	//TODO:check the mutability problem
	//how does SingletonChekcer get instantiated?
	//answer: default value is null, same as instance variable
	private class InstCreateCheckerVisitor extends ASTVisitor{
		private boolean inIf = false;
		private boolean hasCreated = false;
		//this flag indicates whether the constructor be called more than once
		//or called outside of an ifStatement
		private boolean error_flag = false;
		private String _className = null;
		public InstCreateCheckerVisitor(String className) {
			super();
			this._className = className;
		}
		
		public boolean visit(IfStatement ifStatement) {
			this.inIf = true;
			return true;
		}
		
		public void endVisit(IfStatement ifStatement) {
			this.inIf = false;
		}
		
		//debug method
		public boolean visit(TypeDeclaration td) {
			System.out.println("You can't see this message!");
			System.out.println(td.getName().toString());
			return true;
		}
		
		public boolean visit(ClassInstanceCreation classInstCreate) {
			//first verify that the created object is of the correct type
			Type type = classInstCreate.getType();
			if(type instanceof SimpleType) {
				if(this._className.equals(((SimpleType) type).getName().toString())){
					if(this.hasCreated == true) {
						//created more than once
						this.error_flag = true;
						this.hasCreated = true;
					}
					else {
						//never created before
						//check if it is in ifStatement
						if(this.inIf == false) {
							this.error_flag = true;
							this.hasCreated = true;
						}
						else {
							//in ifStatement, first created
							this.hasCreated = true;
						}
					}
				}
			}
			
			return false;
		}
		
		public boolean hasCreated() {
			return this.hasCreated;
		}
		
		public boolean hasError() {
			return this.error_flag;
		}
	}
	
	//field in the host class
	private String _className = null;
	private boolean A_flag = false;
	private boolean B_flag = false;
	private boolean C_flag = false;
	private boolean D_flag = false;
	/*
	 * static factory method
	 * parse the AST and record any information needed to grade
	 * then return the visitor out
	 */
	static public SingletonCheckerVisitor setUpGrader(String sourceFile, String className) {
		
		File file = new File(sourceFile);
		String text = "";
		try {
			text = UtilReader.read(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
    	ASTParser parser = ASTParser.newParser(AST.JLS12); //Create a parser for a version of the Java language (12 here)
    	Map<String, String> options = JavaCore.getOptions(); //get the options for a type of Eclipse plugin that is the basis of Java plugins
    	options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_12); //Specify that we are on Java 12 and add it to the options...
    	parser.setCompilerOptions(options); //forward all these options to our parser
    	parser.setKind(ASTParser.K_COMPILATION_UNIT); //What kind of constructions will be parsed by this parser.  K_COMPILATION_UNIT means we are parsing whole files.
    	parser.setResolveBindings(true); //Enable looking for bindings/connections from this file to other parts of the program.
    	parser.setBindingsRecovery(true); //Also attempt to recover incomplete bindings (only can be set to true if above line is set to true).
    	String[] classpath = { System.getProperty("java.home") + "/lib/rt.jar" }; //Link to your Java installation.
    	parser.setEnvironment(classpath, new String[] { "" }, new String[] { "UTF-8" }, true);
    	parser.setSource(text.toCharArray()); //Load in the text of the file to parse.
    	parser.setUnitName(file.getAbsolutePath()); //Load in the absolute path of the file to parse
    	CompilationUnit cu = (CompilationUnit) parser.createAST(null); //Create the tree and link to the root node.
    	
		  
		SingletonCheckerVisitor visitor = new SingletonCheckerVisitor(className);
		cu.accept(visitor);
		 
    	return visitor;
	}
	
	
	public SingletonCheckerVisitor(String className) {
		super();
		this._className = className;
	}
	
	public boolean visit(TypeDeclaration node) {
		if(this._className.equals(node.getName().toString()) == false) {
			//name doesn't match
			return true;
		}
		checkGradeA(node);
		checkGradeBD(node);
		checkGradeC(node);
		return false;  //no need to visit further
	}
	/*
	 * whether to have a private constructor
	 */
	private void checkGradeA(TypeDeclaration node) {
		MethodDeclaration[] mds = node.getMethods();
		for(MethodDeclaration md: mds) {
			if(md.isConstructor() == true) {
				if(Modifier.isPrivate(md.getModifiers()) == true) {
					this.A_flag = true;
					return;
				}
			}
		}
		
		this.A_flag = false;
	}
	
	
	public boolean gradeA() {
		return this.A_flag;
	}
	
	/*
	 * B:whether to have a public static method that returns an instance of the class type
	 * D:The public static method calls the private constructor exactly once.
	 * Furthermore, this call should be inside an if statement.
	 * Reference: ASTNode type ClassInstanceCreation
	 */
	private void checkGradeBD(TypeDeclaration node) {
		MethodDeclaration[] mds = node.getMethods();
		for(MethodDeclaration md: mds) {
			//first traverse this array
			//try to find a public static method that returns an instance of the class type
			int modifier_flag = md.getModifiers();
			if(Modifier.isPublic(modifier_flag) && Modifier.isStatic(modifier_flag)) {
				//we get a public static method
				//get its return type
				Type type = md.getReturnType2();
				//first check if it is a simpletype
				//if true, then fetch its name and compares it to className
				if(type instanceof SimpleType && ((SimpleType)type).getName().toString().equals(this._className)) {
					this.B_flag = true;
					//furthermore, check if this method calls the private constructor 
					//exactly once in an if statement
					Block block = md.getBody();
					InstCreateCheckerVisitor Dchecker = new InstCreateCheckerVisitor(this._className);
					//TODO:check whether this is feasible
					block.accept(Dchecker);
					
					//examine Dchecker's result
					if(Dchecker.hasError() == true) {
						this.D_flag = false;
					}
					else {
						if(Dchecker.hasCreated() == true) {
							this.D_flag = true;
						}
						else {
							this.D_flag = false;
						}
					}
					
					//stop iterating
					return;
				}
			}
		}
		
		//we don't find that method
		this.B_flag = false;
		this.D_flag = false;
		return;
	}
	
	
	public boolean gradeB() {
		return this.B_flag;
	}
	
	/*
	 * whether to have a private static instance variable of the type of the class
	 */
	private void checkGradeC(TypeDeclaration node) {
		FieldDeclaration [] fds = node.getFields();
		for(FieldDeclaration fd : fds) {
			int modifier_flag = fd.getModifiers();
			if(Modifier.isPrivate(modifier_flag) && Modifier.isStatic(modifier_flag)) {
				//we catch a private static field
				Type type = fd.getType();
				if(type instanceof SimpleType && ((SimpleType) type).getName().toString().equals(this._className)) {
					this.C_flag = true;
					return;
				}
			}
		}
		this.C_flag = false;
	}
	
	public boolean gradeC() {
		return this.C_flag;
	}
	
	/*
	 * The public static method calls the private constructor exactly once.
	 * Furthermore, this call should be inside an if statement.
	 * Reference: ASTNode type ClassInstanceCreation
	 */
	public boolean gradeD() {
		return this.D_flag;
	}
}
