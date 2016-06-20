package com.exedio.cope.instrument;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.TreePathScanner;
import java.util.Arrays;

public class TreeDump extends TreePathScanner<Void,Void>
{
	int indent = 0;

	void enter(String visitMethod, Tree tree, String... details)
	{
		String indentString = getIndentString();
		System.out.println(indentString+visitMethod+" "+Arrays.asList(details)+" {");
		indent++;
	}

	private String getIndentString()
	{
		char[] indentChars = new char[indent];
		Arrays.fill(indentChars, '\t');
		return new String(indentChars);
	}

	void leave()
	{
		indent--;
		System.out.println(getIndentString()+"}");
	}

	void finish()
	{
		if ( indent!=0 ) throw new RuntimeException();
	}

	@Override
	public Void visitAnnotatedType(AnnotatedTypeTree node, Void p)
	{
		enter("visitAnnotatedTypeTree", node);
		Void result = super.visitAnnotatedType(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitAnnotation(AnnotationTree node, Void p)
	{
		enter("visitAnnotation", node, node.getAnnotationType().toString());
		Void result = super.visitAnnotation(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitMethodInvocation(MethodInvocationTree node, Void p)
	{
		enter("visitMethodInvocation", node);
		Void result = super.visitMethodInvocation(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitAssert(AssertTree node, Void p)
	{
		enter("visitAssert", node);
		Void result = super.visitAssert(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitAssignment(AssignmentTree node, Void p)
	{
		enter("visitAssignment", node);
		Void result = super.visitAssignment(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitCompoundAssignment(CompoundAssignmentTree node, Void p)
	{
		enter("visitCompoundAssignment", node);
		Void result = super.visitCompoundAssignment(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitBinary(BinaryTree node, Void p)
	{
		enter("visitBinary", node);
		Void result = super.visitBinary(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitBlock(BlockTree node, Void p)
	{
		enter("visitBlock", node);
		// Void result = super.visitBlock(node, p);
		leave();
		// return result;
		return null;
	}

	@Override
	public Void visitBreak(BreakTree node, Void p)
	{
		enter("visitBreak", node);
		Void result = super.visitBreak(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitCase(CaseTree node, Void p)
	{
		enter("visitCase", node);
		Void result = super.visitCase(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitCatch(CatchTree node, Void p)
	{
		enter("visitCatch", node);
		Void result = super.visitCatch(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitClass(ClassTree node, Void p)
	{
		enter("visitClass", node, node.getSimpleName().toString());
		Void result = super.visitClass(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitConditionalExpression(ConditionalExpressionTree node, Void p)
	{
		enter("visitConditionalExpression", node);
		Void result = super.visitConditionalExpression(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitContinue(ContinueTree node, Void p)
	{
		enter("visitContinue", node);
		Void result = super.visitContinue(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitDoWhileLoop(DoWhileLoopTree node, Void p)
	{
		enter("visitDoWhileLoop", node);
		Void result = super.visitDoWhileLoop(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitErroneous(ErroneousTree node, Void p)
	{
		enter("visitErroneous", node);
		Void result = super.visitErroneous(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitExpressionStatement(ExpressionStatementTree node, Void p)
	{
		enter("visitExpressionStatement", node);
		Void result = super.visitExpressionStatement(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void p)
	{
		enter("visitEnhancedForLoop", node);
		Void result = super.visitEnhancedForLoop(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitForLoop(ForLoopTree node, Void p)
	{
		enter("visitForLoop", node);
		Void result = super.visitForLoop(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitIdentifier(IdentifierTree node, Void p)
	{
		enter("visitIdentifier", node);
		Void result = super.visitIdentifier(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitIf(IfTree node, Void p)
	{
		enter("visitIf", node);
		Void result = super.visitIf(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitImport(ImportTree node, Void p)
	{
		enter("visitImport", node);
		Void result = super.visitImport(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitArrayAccess(ArrayAccessTree node, Void p)
	{
		enter("visitArrayAccess", node);
		Void result = super.visitArrayAccess(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitLabeledStatement(LabeledStatementTree node, Void p)
	{
		enter("visitLabeledStatement", node);
		Void result = super.visitLabeledStatement(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitLiteral(LiteralTree node, Void p)
	{
		enter("visitLiteral", node);
		Void result = super.visitLiteral(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitMethod(MethodTree node, Void p)
	{
		enter("visitMethod", node, node.getName().toString());
		Void result = super.visitMethod(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitModifiers(ModifiersTree node, Void p)
	{
		enter("visitModifiers", node, node.getFlags().toString());
		Void result = super.visitModifiers(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitNewArray(NewArrayTree node, Void p)
	{
		enter("visitNewArray", node);
		Void result = super.visitNewArray(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitNewClass(NewClassTree node, Void p)
	{
		enter("visitNewClass", node);
		Void result = super.visitNewClass(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitLambdaExpression(LambdaExpressionTree node, Void p)
	{
		enter("visitLambdaExpression", node);
		Void result = super.visitLambdaExpression(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitParenthesized(ParenthesizedTree node, Void p)
	{
		enter("visitParenthesized", node);
		Void result = super.visitParenthesized(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitReturn(ReturnTree node, Void p)
	{
		enter("visitReturn", node);
		Void result = super.visitReturn(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitMemberSelect(MemberSelectTree node, Void p)
	{
		enter("visitMemberSelect", node);
		Void result = super.visitMemberSelect(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitMemberReference(MemberReferenceTree node, Void p)
	{
		enter("visitMemberReference", node);
		Void result = super.visitMemberReference(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitEmptyStatement(EmptyStatementTree node, Void p)
	{
		enter("visitEmptyStatement", node);
		Void result = super.visitEmptyStatement(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitSwitch(SwitchTree node, Void p)
	{
		enter("visitSwitch", node);
		Void result = super.visitSwitch(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitSynchronized(SynchronizedTree node, Void p)
	{
		enter("visitSynchronized", node);
		Void result = super.visitSynchronized(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitThrow(ThrowTree node, Void p)
	{
		enter("visitThrow", node);
		Void result = super.visitThrow(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitCompilationUnit(CompilationUnitTree node, Void p)
	{
		enter("visitCompilationUnit", node);
		Void result = super.visitCompilationUnit(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitTry(TryTree node, Void p)
	{
		enter("visitTry", node);
		Void result = super.visitTry(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitParameterizedType(ParameterizedTypeTree node, Void p)
	{
		enter("visitParameterizedType", node);
		Void result = super.visitParameterizedType(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitUnionType(UnionTypeTree node, Void p)
	{
		enter("visitUnionType", node);
		Void result = super.visitUnionType(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitIntersectionType(IntersectionTypeTree node, Void p)
	{
		enter("visitIntersectionType", node);
		Void result = super.visitIntersectionType(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitArrayType(ArrayTypeTree node, Void p)
	{
		enter("visitArrayType", node);
		Void result = super.visitArrayType(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitTypeCast(TypeCastTree node, Void p)
	{
		enter("visitTypeCast", node);
		Void result = super.visitTypeCast(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitPrimitiveType(PrimitiveTypeTree node, Void p)
	{
		enter("visitPrimitiveType", node);
		Void result = super.visitPrimitiveType(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitTypeParameter(TypeParameterTree node, Void p)
	{
		enter("visitTypeParameter", node);
		Void result = super.visitTypeParameter(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitInstanceOf(InstanceOfTree node, Void p)
	{
		enter("visitInstanceOf", node);
		Void result = super.visitInstanceOf(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitUnary(UnaryTree node, Void p)
	{
		enter("visitUnary", node);
		Void result = super.visitUnary(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitVariable(VariableTree node, Void p)
	{
		enter("visitVariable", node, node.getName().toString());
		Void result = super.visitVariable(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitWhileLoop(WhileLoopTree node, Void p)
	{
		enter("visitWhileLoop", node);
		Void result = super.visitWhileLoop(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitWildcard(WildcardTree node, Void p)
	{
		enter("visitWildcard", node);
		Void result = super.visitWildcard(node, p);
		leave();
		return result;
	}

	@Override
	public Void visitOther(Tree node, Void p)
	{
		enter("visitOther", node);
		Void result = super.visitOther(node, p);
		leave();
		return result;
	}

}
