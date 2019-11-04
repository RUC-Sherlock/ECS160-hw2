package ecs160.visitor.astvisitors;


import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import ecs160.visitor.utilities.ASTNodeTypePrinter;

/**
 * Class to print out information about every method
 * declaration we visit in the AST.
 * @author caseycas
 */
public class MethodPrinter extends ASTVisitor{
	
	@Override
	public boolean visit(MethodDeclaration node)
	{
		System.out.println("---------------------------------------------------------------------");
		System.out.print("Method Declaration: " + node.getName() + " (");
		List<SingleVariableDeclaration> params = (List<SingleVariableDeclaration>)node.parameters();
		int i = 0;
		for(SingleVariableDeclaration svd : params)
		{
			System.out.print(" Parameter " + i + ") Type: " + svd.getType() + " Name: " + svd.getName() + " ");
			i++;
		}
		System.out.println(")");
		List<ASTNode> mods = (List<ASTNode>) node.modifiers();
		i = 0;
		for (ASTNode m : mods)
		{
			System.out.println("Modifier " + i + ") Type: " + ASTNodeTypePrinter.getSimpleType(m) + " Name: " + m);
			i++;
		}
		System.out.println("Is constructor: " + node.isConstructor());
		System.out.println("Return type: " + node.getReturnType2()); //getReturnType was deprecated in Java 3.
		System.out.println("{");
		node.accept(new IfPrinter());
		node.accept(new MethodInvocationPrinter());
		System.out.println("}");
		System.out.println("---------------------------------------------------------------------");
		return false;
	}

}
