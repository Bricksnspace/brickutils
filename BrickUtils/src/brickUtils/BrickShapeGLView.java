/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
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
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLOffscreenAutoDrawable;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import bricksnspace.ldrawlib.LDrawLib;

//import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;



public class BrickShapeGLView implements GLEventListener {

	GLCanvas canvas = null;

	private GL2 gl2;
	private LDrawLib ldrlib;
	private float maxX;
	private float minX;
	private float minY;
	private float minZ;
	private float maxY;
	private float maxZ;
	private BrickColor mainColor;
	private String ldrawid;
	private float angleY = 135.0f;
	private float angleX = -45.0f;
	private GLU glu;
	private GLUquadric qn;
	private GLUquadric qi;
	private int studCount;
	private int sizex, sizey;
	


	
	static { GLProfile.initSingleton(); }

	/* 
	 * requires a working LDraw Library from main program
	 */
	public BrickShapeGLView(LDrawLib ldr, boolean onscreen, int width, int height) {
		
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        
        ldrlib = ldr;
        sizex = width;
        sizey = height;
        if (onscreen) {
        	canvas = new GLCanvas(caps);
        	canvas.setPreferredSize(new Dimension(sizex,sizey));
        	canvas.addGLEventListener(this);
        	canvas.setVisible(true);
        }
	}

	
	

	protected void finalize() {
		if (canvas != null)
			canvas.destroy();
	}
	
	
	
	public GLCanvas getCanvas() {
		return canvas;
	}

	
    /**
	 * @return a string for LDraw brick ID rendered in GL canvas
	 */
	public String getLdrawid() {
		return ldrawid;
	}



	/**
	 * @param set brick to render through LDraw id
	 */
	public void setLdrawid(String ldrawid) {
		this.ldrawid = ldrawid;
		canvas.repaint();
	}
	


	public void setLdrawid(String ldrawid, boolean repaint) {
		this.ldrawid = ldrawid;
		if (repaint)
			canvas.repaint();
	}
	


	public void setLdrawColor(int colid) {
    	
    	mainColor = BrickColor.getLdrawColor(colid);
    	canvas.repaint();
    }


	
	public void setLdrawColor(int colid, boolean repaint) {
    	
    	mainColor = BrickColor.getLdrawColor(colid);
    	if (repaint)
    		canvas.repaint();
    }


	public void setColor(int colid) {
    	
    	mainColor = BrickColor.getColor(colid);
    	canvas.repaint();
    }
	
	
	public void setColor(int colid, boolean repaint) {
    	
    	mainColor = BrickColor.getColor(colid);
    	if (repaint)
    		canvas.repaint();
    }


	
	
	/*
	 * start of OpenGl code
	 */
	
