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
import com.sun.source.util.TreeScanner;
import java.util.Arrays;

/**
 * This is a helper class that can be used to dump a {@link Tree} to {@link System#out} in debug code.
 */
@SuppressWarnings("unused") // OK: just used for debugging
class DebugTreeScanner extends TreeScanner<Void, Void>
{
	private int depth;

	@Deprecated // to be used locally for debugging only
	DebugTreeScanner(final int initialIndent)
	{
		depth = initialIndent;
	}

	void enter(final String visitMethod, final Tree tree)
	{
		final char[] indent = new char[depth];
		Arrays.fill(indent, '\t');
		String treeString = tree.toString().replace(System.lineSeparator(), " ");
		treeString = treeString.substring(0, Math.min(50, treeString.length()));
		System.out.println(new String(indent)+visitMethod+" "+tree.getKind()+" "+treeString);
		depth++;
	}

	void leave()
	{
		depth--;
	}

	@Override
	public Void visitAnnotatedType(final AnnotatedTypeTree att, final Void p)
	{
		enter("visitAnnotatedType", att);
		super.visitAnnotatedType(att, p);
		leave();
		return null;
	}

	@Override
	public Void visitAnnotation(final AnnotationTree at, final Void p)
	{
		enter("visitAnnotation", at);
		super.visitAnnotation(at, p);
		leave();
		return null;
	}

	@Override
	public Void visitMethodInvocation(final MethodInvocationTree mit, final Void p)
	{
		enter("visitMethodInvocation", mit);
		super.visitMethodInvocation(mit, p);
		leave();
		return null;
	}

	@Override
	public Void visitAssert(final AssertTree at, final Void p)
	{
		enter("visitAssert", at);
		super.visitAssert(at, p);
		leave();
		return null;
	}

	@Override
	public Void visitAssignment(final AssignmentTree at, final Void p)
	{
		enter("visitAssignment", at);
		super.visitAssignment(at, p);
		leave();
		return null;
	}

	@Override
	public Void visitCompoundAssignment(final CompoundAssignmentTree cat, final Void p)
	{
		enter("visitCompoundAssignment", cat);
		super.visitCompoundAssignment(cat, p);
		leave();
		return null;
	}

	@Override
	public Void visitBinary(final BinaryTree bt, final Void p)
	{
		enter("visitBinary", bt);
		super.visitBinary(bt, p);
		leave();
		return null;
	}

	@Override
	public Void visitBlock(final BlockTree bt, final Void p)
	{
		enter("visitBlock", bt);
		super.visitBlock(bt, p);
		leave();
		return null;
	}

	@Override
	public Void visitBreak(final BreakTree bt, final Void p)
	{
		enter("visitBreak", bt);
		super.visitBreak(bt, p);
		leave();
		return null;
	}

	@Override
	public Void visitCase(final CaseTree ct, final Void p)
	{
		enter("visitCase", ct);
		super.visitCase(ct, p);
		leave();
		return null;
	}

	@Override
	public Void visitCatch(final CatchTree ct, final Void p)
	{
		enter("visitCatch", ct);
		super.visitCatch(ct, p);
		leave();
		return null;
	}

	@Override
	public Void visitClass(final ClassTree ct, final Void p)
	{
		enter("visitClass", ct);
		super.visitClass(ct, p);
		leave();
		return null;
	}

	@Override
	public Void visitConditionalExpression(final ConditionalExpressionTree cet, final Void p)
	{
		enter("visitConditionalExpression", cet);
		super.visitConditionalExpression(cet, p);
		leave();
		return null;
	}

	@Override
	public Void visitContinue(final ContinueTree ct, final Void p)
	{
		enter("visitContinue", ct);
		super.visitContinue(ct, p);
		leave();
		return null;
	}

	@Override
	public Void visitDoWhileLoop(final DoWhileLoopTree dwlt, final Void p)
	{
		enter("visitDoWhileLoop", dwlt);
		super.visitDoWhileLoop(dwlt, p);
		leave();
		return null;
	}

