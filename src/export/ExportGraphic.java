package export;

import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*; 


import globals.*;
import layers.*;

import geom.*;
import circuit.*;

/** ExportGraphic.java v.1.4

	Handle graphic export of a Fidocad file
	This class should be used to export the circuit under different graphic
	formats.
	
<pre>
   ****************************************************************************
   Version History 

Version   Date           Author       Remarks
------------------------------------------------------------------------------
1.0     December 2007	D. Bucci    First working version: format PNG
1.1 	January 2008	D. Bucci	Internazionalized version
1.2		June 2008 		D. Bucci	A few improvements
1.3 	July 2008	    D. Bucci	A few more graphic formats
1.4		January 2009	D. Bucci    Bug fixes

	This file is part of FidoCadJ.

    FidoCadJ is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FidoCadJ is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FidoCadJ.  If not, see <http://www.gnu.org/licenses/>.

	Copyright 2007-2009 by Davide Bucci
</pre>   
  
	@author Davide Bucci
	@version 1.4, January 2009
*/
public class ExportGraphic 
{
	
	
	/** Exports the circuit contained in circ using the specified parsing 
		class.
		
		@param file the file name of the graphic file which will be created.
		@param P the parsing schematics class which should be used (libraries).
		@param format the graphic format which should be used {png|jpg}.
		@param unitperpixel the number of unit for each graphic pixel.
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting
	*/
	public static void export(File file, 
						ParseSchem P, 
						String format,
						double unitPerPixel,
						boolean antiAlias,
						boolean blackWhite,
						boolean ext)
	throws IOException
	{
		exportSizeP( file, 
						 P, 
						 format,
						 0,
						 0,
						 unitPerPixel,
						 false,
						 antiAlias,
						 blackWhite,
						 ext);
	}	
	
	/** Exports the circuit contained in circ using the specified parsing 
		class.
		
		@param file the file name of the graphic file which will be created.
		@param P the parsing schematics class which should be used (libraries).
		@param format the graphic format which should be used {png|jpg}.
		@param width the image width in pixels (raster images only)
		@param heith the image heigth in pixels (raster images only)
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting
	*/
	public static void exportSize(File file, 
						ParseSchem P, 
						String format,
						int width,
						int height,
						boolean antiAlias,
						boolean blackWhite,
						boolean ext)
	throws IOException
	{
		exportSizeP( file, 
						 P, 
						 format,
						 width,
						 height,
						 1,
						 true,
						 antiAlias,
						 blackWhite,
						 ext);
	}
	
