package com.exedio.cope;

import static com.exedio.cope.StringRegexpLikeTest.AnItem.TYPE;
import static com.exedio.cope.StringRegexpLikeTest.AnItem.field;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

public class StringRegexpLikeTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public StringRegexpLikeTest()
	{
		super(MODEL);
	}

	@Test void testCheckOk()
	{
		field.check("abc123");
	}

	@Test void testCheckFail()
	{
		final StringRegexpPatternViolationException e = assertFails(
				() -> field.check("abc"),
				StringRegexpPatternViolationException.class,
				"regexp pattern violation, " +
				"'abc' does not match (?s)\\A[a-z]+.*[0-9]+\\z for "+field);
		assertEquals(field, e.getFeature());
		assertEquals(null, e.getItem());
		assertEquals("abc", e.getValue());
	}
	@Test void testCheckUnsupportedConstraints()
	{
		commit();
		model.checkUnsupportedConstraints();
		startTransaction();
	}

	@Test void testNull()
	{
		new AnItem("null", null);
	}

	@Test void testEmpty()
	{
		assertFails(() -> new AnItem("empty", ""), StringRegexpPatternViolationException.class, "regexp pattern violation, '' does not match (?s)\\A[a-z]+.*[0-9]+\\z for AnItem.field");
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testLinebreaks()
	{
		new AnItem("lf", "a\n0");
		new AnItem("cr", "a\r0");
		new AnItem("crlf", "a\r\n0");

		assertFails(() -> new AnItem("lf-end","b1\n"), StringRegexpPatternViolationException.class, "regexp pattern violation, 'b1\n' does not match (?s)\\A[a-z]+.*[0-9]+\\z for AnItem.field");
	}

	@Test void testNot()
	{
		final RegexpLikeCondition condition = new RegexpLikeCondition(field, "[a-z]{0,4}0");
		assertSame(field, condition.getFunction());
		assertEquals("[a-z]{0,4}0", condition.getRegexp());
		final Condition conditionNot = condition.not();
		final AnItem itemTrue  = new AnItem("true",  "abcd0"); // pattern does match
		final AnItem itemFalse = new AnItem("false", "abc#0"); // pattern does not match
		final AnItem itemNull  = new AnItem("null",  null);
		final AnItem itemSubstring = new AnItem("end", "aabcd0"); //pattern matches only end
		final AnItem itemSubstring2 = new AnItem("start", "abcd01"); //pattern matches only start

		assertEquals(true,  condition.get(itemTrue));
		assertEquals(false, condition.get(itemFalse));
		assertEquals(false, condition.get(itemNull));
		assertEquals(false, condition.get(itemSubstring));
		assertEquals(false, condition.get(itemSubstring2));

		assertEquals(false, conditionNot.get(itemTrue));
		assertEquals(true,  conditionNot.get(itemFalse));
		assertEquals(false, conditionNot.get(itemNull));
		assertEquals(true,  conditionNot.get(itemSubstring));
		assertEquals(true,  conditionNot.get(itemSubstring2));

		assertEquals(asList(itemTrue ), TYPE.search(condition,    TYPE.getThis(), true));
		assertEquals(asList(itemFalse, itemSubstring, itemSubstring2), TYPE.search(conditionNot, TYPE.getThis(), true));
	}

	@Test void testNullPattern()
	{
		new StringField().regexp(null);
	}

	@Test void testEmptyPattern()
	{
		assertFails(() -> new StringField().regexp(""), IllegalArgumentException.class, "pattern must be null or non-empty");
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testInvalidPattern()
	{
		assertFails(() -> new StringField().regexp("[A-Z"), PatternSyntaxException.class, "Illegal/unsupported escape sequence near index 11\n(?s)\\A[A-Z\\z\n           ^");
	}

	@WrapperType(indent=2, comments=false, type=PACKAGE)
	static final class AnItem extends Item
	{
		static final StringField code = new StringField().unique();
		@WrapperInitial
		static final StringField field = new StringField().toFinal().optional().lengthMin(0).regexp("[a-z]+.*[0-9]+");

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem(
					@javax.annotation.Nonnull final java.lang.String code,
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.code,code),
				com.exedio.cope.SetValue.map(AnItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getCode()
		{
			return AnItem.code.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setCode(@javax.annotation.Nonnull final java.lang.String code)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			AnItem.code.set(this,code);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static AnItem forCode(@javax.annotation.Nonnull final java.lang.String code)
		{
			return AnItem.code.searchUnique(AnItem.class,code);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static AnItem forCodeStrict(@javax.annotation.Nonnull final java.lang.String code)
				throws
					java.lang.IllegalArgumentException
		{
			return AnItem.code.searchUniqueStrict(AnItem.class,code);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
