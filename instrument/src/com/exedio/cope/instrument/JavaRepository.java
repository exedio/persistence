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

import bsh.Interpreter;
import bsh.UtilEvalError;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockActivationParameters;
import com.exedio.cope.pattern.BlockType;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.pattern.Money;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.cojen.classfile.ClassFile;
import org.cojen.classfile.CodeBuilder;
import org.cojen.classfile.MethodInfo;
import org.cojen.classfile.Modifiers;
import org.cojen.classfile.TypeDesc;
import org.cojen.util.ClassInjector;

final class JavaRepository
{
	/**
	 * Defines a name space, that does not depend on
	 * information gathered by the instrumentor,
	 * thus can be used in build stage.
	 * Using this in JavaFile greatly reduces number of top name spaces,
	 * for which a new BshClassManager must be created.
	 */
	final CopeNameSpace externalNameSpace = new CopeNameSpace(null, "external");

	// reusing externalNameSpace is more efficient than another root nameSpace
	final CopeNameSpace nameSpace = new NS(externalNameSpace);

	final Interpreter interpreter = new Interpreter();

	static enum Stage
	{
		BUILD,
		BETWEEN,
		GENERATE;
	}

	Stage stage = Stage.BUILD;

	private final ArrayList<JavaFile> files = new ArrayList<>();
	private final HashMap<String, JavaClass> javaClassBySimpleName = new HashMap<>();
	private final HashMap<String, JavaClass> javaClassByFullName = new HashMap<>();

	private final HashMap<JavaClass, CopeType> copeTypeByJavaClass = new HashMap<>();

	void endBuildStage()
	{
		assert stage==Stage.BUILD;
		stage = Stage.BETWEEN;

		// TODO put this into a new class CopeType
		for(final JavaClass javaClass : javaClassByFullName.values())
		{
			if(javaClass.isInterface())
				continue;

			final boolean isItem = isItem(javaClass);
			final boolean isBlock = isBlock(javaClass);
			final boolean isComposite = isComposite(javaClass);
			if(isItem||isBlock||isComposite)
			{
				final CopeType type = new CopeType(javaClass, isBlock, isComposite);

				feature: for(final JavaField javaField : javaClass.getFields())
				{
					final int modifier = javaField.modifier;
					if(!Modifier.isFinal(modifier) || !Modifier.isStatic(modifier))
						continue feature;

					final String docComment = javaField.getDocComment();
					if(docComment!=null && docComment.indexOf('@' + CopeFeature.TAG_PREFIX + "ignore")>=0)
						continue feature;

					final Class<?> typeClass = javaField.file.findTypeExternally(javaField.typeRaw);
					if(typeClass==null)
						continue feature;

					if(typeClass.isAnnotationPresent(WrapFeature.class))
						new CopeFeature(type, javaField);
				}
			}
		}

		stage = Stage.GENERATE;

		for(final CopeType ct : copeTypeByJavaClass.values())
			ct.endBuildStage();
	}

	boolean isBuildStage()
	{
		return stage==Stage.BUILD;
	}

	boolean isGenerateStage()
	{
		return stage==Stage.GENERATE;
	}

	boolean isItem(JavaClass javaClass)
	{
		//System.out.println("--------------"+javaClass.getFullName());
		while(true)
		{
			final String classExtends = javaClass.classExtends;
			if(classExtends==null)
				return false;

			//System.out.println("--------------**"+javaClass.getFullName());
			{
				final Class<?> extendsClass = javaClass.file.findTypeExternally(classExtends);
				//System.out.println("--------------*1"+extendsClass);
				if(extendsClass!=null)
					return Item.class.isAssignableFrom(extendsClass);
			}
			{
				final JavaClass byName = getJavaClass(classExtends);
				//System.out.println("--------------*2"+byName);
				if(byName!=null)
				{
					javaClass = byName;
					continue;
				}
			}
			System.out.println("unknown type " + classExtends + " in " + javaClass);
			return false;
		}
	}

	static boolean isBlock(final JavaClass javaClass)
	{
		final String classExtends = javaClass.classExtends;
		if(classExtends==null)
			return false;

		final Class<?> extendsClass = javaClass.file.findTypeExternally(classExtends);
		if(extendsClass!=null)
			return Block.class.isAssignableFrom(extendsClass);

		return false;
	}

	static boolean isComposite(final JavaClass javaClass)
	{
		final String classExtends = javaClass.classExtends;
		if(classExtends==null)
			return false;

		final Class<?> extendsClass = javaClass.file.findTypeExternally(classExtends);
		if(extendsClass!=null)
			return Composite.class.isAssignableFrom(extendsClass);

		return false;
	}

	void add(final JavaFile file)
	{
		assert stage==Stage.BUILD;
		files.add(file);
	}

	final List<JavaFile> getFiles()
	{
		assert stage==Stage.GENERATE;
		return files;
	}

