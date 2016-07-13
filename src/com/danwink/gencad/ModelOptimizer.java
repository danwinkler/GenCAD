package com.danwink.gencad;

import java.util.ArrayList;

import com.danwink.gencad.Model.Branch;

import jp.objectclub.vecmath.Point3f;

public class ModelOptimizer
{	
	public static void optimize( Model m, ControlPointScore score )
	{
		ArrayList<Branch> branches = m.getBranches();
		
		Branch target = branches.get( (int)(Math.random()*branches.size()) );
		
		if( target == m.root ) return;
		
		Point3f orig = new Point3f( target.p );
		
		Point3f bestPoint = orig;
		float bestScore = score.score( m );
		
		for( int x = -1; x <= 1; x += 2 )
		{
			for( int y = -1; y <= 1; y += 2 )
			{
				for( int z = -1; z <= 1; z += 2 )
				{
					target.p.x = orig.x + (x*m.mutAmount);
					target.p.y = orig.y + (y*m.mutAmount);
					target.p.z = orig.z + (z*m.mutAmount);
					
					float s = score.score( m );
					if( s < bestScore )
					{
						bestPoint.set( target.p );
					}
				}
			}
		}
		
		target.p.set( bestPoint );
	}
}
