package com.exedio.cope.pattern;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Settable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class InterfaceItemFieldHelper<I>
{
	private final String patternName;

	public InterfaceItemFieldHelper(final String patternName)
	{
		this.patternName = patternName;
	}

	@SuppressWarnings("unchecked")
	I get(final Settable<I> pattern, final Item item, final List<ItemField<? extends Item>> fields)
	{
		for(final ItemField<? extends Item> field : fields)
		{
			final Item value = field.get(item);
			if(value!=null)
				return (I)value;
		}

		if(pattern.isMandatory())
			throw new NullPointerException(patternName+pattern+" is mandatory but has no value set");
		else
			return null;
	}

	Set<Class<? extends Throwable>> getInitialExceptions(final Settable<I> settable)
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
		if(settable.isMandatory())
			result.add(MandatoryViolationException.class);
		return result;
	}

	ArrayList<ItemField<? extends Item>> checkClass(
			final boolean isFinal,
			final boolean unique,
			final Class<I> commonInterface,
			final Class<? extends Item>[] classes
			)
	{
		if(classes.length<=1)
		{
			throw new IllegalArgumentException("must use at least 2 classes");
		}
		final ArrayList<ItemField<? extends Item>> fields = new ArrayList<>();
		for(int i = 0; i<classes.length; i++)
		{
			final Class<? extends Item> type = classes[i];
			if(!commonInterface.isAssignableFrom(type))
			{
				throw new IllegalArgumentException("common interface >"+commonInterface+"< must be assignable from class >"
						+type+"<");
			}

			// don't allow different mixin classes to (potentially) share instances
			// because:
			// - unique constraints on ItemFields wouldn't work
			// - searching source would need to be adapted
			for(int j = 0; j<classes.length; j++)
			{
				if(i!=j&&classes[i].isAssignableFrom(classes[j]))
					throw new IllegalArgumentException("Classes must not be super-classes of each other: "+classes[i]
							+" is assignable from "+classes[j]);
			}

			ItemField<? extends Item> field = ItemField.create(type, ItemField.DeletePolicy.CASCADE).optional();
			if(isFinal)
				field = field.toFinal();
			if(unique)
				field = field.unique();
			fields.add(field);
		}
		return fields;
	}

	Condition buildXORCondition(final List<ItemField<? extends Item>> fields, final Settable<I> settable)
	{
		final List<Condition> ors = new ArrayList<>(fields.size());
		for(final ItemField<? extends Item> i : fields)
		{
			final List<Condition> ands = new ArrayList<>(fields.size());
			for(final ItemField<? extends Item> j : fields)
			{
				if(i==j)
				{
					if(settable.isMandatory())
					{
						ands.add(j.isNotNull());
					}
				}
				else
				{
					ands.add(j.isNull());
				}
			}
			ors.add(Cope.and(ands));
		}
		return Cope.or(ors);
	}
}
