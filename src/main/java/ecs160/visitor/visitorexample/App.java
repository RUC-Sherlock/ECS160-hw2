package ecs160.visitor.visitorexample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ecs160.visitor.additionalvisitors.CountIfPrintVisitor;
import ecs160.visitor.additionalvisitors.CountPrintVisitor;
import ecs160.visitor.astvisitors.MethodPrinter;
import ecs160.visitor.utilities.UtilReader;

/**
 * A class for setting up an AST visitor and parsing on a single Java file.
 * @author caseycas
 */
public class App 
{
    public static void main( String[] args )
    {
    		//File file = new File(args[0]);
    		File file = new File("../ECS160JavaReview/src/classes/GermanShepherd.java");
    		//File file = new File("./src/main/java/ecs160/visitor/visitorexample/App.java");
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
	    	
	    	//Print out information about all methods in the tree.
	    	System.out.println("Method Printer:");
	    	cu.accept(new MethodPrinter());   
	    	
	    	//Count number of System.out.println statements
	    	CountPrintVisitor cpv = new CountPrintVisitor();
	    	cu.accept(cpv);
	    	System.out.println(cpv);
	    	
	    	//Count number of System.out.println statements inside if statements.
	    	CountIfPrintVisitor ifprints = new CountIfPrintVisitor();
	    	cu.accept(ifprints);
	    	System.out.println(ifprints);
    }
    
}
