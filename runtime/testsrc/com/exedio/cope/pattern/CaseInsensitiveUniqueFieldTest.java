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

import static com.exedio.cope.SchemaInfo.columnName;
import static com.exedio.cope.pattern.CaseInsensitiveUniqueField.create;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.MysqlExtendedVarchar;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.SQLRuntimeException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

/**
 * @see CaseInsensitiveUniqueFieldMandatoryTest
 * @see CaseInsensitiveUniqueFieldFinalTest
 */
public class CaseInsensitiveUniqueFieldTest extends TestWithEnvironment
{
	public CaseInsensitiveUniqueFieldTest()
	{
		super(model);
	}

	private static final Model model = new Model(MyItem.TYPE);

	@Test void test()
	{
		assertEquals(false, MyItem.field               .isFinal());
		assertEquals(false, MyItem.field.getPristine() .isFinal());
		assertEquals(false, MyItem.field.getLowerCase().isFinal());
		assertEquals(false, MyItem.field               .isMandatory());
		assertEquals(false, MyItem.field.getPristine ().isMandatory());
		assertEquals(false, MyItem.field.getLowerCase().isMandatory());
		assertEquals(false, MyItem.field.isInitial());

		assertEquals("field",           columnName(MyItem.field.getPristine ()));
		assertEquals("field_lowerCase", columnName(MyItem.field.getLowerCase()));
		assertEquals(false, MyItem.field.getPristine ().isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(true,  MyItem.field.getLowerCase().isAnnotationPresent(MysqlExtendedVarchar.class));

		final MyItem i1 = new MyItem("Alpha");
		final MyItem i2 = new MyItem("Beta");
		final MyItem iN = new MyItem((String)null);
		assertEquals("Alpha", i1.getField());
		assertEquals("Beta",  i2.getField());
		assertEquals(null,    iN.getField());
		assertEquals("alpha", MyItem.field.getLowerCase().get(i1));
		assertEquals("beta",  MyItem.field.getLowerCase().get(i2));
		assertEquals(null,    MyItem.field.getLowerCase().get(iN));

		assertFails(
				() -> new MyItem("Alpha"),
				UniqueViolationException.class,
				"unique violation for MyItem.field-lowerCaseImplicitUnique");
		assertFails(
				() -> new MyItem("alpha"),
				UniqueViolationException.class,
				"unique violation for MyItem.field-lowerCaseImplicitUnique");
		assertFails(
				() -> new MyItem("ALPHA"),
				UniqueViolationException.class,
				"unique violation for MyItem.field-lowerCaseImplicitUnique");

		assertFails(
				() -> i2.setField("alphA"),
				UniqueViolationException.class,
				"unique violation on " + i2 + " for MyItem.field-lowerCaseImplicitUnique");
		assertEquals("Beta", i2.getField());

		assertEquals(i1, MyItem.forField("Alpha"));
		assertEquals(i1, MyItem.forField("alpha"));
		assertEquals(i1, MyItem.forField("ALPHA"));
		assertEquals(i2, MyItem.forField("Beta"));
		assertEquals(i2, MyItem.forField("beta"));
		assertEquals(i2, MyItem.forField("BETA"));
		assertEquals(null, MyItem.forField("Gamma"));

		assertEquals(i1, MyItem.forFieldStrict("Alpha"));
		assertEquals(i1, MyItem.forFieldStrict("alpha"));
		assertEquals(i1, MyItem.forFieldStrict("ALPHA"));
		assertEquals(i2, MyItem.forFieldStrict("Beta"));
		assertEquals(i2, MyItem.forFieldStrict("beta"));
		assertEquals(i2, MyItem.forFieldStrict("BETA"));
		assertFails(
				() -> MyItem.forFieldStrict("Gamma"),
				IllegalArgumentException.class,
				"expected result of size one, but was empty for query: select this from MyItem where field-lowerCase='gamma'");

		assertFails(
				() -> MyItem.forField(null),
				NullPointerException.class,
				"cannot search uniquely for null on MyItem.field-lowerCase");
		assertFails(
				() -> MyItem.forFieldStrict(null),
				NullPointerException.class,
				"cannot search uniquely for null on MyItem.field-lowerCase");

		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.isIgnoreCase("Alpha")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.isIgnoreCase("alpha")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.isCaseSensitive("Alpha")));
		assertEquals(List.of(  ), MyItem.TYPE.search(MyItem.field.isCaseSensitive("alpha")));

		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.startsWithIgnoreCase("Alp")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.startsWithIgnoreCase("alp")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.startsWithCaseSensitive("Alp")));
		assertEquals(List.of(  ), MyItem.TYPE.search(MyItem.field.startsWithCaseSensitive("alp")));

		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.endsWithIgnoreCase("pha")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.endsWithIgnoreCase("phA")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.endsWithCaseSensitive("pha")));
		assertEquals(List.of(  ), MyItem.TYPE.search(MyItem.field.endsWithCaseSensitive("phA")));

		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.containsIgnoreCase("Alpha")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.containsIgnoreCase("alpha")));
		assertEquals(List.of(i1), MyItem.TYPE.search(MyItem.field.containsCaseSensitive("Alpha")));
		assertEquals(List.of(  ), MyItem.TYPE.search(MyItem.field.containsCaseSensitive("alpha")));

		i2.setField("Gamma");
		assertEquals("Gamma", i2.getField());
		assertEquals("gamma",  MyItem.field.getLowerCase().get(i2));

		i2.setField(null);
		assertEquals(null, i2.getField());
		assertEquals(null,  MyItem.field.getLowerCase().get(i2));
	}

	@Test void sz()
	{
		// \u00df is an example where toUpperCase is not bijective; if CaseInsensitiveUniqueField would use a redundant
		// _upper_ case field instead of a _lower_ case field, values "ss" and "\u00df" would collide
		final MyItem i1 = new MyItem("klauss");
		new MyItem("klausz");
		final MyItem i3 = new MyItem("klau\u00df");
		assertEquals("KLAUSS", i3.getField().toUpperCase(Locale.ENGLISH));
		//noinspection EnumSwitchStatementWhichMissesCases
		switch (dialect)
		{
			case mysql, postgresql ->
			{
				// DB says toUpperCase("\u00df") is "\u00df"
				assertEquals(
					"KLAU\u00df",
					new Query<>(MyItem.field.getPristine().toUpperCase(), MyItem.TYPE.getThis().is(i3)).searchSingletonStrict()
				);
				assertContains(
					i1,
					MyItem.TYPE.search(MyItem.field.getPristine().toUpperCase().is("KLAUSS"))
				);
			}
			default ->
			{
				// DB says toUpperCase("\u00df") is "SS"
				assertEquals(
					"KLAUSS",
					new Query<>(MyItem.field.getPristine().toUpperCase(), MyItem.TYPE.getThis().is(i3)).searchSingletonStrict()
				);
				assertContains(
					i1, i3,
					MyItem.TYPE.search(MyItem.field.getPristine().toUpperCase().is("KLAUSS"))
				);
			}
		}
		assertEquals(i1, MyItem.forField("KLAUSS"));
		assertEquals(i3, MyItem.forField(i3.getField()));
		assertEquals(i1, MyItem.forField(i3.getField().toUpperCase(Locale.ENGLISH)));
	}

	@Test void turkishDotlessI()
	{
		// in Turkish, lower-case of I is not i but \u0131
		final Locale restore = Locale.getDefault();
		try
		{
			Locale.setDefault(new Locale("tr"));

			final String unicodeUpperCaseDottedI = "\u0130";
			final String unicodeLowerCaseDotlessI = "\u0131";
			//noinspection StringToUpperCaseOrToLowerCaseWithoutLocale testing default locale set above
			assertEquals("i", unicodeUpperCaseDottedI.toLowerCase());
			//noinspection StringToUpperCaseOrToLowerCaseWithoutLocale testing default locale set above
			assertEquals(unicodeLowerCaseDotlessI, "I".toLowerCase());

			final MyItem standardI = new MyItem("I");
			assertEquals("I", standardI.getField());
			assertEquals("i", MyItem.field.getLowerCase().get(standardI));
			if(hsqldb || !SchemaInfo.supportsCheckConstraint(model))
			{
				final MyItem dottedI = new MyItem(unicodeUpperCaseDottedI);
				assertEquals(unicodeUpperCaseDottedI, dottedI.getField());
				final String unicodeCombiningDotAbove = "\u0307";
				assertEquals("i" + unicodeCombiningDotAbove, MyItem.field.getLowerCase().get(dottedI));
			}
			else
			{
				final var ex = assertThrows(
						SQLRuntimeException.class,
						() -> new MyItem(unicodeUpperCaseDottedI));
				assertEquals(switch(dialect)
						{
							case hsqldb -> throw new AssertionFailedError();
							case mysql -> "Check constraint 'MyItem_field_unisonCase' is violated.";
							case postgresql -> "ERROR: new row for relation \"MyItem\" violates check constraint \"MyItem_field_unisonCase\"";
						},
						dropMariaConnectionId(ex.getCause().getMessage()));
			}
		}
		finally
		{
			Locale.setDefault(restore);
		}
	}

	@WrapperType(indent=2)
	static class MyItem extends Item
	{
		@WrapperInitial
		static final CaseInsensitiveUniqueField field = create(new StringField().optional());

		/**
		 * Creates a new MyItem with all the fields initially needed.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.StringCharSetViolationException if field violates its character set constraint.
		 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
		 * @throws com.exedio.cope.UniqueViolationException if field is not unique.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringCharSetViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		/**
		 * Creates a new MyItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringCharSetViolationException
		{
			MyItem.field.set(this,field);
		}

		/**
		 * Finds a myItem by its {@link #field}, ignoring case.
		 * @param field shall be equal to field {@link #field}, ignoring case.
		 * @return null if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static final MyItem forField(@javax.annotation.Nonnull final java.lang.String field)
		{
			return MyItem.field.searchUnique(MyItem.class,field);
		}

		/**
		 * Finds a myItem by its {@link #field}, ignoring case.
		 * @param field shall be equal to field {@link #field}, ignoring case.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final MyItem forFieldStrict(@javax.annotation.Nonnull final java.lang.String field)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.field.searchUniqueStrict(MyItem.class,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for myItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
