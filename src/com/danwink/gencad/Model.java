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
		Point3f p;
		Vector3f v;
		ArrayList<Branch> children = new ArrayList<Branch>();
	}
	
	private void getTipsInternal( Branch b, ArrayList<Point3f> tips )
	{
		if( b.children.size() == 0 )
		{
			Point3f end = new Point3f( b.p );
			end.add( b.v );
			tips.add( end );
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
		for( Branch c : b.children )
		{ 
			l += getTotalBranchLength( c );
		}
		float tl = b.v.lengthSquared();
		return tl + l;// + 1f;
	}
		
	public float getTotalBranchLength()
	{
		return getTotalBranchLength( root );
	}
	
	
	float mutAmount = .01f;
	private void mutInternalBranch( Branch o, Branch n )
	{
		n.v = new Vector3f( o.v );
		n.v.x += DMath.randomf( -mutAmount, mutAmount );
		n.v.y += DMath.randomf( -mutAmount, mutAmount );
		n.v.z += DMath.randomf( -mutAmount, mutAmount );
		
		for( Branch ob : o.children )
		{
			//Occasionally remove branches
			if( Math.random() < .01 )
			{
				continue;
			}
			
			Branch b = new Branch();
			b.p = new Point3f( n.p );
			b.p.add( n.v );
			b.v = new Vector3f( ob.v );
			
			b.v.x += DMath.randomf( -mutAmount, mutAmount );
			b.v.y += DMath.randomf( -mutAmount, mutAmount );
			b.v.z += DMath.randomf( -mutAmount, mutAmount );
			
			n.children.add( b );
			
			mutInternalBranch( ob, b );
		}
		
		//Occasionally add branch
		if( Math.random() < .01 )
		{
			Branch b = new Branch();
			b.p = new Point3f( n.p );
			b.p.add( n.v );
			b.v = new Vector3f();
			
			b.v.x = DMath.randomf( -1, 1 ) * mutAmount * 3;
			b.v.y = DMath.randomf( -1, 1 ) * mutAmount * 3;
			b.v.z = DMath.randomf( -1, 1 ) * mutAmount * 3;
			
			n.children.add( b );
		}
	}
	
	public Model mutate()
	{
		Model copy = new Model();
		
		copy.root = new Branch();
		copy.root.p = root.p;
		
		mutInternalBranch( root, copy.root );
		
		return copy;
	}
}
