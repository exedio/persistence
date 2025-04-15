package com.exedio.cope.pattern;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImporterMultiTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MyItem.TYPE);

	ImporterMultiTest()
	{
		super(MODEL);
	}

	@Test
	void invalidValues()
	{
		assertFails(
				()->MyItem.byAllThree.doImportMultipleKeys(MyItem.class, List.of(), "code", 1, false, "toomany"),
				RuntimeException.class,
				"3-4"
		);
		assertFails(
				()->MyItem.byAllThree.doImportMultipleKeys(MyItem.class, List.of(), "code", 1),
				RuntimeException.class,
				"3-2"
		);
		assertFails(
				()->MyItem.byAllThree.doImportMultipleKeys(MyItem.class, List.of(), "code", 1, "wrongtype"),
				ClassCastException.class,
				"Cannot cast java.lang.String to java.lang.Boolean"
		);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	void invalidCall()
	{
		final Importer byAllThreeRaw = MyItem.byAllThree;
		assertFails(
				()->byAllThreeRaw.doImport(MyItem.class, new Object()),
				RuntimeException.class,
				"3-1"
		);
		assertFails(
				MyItem.byAllThree::getKey,
				IllegalStateException.class,
				"not supported for Importer on compound unique index "+MyItem.allThree
		);
		assertEquals(MyItem.allThree, MyItem.byAllThree.getUniqueConstraint());
	}

	@Test
	void test()
	{
		doTest(false);
	}

	@Test
	void testInitial()
	{
		doTest(true);
	}

	private static void doTest(final boolean initial)
	{
		MyItem.byAllThree.setHintInitialExperimental(initial);
		final MyItem a1f = MyItem.importByAllThree(
				List.of(SetValue.map(MyItem.data, "d1")),
				"a", 1, false
		);
		assertEquals("a", a1f.getFirst());
		assertEquals(1, a1f.getSecond());
		assertEquals(false, a1f.getThird());
		assertEquals("d1", a1f.getData());

		assertEquals(
				a1f,
				MyItem.importByAllThree(
						List.of(SetValue.map(MyItem.data, "d2")),
						"a", 1, false
				)
		);
		assertEquals("d2", a1f.getData());

		assertEquals(
				a1f,
				MyItem.importByAllThree(
						List.of(),
						"a", 1, false
				)
		);
		assertEquals("d2", a1f.getData());

		assertEquals(
				a1f,
				MyItem.importByAllThree(
						List.of(SetValue.map(MyItem.data, null)),
						"a", 1, false
				)
		);
		assertEquals(null, a1f.getData());
	}

	@WrapperType(indent=2, constructor=NONE)
	static final class MyItem extends Item
	{
		static final StringField first = new StringField().toFinal();

		static final IntegerField second = new IntegerField().toFinal();

		static final BooleanField third = new BooleanField().toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		static final UniqueConstraint allThree = UniqueConstraint.create(first, second, third);

		static final Importer<?> byAllThree = Importer.create(allThree);

		static final StringField data = new StringField().optional();

		/**
		 * Creates a new MyItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #first}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getFirst()
		{
			return MyItem.first.get(this);
		}

		/**
		 * Returns the value of {@link #second}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getSecond()
		{
			return MyItem.second.getMandatory(this);
		}

		/**
		 * Returns the value of {@link #third}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean getThird()
		{
			return MyItem.third.getMandatory(this);
		}

		/**
		 * Import {@link #byAllThree}.
		 * @return the imported item
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="import")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem importByAllThree(@javax.annotation.Nonnull final java.util.List<com.exedio.cope.SetValue<?>> setValues,@javax.annotation.Nonnull final java.lang.String first,final int second,final boolean third)
		{
			return MyItem.byAllThree.doImportMultipleKeys(MyItem.class,setValues,first,second,third);
		}

		/**
		 * Returns the value of {@link #data}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getData()
		{
			return MyItem.data.get(this);
		}

		/**
		 * Sets a new value for {@link #data}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setData(@javax.annotation.Nullable final java.lang.String data)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.data.set(this,data);
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
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
