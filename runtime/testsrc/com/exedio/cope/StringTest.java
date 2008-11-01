/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.exedio.cope.testmodel.StringItem;
import com.exedio.cope.util.CharSet;

public class StringTest extends TestmodelTest
{
	boolean supports;
	String emptyString;
	StringItem item, item2;
	int numberOfItems;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		supports = model.supportsEmptyStrings();
		emptyString = supports ? "" : null;
		item = deleteOnTearDown(new StringItem("StringTest"));
		item2 = deleteOnTearDown(new StringItem("StringTest2"));
		numberOfItems = 2;
	}
	
	public void testStrings()
	{
		// test model
		assertEquals(item.TYPE, item.any.getType());
		assertEquals("any", item.any.getName());
		assertEquals(false, item.any.isMandatory());
		assertEquals(null, item.any.getPattern());
		assertEquals(0, item.any.getMinimumLength());
		assertEquals(StringField.DEFAULT_LENGTH, item.any.getMaximumLength());
		assertEquals(null, item.any.getCharSet());

		assertEquals(item.TYPE, item.mandatory.getType());
		assertEquals("mandatory", item.mandatory.getName());
		assertEquals(true, item.mandatory.isMandatory());

		assertEquals(4, item.min4.getMinimumLength());
		assertEquals(StringField.DEFAULT_LENGTH, item.min4.getMaximumLength());

		assertEquals(0, item.max4.getMinimumLength());
		assertEquals(4, item.max4.getMaximumLength());
		assertEquals(null, item.max4.getCharSet());

		assertEquals(4, item.min4Max8.getMinimumLength());
		assertEquals(8, item.min4Max8.getMaximumLength());
		assertEquals(null, item.min4Max8.getCharSet());
		
		assertEquals(6, item.exact6.getMinimumLength());
		assertEquals(6, item.exact6.getMaximumLength());
		assertEquals(null, item.exact6.getCharSet());
		
		assertEquals(0, item.lowercase.getMinimumLength());
		assertEquals(StringField.DEFAULT_LENGTH, item.lowercase.getMaximumLength());
		assertEquals(new CharSet('a', 'z'), item.lowercase.getCharSet());
		
		assertEquals(item.TYPE, item.min4Upper.getType());
		assertEquals("min4Upper", item.min4Upper.getName());
		
		{
			final StringField orig = new StringField().optional();
			assertEquals(false, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertEquals(0, orig.getMinimumLength());
			assertEquals(StringField.DEFAULT_LENGTH, orig.getMaximumLength());

			final StringField copy = orig.copy();
			assertEquals(false, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertEquals(0, copy.getMinimumLength());
			assertEquals(StringField.DEFAULT_LENGTH, copy.getMaximumLength());
		}
		{
			final StringField orig = new StringField().toFinal().optional().lengthMin(10);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNull(orig.getImplicitUniqueConstraint());
			assertEquals(10, orig.getMinimumLength());
			assertEquals(StringField.DEFAULT_LENGTH, orig.getMaximumLength());
			
			final StringField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNull(copy.getImplicitUniqueConstraint());
			assertEquals(10, copy.getMinimumLength());
			assertEquals(StringField.DEFAULT_LENGTH, copy.getMaximumLength());
		}
		{
			final StringField orig = new StringField().toFinal().optional().unique().lengthMin(20);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNotNull(orig.getImplicitUniqueConstraint());
			assertEquals(20, orig.getMinimumLength());
			assertEquals(StringField.DEFAULT_LENGTH, orig.getMaximumLength());
			
			final StringField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNotNull(copy.getImplicitUniqueConstraint());
			assertEquals(20, copy.getMinimumLength());
			assertEquals(StringField.DEFAULT_LENGTH, copy.getMaximumLength());
		}
		{
			final StringField orig = new StringField().lengthRange(10, 20);
			assertEquals(false, orig.isFinal());
			assertEquals(true, orig.isMandatory());
			assertEquals(10, orig.getMinimumLength());
			assertEquals(20, orig.getMaximumLength());
			
			final StringField copy = orig.copy();
			assertEquals(false, copy.isFinal());
			assertEquals(true, copy.isMandatory());
			assertEquals(10, copy.getMinimumLength());
			assertEquals(20, copy.getMaximumLength());
		}
		
		assertWrongLength(-1, 20, "mimimum length must be positive, but was -1.");
		assertWrongLength( 0,  0, "maximum length must be greater zero, but was 0.");
		assertWrongLength(20, 10, "maximum length must be greater or equal mimimum length, but was 10 and 20.");

		// test conditions
		assertEquals(item.any.equal("hallo"), item.any.equal("hallo"));
		assertNotEquals(item.any.equal("hallo"), item.any.equal("bello"));
		assertNotEquals(item.any.equal("hallo"), item.any.equal((String)null));
		assertNotEquals(item.any.equal("hallo"), item.any.like("hallo"));
		assertEquals(item.any.equal(item.mandatory), item.any.equal(item.mandatory));
		assertNotEquals(item.any.equal(item.mandatory), item.any.equal(item.any));

		// test convenience for conditions
		assertEquals(item.any.startsWith("hallo"), item.any.like("hallo%"));
		assertEquals(item.any.endsWith("hallo"), item.any.like("%hallo"));
		assertEquals(item.any.contains("hallo"), item.any.like("%hallo%"));
		assertEquals(item.any.equalIgnoreCase("hallo"), item.any.toUpperCase().equal("HALLO"));
		assertEquals(item.any.likeIgnoreCase("hallo%"), item.any.toUpperCase().like("HALLO%"));
		assertEquals(item.any.startsWithIgnoreCase("hallo"), item.any.toUpperCase().like("HALLO%"));
		assertEquals(item.any.endsWithIgnoreCase("hallo"), item.any.toUpperCase().like("%HALLO"));
		assertEquals(item.any.containsIgnoreCase("hallo"), item.any.toUpperCase().like("%HALLO%"));

		// test check method
		try
		{
			item.mandatory.check(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation on a newly created item for " + item.mandatory, e.getMessage());
		}
		try
		{
			item.mandatory.check("");
			assert supports;
		}
		catch(MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation on a newly created item for " + item.mandatory, e.getMessage());
		}
		try
		{
			item.min4.check("123");
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertEquals(item.min4, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation on a newly created item, " +
					"'123' is too short for " + item.min4 + ", " +
					"must be at least 4 characters, but was 3.",
					e.getMessage());
		}
		
		// any
		item.setAny("1234");
		assertEquals("1234", item.getAny());
		item.setAny("123");
		assertEquals("123", item.getAny());
		
		// standard tests
		item.setAny(null);
		assertString(item, item2, item.any);
		assertString(item, item2, item.long1K);
		assertString(item, item2, item.long1M);
		assertString(item, item2, item.oracleNoCLOB);
		assertString(item, item2, item.oracleCLOB);
		assertStringSet(item, item.max4, "\u20ac\u20ac\u20ac\u20ac"); // euro in utf8 has two bytes
		
		{
			final StringItem itemEmptyInit = deleteOnTearDown(new StringItem("", false));
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
		catch(MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals("mandatory violation on " + item + " for " + item.mandatory, e.getMessage());
		}
		assertEquals("someOtherString", item.getMandatory());
	
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new StringItem((String)null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals("mandatory violation on a newly created item for " + item.mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new StringItem(new SetValue[]{});
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals("mandatory violation on a newly created item for " + item.mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		
		// mandatory and empty string
		try
		{
			item.setMandatory("");
			if(supports)
				assertEquals("", item.getMandatory());
			else
				fail();
		}
		catch(MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for " + item.mandatory, e.getMessage());
			assertEquals("someOtherString", item.getMandatory());
		}
		
		StringItem item3 = null;
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			item3 = deleteOnTearDown(new StringItem("", 0.0));
			numberOfItems++;
			if(supports)
				assertEquals("", item3.getMandatory());
			else
				fail();
		}
		catch(MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(item3.mandatory, e.getFeature());
			assertEquals(item3.mandatory, e.getFeature());
			assertEquals(item3, e.getItem());
			assertEquals("mandatory violation on a newly created item for " + item.mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		
		// min4
		try
		{
			item.setMin4("123");
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4, e.getFeature());
			assertEquals(item.min4, e.getFeature());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'123' is too short for " + item.min4 + ", " +
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
		catch(StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'12345' is too long for " + item.max4 + ", " +
					"must be at most 4 characters, but was 5.",
					e.getMessage());
		}
		assertEquals("1234", item.getMax4());
		restartTransaction();
		assertEquals("1234", item.getMax4());

		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new StringItem("12345", (Date)null);
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(
					"length violation on a newly created item, " +
					"'12345' is too long for " + item.max4 + ", " +
					"must be at most 4 characters, but was 5.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			StringItem.TYPE.newItem(
					item.mandatory.map("defaultByMax4"),
					item.max4.map("12345")
			);
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(
					"length violation on a newly created item, " +
					"'12345' is too long for " + item.max4 + ", " +
					"must be at most 4 characters, but was 5.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());

		// min4max8
		try
		{
			item.setMin4Max8("123");
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals("123", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'123' is too short for " + item.min4Max8 + ", " +
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
		catch(StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals("123456789", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'123456789' is too long for " + item.min4Max8 + ", " +
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
		catch(StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.exact6, e.getFeature());
			assertEquals(item.exact6, e.getFeature());
			assertEquals("12345", e.getValue());
			assertEquals(true, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'12345' is too short for " + item.exact6 + ", " +
					"must be at least 6 characters, but was 5.",
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
		catch(StringLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.exact6, e.getFeature());
			assertEquals(item.exact6, e.getFeature());
			assertEquals("1234567", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on " + item + ", " +
					"'1234567' is too long for " + item.exact6 + ", " +
					"must be at most 6 characters, but was 7.",
					e.getMessage());
		}
		assertEquals("123456", item.getExact6());
		restartTransaction();
		assertEquals("123456", item.getExact6());
		
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new StringItem("1234567", 40);
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.exact6, e.getFeature());
			assertEquals(item.exact6, e.getFeature());
			assertEquals("1234567", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on a newly created item, " +
					"'1234567' is too long for " + item.exact6 + ", " +
					"must be at most 6 characters, but was 7.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			StringItem.TYPE.newItem(
					item.mandatory.map("defaultByExact6"),
					item.exact6.map("1234567")
			);
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.exact6, e.getFeature());
			assertEquals(item.exact6, e.getFeature());
			assertEquals("1234567", e.getValue());
			assertEquals(false, e.isTooShort());
			assertEquals(
					"length violation on a newly created item, " +
					"'1234567' is too long for " + item.exact6 + ", " +
					"must be at most 6 characters, but was 7.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		
		// lowercase
		try
		{
			item.setLowercase("abcABC");
			fail();
		}
		catch(StringCharacterSetViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.lowercase, e.getFeature());
			assertEquals("abcABC", e.getValue());
			assertEquals('A', e.getCharacter());
			assertEquals(3, e.getPosition());
			assertEquals(
					"character set violation on " + item + ", " +
					"'abcABC' for " + item.lowercase + ", " +
					"contains forbidden character 'A' on position 3.",
					e.getMessage());
		}
		assertEquals(null, item.getLowercase());
		restartTransaction();
		assertEquals(null, item.getLowercase());
		item.setLowercase("abcdef");
		assertEquals("abcdef", item.getLowercase());
		restartTransaction();
		assertEquals("abcdef", item.getLowercase());
		
		model.checkUnsupportedConstraints();
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)item.any, Integer.valueOf(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + String.class.getName() + ", " + "but was a " + Integer.class.getName() + " for " + item.any + '.', e.getMessage());
		}
	}
	
	void assertWrongLength(final int minimumLength, final int maximumLength, final String message)
	{
		try
		{
			new StringField().optional().lengthRange(minimumLength, maximumLength);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
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
		final String result = new String(buf);
		//System.err.println("---------------------"+length+"--stringed");
		//System.err.println("---------------------"+length+"--end--"+result.substring(0, 80));
		return result;
	}

	void assertString(final Item item, final Item item2, final StringField sa)
	{
		final Type<? extends Item> type = item.getCopeType();
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
		
		final Type type = item.getCopeType();
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
		final String message;
		try
		{
			message = value+'('+valueChars+','+value.getBytes("utf8").length+')';
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		assertEquals(message, valueChars, sa.length().get(item));
		assertEquals(message, valueChars, new Query<Integer>(sa.length(), Cope.equalAndCast(item.getCopeType().getThis(), item)).searchSingletonStrict());
	}
	
	protected static List<? extends Object> search(final FunctionField<? extends Object> selectAttribute)
	{
		return search(selectAttribute, null);
	}
	
	protected static List<? extends Object> search(final FunctionField<? extends Object> selectAttribute, final Condition condition)
	{
		return new Query<Object>(selectAttribute, condition).search();
	}
	
	// TODO should work without
	private boolean searchEnabled(final StringField field)
	{
		return !oracle || field.getMaximumLength()<=com.exedio.cope.Dialect.ORACLE_VARCHAR_MAX_CHARS;
	}
}