	/** Exports the circuit contained in circ using the specified parsing 
		class.
		
		@param file the file name of the graphic file which will be created.
		@param P the parsing schematics class which should be used (libraries).
		@param format the graphic format which should be used {png|jpg}.
		@param unitperpixel the number of unit for each graphic pixel.
		@param width the image width in pixels (raster images only)
		@param heith the image heigth in pixels (raster images only)
		@param setSize if true, calculate resolution from size. If false, it 
			does the opposite strategy.
		@param antiAlias specify whether the anti alias option should be on.
		@param blackWhite specify that the export should be done in B/W.
		@param ext activate FidoCadJ extensions when exporting
	*/
	private static void exportSizeP(File file, 
						ParseSchem P, 
						String format,
						int width,
						int height,
						double unitPerPixel,
						boolean setSize,
						boolean antiAlias,
						boolean blackWhite,
						boolean ext)
	throws IOException
	{

		// obtain drawing size
		double oxz=P.getMapCoordinates().getXMagnitude();
		double oyz=P.getMapCoordinates().getYMagnitude();
		
		if (setSize) {
			Dimension d = getImageSize(P, 1,false);
		
			d.width+=20;
			d.height+=20;
   	 		//System.out.println(d);

			unitPerPixel = Math.min(width/(double)d.width, height/(double)d.height);
		
			P.getMapCoordinates().setMagnitudes(unitPerPixel, unitPerPixel);		
		} else {
			Dimension d = getImageSize(P, unitPerPixel,false);
		
			width=d.width+20;
			height=d.height+20;
		
		}
		
		ArrayList ol=P.getLayers();

		
		BufferedImage bufferedImage;
		
        
	
		// To print in black and white, we only need to create a single layer
		// in which all layers will be exported and drawn.
		// Clearly, the choosen color will be black.
		if(blackWhite) {
			ArrayList v=new ArrayList();
			for (int i=0; i<16;++i)
				v.add(new LayerDesc(Color.black, 
					((LayerDesc)ol.get(i)).getVisible(),
					"B/W",((LayerDesc)ol.get(i)).getAlpha()));
			
			P.setLayers(v);
		}
        
        
        P.getMapCoordinates().setMagnitudes(unitPerPixel, unitPerPixel);
	   	
       	
                    
        
    	if (format.equals("png")||format.equals("jpg")) {
			// Create a buffered image in which to draw
			
			try {
        		bufferedImage = new BufferedImage(width, height, 
        								  BufferedImage.TYPE_INT_RGB);
    
        		// Create a graphics contents on the buffered image
        		Graphics2D g2d = bufferedImage.createGraphics();
        
        		if(antiAlias) {
        			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                   		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                   		RenderingHints.VALUE_ANTIALIAS_ON);
        			}
        		g2d.setColor(Color.white);
        		g2d.fillRect(0,0, width, height);
				// Save bitmap
				P.draw(g2d);
       		
        		ImageIO.write(bufferedImage, format, file);
        		// Graphics context no longer needed so dispose it
        		g2d.dispose();
        	} catch (java.lang.OutOfMemoryError E) {
        		IOException D=new IOException("Memory Error");
    			MapCoordinates m=P.getMapCoordinates();
       			m.setMagnitudes(oxz, oyz);
       			P.setMapCoordinates(m);
       			P.setLayers(ol);
    			throw D;
        	} catch (Exception E) {
        		IOException D=new IOException("Size error"+E);
    			MapCoordinates m=P.getMapCoordinates();
       			m.setMagnitudes(oxz, oyz);
       			P.setMapCoordinates(m);
       			P.setLayers(ol);
    			throw D;
			}
    	} else if(format.equals("svg")) {
    		ExportSVG es = new ExportSVG(file);
    		P.exportDrawing(es, true);
    	} else if(format.equals("eps")) {
    		ExportEPS ep = new ExportEPS(file);
    		P.exportDrawing(ep, true);
    	} else if(format.equals("pgf")) {
    		ExportPGF ef = new ExportPGF(file);
    		P.exportDrawing(ef, true);
    	} else if(format.equals("pdf")) {
    		ExportPDF ef = new ExportPDF(file);
    		P.exportDrawing(ef, true);
    	} else if(format.equals("scr")) {
    		ExportEagle ef = new ExportEagle(file);
    		P.exportDrawing(ef, true);
    	} else if(format.equals("fcd")) {
    		ExportFidoCad ef = new ExportFidoCad(file);
    		ef.setExtensions(ext);
    		P.exportDrawing(ef, true);
    	} else {
    		IOException E=new IOException(
    			"Wrong file format");
    		throw E;
    	}
    	
    	MapCoordinates m=P.getMapCoordinates();
       	m.setMagnitudes(oxz, oyz);
       	P.setMapCoordinates(m);
       	P.setLayers(ol);

