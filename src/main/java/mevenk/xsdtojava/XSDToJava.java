/**
 * 
 */
package mevenk.xsdtojava;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author VENKATESH
 *
 */
public class XSDToJava {

	static String dirForJavaClasses;
	static String packageName;
	static String XSDFilePath;

	static JFrame xjcFrame;
	static JTextArea xjcResponseTextArea = new JTextArea();

	/**
	 * @return the dirForJavaClasses
	 */
	public String getDirForJavaClasses() {
		return dirForJavaClasses;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the xSDFilePath
	 */
	public String getXSDFilePath() {
		return XSDFilePath;
	}

	/**
	 * @param xSDFilePath
	 *            the xSDFilePath to set
	 */
	public void setXSDFilePath(String xSDFilePath) {
		XSDFilePath = xSDFilePath;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//showGUI();
		
		
		try {
			Object[] xjcReturnObjectArray = XJRProcessExecutor.executeXJRCommand("E:\\work\\temporary\\xsdConverter\\employeexsdclasses", "mevenk.jaxb.beans",
					"E:\\work\\temporary\\xsdConverter\\employee.xsd");
			System.out.println((String) xjcReturnObjectArray[1]);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	static void showGUI() {

		xjcFrame = new JFrame();

		JLabel selectDirForJavaClassesLabel = new JLabel("Select Folder fot JAVA classes");

		JLabel packageNameForJavaClassesLabel = new JLabel("Select Folder fot JAVA classes");

		JFileChooser xsdFileChooser = new JFileChooser();
		xsdFileChooser.setMultiSelectionEnabled(false);
		xsdFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter xsdFileNameExtensionFilter = new FileNameExtensionFilter("XSD FILES", "xsd");
		xsdFileChooser.setFileFilter(xsdFileNameExtensionFilter);

		JFileChooser dirForJavaClassesjFileChooser = new JFileChooser();
		dirForJavaClassesjFileChooser.setMultiSelectionEnabled(false);
		dirForJavaClassesjFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JButton xjcConvertButton = new JButton("Convert");

		xjcConvertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executeXJRCommand();
			}
		});

		xjcResponseTextArea.setText("");

		selectDirForJavaClassesLabel.setBounds(10, 20, 100, 20);
		xjcFrame.add(selectDirForJavaClassesLabel);

		dirForJavaClassesjFileChooser.setBounds(130, 20, 100, 20);
		xjcFrame.add(dirForJavaClassesjFileChooser);
		
		JPanel dirForJavaClassesPanel = new JPanel(new GridBagLayout());
		
		packageNameForJavaClassesLabel.setBounds(10, 50, 100, 20);
		xjcFrame.add(packageNameForJavaClassesLabel);

		xsdFileChooser.setBounds(130, 50, 100, 20);
		xjcFrame.add(xsdFileChooser);

		xjcConvertButton.setBounds(130, 100, 100, 40);
		xjcFrame.add(xjcConvertButton);

		xjcResponseTextArea.setBounds(1, 1, 1, 1);
		xjcFrame.add(xjcResponseTextArea);

		xjcFrame.setSize(400, 500);
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		xjcFrame.setLocation(screenDimension.width / 2 - xjcFrame.getSize().width / 2,
				screenDimension.height / 2 - xjcFrame.getSize().height / 2);

		xjcFrame.setLayout(null);
		xjcFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//xjcFrame.pack();
		xjcFrame.setVisible(true);

	}

	static void exitGUI() {
		xjcFrame.dispose();
	}

	static void executeXJRCommand() {

		xjcResponseTextArea.setText("");

		try {
			Object[] xjcReturnObjectArray = XJRProcessExecutor.executeXJRCommand(dirForJavaClasses, packageName,
					XSDFilePath);
			xjcResponseTextArea.setText((String) xjcReturnObjectArray[1]);
		} catch (IOException | InterruptedException e) {
			// e.printStackTrace();
			xjcResponseTextArea.setText((String) e.getMessage());
		}

	}

}
