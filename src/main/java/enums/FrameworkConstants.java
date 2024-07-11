package enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Assert;
import utilities.PropertyUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FrameworkConstants {

    private FrameworkConstants() {

    }

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String RESOURCES_PATH = USER_DIR + "\\src\\main\\resources";
    public static final String RESULT_DIR = USER_DIR + "\\test-output";
    private static final String API_LOGS_PATH = RESULT_DIR + "\\api-logs";
    private static final String LOG_FILE_PATH = FrameworkConstants.USER_DIR + "\\logs\\log4j2.log";
    private static final String INPUT_EXCEL_PATH = RESOURCES_PATH + "\\TestInputData.xlsx";
    private static final String CLONED_EXCEL_NAME = "TestOutput_" + new SimpleDateFormat("dd_MM_yyyy.HH.mm.ss").format(new Date()) + ".xlsx";
    private static final String OLD_RESULTS_PATH = USER_DIR + "\\oldResults";

    public static String getInputExcelPath() {
        if (PropertyUtils.getValue("inputExcelLocation").equalsIgnoreCase("")) {
            return INPUT_EXCEL_PATH;
        } else {
            return PropertyUtils.getValue("inputExcelLocation");
        }
    }

    public static String getResultDirectoryPath() {
        return RESULT_DIR;
    }

    public static String getApiLogsPath() {
        return API_LOGS_PATH;
    }

    public static String getClonedExcelName() {
        return CLONED_EXCEL_NAME;
    }

    public static String getOldResultsPath() {
        return OLD_RESULTS_PATH;
    }

    public static String getLogFilePath() {
        return LOG_FILE_PATH;
    }

}