	void add(final JavaClass javaClass)
	{
		assert stage==Stage.BUILD;

		//final JavaClass previous =
		javaClassBySimpleName.put(javaClass.name, javaClass);

		//if(previous!=null) System.out.println("collision:"+previous.getFullName()+','+javaClass.getFullName());

		if(javaClassByFullName.put(javaClass.getFullName(), javaClass)!=null)
			throw new RuntimeException(javaClass.getFullName());
	}

	final JavaClass getJavaClass(final String name)
	{
		if(name.indexOf('.')<0)
		{
			return javaClassBySimpleName.get(name);
		}
		else
		{
			final JavaClass byFullName = javaClassByFullName.get(name);
			if(byFullName!=null)
				return byFullName;

			// for inner classes
			final int dot = name.indexOf('.'); // cannot be negative in else branch
			final JavaClass outer = javaClassBySimpleName.get(name.substring(0, dot));
			if(outer!=null)
				return javaClassByFullName.get(outer.file.getPackageName() + '.' + name.replace('.', '$'));

			return null;
		}
	}

	void add(final CopeType copeType)
	{
		assert stage==Stage.BETWEEN;

		if(copeTypeByJavaClass.put(copeType.javaClass, copeType)!=null)
			throw new RuntimeException(copeType.javaClass.getFullName());
		//System.out.println("--------- put cope type: "+name);
	}

	CopeType getCopeType(final String className)
	{
		assert stage==Stage.BETWEEN || stage==Stage.GENERATE;

		final JavaClass javaClass = getJavaClass(className);
		if(javaClass==null)
			throw new RuntimeException("no java class for "+className);

		final CopeType result = copeTypeByJavaClass.get(javaClass);
		if(result==null)
			throw new RuntimeException("no cope type for "+className);

		return result;
	}

	/**
	 * Classes of non-toplevel types must override this constant
	 * for working around http://bugs.java.com/view_bug.do?bug_id=7101374
	 */
	@SuppressFBWarnings("NM_CLASS_NAMING_CONVENTION")
	public static final class classWildcard
	{
		public static final Class<Wildcard> value = Wildcard.class;
	}

	private static final class Wildcard extends Item
	{
		private static final long serialVersionUID = 1l;
	}

	@SuppressFBWarnings("SE_BAD_FIELD_INNER_CLASS") // Non-serializable class has a serializable inner class
	private final class NS extends CopeNameSpace
	{
		private static final long serialVersionUID = 1l;

		NS(final CopeNameSpace parent)
		{
			super(parent, "repository");
		}

		@Override
		public Class<?> getClass(final String name) throws UtilEvalError
		{
			assert stage==Stage.GENERATE;

			final Class<?> superResult = super.getClass(name);
			if(superResult!=null)
				return superResult;

			if(name.endsWith("$classWildcard"))
				return classWildcard.class;

			final JavaClass javaClass = getJavaClass(name);
			if(javaClass!=null)
			{
				//System.out.println("++++++++++++++++getClass(\""+name+"\") == "+javaClass+","+javaClass.isEnum);
				if(javaClass.isEnum)
					return EnumBeanShellHackClass.class;
				if(isItem(javaClass))
				{
					final ClassFile cf =
						new ClassFile(javaClass.getFullName(), Item.class);
					addDelegateConstructor(cf,
							Modifiers.PUBLIC, TypeDesc.forClass(SetValue.class).toArrayType());
					return define(cf);
				}
				if("Block".equals(javaClass.classExtends)) // TODO does not work with subclasses an with fully qualified class names
				{
					return DummyBlock.class;
				}
				if("Composite".equals(javaClass.classExtends)) // TODO does not work with subclasses an with fully qualified class names
				{
					return DummyComposite.class;
				}
			}

			return null;
		}

		private final Class<?> define(final ClassFile cf)
		{
			return ClassInjector.createExplicit(
					cf.getClassName(), getClass().getClassLoader()).defineClass(cf);
		}

		private final void addDelegateConstructor(final ClassFile cf, final Modifiers modifiers, final TypeDesc... args)
		{
			final MethodInfo creator = cf.addConstructor(modifiers, args);
			final CodeBuilder cb = new CodeBuilder(creator);
			cb.loadThis();
			for(int i = 0; i<args.length; i++)
				cb.loadLocal(cb.getParameter(i));
			cb.invokeSuperConstructor(args);
			cb.returnVoid();
		}
	}

	// BEWARE
	// The name of this enum and its only enum value
	// must match the names used in the hack of the beanshell.
	// see bsh-core.PATCH
	public static enum EnumBeanShellHackClass implements Money.Currency
	{
		BEANSHELL_HACK_ATTRIBUTE;
	}

	public static final class DummyBlock extends Block
	{
		private static final long serialVersionUID = 1l;
		public static final BlockType<DummyBlock> TYPE = BlockType.newType(DummyBlock.class);
		private DummyBlock(final BlockActivationParameters ap) { super(ap); }
	}

	static final class DummyComposite extends Composite
	{
		protected DummyComposite(final SetValue<?>... setValues) { super(setValues); }
		private static final long serialVersionUID = 1l;
	}
}