    	return;
    	
    }
    
    /**	Get the image size.
    	@param P the parsing class to be used.
    	@param unitperpixel the zoom set to be used.
    	@param countMin specifies that the size should be calculated counting 
    		the minimum x and y coordinates, and not the origin.
    
    */
    public static Dimension getImageSize(ParseSchem P, 
    							  double unitperpixel, 
    							  boolean countMin)
    {
    	int width;
		int height;
		// Unfortunately, to get the image size, we need to redraw it.	
		// I do not like it, even if here we do not are in a speed sensitive
		// context!
		
		P.setChanged(true);
		double oxz=P.getMapCoordinates().getXMagnitude();
		double oyz=P.getMapCoordinates().getYMagnitude();
		
		BufferedImage bufferedImage = new BufferedImage(10, 10, 
        								  BufferedImage.TYPE_INT_RGB);
    
        // Create a graphics contents on the buffered image
		MapCoordinates m=P.getMapCoordinates();
       	m.setMagnitudes(unitperpixel, unitperpixel);
       	//m.setXCenter(0);
       	//m.setYCenter(0);
       	m.resetMinMax();
       	P.setMapCoordinates(m);
		//System.out.println("unitperpixel: "+unitperpixel);
        
        
        Graphics2D g2d = bufferedImage.createGraphics();
       	// force an in deep recalculation
       	P.setChanged(true);

        P.draw(g2d);
        // Graphics context no longer needed so dispose it
        g2d.dispose();
    	
    	// Verify that the image size is correct
    	
    	if(countMin) {
			width=m.getXMax()-
				m.getXMin();
			height=m.getYMax()-
				m.getYMin();
		} else {
			width=m.getXMax();
			height=m.getYMax();
		}

       	//System.out.println(m.toString());
			
		if(width<=0 || height<=0) {
			System.out.println("Warning: Image has a zero"+
							   "sized image");
							   
			width=100;
			height=100;
		}
        P.getMapCoordinates().setMagnitudes(oxz,oyz);

		return new Dimension(width, height);
    }
    
    /**	Get the image origin.
    	@param P the parsing class to be used.
    	@param unitperpixel the zoom set to be used.
    
    */
    
    public static Point getImageOrigin(ParseSchem P, double unitperpixel)
    {
    	int originx;
		int originy;
		// Unfortunately, to get the image size, we need to redraw it.	
		// I do not like it, even if here we do not are in a speed sensitive
		// context!
		
		P.setChanged(true);		
		double oxz=P.getMapCoordinates().getXMagnitude();
		double oyz=P.getMapCoordinates().getYMagnitude();
		
		double ox=P.getMapCoordinates().getXCenter();
		double oy=P.getMapCoordinates().getYCenter();
		
		BufferedImage bufferedImage = new BufferedImage(100, 100, 
        								  BufferedImage.TYPE_INT_RGB);
    
        // Create a graphics contents on the buffered image
		MapCoordinates m=P.getMapCoordinates();
       	m.setMagnitudes(unitperpixel, unitperpixel);
       	m.setXCenter(0);
       	m.setYCenter(0);
       	
       	//P.setMapCoordinates(m);
       	
		//System.out.println("unitperpixel: "+unitperpixel);
        Graphics2D g2d = bufferedImage.createGraphics();

        P.draw(g2d);
        // Graphics context no longer needed so dispose it
        g2d.dispose();
    	
    
    	// Verify that the image size is correct
		if (P.getMapCoordinates().getXMax() >= P.getMapCoordinates().getXMin() && 
			P.getMapCoordinates().getYMax() >= P.getMapCoordinates().getYMin()){
			originx=P.getMapCoordinates().getXMin();
			originy=P.getMapCoordinates().getYMin();
		} else {
			System.out.println("Warning: Image has a zero"+
							   "sized image");
			originx=0;
			originy=0;
		}
        P.getMapCoordinates().setMagnitudes(oxz, oyz);
        P.getMapCoordinates().setXCenter(ox);
        P.getMapCoordinates().setYCenter(oy);
		
		return new Point(originx, originy);
    }
    
    /** Calculate the zoom to fit the given size in pixel (i.e. the viewport
    	size).
    	
    	@param sizex the width of the area to be used for calculations.
    	@param sizey the height of the area to be used for calculations.
    	@param countMin specify if the absolute or relative size should be
    		taken into account
    
    */
    public static MapCoordinates calculateZoomToFit(ParseSchem P, int sizex, int sizey, 
    				boolean forceCalc, boolean countMin)
    {
 		// Here we calculate the zoom to fit parameters
		double oldZoom=P.getMapCoordinates().getXMagnitude();
		double maxsizex;
		double maxsizey;
		Point org=new Point(0,0);
		
		P.setChanged(true);
		MapCoordinates newZoom=new MapCoordinates();
	
		// If the size is invalid (for example because it's the first time
		// the circuit has been drawn).
		
		forceCalc=true;	// 0.20.5
		if (!forceCalc){
		/*
			maxsizex=(P.getMapCoordinates().getXMax()-
				P.getMapCoordinates().getXMin())/Z;
			maxsizey=(P.getMapCoordinates().getYMax()-
				P.getMapCoordinates().getYMin())/Z;
			org.x=(int)(P.getMapCoordinates().getXMin()/Z);
			org.y=(int)(P.getMapCoordinates().getYMin()/Z);
			*/
			maxsizex=P.getMapCoordinates().getXMax()/oldZoom;
			maxsizey=P.getMapCoordinates().getYMax()/oldZoom;
		} else {
			Dimension D = getImageSize(P,1,countMin); 
			maxsizex=D.width;
			maxsizey=D.height;
			//System.out.println("recalc: "+maxsizex+", "+maxsizey);
			
			
			if (countMin) 
				org=getImageOrigin(P,1);
			

		}
/*
		double zoomx=.98/((maxsizex+10)/(double)sizex);
		double zoomy=.98/((maxsizey+10)/(double)sizey);
*/	

	
		double zoomx=1/((maxsizex)/(double)sizex);
		double zoomy=1/((maxsizey)/(double)sizey);
		//System.out.println("zoomx: "+zoomx+", "+zoomy);
				
		
		double z=(zoomx>zoomy)?zoomy:zoomx;
		
		
		z=Math.round(z*100.0)/100.0;		// 0.20.5
		
		//System.out.println("recalc z: "+z);
			
		
		newZoom.setMagnitudes(z,z);
		newZoom.setXCenter(-(org.x*z));
		newZoom.setYCenter(-(org.y*z));
		
		P.getMapCoordinates().setMagnitudes(oldZoom, oldZoom);
		
		//System.out.println(newZoom.toString());
		return newZoom;
	}
    
}