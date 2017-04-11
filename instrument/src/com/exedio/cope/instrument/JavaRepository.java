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
import com.exedio.cope.pattern.Money;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

final class JavaRepository
{
	/**
	 * Defines a name space, that does not depend on
	 * information gathered by the instrumentor,
	 * thus can be used in build stage.
	 * Using this in JavaFile greatly reduces number of top name spaces,
	 * for which a new BshClassManager must be created.
	 */
	final CopeNameSpace externalNameSpace;

	// reusing externalNameSpace is more efficient than another root nameSpace
	final CopeNameSpace nameSpace;

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
	private final HashMap<String, JavaClass> javaClassByCanonicalName = new HashMap<>();
	private final HashMap<String,List<JavaClass>> problematicSimpleNames = new HashMap<>();

	private final HashMap<JavaClass, LocalCopeType> copeTypeByJavaClass = new HashMap<>();

	public JavaRepository(final ClassLoader cl)
	{
		externalNameSpace = new CopeNameSpace(null, "external");
		externalNameSpace.getClassManager().setClassLoader(cl);
		nameSpace = new NS(externalNameSpace);
	}

	void endBuildStage()
	{
		assert stage==Stage.BUILD;
		stage = Stage.BETWEEN;

		// TODO put this into a new class CopeType
		for(final JavaClass javaClass : javaClassByCanonicalName.values())
		{
			if(javaClass.isInterface())
				continue;

			if(javaClass.kind!=null)
			{
				new LocalCopeType(javaClass, javaClass.kind);
			}
		}

		stage = Stage.GENERATE;

		for(final LocalCopeType ct : copeTypeByJavaClass.values())
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

	void add(final JavaFile file)
	{
		assert stage==Stage.BUILD;
		files.add(file);
	}

	List<JavaFile> getFiles()
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

		if(javaClassByCanonicalName.put(javaClass.getCanonicalName(), javaClass)!=null)
			throw new RuntimeException(javaClass.getCanonicalName());
	}

	private JavaClass resolveBySimpleName(final String name)
	{
		final JavaClass result=javaClassBySimpleName.get(name);
		final List<JavaClass> problematicClasses=problematicSimpleNames.remove(name);
		if (result!=null && problematicClasses!=null)
		{
			final Kind resultKind=result.kind;
			for (final JavaClass checkProblematicClass: problematicClasses)
			{
				if (!Objects.equals(checkProblematicClass.kind, resultKind)
					|| result.isEnum!=checkProblematicClass.isEnum)
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

	static final String DUMMY_ITEM_PREFIX = "com.exedio.cope.DummyItem$";

	JavaClass getJavaClass(final String name)
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
			final JavaClass byCanonicalName = javaClassByCanonicalName.get(name);
			if(byCanonicalName!=null)
				return byCanonicalName;

			// for inner classes
			final int dot = name.indexOf('.'); // cannot be negative in else branch
			final JavaClass outer = resolveBySimpleName(name.substring(0, dot));
			if(outer!=null)
				return javaClassByCanonicalName.get(outer.file.getPackageName() + '.' + name);

			return null;
		}
	}

	void add(final LocalCopeType copeType)
	{
		assert stage==Stage.BETWEEN;

		if(copeTypeByJavaClass.putIfAbsent(copeType.javaClass, copeType)!=null)
			throw new RuntimeException(copeType.javaClass.getFullName());
		//System.out.println("--------- put cope type: "+name);
	}

	LocalCopeType getCopeType(final String className)
	{
		assert stage==Stage.BETWEEN || stage==Stage.GENERATE;

		final JavaClass javaClass = getJavaClass(className);
		if(javaClass==null)
			throw new RuntimeException("no java class for "+className);

		final LocalCopeType result = copeTypeByJavaClass.get(javaClass);
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
				if(javaClass.kind!=null)
					return javaClass.kind.dummy;
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
}
