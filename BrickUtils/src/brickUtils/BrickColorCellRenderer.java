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


import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import bricksnspace.bricklinklib.BricklinkColor;

public class BrickColorCellRenderer extends JLabel implements TableCellRenderer {
	
	private static final long serialVersionUID = 6207685309481210L;
	Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public BrickColorCellRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object mapid,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        @SuppressWarnings("boxing")
		BrickColor bc = BrickColor.getColor((Integer) mapid);
        setBackground(bc.color);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                              table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
        
        setToolTipText("Id:"+bc.ldd+"("+bc.lddName+") Bl:"+bc.bl+"("+
				BricklinkColor.getColor(bc.bl).getName()+(bc.inProduction? ")": ") (discontinued)"));
        return this;
    }

}
