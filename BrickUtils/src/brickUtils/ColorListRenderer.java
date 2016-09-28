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
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

public class ColorListRenderer extends JLabel implements ListCellRenderer<Object> {
	
	private static final long serialVersionUID = 6307685763094812103L;
	Border unselectedBorder = null;
    Border selectedBorder = null;
    private ColorGroup colgrp;

    public ColorListRenderer(ColorGroup cg) {
        setOpaque(true); //MUST do this for background to show up.
        colgrp = cg;
    }

    public Component getListCellRendererComponent(
                            JList<?> list, Object index,
                            int idx,
                            boolean isSelected, boolean hasFocus) {
        
        setBackground(colgrp.get((Integer)index));
        setToolTipText(colgrp.getName((Integer)index));
        setText(" ");
        if (isSelected) {
            if (selectedBorder == null) {
                selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                          list.getSelectionBackground());
            }
            setBorder(selectedBorder);
        } else {
            if (unselectedBorder == null) {
                unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                          list.getBackground());
            }
            setBorder(unselectedBorder);
        }
        
//        setToolTipText("RGB value: " + newColor.getRed() + ", "
//                                     + newColor.getGreen() + ", "
//                                     + newColor.getBlue());
        return this;
    }

}
