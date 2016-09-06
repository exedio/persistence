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
import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
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
	private final HashMap<String,List<JavaClass>> problematicSimpleNames = new HashMap<>();

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

					final String docComment = javaField.docComment;
					if(Tags.cascade(javaField, Option.forIgnore(docComment), javaField.wrapperIgnore, null)!=null)
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
		try
		{
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
		catch (NoClassDefFoundError e)
		{
			throw new RuntimeException("error analyzing "+javaClass.getFullName(), e);
		}
	}

	static boolean isBlock(final JavaClass javaClass)
	{
		try
		{
			final String classExtends = javaClass.classExtends;
			if(classExtends==null)
				return false;

			final Class<?> extendsClass = javaClass.file.findTypeExternally(classExtends);
			if(extendsClass!=null)
				return Block.class.isAssignableFrom(extendsClass);

			return false;
		}
		catch (NoClassDefFoundError e)
		{
			throw new RuntimeException("error analyzing "+javaClass.getFullName(), e);
		}
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

		final JavaClass previous=javaClassBySimpleName.put(javaClass.name, javaClass);

		if(previous!=null)
		{
			List<JavaClass> classes=problematicSimpleNames.get(previous.name);
			if (classes==null)
			{
				problematicSimpleNames.put(previous.name, classes=new ArrayList<>());
				classes.add(previous);
			}
			classes.add(javaClass);
		}

		if(javaClassByFullName.put(javaClass.getFullName(), javaClass)!=null)
			throw new RuntimeException(javaClass.getFullName());
	}

	private JavaClass resolveBySimpleName(final String name)
	{
		final JavaClass result=javaClassBySimpleName.get(name);
		final List<JavaClass> problematicClasses=problematicSimpleNames.remove(name);
		if (result!=null && problematicClasses!=null)
		{
			final boolean resultIsItem=isItem(result);
			final boolean resultIsBlock=isBlock(result);
			for (final JavaClass checkProblematicClass: problematicClasses)
			{
				if (isItem(checkProblematicClass)!=resultIsItem || isBlock(checkProblematicClass)!=resultIsBlock || result.isEnum!=checkProblematicClass.isEnum)
				{
					System.out.println("Problem resolving '"+name+"' - could be one of ...");
					for (final JavaClass logProblematicClass: problematicClasses)
					{
						logProblematicClass.reportSourceProblem(JavaFeature.Severity.warning, "non-unique simple name "+name, null);
					}
					System.out.println("Will use "+result);
					System.out.println("Try avoiding this, for example by <ignore>ing classes in the <instrument> call.");
					System.out.println("");
					break;
				}
			}
		}
		return result;
	}

	static final String DUMMY_ITEM_PREFIX = DummyItem.class.getName() + "$";

	final JavaClass getJavaClass(final String name)
	{
		if(name.indexOf('.')<0)
		{
			return resolveBySimpleName(name);
		}
		else if(name.startsWith(DUMMY_ITEM_PREFIX))
		{
			final String s = name.substring(DUMMY_ITEM_PREFIX.length(), name.length());
			return resolveBySimpleName(s);
		}
		else
		{
			final JavaClass byFullName = javaClassByFullName.get(name);
			if(byFullName!=null)
				return byFullName;

			// for inner classes
			final int dot = name.indexOf('.'); // cannot be negative in else branch
			final JavaClass outer = resolveBySimpleName(name.substring(0, dot));
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
		Class<?> getClassInternal(final String name) throws UtilEvalError
		{
			assert stage==Stage.GENERATE;

			final Class<?> superResult = super.getClassInternal(name);
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
					return DummyItem.class;
				}
				if(isBlock(javaClass))
				{
					return DummyBlock.class;
				}
				if(isComposite(javaClass))
				{
					return DummyComposite.class;
				}
			}

			return null;
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

	public static final class DummyItem extends Item
	{
		private static final long serialVersionUID = 1l;
		public static final Type<DummyItem> TYPE = TypesBound.newType(DummyItem.class);
		private DummyItem(final ActivationParameters ap) { super(ap); }
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
