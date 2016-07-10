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
	
	public float branchAngleDifference( Branch b )
	{
		float ad = 0;
		
		Vector3f bn = new Vector3f( b.v );
		bn.normalize();
		for( Branch c : b.children )
		{
			Vector3f cn = new Vector3f( c.v );
			cn.normalize();
			
			ad += Math.pow( (Math.acos( bn.dot( cn ) ) - (Math.PI * .05f)) * 10, 3 );
			
			ad += branchAngleDifference( c );
		}
		return ad;
	}
	
	public float score( Model model )
	{
		float tipDistanceScore = 0;
		float totalBranchLength = 0;
		float bad = branchAngleDifference( model.root );
		
		ArrayList<Point3f> tips = model.getTips();
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
			tipDistanceScore += minDist; //(float)Math.sqrt( minDist );
		}
		
		totalBranchLength += model.getTotalBranchLength();
		
		
		return tipDistanceScore*100 + totalBranchLength + bad * .1f;
	}
}