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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.tools.Diagnostic;

/**
 * Represents an attribute of a class.
 *
 * @author Ralf Wiebicke
 */
final class JavaField
	extends JavaFeature
{
	private static final Class<?>[] PARAMETERS_DEFAULT=new Class<?>[]{WrapperParametersDefault.class};

	final boolean deprecated;
	private final TypeMirror typeMirror;

	final String typeFullyQualified;

	private final String initializer;
	final WrapperInitial wrapperInitial;
	final WrapperIgnore wrapperIgnore;
	final List<WrapperWrap> wrappers;
	private final Set<WrapperWrap> copeWrapsThatHaveBeenRead=new HashSet<>();
	private final Set<String> unusedValidWrapKeys=new TreeSet<>();

	private Object rtvalue = null;

	private final Map<String,String> typeShortcuts=new HashMap<>();

	JavaField(
		final JavaClass parent,
		final int modifiers,
		final boolean deprecated,
		final TypeMirror typeMirror,
		final String typeFullyQualified,
		final String name,
		final Element sourceLocation,
		final String initializer,
		final WrapperInitial wrapperInitial,
		final WrapperIgnore wrapperIgnore,
		final List<Wrapper> wrappers)
	{
		// parent must not be null
		super(parent.file, parent, modifiers, name, sourceLocation);
		this.deprecated = deprecated;
		this.typeMirror=typeMirror;
		this.typeFullyQualified=typeFullyQualified;
		this.initializer=initializer;
		this.wrapperInitial=wrapperInitial;
		this.wrapperIgnore=wrapperIgnore;
		this.wrappers = new ArrayList<>();
		for(final Wrapper wrapper : wrappers)
		{
			for (final String wrapKey: wrapper.wrap())
			{
				this.wrappers.add(new WrapperWrap(wrapKey, toWrapperConfiguration(wrapper)));
			}
		}
		checkWrapUnique(this.wrappers);

		//noinspection ThisEscapedInObjectConstruction
		parent.add(this);
	}

	String getTypeParameter(final int number)
	{
		if (!(typeMirror instanceof DeclaredType) || ((DeclaredType)typeMirror).getTypeArguments().isEmpty())
			throw new RuntimeException("type "+typeMirror+" is not parameterized");
		return applyTypeShortcuts(((DeclaredType)typeMirror).getTypeArguments().get(number));
	}

	void addTypeShortcut(final String fullType, final String shortType)
	{
		final String collision = typeShortcuts.put(fullType, shortType);
		if (collision!=null && !collision.equals(shortType))
			throw new RuntimeException("shortcut collision: "+fullType+" -> "+collision+"/"+shortType);
		final String collisionArray = typeShortcuts.put(fullType+"[]", shortType+"[]");
		if (collisionArray!=null && !collisionArray.equals(shortType+"[]"))
			throw new RuntimeException("shortcut collision: "+fullType+" -> "+collisionArray+"/"+shortType+"[]");
	}

	String applyTypeShortcuts(final TypeMirror typeMirror)
	{
		//noinspection EnumSwitchStatementWhichMissesCases
		switch (typeMirror.getKind())
		{
			case DECLARED -> {
				final StringBuilder sb = new StringBuilder();
				sb.append( applyTypeShortcuts(((DeclaredType)typeMirror).asElement().toString()) );
				final List<? extends TypeMirror> args = ((DeclaredType)typeMirror).getTypeArguments();
				if (!args.isEmpty())
				{
					sb.append( "<" );
					final StringSeparator separator = new StringSeparator(",");
					for (final TypeMirror arg : args)
					{
						separator.appendTo(sb);
						sb.append( applyTypeShortcuts(arg) );
					}
					sb.append( ">" );
				}
				return sb.toString();
			} case WILDCARD -> {
				final StringBuilder wsb = new StringBuilder();
				wsb.append("?");
				final TypeMirror extendsBound = ((WildcardType)typeMirror).getExtendsBound();
				if (extendsBound!=null)
				{
					wsb.append(" extends ");
					wsb.append(applyTypeShortcuts(extendsBound));
				}
				final TypeMirror superBound = ((WildcardType)typeMirror).getSuperBound();
				if (superBound!=null)
				{
					wsb.append(" super ");
					wsb.append(applyTypeShortcuts(superBound));
				}
				return wsb.toString();
			} default -> {
				return applyTypeShortcuts(typeMirror.toString());
			}
		}
	}

	String applyTypeShortcuts(final String typeName)
	{
		final String shortType = typeShortcuts.get(typeName);
		return shortType==null ? typeName : shortType;
	}

	void reportInvalidWrapperUsages(final Messager messager)
	{
		final List<WrapperWrap> unused=new ArrayList<>(wrappers);
		unused.removeAll(copeWrapsThatHaveBeenRead);
		if (!unused.isEmpty())
		{
			final StringBuilder details=new StringBuilder();
			details.append("unused ").append(Wrapper.class.getSimpleName()).append(" annotation").append(unused.size()>1?"s":"");
			for (final WrapperWrap copeWrap: unused)
			{
				details.append(" ").append(copeWrap.wrapKey());
				if (!copeWrap.configuration().isParametersDefault())
				{
					details.append(" (");
					final List<Class<?>> params=copeWrap.configuration().parameters();
					details.append(params.stream().map(Class::getName).collect(Collectors.joining(", ")));
					details.append(")");
				}
				if (copeWrap.wrapKey().contains(Wrapper.ALL_WRAPS) && !Wrapper.ALL_WRAPS.equals(copeWrap.wrapKey()))
				{
					details.append(" (\"").append(Wrapper.ALL_WRAPS).append("\" is only supported as full value)");
				}
			}
			if (unusedValidWrapKeys.isEmpty())
			{
				details.append(" - there are no unused valid wrap values.");
			}
			else
			{
				details.append(" - unused valid wrap values are:");
				for (final String validWrapKey: unusedValidWrapKeys)
				{
					details.append(" ").append(validWrapKey);
				}
			}
			messager.printMessage(Diagnostic.Kind.ERROR, details.toString(), sourceLocation);
		}
	}

	private static void checkWrapUnique(final List<WrapperWrap> wrappers)
	{
		final Set<WrapperTarget> wraps=new HashSet<>();
		for (final WrapperWrap wrapper: wrappers)
		{
			final WrapperTarget wrapperTarget = new WrapperTarget(wrapper);
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
		private final List<Class<?>> parameters;

		private WrapperTarget(final WrapperWrap wrapper)
		{
			this.wrap = requireNonNull(wrapper.wrapKey());
			this.parameters = wrapper.configuration().parameters();
		}

		@Override
		public String toString()
		{
			return wrap+(parameters==null?"":(" "+parameters));
		}

		@Override
		public boolean equals(final Object obj)
		{
			return obj instanceof WrapperTarget
				&& ((WrapperTarget)obj).wrap.equals(wrap)
				&& Objects.equals(((WrapperTarget)obj).parameters, parameters);
		}

		@Override
		public int hashCode()
		{
			return wrap.hashCode() ^ Objects.hashCode(parameters);
		}
	}

	@Nullable
	WrapperConfiguration getWrappers(final String modifierTag, final Type[] parameterTypes)
	{
		final WrapperWrap byModifierTagAndParameters=readWrapper(modifierTag, parameterTypes);
		if (byModifierTagAndParameters!=null)
			return byModifierTagAndParameters.configuration();
		final WrapperWrap byModifierTag=readWrapper(modifierTag, null);
		if (byModifierTag!=null)
			return byModifierTag.configuration();
		unusedValidWrapKeys.add(modifierTag);
		final WrapperWrap byAll = readWrapper(Wrapper.ALL_WRAPS, null);
		return byAll==null ? null : byAll.configuration();
	}

	private WrapperWrap readWrapper(final String wrap, final Type[] parameterTypes)
	{
		for (final WrapperWrap wrapper: wrappers)
		{
			if (wrap.equals(wrapper.wrapKey()) && Objects.equals(parameterTypes==null ? null : Arrays.asList(parameterTypes), wrapper.configuration().parameters()))
			{
				copeWrapsThatHaveBeenRead.add(wrapper);
				return wrapper;
			}
		}
		return null;
	}

	private Class<?>[] parameters(final Wrapper wrapper)
	{
		try
		{
			//noinspection ResultOfMethodCallIgnored TODO include result into RuntimeException thrown below
			wrapper.parameters();
			throw new RuntimeException("expected MirroredTypesException");
		}
		catch (final MirroredTypesException e)
		{
			final Class<?>[] result = new Class<?>[e.getTypeMirrors().size()];
			for (int i = 0; i < result.length; i++)
			{
				try
				{
					result[i] = TypeMirrorHelper.getClass(e.getTypeMirrors().get(i), parent.file.interimClassLoader);
				}
				catch (final ClassNotFoundException cnf)
				{
					throw new RuntimeException(cnf);
				}
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

	Object evaluate()
	{
		file.repository.assertNotBuildStage();

		if (wrapperIgnore!=null)
			throw new RuntimeException("evaluate ignored");

		if(rtvalue==null)
		{
			if ( getInitializer()==null ) throw new RuntimeException("getInitializer() null");
			rtvalue = getValueFromInterimClassLoader(); // parent.evaluate(getInitializer());
			requireNonNull(rtvalue, ()->getInitializer()+'/'+parent+'/'+name);
			parent.registerInstance(this, rtvalue);
		}

		return rtvalue;
	}

	private Object getValueFromInterimClassLoader()
	{
		try
		{
			final Class<?> interimClass = file.interimClassLoader.loadClass(parent.getFullName());
			final Field field = interimClass.getDeclaredField(name);
			field.setAccessible(true);
			return field.get(null);
		}
		catch (final ClassNotFoundException|NoSuchFieldException|IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	private WrapperConfiguration toWrapperConfiguration(final Wrapper wrapper)
	{
		final Class<?>[] parameters = parameters(wrapper);
		final boolean parametersIsDefault = Arrays.equals(parameters, PARAMETERS_DEFAULT);
		return new WrapperConfiguration(
				parametersIsDefault ? null : Arrays.asList(parameters),
				wrapper.visibility(),
				wrapper.internal(),
				wrapper.booleanAsIs(),
				wrapper.asFinal(),
				wrapper.override(),
				Arrays.asList(wrapper.suppressWarnings()),
				Arrays.asList(wrapper.annotate()),
				wrapper.nullableAsOptional()
		);
	}

	/**
	 * one 'wrap' key in a {@link Wrapper} annotation
	 */
	private record WrapperWrap(String wrapKey, WrapperConfiguration configuration)
	{
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	record WrapperConfiguration(List<Class<?>> parameters, Visibility visibility, boolean internal,
										 boolean booleanAsIs, boolean asFinal, boolean override,
										 List<String> suppressWarnings, List<String> annotate, NullableAsOptional nullableAsOptional)
	{
		private boolean isParametersDefault()
		{
			return null==parameters;
		}
	}
}
