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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.testfeature.OptionFeature;
import com.exedio.cope.instrument.testfeature.SettableOpen;
import com.exedio.cope.instrument.testfeature.SimpleSettable;
import java.util.List;
import java.util.Set;

public abstract class LibItem<T> extends Item
{
	/** check that we can access a field in the initialization of feature LibUser#simple2: */
	public final static boolean CONSTANT_FOR_FALSE_IN_LIBITEM = false;

	public enum	Inner { a, b, c }

	@WrapperInitial
	public static final SimpleSettable a=new SimpleSettable();

	public static final OptionFeature option=new OptionFeature();

	public static final SettableOpen<LibItem.Inner> inner=new SettableOpen<>();

	public static final SettableOpen<String[]> strings=new SettableOpen<>();

	public static final SettableOpen<Set<List<Object>>> nestedGenerics=new SettableOpen<>();

	@WrapperIgnore
	public static final SimpleSettable ignored=new SimpleSettable(true);

	public abstract T makeTee();

	public static final class classWildcard
	{
		@SuppressWarnings("unchecked")
		public static final Class<LibItem<?>> value = (Class<LibItem<?>>)(Class<?>)LibItem.class;
	}

	/**

	 **
	 * Creates a new LibItem with all the fields initially needed.
	 * @param a the initial value for field {@link #a}.
	 * @param inner the initial value for field {@link #inner}.
	 * @param strings the initial value for field {@link #strings}.
	 * @param nestedGenerics the initial value for field {@link #nestedGenerics}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public LibItem(
				@javax.annotation.Nullable final java.lang.String a,
				@javax.annotation.Nonnull final LibItem.Inner inner,
				@javax.annotation.Nonnull final String[] strings,
				@javax.annotation.Nonnull final Set<List<Object>> nestedGenerics)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			LibItem.a.map(a),
			LibItem.inner.map(inner),
			LibItem.strings.map(strings),
			LibItem.nestedGenerics.map(nestedGenerics),
		});
	}/**

	 **
	 * Creates a new LibItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected LibItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.one public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final java.lang.String oneA()
	{
		return LibItem.a.one(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.simple public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void simpleOption()
	{
		LibItem.option.simple(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.method public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final LibItem.Inner methodInner(final LibItem.Inner inner)
	{
		return LibItem.inner.method(inner);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.method public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final String[] methodStrings(final String[] strings)
	{
		return LibItem.strings.method(strings);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.method public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final Set<List<Object>> methodNestedGenerics(final Set<List<Object>> nestedGenerics)
	{
		return LibItem.nestedGenerics.method(nestedGenerics);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for libItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final com.exedio.cope.Type<LibItem<?>> TYPE = com.exedio.cope.TypesBound.newType(LibItem.classWildcard.value);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected LibItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}