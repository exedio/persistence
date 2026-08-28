package com.exedio.cope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PkAssignmentTest extends TestWithEnvironment
{
	static final Model modelA = new ModelBuilder().name("model a").add(ItemA.TYPE).build();
	static final Model modelB = new ModelBuilder().name("model b").add(ItemB.TYPE).build();

	PkAssignmentTest()
	{
		super(modelA);
		copeRule.omitTransaction();
	}

	@BeforeEach
	void connectModelB()
	{
		modelB.connect(model.getConnectProperties());
	}

	@Test
	void parallel()
	{
		assumeTrue(
				model.getConnectProperties().primaryKeyGenerator.persistent,
				"This test simulates two nodes - can't work unless persistent.");
		// simulating creation of items on two cluster nodes, represented by ItemA.TYPE and ItemB.TYPE:
		assertPk(0, 0, ItemA.TYPE);
		assertPk(1, 64, ItemB.TYPE);
		assertPk(2, 1, ItemA.TYPE);
		assertPk(3, 65, ItemB.TYPE);
		assertPk(4, 2, ItemA.TYPE);
		assertPk(5, 66, ItemB.TYPE);
		for (int i=6; i<SequenceImplBatchedSequence.BATCH_SIZE+2; i++)
			assertPk(i, i-3, ItemA.TYPE);
		assertPk(66, 63, ItemA.TYPE);
		assertPk(67, 128, ItemA.TYPE);
		assertPk(68, 67, ItemB.TYPE);
		modelB.disconnect();
		modelB.connect(model.getConnectProperties());
		assertPk(69, 192, ItemB.TYPE);
	}

	private void assertPk(final int expectedStandard, final int expectedBatched, final Type<?> type)
	{
		if (model.getConnectProperties().primaryKeyGenerator == PrimaryKeyGenerator.batchedSequence)
		{
			assertEquals(expectedBatched, getPkInTx(type));
		}
		else
		{
			assertEquals(expectedStandard, getPkInTx(type));
		}
	}

	private static long getPkInTx(final Type<?> type)
	{
		try (final TransactionTry tx = type.getModel().startTransactionTry("tx "+PkAssignmentTest.class.getName()))
		{
			return SchemaInfo.columnValue(
					tx.commit(type.newItem()));
		}
	}

	@WrapperType(indent=2, comments=false)
	@CopeName("ItemAB")
	static class ItemA extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		ItemA()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected ItemA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class,ItemA::new);

		@com.exedio.cope.instrument.Generated
		protected ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	@CopeName("ItemAB")
	static class ItemB extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		ItemB()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected ItemB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class,ItemB::new);

		@com.exedio.cope.instrument.Generated
		protected ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
