/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
	final CopeNameSpace nameSpace;

	private final HashMap<String, JavaField> fields = new HashMap<>();
	private final ArrayList<JavaField> fieldList = new ArrayList<>();
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
		super(file, parent, modifiers, null, stripGenerics(simpleName));
		System.out.println("xxxxxxx " + stripGenerics(simpleName));
		this.nameSpace = new NS(file.nameSpace);
		this.isEnum = isEnum;
		this.classExtends = classExtends;
		file.add(this);
	}

	private static String stripGenerics(final String s)
	{
		if(!s.endsWith(">"))
			return s;

		return s.substring(0, s.indexOf('<'));
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

	public String getCanonicalName()
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
		return buf.toString();
	}

	public final boolean isInterface()
	{
		return Modifier.isInterface(modifier);
	}

	@Override
	public final int getAllowedModifiers()
	{
		return
		Modifier.INTERFACE |
		Modifier.PUBLIC |
		Modifier.PROTECTED |
		Modifier.PRIVATE |
		Modifier.FINAL |
		Modifier.STATIC |
		Modifier.ABSTRACT;
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

	@SuppressFBWarnings("SE_BAD_FIELD_INNER_CLASS") // Non-serializable class has a serializable inner class
	private final class NS extends CopeNameSpace
	{
		private static final long serialVersionUID = 1l;

		NS(final CopeNameSpace parent)
		{
			super(parent);
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
