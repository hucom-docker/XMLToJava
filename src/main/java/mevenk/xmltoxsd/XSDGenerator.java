package mevenk.xmltoxsd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.wiztools.xsdgen.ParseException;
import org.wiztools.xsdgen.XsdGen;

/**
 * @author VENKATESH
 *
 */
/**
 * The Class XMLtoXSD.
 */
public class XSDGenerator {

	/**
	 * Generate XSD.
	 *
	 * @param xmlFile
	 *            the xml file
	 * @param xsdFile
	 *            the xsd file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 */
	public boolean generateXSD(File xmlFile, File xsdFile) throws IOException, ParseException {

		XsdGen xsdGen = new XsdGen();

		xsdGen.parse(xmlFile);

		xsdGen.write(new FileOutputStream(xsdFile));

		return xsdFile.exists();

	}
}
