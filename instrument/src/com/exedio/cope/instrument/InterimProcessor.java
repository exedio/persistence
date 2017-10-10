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
import static java.util.Collections.unmodifiableList;
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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
final class InterimProcessor extends JavacProcessor
{
	private final Path targetDirectory;
	private final Params params;

	private List<File> interimFiles;

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

	@Override
	boolean includeIgnoredFiles()
	{
		return true;
	}

	Path getInterimRootDirectory()
	{
		return targetDirectory;
	}

	List<File> getInterimFiles()
	{
		if (interimFiles==null) throw new RuntimeException("not initialized");
		if (interimFiles.isEmpty()) throw new RuntimeException("no interim files");
		return unmodifiableList(interimFiles);
	}

	private Path getTargetFile(final JavaFileObject originalFileObject)
	{
		final Path originalFile = Paths.get(originalFileObject.toUri());
		final Path originalPath = originalFile.toAbsolutePath();
		for (final File sourceDirectory : params.getSourceDirectories())
		{
			final Path sourcePath = sourceDirectory.toPath().toAbsolutePath();
			if (originalPath.startsWith(sourcePath))
			{
				final Path relative = sourcePath.relativize(originalPath);
				return targetDirectory.resolve(relative);

			}
		}
		throw new RuntimeException();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		if (roundEnv.getRootElements().isEmpty())
			return false;
		if (interimFiles!=null) throw new RuntimeException();
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
		final List<File> interimFiles = new ArrayList<>();
		for (final InterimVisitor interimVisitor : interimVisitors)
		{
			final File file = interimVisitor.finish();
			if (file!=null)
				interimFiles.add(file);
		}
		this.interimFiles = interimFiles;
		return true;
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
			if (classNames!=null) throw new RuntimeException();
			if (sourceFile!=null) throw new RuntimeException();

			classNames = new ArrayList<>();
			for (final Tree typeDecl : cut.getTypeDecls())
			{
				final TypeElement typeElement = (TypeElement)docTrees.getElement(docTrees.getPath(cut, typeDecl));
				classNames.add(typeElement.getQualifiedName());
			}

			sourceFile = cut.getSourceFile();

			if (cut.getPackageName()!=null)
				packageStatement = "package "+cut.getPackageName()+";";

			if (isFileIgnored(sourceFile))
				return null;
			else
				return super.visitCompilationUnit(cut, p);
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
			final Kind kind = Kind.valueOf(element.getAnnotation(WrapType.class));
			if (ct.getKind()==Tree.Kind.ANNOTATION_TYPE) return null;
			if (ct.getSimpleName().length()==0) return null;
			currentClassStack.addFirst(element);
			final StringBuilder declaration = new StringBuilder();
			declaration.append(toStringWithoutMostAnnotations(ct.getModifiers())).append(getTypeToken(ct)).append(" ").append(ct.getSimpleName());
			if (!ct.getTypeParameters().isEmpty())
			{
				declaration.append("<");
				for (final Iterator<? extends TypeParameterTree> iter = ct.getTypeParameters().iterator(); iter.hasNext();)
				{
					final TypeParameterTree typeParameter = iter.next();
					declaration.append(typeParameter);
					if (iter.hasNext())
						declaration.append(",");
				}
				declaration.append(">");
			}
			if (ct.getExtendsClause()!=null)
			{
				declaration.append(" extends ").append(ct.getExtendsClause());
			}
			final List<TypeElement> implementedInterfaces = appendImplements(declaration, ct);
			code = code.openBlock(element.getQualifiedName(), declaration, false);
			blockRegistry.put(element.getQualifiedName(), code);
			if (ct.getKind()==Tree.Kind.ENUM)
			{
				final LineCodePart line = code.startLine("");
				final CollectEnumValuesVisitor enumCollector = new CollectEnumValuesVisitor();
				enumCollector.visitClass(ct, null);
				for (final Iterator<VariableTree> it = enumCollector.enumValues.iterator(); it.hasNext();)
				{
					final VariableTree enumValue = it.next();
					for (final AnnotationTree interimAnnotation : getInterimAnnotations(enumValue.getModifiers().getAnnotations()))
					{
						line.continueLine(interimAnnotation+" ");
					}
					line.continueLine(enumValue.getName().toString());
					if (it.hasNext())
						line.continueLine(", ");
				}
				line.endLine();
			}
			if (kind!=null || isWrapInterim())
				code.require();
			final Void result = super.visitClass(ct, p);
			for (final TypeElement implementedInterface : implementedInterfaces)
			{
				for (final Element enclosedElement : implementedInterface.getEnclosedElements())
				{
					if (enclosedElement.getKind()==ElementKind.METHOD)
					{
						final ExecutableElement method = (ExecutableElement)enclosedElement;
						if (method.isDefault())
							continue;
						final StringBuilder methodDeclaration = new StringBuilder();
						methodDeclaration.
							append("@java.lang.Override public ").
							append(method.getReturnType()).
							append(" ").
							append(method.getSimpleName()).
							append("(");
						for (final Iterator<? extends VariableElement> iter = method.getParameters().iterator(); iter.hasNext();)
						{
							final VariableElement parameter = iter.next();
							methodDeclaration.
								append(parameter.asType()).
								append(" ").
								append(parameter.getSimpleName());
							if (iter.hasNext())
								methodDeclaration.append(", ");
						}
						methodDeclaration.append(")");
						code = code.openBlock(null, methodDeclaration, true);
						code.addLine("throw new RuntimeException(\"don't call in interim code\");");
						code = code.closeBlock();
					}
				}
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
				if (kind.type!=null && wrapperType.type()!=Visibility.NONE)
					code.addLine(
						"public static final "+kind.type.field+"<"+ct.getSimpleName()+"> TYPE = "+
							kind.type.factory+".newType("+ct.getSimpleName()+".class);"
					);
			}
			code = code.closeBlock();
			currentClassStack.removeFirst();
			return result;
		}