	@Override
	public Void visitErroneous(final ErroneousTree et, final Void p)
	{
		enter("visitErroneous", et);
		super.visitErroneous(et, p);
		leave();
		return null;
	}

	@Override
	public Void visitExpressionStatement(final ExpressionStatementTree est, final Void p)
	{
		enter("visitExpressionStatement", est);
		super.visitExpressionStatement(est, p);
		leave();
		return null;
	}

	@Override
	public Void visitEnhancedForLoop(final EnhancedForLoopTree eflt, final Void p)
	{
		enter("visitEnhancedForLoop", eflt);
		super.visitEnhancedForLoop(eflt, p);
		leave();
		return null;
	}

	@Override
	public Void visitForLoop(final ForLoopTree flt, final Void p)
	{
		enter("visitForLoop", flt);
		super.visitForLoop(flt, p);
		leave();
		return null;
	}

	@Override
	public Void visitIdentifier(final IdentifierTree it, final Void p)
	{
		enter("visitIdentifier", it);
		super.visitIdentifier(it, p);
		leave();
		return null;
	}

	@Override
	public Void visitIf(final IfTree iftree, final Void p)
	{
		enter("visitIf", iftree);
		super.visitIf(iftree, p);
		leave();
		return null;
	}

	@Override
	public Void visitImport(final ImportTree it, final Void p)
	{
		enter("visitImport", it);
		super.visitImport(it, p);
		leave();
		return null;
	}

	@Override
	public Void visitArrayAccess(final ArrayAccessTree aat, final Void p)
	{
		enter("visitArrayAccess", aat);
		super.visitArrayAccess(aat, p);
		leave();
		return null;
	}

	@Override
	public Void visitLabeledStatement(final LabeledStatementTree lst, final Void p)
	{
		enter("visitLabeledStatement", lst);
		super.visitLabeledStatement(lst, p);
		leave();
		return null;
	}

	@Override
	public Void visitLiteral(final LiteralTree lt, final Void p)
	{
		enter("visitLiteral", lt);
		super.visitLiteral(lt, p);
		leave();
		return null;
	}

	@Override
	public Void visitMethod(final MethodTree mt, final Void p)
	{
		enter("visitMethod", mt);
		super.visitMethod(mt, p);
		leave();
		return null;
	}

	@Override
	public Void visitModifiers(final ModifiersTree mt, final Void p)
	{
		enter("visitModifiers", mt);
		super.visitModifiers(mt, p);
		leave();
		return null;
	}

	@Override
	public Void visitNewArray(final NewArrayTree nat, final Void p)
	{
		enter("visitNewArray", nat);
		super.visitNewArray(nat, p);
		leave();
		return null;
	}

	@Override
	public Void visitNewClass(final NewClassTree nct, final Void p)
	{
		enter("visitNewClass", nct);
		super.visitNewClass(nct, p);
		leave();
		return null;
	}

	@Override
	public Void visitLambdaExpression(final LambdaExpressionTree let, final Void p)
	{
		enter("visitLambdaExpression", let);
		super.visitLambdaExpression(let, p);
		leave();
		return null;
	}

	@Override
	public Void visitParenthesized(final ParenthesizedTree pt, final Void p)
	{
		enter("visitParenthesized", pt);
		super.visitParenthesized(pt, p);
		leave();
		return null;
	}

	@Override
	public Void visitReturn(final ReturnTree rt, final Void p)
	{
		enter("visitReturn", rt);
		super.visitReturn(rt, p);
		leave();
		return null;
	}

	@Override
	public Void visitMemberSelect(final MemberSelectTree mst, final Void p)
	{
		enter("visitMemberSelect", mst);
		super.visitMemberSelect(mst, p);
		leave();
		return null;
	}

	@Override
	public Void visitMemberReference(final MemberReferenceTree mrt, final Void p)
	{
		enter("visitMemberReference", mrt);
		super.visitMemberReference(mrt, p);
		leave();
		return null;
	}

