/*
 * Copyright (C) 2000  Ralf Wiebicke
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

import bsh.EvalError;
import bsh.Primitive;
import bsh.UtilEvalError;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a class.
 * Is an inner class, if parent is not null.
 *
 * @author Ralf Wiebicke
 */
final class JavaClass extends JavaFeature
{
	final CopeNameSpace nameSpace;

	private final HashMap<String, JavaField> fields = new HashMap<>();
	private final ArrayList<JavaField> fieldList = new ArrayList<>();
	final HashMap<String,JavaClass> innerClasses = new HashMap<>();
	final int typeParameters;
	final boolean isEnum;
	final boolean isItem;
	final boolean isBlock;
	final boolean isComposite;
	final String fullyQualifiedSuperclass;
	final WrapperType typeOption;
	private final int classEndPosition;

	/**
	 * @param parent may be null for non-inner classes
	 */
	public JavaClass(
			final JavaFile file, final JavaClass parent,
			final int modifiers, final String simpleName,
			final String docComment,
			final String sourceLocation,
			final boolean isEnum,
			final boolean isItem,
			final boolean isBlock,
			final boolean isComposite,
			final String fullyQualifiedSuperclass,
			final WrapperType typeOption,
			final int classEndPosition)
	{
		super(file, parent, modifiers, null, Generics.strip(simpleName), docComment, sourceLocation);
		this.nameSpace = new NS(file.nameSpace);
		this.typeParameters = Generics.get(simpleName).size();
		this.isEnum = isEnum;
		this.isItem = isItem;
		this.isBlock = isBlock;
		this.isComposite = isComposite;
		this.fullyQualifiedSuperclass = Generics.strip(fullyQualifiedSuperclass);
		this.typeOption=typeOption;
		this.classEndPosition = classEndPosition;
		file.add(this);
		if (parent!=null)
		{
			parent.addInnerClass(this);
		}
	}

	void add(final JavaField javaField)
	{
		assert file.repository.isBuildStage();

		if(fields.putIfAbsent(javaField.name, javaField)!=null)
			throw new RuntimeException(name+'/'+javaField.name);
		fieldList.add(javaField);
	}

	JavaField getField(final String name)
	{
		assert !file.repository.isBuildStage();

		return fields.get(name);
	}

	List<JavaField> getFields()
	{
		assert !file.repository.isBuildStage();

		return Collections.unmodifiableList(fieldList);
	}

	/**
	 * Constructs the fully qualified name of this class,
	 * including package path.
	 */
	String getFullName()
	{
		return getCompleteName('$');
	}

	String getCanonicalName()
	{
		return getCompleteName('.');
	}

	private String getCompleteName(final char innerClassSeparator)
	{
		final StringBuilder buf=new StringBuilder();
		final String packagename = file.getPackageName();
		if(packagename!=null)
		{
			buf.append(packagename);
			buf.append('.');
		}
		final int pos=buf.length();
		for(JavaClass i=this; i!=null; i=i.parent)
		{
			if(i!=this)
				buf.insert(pos, innerClassSeparator);
			buf.insert(pos, i.name);
		}
		return buf.toString();
	}

	public String getCanonicalNameWildcard()
	{
		final StringBuilder buf=new StringBuilder();
		final String packagename = file.getPackageName();
		if(packagename!=null)
		{
			buf.append(packagename);
			buf.append('.');
		}
		final int pos=buf.length();
		for(JavaClass i=this; i!=null; i=i.parent)
		{
			if(i!=this)
				buf.insert(pos, '.');
			buf.insert(pos, i.name);
		}
		if(typeParameters>0)
		{
			buf.append("<?");
			for(int i = 1; i<typeParameters; i++)
				buf.append(",?");
			buf.append('>');
		}
		return buf.toString();
	}

	public final boolean isInterface()
	{
		return Modifier.isInterface(modifier);
	}

	@Override
	public final int getAllowedModifiers()
	{
		return Modifier.INTERFACE | Modifier.classModifiers();
	}

	int getClassEndPositionInSourceWithoutGeneratedFragments()
	{
		return file.translateToPositionInSourceWithoutGeneratedFragments(classEndPosition);
	}

	Object evaluate(final String s)
	{
		assert !file.repository.isBuildStage();

		try
		{
			//System.out.println("--------evaluate("+s+")");
			final Object result = file.repository.interpreter.eval(Generics.remove(s), nameSpace);
			//System.out.println("--------evaluate("+s+") == "+result);
			return result;
		}
		catch(final NoClassDefFoundError e)
		{
			throw new RuntimeException("In class " + getFullName() + " evaluated " + s, e);
		}
		catch(final EvalError e)
		{
			throw new RuntimeException("In class " + getFullName() + " evaluated " + s, e);
		}
	}

	void addInnerClass(final JavaClass c)
	{
		innerClasses.put(c.name, c);
	}

	@SuppressFBWarnings("SE_BAD_FIELD_INNER_CLASS") // Non-serializable class has a serializable inner class
	private final class NS extends CopeNameSpace
	{
		private static final long serialVersionUID = 1l;

		NS(final CopeNameSpace parent)
		{
			super(parent, name);
		}

		@Override
		Class<?> getClassInternal(final String name) throws UtilEvalError
		{
			final String innerClassName;
			// Un-prefixing DUMMY_ITEM_PREFIX is not a clean solution.
			// See SameInnerTypeCollision for an example where the hack does not work.
			if ( name.startsWith(JavaRepository.DUMMY_ITEM_PREFIX) )
			{
				innerClassName=name.substring(JavaRepository.DUMMY_ITEM_PREFIX.length());
			}
			else
			{
				innerClassName=name;
			}
			final JavaClass inner=innerClasses.get(innerClassName);
			if ( inner==null || !inner.isEnum )
			{
				return super.getClassInternal(name);
			}
			else
			{
				return JavaRepository.EnumBeanShellHackClass.class;
			}
		}

		@Override
		Object getVariableInternal(final String name) throws UtilEvalError
		{
			//System.out.println("++++++++++++++++1--------getVariable(\""+name+"\")");
			final Object superResult = super.getVariableInternal(name);
			if(superResult!=Primitive.VOID)
			{
				//System.out.println("#####"+superResult+"--"+superResult.getClass());
				return superResult;
			}

			//System.out.println("++++++++++++++++2--------getVariable(\""+name+"\")");
			for(CopeType<?> ct = LocalCopeType.getCopeType(JavaClass.this); ct!=null; ct = ct.getSuperclass())
			{
				final Evaluatable eval = ct.getField(name);
				if(eval!=null)
					return eval.evaluate();
			}

			return Primitive.VOID;
		}

	}

	final HashMap<Object, JavaField> javaFieldsByInstance = new HashMap<>();

	void registerInstance(final JavaField field, final Object instance)
	{
		javaFieldsByInstance.put(instance, field);
	}

	final JavaField getFieldByInstance(final Object instance)
	{
		final JavaField result = javaFieldsByInstance.get(instance);
		assert result!=null;
		return result;
	}

	@Override
	public String toString()
	{
		return getFullName();
	}
}
