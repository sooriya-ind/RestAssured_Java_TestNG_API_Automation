package utilities;

import enums.FrameworkConstants;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Base {

    private static final Logger log = LogManager.getLogger(Base.class);
    public static RequestSpecification reqSpec(String eventName, String token) {

        RequestSpecification requestSpecification = new RequestSpecBuilder().build();

        // Add Content Type
        Header contentType = new Header("Content-Type", "application/json");
        requestSpecification.header(contentType);

        // Add Accept
        Header accept = new Header("Accept", "*/*");
        requestSpecification.header(accept);

        // Add Connection
        Header connection = new Header("Connection", "keep-alive");
        requestSpecification.header(connection);

        // Add Token
        Header authToken = new Header("Token", token);
        requestSpecification.header(authToken);

        return requestSpecification;

    }

    public static ResponseSpecification resSpec() {

        ResponseSpecification responseSpecification = new ResponseSpecBuilder().build();

        // Setup Log & Validate Everything Upon Receiving Response
        responseSpecification
                .expect()
                .statusCode(200)
                .contentType(ContentType.JSON);

        return responseSpecification;

    }

    @BeforeSuite
    public void preRequisites() {

        // Creating a File object for directory
        File directoryPath = new File(FrameworkConstants.getResultDirectoryPath());

        // Get TimeStamp From Excel Output File
        String folderName = null;
        File[] listOfFiles = directoryPath.listFiles();
        try {
            assert listOfFiles != null;
        } catch (AssertionError e) {
            try {
                FileUtils.forceMkdir(directoryPath);
                listOfFiles = directoryPath.listFiles();
            } catch (IOException ex) {
                log.error(" !!! Fail Safe 'test-output' folder creation failed. !!! ");
            }
        }
        assert listOfFiles != null;
        for (File fileName : listOfFiles) {
            if (fileName.isFile() && fileName.getName().contains(".xlsx")) {
                folderName = fileName.getName().replace("TestOutput_", "").replace(".xlsx", "");
            }
        }

        // List of all files and directories
        File[] filesList = directoryPath.listFiles();
        assert filesList != null;
        for (File file : filesList) {

            try {

                if (file.isFile()) {

                    FileUtils.moveFileToDirectory(file, new File(FrameworkConstants.getOldResultsPath() + File.separator + folderName), true);

                } else if (file.isDirectory()) {

                    FileUtils.moveDirectoryToDirectory(file, new File(FrameworkConstants.getOldResultsPath() + File.separator + folderName), true);

                }
            } catch (IOException ex) {

                log.error("!!! Moving Old Reports Failed !!!");
            }

        }

        // Clear Log File Contents
        try {
            new FileWriter(FrameworkConstants.getLogFilePath(), false).close();
            log.info("Cleared Old Logs...");
        } catch (IOException e) {
            log.error("!!! Clear Old Logs Failed !!!");
        }

        // Create Directories For Test
        try {
            FileUtils.forceMkdir(new File(FrameworkConstants.getResultDirectoryPath() + "\\api-logs"));
        } catch (IOException e) {
            log.info(" Create directory with screen-shots folder failed. ");
        }

    }

    @AfterMethod
    public synchronized void analyzer(ITestResult result) {

        // Check Whether Test is Failed
        if (result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.SKIP) {

            try {
                DataProviderUtils.excelWriter("Execution Status", "Fail");
            } catch (Exception e) {
                log.error("Exception while writing result : {}", e.getMessage());
            }

        }

        // Check & Update Details Test is Passed
        if (result.getStatus() == ITestResult.SUCCESS) {

            try {
                DataProviderUtils.excelWriter("Execution Status", "Pass");
            } catch (Exception e) {
                log.error("Exception while writing result : {}", e.getMessage());
            }

        }

    }

    @AfterSuite
    public void moveLog() {

        // Create a copy & Move Log file to Results Folder
        try {
            FileUtils.copyFile(new File(FrameworkConstants.getLogFilePath()), new File(FrameworkConstants.getResultDirectoryPath() + "\\log4j2.log"));
            log.info("Log File Moved Successfully.");
        } catch (IOException e) {
            log.error("!!! Move Log To Results Folder Failed !!!");
        }

    }

    public static synchronized void createTextFile(String textToWrite, String fileName, String state, String iteration) {

        // Building Path Dynamically
        String path = null;
        if (fileName.contains("Request")) {
            path = "Request";
        } else if (fileName.contains("Response")) {
            path = "Response";
        }

        try {
            FileUtils.forceMkdir(new File(FrameworkConstants.getApiLogsPath() + File.separator + state.concat("_" + iteration) + File.separator + path));
            Files.writeString(Path.of(FrameworkConstants.getApiLogsPath() + File.separator + state.concat("_" + iteration) + File.separator + path + File.separator + fileName + ".txt"), textToWrite, StandardCharsets.UTF_8);
            log.info(" Created {} file successfully.", fileName);
        } catch (IOException e) {
            log.error(" !!! Save Request/Response Failed !!! ---> {}.", e.getMessage());
        }

    }

}
