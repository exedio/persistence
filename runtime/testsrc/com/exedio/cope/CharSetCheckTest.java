package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.CharSet;
import com.exedio.dsmf.Constraint;
import org.junit.jupiter.api.Test;

class CharSetCheckTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(MyItem.TYPE);

	CharSetCheckTest()
	{
		super(MODEL);
	}

	@Test
	void createAndCheck()
	{
		final String smilingFaceWithSmilingEyes = "\ud83d\ude0a";
		new MyItem(smilingFaceWithSmilingEyes);
		final Constraint csConstraint = MODEL.getSchema().getTable(SchemaInfo.getTableName(MyItem.TYPE)).getConstraint("MyItem_field_CS");
		MODEL.commit();
		if(mysql)
		{
			//TODO should always be 0
			assertEquals(atLeastMysql8() ? 1 : 0, csConstraint.checkL(), csConstraint::getName);
		}
		else
		{
			assertEquals(null, csConstraint);
		}
	}

	@WrapperType(indent=2, comments=false)
	private static class MyItem extends Item
	{
		@WrapInterim
		@SuppressWarnings("HardcodedLineSeparator")
		private static final CharSet CHARSET_INCL_SUPPLEMENTARY_NO_CONTROLS_EXCEPT_NEWLINE_AND_TAB = new CharSet(
				'\t', '\n', '\r', '\r', ' ', (char)0xFFFF
		);

		@Wrapper(wrap="*", visibility=NONE)
		private static final StringField field = new StringField().charSet(CHARSET_INCL_SUPPLEMENTARY_NO_CONTROLS_EXCEPT_NEWLINE_AND_TAB);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringCharSetViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
