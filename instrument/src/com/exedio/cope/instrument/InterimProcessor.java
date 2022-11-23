/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

final class InterimProcessor extends JavacProcessor
{
	private static final Set<String> ALLOWED_CLASS_TREE_KINDS = new HashSet<>(asList(
			Tree.Kind.CLASS.name(),
			Tree.Kind.INTERFACE.name(),
			Tree.Kind.ENUM.name(),
			"RECORD" // added by Java 14 (replace with constant once we require Java 14)
	));

	private final Path targetDirectory;
	private final Params params;

	private ClassLoader interimClassLoader;

	InterimProcessor(final Params params)
	{
		this.targetDirectory = params.buildDirectory.toPath().resolve(Paths.get("interimsrc"));
		this.params = params;
		try
		{
			Files.createDirectories(targetDirectory);
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/** @return null if compiling interim code failed */
	ClassLoader getInterimClassLoader()
	{
		return interimClassLoader;
	}

	@Override
	void processInternal(final RoundEnvironment roundEnv)
	{
		if (interimClassLoader!=null) throw new RuntimeException();
		final DocTrees docTrees = DocTrees.instance(processingEnv);
		final List<InterimVisitor> interimVisitors = new ArrayList<>();
		final Map<Name,Code> blockRegistry = new HashMap<>();
		boolean foundInterim = false;
		for (final Element element : roundEnv.getRootElements())
		{
			final TreePath tp = docTrees.getPath(element);
			final InterimVisitor interimVisitor = new InterimVisitor(docTrees, blockRegistry);
			interimVisitor.scan(tp.getCompilationUnit(), null);
			interimVisitors.add(interimVisitor);
			foundInterim |= interimVisitor.writeInterimFile();
		}
		if (!foundInterim)
			throw new RuntimeException("found no source file an interim file would be written for - check instrumentor scope settings");
		final Set<TypeElement> allRequiredTypes = new HashSet<>();
		for (final InterimVisitor interimVisitor : interimVisitors)
		{
			allRequiredTypes.addAll(interimVisitor.requiredTypes);
		}
		for (final TypeElement requiredType : allRequiredTypes)
		{
			final Code code = blockRegistry.get(requiredType.getQualifiedName());
			if (code!=null)
				code.require();
		}
		for (final InterimVisitor interimVisitor : interimVisitors)
		{
			for (final InterimVisitor otherVisitor : interimVisitors)
			{
				if (!otherVisitor.writeInterimFile())
				{
					// otherVisitor dropped completely -> remove all imports
					for (final Name className : otherVisitor.classNames)
					{
						interimVisitor.removeImports(className.toString());
					}
				}
				else
				{
					// only remove imports for dropped elements
					for (final String importString : otherVisitor.staticDropped)
					{
						interimVisitor.removeImports(importString);
					}
					otherVisitor.code.removeImportsForUnrequired(interimVisitor);
				}
			}
		}
		final InMemoryCompiler compiler = new InMemoryCompiler();
		for (final InterimVisitor interimVisitor : interimVisitors)
		{
			interimVisitor.finish(compiler);
		}
		try
		{
			this.interimClassLoader = compiler.compile(
				ToolProvider.getSystemJavaCompiler(),
				JavacRunner.combineClasspath(JavacRunner.getCurrentClasspath(), JavacRunner.toClasspathString(params.classpath))
			);
			if (params.verbose)
			{
				System.out.println("verbose, writing interim code to "+targetDirectory.toAbsolutePath());
				try
				{
					compiler.dumpJavaFiles(targetDirectory, params.charset);
				}
				catch (final IOException ioe)
				{
					System.out.println("writing interim source to "+targetDirectory.toAbsolutePath()+" failed: "+ioe.getMessage());
				}
			}
		}
		catch (final InMemoryCompiler.CompileException e)
		{
			try
			{
				System.out.println("compile error in interim code - writing to "+targetDirectory.toAbsolutePath()+" for review");
				System.out.println("line numbers in errors below are in interim code, not in original code");
				compiler.dumpJavaFiles(targetDirectory, params.charset);
			}
			catch (final IOException ioe)
			{
				System.out.println("writing interim source to "+targetDirectory.toAbsolutePath()+" failed: "+ioe.getMessage());
			}
			if (e.getDiagnostics().isEmpty())
			{
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "unspecific failure");
			}
			for (final Diagnostic<? extends JavaFileObject> diagnostic : e.getDiagnostics())
			{
				processingEnv.getMessager().printMessage(diagnostic.getKind(), diagnostic.toString());
			}
		}
	}

	private static class Import
	{
		private final ImportTree tree;
		private String importString;

		Import(final ImportTree tree)
		{
			this.tree = tree;
		}

		boolean startsWith(final String importPrefix)
		{
			if (importString==null)
				importString = tree.getQualifiedIdentifier().toString();
			return importString.startsWith(importPrefix) && (importString.length()==importPrefix.length() || importString.charAt(importPrefix.length())=='.');
		}

		String getImportStatement()
		{
			return tree.toString();
		}
	}

	private final class InterimVisitor extends TreePathScanner<Void, Void>
	{
		private final DocTrees docTrees;
		private final Map<Name,Code> blockRegistry;

		private JavaFileObject sourceFile;

		private Code code = new Code(null, null, false);
		private final List<String> staticDropped = new ArrayList<>();
		private String packageStatement = null;
		private final List<Import> imports = new LinkedList<>();
		private final Set<TypeElement> requiredTypes = new HashSet<>();

		private List<Name> classNames;

		private final Deque<TypeElement> currentClassStack = new LinkedList<>();

		private InterimVisitor(final DocTrees docTrees, final Map<Name,Code> blockRegistry)
		{
			this.docTrees = docTrees;
			this.blockRegistry = blockRegistry;
		}

		boolean writeInterimFile()
		{
			return code.required;
		}

		private Element getRequiredElement(final Tree t)
		{
			final Element result = getElement(t);
			if (result==null)
			{
				throw new NullPointerException("no element for '"+t+"' ("+t.getKind()+")");
			}
			return result;
		}

		private Element getElement(final Tree t)
		{
			return docTrees.getElement(docTrees.getPath(getCompilationUnit(), t));
		}

		@Override
		public Void visitCompilationUnit(final CompilationUnitTree cut, final Void p)
		{
			try
			{
				if(classNames != null) throw new RuntimeException();
				if(sourceFile != null) throw new RuntimeException();

				classNames = new ArrayList<>();
				for(final Tree typeDecl : cut.getTypeDecls())
				{
					final TypeElement typeElement = (TypeElement) docTrees.getElement(docTrees.getPath(cut, typeDecl));
					if(typeElement==null)
						continue; // happens, if there is semicolon after a top-level class, see ClassWithSemicolon
					classNames.add(typeElement.getQualifiedName());
				}

				sourceFile = cut.getSourceFile();

				if(cut.getPackageName() != null)
					packageStatement = "package " + cut.getPackageName() + ";";

				if(isFileIgnored(sourceFile))
					return null;
				else
					return super.visitCompilationUnit(cut, p);
			}
			catch (final RuntimeException e)
			{
				throw new RuntimeException("runtime error while processing "+cut.getSourceFile(), e);
			}
		}

		@Override
		public Void visitImport(final ImportTree it, final Void p)
		{
			imports.add(new Import(it));
			return super.visitImport(it, p);
		}

		@Override
		public Void visitClass(final ClassTree ct, final Void p)
		{
			if (isWrapperIgnore()) return null;
			final TypeElement element = (TypeElement) docTrees.getElement(getCurrentPath());
			final WrapImplementsInterim implementsInterim = element.getAnnotation(WrapImplementsInterim.class);
			if (implementsInterim!=null && ct.getKind()!=Tree.Kind.INTERFACE)
				throw new RuntimeException(""+WrapImplementsInterim.class.getSimpleName()+" can only be used at interfaces, not at "+element);
			final Kind kind = Kind.valueOf(element.getAnnotation(WrapType.class));
			if (ct.getKind()==Tree.Kind.ANNOTATION_TYPE) return null;
			if (ct.getSimpleName().length()==0) return null;
			currentClassStack.addFirst(element);
			final StringBuilder declaration = new StringBuilder();
			declaration.append(toStringWithoutMostAnnotations(ct.getModifiers())).append(getTypeToken(ct)).append(" ").append(ct.getSimpleName());
			declaration.append(getTypeParameterString(ct.getTypeParameters()));
			if (ct.getExtendsClause()!=null)
			{
				declaration.append(" extends ").append(ct.getExtendsClause());
			}
			appendImplements(declaration, ct);
			code = code.openBlock(element.getQualifiedName(), declaration, false);
			blockRegistry.put(element.getQualifiedName(), code);
			if (ct.getKind()==Tree.Kind.ENUM)
			{
				final LineCodePart line = code.startLine("");
				final CollectEnumValuesVisitor enumCollector = new CollectEnumValuesVisitor(getElement(ct));
				enumCollector.visitClass(ct, null);
				final StringSeparator comma = new StringSeparator(", ");
				for (final VariableTree enumValue : enumCollector.enumValues)
				{
					comma.appendTo(line.line);
					for (final AnnotationTree interimAnnotation : getInterimAnnotations(enumValue.getModifiers().getAnnotations()))
					{
						line.continueLine(interimAnnotation+" ");
					}
					line.continueLine(enumValue.getName().toString());
				}
				line.continueLine(";");
				line.endLine();
			}
			if (kind!=null || isWrapInterim())
				code.require();
			final Void result = super.visitClass(ct, p);
			if (implementsInterim!=null && implementsInterim.addMethods())
			{
				for (final Element enclosedElement : element.getEnclosedElements())
				{
					if(enclosedElement.getKind() == ElementKind.METHOD)
					{
						final ExecutableElement method = (ExecutableElement) enclosedElement;
						code.addLine(getMethodDeclaration(method, false)+";");
					}
				}
			}
			for (final ExecutableElement method: getMethodsThatNeedImplementation(element, getInterfacesThatAddMethods(element)))
			{
				code = code.openBlock(null, getMethodDeclaration(method, true), true);
				code.addLine("throw new RuntimeException(\"don't call in interim code\");");
				code = code.closeBlock();
			}

			if (kind!=null)
			{
				final WrapperType wrapperType = AnnotationHelper.getOrDefault(getCurrentPathAnnotation(WrapperType.class));
				if (kind.activationConstructor!=null && wrapperType.activationConstructor()!=Visibility.NONE)
				{
					code.addLine("public "+ct.getSimpleName()+"(final "+kind.activationConstructor+" ap) { super(ap); }");
				}
				if (kind.hasGenericConstructor && wrapperType.genericConstructor()!=Visibility.NONE)
				{
					code = code.openBlock(null, "public "+ct.getSimpleName()+"(final com.exedio.cope.SetValue<?>... setValues)", true);
					code.addLine("super(setValues);");
					code = code.closeBlock();
				}
				final int typeParametersNumber = ct.getTypeParameters().size();
				if (typeParametersNumber>0 && wrapperType.wildcardClass()!=Visibility.NONE)
				{
					final StringBuilder typeParameters = new StringBuilder("?");
					for(int i = 1; i<typeParametersNumber; i++)
						typeParameters.append(",?");
					code.addLine(
						"public static final class classWildcard "+
						"{ "+
							"public static final java.lang.Class<"+ct.getSimpleName()+"<"+typeParameters+">> value = "+
								"(java.lang.Class<"+ct.getSimpleName()+"<"+typeParameters+">>)(java.lang.Class<?>)"+ct.getSimpleName()+".class;"+
							"private classWildcard(){}"+ // prevent instantiation
						"}"
					);
				}
				if (kind.typeField!=null && wrapperType.type()!=Visibility.NONE)
					code.addLine(
						"public static final "+kind.typeField+"<"+ct.getSimpleName()+"> TYPE = "+
							kind.typeFactory+"."+Kind.TYPE_FACTORY_METHOD+"("+ct.getSimpleName()+".class);"
					);
			}
			code = code.closeBlock();
			currentClassStack.removeFirst();
			return result;
		}

		private CharSequence getTypeParameterString(final List<? extends TypeParameterTree> tp)
		{
			final StringBuilder typeParameterString = new StringBuilder();
			if (!tp.isEmpty())
			{
				typeParameterString.append(" <");
				final StringSeparator comma = new StringSeparator(", ");
				for (final TypeParameterTree typeParameter : tp)
				{
					comma.appendTo(typeParameterString);
					typeParameterString.append(typeParameter);
				}
				typeParameterString.append(">");
			}
			return typeParameterString;
		}

		private List<TypeElement> getInterfacesThatAddMethods(final TypeElement typeElement)
		{
			final List<TypeElement> result = new ArrayList<>();
			addInterfacesThatAddMethods(typeElement, result);
			return result;
		}

		private void addInterfacesThatAddMethods(final TypeElement type, final List<TypeElement> interfaces)
		{
			if (type.getKind()==ElementKind.INTERFACE && type.getAnnotation(WrapImplementsInterim.class)!=null && type.getAnnotation(WrapImplementsInterim.class).addMethods())
			{
				interfaces.add(type);
			}
			final TypeElement superClass = getSuperclass(type);
			if (superClass!=null)
				addInterfacesThatAddMethods(superClass, interfaces);
			for(final TypeMirror interfaceMirror : type.getInterfaces())
			{
				final TypeElement interfaceType = (TypeElement) processingEnv.getTypeUtils().asElement(interfaceMirror);
				if (interfaceType.getAnnotation(WrapImplementsInterim.class)!=null)
				{
					addInterfacesThatAddMethods(interfaceType, interfaces);
				}
			}
		}

		private List<ExecutableElement> getMethodsThatNeedImplementation(final TypeElement element, final List<TypeElement> implementedInterfaces)
		{
			final List<ExecutableElement> result = new ArrayList<>();
			for (final Element enclosedElement : element.getEnclosedElements())
			{
				if (enclosedElement.getKind()!=ElementKind.METHOD) continue;
				final ExecutableElement method = (ExecutableElement) enclosedElement;
				if ( isOverrideOfExternallyDefinedAbstractMethod(method, element) ||
					  isOverrideOfMethodDefinedInInterface(method, element, implementedInterfaces) )
					result.add(method);
			}
			return result;
		}

		private boolean isOverrideOfExternallyDefinedAbstractMethod(final ExecutableElement method, final TypeElement type)
		{
			TypeElement parentElement = type;
			while ((parentElement=getSuperclass(parentElement))!=null)
			{
				final boolean parentIsInSourcePath = DocTrees.instance(processingEnv).getPath(parentElement)!=null;
				if (!parentIsInSourcePath)
				{
					// the parent class is in an external library, so abstract methods it declares need an implementation
					for(final Element parentEnclosedElement : parentElement.getEnclosedElements())
					{
						if(parentEnclosedElement.getKind() != ElementKind.METHOD) continue;
						final ExecutableElement parentMethod = (ExecutableElement) parentEnclosedElement;
						if (parentMethod.getModifiers().contains(Modifier.ABSTRACT)
							 && processingEnv.getElementUtils().overrides(method, parentMethod, type))
						{
							return true;
						}
					}
				}
			}
			return false;
		}

		private boolean isOverrideOfMethodDefinedInInterface(final ExecutableElement method, final TypeElement type, final List<TypeElement> interfaces)
		{
			for (final TypeElement interfaceType : interfaces)
			{
				for (final Element elementInInterface : interfaceType.getEnclosedElements())
				{
					if (elementInInterface.getKind() != ElementKind.METHOD) continue;
					final ExecutableElement methodInInterface = (ExecutableElement) elementInInterface;
					if (processingEnv.getElementUtils().overrides(method, methodInInterface, type))
						return true;
				}
			}
			return false;
		}

		private TypeElement getSuperclass(final TypeElement typeElement)
		{
			final TypeMirror superMirror = typeElement.getSuperclass();
			if (superMirror.getKind() == TypeKind.NONE)
				return null;
			return (TypeElement) processingEnv.getTypeUtils().asElement(superMirror);
		}

		private String getMethodDeclaration(final ExecutableElement method, final boolean override)
		{
			final StringBuilder methodDeclaration = new StringBuilder();
			if (override)
				methodDeclaration.append("@java.lang.Override ");
			methodDeclaration.
				append("public ").
				append(method.getReturnType()).
				append(" ").
				append(method.getSimpleName()).
				append("(");
			final StringSeparator comma = new StringSeparator(", ");
			for (final VariableElement parameter : method.getParameters())
			{
				comma.appendTo(methodDeclaration);
				methodDeclaration.
					append(parameter.asType()).
					append(" ").
					append(parameter.getSimpleName());
			}
			methodDeclaration.append(")");
			return methodDeclaration.toString();
		}

		private String getTypeToken(final ClassTree ct)
		{
			final String name = ct.getKind().name();
			if (ALLOWED_CLASS_TREE_KINDS.contains(name))
				return name.toLowerCase(Locale.ROOT);
			else
				throw new RuntimeException(ct.getKind().name());
		}

		private boolean currentClassIsFeatureContainer()
		{
			final TypeElement currentClass = getCurrentClass();
			return currentClass.getAnnotation(WrapType.class)!=null;
		}

		private void appendImplements(final StringBuilder sb, final ClassTree ct)
		{
			final List<Tree> implementsInterim = new ArrayList<>();
			for (final Tree implementsClause : ct.getImplementsClause())
			{
				final TypeElement implementsType = (TypeElement) getElement(implementsClause);
				final WrapImplementsInterim wrapImplementsInterim = implementsType.getAnnotation(WrapImplementsInterim.class);
				if (wrapImplementsInterim!=null)
				{
					implementsInterim.add(implementsClause);
				}
			}
			if (!implementsInterim.isEmpty())
			{
				sb.append(ct.getKind()==Tree.Kind.INTERFACE?" extends ":" implements ");
				final StringSeparator comma = new StringSeparator(", ");
				for (final Tree implementsClause : implementsInterim)
				{
					comma.appendTo(sb);
					sb.append(implementsClause);
				}
			}
		}

		@Override
		public Void visitMethod(final MethodTree mt, final Void p)
		{
			if (isWrapInterim())
			{
				final LineCodePart part = code.startLine(toStringWithoutMostAnnotations(mt.getModifiers()));
				if (mt.getReturnType()==null)
				{
					// This is a constructor.
					part.continueLine(getCurrentClass().getSimpleName().toString());
				}
				else
				{
					part.continueLine(getTypeParameterString(mt.getTypeParameters()));
					part.continueLine(" ");
					part.continueLine(mt.getReturnType().toString());
					part.continueLine(" ");
					part.continueLine(mt.getName().toString());
				}
				part.continueLine("(");
				final StringSeparator comma = new StringSeparator(", ");
				for (final VariableTree parameter : mt.getParameters())
				{
					comma.appendTo(part.line);
					part.continueLine(parameter.toString());
				}
				part.continueLine(")");
				final boolean methodIsAbstract = mt.getModifiers().getFlags().contains(Modifier.ABSTRACT);
				if (methodIsAbstract)
					part.continueLine(";");
				part.endLine();
				if (isWrapInterimWithoutBody())
				{
					if (methodIsAbstract)
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "abstract method must not be annotated with \"@WrapInterim(methodBody=false)\"", getElement(mt));
					else
						code.addLine("{ throw new java.lang.RuntimeException(\"@WrapInterim(methodBody=false)\"); }");
				}
				else
				{
					if (!methodIsAbstract)
						code.addLine(mt.getBody().toString());
				}
			}
			else if (mt.getModifiers().getFlags().contains(Modifier.STATIC))
			{
				staticDropped.add(getCurrentClass().getQualifiedName() + "." + mt.getName());
			}
			return null;
		}

		private TypeElement getCurrentClass()
		{
			return requireNonNull(currentClassStack.peek());
		}

		private <A extends Annotation> A getCurrentPathAnnotation(final Class<A> annotationType)
		{
			final Element element = docTrees.getElement(getCurrentPath());
			return element.getAnnotation(annotationType);
		}

		private boolean isWrapInterim()
		{
			final WrapInterim anno = getCurrentPathAnnotation(WrapInterim.class);
			checkWrapInterimAnnotation(anno);
			return anno!=null || getCurrentPathAnnotation(WrapImplementsInterim.class)!=null;
		}

		private void checkWrapInterimAnnotation(final WrapInterim anno)
		{
			if (anno==null || anno.methodBody())
				return;

			final Element element = docTrees.getElement(getCurrentPath());
			final ElementKind kind = element.getKind();
			//noinspection EnumSwitchStatementWhichMissesCases
			switch(kind)
			{
				case METHOD:
				case CONSTRUCTOR:
					break;
				default:
					//noinspection OptionalGetWithoutIsPresent OK: should fail if not present
					processingEnv.getMessager().printMessage(
							Diagnostic.Kind.ERROR,
							"methodBody=false " +
							"is allowed for methods and constructors only, " +
							"but was " + kind,
							element,
							element.
									getAnnotationMirrors().
									stream().
									filter(m -> m.getAnnotationType().toString().equals(
											WrapInterim.class.getName())).
									findFirst().
									get());
					break;
			}
		}

		private boolean isWrapInterimWithoutBody()
		{
			final WrapInterim anno = getCurrentPathAnnotation(WrapInterim.class);
			return anno!=null && !anno.methodBody();
		}

		private boolean isWrapperIgnore()
		{
			return getCurrentPathAnnotation(WrapperIgnore.class)!=null;
		}

		private CompilationUnitTree getCompilationUnit()
		{
			return getCurrentPath().getCompilationUnit();
		}

		@Override
		public Void visitLambdaExpression(final LambdaExpressionTree let, final Void p)
		{
			// stop descending
			return null;
		}

		private void addRequiredTypes(final Tree typeTree)
		{
			//noinspection EnumSwitchStatementWhichMissesCases
			switch (typeTree.getKind())
			{
				case IDENTIFIER:
				case MEMBER_SELECT:
					final TypeElement typeElement = (TypeElement)getRequiredElement(typeTree);
					requiredTypes.add(typeElement);
					break;
				case PARAMETERIZED_TYPE:
					final ParameterizedTypeTree ptt = (ParameterizedTypeTree)typeTree;
					addRequiredTypes(ptt.getType());
					for (final Tree typeArgument : ptt.getTypeArguments())
					{
						addRequiredTypes(typeArgument);
					}
					break;
				case ARRAY_TYPE:
					addRequiredTypes(((ArrayTypeTree)typeTree).getType());
					break;
				case EXTENDS_WILDCARD:
					addRequiredTypes(((WildcardTree)typeTree).getBound());
					break;
				case ERRONEOUS:
				case PRIMITIVE_TYPE:
				case UNBOUNDED_WILDCARD:
					// nothing to do
					break;
				default:
					throw new RuntimeException(typeTree.getKind().name()+": "+typeTree+" / "+typeTree.getClass());
			}
		}

		@Override
		public Void visitVariable(final VariableTree vt, final Void p)
		{
			if (vt.getType()==null)
			{
				throw new NullPointerException(vt+" in "+getCompilationUnit());
			}
			final VariableElement ve = (VariableElement)getElement(vt);
			if (addVariable(ve, vt))
			{
				addRequiredTypes(vt.getType());
				final LineCodePart part = code.startLine(toStringWithoutMostAnnotations(vt.getModifiers()));
				part.continueLine(" ");
				part.continueLine(vt.getType().toString());
				part.continueLine(" ");
				part.continueLine(vt.getName().toString());
				if (ve.getConstantValue()!=null)
				{
					part.continueLine("=");
					part.continueLine(LiteralHelper.getLiteralFor(ve.getConstantValue()));
				}
				else if (vt.getInitializer()!=null)
				{
					part.continueLine("=");
					part.continueLine(vt.getInitializer().toString());
				}
				part.continueLine(";");
				part.endLine();
			}
			else if (vt.getModifiers().getFlags().contains(Modifier.STATIC))
			{
				staticDropped.add(getCurrentClass().getQualifiedName() + "." + vt.getName());
			}
			return super.visitVariable(vt, p);
		}

		private boolean addVariable(final VariableElement ve, final VariableTree vt)
		{
			if (isWrapInterim())
				return true;
			if (ve.getConstantValue()!=null)
				return true;
			final TreePath path = docTrees.getPath(getCompilationUnit(), vt.getType());
			final Element typeElement = docTrees.getElement(path);
			if (!(typeElement instanceof TypeElement)) // null for primitive types; maybe something else for generics
				return false;
			final TypeElement type = (TypeElement)typeElement;
			final VariableElement variable = (VariableElement)docTrees.getElement(getCurrentPath());
			return currentClassIsFeatureContainer()
				&& variable.getModifiers().containsAll(asList(Modifier.STATIC, Modifier.FINAL))
				&& !isWrapperIgnore()
				&& type.getAnnotation(WrapFeature.class)!=null;
		}

		private void removeImports(final String importPrefix)
		{
			imports.removeIf(nextImport -> nextImport.startsWith(importPrefix));
		}

		private void finish(final InMemoryCompiler compiler)
		{
			if (writeInterimFile())
			{
				final String sourceChars;
				try (final StringWriter w = new StringWriter())
				{
					if (packageStatement!=null)
						w.write(packageStatement+System.lineSeparator());
					for (final Import nextImport : imports)
					{
						w.write(nextImport.getImportStatement());
					}
					code.write(w, -1);
					sourceChars = w.toString();
				}
				catch (final IOException e)
				{
					throw new RuntimeException(e);
				}
				compiler.addJavaFile(getSourcePath(sourceFile), sourceChars);
			}
		}

		private Path getSourcePath(final JavaFileObject originalFileObject)
		{
			final Path originalFile = Paths.get(originalFileObject.toUri());
			final Path originalPath = originalFile.toAbsolutePath();
			for (final File sourceDirectory : params.getSourceDirectories())
			{
				final Path sourcePath = sourceDirectory.toPath().toAbsolutePath();
				if (originalPath.startsWith(sourcePath))
				{
					return sourcePath.relativize(originalPath);
				}
			}
			throw new RuntimeException();
		}

		private List<AnnotationTree> getInterimAnnotations(final List<? extends AnnotationTree> annotations)
		{
			final List<AnnotationTree> interimAnnotations = new ArrayList<>(annotations.size());
			for (final AnnotationTree annotation : annotations)
			{
				final TypeElement annotationType = (TypeElement)getElement(annotation.getAnnotationType());
				if (annotationType.getAnnotation(WrapAnnotateInterim.class)!=null)
					interimAnnotations.add(annotation);
			}
			return interimAnnotations;
		}

		private CharSequence toStringWithoutMostAnnotations(final ModifiersTree modifiers)
		{
			final StringBuilder result = new StringBuilder();
			for (final AnnotationTree annotation : getInterimAnnotations(modifiers.getAnnotations()))
			{
				final TypeElement annotationType = (TypeElement)getElement(annotation.getAnnotationType());
				if (annotationType.getAnnotation(WrapAnnotateInterim.class)!=null)
					result.append(annotation).append(" ");
			}
			for (final Modifier modifier : modifiers.getFlags())
			{
				result.append(modifier).append(" ");
			}
			return result;
		}

		private class CollectEnumValuesVisitor extends TreeScanner<Void, Void>
		{
			private final Element enumTypeElement;
			final List<VariableTree> enumValues = new ArrayList<>();

			CollectEnumValuesVisitor(final Element enumTypeElement)
			{
				this.enumTypeElement = enumTypeElement;
			}

			@Override
			public Void visitMethod(final MethodTree mt, final Void p)
			{
				return null;
			}

			@Override
			public Void visitVariable(final VariableTree vt, final Void p)
			{
				final Set<Modifier> flags = vt.getModifiers().getFlags();
				if (flags.contains(Modifier.STATIC) &&
					 flags.contains(Modifier.FINAL) &&
					 enumTypeElement.equals(getElement(vt.getType())) &&
					 vt.getInitializer().getKind()==Tree.Kind.NEW_CLASS)
				{
					enumValues.add(vt);
				}
				return null;
			}
		}
	}

