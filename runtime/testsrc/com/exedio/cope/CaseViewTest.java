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

package com.exedio.cope;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CaseViewTest extends TestWithEnvironment
{
	public CaseViewTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		new AnItem("lower");
		new AnItem("UPPER");
		new AnItem("Numbers1234567890");
		final AnItem szItem = new AnItem(
				"A \u00c4; O \u00d6; U \u00dc; " +
				"a \u00e4; o \u00f6; u \u00fc; sz \u00df;");
		new AnItem("Euro \u20ac;");
		new AnItem(
				"Latin Letter A with Ring Above"+" Small \u00e5 Capital \u00c5; " +
				"Latin Letter L with Acute"     +" Small \u013a Capital \u0139; " +
				"Latin Letter C with Dot Above" +" Small \u010b Capital \u010a;");
		new AnItem(
				"Greek Letter Epsilon"          +" Small \u03b5 Capital \u0395;");
		final AnItem cyrillicItem = new AnItem(
				"Cyrillic Letter Ha"            +" Small \u0445 Capital \u0425; " +
				"Cyrillic Letter Ha with Stroke"+" Small \u04ff Capital \u04fe;");
		final Query<List<Object>> query = Query.newQuery(
				new Selectable<?>[]{
						AnItem.field,
						AnItem.fieldLower,
						AnItem.fieldUpper},
				AnItem.TYPE, null);
		query.setOrderByThis(true);
		assertEquals(asList(
				asList("lower", "lower", "LOWER"),
				asList("UPPER", "upper", "UPPER"),
				asList("Numbers1234567890", "numbers1234567890", "NUMBERS1234567890"),
				asList(
						"A \u00c4; O \u00d6; U \u00dc; " +
						"a \u00e4; o \u00f6; u \u00fc; sz \u00df;",
						"a \u00e4; o \u00f6; u \u00fc; " +
						"a \u00e4; o \u00f6; u \u00fc; sz \u00df;",
						"A \u00c4; O \u00d6; U \u00dc; " +
						"A \u00c4; O \u00d6; U \u00dc; SZ "+(hsqldb?"SS":"\u00df") + ";"),
				asList("Euro \u20ac;", "euro \u20ac;", "EURO \u20ac;"),
				asList(
						"Latin Letter A with Ring Above"+" Small \u00e5 Capital \u00c5; " +
						"Latin Letter L with Acute"     +" Small \u013a Capital \u0139; " +
						"Latin Letter C with Dot Above" +" Small \u010b Capital \u010a;",
						"latin letter a with ring above"+" small \u00e5 capital \u00e5; " +
						"latin letter l with acute"     +" small \u013a capital \u013a; " +
						"latin letter c with dot above" +" small \u010b capital \u010b;",
						"LATIN LETTER A WITH RING ABOVE"+" SMALL \u00c5 CAPITAL \u00c5; " +
						"LATIN LETTER L WITH ACUTE"     +" SMALL \u0139 CAPITAL \u0139; " +
						"LATIN LETTER C WITH DOT ABOVE" +" SMALL \u010a CAPITAL \u010a;"),
				asList(
						"Greek Letter Epsilon"          +" Small \u03b5 Capital \u0395;",
						"greek letter epsilon"          +" small \u03b5 capital \u03b5;",
						"GREEK LETTER EPSILON"          +" SMALL \u0395 CAPITAL \u0395;"),
				asList(
						"Cyrillic Letter Ha"            +" Small \u0445 Capital \u0425; " +
						"Cyrillic Letter Ha with Stroke"+" Small \u04ff Capital \u04fe;",
						"cyrillic letter ha"            +" small \u0445 capital \u0445; " +
						"cyrillic letter ha with stroke"+" small \u04ff capital "+(mysql?"\u04fe":"\u04ff")+";",
						"CYRILLIC LETTER HA"            +" SMALL \u0425 CAPITAL \u0425; " +
						"CYRILLIC LETTER HA WITH STROKE"+" SMALL "+(mysql?"\u04ff":"\u04fe")+" CAPITAL \u04fe;")),
				query.search());

		final Query<List<Object>> mapJavaQuery = Query.newQuery(
				new Selectable<?>[]{
						AnItem.TYPE.getThis(),
						AnItem.field.toLowerCase(),
						AnItem.field.toUpperCase()},
				AnItem.TYPE, null);
		mapJavaQuery.setOrderByThis(true);
		for(final List<Object> l : mapJavaQuery.search())
		{
			final AnItem i = (AnItem)l.get(0);
			String expectedLower = (String)l.get(1);
			String expectedUpper = (String)l.get(2);

			if(!hsqldb && i.equals(szItem))
				expectedUpper = replace(expectedUpper, "\u00df", "SS");

			if(mysql && i.equals(cyrillicItem))
			{
				expectedLower = replace(expectedLower, "\u04fe", "\u04ff");
				expectedUpper = replace(expectedUpper, "\u04ff", "\u04fe");
			}

			assertEquals(expectedLower, i.getFieldLower(), "LOWER " + i.getCopeID());
			assertEquals(expectedUpper, i.getFieldUpper(), "UPPER " + i.getCopeID());
		}
	}

	private static String replace(
			final String value,
			final String target,
			final String replacement)
	{
		final String result = value.replace(target, replacement);
		assertNotEquals(value, result);
		return result;
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		static final StringField field = new StringField().lengthMax(200);
		static final CaseView fieldLower = field.toLowerCase();
		static final CaseView fieldUpper = field.toUpperCase();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nonnull final java.lang.String field)
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
		java.lang.String getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			AnItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		java.lang.String getFieldLower()
		{
			return AnItem.fieldLower.getSupported(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		java.lang.String getFieldUpper()
		{
			return AnItem.fieldUpper.getSupported(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