		private String getTypeToken(final ClassTree ct)
		{
			//noinspection EnumSwitchStatementWhichMissesCases
			switch (ct.getKind())
			{
				case CLASS: return "class";
				case INTERFACE: return "interface";
				case ENUM: return "enum";
				default: throw new RuntimeException(ct.getKind().name());
			}
		}

		private boolean currentClassIsFeatureContainer()
		{
			final TypeElement currentClass = requireNonNull(currentClassStack.peek());
			return currentClass.getAnnotation(WrapType.class)!=null;
		}

		/**
		 * @return the implemented interfaces that have {@link WrapImplementsInterim#addMethods()} set to true
		 */
		private List<TypeElement> appendImplements(final StringBuilder sb, final ClassTree ct)
		{
			final List<Tree> implementsInterim = new ArrayList<>();
			final List<TypeElement> implementsInterimTypes = new ArrayList<>();
			for (final Tree implementsClause : ct.getImplementsClause())
			{
				final TypeElement implementsType = (TypeElement) getElement(implementsClause);
				final WrapImplementsInterim wrapImplementsInterim = implementsType.getAnnotation(WrapImplementsInterim.class);
				if (wrapImplementsInterim!=null)
				{
					implementsInterim.add(implementsClause);
					if (wrapImplementsInterim.addMethods())
						implementsInterimTypes.add(implementsType);
				}
			}
			if (!implementsInterim.isEmpty())
			{
				sb.append(" implements ");
				for (final Iterator<? extends Tree> iter = implementsInterim.iterator(); iter.hasNext();)
				{
					final Tree implementsClause = iter.next();
					sb.append(implementsClause);
					if (iter.hasNext())
						sb.append(", ");
				}
			}
			return implementsInterimTypes;
		}

		@Override
		public Void visitMethod(final MethodTree mt, final Void p)
		{
			if (isWrapInterim())
			{
				final LineCodePart part = code.startLine(toStringWithoutMostAnnotations(mt.getModifiers()));
				part.continueLine(" ");
				part.continueLine(mt.getReturnType().toString());
				part.continueLine(" ");
				part.continueLine(mt.getName().toString());
				part.continueLine("(");
				for (final Iterator<? extends VariableTree> iter = mt.getParameters().iterator(); iter.hasNext();)
				{
					final VariableTree parameter = iter.next();
					part.continueLine(parameter.toString());
					if (iter.hasNext())
						part.continueLine(", ");
				}
				part.continueLine(")");
				part.endLine();
				code.addLine(mt.getBody().toString());
			}
			else if (mt.getModifiers().getFlags().contains(Modifier.STATIC))
			{
				staticDropped.add(requireNonNull(currentClassStack.peek()).getQualifiedName()+"."+mt.getName());
			}
			return null;
		}

		private <A extends Annotation> A getCurrentPathAnnotation(final Class<A> annotationType)
		{
			final Element element = docTrees.getElement(getCurrentPath());
			return element.getAnnotation(annotationType);
		}

		private boolean isGenerated()
		{
			return getCurrentPathAnnotation(Generated.class)!=null;
		}

		private boolean isWrapInterim()
		{
			return getCurrentPathAnnotation(WrapInterim.class)!=null || getCurrentPathAnnotation(WrapImplementsInterim.class)!=null;
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
				staticDropped.add(requireNonNull(currentClassStack.peek()).getQualifiedName()+"."+vt.getName());
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
			if (isGenerated())
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

		private File finish()
		{
			if (writeInterimFile())
			{
				final Path file = getTargetFile(sourceFile);
				try
				{
					Files.createDirectories(file.getParent());
				}
				catch (final IOException e)
				{
					throw new RuntimeException(e);
				}
				try (final Writer w = new OutputStreamWriter(Files.newOutputStream(file), params.charset))
				{
					if (packageStatement!=null)
						w.write(packageStatement+System.lineSeparator());
					for (final Import nextImport : imports)
					{
						w.write(nextImport.getImportStatement());
					}
					code.write(w, -1);
				}
				catch (final IOException e)
				{
					throw new RuntimeException(e);
				}
				return file.toFile();
			}
			else
			{
				return null;
			}
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

		void continueLine(final String line)
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

	private static class CollectEnumValuesVisitor extends TreeScanner<Void, Void>
	{
		final List<VariableTree> enumValues = new ArrayList<>();

		@Override
		public Void visitMethod(final MethodTree mt, final Void p)
		{
			return null;
		}

		@Override
		public Void visitVariable(final VariableTree vt, final Void p)
		{
			final Set<Modifier> flags = vt.getModifiers().getFlags();
			if (flags.contains(Modifier.STATIC) && flags.contains(Modifier.FINAL))
			{
				enumValues.add(vt);
			}
			return null;
		}
	}
}
