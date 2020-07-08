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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.util.CharSet.ALPHA;
import static com.exedio.cope.util.CharSet.ALPHA_LOWER;
import static com.exedio.cope.util.CharSet.ALPHA_UPPER;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.CharSet;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator") // OK: testing line separator characters
public class CharSetConditionTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	public CharSetConditionTest()
	{
		super(MODEL);
	}

	@BeforeEach void beforeEach()
	{
		assumeTrue(MODEL.supportsEmptyStrings());
		new AnItem((String)null);
	}


	@Test void testAlpha()
	{
		final AnItem upperOnly = new AnItem("ABCXYZ");
		final AnItem lowerOnly = new AnItem("abcxyz");
		final AnItem upperSingleA = new AnItem("A");
		final AnItem lowerSingleA = new AnItem("a");
		final AnItem upperSingleZ = new AnItem("Z");
		final AnItem lowerSingleZ = new AnItem("z");
		final AnItem upperWithinLower = new AnItem("abcABCXYZxyz");
		final AnItem lowerWithinUpper = new AnItem("ABCabcxyzXYZ");
		new AnItem("ab\u00fccd");
		final AnItem empty = new AnItem("");

		assertIt(ALPHA_UPPER, upperOnly, upperSingleA, upperSingleZ, empty);
		assertIt(ALPHA_LOWER, lowerOnly, lowerSingleA, lowerSingleZ, empty);
		assertIt(ALPHA,
				upperOnly, lowerOnly, upperSingleA, lowerSingleA, upperSingleZ, lowerSingleZ,
				upperWithinLower, lowerWithinUpper, empty);
		assertIt(ALPHA_UPPER_NL,
				upperOnly, upperSingleA, upperSingleZ,
				empty);
		assertIt(ALPHA_UPPER_CR,
				upperOnly, upperSingleA, upperSingleZ,
				empty);
		assertIt(ALPHA_UPPER_NL_CR,
				upperOnly, upperSingleA, upperSingleZ,
				empty);
	}


	@Test void testLineBreaks()
	{
		final AnItem plain = new AnItem("ABCXYZ");
		final AnItem justNL   = new AnItem(NL);
		final AnItem justCR   = new AnItem(CR);
		final AnItem justNLCR = new AnItem(NLCR);
		final AnItem justCRNL = new AnItem(CRNL);
		final AnItem startNL   = new AnItem(NL  +"ABCXYZ");
		final AnItem startCR   = new AnItem(CR  +"ABCXYZ");
		final AnItem startNLCR = new AnItem(NLCR+"ABCXYZ");
		final AnItem startCRNL = new AnItem(CRNL+"ABCXYZ");
		final AnItem inNL   = new AnItem("ABC"+NL+"XYZ");
		final AnItem inCR   = new AnItem("ABC"+CR+"XYZ");
		final AnItem inNLCR = new AnItem("ABC"+NLCR+"XYZ");
		final AnItem inCRNL = new AnItem("ABC"+CRNL+"XYZ");
		final AnItem endNL   = new AnItem("ABCXYZ"+NL);
		final AnItem endCR   = new AnItem("ABCXYZ"+CR);
		final AnItem endNLCR = new AnItem("ABCXYZ"+NLCR);
		final AnItem endCRNL = new AnItem("ABCXYZ"+CRNL);
		final AnItem empty = new AnItem("");

		assertIt(ALPHA_UPPER,    plain,                               empty);
		assertIt(ALPHA_UPPER_NL, plain, justNL, startNL, inNL, endNL, empty);
		assertIt(ALPHA_UPPER_CR, plain, justCR, startCR, inCR, endCR, empty);
		assertIt(ALPHA_UPPER_NL_CR, plain,
				justNL,  justCR,  justNLCR,  justCRNL,
				startNL, startCR, startNLCR, startCRNL,
				inNL,    inCR,    inNLCR,    inCRNL,
				endNL,   endCR,   endNLCR,   endCRNL,
				empty);
	}

	private static final CharSet ALPHA_UPPER_NL    = new CharSet('\n', '\n',             'A', 'Z');
	private static final CharSet ALPHA_UPPER_CR    = new CharSet(            '\r', '\r', 'A', 'Z');
	private static final CharSet ALPHA_UPPER_NL_CR = new CharSet('\n', '\n', '\r', '\r', 'A', 'Z');

	private static final String NL   = "\n";
	private static final String CR   = "\r";
	private static final String NLCR = "\n\r";
	private static final String CRNL = "\r\n";


	@Test void testWhiteSpace()
	{
		final AnItem plain = new AnItem("ABCXYZ");
		final AnItem justTAB   = new AnItem(TAB);
		final AnItem justSPC   = new AnItem(SPC);
		final AnItem startTAB   = new AnItem(TAB  +"ABCXYZ");
		final AnItem startSPC   = new AnItem(SPC  +"ABCXYZ");
		final AnItem inTAB   = new AnItem("ABC"+TAB+"XYZ");
		final AnItem inSPC   = new AnItem("ABC"+SPC+"XYZ");
		final AnItem endTAB   = new AnItem("ABCXYZ"+TAB);
		final AnItem endSPC   = new AnItem("ABCXYZ"+SPC);
		final AnItem empty = new AnItem("");

		assertIt(ALPHA_UPPER,         plain,                                   empty);
		assertIt(ALPHA_UPPER_TAB,     plain, justTAB, startTAB, inTAB, endTAB, empty);
		assertIt(ALPHA_UPPER_SPC,     plain, justSPC, startSPC, inSPC, endSPC, empty);
		assertIt(ALPHA_UPPER_TAB_SPC, plain,
				justTAB,  justSPC,
				startTAB, startSPC,
				inTAB,    inSPC,
				endTAB,   endSPC,
				empty);
	}

	private static final CharSet ALPHA_UPPER_TAB = new CharSet('\t', '\t', 'A', 'Z');
	private static final CharSet ALPHA_UPPER_SPC = new CharSet(' ', ' ', 'A', 'Z');
	private static final CharSet ALPHA_UPPER_TAB_SPC = new CharSet('\t', '\t', ' ', ' ', 'A', 'Z');

	private static final String TAB   = "\t";
	private static final String SPC   = " ";


	@Test void testBasicPlane()
	{
		final AnItem start = new AnItem("A\u0391\u1200\u30A1\uD7F8\uFFFD");
		final AnItem end   = new AnItem("D\u0394\u1203\u30A4\uD7FB\uFFFD");
		new AnItem('@');      // ASCII Uppercase Latin
		new AnItem('E');
		new AnItem('\u0390'); // Greek
		new AnItem('\u0395');
		new AnItem('\u11FF'); // Ethiopic Syllable
		new AnItem('\u1204');
		new AnItem('\u30A0'); // Katakana Letter
		new AnItem('\u30A5');
		new AnItem('\uD7F7'); // Hangul Jamo Extended-B Jongseong
		new AnItem('\uD7FC');
		new AnItem('\uFFFC'); // Specials
		new AnItem('\uFFFE');
		final AnItem empty = new AnItem("");

		final CharSet cs = new CharSet(
				'A', 'D',            // ASCII Uppercase Latin: A-D
				'\u0391', '\u0394',  // Greek: Alpha-Delta
				'\u1200', '\u1203',  // Ethiopic Syllable: Ha-Haa
				'\u30A1', '\u30A4',  // Katakana Letter: Small A - I
				'\uD7F8', '\uD7FB',  // Hangul Jamo Extended-B Jongseong: Cieuc-Ssangpieup - Phieuph-Thieuth
				'\uFFFD', '\uFFFD'); // Specials: Replacement Character (black diamond with a white question mark)

		assertIt(cs, start, end, empty);
	}


	@Test void testPlanes()
	{
		final AnItem plain =
				new AnItem("ABCXYZabcxyz01278 9");
		final AnItem nl =
				new AnItem("ABC"+NL+"XYZabc"+NL+"xyz012"+NL+"78 9");
		final AnItem spc =
				new AnItem("ABC"+SPC+"XYZabc"+SPC+"xyz012"+SPC+"78 9");
		final AnItem aring =
				new AnItem("ABC\u00C5XY Z");
		final AnItem esh =
				new AnItem("ABC\u0425XY Z");
		// TODO test SMP such as emoji
		final AnItem empty = new AnItem("");

		assertIt(ASC_NO_CONTROLS,             plain,     spc,             empty);
		assertIt(ASC_NO_CONTROLS_PLUS_NL_TAB, plain, nl, spc,             empty);
		assertIt(BMP_NO_CONTROLS,             plain,     spc, aring, esh, empty);
		assertIt(BMP_NO_CONTROLS_PLUS_NL_TAB, plain, nl, spc, aring, esh, empty);
		assertIt(AUP_NO_CONTROLS,             plain,     spc, aring, esh, empty);
		assertIt(AUP_NO_CONTROLS_PLUS_NL_TAB, plain, nl, spc, aring, esh, empty);
	}

	private static final char BEFORE_SURROGATES = (char) 0xD7FF;
	private static final char AFTER_SURROGATES  = (char) 0xE000;
	private static final char END_UTF16         = (char) 0xFFFF;

	// ASCII
	private static final CharSet ASC_NO_CONTROLS =
			new CharSet(' ', (char)127);

	private static final CharSet ASC_NO_CONTROLS_PLUS_NL_TAB =
			new CharSet('\t', '\n', '\r', '\r', ' ', (char)127);

	// BMP = Unicode Basic Multilingual Plane
	private static final CharSet BMP_NO_CONTROLS =
			new CharSet(' ', BEFORE_SURROGATES, AFTER_SURROGATES, END_UTF16);

	private static final CharSet BMP_NO_CONTROLS_PLUS_NL_TAB =
			new CharSet('\t', '\n', '\r', '\r', ' ', BEFORE_SURROGATES, AFTER_SURROGATES, END_UTF16);

	// AUP = All Unicode Planes (Basic and Supplementary)
	private static final CharSet AUP_NO_CONTROLS =
			new CharSet(' ', END_UTF16);

	private static final CharSet AUP_NO_CONTROLS_PLUS_NL_TAB =
			new CharSet('\t', '\n', '\r', '\r', ' ', END_UTF16);


	private void assertIt(final CharSet charSet, final AnItem... expected)
	{
		final List<AnItem> all = AnItem.TYPE.search(AnItem.field.isNotNull(), AnItem.TYPE.getThis(), true);
		{
			final ArrayList<AnItem> actual = new ArrayList<>(all);
			actual.removeIf(item -> charSet.indexOfNotContains(item.getField()) >= 0);
			assertEquals(asList(expected), actual, "charSet");
		}

		final CharSetCondition condition =
				new CharSetCondition(AnItem.field, charSet);
		assertIt(condition, charSet, expected);

		final ArrayList<AnItem> expectedNot = new ArrayList<>(all);
		expectedNot.removeAll(asList(expected));
		assertIt(condition.not(), charSet, expectedNot.toArray(new AnItem[]{}));
	}

	private void assertIt(
			final Condition condition,
			final CharSet charSet,
			final AnItem... expected)
	{
		switch(dialect)
		{
			case mysql:
				if(charSet.isSubsetOfAscii() || MODEL.getEnvironmentInfo().isDatabaseVersionAtLeast(8, 0))
					assertEquals(
							asList(expected),
							AnItem.TYPE.search(condition, AnItem.TYPE.getThis(), true),
							"search");
				else
					assertNotSupported(condition, " with non-ASCII CharSet: " + charSet);
				break;
			case hsqldb:
			case oracle:
			case postgresql:
				assertNotSupported(condition, "");
				break;
		}
	}

	private static void assertNotSupported(
			final Condition condition,
			final String postfix)
	{
		assertFails(
				() -> AnItem.TYPE.search(condition),
				UnsupportedQueryException.class,
				"CharSetCondition not supported by " +
				MODEL.getConnectProperties().getDialect() +
				postfix);
	}

	@WrapperType(indent=2, comments=false)
	static final class AnItem extends Item
	{
		@WrapperInitial
		static final StringField field = new StringField().toFinal().optional().lengthMin(0);

		AnItem(final char field)
		{
			this(String.valueOf(field));
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.field.map(field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
