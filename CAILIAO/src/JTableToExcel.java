
 

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * 
 * 把JTable中的数据导出到Excel表格中工具类
 * 
 * @author Administrator
 * 
 */
public class JTableToExcel  {

	/**外部调用的方法
	 * @param file
	 * @param heading
	 * @param inscribe
	 * @param table
	 */
    public static void export(File file, String heading, String inscribe, JTable table) {
		try {
			WritableWorkbook workbook = null;// 创建工作薄

			if (file.exists()) {// 文件已经存在
				  
				workbook=	Workbook.createWorkbook(new FileOutputStream(file));

				//workbook = Workbook.createWorkbook(file, Workbook.getWorkbook(file));
			} else { // 文件还不存在
				workbook = Workbook.createWorkbook(new FileOutputStream(file));
				 
			}
			// 创建工作表
			WritableSheet sheet = workbook.createSheet(heading, workbook.getNumberOfSheets());

			// 取得Table的行数(rowNum), 列数(colNum)
			int rowNum = table.getRowCount();
			int colNum = table.getColumnCount();

			// 填写主标题
			fillHeading(sheet, heading, colNum); 
			
			// 填写列名
			fillColumnName(sheet, table, colNum);
			
			// 填写落款
			fillInscribe(sheet, inscribe, rowNum, colNum);

			// 填写数据 
			fillCell(sheet, table, rowNum, colNum);

			// 写入工作表
			workbook.write();
			workbook.close();
			
			// 导出成功提示框
			int dialog = JOptionPane.showConfirmDialog(null, "表导出成功！是否现在打开？", "提示", JOptionPane.YES_NO_OPTION);
			if (dialog == JOptionPane.YES_OPTION) {
				Runtime.getRuntime().exec("cmd /c start \"\" \"" + file + "\"");
			}
			
		} catch (FileNotFoundException e) {
             JOptionPane.showMessageDialog(null, "导入数据前请关闭工作表");
	    } catch (Exception e) {
	         JOptionPane.showMessageDialog(null, "没有进行筛选");
	         e.printStackTrace();
        }
    }

    /**
     * 填写主标题
     * @param sheet
     * @param heading
     * @param colNum
     * @throws WriteException
     */
    private static void fillHeading(WritableSheet sheet, String heading, int colNum) throws WriteException {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD,
                false, UnderlineStyle.NO_UNDERLINE, Colour.RED);// 定义字体

        WritableCellFormat format = new WritableCellFormat(font);// 创建格式化对象

        format.setAlignment(Alignment.CENTRE);// 水平居中显示

        format.setVerticalAlignment(VerticalAlignment.CENTRE);// 垂直居中显示

        sheet.mergeCells(0, 0, colNum - 1, 0); // 合并单元格

        sheet.setRowView(0, 600); // 设置行高

        sheet.addCell(new Label(0, 0, heading, format));// 填写工作表

    }
    
    /**
     * 填写列名
     * @param sheet
     * @param table
     * @param colNum
     * @throws WriteException
     */
    private static void fillColumnName(WritableSheet sheet, JTable table, int colNum) throws WriteException {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD,
                false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);// 定义字体

        WritableCellFormat format = new WritableCellFormat(font);// 定义格式化对象

        format.setAlignment(Alignment.CENTRE);// 水平居中显示

        sheet.setColumnView(0, 15);// 设置列宽

        for (int col = 0; col < colNum; col++) {
        	
        	Label colName = new Label(col, 1, table.getModel().getColumnName(col), format);
        	
        	sheet.addCell(colName);
        }
    }

    /**
     * 填写落款
     * @param sheet
     * @param inscribe
     * @param colNum
     * @param rowNum
     * @throws WriteException
     */
    private static void fillInscribe(WritableSheet sheet, String inscribe, int rowNum, int colNum) throws WriteException {
    	if( inscribe == null || inscribe.length() < 1 ) {
    		
    		inscribe = "导出时间: "+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    	}
    	
        WritableFont font = new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD,
                false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);// 定义字体

        WritableCellFormat format = new WritableCellFormat(font);// 定义格式化对象

        format.setAlignment(Alignment.RIGHT);// 水平居中显示

        sheet.mergeCells(0, rowNum+3, colNum - 1, rowNum+3);// 合并单元格

        sheet.addCell(new Label(0, rowNum+3, inscribe, format));// 填写工作表
    }
    
    /**
     * 填写数据
     * @param sheet
     * @param talbe
     * @param rowNum
     * @param colNum
     * @throws WriteException
     */
    private static void fillCell(WritableSheet sheet, JTable table, int rowNum, int colNum ) throws WriteException {
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,
                false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);// 定义字体
        
        WritableCellFormat format = new WritableCellFormat(font);// 定义格式化对象
        
        format.setAlignment(Alignment.CENTRE); // 水平居中显示

        for (int i = 0; i < colNum; i++) {    // 列
        	
			for (int j = 1; j <= rowNum; j++) {// 行
				 Object object= table.getValueAt(j - 1, i);
				String str =object==null?"":object.toString();
				
				Label labelN = new Label(i, j+1, str);
				
				try {
					
					sheet.addCell(labelN);
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
    }
}