	private static class Code
	{
		private final Name name;
		private final Code parent;
		private final List<CodePart> parts = new ArrayList<>();
		private boolean required;

		Code(final Name name, final Code parent, final boolean required)
		{
			this.name = name;
			this.parent = parent;
			this.required = required;
		}

		Code openBlock(final Name name, final CharSequence declaration, final boolean required)
		{
			final SubCodePart sub = new SubCodePart(name, this, required);
			parts.add(sub);
			sub.code.addLine(declaration, false);
			sub.code.addLine("{", false);
			return sub.code;
		}

		Code closeBlock()
		{
			addLine("}", false);
			return requireNonNull(parent);
		}

		LineCodePart startLine(final CharSequence line)
		{
			return startLine(line, true);
		}

		LineCodePart startLine(final CharSequence line, final boolean indented)
		{
			final LineCodePart part = new LineCodePart(indented);
			parts.add(part);
			part.append(line);
			return part;
		}

		void addLine(final CharSequence line)
		{
			addLine(line, true);
		}

		void addLine(final CharSequence line, final boolean indented)
		{
			final LineCodePart part = startLine(line, indented);
			part.endLine();
		}

		void write(final Writer w, final int indent) throws IOException
		{
			if (required)
				for (final CodePart part : parts)
				{
					part.write(w, indent);
				}
		}

