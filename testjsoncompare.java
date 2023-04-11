package qvcaemsuite.US.pages;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import qvcaemsuite.utilities.AEMPageUtil;
import qvcaemsuite.utilities.BaseTest;
import qvcaemsuite.utilities.ExtentReporter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
public class SlingModelJsonComparisonPage extends BaseTest {

    public WebDriver driver;
    final String file = System.getProperty("user.dir") + "\\src\\test\\resources\\jsonFile\\";
  //  private final String[] components = {"Button", "Image", "VideoGallery", "ModuleVideoClip"};
    private final String[] components = {"GridQuery"};
    public SlingModelJsonComparisonPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public void compareJsons() {
        JSONObject mobileJsonObj = null;
        JSONObject modelJsonObj = null;
        Map<String, Object> mobileJsonMap;
        Map<String, Object> modelJsonMap;
        MapDifference<String, Object> difference;
        for (String componentName : components) {
            String mobileJsonFileName = file + componentName + "Mobile.json";
            String modelJsonFileName = file + componentName + "Model.json";
            System.out.println("Mobile JSON file Name " + mobileJsonFileName);
            System.out.println("Model JSON file Name " + modelJsonFileName);
            try {
                mobileJsonObj = (JSONObject) new JSONParser().parse(new FileReader(mobileJsonFileName));
                modelJsonObj = (JSONObject) new JSONParser().parse(new FileReader(modelJsonFileName));
            } catch (IOException | ParseException e) {
                ExtentReporter.reportStep(AEMPageUtil.getWebDriver(),
                        "Exception Occurred in Method: jsonCompare"
                                + e.getMessage(),
                        "fail", 1);
            }
            try {
                mobileJsonMap = FlatMap.flatten(mobileJsonObj);
                modelJsonMap = FlatMap.flatten(modelJsonObj);
                difference = Maps.difference(mobileJsonMap, modelJsonMap);
                System.out.println("Entries only on the Mobile Json\n--------------------------");
                difference.entriesOnlyOnLeft()
                        .forEach((key, value) -> System.out.println(key + ": " + value));
                System.out.println("\n\nEntries only on the Model Json\n--------------------------");
                difference.entriesOnlyOnRight()
                        .forEach((key, value) -> System.out.println(key + ": " + value));
                System.out.println("\n\nEntries differing\n--------------------------");
                difference.entriesDiffering()
                        .forEach((key, value) -> System.out.println(key + ": " + value));
                if(difference.areEqual()) {
                    ExtentReporter.reportStep(AEMPageUtil.getWebDriver(), "Both the Jsons are same for the component : "+componentName+" : "+difference, "pass", 0);
                }else {
                    ExtentReporter.reportStep(AEMPageUtil.getWebDriver(), "Both the Jsons are not same for the component : " + componentName + " : " + difference,"Fail");
                }
            }catch(Exception e){
                ExtentReporter.reportStep(AEMPageUtil.getWebDriver(),
                        "Exception Occurred in Method: jsonCompare"
                                + e.getMessage(),
                        "fail", 1);
            }
        }
    }
}
