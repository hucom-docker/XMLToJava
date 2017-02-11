package mevenk.xmltojava;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;

public class XMLToJava {

	final String lineSeparator = System.lineSeparator();

	private JFrame frame;
	private JFileChooser xsdFileChooser = new JFileChooser();
	private JFileChooser xsdClassesDirFileChooser = new JFileChooser();
	JLabel selextedXSDFileLabel = new JLabel("");
	JLabel selextedDirForJavaFilesLabel = new JLabel("");

	File xsdFileSelected;
	File xsdClassesDir;
	String packageName;

	private JTextPane resultTextPane;
	private JLabel enterPackageNameLabel;
	private JTextField packageNameTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					XMLToJava window = new XMLToJava();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
		frame.setBounds(100, 100, 601, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screenDimension.width / 2 - frame.getSize().width / 2,
				screenDimension.height / 2 - frame.getSize().height / 2);

		JButton convertButton = new JButton("Convert");
		convertButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		convertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Document document = resultTextPane.getDocument();
				boolean valid = true;

				try {

					resultTextPane.setText("");

					if (selextedXSDFileLabel.getText().length() == 0) {

						document.insertString(document.getLength(), lineSeparator + "XSD File required !!", null);
						valid = false;
					} else {
						document.insertString(document.getLength(),
								lineSeparator + "XSD File : " + selextedXSDFileLabel.getText(), null);
					}

					if (selextedDirForJavaFilesLabel.getText().length() == 0) {

						document.insertString(document.getLength(), lineSeparator + "Dir for Java Files required !!",
								null);
						valid = false;
					} else {
						document.insertString(document.getLength(),
								lineSeparator + "Dir path : " + selextedDirForJavaFilesLabel.getText(), null);
					}

					if (packageNameTextField.getText().length() == 0) {

						document.insertString(document.getLength(), lineSeparator + "package required !!", null);
						valid = false;
					} else {
						document.insertString(document.getLength(),
								lineSeparator + "package : " + packageNameTextField.getText(), null);
					}

					if (!valid) {
						return;
					}

					if (xsdClassesDir.listFiles().length != 0) {
						xsdClassesDir.delete();
						document.insertString(document.getLength(),
								lineSeparator + "Deleting " + xsdClassesDir.getPath(), null);
						xsdClassesDir.mkdir();
					}

					Runtime runtime = Runtime.getRuntime();

					Process xjcProcess = runtime.exec(new String[] { "xjc", "-d", xsdClassesDir.getAbsolutePath(), "-p",
							packageName, xsdFileSelected.getAbsolutePath() });
					
					int exitValue = xjcProcess.waitFor();
					
					BufferedReader xjcCommandBufferedReader = new BufferedReader(
							new InputStreamReader(xjcProcess.getInputStream()));
					String commandOutputLine = "";
					while ((commandOutputLine = xjcCommandBufferedReader.readLine()) != null) {
						document.insertString(document.getLength(), lineSeparator + commandOutputLine, null);
					}
					
					
					BufferedReader xjcCommandErrorBufferedReader = new BufferedReader(
							new InputStreamReader(xjcProcess.getErrorStream()));
					while ((commandOutputLine = xjcCommandErrorBufferedReader.readLine()) != null) {
						document.insertString(document.getLength(), lineSeparator + commandOutputLine, null);
					}
					

					if (exitValue == 0) {
						document.insertString(document.getLength(), lineSeparator + "SUCCESS", null);
					} else {
						document.insertString(document.getLength(), lineSeparator + "ERROR - Exit value : " + exitValue, null);
					}

				} catch (Exception exception) {exception.printStackTrace();
					try {
						StringWriter stringWriter = new StringWriter();
						exception.printStackTrace(new PrintWriter(stringWriter));
						document.insertString(document.getLength(),
								lineSeparator + "Error !! " + lineSeparator + stringWriter.toString(), null);
					} catch (BadLocationException badLocationException) {
						JOptionPane.showMessageDialog(frame, badLocationException.toString(), "Error!!",
								JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		});
		convertButton.setBounds(240, 165, 120, 40);
		frame.getContentPane().add(convertButton);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 220, 570, 180);
		frame.getContentPane().add(scrollPane);

