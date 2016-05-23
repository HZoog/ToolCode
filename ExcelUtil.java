package com.gm.comm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.struts2.ServletActionContext;

public class ExcelUtil {
	
	@SuppressWarnings({ "resource", "deprecation" })
	public static String createXLSX(XlsxObj xObj, String fileName) {
		SXSSFWorkbook wb = new SXSSFWorkbook(-1);
		Sheet sh = wb.createSheet();
		Row fNameRow = sh.createRow(0);
		for (int i = 0; i < xObj.getFiledName().length; i++) {
			Cell fNameCell = fNameRow.createCell(i);
			fNameCell.setCellValue(xObj.getFiledName()[i]);
		}
		try {
			// 设置内容
			for (int i = 0; i < xObj.getFiledValue().size(); i++) {
				Row vRow = sh.createRow(i + 1);
				String[] cellValue = xObj.getFiledValue().get(i);
				for (int j = 0; j < cellValue.length; j++) {
					Cell fvalueCell = vRow.createCell(j);
					fvalueCell.setCellValue(cellValue[j]);
				}

				if (i % 100 == 0) {
					((SXSSFSheet) sh).flushRows(100);
				}
			}
			String path = ServletActionContext.getRequest().getRealPath("/");
			FileOutputStream out = new FileOutputStream(path + "download/" + fileName + ".xlsx");
			wb.write(out);
			out.close();
			wb.dispose();
			return "download/" + fileName + ".xlsx";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String createXLSX(boolean isCreate, String fileName, List<Object> dataL, String[] args) {
		if (isCreate) {
			XlsxObj xlsxObj = new XlsxObj();
			String[] fieldName = new String[args.length / 2];
			int fieldIdx = 0;
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					fieldName[fieldIdx++] = args[i];
				}
			}
			xlsxObj.setFiledName(fieldName);
			for (Object smap : dataL) {
				String[] values = new String[args.length / 2];
				int valueIdx = 0;
				for (int i = 0; i < args.length; i++) {
					if (i % 2 > 0) {
						try {
							Method method 		= smap.getClass().getMethod("get",Object.class);
							Object methodVal 	= method.invoke(smap, args[i]);
							values[valueIdx++] 	= methodVal.toString();
						} catch (Exception e) {
							//e.printStackTrace();
						}
					}
				}
				xlsxObj.getFiledValue().add(values);
			}
			return ExcelUtil.createXLSX(xlsxObj, fileName);
		}
		return "";
	}

}
