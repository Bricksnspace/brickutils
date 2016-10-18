/*
	Copyright 2013-2015 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils.

	BrickUtils is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickUtils is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickUtils.  If not, see <http://www.gnu.org/licenses/>.

*/


package brickUtils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import bricksnspace.j3dgeom.Matrix3D;
import bricksnspace.ldraw3d.LDRenderedPart;
import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldrawlib.LDPrimitive;

/**
 * A "harmless" class used for template export via Velocity
 * <p/>
 * It contains same part data from class {@link Brick} but without data-poisoning methods  
 * 
 * @author Mario Pascucci
 *
 */
public class ExportedBrick {
	
	int id;				// index for db and relations
    String masterID;	// the brick "family" ID 
    String designID;	// LDD design ID
    String partNO;		// LEGO® part ID
    String name;		// brick name from BL
    String blID;		// BrickLink ID
    String ldrawID;		// LDraw catalog ID
    int color;			// color index
    String decorID;
    int quantity;		// brick quantity
    boolean extra;		// it is an extra part
    boolean alt;		// it is an alternate part
    int matchid;		// match for alternate part

   
    ExportedBrick(Brick b) {
    	
    	id = b.id;
    	quantity = b.quantity;
    	designID = b.designID;
    	masterID = b.masterID;
    	partNO = b.partNO;
    	color = b.color;
    	name = b.name;
    	blID = b.blID;
    	ldrawID = b.ldrawID;
    	decorID = b.decorID;
    	extra = b.extra;
    	alt = b.alt;
    	matchid = b.matchid;
    }

    
    
	@Override
	public String toString() {
		return "Brick [id=" + id + ", masterID=" + masterID + ", designID="
				+ designID + ", partNO=" + partNO + ", name=" + name
				+ ", blID=" + blID + ", ldrawID=" + ldrawID + ", color="
				+ color + ", decorID=" + decorID + ", quantity=" + quantity
				+ ", extra=" + extra + ", alt=" + alt + ", matchid=" + matchid
				+ "]";
	}

	
	

	
	public int getId() {
		return id;
	}



	public String getMasterID() {
		return masterID;
	}



	public String getDesignID() {
		return designID;
	}



	public String getPartNO() {
		return partNO;
	}



	public String getName() {
		return name;
	}



	public String getBlID() {
		return blID;
	}



	public String getLdrawID() {
		return ldrawID;
	}



	public String getDecorID() {
		return decorID;
	}



	public int getQuantity() {
		return quantity;
	}



	public Color getColor() {
		
		return BrickColor.getColor(color).color;
	}
	
	
	public String getHtmlColor() {
		
		return String.format("#%02x%02x%02x", 
				/* red */   getColor().getRed(),
				/* green */ getColor().getGreen(),
				/* blue */  getColor().getBlue());
	}
	
	
	public String getColorName() {
		
		return BrickColor.getColor(color).getLddName();
	}
	
	
	public int getLddColor() {
		
		return BrickColor.getColor(color).ldd;
	}
	
	
	public int getBlColor() {
		
		return BrickColor.getColor(color).bl;
	}
	
	
	
	public int getLdrawColor() {
		
		return BrickColor.getColor(color).ldraw;
	}
	
	
	
	public boolean isExtra() {
		return extra;
	}



	public boolean isDecorated() {
		
		return decorID != null && decorID.trim().length() > 0;
	}
	
	
	
	public static boolean isFrontAndRear() {
		return Brick.isFrontAndRear();
	}



	
	///////////////////////////////////////////
	// I/O helper methods
	///////////////////////////////////////////

	/**
	 * return brick PNG image encoded Base64 (for HTML embedding) 
	 * @return 3D image of this brick base64 encoded
	 * @throws IOException
	 */
	public String getBrickImageBase64Enc() throws IOException {

		if (Brick.getShapeView() == null) {
			throw new IllegalStateException("[getBrickImageBase64Enc] shapeView not initialized");
		}
		BufferedImage image = getBrickImage(Brick.getShapeView(), Brick.isFrontAndRear());
		ByteArrayOutputStream img = new ByteArrayOutputStream();
		ImageIO.write(image, "png", img);
		return Base64.encodeBase64String(img.toByteArray());
	}
	

	/**
	 * generates an image for brick
	 * @param brickShape a BrickShapeGLView "off screen" to generate images
	 * @param frontandback if true generates a front and back view, else only front view
	 * @return 3D image of this brick
	 */
	public BufferedImage getBrickImage(LDrawGLDisplay brickShape, boolean frontandback) {
		
		brickShape.disableAutoRedraw();
		brickShape.clearAllParts();
		LDRenderedPart rendPart = LDRenderedPart.newRenderedPart(
				LDPrimitive.newGlobalPart(ldrawID, BrickColor.getColor(color).ldraw, new Matrix3D()));
		double diagxz;
		float angle = 20f;
		if (rendPart.getSizeZ() > rendPart.getSizeX()) {
			angle = 70;
		}
		diagxz = Math.sqrt(rendPart.getSizeX()*rendPart.getSizeX() +
			rendPart.getSizeZ()*rendPart.getSizeZ());
		double diagxy = Math.sqrt(rendPart.getSizeX()*rendPart.getSizeX() +
				rendPart.getSizeY()*rendPart.getSizeY());
		float diag = (float) Math.max(diagxz,diagxy);
		int size = brickShape.getCanvas().getPreferredSize().width;
		float ratio = (float) (diag/(size-30f));
		brickShape.resetView();
		brickShape.rotateY(angle);
		brickShape.rotateX(-30);
		brickShape.setOrigin(rendPart.getCenterX(), rendPart.getCenterY(), rendPart.getCenterZ());
		brickShape.setZoom(ratio);
		brickShape.enableAutoRedraw();
		brickShape.addRenderedPart(rendPart);
		BufferedImage img = brickShape.getStaticImage(size, size);
		if (! frontandback) {
			return img;
		}
		brickShape.rotateX(180);
		brickShape.addRenderedPart(rendPart);
		BufferedImage imgf = brickShape.getStaticImage(size, size);
		int sizex = img.getWidth()+imgf.getWidth();
		// image height is max of two
		int sizey = img.getHeight() > imgf.getHeight() ? img.getHeight() : imgf.getHeight();
		BufferedImage tot = new BufferedImage(sizex,sizey,BufferedImage.TYPE_3BYTE_BGR);
		Graphics paint = tot.getGraphics();
		paint.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
		paint.drawImage(imgf, img.getWidth(), 0, imgf.getWidth(), imgf.getHeight(), null);
		return tot;
	}
	
	
}
