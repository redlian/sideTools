package demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.util.SystemPropertyUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class CreateXml {

	final static String auto = "-auto";

	final static String ITEMCLASS = "class";
	final static String ITEMFIELD_POS = "field_pos";
	final static String ITEMMODEL = "model";
	final static String ITEMTITLE_POS = "title_pos";

	static int initialpos = 0;
	final static int row = 25;

	public static final String xmlFilePath = System.getProperty("user.home") + File.separator + "Desktop"
			+ File.separator + "XML";

	final static String BASEDIR = System.getProperty("user.home") + File.separator + "Desktop" + File.separator
			+ "creater" + File.separator;

	public static void generateXML(Document doc, Element root, File filepath) throws IOException {
		generateXML(doc, root, filepath.getAbsoluteFile().toString(), false);
	}

	public static void generateXML(Document doc, Element root, String path, Boolean defaultpath) throws IOException {
		try {

			InputStreamReader isr = null;
			String regex = "\t";
			if (defaultpath) {
				isr = new InputStreamReader(new FileInputStream(System.getProperty("user.home")+"\\Desktop\\DEMO.tsv"));// 檔案讀取路徑
			} else {
				isr = new InputStreamReader(new FileInputStream(path));// 檔案讀取路徑
			}
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(isr);
			if (path.endsWith("csv") || reader.readLine().indexOf(",") != -1)
				regex = "\\,";
			String line = null;
			int size = 0;
			Set idset = new HashSet();
			while ((line = reader.readLine()) != null) {
				int x = 0, y = 0, w = 10, h = 1;
				System.out.println("##############" + line);
				String[] idata = line.split(regex);

				if (line.matches("^initial\\_position.*")) {
					initialpos = Integer.valueOf(idata[1]);
					continue;
				}
				if (idset.add(idata[1]) == false)
					System.out.println(
							"####[Field id repeate]Name: " + idata[2] + ", ID: " + idata[1] + " ,Class: " + idata[0]);
				if (line.matches("^ITEM\\_CLASS.*") == false) {
					Element defaultelee = null;
					int[] fieldpos = { x, y, w, h };
					int[] titlepos = { x, y, w, h };
					String classBean = idata[0];
					switch (classBean) {
					case "AlphaNumericField":
					case "AN":
						Element Alpha = createAlpha(doc, root, idata[1], idata[2]);
						defaultelee = Alpha;
						break;
					case "ButtonItem":
					case "BTN":
						Element btnelee = createButton(doc, root, idata[1], idata[2]);
						y = initialpos++;
						fieldpos = new int[] { x, y, w, h };
						setFieldpos(doc, btnelee, fieldpos);
						break;
					case "LabelItem":
					case "LB":
						Element labelelee = createLabel(doc, root, idata[1], idata[2]);
						y = initialpos++;
						fieldpos = new int[] { x, y, w, h };
						setFieldpos(doc, labelelee, fieldpos);
						break;
					case "CheckBoxItem":
					case "CBox":
						Element checkboxelee = createCheckbox(doc, root, idata[1], idata[2]);
						y = initialpos++;
						fieldpos = new int[] { x, y, w, h };
						setFieldpos(doc, checkboxelee, fieldpos);
						break;
					case "BorderItem":
					case "BD":
						Element borderelee = createBorder(doc, root, idata[1], idata[2]);
						y = initialpos++;
						h = h + 10;
						initialpos = initialpos + h - 1;
						w = w + 30;
						fieldpos = new int[] { x, y, w, h };
						setFieldpos(doc, borderelee, fieldpos);
						break;
					case "ComboBoxItem":
					case "CB":
						Element comboboxelee = createCombobox(doc, root, idata[1], idata[2], idata[3], idata[4]);
						y = initialpos++;
						titlepos = new int[] { x, y, w, h };
						x = x + w;
						fieldpos = new int[] { x, y, w, h };
						setFieldpos(doc, comboboxelee, fieldpos);
						setTitlepos(doc, comboboxelee, titlepos);

						break;
					default:
						System.out.println("不存在" + line + " 類別");
					}
					if (defaultelee != null) {
						y = initialpos++;
						titlepos = new int[] { x, y, w, h };
						x = x + w;
						fieldpos = new int[] { x, y, w, h };
						setFieldpos(doc, defaultelee, fieldpos);
						setTitlepos(doc, defaultelee, titlepos);
					}
				}
				size++;
			}

			//// 設定畫面列數 若總欄位數 < 25 則預設為25
			setPatternRowSize(doc, root, size < row ? String.valueOf(row) : String.valueOf(size));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Document initialDoc() throws ParserConfigurationException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

		Document document = documentBuilder.newDocument();
		return document;
	}

	public static Element rootBeans(Document doc) {
		// root element
		Element root = doc.createElement("beans");
		doc.appendChild(root);
		return root;

	}

	public static Element patternBean(Document doc, Element root) {
		// root element
		Element patternModel = doc.createElement("bean");
		root.appendChild(patternModel);
		return patternModel;

	}

	public static void outprintXML(Document document) throws TransformerException {
		outprintXML(document, "defautl");
	}

	/**
	 * 輸出XML on Console/ 輸出 file
	 * 
	 * @since 2021年7月2日 下午5:57:58
	 * @param document
	 * @throws TransformerException
	 */
	public static void outprintXML(Document document, String filename) throws TransformerException {
		File xmlfile = new File(xmlFilePath);
		if (xmlfile.exists() == false) {
			xmlfile.mkdirs();
		}
		xmlfile = new File(xmlFilePath + File.separator + filename + ".xml");

		// create the xml file
		// transform the DOM Object to an XML File
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(xmlfile);
		StreamResult streamResult2 = new StreamResult(System.out);

		// If you use
		// StreamResult result = new StreamResult(System.out);
		// the output will be pushed to the standard output ...
		// You can use that for debugging

		transformer.transform(domSource, streamResult);
		transformer.transform(domSource, streamResult2);
	}

	public static void main(String[] args) throws IOException {
		try {
//			Document document =  initialDoc();
//
//			// root element
//			Element root = rootBeans(document);
//
//			// pattner element
//			Element patternModel = patternBean(document, root);
//
//			setPatternAttr(document, patternModel, "" + auto);
			////
			Date start = new Date();
			System.out.println("###### tags: Generate XML Start [ "
					+ new SimpleDateFormat("yyyy-mm-dd HH:mm:ss S").format(start) + " ]");
//			generateXML(document, patternModel, "", true);

			if (new File(BASEDIR).listFiles().length == 0) {
				System.out.println("#### [Warning] 請確認路徑下是否有需產生的檔案(tsv/csv/txt): " + BASEDIR);
				System.exit(0);
			}
			File[] files = new File(BASEDIR).listFiles();
			for (File file : files) {
				Document document = initialDoc();

				// root element
				Element root = rootBeans(document);

				// pattner element
				Element patternModel = patternBean(document, root);

				setPatternAttr(document, patternModel, "" + auto);
				System.out.println(file.getAbsolutePath());
				setPatternNo(document, patternModel, file.getName().replaceAll("[DEMO\\s\\-]|\\.tsv|\\s", "") + auto);
				generateXML(document, patternModel, file);

				//// print out also can create xml file
				//
				outprintXML(document);
				break;
			}

			Date end = new Date();
			System.out.println("Done creating XML File");
			System.out.println("###### tags: Generate XML in the End [ "
					+ new SimpleDateFormat("yyyy-mm-dd HH:mm:ss S").format(end) + " ]");
			System.out.println("Cost time:" + (end.getTime() - start.getTime()) / (1000) + "s");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	/*
	 * <bean column="80" model="PatternModel" name="0005B" no="0005B" row="30">
	 */
	public static void setPatternAttr(Document doc, Element patel, String patternName) {
		//// default
		Attr colAttr = doc.createAttribute("column");
		colAttr.setValue("80");
		Attr modelAttr = doc.createAttribute("model");
		modelAttr.setValue("PatternModel");
		////
		Attr name = doc.createAttribute("name");
		name.setValue(patternName);
		Attr no = doc.createAttribute("no");
		no.setValue(patternName);
		Attr rowAttr = doc.createAttribute("row");
		rowAttr.setValue("25");

		// set element attributeNode
		patel.setAttributeNode(colAttr);
		patel.setAttributeNode(modelAttr);
		patel.setAttributeNode(name);
		patel.setAttributeNode(no);
		patel.setAttributeNode(rowAttr);

	}

	private static void setPatternNo(Document doc, Element patel, String ptNo) {
		Attr no = doc.createAttribute("no");
		no.setValue(ptNo);
		patel.setAttributeNode(no);
	}

	private static void setPatternRowSize(Document doc, Element patel, String rowSize) {
		Attr rowAttr = doc.createAttribute("row");
		rowAttr.setValue(rowSize);
		patel.setAttributeNode(rowAttr);
	}

	public static void setItemElAttribute(Document doc, Element elee, String className, String fieldpos,
			String modelname, String titlepos) {

		Attr attrc = doc.createAttribute(ITEMCLASS);
		attrc.setValue(className);
		Attr attrf = doc.createAttribute(ITEMFIELD_POS);
		attrf.setValue(fieldpos);
		Attr attrm = doc.createAttribute(ITEMMODEL);
		attrm.setValue(modelname);
		if (titlepos != null) {
			Attr attrct = doc.createAttribute(ITEMTITLE_POS);
			attrct.setValue(titlepos);
			elee.setAttributeNode(attrct);
		}
		Attr uid = doc.createAttribute("uid");

		elee.setAttributeNode(attrc);
		elee.setAttributeNode(attrf);
		elee.setAttributeNode(attrm);
		elee.setAttributeNode(uid);
	}

	public static void setTitlepos(Document doc, Element elee, String titlepos) {
		Attr attrct = doc.createAttribute(ITEMTITLE_POS);
		attrct.setValue(titlepos);
		elee.setAttributeNode(attrct);
	}

	private static void setTitlepos(Document doc, Element elee, int[] titlepos) {
		String titleposStr = Arrays.toString(titlepos).replaceAll("\\[|\\]|\\s", "");
		setTitlepos(doc, elee, titleposStr);
	}

	public static void setFieldpos(Document doc, Element elee, String fieldpos) {
		Attr attrf = doc.createAttribute(ITEMFIELD_POS);
		attrf.setValue(fieldpos);
		elee.setAttributeNode(attrf);
	}

	private static void setFieldpos(Document doc, Element elee, int[] fieldpos) {
		String fieldposStr = Arrays.toString(fieldpos).replaceAll("\\[|\\]|\\s", "");
		setFieldpos(doc, elee, fieldposStr);
	}

	public static void setPropertyValue(Document doc, Element elee, String propkey, String propValue) {
		Element property = doc.createElement("property");
		property.setAttribute("name", propkey);
		property.setAttribute("value", propValue);
		elee.appendChild(property);
	}

	public static void setTitleFont(Document doc, Element elee) {
		setPropertyValue(doc, elee, "titleFont", "Monospaced-plain-16");
	}

	public static void setFieldFont(Document doc, Element elee) {
		setPropertyValue(doc, elee, "font", "Monospaced-plain-16");
	}

	/**
	 * 
	 * @since 2021年7月2日 下午5:40:00
	 * @param doc
	 * @param root
	 * @param id
	 * @param title
	 * @return
	 */
	public static Element createAlpha(Document doc, Element root, String id, String title) {
		Element init = doc.createElement("bean");
		setItemElAttribute(doc, init, "AlphaNumericField", "0,0,0,0", "ItemModel", "0,0,10,0");
		setPropertyValue(doc, init, "ID", id);
		setPropertyValue(doc, init, "title", title);

		/// default autoTab: true
		setPropertyValue(doc, init, "autoTab", "false");
		setTitleFont(doc, init);
		setFieldFont(doc, init);
		root.appendChild(init);
		return init;
	}

	public static Element createButton(Document doc, Element root, String id, String buttonName) {
		Element init = doc.createElement("bean");
		setItemElAttribute(doc, init, "ButtonItem", "0,0,0,0", "ItemModel", null);
		setPropertyValue(doc, init, "ID", id);
		setPropertyValue(doc, init, "value", buttonName);
		setFieldFont(doc, init);
		root.appendChild(init);
		return init;
	}

	public static Element createLabel(Document doc, Element root, String id, String value) {
		Element init = doc.createElement("bean");
		setItemElAttribute(doc, init, "LabelItem", "0,0,0,0", "ItemModel", null);
		setPropertyValue(doc, init, "ID", id);
		setPropertyValue(doc, init, "value", value);
		setFieldFont(doc, init);
		root.appendChild(init);
		return init;
	}

	public static Element createCheckbox(Document doc, Element root, String id, String text) {
		Element init = doc.createElement("bean");
		setItemElAttribute(doc, init, "CheckBoxItem", "0,0,0,0", "ItemModel", null);
		setPropertyValue(doc, init, "ID", id);
		setPropertyValue(doc, init, "value", text);
		setFieldFont(doc, init);
		root.appendChild(init);
		return init;
	}

	public static Element createBorder(Document doc, Element root, String id, String value) {
		Element init = doc.createElement("bean");
		setItemElAttribute(doc, init, "BorderItem", "0,0,0,0", "ItemModel", null);
		setPropertyValue(doc, init, "ID", id);
		setPropertyValue(doc, init, "value", value);
		setFieldFont(doc, init);
		root.appendChild(init);
		return init;
	}

	/**
	 * 
	 * @since 2021年7月1日 下午6:48:46
	 * @param doc
	 * @param root
	 * @param id
	 * @param title
	 * @param outputStyle  0=key, 1=description, 2=key-description
	 * @param defaultValue
	 * @return
	 */
	public static Element createCombobox(Document doc, Element root, String id, String title, String outputStyle,
			String defaultValue) {
		Element init = doc.createElement("bean");
		setItemElAttribute(doc, init, "ComboBoxItem", "0,0,0,0", "ItemModel", "0,0,10,0");
		setPropertyValue(doc, init, "ID", id);
		setPropertyValue(doc, init, "title", title);

		setPropertyValue(doc, init, "value", defaultValue);
		if (outputStyle.equals("0") == false)// outputStyle default: 0
			setPropertyValue(doc, init, "outputStyle", outputStyle);

		/// default autoTab: true
		setPropertyValue(doc, init, "autoTab", "false");
		setTitleFont(doc, init);
		setFieldFont(doc, init);
		root.appendChild(init);
		return init;
	}
}
