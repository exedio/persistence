/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

final class Types
{
	private final Type<?>[] types;
	private final Type<?>[] concreteTypes;
	private final Type<?>[] typesSorted;
	final int concreteTypeCount;
	final List<Type<?>> typeList;
	final List<Type<?>> typeListSorted;
	final List<Type<?>> concreteTypeList;
	private final HashMap<String, Type> typesByID = new HashMap<String, Type>();
	private final HashMap<String, Feature> featuresByID = new HashMap<String, Feature>();
	
	Types(final Model model, final Type[] types)
	{
		if(types==null)
			throw new NullPointerException("types");
		if(types.length==0)
			throw new IllegalArgumentException("types must not be empty");
		
		final Type<?>[] explicitTypes = types;
		final Type<?>[] explicitTypesSorted = sort(explicitTypes);
		assert types.length==explicitTypesSorted.length;

		final ArrayList<Type<?>> typesL = new ArrayList<Type<?>>();
		for(final Type<?> type : explicitTypes)
			addTypeIncludingGenerated(type, typesL, 10);
		
		final ArrayList<Type<?>> concreteTypes = new ArrayList<Type<?>>();
		for(final Type<?> type : typesL)
		{
			final Type collisionType = typesByID.put(type.id, type);
			if(collisionType!=null)
				throw new IllegalArgumentException("duplicate type id \"" + type.id + "\" for classes " + collisionType.getJavaClass().getName() + " and " + type.getJavaClass().getName());
			if(!type.isAbstract)
				concreteTypes.add(type);
			
			for(final Feature feature : type.getDeclaredFeatures())
				if(featuresByID.put(feature.getID(), feature)!=null)
					throw new IllegalArgumentException("duplicate feature id \"" + feature.getID() + '"');
		}
		
		final ArrayList<Type<?>> typesSorted = new ArrayList<Type<?>>();
		for(final Type<?> type : explicitTypesSorted)
			addTypeIncludingGenerated(type, typesSorted, 10);

		final HashMap<Type, MountParameters> parametersMap = new HashMap<Type, MountParameters>();
		int concreteTypeCount = 0;
		int abstractTypeCount = -1;
		for(final Type<?> type : typesSorted)
			parametersMap.put(type, new MountParameters(type, type.isAbstract ? abstractTypeCount-- : concreteTypeCount++));
		for(final Type<?> type : typesSorted)
		{
			final Type supertype = type.getSupertype();
			if(supertype!=null)
				parametersMap.get(supertype).addSubtype(type);
		}
		for(final Type<?> type : typesSorted)
		{
			final MountParameters c = parametersMap.get(type);
			c.recurse(parametersMap, c, 10);
			for(final Field f : type.getDeclaredFields())
				if(f instanceof ItemField)
				{
					final ItemField ff = (ItemField)f;
					ff.resolveValueType();
					final Type valueType = ff.getValueType();
					parametersMap.get(valueType).addReference(ff);
				}
		}
		
		for(final Type<?> type : typesSorted)
			type.mount(model, parametersMap.get(type));
		
		this.types = typesL.toArray(new Type[typesL.size()]);
		this.typeList = Collections.unmodifiableList(typesL);
		this.concreteTypeCount = concreteTypeCount;
		this.concreteTypes = concreteTypes.toArray(new Type[concreteTypeCount]);
		this.concreteTypeList = Collections.unmodifiableList(Arrays.asList(this.concreteTypes));
		this.typesSorted = typesSorted.toArray(new Type[typesSorted.size()]);
		this.typeListSorted = Collections.unmodifiableList(Arrays.asList(this.typesSorted));
		
		assert this.concreteTypeCount==this.concreteTypes.length;
		assert this.concreteTypeCount==this.concreteTypeList.size();
		
		for(final Type<?> type : typesSorted)
			type.testActivation();
	}
	
