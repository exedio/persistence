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

import static com.exedio.cope.pattern.LimitedListFieldMandatoryTest.AnItem.TYPE;
import static com.exedio.cope.pattern.LimitedListFieldMandatoryTest.AnItem.field;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

public class LimitedListFieldMandatoryTest extends TestWithEnvironment
{
	@Test void testModel()
	{
		final IntegerField length = field.getLengthIfExists();
		final List<FunctionField<String>> sources = field.getListSources();
		assertEquals(3, sources.size());
		assertEquals(Integer.valueOf(0), length.getDefaultConstant());
		assertEquals(0, length.getMinimum());
		assertEquals(3, length.getMaximum());
		assertEquals(null, sources.get(0).getDefaultConstant());
		assertEquals(null, sources.get(1).getDefaultConstant());
		assertEquals(null, sources.get(2).getDefaultConstant());
		assertEquals(false, length.isFinal());
		assertEquals(false, sources.get(0).isFinal());
		assertEquals(false, sources.get(1).isFinal());
		assertEquals(false, sources.get(2).isFinal());
		assertEquals(true, length.isMandatory());
		assertEquals(false, sources.get(0).isMandatory());
		assertEquals(false, sources.get(1).isMandatory());
		assertEquals(false, sources.get(2).isMandatory());
		assertEquals(
				"(" +
				"((AnItem.field-Len>'0' AND AnItem.field-0 is not null) OR (AnItem.field-Len<='0' AND AnItem.field-0 is null)) AND " +
				"((AnItem.field-Len>'1' AND AnItem.field-1 is not null) OR (AnItem.field-Len<='1' AND AnItem.field-1 is null)) AND " +
				"((AnItem.field-Len>'2' AND AnItem.field-2 is not null) OR (AnItem.field-Len<='2' AND AnItem.field-2 is null)))",
				field.getUnison().getCondition().toString());
		assertEquals(0, field.getMinimumSize());
		assertEquals(3, field.getMaximumSize());
		assertEquals(false, field.isFinal());
		assertEquals(true,  field.isMandatory());
		assertEquals(false, field.isInitial());
	}

	@Test void testEmpty()
	{
		final AnItem item = new AnItem(asList());
		assertEquals(asList(), item.getField());
		assertEquals(0,    field.getLengthIfExists().getMandatory(item));
		assertEquals(null, field.getListSources().get(0).get(item));
		assertEquals(null, field.getListSources().get(1).get(item));
		assertEquals(null, field.getListSources().get(2).get(item));

		item.setField(asList("one", "two"));
		assertEquals(asList("one", "two"), item.getField());
		assertEquals(2,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals(null,  field.getListSources().get(2).get(item));

		item.set(field.map(asList("one", "two", "thr")));
		assertEquals(asList("one", "two", "thr"), item.getField());
		assertEquals(3,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals("thr", field.getListSources().get(2).get(item));
	}

	@Test void testCreateWithoutMapping()
	{
		final AnItem item = new AnItem();
		assertEquals(asList(), item.getField());
		assertEquals(0,    field.getLengthIfExists().getMandatory(item));
		assertEquals(null, field.getListSources().get(0).get(item));
		assertEquals(null, field.getListSources().get(1).get(item));
		assertEquals(null, field.getListSources().get(2).get(item));
	}

	@Test void testCreateNormal()
	{
		final AnItem item = new AnItem(asList("one", "two"));
		assertEquals(asList("one", "two"), item.getField());
		assertEquals(2,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals(null,  field.getListSources().get(2).get(item));
	}

	@Test void testCreateMax()
	{
		final AnItem item = new AnItem(asList("one", "two", "thr"));
		assertEquals(asList("one", "two", "thr"), item.getField());
		assertEquals(3,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals("thr", field.getListSources().get(2).get(item));

		item.setField(asList("one", "two"));
		assertEquals(asList("one", "two"), item.getField());
		assertEquals(2,     field.getLengthIfExists().getMandatory(item));
		assertEquals("one", field.getListSources().get(0).get(item));
		assertEquals("two", field.getListSources().get(1).get(item));
		assertEquals(null,  field.getListSources().get(2).get(item));
	}

	@Test void testCreateNull()
	{
		assertFails(
				() -> new AnItem((Collection<String>)null),
				MandatoryViolationException.class,
				"mandatory violation for " + field);
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final LimitedListField<String> field =
				LimitedListField.create(new StringField(), 3);

		AnItem(@Nonnull final Collection<String> field)
		{
			this(AnItem.field.map(field));
		}


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
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
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException,
					com.exedio.cope.pattern.ListSizeViolationException
		{
			AnItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);

	public LimitedListFieldMandatoryTest()
	{
		super(MODEL);
	}
}
