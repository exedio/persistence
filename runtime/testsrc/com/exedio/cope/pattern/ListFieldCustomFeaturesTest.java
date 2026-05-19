package com.exedio.cope.pattern;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.CheckViolationException;
import com.exedio.cope.Condition;
import com.exedio.cope.CopeIgnore;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.SetValueUtil;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListFieldCustomFeaturesTest extends TestWithEnvironment
{
	private static final Model MODEL = Model.builder().
			add(MyItem.TYPE).
			changeHooks(AutoFillHook::new).
			build();

	private static int counter = 42;

	ListFieldCustomFeaturesTest()
	{
		super(MODEL);
	}

	@Test
	void model()
	{
		assertEquals(
				List.of(
						MyItem.TYPE,
						MyItem.stringsWithCustom.getEntryType(),
						((ListField<?>)MyItem.stringsWithCustom.getEntryType().getDeclaredFeature("customList")).getEntryType(),
						MyItem.stringsWithMandatoryCustom.getEntryType(),
						MyItem.stringsWithCounter.getEntryType(),
						MyItem.blockA.of(MyBlock.inBlock).getEntryType(),
						((ListField<?>) MyItem.blockA.of(MyBlock.inBlock).getEntryType().getDeclaredFeature("customListInBlock")).getEntryType(),
						MyItem.blockB.of(MyBlock.inBlock).getEntryType(),
						((ListField<?>) MyItem.blockB.of(MyBlock.inBlock).getEntryType().getDeclaredFeature("customListInBlock")).getEntryType()
				),
				MODEL.getTypes()
		);
	}

	@Test
	void mandatoryCustom()
	{
		final MyItem item = new MyItem();
		assertFails(
				()->item.addToStringsWithMandatoryCustom("fail"),
				MandatoryViolationException.class,
				"mandatory violation for MyItem-stringsWithMandatoryCustom.mandatoryCustomField"
		);
	}

	@Test
	void withCounter()
	{
		final MyItem item = new MyItem();
		item.addToStringsWithCounter("now");
		final Item entry = MyItem.stringsWithCounter.getEntryType().searchSingletonStrict(Condition.ofTrue());
		assertEquals(43, entry.get(MyItem.counter));

		item.setStringsWithCounter(List.of("now2"));
		assertEquals(44, entry.get(MyItem.counter));

		assertFails(
				()->item.setStringsWithCounter(List.of("now3")),
				CheckViolationException.class,
				"check violation on MyItem-stringsWithCounter-0 for MyItem-stringsWithCounter.counterCheck"
		);
		assertEquals(List.of("now2"), item.getStringsWithCounter());
		assertEquals(44, entry.get(MyItem.counter));
	}

	@Test
	void customDate()
	{
		final MyItem item = new MyItem();
		item.addToStringsWithCustom("fail");
		final Item entry = MyItem.stringsWithCustom.getEntryType().searchSingletonStrict(Condition.ofTrue());
		assertEquals(null, entry.get(MyItem.customDate));

		final Date date = new Date();
		entry.set(MyItem.customDate, date);
		assertEquals(date, entry.get(MyItem.customDate));
	}

	private static class AutoFillHook implements ChangeHook
	{
		AutoFillHook(@SuppressWarnings("unused") final Model model)
		{
		}

		@Override
		public SetValue<?>[] beforeNew(final Type<?> type, final SetValue<?>[] setValues)
		{
			return addCounterValue(type, setValues);
		}

		@Override
		public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] setValues)
		{
			return addCounterValue(item.getCopeType(), setValues);
		}

		private static SetValue<?>[] addCounterValue(final Type<?> type, final SetValue<?>[] setValues)
		{
			if (type == MyItem.stringsWithCounter.getEntryType())
				return SetValueUtil.add(setValues, SetValue.map(MyItem.counter, ++counter));
			else
				return setValues;
		}
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyBlock extends Block
	{
		private static final ListField<String> inBlock = ListField.create(new StringField()).
				customEntryFeature("customListInBlock", ListField.create(new IntegerField()));

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		private java.util.List<String> getInBlock()
		{
			return field().of(MyBlock.inBlock).get(item());
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		private com.exedio.cope.Query<String> getInBlockQuery()
		{
			return field().of(MyBlock.inBlock).getQuery(item());
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void addToInBlock(@javax.annotation.Nonnull final String inBlock)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			field().of(MyBlock.inBlock).add(item(),inBlock);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private boolean removeAllFromInBlock(@javax.annotation.Nonnull final String inBlock)
		{
			return field().of(MyBlock.inBlock).removeAll(item(),inBlock);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void setInBlock(@javax.annotation.Nonnull final java.util.Collection<? extends String> inBlock)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			field().of(MyBlock.inBlock).set(item(),inBlock);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.pattern.BlockType<MyBlock> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlock.class,MyBlock::new);

		@com.exedio.cope.instrument.Generated
		private MyBlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class MyItem extends Item
	{
		@CopeIgnore
		@WrapInterim
		private static final DateField customDate = new DateField().optional();

		@CopeIgnore
		@WrapInterim
		private static final IntegerField counter = new IntegerField();

		static final ListField<String> stringsWithCustom = ListField.create(new StringField()).
				customEntryFeature("customDate", customDate).
				customEntryFeature("customList", ListField.create(new IntegerField()));

		static final ListField<String> stringsWithMandatoryCustom = ListField.create(new StringField()).
				customEntryFeature("mandatoryCustomField", new IntegerField());

		static final ListField<String> stringsWithCounter = ListField.create(new StringField()).
				customEntryFeature("counter", counter).
				customEntryFeature("counterCheck", new CheckConstraint(counter.isNot(45)));

		static final BlockField<MyBlock> blockA = BlockField.create(MyBlock.TYPE);

		static final BlockField<MyBlock> blockB = BlockField.create(MyBlock.TYPE);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					final int counter)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.counter,counter),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		private java.util.Date getCustomDate()
		{
			return MyItem.customDate.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void setCustomDate(@javax.annotation.Nullable final java.util.Date customDate)
		{
			MyItem.customDate.set(this,customDate);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void touchCustomDate()
		{
			MyItem.customDate.touch(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private int getCounter()
		{
			return MyItem.counter.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void setCounter(final int counter)
		{
			MyItem.counter.set(this,counter);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.List<String> getStringsWithCustom()
		{
			return MyItem.stringsWithCustom.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final com.exedio.cope.Query<String> getStringsWithCustomQuery()
		{
			return MyItem.stringsWithCustom.getQuery(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final java.util.List<MyItem> getDistinctParentsOfStringsWithCustom(final String element)
		{
			return MyItem.stringsWithCustom.getDistinctParents(MyItem.class,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void addToStringsWithCustom(@javax.annotation.Nonnull final String stringsWithCustom)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			MyItem.stringsWithCustom.add(this,stringsWithCustom);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean removeAllFromStringsWithCustom(@javax.annotation.Nonnull final String stringsWithCustom)
		{
			return MyItem.stringsWithCustom.removeAll(this,stringsWithCustom);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setStringsWithCustom(@javax.annotation.Nonnull final java.util.Collection<? extends String> stringsWithCustom)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			MyItem.stringsWithCustom.set(this,stringsWithCustom);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MyItem> stringsWithCustomParent()
		{
			return MyItem.stringsWithCustom.getParent(MyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.List<String> getStringsWithMandatoryCustom()
		{
			return MyItem.stringsWithMandatoryCustom.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final com.exedio.cope.Query<String> getStringsWithMandatoryCustomQuery()
		{
			return MyItem.stringsWithMandatoryCustom.getQuery(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final java.util.List<MyItem> getDistinctParentsOfStringsWithMandatoryCustom(final String element)
		{
			return MyItem.stringsWithMandatoryCustom.getDistinctParents(MyItem.class,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void addToStringsWithMandatoryCustom(@javax.annotation.Nonnull final String stringsWithMandatoryCustom)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			MyItem.stringsWithMandatoryCustom.add(this,stringsWithMandatoryCustom);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean removeAllFromStringsWithMandatoryCustom(@javax.annotation.Nonnull final String stringsWithMandatoryCustom)
		{
			return MyItem.stringsWithMandatoryCustom.removeAll(this,stringsWithMandatoryCustom);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setStringsWithMandatoryCustom(@javax.annotation.Nonnull final java.util.Collection<? extends String> stringsWithMandatoryCustom)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			MyItem.stringsWithMandatoryCustom.set(this,stringsWithMandatoryCustom);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MyItem> stringsWithMandatoryCustomParent()
		{
			return MyItem.stringsWithMandatoryCustom.getParent(MyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.List<String> getStringsWithCounter()
		{
			return MyItem.stringsWithCounter.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final com.exedio.cope.Query<String> getStringsWithCounterQuery()
		{
			return MyItem.stringsWithCounter.getQuery(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final java.util.List<MyItem> getDistinctParentsOfStringsWithCounter(final String element)
		{
			return MyItem.stringsWithCounter.getDistinctParents(MyItem.class,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void addToStringsWithCounter(@javax.annotation.Nonnull final String stringsWithCounter)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			MyItem.stringsWithCounter.add(this,stringsWithCounter);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean removeAllFromStringsWithCounter(@javax.annotation.Nonnull final String stringsWithCounter)
		{
			return MyItem.stringsWithCounter.removeAll(this,stringsWithCounter);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setStringsWithCounter(@javax.annotation.Nonnull final java.util.Collection<? extends String> stringsWithCounter)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					java.lang.ClassCastException
		{
			MyItem.stringsWithCounter.set(this,stringsWithCounter);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MyItem> stringsWithCounterParent()
		{
			return MyItem.stringsWithCounter.getParent(MyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final MyBlock blockA()
		{
			return MyItem.blockA.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final MyBlock blockB()
		{
			return MyItem.blockB.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
