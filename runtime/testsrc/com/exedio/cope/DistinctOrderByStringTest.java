package com.exedio.cope;

import static com.exedio.cope.DistinctOrderByStringTest.MyItem.TYPE;
import static com.exedio.cope.DistinctOrderByStringTest.MyItem.ints;
import static com.exedio.cope.DistinctOrderByStringTest.MyItem.intsParent;
import static com.exedio.cope.DistinctOrderByStringTest.MyItem.text;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.SetField;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class DistinctOrderByStringTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	MyItem a, b;

	DistinctOrderByStringTest()
	{
		super(MODEL);
	}

	@BeforeEach void setUp()
	{
		b = new MyItem("B");
		b.addToInts(1);
		a = new MyItem("a");
		a.addToInts(1);
		a.addToInts(2);
		new MyItem("c").addToInts(3);
	}

	@Test void orderByString()
	{
		final Query<MyItem> query = createQuery();
		query.addOrderBy(text);
		// postgresql on debian (but not MacOS) orders case-insensitive:
		// https://dba.stackexchange.com/questions/106964/why-is-my-postgresql-order-by-case-insensitive
		assertEquals(
				postgresql ? List.of(a, b) : List.of(b, a),
				query.search()
		);
	}

	@Test void orderByStringUpper()
	{
		final Query<MyItem> query = createQuery();
		query.addOrderBy(text.toUpperCase());
		final EnvironmentInfo env = model.getEnvironmentInfo();
		switch(dialect)
		{
			case hsqldb:
				notAllowed(query, s -> s.startsWith("invalid ORDER BY expression"));
				break;
			case mysql:
				if(env.isDatabaseVersionAtLeast(5, 7))
					notAllowed(query, s -> s.contains("ORDER BY clause is not in SELECT list"));
				else
					assertEquals(List.of(a, b), query.search());
				break;
			case postgresql:
				notAllowed(query, s -> s.contains("SELECT DISTINCT, ORDER BY expressions"));
				break;
			default:
				throw new RuntimeException(dialect.name());
		}
	}

	@Nonnull
	private static Query<MyItem> createQuery()
	{
		final Query<MyItem> query = TYPE.newQuery();
		query.join(ints.getRelationType(), intsParent().equalTarget());
		query.narrow(ints.getElement().in(1, 2));
		query.setDistinct(true);
		return query;
	}

	@WrapperType(indent=2, comments=false)
	static class MyItem extends Item
	{
		static final StringField text = new StringField();

		static final SetField<Integer> ints = SetField.create(new IntegerField());

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nonnull final java.lang.String text)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.text,text),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.lang.String getText()
		{
			return MyItem.text.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setText(@javax.annotation.Nonnull final java.lang.String text)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.text.set(this,text);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.util.Set<Integer> getInts()
		{
			return MyItem.ints.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final com.exedio.cope.Query<Integer> getIntsQuery()
		{
			return MyItem.ints.getQuery(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final java.util.List<MyItem> getParentsOfInts(@javax.annotation.Nonnull final Integer element)
		{
			return MyItem.ints.getParents(MyItem.class,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setInts(@javax.annotation.Nonnull final java.util.Collection<? extends Integer> ints)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			MyItem.ints.set(this,ints);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean addToInts(@javax.annotation.Nonnull final Integer element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return MyItem.ints.add(this,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean removeFromInts(@javax.annotation.Nonnull final Integer element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return MyItem.ints.remove(this,element);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final com.exedio.cope.ItemField<MyItem> intsParent()
		{
			return MyItem.ints.getParent(MyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
