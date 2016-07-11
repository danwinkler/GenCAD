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
		boolean inserted = false;
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
	
	private void getPointsInternal( Branch b, ArrayList<Point3f> points )
	{
		points.add( b.p );
	
		for( Branch c : b.children )
		{
			getPointsInternal( c, points );
		}
	}
	
	public ArrayList<Point3f> getPoints()
	{
		ArrayList<Point3f> points = new ArrayList<Point3f>();
		points.add( root.p );
		
		getPointsInternal( root, points );
		
		return points;
	}
	
	private void getBranchesInternal( Branch b, ArrayList<Branch> branches )
	{
		branches.add( b );
	
		for( Branch c : b.children )
		{
			getBranchesInternal( c, branches );
		}
	}
	
	public ArrayList<Branch> getBranches()
	{
		ArrayList<Branch> branches = new ArrayList<>();
		branches.add( root );
		
		getBranchesInternal( root, branches );
		
		return branches;
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
		//Randomly split branch into two
		if( false && o.children.size() > 0 && Math.random() < .05 )
		{
			Branch mid = new Branch();
			mid.inserted = true;
			mid.p = new Point3f();
			mid.p.add( n.p );
			mid.p.add( o.children.get( 0 ).p );
			mid.p.scale( .5f );
			
			mid.p.x += DMath.randomf( -1, 1 ) * mutAmount * 10;
			mid.p.y += DMath.randomf( -1, 1 ) * mutAmount * 10;
			mid.p.z += DMath.randomf( -1, 1 ) * mutAmount * 10;
			
			n.children.add( mid );
			
			for( Branch ob : o.children )
			{
				Branch b = new Branch();
				b.inserted = ob.inserted;
				b.p = new Point3f( ob.p );
				
				mid.children.add( b );
				
				mutInternalBranch( ob, b );
			}
			return;
		}
		
		for( Branch ob : o.children )
		{
			//Occasionally remove branches if they are tips
			if( ob.children.size() == 0 && Math.random() < .02 )
			{
				continue;
			}
			
			Branch b = new Branch();
			b.inserted = ob.inserted;
			b.p = new Point3f( ob.p );
			
			if( Math.random() < .01 )
			{
				b.p.x += DMath.randomf( -1, 1 ) * mutAmount;
				b.p.y += DMath.randomf( -1, 1 ) * mutAmount;
				b.p.z += DMath.randomf( -1, 1 ) * mutAmount;
			}
			
			n.children.add( b );
			
			mutInternalBranch( ob, b );
		}
		
		//Occasionally add branch
		double branchChance = n.children.size() == 0 ? .04 : .02;
		if( Math.random() < branchChance )
		{
			Branch b = new Branch();
			b.p = new Point3f( n.p );
			b.p.x += DMath.randomf( -1, 1 ) * mutAmount * 20;
			b.p.y += DMath.randomf( -1, 1 ) * mutAmount * 20;
			b.p.z += DMath.randomf( -1, 1 ) * mutAmount * 20;
			
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
	
	private void copyInternalBranch( Branch o, Branch n )
	{
		for( Branch ob : o.children )
		{
			Branch b = new Branch();
			b.p = new Point3f( ob.p );
			n.children.add( b );
			
			copyInternalBranch( ob, b );
		}
	}
	
	public Model copy()
	{
		Model copy = new Model();
		
		copy.root = new Branch();
		copy.root.p = root.p;
		copy.root.isRoot = true;
		
		copyInternalBranch( root, copy.root );
		
		return copy;
	}
}
