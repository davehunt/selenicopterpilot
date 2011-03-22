import org.apache.commons.cli.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SelenicopterPilot {

    private static final int WAITING = 1;
    private static final int PLAYING = 3;

    private static final int HELICOPTER_COLUMN = 7;
    private static final int VISIBILITY_START = 0;
    private static final double VISIBILITY_DEPTH_MULTIPLIER = 0.15;

	private static ArrayList<Integer> scores = new ArrayList<Integer>();

    public static WebDriver driver;
    public static JavascriptExecutor js;
    public static WebElement controls;

    private static String targetURL;
    private static Map helicopterCache = null;
    private static boolean thrustersOn;
    private static double targetAltitude = 50;
    private static double visibilityDepth = 12;
    private static boolean showHUD = false;


    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption("url", true, "target url for helicopter game");
        options.addOption("showhud", false, "show heads up display");

        CommandLineParser parser = new GnuParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("url") && !cmd.getOptionValue("url").equals("")) {
            targetURL = cmd.getOptionValue("url");
        } else {
            System.err.println("-url has not been specified");
            System.exit(1);
        }

        if (cmd.hasOption("showhud"))
            showHUD = true;

        driver = new FirefoxDriver();
        driver.get(targetURL);
        js = (JavascriptExecutor) driver;

        while (true) {
          playHelicopter();
        }
    }

    public static void playHelicopter() throws Exception {
        controls = driver.findElement(By.id("helicopter"));

        while (!isGameReady()) {
            Thread.sleep(500);
        }

		System.out.println("==================");
		System.out.println("    LET'S GO!!");
		System.out.println("==================");

        controls.sendKeys(Keys.RETURN);
        thrustersOn = false;

        while (isHelicopterAlive() && scores.size() <= 50) {
            flyHelicopter();
        }

        gameOver();
    }

    public static boolean isGameReady() throws Exception {
        return (getState() == WAITING);
    }

    public static boolean isHelicopterAlive() throws Exception {
        return (getState() == PLAYING);
    }

    public static Map getInfo() throws InterruptedException {
        while (js.executeScript("return myHelicopter") == null) {
            Thread.sleep(500);
        }
        helicopterCache = (Map) js.executeScript("return myHelicopter.gameData()");
        if (showHUD) {
            js.executeScript("myHelicopter.updateVisualCue(" +
                    (HELICOPTER_COLUMN + VISIBILITY_START) + "," +
                    (HELICOPTER_COLUMN + VISIBILITY_START + visibilityDepth) + "," +
                    targetAltitude + ")");
        }
        return helicopterCache;
    }

    public static Object getInfo(String key) throws Exception {
        return getInfo().get(key);
    }

    public static int getState() throws Exception {
        return new Integer(getInfo("state").toString());
    }

	private static int getDistance() throws Exception {
        return new Integer(getInfo("distance").toString());
	}

	private static int getTargetAltitude() {
        List<Map> cave = (List<Map>) helicopterCache.get("terrain");

        visibilityDepth = (100 - new Integer(cave.get(HELICOPTER_COLUMN).get("top").toString()) -
                new Integer(cave.get(HELICOPTER_COLUMN).get("bottom").toString())) * VISIBILITY_DEPTH_MULTIPLIER;

        List<Map> caveAhead = cave.subList(HELICOPTER_COLUMN + VISIBILITY_START,
                Math.max(new Long(HELICOPTER_COLUMN + VISIBILITY_START + Math.round(visibilityDepth)).intValue(), cave.size()));
        List<Integer> caveRoofAhead = new ArrayList<Integer>();
        List<Integer> caveFloorAhead = new ArrayList<Integer>();
        for (Map column : caveAhead) {
            caveRoofAhead.add(100 - new Integer(column.get("top").toString()));
            caveFloorAhead.add(new Integer(column.get("bottom").toString()));
        }
        Collections.sort(caveRoofAhead);
        Collections.sort(caveFloorAhead);
        int lowestRoofAhead = caveRoofAhead.get(0);
        int highestFloorAhead = caveFloorAhead.get(caveFloorAhead.size() - 1);

        Map obstacle = (Map) helicopterCache.get("obstacle");
        Integer obstacleColumn = new Integer(obstacle.get("column").toString());
        Integer obstacleTop = new Integer(obstacle.get("top").toString());
        Integer obstacleHeight = new Integer(obstacle.get("height").toString());
        if (obstacleHeight > 0 &&
                obstacleColumn > HELICOPTER_COLUMN + VISIBILITY_START &&
                obstacleColumn < HELICOPTER_COLUMN + VISIBILITY_START + visibilityDepth) {
            int roofClearance = lowestRoofAhead - obstacleTop;
            int floorClearance = (obstacleTop - obstacleHeight) - highestFloorAhead;
            if (roofClearance > floorClearance) {
                return ((lowestRoofAhead - obstacleTop) /2) + obstacleTop;
            } else {
                return ((obstacleTop - obstacleHeight - highestFloorAhead) / 2) + highestFloorAhead;
            }
        } else {
            return ((lowestRoofAhead - highestFloorAhead) / 2) + highestFloorAhead;
        }
    }

    private static void flyHelicopter() throws InterruptedException {
        double currentAltitude = new Double(helicopterCache.get("position").toString());
        targetAltitude = getTargetAltitude();
        double momentum = new Double(helicopterCache.get("momentum").toString());
        double distanceFromTarget = currentAltitude - targetAltitude;
        if (distanceFromTarget + (momentum * 5) < 1) {
            if (!thrustersOn) {
                new WebDriverBackedSelenium(driver, "").mouseDown("id=helicopter");
                thrustersOn = true;
            }
        } else if (thrustersOn) {
            new WebDriverBackedSelenium(driver, "").mouseUp("id=helicopter");
            thrustersOn = false;
        }
    }

    private static void gameOver() throws Exception {
		System.out.println("==================");
		System.out.println("    GAME OVER!");
		System.out.println("==================");
		checkScores(getDistance());
	}

	private static void checkScores(int score) {
		if (scores.size() == 0 || score > Collections.max(scores)) {
            newHighScore(score);
        }
		scores.add(score);
        System.out.println("Score: " + score);
		System.out.println("==================");
		System.out.println("   HIGH SCORES:");
		System.out.println("==================");
        Collections.sort(scores);
        Collections.reverse(scores);
		for (int i=0; i < scores.size() && i < 10; i++) {
			System.out.println(i+1 + ": " + scores.get(i));
		}
        int totalScore = 0;
        for (int currentScore : scores) {
          totalScore += currentScore;
        }
		System.out.println();
        System.out.println("==================");
        System.out.println("    SUMMARY:");
        System.out.println("==================");
        System.out.println("Games: " + scores.size());
        System.out.println("Average Score: " + totalScore / scores.size());
        System.out.println();
	}

    private static void newHighScore(int score) {
        System.out.println("NEW HIGH SCORE!");
    }

}
