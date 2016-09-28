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
import java.util.ArrayList;


/**
 * @author Mario Pascucci
 * utility class for grouping colors
 * it will be used to find substitutions
 */
public class ColorGroup {
	
	private ArrayList<Color> colorGroup;
	private ArrayList<String> colorName;

	/**
	 * creates list of color group
	 */
	public ColorGroup() {
		colorGroup = new ArrayList<Color>();
		colorName = new ArrayList<String>();
		colorGroup.add(Color.BLACK);
		colorName.add("Black");
		colorGroup.add(Color.GRAY);
		colorName.add("Dark gray");
		colorGroup.add(Color.LIGHT_GRAY);
		colorName.add("Light gray");
		colorGroup.add(Color.WHITE);
		colorName.add("White");
		colorGroup.add(Color.decode("#A52A2A")); // brown 
		colorName.add("Brown");
		colorGroup.add(Color.decode("#D2B48C"));
		colorName.add("Tan");
		colorGroup.add(Color.RED);
		colorName.add("Red");
		colorGroup.add(Color.ORANGE);
		colorName.add("Orange");
		colorGroup.add(Color.YELLOW);
		colorName.add("Yellow");
		colorGroup.add(Color.GREEN);
		colorName.add("Green");
		colorGroup.add(Color.decode("#006400"));
		colorName.add("Dark green");
		colorGroup.add(Color.BLUE);
		colorName.add("Blue");
		colorGroup.add(Color.CYAN);
		colorName.add("Light blue");
		colorGroup.add(Color.decode("#8A2BE2")); // violet
		colorName.add("Violet");
		colorGroup.add(Color.MAGENTA);
		colorName.add("Purple");
		colorGroup.add(Color.PINK);
		colorName.add("Pink");
		colorGroup.add(Color.decode("#DAA520")); // gold
		colorName.add("Gold");
		colorGroup.add(Color.decode("#C0C0C0")); // silver 
		colorName.add("Silver");
		
	}

	public Color get(int idx) {
		return colorGroup.get(idx);
	}
	
	public String getName(int idx) {
		return colorName.get(idx);
	}
	
	public int size() {
		return colorGroup.size();
	}
	

}
