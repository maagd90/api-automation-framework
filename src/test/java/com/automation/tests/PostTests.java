import io.qameta.allure.*;

@Epic("API Automation Framework")
@Feature("Post Tests")
public class PostTests {

    @Story("Create a Post")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("12345")
    @Owner("maagd90")
    @Test
    public void createPost() {
        // Your test code here
    }

    @Story("Get a Post")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("12346")
    @Owner("maagd90")
    @Test
    public void getPost() {
        // Your test code here
    }

    // Add more tests with Allure annotations as needed
}