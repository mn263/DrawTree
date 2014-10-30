package com.company;


import com.company.draw.shapes.*;
import spark.data.*;
import spark.data.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;

public class SwingTree extends JFrame {

	public static TreePanel treePanel = new TreePanel(new Mouse());
	public static Root root = null;

	public SwingTree() {
//		Make Menu and MenuBar
		JMenu menu = new JMenu("Menu");
		JMenuBar mb = new JMenuBar();
		menu.setMnemonic(KeyEvent.VK_M);
		menu.add(getOpenOption(this)); // Add Option
		mb.add(menu); // Add Menu to MenuBar
//		Set Container
		Container container = this.getContentPane();
		container.setBackground(Color.white);
		container.setPreferredSize(new Dimension(800, 800));

		this.setJMenuBar(mb);
		this.add(treePanel);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		WidgetUtils.setSwingTree(this);
	}

	public JMenuItem getOpenOption(final SwingTree swingTree) {
		JMenuItem menuOpen = new JMenuItem("Open");

		menuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);

				FileFilter drawFilter = new FileNameExtensionFilter(".draw Files", "draw");
				chooser.setFileFilter(drawFilter);
				int option = chooser.showOpenDialog(SwingTree.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					File sf = chooser.getSelectedFile();
					String filePath = sf.getAbsolutePath();
					String fileContents = readFile(filePath);

					String [] factoryTypes = new String[1];
					factoryTypes[0] = "com.company.draw.shapes";

					SONReader sonReader = new SONReader(factoryTypes, fileContents);
					SV sv = sonReader.read();
					SO object = sv.getSO();
					Drawable drawable = (Drawable) object;
					treePanel.addDrawable(drawable);
					if (object.getClass().toString().equals("class com.company.draw.shapes.Root") && root == null) {
						root = (Root) object;
					}
					swingTree.getContentPane().repaint();
				}
			}
		});
		return menuOpen;
	}

	private String readFile(String filePath) {
		BufferedReader br = null;
		String returnString = "";
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filePath));
			while ((sCurrentLine = br.readLine()) != null) {
				returnString += sCurrentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return returnString;
	}

	public static void runGUI() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new SwingTree();
			}
		});
	}

	public static Root getRoot() {
		return root;
	}

	public static void mouseReleased(MouseEvent e) {
		if (root != null) {
			root.mouseUp(e.getX(), e.getY(), new AffineTransform());
			WidgetUtils.repaintAll();
		}
	}

	public static void mousePressed(MouseEvent e) {
		if (root != null) {
			root.mouseDown(e.getX(), e.getY(), new AffineTransform());
			WidgetUtils.repaintAll();
		}
	}

	public static void mouseMoved(MouseEvent e) {
		if (root != null) {
			root.mouseMove(e.getX(), e.getY(), new AffineTransform());
			WidgetUtils.repaintAll();
		}
	}

	public static void mouseDragged(MouseEvent e) {
		if (root != null) {
			root.mouseMove(e.getX(), e.getY(), new AffineTransform());
			WidgetUtils.repaintAll();
		}
	}
}