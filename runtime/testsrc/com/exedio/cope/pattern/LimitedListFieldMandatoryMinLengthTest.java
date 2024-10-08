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

import static com.exedio.cope.pattern.LimitedListFieldMandatoryMinLengthTest.AnItem.TYPE;
import static com.exedio.cope.pattern.LimitedListFieldMandatoryMinLengthTest.AnItem.field;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LimitedListFieldMandatoryMinLengthTest extends TestWithEnvironment
{
	@Test void testModel()
	{
		final IntegerField length = field.getLengthIfExists();
		final List<FunctionField<String>> sources = field.getListSources();
		assertEquals(4, sources.size());
		assertEquals(null, length.getDefaultConstant());
		assertEquals(2, length.getMinimum());
		assertEquals(4, length.getMaximum());
		assertEquals(null, sources.get(0).getDefaultConstant());
		assertEquals(null, sources.get(1).getDefaultConstant());
		assertEquals(null, sources.get(2).getDefaultConstant());
		assertEquals(null, sources.get(3).getDefaultConstant());
		assertEquals(false, length.isFinal());
		assertEquals(false, sources.get(0).isFinal());
		assertEquals(false, sources.get(1).isFinal());
		assertEquals(false, sources.get(2).isFinal());
		assertEquals(false, sources.get(3).isFinal());
		assertEquals(true, length.isMandatory());
		assertEquals(true,  sources.get(0).isMandatory());
		assertEquals(true,  sources.get(1).isMandatory());
		assertEquals(false, sources.get(2).isMandatory());
		assertEquals(false, sources.get(3).isMandatory());
		assertEquals(
				"(" +
				"((AnItem.field-Len>'2' and AnItem.field-2 is not null) or (AnItem.field-Len<='2' and AnItem.field-2 is null)) and " +
				"((AnItem.field-Len>'3' and AnItem.field-3 is not null) or (AnItem.field-Len<='3' and AnItem.field-3 is null)))",
				field.getUnison().getCondition().toString());
		assertEquals(2, field.getMinimumSize());
		assertEquals(4, field.getMaximumSize());
		assertEquals(false, field.isFinal());
		assertEquals(true,  field.isMandatory());
		assertEquals(true,  field.isInitial());
	}

	@Test void testEmpty()
	{
		assertFails(
				() -> new AnItem(asList()),
				ListSizeViolationException.class,
				"size violation, value is too short for AnItem.field, " +
				"must be at least 2 elements, but was 0");
	}

	@Test void testTooShort()
	{
		assertFails(
				() -> new AnItem(asList("one")),
				ListSizeViolationException.class,
				"size violation, value is too short for AnItem.field, " +
				"must be at least 2 elements, but was 1");
	}

	@Test void testCreateWithoutMapping()
	{
		//noinspection Convert2MethodRef
		assertFails(
				() -> new AnItem(),
				MandatoryViolationException.class,
				"mandatory violation for AnItem.field-Len");
	}

	@Test void testCreateMin()
	{
		final AnItem item = new AnItem(asList("one", "two"));
		assertEquals(asList("one", "two"), item.getField());
		assertEquals(2,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals(null,  field.getListSources().get(2).get(item));
		assertEquals(null,  field.getListSources().get(3).get(item));
	}

	@Test void testCreateMax()
	{
		final AnItem item = new AnItem(asList("one", "two", "thr", "fou"));
		assertEquals(asList("one", "two", "thr", "fou"), item.getField());
		assertEquals(4,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals("thr", field.getListSources().get(2).get(item));
		assertEquals("fou", field.getListSources().get(3).get(item));

		item.setField(asList("one", "two"));
		assertEquals(asList("one", "two"), item.getField());
		assertEquals(2,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals(null,  field.getListSources().get(2).get(item));
		assertEquals(null,  field.getListSources().get(3).get(item));
	}

	@WrapperType(indent=2, comments=false)
	static final class AnItem extends Item
	{
		static final LimitedListField<String> field =
				LimitedListField.create(new StringField(), 2, 4);

		AnItem()
		{
			this(SetValue.EMPTY_ARRAY);
		}


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem(
					@javax.annotation.Nonnull final java.util.Collection<String> field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.List<String> getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final java.util.Collection<? extends String> field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException,
					com.exedio.cope.pattern.ListSizeViolationException
		{
			AnItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);

	public LimitedListFieldMandatoryMinLengthTest()
	{
		super(MODEL);
	}
}
