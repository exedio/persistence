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

package com.exedio.cope.instrument.parameters;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapInterim;

@SuppressWarnings({"ConstantForZeroLengthArrayAllocation", "FinalMethodInFinalClass", "UnnecessarilyQualifiedStaticUsage"})
public final class UTF8Item extends Item
{
	public static final StringField string = new StringField().defaultTo("Hallöle! Arabic(إ) Cyrillic(Х) Emoji(😱🆒)");

	@SuppressWarnings({"HardcodedLineSeparator", "unused"})
	@WrapInterim(methodBody=false)
	public void doStuff()
	{
		final String content = "Hallo,\n\n" +
									  "eine Bestellung mit Daten wurde aufgegeben.\n\n" +
									  getString() +
									  "\n" +
									  "Daten zur Vervollständigung\n";
		System.out.println(content);
	}

	/**
	 * Creates a new UTF8Item with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"constructor-first","constructor-second"})
	public UTF8Item()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new UTF8Item and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(genericConstructor=...)
	private UTF8Item(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #string}.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings("wrapper-single")
	@javax.annotation.Nonnull
	public final java.lang.String getString()
	{
		return UTF8Item.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings("wrapper-single")
	public final void setString(@javax.annotation.Nonnull final java.lang.String string)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		UTF8Item.string.set(this,string);
	}

	@com.exedio.cope.instrument.GeneratedClass
	private static final long serialVersionUID = 1;

	/**
	 * The persistent type information for uTF8Item.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(type=...)
	@java.lang.SuppressWarnings("type-single")
	public static final com.exedio.cope.Type<UTF8Item> TYPE = com.exedio.cope.TypesBound.newType(UTF8Item.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.GeneratedClass
	private UTF8Item(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
