package com.exedio.cope.lib;


final class PrimaryKeyIterator
{
	private final Table table;
	
	PrimaryKeyIterator(final Table table)
	{
		this.table = table;
	}

	private int nextPkLo = Type.NOT_A_PK;
	private int nextPkHi = Type.NOT_A_PK;
	private boolean nextIsLo;
	
	void flushPK()
	{
		nextPkLo = Type.NOT_A_PK;
		nextPkHi = Type.NOT_A_PK;
	}

	int nextPK()
	{
		if(nextPkLo==Type.NOT_A_PK)
		{
			final int[] nextPks = table.database.getNextPK(table);
			if(nextPks.length!=2)
				throw new RuntimeException(String.valueOf(nextPks.length));
			nextPkLo = nextPks[0];
			nextPkHi = nextPks[1];
			if(nextPkLo>=nextPkHi)
				throw new RuntimeException(String.valueOf(nextPkLo)+">="+String.valueOf(nextPkHi));
			nextIsLo = (-nextPkLo)<=nextPkHi;
			//System.out.println(this.trimmedName+": getNextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
		}
		
		//System.out.println(this.trimmedName+": nextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
		final int result = nextIsLo ? nextPkLo-- : nextPkHi++;
		nextIsLo = !nextIsLo;

		if(nextPkLo>=nextPkHi) // TODO : somehow handle pk overflow
			throw new RuntimeException(String.valueOf(nextPkHi)+String.valueOf(nextPkLo));
		return result;
	}

	long pk2id(final int pk)
	{
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException("not a pk");

		final long longPk = (long)pk;
		return
			(pk>=0) ?
				(longPk<<1) : // 2*pk
				-((longPk<<1)|1l); // -(2*pk + 1)
	}

	int id2pk(final long id)
			throws NoSuchIDException
	{
		if(id<0)
			throw new NoSuchIDException(id, "must be positive");
		if(id>=4294967296l)
			throw new NoSuchIDException(id, "does not fit in 32 bit");

		final long result =
			((id&1l)>0) ? // odd id ?
				-((id>>>1)+1l) : // -(id/2 +1)
				id>>1; // id/2

		//System.out.println("id2pk: "+id+" -> "+result);
		if(result==(long)Type.NOT_A_PK)
			throw new NoSuchIDException(id, "is a NOT_A_PK");

		return (int)result;
	}

}
