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

import static com.exedio.cope.testmodel.StringItem.TYPE;
import static com.exedio.cope.testmodel.StringItem.any;
import static com.exedio.cope.testmodel.StringItem.exact6;
import static com.exedio.cope.testmodel.StringItem.long1K;
import static com.exedio.cope.testmodel.StringItem.long1M;
import static com.exedio.cope.testmodel.StringItem.lowercase;
import static com.exedio.cope.testmodel.StringItem.mandatory;
import static com.exedio.cope.testmodel.StringItem.max4;
import static com.exedio.cope.testmodel.StringItem.min4;
import static com.exedio.cope.testmodel.StringItem.min4Max8;
import static com.exedio.cope.testmodel.StringItem.oracleCLOB;
import static com.exedio.cope.testmodel.StringItem.oracleNoCLOB;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.testmodel.StringItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringTest extends TestWithEnvironment
{
	public StringTest()
	{
		super(StringModelTest.MODEL);
	}

	boolean supports;
	String emptyString;
	StringItem item, item2;
	int numberOfItems;

	@BeforeEach final void setUp()
	{
		supports = model.supportsEmptyStrings();
		emptyString = supports ? "" : null;
		item = new StringItem("StringTest");
		item2 = new StringItem("StringTest2");
		numberOfItems = 2;
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test void testStrings()
	{
		// test check method
		try
		{
			mandatory.check(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(mandatory, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		try
		{
			mandatory.check("");
			assert supports;
		}
		catch(final MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(mandatory, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		try
		{
			min4.check("123");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertSame(min4, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation, " +
					"'123' is too short for " + min4 + ", " +
					"must be at least 4 characters, but was 3.",
					e.getMessage());
		}
		min4.check("1234");

		// any
		item.setAny("1234");
		assertEquals("1234", item.getAny());
		item.setAny("123");
		assertEquals("123", item.getAny());

		// standard tests
		item.setAny(null);
		assertString(item, item2, any);
		assertString(item, item2, long1K);
		assertString(item, item2, long1M);
		assertString(item, item2, oracleNoCLOB);
		assertString(item, item2, oracleCLOB);
		assertStringSet(item, max4, "\u20ac\u20ac\u20ac\u20ac"); // euro in utf8 has two bytes

		{
			final StringItem itemEmptyInit = new StringItem("", false);
			numberOfItems++;
			assertEquals(emptyString, itemEmptyInit.getAny());
			restartTransaction();
			assertEquals(emptyString, itemEmptyInit.getAny());
		}

		// mandatory
		assertEquals("StringTest", item.getMandatory());

		item.setMandatory("someOtherString");
		assertEquals("someOtherString", item.getMandatory());

		try
		{
			item.setMandatory(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals("mandatory violation on " + item + " for " + mandatory, e.getMessage());
		}
		assertEquals("someOtherString", item.getMandatory());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new StringItem((String)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new StringItem(new SetValue<?>[]{});
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		// mandatory and empty string
		try
		{
			item.setMandatory("");
			if(supports)
				assertEquals("", item.getMandatory());
			else
				fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for " + mandatory, e.getMessage());
			assertEquals("someOtherString", item.getMandatory());
		}

		StringItem item3 = null;
		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			item3 = new StringItem("", 0.0);
			numberOfItems++;
			if(supports)
				assertEquals("", item3.getMandatory());
			else
				fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals(item3, e.getItem());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		// min4
		try
		{
			item.setMin4("123");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4, e.getFeature());
			assertSame(min4, e.getFeature());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'123' is too short for " + min4 + ", " +
					"must be at least 4 characters, but was 3.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4());
		restartTransaction();
		assertEquals(null, item.getMin4());

		item.setMin4("1234");
		assertEquals("1234", item.getMin4());

		// max4
		item.setMax4("1234");
		assertEquals("1234", item.getMax4());
		try
		{
			item.setMax4("12345");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'12345' is too long for " + max4 + ", " +
					"must be at most 4 characters, but was 5.",
					e.getMessage());
		}
		assertEquals("1234", item.getMax4());
		restartTransaction();
		assertEquals("1234", item.getMax4());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new StringItem("12345", null);
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(
					"length violation, " +
					"'12345' is too long for " + max4 + ", " +
					"must be at most 4 characters, but was 5.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			TYPE.newItem(
					mandatory.map("defaultByMax4"),
					max4.map("12345")
			);
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(
					"length violation, " +
					"'12345' is too long for " + max4 + ", " +
					"must be at most 4 characters, but was 5.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		// min4max8
		try
		{
			item.setMin4Max8("123");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4Max8, e.getFeature());
			assertSame(min4Max8, e.getFeature());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'123' is too short for " + min4Max8 + ", " +
					"must be at least 4 characters, but was 3.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4Max8());
		restartTransaction();
		assertEquals(null, item.getMin4Max8());

		item.setMin4Max8("1234");
		assertEquals("1234", item.getMin4Max8());

		item.setMin4Max8("12345678");
		assertEquals("12345678", item.getMin4Max8());

		restartTransaction();
		assertEquals("12345678", item.getMin4Max8());

		try
		{
			item.setMin4Max8("123456789");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4Max8, e.getFeature());
			assertSame(min4Max8, e.getFeature());
			assertEquals("123456789", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'123456789' is too long for " + min4Max8 + ", " +
					"must be at most 8 characters, but was 9.",
					e.getMessage());
		}
		assertEquals("12345678", item.getMin4Max8());
		restartTransaction();
		assertEquals("12345678", item.getMin4Max8());

		// exact6
		try
		{
			item.setExact6("12345");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(exact6, e.getFeature());
			assertSame(exact6, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'12345' is too short for " + exact6 + ", " +
					"must be exactly 6 characters, but was 5.",
					e.getMessage());
		}
		assertEquals(null, item.getExact6());
		restartTransaction();
		assertEquals(null, item.getExact6());

		item.setExact6("123456");
		assertEquals("123456", item.getExact6());

		restartTransaction();
		assertEquals("123456", item.getExact6());

		try
		{
			item.setExact6("1234567");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(exact6, e.getFeature());
			assertSame(exact6, e.getFeature());
			assertEquals("1234567", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'1234567' is too long for " + exact6 + ", " +
					"must be exactly 6 characters, but was 7.",
					e.getMessage());
		}
		assertEquals("123456", item.getExact6());
		restartTransaction();
		assertEquals("123456", item.getExact6());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new StringItem("1234567", 40);
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(exact6, e.getFeature());
			assertSame(exact6, e.getFeature());
			assertEquals("1234567", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation, " +
					"'1234567' is too long for " + exact6 + ", " +
					"must be exactly 6 characters, but was 7.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			TYPE.newItem(
					mandatory.map("defaultByExact6"),
					exact6.map("1234567")
			);
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(exact6, e.getFeature());
			assertSame(exact6, e.getFeature());
			assertEquals("1234567", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation, " +
					"'1234567' is too long for " + exact6 + ", " +
					"must be exactly 6 characters, but was 7.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		// lowercase
		try
		{
			item.setLowercase("abcABC");
			fail();
		}
		catch(final StringCharSetViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(lowercase, e.getFeature());
			assertEquals("abcABC", e.getValue());
			assertEquals('A', e.getCharacter());
			assertEquals(3, e.getPosition());
			assertEquals(
					"character set violation on " + item + ", " +
					"'abcABC' for " + lowercase + ", " +
					"contains forbidden character 'A' (U+0041) on position 3.",
					e.getMessage());
		}
		assertEquals(null, item.getLowercase());
		restartTransaction();
		assertEquals(null, item.getLowercase());
		item.setLowercase("abcdef");
		assertEquals("abcdef", item.getLowercase());
		restartTransaction();
		assertEquals("abcdef", item.getLowercase());

		commit();
		model.checkUnsupportedConstraints();
		startTransaction();
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	@Test void testUnchecked()
	{
		try
		{
			item.set((FunctionField)any, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + String.class.getName() + ", " +
					"but was a " + Integer.class.getName() + " for " + any + '.',
					e.getMessage());
		}
	}

	static final String makeString(final int length)
	{
		final int segmentLength = length/20 + 1;
		//System.err.println("---------------------"+length+"--start");
		final char[] buf = new char[length];
		//System.err.println("---------------------"+length+"--allocated");

		char val = 'A';
		for(int i = 0; i<length; i+=segmentLength)
			Arrays.fill(buf, i, Math.min(i+segmentLength, length), val++);

		final String lengthString = String.valueOf(length) + ':';
		System.arraycopy(lengthString.toCharArray(), 0, buf, 0, Math.min(lengthString.length(), length));

		//System.err.println("---------------------"+length+"--copied");
		@SuppressWarnings("UnnecessaryLocalVariable")
		final String result = new String(buf);
		//System.err.println("---------------------"+length+"--stringed");
		//System.err.println("---------------------"+length+"--end--"+result.substring(0, 80));
		return result;
	}

	@SuppressWarnings("HardcodedLineSeparator")
	void assertString(final Item item, final Item item2, final StringField sa)
	{
		final Type<?> type = item.getCopeType();
		assertEquals(type, item2.getCopeType());

		final String VALUE = "someString";
		final String VALUE2 = VALUE+"2";
		final String VALUE_UPPER = "SOMESTRING";
		final String VALUE2_UPPER = "SOMESTRING2";

		final UppercaseView saup = sa.toUpperCase();
		final LengthView saln = sa.length();

		assertEquals(null, sa.get(item));
		assertEquals(null, saup.get(item));
		assertEquals(null, saln.get(item));
		assertEquals(null, sa.get(item2));
		assertEquals(null, saup.get(item2));
		assertEquals(null, saln.get(item2));
		assertContains(item, item2, type.search(sa.isNull()));

		sa.set(item, VALUE);
		assertEquals(VALUE, sa.get(item));
		assertEquals(VALUE_UPPER, saup.get(item));
		assertEquals(Integer.valueOf(VALUE.length()), saln.get(item));

		sa.set(item2, VALUE2);
		assertEquals(VALUE2, sa.get(item2));
		assertEquals(VALUE2_UPPER, saup.get(item2));
		assertEquals(Integer.valueOf(VALUE2.length()), saln.get(item2));

		if(searchEnabled(sa))
		{
			assertContains(item, type.search(sa.equal(VALUE)));
			assertContains(item2, type.search(sa.notEqual(VALUE)));
			assertContains(type.search(sa.equal(VALUE_UPPER)));
			assertContains(item, type.search(sa.like(VALUE)));
			assertContains(item, item2, type.search(sa.like(VALUE+"%")));
			assertContains(item2, type.search(sa.like(VALUE2+"%")));

			assertContains(item, type.search(saup.equal(VALUE_UPPER)));
			assertContains(item2, type.search(saup.notEqual(VALUE_UPPER)));
			assertContains(type.search(saup.equal(VALUE)));
			assertContains(item, type.search(saup.like(VALUE_UPPER)));
			assertContains(item, item2, type.search(saup.like(VALUE_UPPER+"%")));
			assertContains(item2, type.search(saup.like(VALUE2_UPPER+"%")));

			assertContains(item, type.search(saln.equal(VALUE.length())));
			assertContains(item2, type.search(saln.notEqual(VALUE.length())));
			assertContains(type.search(saln.equal(VALUE.length()+2)));

			assertContains(VALUE, VALUE2, search(sa));
			assertContains(VALUE, search(sa, sa.equal(VALUE)));
			// TODO allow functions for select
			//assertContains(VALUE_UPPER, search(saup, sa.equal(VALUE)));
		}

		restartTransaction();
		assertEquals(VALUE, sa.get(item));
		assertEquals(VALUE_UPPER, saup.get(item));
		assertEquals(Integer.valueOf(VALUE.length()), saln.get(item));

		{
			sa.set(item, "");
			assertEquals(emptyString, sa.get(item));
			restartTransaction();
			assertEquals(emptyString, sa.get(item));
			assertEquals(list(item), type.search(sa.equal(emptyString)));
			if(searchEnabled(sa))
			{
				assertEquals(list(), type.search(sa.equal("x")));
				assertEquals(supports ? list(item) : list(), type.search(sa.equal("")));
			}
		}

		assertStringSet(item, sa, " trim "); // ensure that leading/trailing white space is not removed
		assertStringSet(item, sa,
			"Auml \u00c4; "
			+ "Ouml \u00d6; "
			+ "Uuml \u00dc; "
			+ "auml \u00e4; "
			+ "ouml \u00f6; "
			+ "uuml \u00fc; "
			+ "szlig \u00df; ");
		assertStringSet(item, sa,
			"paragraph \u00a7; "
			+ "kringel \u00b0; "
			//+ "abreve \u0102; "
			//+ "hebrew \u05d8 "
			+ "euro \u20ac");

		// byte C5 in several encodings
		assertStringSet(item, sa,
			"Aringabove \u00c5;"       // ISO-8859-1/4/9/10 (Latin1/4/5/6)
			+ "Lacute \u0139;"         // ISO-8859-2 (Latin2)
			+ "Cdotabove \u010a;"      // ISO-8859-3 (Latin3)
			+ "ha \u0425;"             // ISO-8859-5 (Cyrillic)
			+ "AlefHamzaBelow \u0625;" // ISO-8859-6 (Arabic)
			+ "Epsilon \u0395;"        // ISO-8859-7 (Greek)
			);

		// test SQL injection
		// if SQL injection is not prevented properly,
		// the following lines will throw a SQLException
		// due to column "hijackedColumn" not found
		assertStringSet(item, sa, "value',hijackedColumn='otherValue");

		assertStringSet(item, sa, "a'b");
		assertStringSet(item, sa, "ab'");
		assertStringSet(item, sa, "'ab");
		assertStringSet(item, sa, "a''b");
		assertStringSet(item, sa, "ab''");
		assertStringSet(item, sa, "''ab");

		// PostgreSQL does not support \0 in characters:
		// https://www.postgresql.org/message-id/45DAE076.1060407@opencloud.com
		// Error message is
		// ERROR: invalid byte sequence for encoding "UTF8": 0x00
		if(!postgresql)
			assertStringSet(item, sa, "-\0- zero");

		// Test that database does not interpret backslash sequences
		assertStringSet(item, sa, "-\\0- slash zero");
		assertStringSet(item, sa, "-\\'- slash quote");
		assertStringSet(item, sa, "-\\\"- slash double quote");
		assertStringSet(item, sa, "-\\b- slash b");
		assertStringSet(item, sa, "-\\f- slash f");
		assertStringSet(item, sa, "-\\n- slash n");
		assertStringSet(item, sa, "-\\r- slash r");
		assertStringSet(item, sa, "-\\t- slash t");
		assertStringSet(item, sa, "-\\z- slash z");
		assertStringSet(item, sa, "-\\Z- slash Z");
		assertStringSet(item, sa, "-\\\\- slash slash");
		assertStringSet(item, sa, "-\\%- slash percent");
		assertStringSet(item, sa, "-\\_- slash underscore");
		assertStringSet(item, sa, "-\\xaf- slash hex");
		assertStringSet(item, sa, "-\\uafec- slash unicode");

		// TODO use streams for oracle
		assertStringSet(item, sa, makeString(Math.min(sa.getMaximumLength(), oracle ? (1300/*32766-1*/) : (4 * 1000 * 1000))));

		sa.set(item, null);
		assertEquals(null, sa.get(item));
		assertEquals(null, saup.get(item));
		assertEquals(null, saln.get(item));
		assertContains(item, type.search(sa.isNull()));

		sa.set(item2, null);
		assertEquals(null, sa.get(item2));
		assertEquals(null, saup.get(item2));
		assertEquals(null, saln.get(item2));
		assertContains(item, item2, type.search(sa.isNull()));
	}

	private void assertStringSet(final Item item, final StringField sa, final String value)
	{
		//if(value.length()<=100) System.out.println("---------"+value+"------------");

		final Type<?> type = item.getCopeType();
		sa.set(item, value);
		assertEquals(value, sa.get(item));
		restartTransaction();
		assertEquals(value, sa.get(item));
		if(searchEnabled(sa))
		{
			assertEquals(list(item), type.search(sa.equal(value)));
			assertEquals(list(), type.search(sa.equal(value+"x")));
		}

		// test length view
		final Integer valueChars = value.length();
		final String message = value+'('+valueChars+','+value.getBytes(UTF_8).length+')';
		assertEquals(valueChars, sa.length().get(item), message);
		assertEquals(valueChars, new Query<>(sa.length(), Cope.equalAndCast(item.getCopeType().getThis(), item)).searchSingletonStrict(), message);
	}

	protected static List<?> search(final FunctionField<?> selectAttribute)
	{
		return search(selectAttribute, null);
	}

	protected static List<?> search(final FunctionField<?> selectAttribute, final Condition condition)
	{
		return new Query<>(selectAttribute, condition).search();
	}

	// TODO should work without
	private boolean searchEnabled(final StringField field)
	{
		return !oracle || field.getMaximumLength()<=com.exedio.cope.OracleDialect.VARCHAR_MAX_CHARS;
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}