		resultTextPane = new JTextPane();
		scrollPane.setViewportView(resultTextPane);
		resultTextPane.setEditable(false);
		resultTextPane.setFont(new Font("Consolas", Font.BOLD, 12));
		resultTextPane.setForeground(Color.WHITE);
		resultTextPane.setBackground(Color.BLACK);

		JButton selectXSDFileButton = new JButton("<html><center>Select XSD File</center></html>");
		selectXSDFileButton.setToolTipText("Select XSD File");
		selectXSDFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				xsdFileChooser = new JFileChooser();
				xsdFileChooser.setMultiSelectionEnabled(false);
				xsdFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter xsdFileNameExtensionFilter = new FileNameExtensionFilter("XSD Files", "xsd");
				xsdFileChooser.setFileFilter(xsdFileNameExtensionFilter);

				int xsdFileChooserReturnVal = xsdFileChooser.showOpenDialog(frame);
				if (xsdFileChooserReturnVal == JFileChooser.APPROVE_OPTION) {
					xsdFileSelected = xsdFileChooser.getSelectedFile();
					try {
						selextedXSDFileLabel.setText(xsdFileSelected.getPath());
					} catch (Exception ex) {
						ex.printStackTrace(System.err);
					}
				}

			}
		});
		selectXSDFileButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		selectXSDFileButton.setBounds(25, 11, 250, 40);
		frame.getContentPane().add(selectXSDFileButton);

		JButton selectDirForJavaFilesButton = new JButton(
				"<html><center>Select Directory to save <br />Java classes</center></html>");
		selectDirForJavaFilesButton.setToolTipText("Select Directory to save Java classes");
		selectDirForJavaFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				xsdClassesDirFileChooser = new JFileChooser();
				xsdClassesDirFileChooser.setMultiSelectionEnabled(false);
				xsdClassesDirFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				xsdClassesDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int dirForJavaClassesFileChooserReturnVal = xsdClassesDirFileChooser.showOpenDialog(frame);
				if (dirForJavaClassesFileChooserReturnVal == JFileChooser.APPROVE_OPTION) {
					xsdClassesDir = xsdClassesDirFileChooser.getSelectedFile();
					try {
						selextedDirForJavaFilesLabel.setText(xsdClassesDir.getPath());
					} catch (Exception ex) {
						ex.printStackTrace(System.err);
					}
				}

			}
		});
		selectDirForJavaFilesButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		selectDirForJavaFilesButton.setBounds(25, 62, 250, 40);
		frame.getContentPane().add(selectDirForJavaFilesButton);

		selextedXSDFileLabel = new JLabel("");
		selextedXSDFileLabel.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent event) {
			}

			public void inputMethodTextChanged(InputMethodEvent event) {
			}
		});
		selextedXSDFileLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		selextedXSDFileLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
		selextedXSDFileLabel.setBounds(310, 11, 260, 40);
		frame.getContentPane().add(selextedXSDFileLabel);

		selextedDirForJavaFilesLabel = new JLabel("");
		selextedDirForJavaFilesLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		selextedDirForJavaFilesLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 11));
		selextedDirForJavaFilesLabel.setBounds(310, 62, 260, 40);
		frame.getContentPane().add(selextedDirForJavaFilesLabel);

		enterPackageNameLabel = new JLabel();
		enterPackageNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		enterPackageNameLabel.setText("Enter package name");
		enterPackageNameLabel.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		enterPackageNameLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		enterPackageNameLabel.setBounds(25, 114, 250, 40);
		frame.getContentPane().add(enterPackageNameLabel);

		packageNameTextField = new JTextField();
		packageNameTextField.setFont(new Font("Source Code Pro Semibold", Font.PLAIN, 12));
		packageNameTextField.setBounds(310, 113, 260, 41);
		frame.getContentPane().add(packageNameTextField);
		packageNameTextField.setColumns(50);

	}
}
