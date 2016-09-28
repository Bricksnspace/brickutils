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


/**
 * A class to avoid data modification via Velocity macros in a template export.
 * </p>
 * Contains same data of {@link BrickSet} class, but without methods that modifies data
 * 
 * @author Mario Pascucci
 *
 */
public class ExportedSet {

	public int id;
	public String setid;
	public int type;		// moc, lot, official, ecc.
	public String name;
	public String category;
	public int year;
	public int catid;
	public String notes;
	public boolean available;
	

	
	public ExportedSet(BrickSet set) {
		id = set.id;
		setid = set.setid;
		type = set.type;
		name = set.name;
		category = set.category;
		year = set.year;
		catid = set.catid;
		notes = set.notes;
		available = set.available;
	}
	
	
	@Override
	public String toString() {
		return "BrickSet [id=" + id + ", setid=" + setid + ", type=" + type
				+ ", name=" + name + ", category=" + category + ", year="
				+ year + ", catid=" + catid + ", notes=" + notes
				+ ", available=" + available + "]";
	}

	public int getId() {
		return id;
	}


	public String getSetid() {
		return setid;
	}


	public int getType() {
		return type;
	}


	public String getName() {
		return name;
	}


	public String getCategory() {
		return category;
	}


	public int getYear() {
		return year;
	}


	public int getCatid() {
		return catid;
	}


	public String getNotes() {
		return notes;
	}


	public boolean isAvailable() {
		return available;
	}

}
