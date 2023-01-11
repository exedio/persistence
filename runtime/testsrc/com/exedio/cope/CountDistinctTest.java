package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertContains;

import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

class CountDistinctTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(SimpleItem.TYPE);

	protected CountDistinctTest()
	{
		super(MODEL);
	}

	@Test
	void distinct()
	{
		new SimpleItem(3);
		new SimpleItem(3);
		new SimpleItem(5);
		assertContains(
				3, 5,
				new Query<>(SimpleItem.value.distinct()).search()
		);
		assertContains(
				2,
				new Query<>(SimpleItem.value.distinct().count()).search()
		);
	}

	@Test
	void grouped()
	{
		new SimpleItem(3).setGroup("a");
		new SimpleItem(3).setGroup("a");
		new SimpleItem(5).setGroup("b");
		new SimpleItem(7).setGroup("b");
		final Query<List<Object>> query = Query.newQuery(new Selectable<?>[]{SimpleItem.group, SimpleItem.value.distinct().count()}, SimpleItem.TYPE, null);
		query.setGroupBy(SimpleItem.group);
		assertContains(
				List.of("a", 1), List.of("b", 2),
				query.search()
		);
	}

	@WrapperType(comments=false, indent=2)
	static final class SimpleItem extends Item
	{
		static final IntegerField value = new IntegerField().optional().toFinal();

		static final StringField group = new StringField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		SimpleItem(
					@javax.annotation.Nullable final java.lang.Integer value)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				SimpleItem.value.map(value),
			});
		}

		@com.exedio.cope.instrument.Generated
		private SimpleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Integer getValue()
		{
			return SimpleItem.value.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getGroup()
		{
			return SimpleItem.group.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setGroup(@javax.annotation.Nullable final java.lang.String group)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			SimpleItem.group.set(this,group);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<SimpleItem> TYPE = com.exedio.cope.TypesBound.newType(SimpleItem.class,SimpleItem::new);

		@com.exedio.cope.instrument.Generated
		private SimpleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
