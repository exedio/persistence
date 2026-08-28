package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.SQLRuntimeException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class PkOverflowTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(
			StandardItem.TYPE,
			LongItem.TYPE
	);

	public PkOverflowTest()
	{
		super(MODEL);
	}

	@Test
	void intPk() throws SQLException
	{
		final PrimaryKeyGenerator generator = model.getConnectProperties().primaryKeyGenerator;
		assumeFalse(
				generator==PrimaryKeyGenerator.batchedSequence,
				"SequenceImplBatchedSequence does not assign the last batch of values"
		);
		IntegerTypeTest.setPkTo(StandardItem.TYPE, Integer.MAX_VALUE-2);
		assertEquals("StandardItem-2147483646", new StandardItem().getCopeID());
		if (generator.persistent && dialect==RuntimeTester.Dialect.mysql)
		{
			assertFailsWithCause(
					StandardItem::new,
					SQLRuntimeException.class,
					"INSERT `StandardItem_this_Seq` VALUES()",
					SQLIntegrityConstraintViolationException.class,
					"Duplicate entry '2147483647' for key 'StandardItem_this_Seq.PRIMARY'"
			);
		}
		else
		{
			assertEquals("StandardItem-2147483647", new StandardItem().getCopeID());
			if(generator==PrimaryKeyGenerator.memory)
			{
				assertFails(
						StandardItem::new,
						IllegalStateException.class,
						"sequence overflow to 2147483648 in StandardItem.this limited to 0,2147483647");
			}
			else if(dialect==RuntimeTester.Dialect.postgresql)
			{
				assertFailsWithCause(
						StandardItem::new,
						SQLRuntimeException.class,
						"SELECT NEXTVAL('\"StandardItem_this_Seq\"')",
						SQLException.class,
						"reached maximum value of sequence \"StandardItem_this_Seq\" (2147483647)");
			}
			else if(dialect==RuntimeTester.Dialect.hsqldb)
			{
				assertFailsWithCause(
						StandardItem::new,
						SQLRuntimeException.class,
						"SELECT NEXT VALUE FOR \"StandardItem_this_Seq\" FROM \"hsqldb_temp_table_for_sequences\"",
						SQLDataException.class,
						"data exception: sequence generator limit exceeded");
			}
			else
			{
				throw new RuntimeException();
			}
		}
	}

	@Test
	void longPk() throws SQLException
	{
		final PrimaryKeyGenerator generator = model.getConnectProperties().primaryKeyGenerator;
		assumeFalse(
				generator==PrimaryKeyGenerator.batchedSequence,
				"SequenceImplBatchedSequence does not assign the last batch of values"
		);
		IntegerTypeTest.setPkTo(LongItem.TYPE, Long.MAX_VALUE-2);
		assertEquals("LongItem-9223372036854775806", new LongItem().getCopeID());
		if (generator.persistent && dialect==RuntimeTester.Dialect.mysql)
		{
			assertFailsWithCause(
					LongItem::new,
					SQLRuntimeException.class,
					"INSERT `LongItem_this_Seq` VALUES()",
					SQLIntegrityConstraintViolationException.class,
					"Duplicate entry '9223372036854775807' for key 'LongItem_this_Seq.PRIMARY'");
		}
		else
		{
			assertEquals("LongItem-9223372036854775807", new LongItem().getCopeID());
			if(!generator.persistent)
			{
				assertEquals(model, LongItem.TYPE.getModel());
				assertFails(
						LongItem::new,
						IllegalStateException.class,
						"sequence overflow to -9223372036854775808 in LongItem.this limited to 0,9223372036854775807");
			}
			else if(dialect==RuntimeTester.Dialect.postgresql)
			{
				assertFailsWithCause(
						LongItem::new,
						SQLRuntimeException.class,
						"SELECT NEXTVAL('\"LongItem_this_Seq\"')",
						SQLException.class,
						"reached maximum value of sequence \"LongItem_this_Seq\" (9223372036854775807)");
			}
			else if(dialect==RuntimeTester.Dialect.hsqldb)
			{
				assertFailsWithCause(
						LongItem::new,
						SQLRuntimeException.class,
						"SELECT NEXT VALUE FOR \"LongItem_this_Seq\" FROM \"hsqldb_temp_table_for_sequences\"",
						SQLDataException.class,
						"data exception: sequence generator limit exceeded");
			}
			else
			{
				throw new RuntimeException();
			}
		}
	}

	public static <T extends Throwable> void assertFailsWithCause(
			final Executable executable,
			final Class<T> expectedType,
			final String expectedMessage,
			final Class<? extends Throwable> expectedCauseType,
			final String expectedCauseMessageEnd)
	{
		final Throwable cause = assertFails(executable, expectedType, expectedMessage).getCause();
		assertAll(
				() -> assertTrue(expectedCauseType.isInstance(cause), "found "+cause.getClass()),
				() -> assertTrue(cause.getMessage().endsWith(expectedCauseMessageEnd), cause.getMessage())
		);
	}

	@WrapperType(indent=2, comments=false)
	static final class StandardItem extends Item
	{

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		StandardItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private StandardItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<StandardItem> TYPE = com.exedio.cope.TypesBound.newType(StandardItem.class,StandardItem::new);

		@com.exedio.cope.instrument.Generated
		private StandardItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	@CopeCreateLimit(Long.MAX_VALUE)
	static final class LongItem extends Item
	{

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		LongItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private LongItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<LongItem> TYPE = com.exedio.cope.TypesBound.newType(LongItem.class,LongItem::new);

		@com.exedio.cope.instrument.Generated
		private LongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

}
