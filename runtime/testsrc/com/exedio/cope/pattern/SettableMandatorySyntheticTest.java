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

package com.exedio.cope.pattern;

import static com.exedio.cope.instrument.Visibility.PUBLIC;
import static com.exedio.cope.pattern.SettableMandatorySyntheticTest.AnItem.TYPE;
import static com.exedio.cope.pattern.SettableMandatorySyntheticTest.AnItem.mandatory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import org.junit.Test;

public class SettableMandatorySyntheticTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public SettableMandatorySyntheticTest()
	{
		super(MODEL);
	}

	@Test public void test()
	{
		final SetValue<?> setValue = mandatory.map(null);

		try
		{
			new AnItem(setValue);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory.source, e.getFeature()); // TODO should be field itself
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for AnItem.mandatory-source", e.getMessage());
		}

		final AnItem item = new AnItem(3);
		try
		{
			item.set(setValue);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory.source, e.getFeature()); // TODO should be field itself
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for AnItem.mandatory-source", e.getMessage());
		}
		assertEquals(3, mandatory.source.getMandatory(item));
	}


	@com.exedio.cope.instrument.WrapperType(genericConstructor=PUBLIC) // TODO use import, but this is not accepted by javac
	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final SettableSyntheticField mandatory = new SettableSyntheticField();

	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				final int mandatory)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.mandatory.map(mandatory),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

}