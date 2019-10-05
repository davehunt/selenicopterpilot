# Selenicopter Pilot

This project is an attempt at playing the HTML5 game [Helicopter](http://arandomurl.com/2010/08/05/html5-helicopter.html) created by [Dale Harvey](https://github.com/daleharvey) using the [Selenium](http://seleniumhq.org) automation tool. Selenicopter Pilot required a few changes to the Helicopter game, so you will need to clone the [Selenicopter](https://github.com/davehunt/selenicopter) repository to get these modifications.

## Playing the game

### Requirements

* [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/index.html) is required to play the game with Selenium.
* [Ant](http://ant.apache.org/) is required to use the simple build script.

### How to play

From the project's `java` directory, enter the following command in a command prompt:

    ant -Durl=file:///Users/dave/workspace/selenicopter/index.html

If you want to display the HUD (heads up display) then use the `run-with-hud` ant target:

    ant run-with-hud -Durl=file:///Users/dave/workspace/selenicopter/index.html

Replace the URL with the location of your clone of the Selenicopter project.

## Running the tests

### Requirements

* The tests are written in [Python](http://www.python.org/) so you will require that to be installed.
* You will require the Selenium module (at least version 2.0b3). To install using pip: `sudo pip install selenium`
* You will need to modify the URL in `base_page.py` to match the location of your clone of the Selenicopter project.

### How to run

From the project's `python` folder, enter the following in a command prompt:

    python test_crash

### Known issues

The Python bindings do not currently support WebdriverBackedSelenium or Advanced User Interactions, and therefore holding the mouse button down is not simple. As a result, the `testShouldCrashIntoCeiling` will currently fail.

## Other resources

* Slides: 
 * [Automating Canvas Applications Using Selenium](http://www.slideshare.net/davehunt82/automating-canvas-applications-using-selenium)
* Videos: 
 * [Playing the helicopter game](http://www.youtube.com/watch?v=0jhJVshJhJg)
 * [Playing the helicopter game using Selenium (no obstacles)](http://www.youtube.com/watch?v=eLW-5Fi4CtQ)
 * [Playing the helicopter game using Selenium (with HUD)](http://www.youtube.com/watch?v=eQ6Kaka1DD4)