	private static final Type<?>[] sort(final Type<?>[] types)
	{
		final HashSet<Type> typeSet = new HashSet<Type>(Arrays.asList(types));
		final HashSet<Type> done = new HashSet<Type>();
		//System.out.println(">--------------------"+Arrays.asList(types));

		final ArrayList<Type> result = new ArrayList<Type>();
		for(int i = 0; i<types.length; i++)
		{
			final ArrayList<Type> stack = new ArrayList<Type>();

			//System.out.println("------------------------------ "+types[i].getID());

			for(Type type = types[i]; type!=null; type=type.supertype)
			{
				//System.out.println("-------------------------------> "+type.getID());
				if(!typeSet.contains(type))
					throw new RuntimeException("type "+type.id+ " is supertype of " + types[i].id + " but not part of the model");
				stack.add(type);
			}
			
			for(ListIterator<Type> j = stack.listIterator(stack.size()); j.hasPrevious(); )
			{
				final Type type = j.previous();
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
			throw new RuntimeException(done.toString()+"<->"+typeSet.toString());
		
		//System.out.println("<--------------------"+result);
		return result.toArray(new Type[]{});
	}
	
	private static final void addTypeIncludingGenerated(final Type<?> type, final ArrayList<Type<?>> result, int hopCount)
	{
		hopCount--;
		if(hopCount<0)
			throw new RuntimeException();
		
		result.add(type);
		for(final Feature f : type.getDeclaredFeatures())
			if(f instanceof Pattern)
				for(final Type<?> generatedType : ((Pattern)f).getSourceTypes())
					addTypeIncludingGenerated(generatedType, result, hopCount);
	}
	
	static final class MountParameters
	{
		final Type type;
		final int idTransiently;
		private ArrayList<Type> subtypes;
		private ArrayList<Type> subtypesTransitively;
		private ArrayList<Type> typesOfInstances;
		private ArrayList<ItemField> references;
		
		MountParameters(final Type type, final int idTransiently)
		{
			this.type = type;
			this.idTransiently = idTransiently;
			assert (idTransiently<0) == type.isAbstract;
		}
		
		void addSubtype(final Type type)
		{
			if(subtypes==null)
				subtypes = new ArrayList<Type>();
			subtypes.add(type);
		}
		
		void addSubtypeTransitively(final Type type)
		{
			if(subtypesTransitively==null)
			{
				subtypesTransitively = new ArrayList<Type>();
				typesOfInstances = new ArrayList<Type>();
			}
			subtypesTransitively.add(type);
			if(!type.isAbstract)
				typesOfInstances.add(type);
		}
		
		void recurse(final HashMap<Type, MountParameters> parametersMap, final MountParameters target, int hopCount)
		{
			hopCount--;
			if(hopCount<0)
				throw new RuntimeException();
			
			target.addSubtypeTransitively(type);
			if(subtypes!=null)
				for(final Type type : subtypes)
					parametersMap.get(type).recurse(parametersMap, target, hopCount);
		}
		
		void addReference(final ItemField reference)
		{
			if(references==null)
				references = new ArrayList<ItemField>();
			references.add(reference);
		}
		
		List<Type> getSubtypes()
		{
			return finish(subtypes);
		}
		
		List<Type> getSubtypesTransitively()
		{
			return finish(subtypesTransitively);
		}
		
		List<Type> getTypesOfInstances()
		{
			return finish(typesOfInstances);
		}
		
		List<ItemField> getReferences()
		{
			return finish(references);
		}
		
		private static <X> List<X> finish(final ArrayList<X> list)
		{
			if(list==null)
				return Collections.<X>emptyList();
			else
			{
				switch(list.size())
				{
				case 0:
					throw new RuntimeException();
				case 1:
					return Collections.singletonList(list.get(0));
				default:
					list.trimToSize();
					return Collections.<X>unmodifiableList(list);
				}
			}
		}
	}
	
	boolean containsTypeSet(final Type... typeSet)
	{
		if(typeSet==null)
			throw new NullPointerException("typeSet");
		if(typeSet.length==0)
			throw new IllegalArgumentException("typeSet is empty");
		for(int i = 0; i<typeSet.length; i++)
			if(typeSet[i]==null)
				throw new NullPointerException("typeSet[" + i + ']');
		
		final HashSet<Type> typesAsSet = new HashSet<Type>(Arrays.asList(typesSorted));
		if(typesAsSet.containsAll(Arrays.asList(typeSet)))
			return true;
		
		for(final Type t : typeSet)
			if(typesAsSet.contains(t))
			{
				final StringBuilder bf = new StringBuilder("inconsistent type set: ");
				boolean first = true;
				for(final Type tx : typeSet)
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
	
	Map<Feature, Feature> getHiddenFeatures()
	{
		final HashMap<Feature, Feature> result = new HashMap<Feature, Feature>();
		for(final Type<?> t : types)
		{
			final Type st = t.getSupertype();
			if(st==null)
				continue;
			
			for(final Feature f : t.getDeclaredFeatures())
			{
				if(f instanceof This)
					continue;
				
				final Feature hidden = st.getFeature(f.getName());
				if(hidden!=null)
				{
					final Feature previous = result.put(f, hidden);
					assert previous==null;
				}
			}
		}
		return result;
	}
	
	Type getType(final String id)
	{
		return typesByID.get(id);
	}
	
	Feature getFeature(final String id)
	{
		return featuresByID.get(id);
	}
	
	Type getConcreteType(final int transientNumber)
	{
		return concreteTypes[transientNumber];
	}
	
	void connect(final Database db)
	{
		for(final Type type : typesSorted)
			type.connect(db);
	}
	
	void disconnect()
	{
		for(final Type type : typesSorted)
			type.disconnect();
	}
}
