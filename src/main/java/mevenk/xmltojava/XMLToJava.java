package mevenk.xmltojava;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import java.awt.Window.Type;

public class XMLToJava {

	public static final String LINE_SEPARATOR = System.lineSeparator();

	private static final String FILE_SEPARATOR = File.separator;

	private JFrame frmXmlToJava;
	private JButton convertButton;

	private JFileChooser xmlFileChooser = new JFileChooser();
	private JFileChooser javaFilesDirFileChooser = new JFileChooser();
	private JLabel selextedXMLFileLabel = new JLabel("");
	private JLabel selectedDirForJavaFilesLabel = new JLabel("");

	private File xmlFileSelected;
	private File xsdFileFromXML;
	private File selectedDirectoryToSaveFiles;

	private static JTextPane resultTextPane = new JTextPane();
	private JLabel enterPackageNameLabel;
	private JTextField packageNameTextField;

	JPopupMenu packageNameTextFieldContextMenu = new JPopupMenu();
	JPopupMenu resultTextPaneContextMenu = new JPopupMenu();

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
			System.out.println("Select Empty Directory to save Files");
			System.out.println("Will be replaced if not empty !!");
			System.out.println();

			XMLToJava window = new XMLToJava();
			window.frmXmlToJava.setVisible(true);

		} catch (Exception exception) {
			JOptionPane.showMessageDialog(null, "Error!!" + LINE_SEPARATOR + exception.getMessage(), "Error",
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
		frmXmlToJava = new JFrame();
		frmXmlToJava.setIconImage(Toolkit.getDefaultToolkit().getImage(XMLToJava.class.getResource("/mevenk/image/mevenkGitHubLogo.png")));
		frmXmlToJava.setType(Type.UTILITY);
		frmXmlToJava.setTitle("XML To JAVA");
		frmXmlToJava.setResizable(false);
		frmXmlToJava.setBounds(100, 100, 600, 500);
		frmXmlToJava.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmXmlToJava.getContentPane().setLayout(null);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		frmXmlToJava.setLocation(screenDimension.width / 2 - frmXmlToJava.getSize().width / 2,
				screenDimension.height / 2 - frmXmlToJava.getSize().height / 2);

		convertButton = new JButton("Convert");
		convertButton.setBounds(240, 165, 120, 40);
		convertButton.setLocation(frmXmlToJava.getSize().width / 2 - convertButton.getSize().width / 2, 165);
		convertButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		convertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

				boolean valid = true;
				boolean xsdFileGenerated = false;

				try {

					resultTextPane.setText("");

					if (xmlFileSelected == null || !xmlFileSelected.exists()) {
						System.out.println("XML File required !!");
						valid = false;
					} else {
						System.out.println("XML File : " + xmlFileSelected.getPath());
					}

					if (selectedDirectoryToSaveFiles == null || !selectedDirectoryToSaveFiles.exists()) {
						System.out.println("Dir for Java Files required !!");
						valid = false;
					} else {
						System.out.println("Dir path : " + selectedDirectoryToSaveFiles.getPath());
					}

					if (packageNameTextField.getText().length() == 0) {
						System.out.println("package required !!");
						valid = false;
					} else {
						System.out.println("package : " + packageNameTextField.getText());
					}

					if (!valid) {
						// frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return;
					}

					xsdFileFromXML = new File(selectedDirectoryToSaveFiles + FILE_SEPARATOR
							+ xmlFileSelected.getName().replaceFirst("[.][^.]+$", "") + ".xsd");

					if (selectedDirectoryToSaveFiles.listFiles().length != 0) {
						System.out.println(LINE_SEPARATOR + "Selected Directory not Empty" + LINE_SEPARATOR
								+ "Deleting " + selectedDirectoryToSaveFiles.getPath() + LINE_SEPARATOR);
						selectedDirectoryToSaveFiles.delete();
						selectedDirectoryToSaveFiles.mkdir();
					}

					xsdFileGenerated = new XSDGenerator().generateXSD(xmlFileSelected, xsdFileFromXML);

					if (!xsdFileGenerated) {
						System.out.println(LINE_SEPARATOR + "XSD Generation Failure" + LINE_SEPARATOR);
						// frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						return;

					} else {
						System.out.println(LINE_SEPARATOR + "XSD Generation Success" + LINE_SEPARATOR);
					}

					new XJCProcessExecutor().executeXJCCommand(selectedDirectoryToSaveFiles.getPath(),
							packageNameTextField.getText(), xsdFileFromXML.getPath());

				} catch (Exception exception) {

					if (!xsdFileGenerated) {
						System.out.println(LINE_SEPARATOR + "XSD Generation Failure" + LINE_SEPARATOR);
						// frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					}
					exception.printStackTrace();
					// frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}

				// frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			}
		});
		frmXmlToJava.getContentPane().add(convertButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 220, 570, 225);
		frmXmlToJava.getContentPane().add(scrollPane);

		scrollPane.setViewportView(resultTextPane);
		resultTextPane.setEditable(false);
		resultTextPane.setFont(new Font("Consolas", Font.BOLD, 12));
		resultTextPane.setForeground(Color.WHITE);
		resultTextPane.setBackground(Color.BLACK);

		JButton resultTextPaneContextMenuCopyButton = new JButton(" Copy To Clipboard ");
		resultTextPaneContextMenuCopyButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				String resultTextPaneText = resultTextPane.getText();

				if (resultTextPaneText != null && resultTextPaneText.length() > 0) {

					StringSelection resultTextPaneTextStringSelection = new StringSelection(resultTextPaneText);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(resultTextPaneTextStringSelection,
							null);
				}

				if (resultTextPaneContextMenu.isShowing()) {
					resultTextPaneContextMenu.setVisible(false);
				}
			}

		});

		resultTextPaneContextMenu.add(resultTextPaneContextMenuCopyButton);
		addPopup(resultTextPane, resultTextPaneContextMenu);

		JButton selectXMLFileButton = new JButton("<html><center>Select XML File</center></html>");
		selectXMLFileButton.setToolTipText("Select XML File");
		selectXMLFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				xmlFileChooser = new JFileChooser();
				xmlFileChooser.setMultiSelectionEnabled(false);
				xmlFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter xmlFileNameExtensionFilter = new FileNameExtensionFilter("XML Files", "xml");
				xmlFileChooser.setFileFilter(xmlFileNameExtensionFilter);

				int xsdFileChooserReturnVal = xmlFileChooser.showOpenDialog(frmXmlToJava);
				if (xsdFileChooserReturnVal == JFileChooser.APPROVE_OPTION) {
					xmlFileSelected = xmlFileChooser.getSelectedFile();
					selextedXMLFileLabel.setText(xmlFileSelected.getName());
					selextedXMLFileLabel.setToolTipText(xmlFileSelected.getPath());
				}

			}
		});
		selectXMLFileButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		selectXMLFileButton.setBounds(25, 11, 250, 40);
		frmXmlToJava.getContentPane().add(selectXMLFileButton);

		JButton selectDirForJavaFilesButton = new JButton(
				"<html><center>Select Directory to save <br />Files</center></html>");
		selectDirForJavaFilesButton.setToolTipText("Select Directory to save Java classes");
		selectDirForJavaFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				javaFilesDirFileChooser = new JFileChooser();
				javaFilesDirFileChooser.setMultiSelectionEnabled(false);
				javaFilesDirFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				javaFilesDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int dirForJavaClassesFileChooserReturnVal = javaFilesDirFileChooser.showOpenDialog(frmXmlToJava);
				if (dirForJavaClassesFileChooserReturnVal == JFileChooser.APPROVE_OPTION) {
					selectedDirectoryToSaveFiles = javaFilesDirFileChooser.getSelectedFile();
					selectedDirForJavaFilesLabel.setText(selectedDirectoryToSaveFiles.getName());
					selectedDirForJavaFilesLabel.setToolTipText(selectedDirectoryToSaveFiles.getPath());
				}

			}
		});
		selectDirForJavaFilesButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		selectDirForJavaFilesButton.setBounds(25, 62, 250, 40);
		frmXmlToJava.getContentPane().add(selectDirForJavaFilesButton);

		selextedXMLFileLabel = new JLabel("");
		selextedXMLFileLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selextedXMLFileLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		selextedXMLFileLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
		selextedXMLFileLabel.setBounds(310, 11, 260, 40);
		frmXmlToJava.getContentPane().add(selextedXMLFileLabel);

		selectedDirForJavaFilesLabel = new JLabel("");
		selectedDirForJavaFilesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		selectedDirForJavaFilesLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		selectedDirForJavaFilesLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
		selectedDirForJavaFilesLabel.setBounds(310, 62, 260, 40);
		frmXmlToJava.getContentPane().add(selectedDirForJavaFilesLabel);

		enterPackageNameLabel = new JLabel();
		enterPackageNameLabel.setToolTipText("Enter package name");
		enterPackageNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		enterPackageNameLabel.setText("Enter package name");
		enterPackageNameLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		enterPackageNameLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		enterPackageNameLabel.setBounds(25, 114, 250, 40);
		frmXmlToJava.getContentPane().add(enterPackageNameLabel);

		packageNameTextField = new JTextField();
		packageNameTextField.setFont(new Font("Source Code Pro Semibold", Font.PLAIN, 12));
		packageNameTextField.setBounds(310, 113, 260, 41);
		frmXmlToJava.getContentPane().add(packageNameTextField);
		packageNameTextField.setColumns(50);

		packageNameTextFieldContextMenu = new JPopupMenu();
		addPopup(packageNameTextField, packageNameTextFieldContextMenu);

		JButton packageNameTextFieldContextMenuCopyButton = new JButton(" Copy  ");
		packageNameTextFieldContextMenuCopyButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String packageNameTextFieldtext = packageNameTextField.getText();

				if (packageNameTextFieldtext != null && packageNameTextFieldtext.length() > 0) {

					StringSelection packageNameTextFieldTextStringSelection = new StringSelection(
							packageNameTextFieldtext);
					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(packageNameTextFieldTextStringSelection, null);
				}

				if (packageNameTextFieldContextMenu.isShowing()) {
					packageNameTextFieldContextMenu.setVisible(false);
				}
			}
		});
		packageNameTextFieldContextMenu.add(packageNameTextFieldContextMenuCopyButton);

		JButton packageNameTextFieldContextMenuPasteButton = new JButton(" Paste ");
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
