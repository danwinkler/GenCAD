package com.danwink.gencad;
import java.util.ArrayList;

import com.danwink.gencad.Model.Branch;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class ControlPointScore
{
	ArrayList<Point3f> points = new ArrayList<Point3f>();
	
	public ControlPointScore()
	{
		
	}
	
	public void add( Point3f p )
	{
		points.add( p );
	}
	
	public float branchAngleDifference( Branch b, Branch parent )
	{
		float ad = 0;
		
		Vector3f pn = new Vector3f( b.p );
		if( parent != null )
		{
			pn.sub( parent.p );
			pn.normalize();
		}
		
		for( Branch c : b.children )
		{
			if( parent != null )
			{
				Vector3f bn = new Vector3f( c.p );
				bn.sub( b.p );
				bn.normalize();
				
				ad += Math.max( Math.pow( (Math.acos( bn.dot( pn ) )) * 10, 3 ), 0 );
			}
			
			ad += branchAngleDifference( c, b );
		}
		return ad;
	}
	
	public float score( Model model )
	{
		float tipDistanceScore = 0;
		float totalBranchLength = model.getTotalBranchLength();
		float bad = branchAngleDifference( model.root, null );
		
		ArrayList<Point3f> tips = model.getPoints();
		for( Point3f p : points )
		{
			float minDist = Float.MAX_VALUE;
			for( Point3f t : tips )
			{
				Vector3f d = new Vector3f( t );
				d.sub( p );
				float l = d.lengthSquared();
				if( l < minDist )
				{
					minDist = l;
				}
			}
			tipDistanceScore += minDist;//(float)Math.sqrt( minDist );
		}
		
		return tipDistanceScore*100 + totalBranchLength*1.0f / points.size() + bad * .2f;
	}
}
