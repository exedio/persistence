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
	private final HashMap<String, JavaField> fields = new HashMap<>();
	private final ArrayList<JavaField> fieldList = new ArrayList<>();
	final int typeParameters;
	final Kind kind;
	final String fullyQualifiedSuperclass;
	final WrapperType typeOption;
	private final int classEndPosition;

	/**
	 * @param parent may be null for non-inner classes
	 */
	JavaClass(
			final JavaFile file, final JavaClass parent,
			final int modifiers, final String simpleName,
			final int typeParameters,
			final String sourceLocation,
			final Kind kind,
			final String fullyQualifiedSuperclass,
			final WrapperType typeOption,
			final int classEndPosition)
	{
		super(file, parent, modifiers, simpleName, sourceLocation);
		this.typeParameters = typeParameters;
		this.kind = kind;
		this.fullyQualifiedSuperclass = fullyQualifiedSuperclass;
		this.typeOption=typeOption;
		this.classEndPosition = classEndPosition;
		//noinspection ThisEscapedInObjectConstruction
		file.add(this);
	}

	void add(final JavaField javaField)
	{
		assert file.repository.isBuildStage();

		if(fields.putIfAbsent(javaField.name, javaField)!=null)
			throw new RuntimeException(name+'/'+javaField.name);
		fieldList.add(javaField);
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

	public boolean isInterface()
	{
		return Modifier.isInterface(modifier);
	}

	@Override
	public int getAllowedModifiers()
	{
		return Modifier.INTERFACE | Modifier.classModifiers();
	}

	int getClassEndPositionInSourceWithoutGeneratedFragments()
	{
		return file.translateToPositionInSourceWithoutGeneratedFragments(classEndPosition);
	}

	final HashMap<Object, JavaField> javaFieldsByInstance = new HashMap<>();

	void registerInstance(final JavaField field, final Object instance)
	{
		javaFieldsByInstance.put(instance, field);
	}

	JavaField getFieldByInstance(final Object instance)
	{
		return javaFieldsByInstance.get(instance);
	}

	@Override
	public String toString()
	{
		return getFullName();
	}
}
