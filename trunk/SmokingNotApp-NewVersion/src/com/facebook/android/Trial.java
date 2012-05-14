package com.facebook.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Trial extends ListActivity {

	private static String name, phone, mail, add;

	Trial(String name, String phone, String mail, String add) {
		name = Trial.name;
		phone = Trial.phone;
		mail = Trial.mail;
		add = Trial.add;
	}

	public File CreatePdf() {

		String flip = "";
		char[] ch;

		ch = getResources().getString(R.string.OfficialReport).toCharArray();
		int j = 1, k = 0, getPhone = -1, getMail = -1;
		int getDate = -1, getName = -1, getAddress = -1;
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == ':') {
				if (getPhone == -1)
					getPhone = setPhone(ch, i);
				if (getMail == -1)
					getMail = setMail(ch, i);
				if (getDate == -1)
					getDate = setDate(ch, i);
				if (getName == -1)
					getName = setName(ch, i);
				if (getAddress == -1)
					getAddress = setAddress(ch, i);
			}
			if (ch[i] == '\n') {
				for (j = i; j > k; j--) {
					if (j == getPhone) {
						flip = flip.concat(phone);
						while (ch[j - 1] == '_')
							j--;
						getPhone = -1;
					} else if (j == getMail) {
						flip = flip.concat(mail);
						while (ch[j - 1] == '_')
							j--;
						getMail = -1;
					} else if (j == getDate) {
						DateFormat dateFormat = new SimpleDateFormat(
								"yyyy/MM/dd");
						Date date = new Date();
						flip = flip.concat(dateFormat.format(date));
						while (ch[j - 1] == '_')
							j--;
						getDate = -1;
					} else if (j == getName) {
						flip = flip.concat(name);
						while (ch[j - 1] == '_')
							j--;
						getName = -1;
					} else if (j == getAddress) {
						flip = flip.concat(add);
						while (ch[j - 1] == '_')
							j--;
						getAddress = -1;
					} else
						flip = flip.concat("" + ch[j]);
				}
				k = i;
			}
		}
		File f;
		String FILE = "C:\\Users\\UZER\\Desktop\\out.pdf";
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(FILE));
			new OutputStreamWriter(new FileOutputStream(FILE), "UTF-8");
			document.open();
			addMetaData(document);
			addContent(document, flip);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		f = new File(FILE);
		return f;
	}

	private static int setDate(char[] ch, int i) {
		int index, ikeep = -1;
		char[] date = { ':', '�' , '�', '�', '�', '�' };
		for (index = 0; index < 6 && ch[i] == date[index]; i++, index++)
			;
		if (index == 6)
			ikeep = i - index - 1;

		return ikeep;
	}

	private static int setMail(char[] ch, int i) {
		int index, ikeep = -1;
		char[] mail = { ':', '�', '"', '�', '�', '�' };
		for (index = 0; index < 6 && ch[i] == mail[index]; i++, index++)
			;
		if (index == 6)
			ikeep = i - index - 1;

		return ikeep;
	}

	private static int setPhone(char[] ch, int i) {
		int index, ikeep = -1;
		char[] phone = { ':', '�', '�', '�', '�', '�' };
		for (index = 0; index < 6 && ch[i] == phone[index]; i++, index++)
			;
		if (index == 6)
			ikeep = i - index - 1;

		return ikeep;

	}

	private static int setAddress(char[] ch, int i) {
		int index, ikeep = -1;
		char[] address = { ':', '�', '�', '�', '�', '�' };
		for (index = 0; index < 6 && ch[i] == address[index]; i++, index++)
			;
		if (index == 6)
			ikeep = i - index - 1;

		return ikeep;

	}

	private static int setName(char[] ch, int i) {
		int index, ikeep = -1;
		char[] name = { ':', '�', '�' };
		for (index = 0; index < 3 && ch[i] == name[index]; i++, index++)
			;
		if (index == 3)
			ikeep = i - index - 1;

		return ikeep;

	}

	private static void addMetaData(Document document) {
		document.addTitle("Official Report");
		document.addCreator("Smoking Not! application");
	}

	private static void addContent(Document document, String content)
			throws DocumentException {

		Font hFont;
		BaseFont unicode = null;
		try {
			unicode = BaseFont.createFont("c:/windows/fonts/arialuni.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			hFont = new Font(unicode, 12, Font.NORMAL);
			PdfPTable table = new PdfPTable(1); // 3 columns.
			PdfPCell pdfCell = new PdfPCell(new Phrase(content, hFont));
			pdfCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

			table.addCell(pdfCell);
			document.add(table);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
