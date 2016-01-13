/*
 * 
 * BY: Joakim Berglund (jobe7147)
 * 	   joakimberglund@live.se
 * 	   
 * 	   Cristoffer Lagergren (crla5658)
 *     cristofferlagergren@gmail.com
 * 
 * 	   2015-04-29
 * 	   @DSV Stockholms universitet
 * 
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ProgramUI extends JFrame {

	private JButton searchButton, hideButton, delButton, whatButton,
			newCatButton, hideCatButton, delCatButton;
	private CenterPanel centerPanel = new CenterPanel(null);
	private JTextField catField, placeName, searchField;
	private JTextArea placeDesc;
	private JColorChooser colChoose;
	private Map<String, Color> categories = new HashMap<>();
	private Map<String, Set<NamedPlace>> placesCatKey = new HashMap<>();
	private Map<Position, Set<NamedPlace>> placesPosKey = new HashMap<>();
	private Map<String, Set<NamedPlace>> placesNameKey = new TreeMap<>();
	private DefaultListModel<String> catModel = new DefaultListModel<>();
	private JList<String> catList = new JList<>(catModel);
	private JComboBox<String> placeBox;
	private MapListen ml = new MapListen();
	private WhatMouse wm = new WhatMouse();
	private JFileChooser jfc = new JFileChooser(".");
	private FileNameExtensionFilter bildFilter = new FileNameExtensionFilter(
			"Image Files", "jpg", "png", "tif");
	private FileNameExtensionFilter kartFilter = new FileNameExtensionFilter(
			"Map Files (*.krt; *.map)", "krt", "map");
	private boolean saved = true;
	private String fileName = null;
	private Dimension listDimension = new Dimension(150, 200);

	public ProgramUI() {
		setLayout(new BorderLayout());

		// Top menu
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Arkiv");
		menuBar.add(menu);
		JMenuItem newItem = new JMenuItem("New map");
		menu.add(newItem);
		newItem.addActionListener(new NewMap());
		JMenuItem openItem = new JMenuItem("Open");
		menu.add(openItem);
		openItem.addActionListener(new OpenListen());
		JMenuItem saveItem = new JMenuItem("Save");
		menu.add(saveItem);
		saveItem.addActionListener(new SaveListen());
		JMenuItem exitItem = new JMenuItem("Exit");
		menu.add(exitItem);
		exitItem.addActionListener(new WindowExitListen());
		setJMenuBar(menuBar);

		// North menu
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		northPanel.add(new JLabel("New:"));
		placeBox = new JComboBox<String>();
		placeBox.addItem("");
		placeBox.addItem("Described Place");
		placeBox.addItem("Named Place");
		placeBox.setSelectedIndex(0);
		placeBox.addActionListener(new ComboSelect());
		northPanel.add(placeBox);
		searchField = new JTextField("Search", 25);
		northPanel.add(searchField);
		northPanel.add(searchButton = new JButton("Search"));
		searchButton.addActionListener(new SearchListen());
		northPanel.add(hideButton = new JButton("Hide Place"));
		hideButton.addActionListener(new HidePlaceListen());
		northPanel.add(delButton = new JButton("Delete Place"));
		delButton.addActionListener(new DeletePlaceListen());
		northPanel.add(whatButton = new JButton("What is here?"));
		whatButton.addActionListener(new WhatListen());
		add(northPanel, BorderLayout.NORTH);

		// East menu
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		JLabel catLabel = new JLabel("Categories:");
		catLabel.setAlignmentX(CENTER_ALIGNMENT);
		eastPanel.add(Box.createVerticalGlue());
		eastPanel.add(catLabel);

		
		catList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		catList.setLayoutOrientation(JList.VERTICAL);
		catList.addListSelectionListener(new CatListSelect());
		JScrollPane catScroll = new JScrollPane(catList);
		catScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		catScroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		catScroll.setPreferredSize(listDimension);
		catScroll.setMaximumSize(listDimension);
		catScroll.setMinimumSize(listDimension);
		catScroll.setSize(listDimension);
		eastPanel.add(catScroll);
		eastPanel.add(hideCatButton = new JButton("Hide Category"));
		hideCatButton.setAlignmentX(CENTER_ALIGNMENT);
		hideCatButton.addActionListener(new HideCatListen());
		eastPanel.add(newCatButton = new JButton("New Category"));
		newCatButton.setAlignmentX(CENTER_ALIGNMENT);
		eastPanel.add(delCatButton = new JButton("Delete Category"));
		newCatButton.addActionListener(new NewCategory());
		delCatButton.setAlignmentX(CENTER_ALIGNMENT);
		delCatButton.addActionListener(new RemoveCat());
		add(eastPanel, BorderLayout.EAST);

		jfc.addChoosableFileFilter(bildFilter);
		jfc.addChoosableFileFilter(kartFilter);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowExitListen());
		repaint();
		pack();
		setResizable(false);
		setVisible(true);
	}

	class NewMap implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			jfc.setFileFilter(bildFilter);
			try {
				int chosen = jfc.showOpenDialog(ProgramUI.this);
				File file = jfc.getSelectedFile();
				String mapName = file.getAbsolutePath();
				if (chosen != JFileChooser.APPROVE_OPTION) {
					return;
				}
				if (centerPanel != null) {
					remove(centerPanel);
				}
				centerPanel = new CenterPanel(mapName);
				add(centerPanel, BorderLayout.CENTER);
				pack();
				centerPanel.repaint();
			} catch (NullPointerException npe) {
				return;
			}

		}
	}

	class OpenListen implements ActionListener {
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			jfc.setFileFilter(kartFilter);
			try {
			int answer = jfc.showOpenDialog(ProgramUI.this);
			if (answer != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File file = jfc.getSelectedFile();
			fileName = file.getAbsolutePath();

			
				FileInputStream fis = new FileInputStream(fileName);
				ObjectInputStream ois = new ObjectInputStream(fis);
				remove(centerPanel);
				categories.clear();
				centerPanel = (CenterPanel) ois.readObject();
				placesNameKey = (TreeMap<String, Set<NamedPlace>>) ois
						.readObject();
				placesPosKey = (HashMap<Position, Set<NamedPlace>>) ois
						.readObject();
				categories = (HashMap<String, Color>) ois.readObject();
				add(centerPanel, BorderLayout.CENTER);
				catModel.clear();
				Object[] catArray = categories.keySet().toArray();
				for (Object o : catArray) {
					String str = (String) o;
					catModel.addElement(str);
				}
				ois.close();
				fis.close();
				pack();
				validate();
				repaint();
			} catch (ClassNotFoundException cnf) {
				JOptionPane.showMessageDialog(ProgramUI.this,
						"Class not found!");
			} catch (FileNotFoundException fnf) {
				JOptionPane
						.showMessageDialog(ProgramUI.this, "File not found!");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(ProgramUI.this,
						"Error: " + ioe.getMessage());
			}
		}
	}

	class SaveListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			jfc.setFileFilter(kartFilter);
			File file = null;

			if (fileName == null) {
				int answer = jfc.showSaveDialog(ProgramUI.this);
				if (answer != JFileChooser.APPROVE_OPTION) {
					return;
				}
				file = jfc.getSelectedFile();
				fileName = file.getAbsolutePath();

				if (!fileName.toLowerCase().endsWith(".krt")) {
					fileName = file.getAbsolutePath() + ".krt";
				}

			}

			try {
				FileOutputStream fos = new FileOutputStream(fileName);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				centerPanel.removeMouseListener(ml);
				centerPanel.removeMouseListener(wm);
				remove(centerPanel);
				oos.writeObject(centerPanel);
				add(centerPanel);
				oos.writeObject(placesNameKey);
				oos.writeObject(placesPosKey);
				oos.writeObject(categories);
				oos.close();
				fos.close();
				saved = true;
			} catch (FileNotFoundException fnf) {
				JOptionPane
						.showMessageDialog(ProgramUI.this, "File not found!");
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(ProgramUI.this,
						"Error: " + ioe.getMessage());
			}
		}
	}

	class WindowExitListen extends WindowAdapter implements ActionListener {
		public void exitMethod() {
			if (!saved) {
				int answer = JOptionPane.showConfirmDialog(ProgramUI.this,
						"Unsaved changes, exit anyway?", "Warning",
						JOptionPane.OK_CANCEL_OPTION);
				if (answer == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			} else {
				System.exit(0);
			}
		}

		public void windowClosing(WindowEvent wev) {
			exitMethod();
		}

		public void actionPerformed(ActionEvent e) {
			exitMethod();
		}
	}

	class ComboSelect implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (centerPanel != null) {
				centerPanel.removeMouseListener(ml);
				centerPanel.addMouseListener(ml);
				centerPanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
		}
	}

	class SearchListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String searched = searchField.getText();

			for (String str : placesNameKey.keySet()) {
				if (str.equals(searched)) {
					for (NamedPlace n : placesNameKey.get(str)) {
						n.setMarked(true);
						n.setVisible(true);
					}
				}
			}
			centerPanel.repaint();
		}
	}

	class HidePlaceListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (String str : placesNameKey.keySet()) {
				for (NamedPlace n : placesNameKey.get(str)) {
					if (n.isMarked()) {
						n.setMarked(false);
						n.setVisible(false);
					}
				}
			}
			saved = false;
			pack();
			validate();
			repaint();
		}
	}

	class DeletePlaceListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Iterator<String> nameIt = placesNameKey.keySet().iterator();
			Iterator<String> catIt = placesCatKey.keySet().iterator();
			Iterator<Position> placeIt = placesPosKey.keySet().iterator();
			try {
				while (nameIt.hasNext()) {
					String str = nameIt.next();
					for (NamedPlace n : placesNameKey.get(str)) {
						if (n.isMarked()) {
							nameIt.remove();
						}
					}
				}

				while (catIt.hasNext()) {
					String str = catIt.next();
					for (NamedPlace n : placesCatKey.get(str)) {
						if (n.isMarked()) {
							catIt.remove();
						}
					}
				}

				while (placeIt.hasNext()) {
					Position pos = placeIt.next();
					for (NamedPlace n : placesPosKey.get(pos)) {
						if (n.isMarked()) {
							placeIt.remove();
							centerPanel.remove(n);
						}
					}
				}

			} catch (IllegalStateException ise) {
				actionPerformed(e);
			} catch (ConcurrentModificationException cme) {
				return;
			}
			saved = false;
			pack();
			validate();
			repaint();
		}
	}

	class WhatListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (centerPanel != null) {
				centerPanel.removeMouseListener(ml);
				centerPanel.removeMouseListener(wm);
				centerPanel.addMouseListener(wm);
				centerPanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
		}
	}

	class WhatMouse extends MouseAdapter {
		public void mouseClicked(MouseEvent mev) {
			
			for (Object o : placesPosKey.keySet().toArray()) {
				Position pos = (Position) o;
				for (int x = mev.getX() - 7 ; x < mev.getX() + 7; x++) {
					if (x == pos.getX()) {
						for (int y = mev.getY() - 7; y < mev.getY() + 7; y++) {
							if (y == pos.getY()) {
								for (NamedPlace n : placesPosKey.get(pos)) {
									n.setVisible(true);
									break;
								}
							} 
						} // second for
					} 
				} // first for
			} // enhanced for
			centerPanel.repaint();
			centerPanel.validate();
			centerPanel.setCursor(Cursor.getDefaultCursor());
			centerPanel.removeMouseListener(wm);
		}
	}

	class CatListSelect implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent lsv) {
			List<String> showThese = catList.getSelectedValuesList();


			for (String showStr : showThese) {
				for (String cat : placesCatKey.keySet()) {
					if (showStr.equals(cat)) {
						for (Map.Entry<String, Set<NamedPlace>> entry : placesCatKey
								.entrySet()) {
							for (NamedPlace n : entry.getValue()) {
								if (n.getCategory().equals(showStr)) {
									n.setVisible(true);
								}
							}
						}
					}
				}
			}
		}
	}

	class HideCatListen implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			List<String> hideThese = catList.getSelectedValuesList();

			for (String hideStr : hideThese) {
				for (String cat : placesCatKey.keySet()) {
					if (hideStr.equals(cat)) {
						for (Map.Entry<String, Set<NamedPlace>> entry : placesCatKey
								.entrySet()) {
							for (NamedPlace n : entry.getValue()) {
								if (n.getCategory().equals(hideStr)) {
									n.setVisible(false);
									n.setMarked(false);
								} // if
							} 	// Fourth for
						} // Third for
					} // if
				} // Second for
			} // First for
		} // actionPerformed
	} // HideCatListen

	class NewCategory implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int pressed = JOptionPane.showConfirmDialog(ProgramUI.this,
					catPanel(), "New Category", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);

			if (pressed != JOptionPane.OK_OPTION) {
				return;
			}

			if (!catField.getText().isEmpty()) {
				categories.put(catField.getText(), colChoose.getColor());
				catModel.addElement(catField.getText());

			} else {
				JOptionPane.showMessageDialog(ProgramUI.this,
						"You need to enter a name.", "Wrong input",
						JOptionPane.OK_OPTION);
				actionPerformed(e);
			}
			saved = false;
		}
	}

	class RemoveCat implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (catList.getSelectedValue() != null) {
				String catKey = catList.getSelectedValue();
				int index = catList.getSelectedIndex();
				int pressed = JOptionPane.showConfirmDialog(ProgramUI.this,
						"Are you sure you want to remove category: " + catKey
								+ "?", "Category removal",
						JOptionPane.OK_CANCEL_OPTION);
				if (pressed != JOptionPane.OK_OPTION) {
					return;
				} else {
					categories.remove(catKey);
					catModel.removeElementAt(index);
				}
				saved = false;
			}
		}
	}

	class MapListen extends MouseAdapter {
		public void mouseClicked(MouseEvent mev) {
			int x = mev.getX();
			int y = mev.getY();
			int pressed = -1;
			String[] options = { "OK", "Cancel" };

			try {
				if (catList.getSelectedValues().length > 1) {
					JOptionPane.showMessageDialog(ProgramUI.this,
							"You have chosen more than one category.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (placeBox.getSelectedIndex() == 1) {

						pressed = JOptionPane.showOptionDialog(ProgramUI.this,
								describedPanel(), "New Described Place",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, options,
								placeName);

						String name = placeName.getText();
						String desc = placeDesc.getText();
						String category = catList.getSelectedValue();
						if (category == null) {
							category = "";
						}
						Position p = new Position(x, y);
						DescribedPlace d = new DescribedPlace(p, name,
								category, (category.equals("") ? Color.BLACK
										: categories.get(category)), desc);
						Set<NamedPlace> old = placesPosKey.get(p);
						Set<NamedPlace> old1 = placesNameKey.get(name);
						Set<NamedPlace> old2 = placesCatKey.get(category);
						if (old == null) {
							old = new HashSet<NamedPlace>();
							placesPosKey.put(p, old);
						}
						old.add(d);
						if (old1 == null) {
							old1 = new HashSet<NamedPlace>();
							placesNameKey.put(name, old1);
						}
						old1.add(d);
						if (old2 == null) {
							old2 = new HashSet<NamedPlace>();
							placesCatKey.put(category, old2);
						}
						old2.add(d);

						if (pressed == JOptionPane.OK_OPTION) {
							centerPanel.add(d);
							centerPanel.repaint();
						}
					} else if (placeBox.getSelectedIndex() == 2) {

						pressed = JOptionPane.showOptionDialog(ProgramUI.this,
								namedPanel(), "New Named Place",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, options,
								placeName);

						String name = placeName.getText();
						String category = catList.getSelectedValue();
						if (category == null) {
							category = "";
						}
						Position p = new Position(x, y);

						NamedPlace n = new NamedPlace(p, name, category,
								(category.equals("") ? Color.BLACK
										: categories.get(category)));

						Set<NamedPlace> old = placesPosKey.get(p);
						Set<NamedPlace> old1 = placesNameKey.get(name);
						Set<NamedPlace> old2 = placesCatKey.get(category);
						if (old == null) {
							old = new HashSet<NamedPlace>();
							placesPosKey.put(p, old);
						}
						old.add(n);
						if (old1 == null) {
							old1 = new HashSet<NamedPlace>();
							placesNameKey.put(name, old1);
						}
						old1.add(n);
						if (old2 == null) {
							old2 = new HashSet<NamedPlace>();
							placesCatKey.put(category, old2);
						}
						old2.add(n);

						if (pressed == JOptionPane.OK_OPTION) {
							centerPanel.add(n);
							centerPanel.repaint();
						}
					}
				}

			} catch (NullPointerException e) {
				return;
			}
			centerPanel.removeMouseListener(ml);
			centerPanel.setCursor(Cursor.getDefaultCursor());
			saved = false;
		}
	}

	public JPanel catPanel() {
		JPanel catPanel = new JPanel(new BorderLayout());
		colChoose = new JColorChooser();
		JLabel catLabel = new JLabel("Name: ");
		catField = new JTextField(20);
		JPanel topPanel = new JPanel();
		catPanel.setLayout(new BoxLayout(catPanel, BoxLayout.Y_AXIS));
		topPanel.add(catLabel);
		topPanel.add(catField);
		topPanel.add(Box.createHorizontalStrut(300));
		catPanel.add(topPanel, BorderLayout.NORTH);
		catPanel.add(colChoose, BorderLayout.CENTER);
		return catPanel;
	}

	public JPanel describedPanel() {
		JPanel describedPanel = new JPanel(new BorderLayout());
		JPanel top = new JPanel();
		placeName = new JTextField(20);
		top.add(new JLabel("Name: "));
		top.add(placeName);
		describedPanel.add(top, BorderLayout.NORTH);
		JPanel bot = new JPanel(new BorderLayout());
		placeDesc = new JTextArea(20, 20);
		placeDesc.setLineWrap(true);
		placeDesc.setWrapStyleWord(true);
		bot.add(new JLabel("Description: "), BorderLayout.NORTH);
		bot.add(placeDesc, BorderLayout.SOUTH);
		describedPanel.add(bot, BorderLayout.SOUTH);
		return describedPanel;
	}

	public JPanel namedPanel() {
		JPanel namedPanel = new JPanel(new BorderLayout());
		JPanel top = new JPanel();
		placeName = new JTextField(20);
		top.add(new JLabel("Name: "));
		top.add(placeName);
		namedPanel.add(top, BorderLayout.NORTH);
		return namedPanel;
	}

	public static void main(String[] args) {
		new ProgramUI();
	}
}
