package mevenk.xmltojava;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import mevenk.xmltoxsd.XSDGenerator;
import mevenk.xsdtojava.XJCProcessExecutor;

public class XMLToJava {

	public static final String lineSeparator = System.lineSeparator();

	private JFrame frame;
	private JButton convertButton;

	private JFileChooser xmlFileChooser = new JFileChooser();
	private JFileChooser javaFilesDirFileChooser = new JFileChooser();
	private JLabel selextedXMLFileLabel = new JLabel("");
	private JLabel selectedDirForJavaFilesLabel = new JLabel("");

	private File xmlFileSelected;
	private File xsdFileFromXML;
	private File selectedDirForJavaFiles;

	private static JTextPane resultTextPane = new JTextPane();
	private JLabel enterPackageNameLabel;
	private JTextField packageNameTextField;
	
	JPopupMenu packageNameTextFieldContextMenu = new JPopupMenu();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {

			PrintStream resultTextPanePrintStream = new PrintStream(new ResultTextPaneOutputStream(resultTextPane),
					true);
			System.setOut(resultTextPanePrintStream);
			System.setErr(resultTextPanePrintStream);
			System.out.println();

			XMLToJava window = new XMLToJava();
			window.frame.setVisible(true);

		} catch (Exception exception) {
			JOptionPane.showMessageDialog(null, "Error!!" + lineSeparator + exception.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Create the application.
	 */
	public XMLToJava() {

		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 600, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screenDimension.width / 2 - frame.getSize().width / 2,
				screenDimension.height / 2 - frame.getSize().height / 2);

		convertButton = new JButton("Convert");
		convertButton.setBounds(240, 165, 120, 40);
		convertButton.setLocation(frame.getSize().width / 2 - convertButton.getSize().width / 2, 165);
		convertButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		convertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

				boolean valid = true;

				try {

					resultTextPane.setText("");

					if (xmlFileSelected == null || !xmlFileSelected.exists()) {
						System.out.println("XML File required !!");
						valid = false;
					} else {
						System.out.println("XSD File : " + xmlFileSelected.getPath());
					}

					if (selectedDirForJavaFiles == null || !selectedDirForJavaFiles.exists()) {
						System.out.println("Dir for Java Files required !!");
						valid = false;
					} else {
						System.out.println("Dir path : " + selectedDirForJavaFiles.getPath());
					}

					if (packageNameTextField.getText().length() == 0) {
						System.out.println("package required !!");
						valid = false;
					} else {
						System.out.println("package : " + packageNameTextField.getText());
					}

					if (!valid) {
						//frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return;
					}

					xsdFileFromXML = new File(xmlFileSelected.getParent() + File.separator
							+ xmlFileSelected.getName().replaceFirst("[.][^.]+$", "") + ".xsd");

					if (xsdFileFromXML.exists()) {
						System.out.println(lineSeparator + "Deleting existing XSD(" + xsdFileFromXML.getPath() + ")");
						xsdFileFromXML.delete();
					}

					boolean xsdFileGenerated = new XSDGenerator().generateXSD(xmlFileSelected, xsdFileFromXML);

					if (!xsdFileGenerated) {
						System.out.println("XSD Generation Failure");
						//frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return;

					} else {
						System.out.println("XSD Generation Success");
					}

					if (selectedDirForJavaFiles.listFiles().length != 0) {
						System.out.println(
								lineSeparator + "Deleting " + selectedDirForJavaFiles.getPath() + lineSeparator);
						selectedDirForJavaFiles.delete();
						selectedDirForJavaFiles.mkdir();
					}

					XJCProcessExecutor xJCProcessExecutor = new XJCProcessExecutor();
					xJCProcessExecutor.executeXJCCommand(selectedDirForJavaFiles.getPath(),
							packageNameTextField.getText(), xsdFileFromXML.getPath());

				} catch (Exception exception) {
					exception.printStackTrace();
					//frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

				//frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			}
		});
		frame.getContentPane().add(convertButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 220, 570, 225);
		frame.getContentPane().add(scrollPane);

		scrollPane.setViewportView(resultTextPane);
		resultTextPane.setEditable(false);
		resultTextPane.setFont(new Font("Consolas", Font.BOLD, 12));
		resultTextPane.setForeground(Color.WHITE);
		resultTextPane.setBackground(Color.BLACK);

		JButton selectXMLFileButton = new JButton("<html><center>Select XML File</center></html>");
		selectXMLFileButton.setToolTipText("Select XML File");
		selectXMLFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				xmlFileChooser = new JFileChooser();
				xmlFileChooser.setMultiSelectionEnabled(false);
				xmlFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter xmlFileNameExtensionFilter = new FileNameExtensionFilter("XML Files", "xml");
				xmlFileChooser.setFileFilter(xmlFileNameExtensionFilter);

				int xsdFileChooserReturnVal = xmlFileChooser.showOpenDialog(frame);
				if (xsdFileChooserReturnVal == JFileChooser.APPROVE_OPTION) {
					xmlFileSelected = xmlFileChooser.getSelectedFile();
					selextedXMLFileLabel.setText(xmlFileSelected.getName());
					selextedXMLFileLabel.setToolTipText(xmlFileSelected.getPath());
				}

			}
		});
		selectXMLFileButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		selectXMLFileButton.setBounds(25, 11, 250, 40);
		frame.getContentPane().add(selectXMLFileButton);

		JButton selectDirForJavaFilesButton = new JButton(
				"<html><center>Select Directory to save <br />Java classes</center></html>");
		selectDirForJavaFilesButton.setToolTipText("Select Directory to save Java classes");
		selectDirForJavaFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				javaFilesDirFileChooser = new JFileChooser();
				javaFilesDirFileChooser.setMultiSelectionEnabled(false);
				javaFilesDirFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				javaFilesDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int dirForJavaClassesFileChooserReturnVal = javaFilesDirFileChooser.showOpenDialog(frame);
				if (dirForJavaClassesFileChooserReturnVal == JFileChooser.APPROVE_OPTION) {
					selectedDirForJavaFiles = javaFilesDirFileChooser.getSelectedFile();
					selectedDirForJavaFilesLabel.setText(selectedDirForJavaFiles.getName());
					selectedDirForJavaFilesLabel.setToolTipText(selectedDirForJavaFiles.getPath());
				}

			}
		});
		selectDirForJavaFilesButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		selectDirForJavaFilesButton.setBounds(25, 62, 250, 40);
		frame.getContentPane().add(selectDirForJavaFilesButton);

		selextedXMLFileLabel = new JLabel("");
		selextedXMLFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selextedXMLFileLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		selextedXMLFileLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
		selextedXMLFileLabel.setBounds(310, 11, 260, 40);
		frame.getContentPane().add(selextedXMLFileLabel);

		selectedDirForJavaFilesLabel = new JLabel("");
		selectedDirForJavaFilesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selectedDirForJavaFilesLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		selectedDirForJavaFilesLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
		selectedDirForJavaFilesLabel.setBounds(310, 62, 260, 40);
		frame.getContentPane().add(selectedDirForJavaFilesLabel);

		enterPackageNameLabel = new JLabel();
		enterPackageNameLabel.setToolTipText("Enter package name");
		enterPackageNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		enterPackageNameLabel.setText("Enter package name");
		enterPackageNameLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		enterPackageNameLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		enterPackageNameLabel.setBounds(25, 114, 250, 40);
		frame.getContentPane().add(enterPackageNameLabel);

		packageNameTextField = new JTextField();
		packageNameTextField.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				String packageNameTextFieldtext = packageNameTextField.getText();
				String clipboardString;
				try {
					clipboardString = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
							.getData(DataFlavor.stringFlavor);

					if (packageNameTextFieldtext != null && packageNameTextFieldtext.length() > 0
							&& clipboardString.equalsIgnoreCase(packageNameTextFieldtext)) {
						packageNameTextField.setToolTipText("Copied to Clipboard");
					} else {
						packageNameTextField.setToolTipText(null);
					}

				} catch (Exception exception) {
					packageNameTextField.setToolTipText("Clipboard copy/pase not working !");
				}

			}
		});
		packageNameTextField.setFont(new Font("Source Code Pro Semibold", Font.PLAIN, 12));
		packageNameTextField.setBounds(310, 113, 260, 41);
		frame.getContentPane().add(packageNameTextField);
		packageNameTextField.setColumns(50);

		packageNameTextFieldContextMenu = new JPopupMenu();
		addPopup(packageNameTextField, packageNameTextFieldContextMenu);

		JButton packageNameTextFieldContextMenuCopyButton = new JButton("Copy");
		packageNameTextFieldContextMenuCopyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String packageNameTextFieldtext = packageNameTextField.getText();

				if (packageNameTextFieldtext != null && packageNameTextFieldtext.length() > 0) {

					StringSelection packageNameTextFieldTextStringSelection = new StringSelection(
							packageNameTextFieldtext);
					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(packageNameTextFieldTextStringSelection, null);
					packageNameTextField.setToolTipText("Copied to Clipboard");
				}

				if (packageNameTextFieldContextMenu.isShowing()) {
					packageNameTextFieldContextMenu.setVisible(false);
				}
			}
		});
		packageNameTextFieldContextMenu.add(packageNameTextFieldContextMenuCopyButton);

		JButton packageNameTextFieldContextMenuPasteButton = new JButton("Paste");
		packageNameTextFieldContextMenuPasteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String clipboardString;
				try {
					clipboardString = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
							.getData(DataFlavor.stringFlavor);
					packageNameTextField.setText(clipboardString);
				} catch (Exception exception) {
					packageNameTextField.setToolTipText("Clipboard copy/pase not working !");
				}

				if (packageNameTextFieldContextMenu.isShowing()) {
					packageNameTextFieldContextMenu.setVisible(false);
				}
			}
		});
		packageNameTextFieldContextMenu.add(packageNameTextFieldContextMenuPasteButton);

	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}

class ResultTextPaneOutputStream extends OutputStream {
	JTextPane resultTextPane;
	Document resultPaneDocument;

	public ResultTextPaneOutputStream(JTextPane resultTextPane) {
		this.resultTextPane = resultTextPane;
		this.resultPaneDocument = resultTextPane.getDocument();
	}

	@Override
	public void write(int b) throws IOException {
		try {
			resultPaneDocument.insertString(resultPaneDocument.getLength(), String.valueOf((char) b), null);
			resultTextPane.setCaretPosition(resultTextPane.getDocument().getLength());
		} catch (BadLocationException badLocationException) {
			badLocationException.printStackTrace();
		}

	}
}
