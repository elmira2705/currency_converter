package currenc_converter_RUR;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.awt.Font;
import java.io.IOException;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Currency {

	public static void main(String[] args) throws Exception {
		//http://www.cbr.ru/scripts/XML_daily.asp?date_req=dd/MM/yyyy
		String [][] rates = getRates();
		JFrame frame = new JFrame();
		frame.setTitle ("Курс валют");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String[] columnNames = {"Код валюты", "Курс к рублю (RUR)"};
		JTable table = new JTable(rates, columnNames);
		
		
		JTableHeader header = table.getTableHeader();
		
		header.setFont(new Font ("Arial", Font.BOLD, 18));
		table.setFont(new Font("Arial", Font.PLAIN, 15));
		table.setRowHeight(table.getRowHeight()+16);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		frame.add(scrollPane);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static String[][] getRates() throws Exception{
		HashMap <String, NodeList> result = new HashMap();
		String[][] rates = null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");
		Date date = new Date ();
		String url = "http://www.cbr.ru/scripts/XML_daily.asp?date_req="+ dateFormat.format(date);
		Document doc = loadDocument(url);
		System.out.println(doc.getXmlVersion());
				
		NodeList nl = doc.getElementsByTagName("Valute");
		for (int i = 0; i< nl.getLength(); i++) {
			Node c = nl.item(i);
			NodeList nlChilds = c.getChildNodes();
			for (int j = 0; j< nlChilds.getLength(); j++) {
				if (nlChilds.item(j).getNodeName().equals("CharCode")) {
					result.put(nlChilds.item(j).getTextContent(), nlChilds);
				}
			}
		} 
		
		int k = 0;
		rates = new String[result.size()][2];
		
		for (Map.Entry<String, NodeList> entry : result.entrySet()) {
			NodeList temp = entry.getValue();
			double value = 0;
			int nominal = 0;
			
			for (int i = 0; i < temp.getLength(); i++) {
				if (temp.item(i).getNodeName().equals("Value")) {
					value = Double.parseDouble(temp.item(i).getTextContent().replace(',' ,'.'));
				} else if (temp.item(i).getNodeName().equals("Nominal")) {
					nominal = Integer.parseInt(temp.item(i).getTextContent());
				}
			}
			double amount = value / nominal;
			
			rates [k][0] = entry.getKey();
			rates [k][1] = Double.toString(((double) Math.round(amount*10000))/10000);
			k++;
			
		}
		return rates;
	}
	
	private static Document loadDocument(String url) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		return factory.newDocumentBuilder().parse(new URL(url).openStream());
	}
}
