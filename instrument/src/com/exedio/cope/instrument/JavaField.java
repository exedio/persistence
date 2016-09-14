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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nullable;

/**
 * Represents an attribute of a class.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 *
 * @author Ralf Wiebicke
 */
final class JavaField
	extends JavaFeature
{
	private final String initializer;
	final WrapperInitial wrapperInitial;
	final WrapperIgnore wrapperIgnore;
	final List<Wrapper> wrappers;
	private final Set<Wrapper> copeWrapsThatHaveBeenRead=new HashSet<>();
	private final Set<String> unusedValidWrapKeys=new TreeSet<>();

	private Object rtvalue = null;

	JavaField(
		final JavaClass parent,
		final int modifiers,
		final String type,
		final String name,
		final String docComment,
		final String sourceLocation,
		final String initializer,
		final WrapperInitial wrapperInitial,
		final WrapperIgnore wrapperIgnore,
		final List<Wrapper> wrappers)
	{
		// parent must not be null
		super(parent.file, parent, modifiers, type, name, docComment, sourceLocation);
		if (type == null)
			throw new RuntimeException();
		checkWrapUnique(wrappers);
		this.initializer=initializer;
		this.wrapperInitial=wrapperInitial;
		this.wrapperIgnore=wrapperIgnore;
		this.wrappers=wrappers;

		parent.add(this);
	}

	boolean hasInvalidWrapperUsages()
	{
		final List<Wrapper> unused=new ArrayList<>(wrappers);
		unused.removeAll(copeWrapsThatHaveBeenRead);
		if (unused.isEmpty())
		{
			return false;
		}
		else
		{
			final StringBuilder details=new StringBuilder();
			details.append('\t').append(name).append(" has unused ").append(Wrapper.class.getSimpleName()).append(" annotations:");
			for (final Wrapper copeWrap: unused)
			{
				details.append(" ").append(copeWrap.wrap());
			}
			details.append(System.lineSeparator());
			if (unusedValidWrapKeys.isEmpty())
			{
				details.append("\tThere are no unused valid wrap values.");
				details.append(System.lineSeparator());
			}
			else
			{
				details.append("\tUnused valid wrap values are:");
				for (final String validWrapKey: unusedValidWrapKeys)
				{
					details.append(" ").append(validWrapKey);
				}
				details.append(System.lineSeparator());
			}
			reportSourceProblem(Severity.error, "invalid wrap", details.toString());
			return true;
		}
	}

	private static void checkWrapUnique(final List<Wrapper> wrappers)
	{
		final Set<String> wraps=new HashSet<>();
		for (final Wrapper wrapper: wrappers)
		{
			if ( !wraps.add(wrapper.wrap()) )
			{
				throw new RuntimeException("duplicate @"+Wrapper.class.getSimpleName()+" for "+wrapper.wrap());
			}
		}
	}

	@Nullable
	Wrapper getWrappers(final String modifierTag)
	{
		for (final Wrapper wrapper: wrappers)
		{
			if (modifierTag.equals(wrapper.wrap()))
			{
				copeWrapsThatHaveBeenRead.add(wrapper);
				return wrapper;
			}
		}
		unusedValidWrapKeys.add(modifierTag);
		for (final Wrapper wrapper: wrappers)
		{
			if ("*".equals(wrapper.wrap()))
			{
				copeWrapsThatHaveBeenRead.add(wrapper);
				return wrapper;
			}
		}
		return null;
	}

	@Override
	final int getAllowedModifiers()
	{
		return Modifier.fieldModifiers();
	}

	String getInitializer()
	{
		return initializer;
	}

	Object evaluate()
	{
		assert !file.repository.isBuildStage();

		if(rtvalue==null)
		{
			if ( getInitializer()==null ) throw new RuntimeException("getInitializer() null");
			rtvalue = parent.evaluate(getInitializer());
			assert rtvalue!=null : getInitializer()+'/'+parent+'/'+name;
			parent.registerInstance(this, rtvalue);
		}

		return rtvalue;
	}
}
