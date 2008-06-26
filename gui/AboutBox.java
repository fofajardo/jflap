/* -- JFLAP 4.0 --
 *
 * Copyright information:
 *
 * Susan H. Rodger, Thomas Finley
 * Computer Science Department
 * Duke University
 * April 24, 2003
 * Supported by National Science Foundation DUE-9752583.
 *
 * Copyright (c) 2003
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the author.  The name of the author may not be used to
 * endorse or promote products derived from this software without
 * specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package gui;

import gui.ImageDisplayComponent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

// import java.applet.Applet;
// import java.applet.AudioClip;

/**
 * The <TT>AboutBox</TT> is the about box for JFLAP.
 * 
 * @author Thomas Finley
 */

public class AboutBox extends JWindow {
	/**
	 * Instantiates a new <TT>AboutBox</TT>.
	 * 
	 * @param owner
	 *            the owner of this about box
	 */
	public AboutBox(Frame owner) {
		super(owner);
		getContentPane().setLayout(new OverlayLayout(getContentPane()));
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(3, 3, 3, 3));
		JPanel fullPanel = new JPanel(new BorderLayout());
		fullPanel.setOpaque(false);
		panel.add(fullPanel, BorderLayout.SOUTH);
		getContentPane().add(panel);
		getContentPane().add(new ImageDisplayComponent(IMAGE));
		addMouseListener(new BoxDismisser());
	}

	/**
	 * Returns a label with the appropriate string.
	 * 
	 * @param string
	 *            the string to display
	 * @return a properly created JLabel visible on this frame
	 */
	private static JLabel getLabel(String string) {
		JLabel label = new JLabel(string);
		if (IMAGE != null)
			label.setForeground(Color.black);
		return label;
	}

	/**
	 * Instantiates a new <TT>AboutBox</TT> with no specified owner.
	 */
	public AboutBox() {
		this((Frame) null);
	}

	/**
	 * Displays this about box, and plays the clip.
	 */
	public void displayBox() {
		boolean toPlay = !isVisible();
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension boxSize = getPreferredSize();
		setLocation((screenSize.width - boxSize.width) >> 1,
				(screenSize.height - boxSize.height) >> 1);
		toFront();
		setVisible(true);
	}

	/**
	 * Dismisses this about box, and stops the clip.
	 */
	public void dismissBox() {
		dispose();
		// CLIP.stop();
	}

	/**
	 * This listens for clicks on the box. When it receives them, the box is
	 * dismissed.
	 */
	private class BoxDismisser extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			dismissBox();
		}
	}

	/** A simple object to get the class off for resource reading. */
	private static Object OBJECT = new Object();

	/** The image to display in the about box. */
	private static Image IMAGE = null;

	/** The version string. */
	public static final String VERSION = "6.4";

	/**
	 * Some simple test code for the about box.
	 */
	public static void main(String args[]) {
		AboutBox box = new AboutBox();
		box.displayBox();
	}

	static {
		try {
			IMAGE = Toolkit.getDefaultToolkit().getImage(
					OBJECT.getClass().getResource("/MEDIA/about.png"));
		} catch (NullPointerException e) {

		}
	}
}
