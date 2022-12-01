/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import gnu.trove.TLongHashSet;
import gnu.trove.TLongIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Types
{
	private static final Type<?>[] EMPTY_TYPE_ARRAY = new Type<?>[0];

	private final Type<?>[] typesSorted;
	private final Type<?>[] typesByCacheIdTransiently;
	final int concreteTypeCount;
	final List<Type<?>> typeList;
	final List<Type<?>> typeListSorted;
	final List<Type<?>> concreteTypes;
	private final HashMap<String, Type<?>> typesByID = new HashMap<>();
	private final HashMap<String, Feature> featuresByID = new HashMap<>();

	Types(final Model model, final TypeSet[] typeSets, final Type<?>[] typesWithoutSets)
	{
		final Type<?>[] explicitTypes = unify(typeSets, typesWithoutSets);
		TypeSet.check(explicitTypes);
		for(final Type<?> type : explicitTypes)
			type.assertNotMounted();

		final Type<?>[] explicitTypesSorted = sort(explicitTypes);
		assert explicitTypes.length==explicitTypesSorted.length;

		final ArrayList<Type<?>> typesL = new ArrayList<>();
		for(final Type<?> type : explicitTypes)
			addTypeIncludingSourceTypes(type, typesL, 10);

		for(final Type<?> type : typesL)
			type.assertNotMounted();

		final ArrayList<Type<?>> concreteTypes = new ArrayList<>();
		for(final Type<?> type : typesL)
		{
			final Type<?> collisionType = typesByID.putIfAbsent(type.id, type);
			if(collisionType!=null)
				throw new IllegalArgumentException("duplicate type id \"" + type.id + "\" for classes " + collisionType.getJavaClass().getName() + " and " + type.getJavaClass().getName());
			if(!type.isAbstract)
				concreteTypes.add(type);

			for(final Feature feature : type.getDeclaredFeatures())
				if(featuresByID.putIfAbsent(feature.getID(), feature)!=null)
					throw new IllegalArgumentException("duplicate feature id \"" + feature.getID() + '"');
		}

		final ArrayList<Type<?>> typesSorted = new ArrayList<>();
		for(final Type<?> type : explicitTypesSorted)
			addTypeIncludingSourceTypes(type, typesSorted, 10);

		for(final Type<?> type : typesSorted)
			type.testActivation();

		final HashMap<Type<?>, MountParameters> parametersMap = new HashMap<>();
		int typeCount = 0;
		int concreteTypeCount = 0;
		int abstractTypeCount = -1;
		for(final Type<?> type : typesSorted)
			parametersMap.put(type, new MountParameters(type, typeCount++, type.isAbstract ? abstractTypeCount-- : concreteTypeCount++));
		for(final Type<?> type : typesSorted)
		{
			final Type<?> supertype = type.getSupertype();
			if(supertype!=null)
				parametersMap.get(supertype).addSubtype(type);
		}
		for(final Type<?> type : typesSorted)
		{
			final MountParameters c = parametersMap.get(type);
			c.recurse(parametersMap, c, 10);
			for(final Field<?> f : type.getDeclaredFields())
				if(f instanceof ItemField<?>)
				{
					final ItemField<?> ff = (ItemField<?>)f;
					ff.resolveValueType(parametersMap.keySet());
					final Type<?> valueType = ff.getValueType();
					parametersMap.get(valueType).addReference(ff);
				}
		}

		for(final Type<?> type : typesSorted)
			type.mount(model, parametersMap.get(type));

		for(final Type<?> type : typesSorted)
		{
			for(final CopyConstraint constraint : type.getDeclaredCopyConstraints())
			{
				constraint.resolveTemplate();
			}
		}

		this.typesByCacheIdTransiently = new Type<?>[concreteTypeCount];
		{
			int cacheIdTransiently = 0;
			for(final Type<?> type : typesSorted)
			{
				if(!type.isAbstract)
				{
					assert
						type.cacheIdTransiently==cacheIdTransiently :
						String.valueOf(type.cacheIdTransiently) + '/' + type.id + '/' + cacheIdTransiently;
					typesByCacheIdTransiently[cacheIdTransiently++] = type;
				}
			}
			assert cacheIdTransiently==typesByCacheIdTransiently.length;
		}


		this.typeList = Collections.unmodifiableList(typesL);
		this.concreteTypeCount = concreteTypeCount;
		this.concreteTypes = Collections.unmodifiableList(concreteTypes);
		this.typesSorted = typesSorted.toArray(EMPTY_TYPE_ARRAY);
		this.typeListSorted = List.of(this.typesSorted);

		assert this.concreteTypeCount==this.concreteTypes.size();
	}

	private static Type<?>[] unify(final TypeSet[] typeSets, final Type<?>[] typesWithoutSets)
	{
		if(typeSets==null && typesWithoutSets==null)
			return null;

		if(typeSets==null)
			return typesWithoutSets;

		final ArrayList<Type<?>> result = new ArrayList<>();
		for(final TypeSet typeSet : typeSets)
			typeSet.addTo(result);
		if(typesWithoutSets!=null)
			result.addAll(Arrays.asList(typesWithoutSets));

		return result.toArray(EMPTY_TYPE_ARRAY);
	}

	private static Type<?>[] sort(final Type<?>[] types)
	{
		final HashSet<Type<?>> typeSet = new HashSet<>(Arrays.asList(types));
		final HashSet<Type<?>> done = new HashSet<>();
		//System.out.println(">--------------------"+Arrays.asList(types));

		final ArrayList<Type<?>> result = new ArrayList<>();
		for(final Type<?> type2 : types)
		{
			final ArrayList<Type<?>> stack = new ArrayList<>();

			//System.out.println("------------------------------ "+types[i].getID());

			for(Type<?> type = type2; type!=null; type=type.supertype)
			{
				//System.out.println("-------------------------------> "+type.getID());
				if(!typeSet.contains(type))
					throw new RuntimeException("type "+type.id+ " is supertype of " + type2.id + " but not part of the model");
				stack.add(type);
			}

			for(final ListIterator<Type<?>> j = stack.listIterator(stack.size()); j.hasPrevious(); )
			{
				final Type<?> type = j.previous();
				//System.out.println("-------------------------------) "+type.getID());

				if(!done.contains(type))
				{
					//System.out.println("-------------------------------] "+type.getID());
					result.add(type);
					done.add(type);
				}
			}
		}
		if(!done.equals(typeSet))
			throw new RuntimeException(done+"<->"+typeSet);

		//System.out.println("<--------------------"+result);
		return result.toArray(EMPTY_TYPE_ARRAY);
	}

	private static void addTypeIncludingSourceTypes(
			final Type<?> type,
			final ArrayList<Type<?>> result,
			int hopCount)
	{
		hopCount--;
		if(hopCount<0)
			throw new RuntimeException();

		result.add(type);
		for(final Feature f : type.getDeclaredFeatures())
			if(f instanceof Pattern)
				for(final Type<?> sourceType : ((Pattern)f).getSourceTypes())
					addTypeIncludingSourceTypes(sourceType, result, hopCount);
	}

	static final class MountParameters
	{
		final Type<?> type;
		final int orderIdTransiently;
		final int cacheIdTransiently;
		private ArrayList<Type<?>> subtypes;
		private ArrayList<Type<?>> subtypesTransitively;
		private ArrayList<Type<?>> typesOfInstances;
		private ArrayList<ItemField<?>> references;

		MountParameters(final Type<?> type, final int orderIdTransiently, final int cacheIdTransiently)
		{
			this.type = type;
			this.orderIdTransiently = orderIdTransiently;
			this.cacheIdTransiently = cacheIdTransiently;
			assert (cacheIdTransiently<0) == type.isAbstract;
		}

		void addSubtype(final Type<?> type)
		{
			if(subtypes==null)
				subtypes = new ArrayList<>();
			subtypes.add(type);
		}

		void addSubtypeTransitively(final Type<?> type)
		{
			if(subtypesTransitively==null)
			{
				subtypesTransitively = new ArrayList<>();
				typesOfInstances = new ArrayList<>();
			}
			subtypesTransitively.add(type);
			if(!type.isAbstract)
				typesOfInstances.add(type);
		}

		void recurse(final HashMap<Type<?>, MountParameters> parametersMap, final MountParameters target, int hopCount)
		{
			hopCount--;
			if(hopCount<0)
				throw new RuntimeException();

			target.addSubtypeTransitively(type);
			if(subtypes!=null)
				for(final Type<?> type : subtypes)
					parametersMap.get(type).recurse(parametersMap, target, hopCount);
		}

		void addReference(final ItemField<?> reference)
		{
			if(references==null)
				references = new ArrayList<>();
			references.add(reference);
		}

		List<Type<?>> getSubtypes()
		{
			return finish(subtypes);
		}

		List<Type<?>> getSubtypesTransitively()
		{
			return finish(subtypesTransitively);
		}

		List<Type<?>> getTypesOfInstances()
		{
			return finish(typesOfInstances);
		}

		List<ItemField<?>> getReferences()
		{
			return finish(references);
		}

		private static <X> List<X> finish(final ArrayList<X> list)
		{
			if(list==null)
				return List.of();
			assert !list.isEmpty();
			return List.copyOf(list);
		}
	}

	boolean containsTypeSet(final Type<?>... typeSet)
	{
		if(typeSet==null)
			throw new NullPointerException("typeSet");
		if(typeSet.length==0)
			throw new IllegalArgumentException("typeSet is empty");
		for(int i = 0; i<typeSet.length; i++)
			if(typeSet[i]==null)
				throw new NullPointerException("typeSet[" + i + ']');

		final HashSet<Type<?>> typesAsSet = new HashSet<>(Arrays.asList(typesSorted));
		if(typesAsSet.containsAll(Arrays.asList(typeSet)))
			return true;

		for(final Type<?> t : typeSet)
			if(typesAsSet.contains(t))
			{
				final StringBuilder bf = new StringBuilder("inconsistent type set: ");
				boolean first = true;
				for(final Type<?> tx : typeSet)
				{
					if(first)
						first = false;
					else
						bf.append(", ");

					final boolean n = typesAsSet.contains(tx);
					if(n)
						bf.append('[');
					bf.append(tx.id);
					if(n)
						bf.append(']');
				}

				throw new IllegalArgumentException(bf.toString());
			}

		return false;
	}

	Type<?> getType(final String id)
	{
		return typesByID.get(id);
	}

	Feature getFeature(final String id)
	{
		return featuresByID.get(id);
	}

	private Type<?> getConcreteType(final int transientNumber)
	{
		final Type<?> result = typesByCacheIdTransiently[transientNumber];
		assert result.cacheIdTransiently==transientNumber : String.valueOf(result.cacheIdTransiently) + '/' + result.id + '/' + transientNumber;
		return result;
	}

	Item getItem(final String id) throws NoSuchIDException
	{
		final int pos = id.lastIndexOf(Item.ID_SEPARATOR);
		if(pos<=0)
			throw new NoSuchIDException(id, true, "no separator '" + Item.ID_SEPARATOR + "' in id");

		final String typeID = id.substring(0, pos);
		final Type<?> type = getType(typeID);
		if(type==null)
			throw new NoSuchIDException(id, true, "type <" + typeID + "> does not exist");
		if(type.isAbstract)
			throw new NoSuchIDException(id, true, "type is abstract");

		final String pkString = id.substring(pos+1);
		if(pkString.length()>1)
		{
			switch(pkString.charAt(0))
			{
				case '+':
					throw new NoSuchIDException(id, true, "has plus sign");
				case '0':
					throw new NoSuchIDException(id, true, "has leading zeros");
			}
		}

		final long pkLong;
		try
		{
			pkLong = Long.parseLong(pkString);
		}
		catch(final NumberFormatException e)
		{
			throw new NoSuchIDException(id, e, pkString);
		}

		if(pkLong<0)
			throw new NoSuchIDException(id, true, "must be positive");
		if(pkLong>type.createLimit)
			throw new NoSuchIDException(id, true, "must be less or equal " + type.createLimit);

		final Item result = type.getItemObject(pkLong);
		if(!result.existsCopeItem())
			throw new NoSuchIDException(id, false, "item <" + pkLong + "> does not exist");
		return result;
	}

	@SuppressWarnings("OverlyStrongTypeCast")
	void checkTypeColumns()
	{
		for(final Type<?> t : typesSorted)
		{
			checkTypeColumn(t.thisFunction);
			for(final Field<?> a : t.getDeclaredFields())
				if(a instanceof ItemField<?>)
					checkTypeColumn((ItemField<?>)a);
		}
	}

	private static void checkTypeColumn(final ItemFunction<?> f)
	{
		if(f.needsCheckTypeColumn())
		{
			final long count = f.checkTypeColumnL();
			if(count!=0)
				throw new RuntimeException("wrong type column for " + f + " on " + count + " tuples.");
		}
	}

	void afterModelCreated()
	{
		for(final Type<?> type : typesSorted)
			for(final Feature feature : type.getDeclaredFeatures())
				feature.afterModelCreated();

		for(final Type<?> type : typesSorted)
			for(final Feature feature : type.getDeclaredFeatures())
			{
				final Collection<String> suspicions = feature.getSuspicions();
				if(suspicions.isEmpty())
					continue;

				final Logger logger = LoggerFactory.getLogger(feature.getClass());
				if(!logger.isErrorEnabled())
					continue;

				final String featureID = feature.getID();
				for(final String suspicion : feature.getSuspicions())
					logger.error("{}: {}", featureID, suspicion);
			}
	}

	void connect(final Database db, final ModelMetrics metrics)
	{
		for(final Type<?> type : typesSorted)
			type.connect(db, metrics);
	}

	void disconnect()
	{
		for(final Type<?> type : typesSorted)
			type.disconnect();
	}

	Item[] activate(final TLongHashSet[] invalidations)
	{
		int length = 0;
		for(final TLongHashSet set : invalidations)
		{
			if(set!=null)
				length += set.size();
		}

		final Item[] result = new Item[length];
		int item = 0;
		for(int type = 0; type<invalidations.length; type++)
		{
			final TLongHashSet set = invalidations[type];
			if(set!=null)
			{
				final Type<?> typeO = getConcreteType(type);
				for(final TLongIterator i = set.iterator(); i.hasNext(); )
					result[item++] = typeO.activate(i.next());
			}
		}
		assert item==length;
		return result;
	}

	void unsetKnownToBeEmptyForTest(final TLongHashSet[] invalidations)
	{
		for(int typeIndex = 0; typeIndex<invalidations.length; typeIndex++)
		{
			if(invalidations[typeIndex]!=null)
			{
				for(Type<?> type = getConcreteType(typeIndex); type!=null; type = type.getSupertype())
				{
					type.table.knownToBeEmptyForTest = false;
				}
			}
		}
	}
}