    /*
     * setup GL material properties for brick, based on BrickColor
     */
    private void setMaterial() {
    	
    	if (mainColor.metallic) {
    		// it is a metallic color, set high shininess
    		gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, new float[] {0.5f,0.5f,0.5f,1.0f},0);
            gl2.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 60.0f);
    	}
    	else {
    		// shiny plastic
    		gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, new float[] {0.7f,0.7f,0.7f,1.0f},0);
            gl2.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 30.0f);
    	}
    }
    
    
	/*
	 * set main color for part
	 */
	private void setColor(Color bc) {
		
    	if (bc == null) {
    		gl2.glColor3d(0.0, 0.0, 0.0);
    	}
    	else {
    		
            gl2.glColor4d(bc.getRed()/255.0,bc.getGreen()/255.0,
            		bc.getBlue()/255.0,bc.getAlpha()/255.0);
            //gl2.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, 0.3f);
            gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, 
            		new float[] {(float)(bc.getRed()/256.0),(float)(bc.getGreen()/256.0),(float)(bc.getBlue()/256.0)},0);
    	}
    }

        
    /* 
     * this is a special LDraw color, usually edge color.
     * It is referred as "complementary" color, but it is always a darker (or brighter)
     *  version of main color.
     */
    private Color getColorCompl(Color bc) {
    	
    	if (bc == null) {
    		return new Color(0.3f, 0.3f, 0.3f);
    	}
    	else {
    		/* 
    		 * creates a "complementary" color, darker if color is light
    		 * lighter if a color is dark
    		 */
    		if (bc.getRed()+bc.getGreen()+bc.getBlue() < 100) {
    			// a dark color, use lighter one
    			return bc.brighter();
    		}
    		else {
    			// a bright enough color, use a darker one
    			return bc.darker();
    		}
    	}
    	
    }
    
    
    
    private Color parseDirectColor(String dc) {
    	
    	if (dc.startsWith("0x2")) {
    		int r = Integer.parseInt(dc.substring(3, 5),16);
    		int g = Integer.parseInt(dc.substring(5, 7),16);
    		int b = Integer.parseInt(dc.substring(7),16);
    		return new Color(r,g,b);
    	}
    	else return Color.BLACK;
    }

        
    private Color getColorId(int id) {
    	
    	BrickColor bc;

    	bc = BrickColor.getLdrawColor(id);
    	if (bc == null)
    		return Color.BLACK;
    	return bc.color;
    }
    
    
    private void ld4_4cyli(boolean invert, Color c) {

    	int nseg;
    	
		if (studCount > 200)
    		nseg = 4;
		else
			nseg = 16;
        gl2.glPushMatrix();
        gl2.glRotatef(-90,1,0,0);
        if (invert) {
        	setColor(getColorCompl(c));
            glu.gluCylinder(qi,1,1,1,nseg,1);
        }
        else {
        	setColor(c);
            glu.gluCylinder(qn,1,1,1,nseg,1);
        }
        gl2.glPopMatrix();
    }

    
    private void ldstud(boolean invert, Color c) {

    	int nseg;
    	
		if (studCount > 200)
    		nseg = 4;
		else
			nseg = 16;
        gl2.glPushMatrix();
        gl2.glRotatef(-90,1,0,0);
        if (invert) {
            setColor(getColorCompl(c));
            glu.gluDisk(qn, 5.8, 6.2,nseg, 1);
            setColor(c);
            gl2.glTranslated(0, 0, -4);
            glu.gluCylinder(qi,6,6,4,nseg,1);
            glu.gluDisk(qn,0,6,nseg,1);
            setColor(getColorCompl(c));
            glu.gluDisk(qn, 5.8, 6.2,nseg, 1);
            setColor(c);
        }
        else {
            setColor(getColorCompl(c));
            glu.gluDisk(qi, 5.8, 6.2,nseg, 1);
            setColor(c);
            gl2.glTranslated(0, 0, -4);
            glu.gluCylinder(qn,6,6,4,nseg,1);
            glu.gluDisk(qi,0,6,nseg,1);
            setColor(getColorCompl(c));
            glu.gluDisk(qi, 5.8, 6.2,nseg, 1);
            setColor(c);
        }
        gl2.glPopMatrix();
    }


    
    private void ld4_4disc(boolean invert, Color c) {

    	int nseg;
    	
		if (studCount > 200)
    		nseg = 4;
		else
			nseg = 16;
        gl2.glPushMatrix();
        gl2.glRotatef(90,1,0,0);
        if (invert) {
        	setColor(getColorCompl(c));
            glu.gluDisk(qi,0,1,nseg,1);
        }
        else {
            setColor(c);
            glu.gluDisk(qn,0,1,nseg,1);
        }
        gl2.glPopMatrix();
    }


    private void ld4_4edge(boolean invert, Color c) {
    	
    	int nseg;
    	
		if (studCount > 200)
    		nseg = 4;
		else
			nseg = 16;
        gl2.glPushMatrix();
        gl2.glRotatef(90,1,0,0);
        if (invert) {
        	setColor(getColorCompl(c));
            glu.gluDisk(qn,0.99,1.01,nseg,1);
        }
        else {
            glu.gluDisk(qi,0.99,1.01,nseg,1);
            setColor(c);
        }

    	gl2.glPopMatrix();    	
    }
    
    
	private void checkMinMax(float x,float y,float z, float[] m) {
    	
    	float a,b,c,e,f,g,i,j,k,u,v,w;
    	float xm,ym,zm;

        a = m[0]; // a,b,c = (m[0][0],m[1][0],m[2][0])
        b = m[4];
        c = m[8];
        e = m[1]; // e,f,g = (m[0][1],m[1][1],m[2][1])
        f = m[5];
        g = m[9];
        i = m[2];	//i,j,k = (m[0][2],m[1][2],m[2][2])
        j = m[6];
        k = m[10];
        u = m[12];	// u,v,w = (m[3][0],m[3][1],m[3][2])
        v = m[13];
        w = m[14];

        xm = x*a + y*b + z*c + u;
        ym = x*e + y*f + z*g + v;
        zm = x*i + y*j + z*k + w;
        if (xm > maxX) 
            maxX = xm;
        if (ym > maxY)
            maxY = ym;
        if (zm > maxZ)
            maxZ = zm;
        if (xm < minX) 
            minX = xm;
        if (ym < minY) 
            minY = ym;
        if (zm < minZ)
            minZ = zm;
    }

	
    private float[] calcNormal(float x,float y,float z,
    						float x1,float y1,float z1,
    						float x2,float y2,float z2) {
        
    	float v1x,v1y,v1z,v2x,v2y,v2z,xn,yn,zn,d;
    	
    	v1x = x1 - x;
        v1y = y1 - y;
        v1z = z1 - z;
        v2x = x2 - x1;
        v2y = y2 - y1;
        v2z = z2 - z1;
        xn = v1y * v2z - v1z * v2y;
        yn = v1z * v2x - v1x * v2z;
        zn = v1x * v2y - v1y * v2x;
        d = (float) Math.sqrt((double)xn*xn+yn*yn+zn*zn);
        if (d == 0)
            return new float[] {0.0f,0.0f,1.0f};
        xn = xn/d;
        yn = yn/d;
        zn = zn/d;
        return new float[] {xn,yn,zn};
    }


    void checkSize(LineNumberReader ldf) throws NumberFormatException, IOException {
    	
    	String l;
    	String[] ld;
    	LineNumberReader part;
    	float m[] = new float[16];
    	float matrix[] = new float[16];

    	
        gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX,m,0);
        while ((l = ldf.readLine()) != null) {
            ld = l.toLowerCase().trim().split("\\s+");
            if (ld.length < 1 || ld[0].equals("0"))
                continue;
            else if (ld[0].equals("1")) {
                if (ld.length < 15) {
                    System.out.println("Error line="+ldf.getLineNumber()+", too few parms: "+ld);
                    continue;
                }
                if (ld[14].equals("stud.dat")) {
                	studCount++;
                	continue;
                }
                part = ldrlib.getPart(ld[14]);
                if (part == null) {
                	//System.out.println("Part not found: "+ld[14]+" linea: "+ldf.getLineNumber());
                	continue;
                }
                gl2.glPushMatrix();                
                matrix[0] = Float.parseFloat(ld[5]); //[float(ld[5]),float(ld[8]),float(ld[11]),0.0,
                matrix[1] = Float.parseFloat(ld[8]);
                matrix[2] = Float.parseFloat(ld[11]);
                matrix[3] = 0.0f;
                matrix[4] = Float.parseFloat(ld[6]); //float(ld[6]),float(ld[9]),float(ld[12]),0.0,
                matrix[5] = Float.parseFloat(ld[9]);
                matrix[6] = Float.parseFloat(ld[12]);
                matrix[7] = 0.0f;
                matrix[8] = Float.parseFloat(ld[7]); //float(ld[7]),float(ld[10]),float(ld[13]),0.0,
                matrix[9] = Float.parseFloat(ld[10]);
                matrix[10] = Float.parseFloat(ld[13]);
                matrix[11] = 0.0f;
                matrix[12] = Float.parseFloat(ld[2]); //float(ld[2]),float(ld[3]),float(ld[4]),1.0
                matrix[13] = Float.parseFloat(ld[3]);
                matrix[14] = Float.parseFloat(ld[4]);
                matrix[15] = 1.0f;
                
                gl2.glMultMatrixf(matrix, 0);
                
                checkSize(part);
                part.close();
                gl2.glPopMatrix();
            }
            else if (ld[0].equals("2")) {
                // -------------------------------------------------- it is a line
                if (ld.length < 8) {
                    System.out.println("Error line: "+ldf.getLineNumber()+", too few par: "+ld);
                    continue;
                }
                //x,y,z = self.quickMult(float(ld[2]),float(ld[3]), float(ld[4]),m)
                checkMinMax(Float.parseFloat(ld[2]), Float.parseFloat(ld[3]),Float.parseFloat(ld[4]),m);
                //x,y,z = self.quickMult(float(ld[5]),float(ld[6]), float(ld[7]),m)
                checkMinMax(Float.parseFloat(ld[5]),Float.parseFloat(ld[6]),Float.parseFloat(ld[7]),m);
            }
            else if (ld[0].equals("3")) {
                // ------------------------------------------------------ triangle
                if (ld.length < 11) {
                    System.out.println("Error line: "+ldf.getLineNumber()+", too few par: "+ld);
                    continue;
                }
                //x,y,z = self.quickMult(float(ld[2]),float(ld[3]), float(ld[4]),m)
                checkMinMax(Float.parseFloat(ld[2]), Float.parseFloat(ld[3]), Float.parseFloat(ld[4]),m);
                //x,y,z = self.quickMult(float(ld[5]),float(ld[6]), float(ld[7]),m)
                checkMinMax(Float.parseFloat(ld[5]), Float.parseFloat(ld[6]), Float.parseFloat(ld[7]),m);
                //x,y,z = self.quickMult(float(ld[8]),float(ld[9]), float(ld[10]),m)
                checkMinMax(Float.parseFloat(ld[8]),Float.parseFloat(ld[9]),Float.parseFloat(ld[10]),m);
            }
            else if (ld[0].equals("4")) {
                // -------------------------------------------------------- quads
                if (ld.length < 14) {
                    System.out.println("Error line: "+ldf.getLineNumber()+", too few par: "+ld);
                    continue;
                }
                //x,y,z = self.quickMult(float(ld[2]),float(ld[3]), float(ld[4]),m)
                checkMinMax(Float.parseFloat(ld[2]),Float.parseFloat(ld[3]),Float.parseFloat(ld[4]),m);
                //x,y,z = self.quickMult(float(ld[5]),float(ld[6]), float(ld[7]),m)
                checkMinMax(Float.parseFloat(ld[5]),Float.parseFloat(ld[6]),Float.parseFloat(ld[7]),m);
                //x,y,z = self.quickMult(float(ld[8]),float(ld[9]), float(ld[10]),m)
                checkMinMax(Float.parseFloat(ld[8]),Float.parseFloat(ld[9]),Float.parseFloat(ld[10]),m);
                //x,y,z = self.quickMult(float(ld[11]),float(ld[12]), float(ld[13]),m)
                checkMinMax(Float.parseFloat(ld[11]),Float.parseFloat(ld[12]),Float.parseFloat(ld[13]),m);
            }
            else if (ld[0].equals("5")) {
                // ------------------------------------------------- optional line
                if (ld.length < 14) {
                    System.out.println("Error line: "+ldf.getLineNumber()+", too few par: "+ld);
                    continue;
                }
                continue;
            }
        }
    }
	
	
    private void drawFile(LineNumberReader ldf, Color c, boolean invAll) throws NumberFormatException, IOException {
    	
    	String l;
    	String[] ld;
    	LineNumberReader part;
    	float matrix[] = new float[16];
    	boolean invNext,invFlag,winding;
    	Color nc;
    	
        invNext = false;
        invFlag = invAll;
        winding = false;
        
        //System.out.println("Studs="+studCount);
        setColor(c);
		while ((l = ldf.readLine()) != null) {
			//System.out.println("linea: "+ldf.getLineNumber()+" -- "+l);
		    ld = l.toLowerCase().trim().split("\\s+");
		    if (ld.length < 1) 
		        continue;
		    else if (ld[0].equals("0")) {
		    	if (ld.length > 2)
		            if (ld[1].equals("bfc")) 
		                if (ld[2].equals("invertnext")) 
		                    invNext = true;
		                else if (ld[2].equals("certify")) 
		                    if (ld.length == 3 || ld[3].equals("cw"))
		                        winding = true;
		                    else
		                        winding = false;
		    	continue;
		    }
		    else if (ld[0].equals("1")) {
		        if (ld.length < 15) {
		            System.out.println("Error line="+ldf.getLineNumber()+", too few parms: "+ld);
		            continue;
		        }
		        gl2.glPushMatrix();
		        if (ld[1].equals("16")) 
		            nc = c;
		        else if (!ld[1].equals("24")) {
		        	if (ld[1].startsWith("0x2")) {
		        		nc = parseDirectColor(ld[1]);
		        	}
		        	else
		        		nc = getColorId(Integer.parseInt(ld[1]));
		        }
		        else
		            nc = getColorCompl(c);

		        matrix[0] = Float.parseFloat(ld[5]); //[float(ld[5]),float(ld[8]),float(ld[11]),0.0,
		        matrix[1] = Float.parseFloat(ld[8]);
		        matrix[2] = Float.parseFloat(ld[11]);
		        matrix[3] = 0.0f;
		        matrix[4] = Float.parseFloat(ld[6]); //float(ld[6]),float(ld[9]),float(ld[12]),0.0,
		        matrix[5] = Float.parseFloat(ld[9]);
		        matrix[6] = Float.parseFloat(ld[12]);
		        matrix[7] = 0.0f;
		        matrix[8] = Float.parseFloat(ld[7]); //float(ld[7]),float(ld[10]),float(ld[13]),0.0,
		        matrix[9] = Float.parseFloat(ld[10]);
		        matrix[10] = Float.parseFloat(ld[13]);
		        matrix[11] = 0.0f;
		        matrix[12] = Float.parseFloat(ld[2]); //float(ld[2]),float(ld[3]),float(ld[4]),1.0
		        matrix[13] = Float.parseFloat(ld[3]);
		        matrix[14] = Float.parseFloat(ld[4]);
		        matrix[15] = 1.0f;
		        
		        gl2.glMultMatrixf(matrix,0);
		        if (ld[14].equals("stud.dat")) {
		        	ldstud(invFlag^invNext,nc);
		        	//System.out.println("stud "+studCount );
		        }
		        else if (ld[14].equals("4-4cyli.dat")) 
		        	ld4_4cyli(invFlag^invNext,nc);
		        else if (ld[14].equals("4-4disc.dat"))
		        	ld4_4disc(invFlag^invNext,nc);
		        else if (ld[14].equals("4-4edge.dat"))
		        	ld4_4edge(invFlag^invNext,nc);
		        else {
			        part = ldrlib.getPart(ld[14]);
			        if (part == null) {
			        	//System.out.println("Part not found: "+ld[14]+" linea: "+ldf.getLineNumber());
			        	continue;
			        }
			        drawFile(part,nc,(invFlag^invNext));
			        try {
						part.close();
					} catch (IOException e) {
						;
					}
		        }
		        gl2.glPopMatrix();
		        invNext = false;
		    }
		    else if (ld[0].equals("2")) {
		        // -------------------------------------------------- it is a line
		        if (ld.length < 8) {
		            System.out.println("Error line: "+ldf.getLineNumber()+", too few par: "+ld);
		            continue;
		        }
		        gl2.glBegin(GL.GL_LINES);
		        if (ld[1].equals("24")) 
		            setColor(getColorCompl(c));
		        else if (!ld[1].equals("16")) {
		            if (ld[1].startsWith("0x2")) {
		            	setColor(parseDirectColor(ld[1]));
		            }
		            else 
		            	setColor(getColorId(Integer.parseInt(ld[1])));
		        }
		        else {
		        	setColor(c);
		        }
		        gl2.glNormal3f(0.0f,0.0f,1.0f);
		        gl2.glVertex3f(Float.parseFloat(ld[2]),Float.parseFloat(ld[3]),Float.parseFloat(ld[4]));
		        gl2.glVertex3f(Float.parseFloat(ld[5]),Float.parseFloat(ld[6]),Float.parseFloat(ld[7]));
		        gl2.glEnd();
		        setColor(c);
		        
		    }
		    else if (ld[0].equals("3")) {
		        // ------------------------------------------------------ triangle
		        if (ld.length < 11) {
		            System.out.println("Error line: "+ldf.getLineNumber()+", too few par: "+ld);
		            continue;
		        }
		        gl2.glBegin(GL.GL_TRIANGLES);
		        if (ld[1].equals("24")) 
		            setColor(getColorCompl(c));
		        else if (!ld[1].equals("16")) {
		            if (ld[1].startsWith("0x2")) {
		            	setColor(parseDirectColor(ld[1]));
		            }
		            else 
		            	setColor(getColorId(Integer.parseInt(ld[1])));
		        }
		        else {
		        	setColor(c);
		        }
		        if (invFlag ^ !winding) {
		            gl2.glNormal3fv(calcNormal(
		            		Float.parseFloat(ld[2]),Float.parseFloat(ld[3]),Float.parseFloat(ld[4]),
		                    Float.parseFloat(ld[5]),Float.parseFloat(ld[6]), Float.parseFloat(ld[7]),
		                    Float.parseFloat(ld[8]),Float.parseFloat(ld[9]), Float.parseFloat(ld[10])),0);
		        }
		        else {
		            gl2.glNormal3fv(calcNormal(
		            		Float.parseFloat(ld[8]),Float.parseFloat(ld[9]), Float.parseFloat(ld[10]), 
		                    Float.parseFloat(ld[5]),Float.parseFloat(ld[6]), Float.parseFloat(ld[7]),
		                    Float.parseFloat(ld[2]),Float.parseFloat(ld[3]), Float.parseFloat(ld[4])),0);
		        }
		        gl2.glVertex3f(Float.parseFloat(ld[2]),Float.parseFloat(ld[3]),Float.parseFloat(ld[4]));
		        gl2.glVertex3f(Float.parseFloat(ld[5]),Float.parseFloat(ld[6]),Float.parseFloat(ld[7]));
		        gl2.glVertex3f(Float.parseFloat(ld[8]),Float.parseFloat(ld[9]),Float.parseFloat(ld[10]));
		        gl2.glEnd();
		        setColor(c);
		    }
		    else if (ld[0].equals("4")) {
		        // -------------------------------------------------------- quads
		        if (ld.length < 14) {
		            System.out.println("Error line: "+ldf.getLineNumber()+", too few par: "+ld);
		            continue;
		        }
		        gl2.glBegin(GL2.GL_QUADS);
		        if (ld[1].equals("24")) 
		            setColor(getColorCompl(c));
		        else if (!ld[1].equals("16")) {
		            if (ld[1].startsWith("0x2")) {
		            	setColor(parseDirectColor(ld[1]));
		            }
		            else 
		            	setColor(getColorId(Integer.parseInt(ld[1])));
		        }
		        else {
		        	setColor(c);
		        }
		        if (invFlag ^ !winding) {
		            gl2.glNormal3fv(calcNormal(
		            		Float.parseFloat(ld[2]),Float.parseFloat(ld[3]),Float.parseFloat(ld[4]),
		                    Float.parseFloat(ld[5]),Float.parseFloat(ld[6]), Float.parseFloat(ld[7]),
		                    Float.parseFloat(ld[8]),Float.parseFloat(ld[9]), Float.parseFloat(ld[10])),0);
		        }
		        else {
		            gl2.glNormal3fv(calcNormal(
		            		Float.parseFloat(ld[8]),Float.parseFloat(ld[9]), Float.parseFloat(ld[10]), 
		                    Float.parseFloat(ld[5]),Float.parseFloat(ld[6]), Float.parseFloat(ld[7]),
		                    Float.parseFloat(ld[2]),Float.parseFloat(ld[3]), Float.parseFloat(ld[4])),0);
		        }
		        gl2.glVertex3f(Float.parseFloat(ld[2]),Float.parseFloat(ld[3]),Float.parseFloat(ld[4]));
		        gl2.glVertex3f(Float.parseFloat(ld[5]),Float.parseFloat(ld[6]),Float.parseFloat(ld[7]));
		        gl2.glVertex3f(Float.parseFloat(ld[8]),Float.parseFloat(ld[9]),Float.parseFloat(ld[10]));
		        gl2.glVertex3f(Float.parseFloat(ld[11]),Float.parseFloat(ld[12]),Float.parseFloat(ld[13]));
		        gl2.glEnd();
		        setColor(c);
		    }
		}
    }





    
    

	protected void render(GL2 gl2) {
		
		LineNumberReader ldf;
		
		//long t0 = System.currentTimeMillis();
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);    // Clear The Screen And The Depth Buffer

        gl2.glPushMatrix();					// Reset The View
        gl2.glLoadIdentity();
        gl2.glRotatef(180,0,0,1);
        gl2.glRotatef(angleX,1,0,0);
        gl2.glRotatef(angleY,0,1,0);
        maxX = -10000;
        minX = 10000;
        minY = 10000;
        minZ = 10000;
        maxY = -10000;
        maxZ = -10000;
        studCount = 0;
        ldf = ldrlib.getPart(ldrawid);
        if (ldf == null) {
            gl2.glPopMatrix();
        	return;
        }
        try {
			checkSize(ldf);
			ldf.close();
		} catch (IOException e) {
	        gl2.glPopMatrix();
			return;
		}
        gl2.glPopMatrix();
        gl2.glPushMatrix();              // Reset The View
        gl2.glLoadIdentity();
        float M = Math.max(Math.max(maxX,maxY),maxZ);
        float m = Math.min(Math.min(minX,minY),minZ);
        float y = -(maxY + minY) / 2;
        float x = -(maxX + minX) / 2;
        float t = (M - m);
        gl2.glTranslated(x,y,-t*1.3-30);
        gl2.glRotatef(180,0,0,1);
        gl2.glRotatef(angleX,1,0,0);
        gl2.glRotatef(angleY,0,1,0);
        ldf = ldrlib.getPart(ldrawid);
        setMaterial();
        try {
			drawFile(ldf,mainColor.color,false);
			ldf.close();
		} catch (IOException e) {
	        gl2.glPopMatrix();
			return;
		}
        gl2.glPopMatrix();
        //System.out.println("Time: "+(System.currentTimeMillis()-t0));
	}
	

	
	/* 
	 * rotate shape by 30 degree steps
	 */
	public void rotateUp() {
		angleX += 30.0f;
		if (angleX > 360.0f) 
			angleX -= 360.0;
		if (angleX < -360.0f) 
			angleX -= -360.0f;
		canvas.repaint();
	}
	

	public void rotateDown() {
		angleX -= 30.0f;
		if (angleX > 360.0f) 
			angleX -= 360.0;
		if (angleX < -360.0f) 
			angleX -= -360.0f;
		canvas.repaint();
	}
	
	
	public void rotateLeft() {
		angleY += 30.0f;
		if (angleY > 360.0f) 
			angleY -= 360.0;
		if (angleY < -360.0f) 
			angleY -= -360.0f;
		canvas.repaint();
	}
	
	
	public void rotateRight() {
		angleY -= 30.0f;
		if (angleY > 360.0f) 
			angleY -= 360.0;
		if (angleY < -360.0f) 
			angleY -= -360.0f;
		canvas.repaint();
	}
	
	
	public void rotateFlipX() {
		angleX += 180.0f;
		if (angleX > 360.0f) 
			angleX -= 360.0;
		if (angleX < -360.0f) 
			angleX -= -360.0f;
		if (canvas != null)
			canvas.repaint();
	}
	

	public void rotateFlipY() {
		angleY += 180.0f;
		if (angleY > 360.0f) 
			angleY -= 360.0;
		if (angleY < -360.0f) 
			angleY -= -360.0f;
		canvas.repaint();
	}
	
	
	public void rotateReset() {
        angleY = 135.0f;
        angleX = -45.0f;
		canvas.repaint();
	}
	

	
	public BufferedImage getStaticImage(boolean rear) {
		
		GLDrawableFactory fac = GLDrawableFactory.getFactory(GLProfile.getGL2ES1());
		GLCapabilities glCap = new GLCapabilities(GLProfile.getGL2ES1());
		// Without line below, there is an error in Windows.
		glCap.setDoubleBuffered(false);
		//makes a new buffer
		GLOffscreenAutoDrawable buf = fac.createOffscreenAutoDrawable(null, glCap, null, sizex, sizey);
		//required for drawing to the buffer
		GLContext context =  buf.createContext(null); 
		context.makeCurrent();
		gl2 = context.getGL().getGL2();
		//System.out.println("disegno");
		gl2.glViewport(0, 0, sizex, sizey);
		glInit(gl2);
		if (rear) {
			angleX = 135.0f;
		}
		else {
			angleX = -45.0f;
		}
		render(gl2);
		//gl2.glFinish();
		//System.out.println("catturo");
		// not working on Mavericks 10.9.x
		//AWTGLReadBufferUtil agb = new AWTGLReadBufferUtil(buf.getGLProfile(), true);
		//BufferedImage image = agb.readPixelsToBufferedImage(context.getGL(), true);
		BufferedImage image = new BufferedImage(sizex,sizey, BufferedImage.TYPE_3BYTE_BGR);
		DataBufferByte awfulBufferBack = (DataBufferByte) image.getRaster().getDataBuffer();
		Buffer b = ByteBuffer.wrap(awfulBufferBack.getData());
		gl2.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);
		// broken images on OSX Mavericks if alpha channel is included
