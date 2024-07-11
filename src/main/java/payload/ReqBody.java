package payload;

import enums.FrameworkConstants;
import io.restassured.path.json.JsonPath;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.Base;
import utilities.PropertyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

@SuppressWarnings("ALL")
public class ReqBody extends Base {

    private static final Logger log = LogManager.getLogger(ReqBody.class);

    public static String apiRequestBody() {
        return "";
    }

}
