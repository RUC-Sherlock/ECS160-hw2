package ecs160.visitor.additionalvisitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * A simple visitor to count the number of println
 * method calls in a java file.
 * @author caseycas
 */
public class CountPrintVisitor extends ASTVisitor {
	
	private int prints;

	
	/**
	 * Print out a message with the class name.
	 */
	public static void stateName()
	{
		System.out.println("CountPrintVisitor Class");
	}
	

	public CountPrintVisitor()
	{
		prints = 0;
	}
	

	
	/**
	 * Visitor to method invocations that increments
	 * our count of the method name is 'println'
	 */
	@Override
	public boolean visit(MethodInvocation node)
	{
		if(node.getName().toString().equals("println"))
		{
			prints++;
		}
		return true;
	}
	
	/**
	 * Get the number of print statements.
	 * @return the number of recorded print statements.
	 */
	public int getPrints()
	{
		return prints;
	}
	
	/**
	 * Override the toString method to nicely print
	 * out the prints field of the class.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("--------- Print Counts ---------");
		sb.append("\n");
		sb.append(prints);
		sb.append("\n");
		sb.append("--------------------------------");
		return sb.toString();
	}

}