//		glcontext.getGL().getGL2().glReadPixels(0, 0, w, h, GL2.GL_BGRA, GL2.GL_UNSIGNED_INT_8_8_8_8_REV, b);
		gl2.glReadPixels(0, 0, sizex, sizey, GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE, b);
		context.release();

		//System.out.println(image.toString());
		context.destroy();
		buf.destroy();
		// flip vertical
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
	    tx.translate(0,-image.getHeight());
	    AffineTransformOp op = new AffineTransformOp(tx,
	        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    image = op.filter(image, null);
        return image;
	}


	@Override
	public void display(GLAutoDrawable glad) {

		render(glad.getGL().getGL2());
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		
		;
	}

	@Override
	public void init(GLAutoDrawable glad) {
		
		gl2 = glad.getGL().getGL2();
		glInit(gl2);
	}
	
	
	private void glInit(GL2 gl2) {
		
		float[] ambient  = {0.2f,0.2f,0.2f,1.0f};
		float[] diffuse   = {0.75f,0.75f,0.75f,1.0f};
		float posLight1[] = { -10000.0f, 10000.f, 10000.0f, 0.0f };
		float posLight2[] = { 10000.0f, -2000.0f, 6000.0f , 0.0f};
		
		glu = GLU.createGLU(gl2);

        gl2.glLightfv( GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient ,0);
        gl2.glLightfv( GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse ,0 );
        gl2.glLightfv( GL2.GL_LIGHT2, GL2.GL_DIFFUSE, diffuse ,0 );
        gl2.glLightfv( GL2.GL_LIGHT1, GL2.GL_POSITION, posLight1 ,0 );
        gl2.glLightfv( GL2.GL_LIGHT2, GL2.GL_POSITION, posLight2, 0);
        

        gl2.glEnable(GL2.GL_LIGHTING);
        gl2.glDisable(GL2.GL_LIGHT0);
        gl2.glEnable(GL2.GL_LIGHT1);
        gl2.glEnable(GL2.GL_LIGHT2);
        //gl2.glEnable(GL2.GL_DEPTH_TEST);
        gl2.glEnable(GL2.GL_BLEND); 
        gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        gl2.glEnable(GL2.GL_COLOR_MATERIAL);
        gl2.glClearColor(0.90f, 0.90f, 0.90f, 1);    // This Will Clear The Background Color
        gl2.glClearDepth(1.0);                    // Enables Clearing Of The Depth Buffer
        gl2.glDepthFunc(GL2.GL_LESS);                // The Type Of Depth Test To Do
        //gl2.glEnable(GL2.GL_ALPHA_TEST);
        gl2.glEnable(GL2.GL_DEPTH_TEST);                // Enables Depth Testing
        //gl2.glShadeModel(GL2.GL_SMOOTH);                // Enables Smooth Color Shading
        gl2.glEnable(GL2.GL_NORMALIZE);
        //gl2.glEnable(GL2.GL_LINE_SMOOTH);

        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();                    // Reset The Projection Matrix
                                            // Calculate The Aspect Ratio Of The Window
        // glu.gluPerspective(45.0, (float) /*canvas.getSize().width/ (float) canvas.getSize().height*/ sizex / (float) sizey, 0.1, 2000.0);
        glu.gluPerspective(45.0,1.0,1,2000);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        qn = glu.gluNewQuadric();
        glu.gluQuadricTexture(qn,true);
        qi = glu.gluNewQuadric();
        glu.gluQuadricTexture(qi,true);
        glu.gluQuadricOrientation(qi,GLU.GLU_INSIDE);        

        angleY = 135.0f;
        angleX = -45.0f;
		
	}

	@Override
	public void reshape(GLAutoDrawable glad, int x, int y, int w, int h) {

		gl2 = glad.getGL().getGL2();
		glu = GLU.createGLU(gl2);
    
        gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();                    // Reset The Projection Matrix
        // Calculate The Aspect Ratio Of The Window
        glu.gluPerspective(45.0, (float) canvas.getSize().width/ (float) canvas.getSize().height, 1, 2000.0);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);

		render(glad.getGL().getGL2());
		
	}

}
