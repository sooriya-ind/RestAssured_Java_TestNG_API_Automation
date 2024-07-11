package utilities;

import enums.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public final class DataProviderUtils {

    private static final Logger log = LogManager.getLogger(DataProviderUtils.class);
    private static final String SHEET_NAME = "sheetName";
    private static final String ITERATION = "iteration";

    private DataProviderUtils() {
    }

    // Setting Up Data To Execute
    @DataProvider(name = "dataTest", parallel = true)
    public static Object[][] testData(Method m) {

        // Clone Test Input File To Write Results
        cloneFile(FrameworkConstants.getInputExcelPath());

        // Return Test-Data
        return readDataFromExcel(FrameworkConstants.getInputExcelPath(), PropertyUtils.getValue(SHEET_NAME));

    }

    public static synchronized Object[][] readDataFromExcel(String excelPath, String sheetName) {

        try (InputStream inp = new FileInputStream(excelPath);
             Workbook wb = WorkbookFactory.create(inp)) {

            // To check the sheet is null or exists
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet with name " + sheetName + " not found in the Excel file.");
            }

            List<Map<String, String>> dataList = extractData(sheet);

            return convertTo2DArray(dataList);

        } catch (IOException e) {
            log.error("Unable to read data from excel --> {}.", e.getMessage());
            return new Object[0][0]; // Return empty array in case of failure
        }
    }

    private static synchronized List<Map<String, String>> extractData(Sheet sheet) {

        // Plus 1 for 0-based index
        int rowCount = sheet.getLastRowNum() + 1;
        int columnCount = sheet.getRow(0).getLastCellNum();

        List<Map<String, String>> dataList = new ArrayList<>();
        Set<String> uniqueIterations = new HashSet<>();

        // Start from 1 to skip header
        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row != null && !row.getZeroHeight()) {
                Map<String, String> mapData = extractRowData(sheet.getRow(0), row, columnCount);
                String iteration = mapData.get(ITERATION);
                if (iteration != null && !iteration.contains("_") && uniqueIterations.add(iteration)) {
                    dataList.add(mapData);
                }
            }
        }

        return dataList;
    }

    private static synchronized Map<String, String> extractRowData(Row headerRow, Row dataRow, int columnCount) {

        DataFormatter dft = new DataFormatter();
        dft.setUseCachedValuesForFormulaCells(true);

        Map<String, String> mapData = new HashMap<>();
        for (int j = 0; j < columnCount; j++) {
            Cell keyCell = headerRow.getCell(j);
            Cell valueCell = dataRow.getCell(j);
            if (keyCell != null && valueCell != null) {
                String tempKey = dft.formatCellValue(keyCell);
                String tempValue = dft.formatCellValue(valueCell);
                mapData.put(tempKey, tempValue);
            }
        }

        return mapData;
    }

    private static synchronized Object[][] convertTo2DArray(List<Map<String, String>> dataList) {
        return dataList.stream()
                .map(m -> new Object[]{m})
                .toArray(Object[][]::new);
    }

    public static void cloneFile(String originalFileLocation) {

        int columnCount;

        // Create Clone File
        try (InputStream inp = new FileInputStream(originalFileLocation)) {
            try (Workbook wb = WorkbookFactory.create(inp)) {
                FileOutputStream fos = new FileOutputStream(FrameworkConstants.getResultDirectoryPath() + "\\" + FrameworkConstants.getClonedExcelName());
                wb.write(fos);
            }
        } catch (Exception e) {
            log.error(" !!! Cloning File Failed !!! ---> {}.", e.getMessage());
        }

        // Open Excel Sheet & Enter Headings To Excel
        try (InputStream inp = new FileInputStream(FrameworkConstants.getResultDirectoryPath() + "\\" + FrameworkConstants.getClonedExcelName());
             Workbook wb = WorkbookFactory.create(inp)) {

            // Open Excel & Write
            Sheet sheet = wb.getSheet(PropertyUtils.getValue(SHEET_NAME));
            columnCount = sheet.getRow(0).getLastCellNum();
            Row row = sheet.getRow(0);

            // Create & Set Headings First
            row.createCell(columnCount).setCellValue("Execution Status");
            List<String> columnHeadings = getColumnHeadings();
            for (int i = 1; i <= columnHeadings.size(); i++) {
                row.createCell(columnCount + i).setCellValue(columnHeadings.get(i - 1));
            }

            // Hide Tax Columns
            setColumnsHidden(sheet, 19, 26);
            setColumnsHidden(sheet, 39, 46);

            // Write value to file
            OutputStream os = new FileOutputStream(FrameworkConstants.getResultDirectoryPath() + "\\" + FrameworkConstants.getClonedExcelName());
            wb.write(os);

        } catch (Exception e) {

            log.info(" !!!!!! Excel WRITING Failed !!!!!! ----> {}.", e.getMessage());

        }

    }

    private static List<String> getColumnHeadings() {
        List<String> columnHeadings = new ArrayList<>();
        columnHeadings.add("effectiveDate");
        columnHeadings.add("expiryDate");
        columnHeadings.add("submissionId");
        columnHeadings.add("submissionNumber");
        columnHeadings.add("quoteId");
        columnHeadings.add("quoteNumber");
        columnHeadings.add("policyId");
        columnHeadings.add("policyNumber");
        columnHeadings.add("NB_SurplusLinesTax");
        columnHeadings.add("NB_StampingFee");
        columnHeadings.add("NB_FireMarshalTax");
        columnHeadings.add("NB_RegulatoryFee");
        columnHeadings.add("NB_WindPoolFee");
        columnHeadings.add("NB_AdditionalFee");
        columnHeadings.add("NB_SurplusLinesServiceCharge");
        columnHeadings.add("NB_MaintenanceFee");
        columnHeadings.add("previewQuoteProposalUrl");
        columnHeadings.add("previewPolicyIssuanceUrl");
        columnHeadings.add("quoteProposalUrl");
        columnHeadings.add("policyIssuanceUrl");
        columnHeadings.add("renewedEffectiveDate");
        columnHeadings.add("renewedExpiryDate");
        columnHeadings.add("renewedSubmissionId");
        columnHeadings.add("renewedSubmissionNumber");
        columnHeadings.add("renewedQuoteId");
        columnHeadings.add("renewedQuoteNumber");
        columnHeadings.add("renewedPolicyId");
        columnHeadings.add("renewedPolicyNumber");
        columnHeadings.add("REN_SurplusLinesTax");
        columnHeadings.add("REN_StampingFee");
        columnHeadings.add("REN_FireMarshalTax");
        columnHeadings.add("REN_RegulatoryFee");
        columnHeadings.add("REN_WindPoolFee");
        columnHeadings.add("REN_AdditionalFee");
        columnHeadings.add("REN_SurplusLinesServiceCharge");
        columnHeadings.add("REN_MaintenanceFee");
        columnHeadings.add("previewRenewedQuoteProposalUrl");
        columnHeadings.add("previewRenewedPolicyIssuanceUrl");
        columnHeadings.add("renewedQuoteProposalUrl");
        columnHeadings.add("renewedPolicyIssuanceUrl");
        return columnHeadings;
    }

    private static void setColumnsHidden(Sheet sheet, int from, int to) {
        for (int i = from; i <= to; i++) {
            sheet.setColumnHidden(i, true);
        }
    }

    public static synchronized void excelWriter(String columnName, String result) {

        // Creating file object of existing Excel file
        File xlsxFile = new File(FrameworkConstants.getResultDirectoryPath() + File.separator + FrameworkConstants.getClonedExcelName());
        DataFormatter formatter = new DataFormatter();

        Workbook workbook = null;
        FileOutputStream os = null;

        // Creating input stream
        try (FileInputStream inputStream = new FileInputStream(xlsxFile)) {

            // Creating workbook from input stream
            workbook = WorkbookFactory.create(inputStream);

            // Reading first sheet of Excel file
            Sheet sheet = workbook.getSheet(PropertyUtils.getValue(SHEET_NAME));

            // Iterating Excel to update
            for (Row row : sheet) {
                for (Cell cell : row) {
                    // Get the text that appears in the cell by getting the cell value and applying any data formats (Date, 0.00, 1.23e9, $1.23, etc)
                    String text = formatter.formatCellValue(cell);

                    // Is it an exact match?
                    if (columnName.equals(text)) {
                        Row row1 = sheet.getRow(Integer.parseInt(Thread.currentThread().getName()));
                        // Enter Values
                        row1.createCell(cell.getColumnIndex()).setCellValue(result);
                        log.info("Updated to excel : {}.", result);
                        break;
                    }

                }
            }

            // Crating output stream and writing the updated workbook
            os = new FileOutputStream(xlsxFile);
            workbook.write(os);

            log.info("Excel file has been updated successfully.");

        } catch (IOException e) {
            log.info("Exception while updating an existing excel file. !!! ---> {}.", e.getMessage());
        } finally {

            // Close the workbook and output stream
            try {
                assert workbook != null;
                workbook.close();
                assert os != null;
                os.close();
            } catch (IOException e) {
                log.error(" !!! Close Workbook Failed !!! ");
            }

        }

    }

    public static synchronized String getValueFromExcel(String columnName) {

        // Creating file object of existing Excel file
        File xlsxFile = new File(FrameworkConstants.getResultDirectoryPath() + File.separator + FrameworkConstants.getClonedExcelName());
        DataFormatter formatter = new DataFormatter();

        // Variables For Flagging
        String sheetName = PropertyUtils.getValue(SHEET_NAME);

        // Creating input stream
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(xlsxFile))) {

            // Creating workbook from input stream
            try (Workbook workbook = WorkbookFactory.create(inputStream)) {

                // Reading first sheet of Excel file
                Sheet sheet = workbook.getSheet(sheetName);

                // Find the corresponding value in the same column in the current row
                Row valueRow = sheet.getRow(getIndex(Thread.currentThread().getName())[0]);
                return formatter.formatCellValue(valueRow.getCell(getIndex(columnName)[1]));

            }

        } catch (IOException e) {
            log.info("Exception while reading from the Excel file. ---> {}", e.getMessage());
        }

        // Return an empty string if the column is not found or there is an error
        return "FILE/VALUE_NOT_FOUND";
    }

    public static int[] getIndex(String searchText) {

        Workbook workbook;
        try (FileInputStream fis = new FileInputStream(FrameworkConstants.getResultDirectoryPath() + File.separator + FrameworkConstants.getClonedExcelName())) {
            workbook = new XSSFWorkbook(fis);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheet(PropertyUtils.getValue(SHEET_NAME));

                for (Row row : sheet) {
                    for (Cell cell : row) {

                        DataFormatter dft = new DataFormatter();
                        dft.setUseCachedValuesForFormulaCells(true);
                        String cellText = dft.formatCellValue(cell);
                        if (cellText.equals(searchText)) {
                            int rowIndex = row.getRowNum(); // Add 1 to convert to human-readable index
                            int columnIndex = cell.getColumnIndex(); // Add 1 to convert to human-readable index
                            return new int[]{rowIndex, columnIndex};
                        }

                    }
                }
            }

        } catch (IOException e) {
            log.error("Failed Find Operation");
        }

        return new int[0]; // Text not found in the Excel file
    }

}

