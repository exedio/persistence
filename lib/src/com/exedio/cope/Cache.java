package com.exedio.cope;

import bak.pcj.map.IntKeyMap;
import bak.pcj.map.IntKeyOpenHashMap;
import bak.pcj.set.IntSet;

final class Cache
{
	private final IntKeyMap[] stateMaps;
	
	Cache( int numberOfTypes )
	{
		stateMaps = new IntKeyMap[numberOfTypes];
		for ( int i=0; i<numberOfTypes; i++ )
		{
			stateMaps[i] = new IntKeyOpenHashMap();
		}
	}
	
	private IntKeyMap getStateMap( Type type )
	{
		return getStateMap( type.transientNumber );
	}
	
	private IntKeyMap getStateMap( int transientTypeNumber )
	{
		return stateMaps[ transientTypeNumber ];
	}
	
	PersistentState getPersistentState( final Transaction connectionSource, final Item item )
	{
		PersistentState state;
		IntKeyMap stateMap = getStateMap( item.type );
		synchronized (stateMap)
		{
			state = (PersistentState)stateMap.get( item.pk );
		}
		if ( state==null )
		{
			state = new PersistentState( connectionSource.getConnection(), item );
			Object oldValue;
			synchronized (stateMap)
			{
				oldValue = stateMap.put( item.pk, state );
			}
			if ( oldValue!=null )
			{
				System.out.println("warning: duplicate computation of state "+item.getCopeID());
			}
		}
		return state;
	}
	
	void invalidate( int transientTypeNumber, IntSet invalidatedPKs )
	{
		IntKeyMap stateMap = getStateMap( transientTypeNumber );
		synchronized ( stateMap )
		{
			stateMap.keySet().removeAll( invalidatedPKs );
		}
	}

	void clear()
	{
		for ( int i=0; i<stateMaps.length; i++ )
		{
			IntKeyMap stateMap = getStateMap( i );
			synchronized ( stateMap )
			{
				stateMap.clear();
			}
		}
	}
}
