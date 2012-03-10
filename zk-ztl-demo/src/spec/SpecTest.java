import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.zkoss.ztl.ZKIntegrationTestCase;
import org.zkoss.ztl.util.ui.Button;

@RunWith(ConcordionRunner.class)
public class SpecTest {
	private ZKIntegrationTestCase zk;

	public SpecTest() {
		zk = new ZKIntegrationTestCase();
		zk.setTarget("http://localhost:8080/ZK-demo");
		zk.setBrowsers(zk.makeListOfBrowsers("iexplore"));
		zk._setTimeout(100);
		zk.setClickTimeOut(60000);
	}
	
	public void setUp(){
		zk.setUp();
	}
	
	public void tearDown(){
		try {
			zk.tearDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openBrowser(){
		zk.start(zk.getBrowsers().get(0));
	}
	
	public void checkClick(String btnCaption){
		zk.windowFocus();
		zk.windowMaximize();
		zk.clickAndWait(Button.byLabel("click"));
	}
	
	public boolean verifyIfLabelIs(String label){
		try {
			zk.assertElementPresent(zk.getJq("@label[value=\"hello\"]"));
			return true;
		} catch(AssertionError e) {
			return false;
		}
	}
	
	public void closeBrowser() {
		zk.close();
	}
}
