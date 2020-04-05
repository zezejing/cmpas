package com.hnisc.cmpas.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.*;

/**
 *
 * All rights Reserved, Designed By HumorChen
 * @Title:  MyExcelFileUtil.java
 * @Package model
 * @Description:    TODO(我的表格文件工具，提取字段、提取某字段)
 * @author: 陈福星
 * @date:   2019年6月3日 上午11:20:17
 * @version V1.0
 * @Copyright: 2019 humorchen@vip.qq.com  All rights reserved.
 * 注意：本内容仅限于湖南信息学院内部传阅，禁止外泄以及用于其他的商业目
 */
public class MyExcelFileUtil {
	//读取指定行的所有有效字段
	public static List<String> readAllColumn(File f,int rowNumber) throws Exception
	{
		rowNumber--;
		List<String>list=new ArrayList<String>();
		if(f.getName().endsWith(".xlsx"))
		{
			InputStream is=new FileInputStream(f);
			XSSFWorkbook wb=new XSSFWorkbook(is);
			XSSFSheet sheet=wb.getSheetAt(0);
			XSSFRow row=sheet.getRow(rowNumber);
			for(int i=0;i<row.getLastCellNum();i++)
			{
				String s=row.getCell(i).toString().trim();
				if(!s.equals(""))
					list.add(s);
			}
		}
		else if(f.getName().endsWith(".xls"))
		{
			FileInputStream fis=new FileInputStream(f);
			POIFSFileSystem fs=new POIFSFileSystem(fis);
			HSSFWorkbook wb=new HSSFWorkbook(fs);
			HSSFSheet sheet=wb.getSheetAt(0);
			HSSFRow row=sheet.getRow(rowNumber);
			for(int i=0;i<row.getLastCellNum();i++)
			{
				String s=row.getCell(i).toString().trim();
				if(!s.equals(""))
					list.add(s);
			}
		}
		return list;
	}
	//读取某个字段的所有有效值
	public static  List<String> readColumn(File f,int columnIndex)throws Exception
	{
		List<String>list=new ArrayList<String>();
		if(f.getName().endsWith(".xlsx"))
		{
			InputStream is=new FileInputStream(f);
			XSSFWorkbook wb=new XSSFWorkbook(is);
			XSSFSheet sheet=wb.getSheetAt(0);
			for(int i=0;i<=sheet.getLastRowNum();i++)
			{
				String s;
				try {
					s=sheet.getRow(i).getCell(columnIndex).toString().trim();
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				if(s!=null&&(!s.equals("")))
					list.add(s);
			}

		}
		else if(f.getName().endsWith(".xls"))
		{
			FileInputStream fis=new FileInputStream(f);
			POIFSFileSystem fs=new POIFSFileSystem(fis);
			HSSFWorkbook wb=new HSSFWorkbook(fs);
			HSSFSheet sheet=wb.getSheetAt(0);
			for(int i=0;i<=sheet.getLastRowNum();i++)
			{
				String s;
				try {
					s=sheet.getRow(i).getCell(columnIndex).toString().trim();
				} catch (Exception e) {
//					e.printStackTrace();
					break;
				}
				if(s!=null&&(!s.equals("")))
					list.add(s);
			}
		}
		return list;
	}
	//将数据写入Excel文件
	public static void writeDataToFile(String [][]data,File file)
	{
		try {
			Util.mkdir(file.getParent());
			if (!file.exists())
				file.createNewFile();
			XSSFWorkbook  wb=new XSSFWorkbook();
			XSSFSheet sheet=wb.createSheet();
			XSSFRow row;
			XSSFCell cell;
			XSSFCellStyle textStyle = wb.createCellStyle();
			XSSFDataFormat format = wb.createDataFormat();
			textStyle.setDataFormat(format.getFormat("@"));
			int i,j;
			for(i=0;i<data.length;i++)
			{
				row=sheet.createRow(i);
				for(j=0;j<data[i].length;j++) {
					cell=row.createCell(j);
					cell.setCellStyle(textStyle);
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(data[i][j]);
				}
			}
			wb.write(new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
