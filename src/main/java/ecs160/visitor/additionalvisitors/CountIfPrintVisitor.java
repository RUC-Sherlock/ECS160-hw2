package ecs160.visitor.additionalvisitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;

/**
 * A class to print out print statements that appear inside
 * if statements.
 * @author caseycas
 */
public class CountIfPrintVisitor extends ASTVisitor {
	
	private int ifPrints;
	
	public CountIfPrintVisitor()
	{
		ifPrints = 0;
	}
	
	/**
	 * Identify when we are inside an if statement, and then
	 * use the CountPrintVisitor to count the number of print
	 * statements.
	 */
	public boolean visit(IfStatement node)
	{
		CountPrintVisitor printCounter = new CountPrintVisitor();
		node.accept(printCounter);
		ifPrints = getIfPrints() + printCounter.getPrints();
		return false;
	}


	public int getIfPrints() {
		return ifPrints;
	}
	
	/**
	 * Override the toString method to nicely print
	 * out the prints field of the class.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("--------- Count of Prints Inside of If ---------");
		sb.append("\n");
		sb.append(ifPrints);
		sb.append("\n");
		sb.append("------------------------------------------------");
		return sb.toString();
	}
}
