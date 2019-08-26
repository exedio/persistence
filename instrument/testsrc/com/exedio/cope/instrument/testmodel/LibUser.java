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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.instrument.testfeature.FilterFeature;
import com.exedio.cope.instrument.testfeature.GenericFeatureReference;
import com.exedio.cope.instrument.testfeature.SimpleSettable;
import com.exedio.cope.instrument.testfeature.WrapVarargs;
import com.exedio.cope.instrument.testlib.LibItem;
import com.exedio.cope.misc.ReflectionTypes;

/** test extening an item that is imported from a library */
class LibUser extends LibItem<String>
{
	/** check that we can access a field in the initialization of feature {@link #simple}: */
	private static final boolean CONSTANT_FOR_FALSE = false;

	static final FilterFeature filter=new FilterFeature(option);

	static final GenericFeatureReference<LibItem<?>> ref=GenericFeatureReference.create(LibItem.classWildcard.value, ReflectionTypes.parameterized(LibItem.class, ReflectionTypes.sub(Object.class)));

	static final SimpleSettable simple=new SimpleSettable(CONSTANT_FOR_FALSE);
	static final SimpleSettable simple2=new SimpleSettable(CONSTANT_FOR_FALSE_IN_LIBITEM);

	static final WrapVarargs wrapLibSuper=new WrapVarargs(inSuper);

	@Override
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	public String makeTee()
	{
		return "tee";
	}


	/**
	 * Creates a new LibUser with all the fields initially needed.
	 * @param inSuper the initial value for field {@link #inSuper}.
	 * @param a the initial value for field {@link #a}.
	 * @param inner the initial value for field {@link #inner}.
	 * @param strings the initial value for field {@link #strings}.
	 * @param nestedGenerics the initial value for field {@link #nestedGenerics}.
	 * @param ref the initial value for field {@link #ref}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	LibUser(
				@javax.annotation.Nullable final java.lang.String inSuper,
				@javax.annotation.Nullable final java.lang.String a,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testlib.LibItem.Inner inner,
				@javax.annotation.Nonnull final java.lang.String[] strings,
				@javax.annotation.Nonnull final java.util.Set<java.util.List<java.lang.Object>> nestedGenerics,
				@javax.annotation.Nonnull final LibItem<?> ref)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.instrument.testlib.LibSuperItem.inSuper.map(inSuper),
			com.exedio.cope.instrument.testlib.LibItem.a.map(a),
			com.exedio.cope.instrument.testlib.LibItem.inner.map(inner),
			com.exedio.cope.instrument.testlib.LibItem.strings.map(strings),
			com.exedio.cope.instrument.testlib.LibItem.nestedGenerics.map(nestedGenerics),
			LibUser.ref.map(ref),
		});
	}

	/**
	 * Creates a new LibUser and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected LibUser(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	final void simpleFilter()
	{
		LibUser.filter.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	final LibItem<?> methodRef(final LibItem<?> ref)
	{
		return LibUser.ref.method(this,ref);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
	final java.lang.String oneSimple()
	{
		return LibUser.simple.one(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
	final java.lang.String oneSimple2()
	{
		return LibUser.simple2.one(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	static final void simpleWrapLibSuper(final java.lang.String inSuper)
	{
		LibUser.wrapLibSuper.simple(inSuper);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="moreParameters")
	static final void moreWrapLibSuperParameters(final int wrapLibSuper,final java.lang.String inSuper)
	{
		LibUser.wrapLibSuper.moreParameters(wrapLibSuper,inSuper);
	}

	/**
	 * @param inSuper myDoc/{@link #inSuper}/inSuper/libSuperItem/
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="staticToken")
	static final LibUser staticWrapLibSuperToken(final java.lang.String inSuper)
	{
		return LibUser.wrapLibSuper.staticToken(LibUser.class,inSuper);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for libUser.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<LibUser> TYPE = com.exedio.cope.TypesBound.newType(LibUser.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected LibUser(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
