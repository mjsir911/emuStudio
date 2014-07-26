/*
 * TerminalWindow.java
 *
 * Created on Piatok, 2007, november 16, 11:42
 *
 * Copyright (C) 2007-2013 Peter Jakubčo
 * KISS, YAGNI, DRY
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.sf.emustudio.devices.adm3a.gui;

import emulib.runtime.LoggerFactory;
import emulib.runtime.interfaces.Logger;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import net.sf.emustudio.devices.adm3a.impl.Display;

/**
 * Window representing the terminal.
 *
 * @author Peter Jakubčo
 */
public class TerminalWindow extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalWindow.class);
    private final Display display;
    private final Font terminalFont;

    public TerminalWindow(Display display) {
        InputStream fin = this.getClass().getResourceAsStream("/net/sf/emustudio/devices/adm3a/gui/terminal.ttf");
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fin).deriveFont(12f);
        } catch (Exception e) {
            LOGGER.error("Cannot load custom font file, using Java monospace", e);
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
                LOGGER.error("Could not close font input stream", e);
            }
        }
        if (font == null) {
            font = new java.awt.Font("Monospaced", 0, 12);
        }
        terminalFont = font;
        this.display = display;

        initComponents();
        setVisible(false);
        this.setLocationRelativeTo(null);
    }

    public void destroy() {
        display.destroy();
        this.dispose();
    }

    private void initComponents() {
        JLabel lblBack = new JLabel();
        ImageIcon img = new ImageIcon(getClass().getResource("/net/sf/emustudio/devices/adm3a/gui/display.gif"));

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Terminal ADM-3A");
        setResizable(false);

        display.setFont(terminalFont);
        display.setForeground(new java.awt.Color(0, 255, 0));
        display.setBackground(new Color(0, 0, 0));
        display.setBounds(53, 60, 653, 400);

        lblBack.setLocation(0, 0);
        lblBack.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        lblBack.setIcon(img); // NOI18N
        lblBack.setFocusable(false);

        lblBack.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());

        Container pane = getContentPane();
        pane.setBackground(Color.BLACK);
        pane.setLayout(null);

        pane.add(display);
        pane.add(lblBack);
        pack();
        setSize(img.getIconWidth(), img.getIconHeight());
    }
}