	@Override
	public Void visitEmptyStatement(final EmptyStatementTree est, final Void p)
	{
		enter("visitEmptyStatement", est);
		super.visitEmptyStatement(est, p);
		leave();
		return null;
	}

	@Override
	public Void visitSwitch(final SwitchTree st, final Void p)
	{
		enter("visitSwitch", st);
		super.visitSwitch(st, p);
		leave();
		return null;
	}

	@Override
	public Void visitSynchronized(final SynchronizedTree st, final Void p)
	{
		enter("visitSynchronized", st);
		super.visitSynchronized(st, p);
		leave();
		return null;
	}

	@Override
	public Void visitThrow(final ThrowTree tt, final Void p)
	{
		enter("visitThrow", tt);
		super.visitThrow(tt, p);
		leave();
		return null;
	}

	@Override
	public Void visitCompilationUnit(final CompilationUnitTree cut, final Void p)
	{
		enter("visitCompilationUnit", cut);
		super.visitCompilationUnit(cut, p);
		leave();
		return null;
	}

	@Override
	public Void visitTry(final TryTree tt, final Void p)
	{
		enter("visitTry", tt);
		super.visitTry(tt, p);
		leave();
		return null;
	}

	@Override
	public Void visitParameterizedType(final ParameterizedTypeTree ptt, final Void p)
	{
		enter("visitParameterizedType", ptt);
		super.visitParameterizedType(ptt, p);
		leave();
		return null;
	}

	@Override
	public Void visitUnionType(final UnionTypeTree utt, final Void p)
	{
		enter("visitUnionType", utt);
		super.visitUnionType(utt, p);
		leave();
		return null;
	}

	@Override
	public Void visitIntersectionType(final IntersectionTypeTree itt, final Void p)
	{
		enter("visitIntersectionType", itt);
		super.visitIntersectionType(itt, p);
		leave();
		return null;
	}

	@Override
	public Void visitArrayType(final ArrayTypeTree att, final Void p)
	{
		enter("visitArrayType", att);
		super.visitArrayType(att, p);
		leave();
		return null;
	}

	@Override
	public Void visitTypeCast(final TypeCastTree tct, final Void p)
	{
		enter("visitTypeCast", tct);
		super.visitTypeCast(tct, p);
		leave();
		return null;
	}

	@Override
	public Void visitPrimitiveType(final PrimitiveTypeTree ptt, final Void p)
	{
		enter("visitPrimitiveType", ptt);
		super.visitPrimitiveType(ptt, p);
		leave();
		return null;
	}

	@Override
	public Void visitTypeParameter(final TypeParameterTree tpt, final Void p)
	{
		enter("visitTypeParameter", tpt);
		super.visitTypeParameter(tpt, p);
		leave();
		return null;
	}

	@Override
	public Void visitInstanceOf(final InstanceOfTree iot, final Void p)
	{
		enter("visitInstanceOf", iot);
		super.visitInstanceOf(iot, p);
		leave();
		return null;
	}

	@Override
	public Void visitUnary(final UnaryTree ut, final Void p)
	{
		enter("visitUnary", ut);
		super.visitUnary(ut, p);
		leave();
		return null;
	}

	@Override
	public Void visitVariable(final VariableTree vt, final Void p)
	{
		enter("visitVariable", vt);
		super.visitVariable(vt, p);
		leave();
		return null;
	}

	@Override
	public Void visitWhileLoop(final WhileLoopTree wlt, final Void p)
	{
		enter("visitWhileLoop", wlt);
		super.visitWhileLoop(wlt, p);
		leave();
		return null;
	}

	@Override
	public Void visitWildcard(final WildcardTree wt, final Void p)
	{
		enter("visitWildcard", wt);
		super.visitWildcard(wt, p);
		leave();
		return null;
	}

	@Override
	public Void visitOther(final Tree tree, final Void p)
	{
		enter("visitOther", tree);
		super.visitOther(tree, p);
		leave();
		return null;
	}
}
