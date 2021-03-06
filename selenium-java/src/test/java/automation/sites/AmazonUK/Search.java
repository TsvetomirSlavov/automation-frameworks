package automation.sites.AmazonUK;

import org.junit.Assert;
import org.openqa.selenium.WebElement;

import static automation.core.WebDriverExtensions.*;

public class Search {

  private static WebElement SearchTextbox() {
    return findElementByCssSelector("#twotabsearchtextbox");
  }
  
  private static WebElement SearchButton() {
    return findElementByCssSelector(".nav-submit-button.nav-sprite input[value='Go']");
  }
  
  private static WebElement SearchDropdownBox() {
    return findElementByCssSelector("#searchDropdownBox");
  }
  
  public static boolean isSearchDisplayed() {
    WebElement element = findElementByCssSelector("form.nav-searchbar-inner");
    waitForElement(element);
    return isElementDisplayed(element);
  }

  public static void enterSearchCriteria(String criteria) {
    typeText(SearchTextbox(), criteria);
  }
  
  public static void selectSearchDepartment(String department) {
    if(department == null || department.isEmpty())
      department = "All Departments";
      
    select(SearchDropdownBox(), department);
  }
  
  public static void search(String department, String criteria) {
    try {
        Assert.assertTrue(isSearchDisplayed());
        selectSearchDepartment(department);
        enterSearchCriteria(criteria);
        clickOn(SearchButton());
    } catch (Exception e) {
        e.printStackTrace();
    }
  }  
}
