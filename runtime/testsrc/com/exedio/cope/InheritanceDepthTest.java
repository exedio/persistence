package com.exedio.cope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

class InheritanceDepthTest
{
	@SuppressWarnings("unused") // OK: Model that is never connected
	private static final Model MODEL = Model.builder().
			name(InheritanceDepthTest.class).
			add(
					Grandparent.TYPE,
					Parent.TYPE,
					Child.TYPE
			).build();

	@Test
	void depth()
	{
		assertEquals(0, Grandparent.TYPE.inheritanceDepth);
		assertEquals(1, Parent.TYPE.inheritanceDepth);
		assertEquals(2, Child.TYPE.inheritanceDepth);
	}

	@WrapperType(indent=2, comments=false)
	private static class Grandparent extends Item
	{

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Grandparent()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Grandparent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Grandparent> TYPE = com.exedio.cope.TypesBound.newType(Grandparent.class,Grandparent::new);

		@com.exedio.cope.instrument.Generated
		protected Grandparent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class Parent extends Grandparent
	{

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Parent()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Parent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Parent> TYPE = com.exedio.cope.TypesBound.newType(Parent.class,Parent::new);

		@com.exedio.cope.instrument.Generated
		protected Parent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class Child extends Parent
	{

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Child()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected Child(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Child> TYPE = com.exedio.cope.TypesBound.newType(Child.class,Child::new);

		@com.exedio.cope.instrument.Generated
		protected Child(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
