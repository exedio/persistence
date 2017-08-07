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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nullable;
import javax.lang.model.type.MirroredTypesException;

/**
 * Represents an attribute of a class.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 *
 * @author Ralf Wiebicke
 */
final class JavaField
	extends JavaFeature
	implements Evaluatable
{
	private static final Class<?>[] PARAMETERS_DEFAULT=new Class<?>[]{WrapperParametersDefault.class};

	private final String initializer;
	final WrapperInitial wrapperInitial;
	final WrapperIgnore wrapperIgnore;
	final List<Wrapper> wrappers;
	private final Set<Wrapper> copeWrapsThatHaveBeenRead=new HashSet<>();
	private final Set<String> unusedValidWrapKeys=new TreeSet<>();

	private Object rtvalue = null;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
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

		//noinspection ThisEscapedInObjectConstruction
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
				if (!isParametersDefault(copeWrap))
				{
					details.append(" (");
					final Class<?>[] params=parameters(copeWrap);
					for (int i=0; i<params.length; i++)
					{
						if (i!=0) details.append(", ");
						details.append(params[i].getName());
					}
					details.append(")");
				}
				if (copeWrap.wrap().contains(Wrapper.ALL_WRAPS) && !Wrapper.ALL_WRAPS.equals(copeWrap.wrap()))
				{
					details.append(" (\"").append(Wrapper.ALL_WRAPS).append("\" is only supported as full value)");
				}
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

	private void checkWrapUnique(final List<Wrapper> wrappers)
	{
		final Set<WrapperTarget> wraps=new HashSet<>();
		for (final Wrapper wrapper: wrappers)
		{
			final WrapperTarget wrapperTarget = new WrapperTarget(this, wrapper);
			if (wrapperTarget.wrap.equals(Wrapper.ALL_WRAPS) && wrapperTarget.parameters!=null)
			{
				throw new RuntimeException("invalid @"+Wrapper.class.getSimpleName()+": parameters not supported for wrap=\""+Wrapper.ALL_WRAPS+"\"");
			}
			if (!wraps.add(wrapperTarget))
			{
				throw new RuntimeException("duplicate @"+Wrapper.class.getSimpleName()+" for "+wrapperTarget);
			}
		}
	}

	private static final class WrapperTarget
	{
		private final String wrap;
		private final Class<?>[] parameters;

		private WrapperTarget(final JavaField outer, final Wrapper wrapper)
		{
			this.wrap = Objects.requireNonNull(wrapper.wrap());
			this.parameters = outer.isParametersDefault(wrapper) ? null : outer.parameters(wrapper);
		}

		@Override
		public String toString()
		{
			return wrap+(parameters==null?"":(" "+Arrays.toString(parameters)));
		}

		@Override
		public boolean equals(final Object obj)
		{
			return obj instanceof WrapperTarget
				&& ((WrapperTarget)obj).wrap.equals(wrap)
				&& Arrays.equals(((WrapperTarget)obj).parameters, parameters);
		}

		@Override
		public int hashCode()
		{
			return wrap.hashCode() ^ Arrays.hashCode(parameters);
		}
	}

	@Nullable
	Wrapper getWrappers(final String modifierTag, final Type[] parameterTypes)
	{
		final Wrapper byModifierTagAndParameters=readWrapper(modifierTag, parameterTypes);
		if (byModifierTagAndParameters!=null)
			return byModifierTagAndParameters;
		final Wrapper byModifierTag=readWrapper(modifierTag, PARAMETERS_DEFAULT);
		if (byModifierTag!=null)
			return byModifierTag;
		unusedValidWrapKeys.add(modifierTag);
		return readWrapper(Wrapper.ALL_WRAPS, PARAMETERS_DEFAULT);
	}

	private Wrapper readWrapper(final String wrap, final Type[] parameterTypes)
	{
		for (final Wrapper wrapper: wrappers)
		{
			if (wrap.equals(wrapper.wrap()) && Arrays.equals(parameterTypes, parameters(wrapper)))
			{
				copeWrapsThatHaveBeenRead.add(wrapper);
				return wrapper;
			}
		}
		return null;
	}

	private boolean isParametersDefault(final Wrapper wrapper)
	{
		final Class<?>[] parameters = parameters(wrapper);
		return Arrays.equals(parameters, PARAMETERS_DEFAULT);
	}

	private Class<?>[] parameters(final Wrapper wrapper)
	{
		try
		{
			wrapper.parameters();
			throw new RuntimeException("expected MirroredTypesException");
		}
		catch (final MirroredTypesException e)
		{
			final Class<?>[] result = new Class<?>[e.getTypeMirrors().size()];
			for (int i = 0; i < result.length; i++)
			{
				result[i] = TypeMirrorHelper.getClass(e.getTypeMirrors().get(i), parent.nameSpace);
				if (result[i]==null) throw new NullPointerException(e.getTypeMirrors()+"  "+i);
			}
			return result;
		}
	}

	@Override
	int getAllowedModifiers()
	{
		return Modifier.fieldModifiers();
	}

	String getInitializer()
	{
		return initializer;
	}

	@Override
	public Object evaluate()
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
