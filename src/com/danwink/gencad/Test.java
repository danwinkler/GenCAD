package com.danwink.gencad;
import java.util.ArrayList;
import java.util.Collections;

import com.danwink.gencad.Model.Branch;
import com.danwink.surfbuilder.MarchingSolver;
import com.danwink.surfbuilder.Preset;
import com.danwink.surfbuilder.Primitive;
import com.danwink.surfbuilder.Triangle;
import com.danwink.surfbuilder.fields.FieldBuilder;
import com.danwink.surfbuilder.fields.FieldBuilder.Field;
import com.danwink.surfbuilder.fields.MaxFieldBuilder;
import com.danwink.surfbuilder.polygonize.MarchingCubePolygonizer;
import com.danwink.surfbuilder.fields.SumFieldBuilder;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import jp.objectclub.vecmath.Point3f;
import jp.objectclub.vecmath.Vector3f;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PShape;

public class Test extends PApplet implements ControlListener
{
	PShape shape;
	
	PeasyCam cam;
	ControlP5 cp5;
	
	ArrayList<Model> models = new ArrayList<Model>();
	
	ControlPointScore score = new ControlPointScore();
	
	boolean optimizing = false;
	
	public void settings()
	{
		/*
		for( int x = -1; x <= 1; x += 2 )
		{
			for( int y = -1; y <= 1; y += 2 )
			{
				for( int z = -1; z <= 1; z += 2 )
				{
					score.add( new Point3f( x*5, y*5, z*5 ) );
				}	
			}	
		}
		*/
		
		for( int y = 0; y < 6; y++ )
		{
			for( int ai = 0; ai < 6; ai++ )
			{
				float angle = (float)((Math.PI/3) * ai);
				score.add( new Point3f( (float)(Math.cos( angle ) * (7 - y)), (float)(Math.sin( angle ) * (7 - y)), y * 3 ) );
			}
		}
		
		buildShape();
		
		size( 800, 600, "processing.opengl.PGraphics3D" );
	}
	
	public void setup()
	{
		cam = new PeasyCam( this, 50 );
		cam.setMinimumDistance( 1 );
		cam.setMaximumDistance( 100 );
		
		cp5 = new ControlP5( this );
		cp5.setAutoDraw( false );
	}
	
	public void addLines( Branch b, ArrayList<Primitive> prims )
	{
		for( Branch c : b.children )
		{
			prims.add( new Preset.ConvLine( b.p, c.p, 2 ) );
			addLines( c, prims );
		}
	}
	
	public void save()
	{
		ArrayList<Primitive> prims = new ArrayList<Primitive>();
		
		addLines( models.get( 0 ).root, prims );
		
		FieldBuilder fb = new SumFieldBuilder();
		Field f = fb.buildField( prims, new Vector3f( -10, -10, -3 ), new Vector3f( 10, 10, 21 ), .2f );
		
		MarchingCubePolygonizer mc = new MarchingCubePolygonizer();
		
		ArrayList<Triangle> tris = mc.polygonize( f, .5f );
		MarchingSolver.saveTriangles( tris, "test.scad" );
	}
	
	public void controlEvent( ControlEvent e )
	{
		
	}
	
	public void buildShape()
	{
		models.clear();
		for( int i = 0; i < 36; i++ )
		{
			Model m = new Model();
			m.root = new Branch();
			m.root.p = new Point3f( score.points.get( 0 ) );
			m.root.isRoot = true;
			
			models.add( m );
		}
	}
	
	public void mutate()
	{
		ArrayList<Model> newModels = new ArrayList<>();
		
		for( int j = 0; j < 3; j++ )
		{
			for( int k = 0; k < 36/3; k++ )
			{
				if( k == 0 ) 
				{
					newModels.add( models.get( j ) );
				}
				else
				{
					newModels.add( models.get( j ).mutate() );
				}
			}
		}
		
		models = newModels;
		
		for( Model m : models )
		{
			m.score = score.score( m );
		}
		
		Collections.sort( models, (a, b) -> Float.compare( a.score, b.score ) );
	}
	
	public void drawBranch( Branch b )
	{
		for( Branch c : b.children )
		{
			if( c.inserted ) stroke( 255, 0, 0 );
			else if( b == models.get( 0 ).root ) stroke( 0, 255, 0 );
			else stroke( 255, 255, 255 );
			line( b.p.x, b.p.y, b.p.z, c.p.x, c.p.y, c.p.z );
			drawBranch( c );
		}
	}
	
	public void draw()
	{
		if( keyPressed ) 
		{
		    if( key == 'r' )
		    {
		    	buildShape();
		    }
		    if( key == 's' )
		    {
		    	save();
		    }
		}
		
		if( optimizing )
		{
			ModelOptimizer.optimize( models.get( 0 ), score );
			optimizing = false;
		}
		else
		{
			mutate();
			optimizing = true;
		}
		
		//RENDER
		background( 0 );
		lights();
		
		perspective( PI/3.f, (float)width/height, .1f, 100 );
		
		stroke( 255, 255, 255 );
		fill( 255, 255, 255 ); 
		
		for( Point3f p : score.points )
		{
			pushMatrix();
			translate( p.x, p.y, p.z );
			sphere( .1f );
			popMatrix();
		}
		
		drawBranch( models.get( 0 ).root );
		
		cam.beginHUD();
		ortho();
		camera();
		noLights();
		text( models.get( 0 ).score, 20, 20 );
		cp5.draw();
		cam.endHUD();
	}
	
	public void mouseMoved()
	{
		if( mouseX < 150 )
		{
			cam.setActive( false );
		}
		else
		{
			cam.setActive( true );
		}
	}
	
	public static void main( String[] args )
	{
		PApplet.runSketch( new String[] { "Test" }, new Test() );
	}
}