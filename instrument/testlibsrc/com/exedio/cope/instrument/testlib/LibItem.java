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

package com.exedio.cope.instrument.testlib;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.testfeature.OptionFeature;
import com.exedio.cope.instrument.testfeature.SettableOpen;
import com.exedio.cope.instrument.testfeature.SimpleSettable;
import com.exedio.cope.misc.ReflectionTypes;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"UnnecessarilyQualifiedInnerClassAccess", "AbstractClassExtendsConcreteClass"})
public abstract class LibItem<T> extends LibSuperItem
{
	/** check that we can access a field in the initialization of feature LibUser#simple2: */
	public static final boolean CONSTANT_FOR_FALSE_IN_LIBITEM = false;

	@SuppressWarnings({"EmptyClass", "RedundantSuppression"}) // OK: just for testing instrumentor
	public enum Inner { }

	@WrapperInitial
	public static final SimpleSettable a=new SimpleSettable();

	public static final OptionFeature option=new OptionFeature();

	public static final SettableOpen<LibItem.Inner> inner=new SettableOpen<>(LibItem.Inner.class);

	public static final SettableOpen<String[]> strings=new SettableOpen<>(String[].class);

	public static final SettableOpen<Set<List<Object>>> nestedGenerics=new SettableOpen<>(
		ReflectionTypes.parameterized(Set.class, ReflectionTypes.parameterized(List.class, Object.class))
	);

	@WrapperIgnore
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	public static final SimpleSettable ignored=new SimpleSettable(true);

	@SuppressWarnings("unused") // OK: just for testing instrumentor
	public abstract T makeTee();

	/**
	 * Creates a new LibItem with all the fields initially needed.
	 * @param inSuper the initial value for field {@link #inSuper}.
	 * @param a the initial value for field {@link #a}.
	 * @param inner the initial value for field {@link #inner}.
	 * @param strings the initial value for field {@link #strings}.
	 * @param nestedGenerics the initial value for field {@link #nestedGenerics}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	protected LibItem(
				@javax.annotation.Nullable final java.lang.String inSuper,
				@javax.annotation.Nullable final java.lang.String a,
				@javax.annotation.Nonnull final LibItem.Inner inner,
				@javax.annotation.Nonnull final String[] strings,
				@javax.annotation.Nonnull final Set<List<Object>> nestedGenerics)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.instrument.testlib.LibSuperItem.inSuper.map(inSuper),
			LibItem.a.map(a),
			LibItem.inner.map(inner),
			LibItem.strings.map(strings),
			LibItem.nestedGenerics.map(nestedGenerics),
		});
	}

	/**
	 * Creates a new LibItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected LibItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final java.lang.String oneA()
	{
		return LibItem.a.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void simpleOption()
	{
		LibItem.option.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public static final LibItem.Inner methodInner(final LibItem.Inner inner)
	{
		return LibItem.inner.method(inner);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public static final String[] methodStrings(final String[] strings)
	{
		return LibItem.strings.method(strings);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public static final Set<List<Object>> methodNestedGenerics(final Set<List<Object>> nestedGenerics)
	{
		return LibItem.nestedGenerics.method(nestedGenerics);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2L;

	/**
	 * Use LibItem.classWildcard.value instead of LibItem.class to avoid rawtypes warnings.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
	public static final class classWildcard { public static final java.lang.Class<LibItem<?>> value = com.exedio.cope.ItemWildcardCast.cast(LibItem.class); private classWildcard(){} }

	/**
	 * The persistent type information for libItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<LibItem<?>> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(classWildcard.value);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected LibItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
