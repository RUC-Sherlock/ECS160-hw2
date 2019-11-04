package ecs160.visitor.astvisitors;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import ecs160.visitor.utilities.ASTNodeTypePrinter;

/**
 * Method to print out information about source code
 * calling a method.
 * @author caseycas
 */
public class MethodInvocationPrinter extends ASTVisitor{
	
	/**
	 * A visitor returning false means we do not check the child nodes further along in the tree.
	 * Returning true means we do visit the child nodes.
	 */
	public boolean visit(MethodInvocation node){
		System.out.println("-- MethodInvocation --> " + node.getName());
		List<Expression> args = (List<Expression>)node.arguments();
		int i = 0;
		for(Expression a : args)
		{
			System.out.println("\tArgument " + i + ") " + ASTNodeTypePrinter.getSimpleType(a) + " -- "+ a);
			i++;
		}

		return true; //If false, we wouldn't print out nested function calls.
	}

}
