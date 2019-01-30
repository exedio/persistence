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

import static com.exedio.cope.pattern.LimitedListFieldFinalTest.AnItem.TYPE;
import static com.exedio.cope.pattern.LimitedListFieldFinalTest.AnItem.text;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LimitedListFieldFinalTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public LimitedListFieldFinalTest()
	{
		super(MODEL);
	}


	@Test void testModel()
	{
		final List<FunctionField<String>> textSources = text.getListSources();
		assertEquals(3, textSources.size());
		assertEquals(Integer.valueOf(0), text.getLength().getDefaultConstant());
		assertEquals(null, textSources.get(0).getDefaultConstant());
		assertEquals(null, textSources.get(1).getDefaultConstant());
		assertEquals(null, textSources.get(2).getDefaultConstant());
		assertEquals(true, text.getLength().isFinal());
		assertEquals(true, textSources.get(0).isFinal());
		assertEquals(true, textSources.get(1).isFinal());
		assertEquals(true, textSources.get(2).isFinal());
		assertEquals(true,  text.isFinal());
		assertEquals(true,  text.isMandatory());
		assertEquals(true,  text.isInitial());
	}

	@Test void testEmpty()
	{
		final AnItem item = new AnItem(asList());
		assertEquals(asList(), item.getText());

		try
		{
			item.setText(asList("zack"));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(text, e.getFeature());
		}
		assertEquals(asList(), item.getText());

		try
		{
			item.set(text.map(asList("zack")));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(text, e.getFeature());
		}
		assertEquals(asList(), item.getText());
	}

	@Test void testCreateWithoutMapping()
	{
		final AnItem item = new AnItem();
		assertEquals(asList(), item.getText());
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test void testCreateNull()
	{
		try
		{
			new AnItem((Collection<String>)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(text, e.getFeature());
		}
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final LimitedListField<String> text =
				LimitedListField.create(new StringField().toFinal().optional(), 3);

		AnItem()
		{
			this(new SetValue<?>[]{
			});
		}


		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnItem(
					@javax.annotation.Nonnull final java.util.Collection<String> text)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.text.map(text),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.util.List<String> getText()
		{
			return AnItem.text.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setText(@javax.annotation.Nonnull final java.util.Collection<? extends String> text)
				throws
					com.exedio.cope.FinalViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException,
					com.exedio.cope.pattern.ListSizeViolationException
		{
			AnItem.text.set(this,text);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
