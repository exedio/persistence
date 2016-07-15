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
 * Represents a class parsed by the java parser.
 * Is an inner class, if parent is not null.
 * @see Parser
 *
 * @author Ralf Wiebicke
 */
final class JavaClass extends JavaFeature
{
	private static final String PREFIX_OF_INNER_CLASS_IN_DUMMY=JavaRepository.DummyItem.class.getName()+"$";

	final CopeNameSpace nameSpace;

	private final HashMap<String, JavaField> fields = new HashMap<>();
	private final ArrayList<JavaField> fieldList = new ArrayList<>();
	private final HashMap<String,JavaClass> innerClasses = new HashMap<>();
	final int typeParameters;
	final boolean isEnum;
	final String classExtends;
	private String docComment;
	private int classEndPosition = -1;


	/**
	 * @param parent may be null for non-inner classes
	 */
	public JavaClass(
			final JavaFile file, final JavaClass parent,
			final int modifiers, final boolean isEnum, final String simpleName,
			final String classExtends)
	throws ParserException
	{
		super(file, parent, modifiers, null, Generics.strip(simpleName));
		this.nameSpace = new NS(file.nameSpace);
		this.typeParameters = Generics.get(simpleName).size();
		this.isEnum = isEnum;
		this.classExtends = Generics.strip(classExtends);
		file.add(this);
		if (parent!=null)
		{
			parent.addInnerClass(this);
		}
	}

	void add(final JavaField javaField)
	{
		assert file.repository.isBuildStage();

		if(fields.put(javaField.name, javaField)!=null)
			throw new RuntimeException(name+'/'+javaField.name);
		fieldList.add(javaField);
	}

	JavaField getFields(final String name)
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
	public String getFullName()
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
				buf.insert(pos, '$');
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

	void setDocComment(final String docComment)
	{
		assert this.docComment==null;
		this.docComment = docComment;
	}

	String getDocComment()
	{
		return docComment;
	}

	void setClassEndPosition(final int classEndPosition)
	{
		assert file.repository.isBuildStage();
		assert this.classEndPosition==-1;
		assert classEndPosition>=0;

		this.classEndPosition = classEndPosition;
	}

	int getClassEndPosition()
	{
		assert classEndPosition>=0;

		return classEndPosition;
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

	void addInnerClass(JavaClass c)
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
		public Class<?> getClass(final String name) throws UtilEvalError
		{
			final String innerClassName;
			// Un-prefixing PREFIX_OF_INNER_CLASS_IN_DUMMY is not a clean solution.
			// See SameInnerTypeCollision for an example where the hack does not work.
			if ( name.startsWith(PREFIX_OF_INNER_CLASS_IN_DUMMY) )
			{
				innerClassName=name.substring(PREFIX_OF_INNER_CLASS_IN_DUMMY.length());
			}
			else
			{
				innerClassName=name;
			}
			final JavaClass inner=innerClasses.get(innerClassName);
			if ( inner==null || !inner.isEnum )
			{
				return super.getClass(name);
			}
			else
			{
				return JavaRepository.EnumBeanShellHackClass.class;
			}
		}

		@Override
	   public Object getVariable(final String name) throws UtilEvalError
	   {
			//System.out.println("++++++++++++++++1--------getVariable(\""+name+"\")");
			final Object superResult = super.getVariable(name);
			if(superResult!=Primitive.VOID)
			{
				//System.out.println("#####"+superResult+"--"+superResult.getClass());
				return superResult;
			}

			//System.out.println("++++++++++++++++2--------getVariable(\""+name+"\")");
			for(CopeType ct = CopeType.getCopeType(JavaClass.this); ct!=null; ct = ct.getSuperclass())
			{
				final JavaField ja = ct.javaClass.getFields(name);
				if(ja!=null)
					return ja.evaluate();
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
