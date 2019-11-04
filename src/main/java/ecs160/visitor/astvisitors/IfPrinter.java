package ecs160.visitor.astvisitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

import ecs160.visitor.utilities.ASTNodeTypePrinter;

/**
 * A visitor to print out information about if statements
 * including the expression, the body of the if, and the body of the 'else'.
 * @author caseycas
 */
public class IfPrinter extends ASTVisitor{
	
	public boolean visit(IfStatement node)
	{
		System.out.println(" -- if statement -- ");
		Expression if_exp = node.getExpression();
		System.out.println("\tExpression --> Type: " + ASTNodeTypePrinter.getSimpleType(if_exp) + " Code: " + if_exp);
		//We could define and infix or prefix visitor and invoke it here...
		//Example Functions on an InfixExpression: getOperator(), getRightOperand(), getLeftOperand()
		System.out.println("\tthen body: " + node.getThenStatement().toString().replaceAll("\n", " "));
		//The else statement becomes nested if we have else if blocks, becoming another if statement we could
		//call recursively...
		Statement elseStmt = node.getElseStatement();
		if(elseStmt != null)
		{
			System.out.println("\telse body: " + node.getElseStatement().toString().replaceAll("\n", " "));
		}
		else
		{
			System.out.println("\tno else statement");
		}
		return true; //True here allows us to recursively unwind an if statement with multiple blocks.
	}

}
