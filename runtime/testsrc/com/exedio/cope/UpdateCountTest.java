package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

class UpdateCountTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(Super.TYPE, MyItem.TYPE, OtherItem.TYPE);

	UpdateCountTest()
	{
		super(MODEL);
	}

	@Test
	void noIncrement()
	{
		final UpdateCount updateCount = UpdateCount.forStoredValue(MyItem.TYPE).set(MyItem.TYPE, 42).set(Super.TYPE, 42).build();
		final UpdateCount.Modifier modifier = updateCount.modifier();
		final UpdateCount next = modifier.nextUpdateCount();
		assertEquals(42, next.getValue(MyItem.TYPE));
		assertEquals(42, next.getValue(Super.TYPE));
	}

	@Test
	void next()
	{
		final UpdateCount updateCount = UpdateCount.forStoredValue(MyItem.TYPE).set(MyItem.TYPE, 42).set(Super.TYPE, 42).build();
		assertEquals(42, updateCount.getValue(MyItem.TYPE));
		assertEquals(42, updateCount.getValue(Super.TYPE));
		assertEquals(false, updateCount.isInitial());
		assertEquals(42, updateCount.modifier().nextUpdateCount().getValue(MyItem.TYPE));
		assertEquals(42, updateCount.modifier().nextUpdateCount().getValue(Super.TYPE));

		final UpdateCount.Modifier modifier = updateCount.modifier();
		assertEquals(43, modifier.nextValue(MyItem.TYPE));
		assertEquals(43, modifier.nextValue(MyItem.TYPE));
		final UpdateCount next = modifier.nextUpdateCount();
		assertEquals(43, next.getValue(MyItem.TYPE));
		assertEquals(42, next.getValue(Super.TYPE));
	}

	@Test
	void overflow()
	{
		final UpdateCount updateCount = UpdateCount.forStoredValue(MyItem.TYPE).set(MyItem.TYPE, Integer.MAX_VALUE).set(Super.TYPE, Integer.MAX_VALUE).build();
		assertEquals(Integer.MAX_VALUE, updateCount.getValue(MyItem.TYPE));
		assertEquals(false, updateCount.isInitial());
		assertEquals(Integer.MAX_VALUE, updateCount.modifier().nextUpdateCount().getValue(MyItem.TYPE));

		final UpdateCount.Modifier modifier = updateCount.modifier();
		assertEquals(0, modifier.nextValue(MyItem.TYPE));
		final UpdateCount next = modifier.nextUpdateCount();
		assertEquals(0, next.getValue(MyItem.TYPE));
		assertEquals(Integer.MAX_VALUE, next.getValue(Super.TYPE));
	}

	@Test
	void initial()
	{
		final UpdateCount initial = UpdateCount.initial(MyItem.TYPE);
		assertEquals(true, initial.isInitial());
		assertFails(
				() -> initial.getValue(MyItem.TYPE),
				RuntimeException.class,
				"initial"
		);
		assertFails(
				() -> initial.modifier().nextUpdateCount().getValue(MyItem.TYPE),
				RuntimeException.class,
				"initial"
		);

		final UpdateCount.Modifier modifier = initial.modifier();
		assertEquals(0, modifier.nextValue(MyItem.TYPE));
		assertEquals(0, modifier.nextUpdateCount().getValue(MyItem.TYPE));
	}

	@Test
	void type()
	{
		final UpdateCount superCount = UpdateCount.forStoredValue(Super.TYPE).set(Super.TYPE, 0).build();
		assertEquals(0, superCount.getValue(Super.TYPE));
		assertFails(
				() -> superCount.getValue(MyItem.TYPE),
				IllegalArgumentException.class,
				"unexpected request for "+MyItem.TYPE+" in UpdateCount for "+Super.TYPE
		);

		final UpdateCount myCount = UpdateCount.forStoredValue(MyItem.TYPE).set(MyItem.TYPE, 0).set(Super.TYPE, 0).build();
		assertEquals(0, myCount.getValue(Super.TYPE));
		assertEquals(0, myCount.getValue(MyItem.TYPE));
		assertFails(
				() -> myCount.getValue(OtherItem.TYPE),
				IllegalArgumentException.class,
				"unexpected request for "+OtherItem.TYPE+" in UpdateCount for "+MyItem.TYPE
		);
	}

	@WrapperType(indent=2, comments=false, constructor=NONE, genericConstructor=NONE)
	static class Super extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused")
		static final IntegerField s = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Super> TYPE = com.exedio.cope.TypesBound.newType(Super.class,Super::new);

		@com.exedio.cope.instrument.Generated
		protected Super(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false, constructor=NONE, genericConstructor=NONE)
	static class MyItem extends Super
	{
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused")
		static final IntegerField m = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false, constructor=NONE, genericConstructor=NONE)
	static class OtherItem extends Super
	{
		@Wrapper(wrap="*", visibility=NONE)
		@SuppressWarnings("unused")
		static final IntegerField o = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<OtherItem> TYPE = com.exedio.cope.TypesBound.newType(OtherItem.class,OtherItem::new);

		@com.exedio.cope.instrument.Generated
		protected OtherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
