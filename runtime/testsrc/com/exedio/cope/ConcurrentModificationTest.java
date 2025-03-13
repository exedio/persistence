package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.dsmf.SQLRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ConcurrentModificationTest extends TestWithEnvironment
{
	static final Model MODEL = new ModelBuilder().
			name(ConcurrentModificationTest.class).
			add(
					Parent.TYPE,
					Child.TYPE
			).build();

	ConcurrentModificationTest()
	{
		super(MODEL);
	}

	private static String upToColon(final String s)
	{
		final int index = s.indexOf(':');
		return index==-1 ? s : s.substring(0, index);
	}

	private void assertCollision(final Executable r)
	{
		if (hsqldb || postgresql)
			assertFails(
					r,
					SQLRuntimeException.class,
					"UPDATE ",
					s -> s.substring(0, 7)
			);
		else
			assertFails(
					r,
					TemporaryTransactionException.class,
					"expected one row, but got 0 on statement",
					ConcurrentModificationTest::upToColon
			);
	}

	@Test
	void bothModifyChildField()
	{
		final Child item = createChildModifiedInOtherTx();
		assertEquals(0, item.getChildField());
		assertCollision( () -> item.setChildField(2) );
		model.rollback();
		model.startTransaction("tx");
		assertEquals(1, item.getChildField());
	}

	@Test
	void oneChildOneParent()
	{
		final Child item = createChildModifiedInOtherTx();
		assertEquals(0, item.getChildField());
		if (model.getConnectProperties().storeOnlyModifiedColumns)
		{
			item.setParentField(2);

			restartTransaction();
			assertEquals(1, item.getChildField());
			assertEquals(2, item.getParentField());

			assertEquals(1, Child.oneMustBeZero.check());
		}
		else
		{
			assertCollision(() -> item.setParentField(2));

			model.rollback();
			model.startTransaction("tx");
			assertEquals(1, item.getChildField());
			assertEquals(0, item.getParentField());

			assertEquals(0, Child.oneMustBeZero.check());
		}
	}

	@Test
	void childIntAndData()
	{
		final Child item = createChildModifiedInOtherTx();
		assertEquals(0, item.getChildField());
		if (hsqldb || postgresql || allVault())
		{
			assertCollision(() -> item.setChildData(new byte[]{4, 2}));
			model.rollback();
			model.startTransaction("tx");
			assertEquals(1, item.getChildField());
			assertEquals(null, item.getChildDataArray());
		}
		else
		{
			item.setChildData(new byte[]{4, 2});
			restartTransaction();

			assertEquals(
					model.getConnectProperties().storeOnlyModifiedColumns ? 1 : 0, // '0' is dubious - a committed tx changed the value to '1'
					item.getChildField()
			);
			assertArrayEquals(new byte[]{4, 2}, item.getChildDataArray());
		}
	}

	private boolean allVault()
	{
		final VaultProperties vaultProperties = model.getConnectProperties().getVaultProperties();
		return vaultProperties!=null && vaultProperties.isAppliedToAllFields();
	}

	private Child createChildModifiedInOtherTx()
	{
		final Child item = new Child();
		restartTransaction();

		assertEquals(0, item.getChildField());
		final Transaction otherTx = model.leaveTransaction();

		model.startTransaction("txB");
		item.setChildField(1);
		assertEquals(1, item.getChildField());
		commit();

		model.joinTransaction(otherTx);
		return item;
	}

	@WrapperType(indent=2, comments=false)
	private abstract static class Parent extends Item
	{
		static final IntegerField parentField = new IntegerField().defaultTo(0);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Parent()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Parent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final int getParentField()
		{
			return Parent.parentField.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setParentField(final int parentField)
		{
			Parent.parentField.set(this,parentField);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Parent> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(Parent.class);

		@com.exedio.cope.instrument.Generated
		protected Parent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class Child extends Parent
	{
		static final IntegerField childField = new IntegerField().defaultTo(0);

		static final DataField childData = new DataField().optional();

		static final CheckConstraint oneMustBeZero = new CheckConstraint(Cope.or(
				childField.is(0),
				parentField.is(0)
		));

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Child()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Child(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final int getChildField()
		{
			return Child.childField.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setChildField(final int childField)
		{
			Child.childField.set(this,childField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean isChildDataNull()
		{
			return Child.childData.isNull(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final long getChildDataLength()
		{
			return Child.childData.getLength(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final byte[] getChildDataArray()
		{
			return Child.childData.getArray(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getChildData(@javax.annotation.Nonnull final java.io.OutputStream childData)
				throws
					java.io.IOException
		{
			Child.childData.get(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getChildData(@javax.annotation.Nonnull final java.nio.file.Path childData)
				throws
					java.io.IOException
		{
			Child.childData.get(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		final void getChildData(@javax.annotation.Nonnull final java.io.File childData)
				throws
					java.io.IOException
		{
			Child.childData.get(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setChildData(@javax.annotation.Nullable final com.exedio.cope.DataField.Value childData)
		{
			Child.childData.set(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setChildData(@javax.annotation.Nullable final byte[] childData)
		{
			Child.childData.set(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setChildData(@javax.annotation.Nullable final java.io.InputStream childData)
				throws
					java.io.IOException
		{
			Child.childData.set(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setChildData(@javax.annotation.Nullable final java.nio.file.Path childData)
				throws
					java.io.IOException
		{
			Child.childData.set(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		final void setChildData(@javax.annotation.Nullable final java.io.File childData)
				throws
					java.io.IOException
		{
			Child.childData.set(this,childData);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Child> TYPE = com.exedio.cope.TypesBound.newType(Child.class,Child::new);

		@com.exedio.cope.instrument.Generated
		protected Child(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
