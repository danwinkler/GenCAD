package com.danwink.gencad;
import java.util.ArrayList;

import com.phyloa.dlib.util.DMath;

import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;

public class Model
{
	Branch root;
	float score;
	
	public static class Branch
	{
		boolean isRoot = false;
		Point3f p;
		ArrayList<Branch> children = new ArrayList<Branch>();
	}
	
	private void getTipsInternal( Branch b, ArrayList<Point3f> tips )
	{
		if( b.children.size() == 0 )
		{
			tips.add( b.p );
		}
		else
		{
			for( Branch c : b.children )
			{
				getTipsInternal( c, tips );
			}
		}
	}
	
	public ArrayList<Point3f> getTips()
	{
		ArrayList<Point3f> tips = new ArrayList<Point3f>();
		tips.add( root.p );
		
		getTipsInternal( root, tips );
		
		return tips;
	}
	
	private float getTotalBranchLength( Branch b )
	{
		float l = 0;
		Vector3f v = new Vector3f();
		for( Branch c : b.children )
		{ 
			v.set( b.p );
			v.sub( c.p );
			
			l += v.lengthSquared();
			
			l += getTotalBranchLength( c );
		}
		return l;
	}
		
	public float getTotalBranchLength()
	{
		return getTotalBranchLength( root );
	}
	
	
	float mutAmount = .05f;
	private void mutInternalBranch( Branch o, Branch n )
	{
		n.p = new Point3f( o.p );
		if( !n.isRoot ) 
		{
			n.p.x += DMath.randomf( -1, 1 ) * mutAmount;
			n.p.y += DMath.randomf( -1, 1 ) * mutAmount;
			n.p.z += DMath.randomf( -1, 1 ) * mutAmount;
		}
		
		for( Branch ob : o.children )
		{
			//Occasionally remove branches
			if( Math.random() < .01 )
			{
				continue;
			}
			
			Branch b = new Branch();
			b.p = new Point3f( n.p );
			
			b.p.x += DMath.randomf( -1, 1 ) * mutAmount;
			b.p.y += DMath.randomf( -1, 1 ) * mutAmount;
			b.p.z += DMath.randomf( -1, 1 ) * mutAmount;
			
			n.children.add( b );
			
			mutInternalBranch( ob, b );
		}
		
		//Occasionally add branch
		if( Math.random() < .01 )
		{
			Branch b = new Branch();
			b.p = new Point3f( n.p );
			b.p.x += DMath.randomf( -1, 1 ) * mutAmount * 3;
			b.p.y += DMath.randomf( -1, 1 ) * mutAmount * 3;
			b.p.z += DMath.randomf( -1, 1 ) * mutAmount * 3;
			
			n.children.add( b );
		}
	}
	
	public Model mutate()
	{
		Model copy = new Model();
		
		copy.root = new Branch();
		copy.root.p = root.p;
		copy.root.isRoot = true;
		
		mutInternalBranch( root, copy.root );
		
		return copy;
	}
}