		void require()
		{
			Code c = this;
			while (c!=null)
			{
				c.required = true;
				c = c.parent;
			}
		}

		private void removeImportsForUnrequired(final InterimVisitor interimVisitor)
		{
			if (!required)
			{
				interimVisitor.removeImports(name.toString());
				return;
			}
			for (final CodePart part : parts)
			{
				if (part instanceof SubCodePart)
					((SubCodePart)part).code.removeImportsForUnrequired(interimVisitor);
			}
		}
	}

	private abstract static class CodePart
	{
		abstract void write(final Writer w, final int indent) throws IOException;
	}

	private static class LineCodePart extends CodePart
	{
		private final StringBuilder line = new StringBuilder();
		private boolean closed = false;
		private final boolean indented;

		LineCodePart(final boolean indented)
		{
			this.indented = indented;
		}

		void continueLine(final CharSequence line)
		{
			append(line);
		}

		void endLine()
		{
			append(System.lineSeparator());
			closed = true;
		}

		void append(final CharSequence cs)
		{
			if (closed) throw new RuntimeException();
			line.append(cs);
		}

		@Override
		void write(final Writer w, final int indent) throws IOException
		{
			for (int i=0; i<indent; i++)
				w.write('\t');
			if (indented)
				w.write('\t');
			w.write(line.toString());
		}
	}

	private static class SubCodePart extends CodePart
	{
		final Code code;

		SubCodePart(final Name name, final Code parent, final boolean required)
		{
			this.code = new Code(name, parent, required);
		}

		@Override
		void write(final Writer w, final int indent) throws IOException
		{
			code.write(w, indent+1);
		}
	}
}
