#!/usr/bin/env python

import time

from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.common.exceptions import WebDriverException


class BasePage():

    _controls_locator = (By.ID, "helicopter")

    def __init__(self, driver):
        self.driver = driver
        driver.get("file:///Users/dave/workspace/selenicopter/with-obstacles/index.html")

    def _cache_debug(self):
        while True:
            try:
                self._debug = self.driver.execute_script("return myHelicopter.gameData()")
                break
            except WebDriverException:
                time.sleep(0.5)

    @property
    def game_state(self):
        self._cache_debug()
        return self._debug["state"]

    @property
    def helicopter_altitude(self):
        return self._debug["position"]

    def _wait_until_game_is_ready(self):
        while not self.game_state == 1:
            time.sleep(0.5)

    def wait_until_crash(self):
        while not self.game_state == 4:
            time.sleep(0.5)

    def start_game(self):
        self._wait_until_game_is_ready()
        controls = self._find_element(self._controls_locator)
        controls.send_keys(Keys.RETURN)

    def engage_thrusters(self):
        controls = self._find_element(self._controls_locator)
        controls.send_keys(Keys.RETURN)

    def _find_element(self, locator):
        return self.driver.find_element(locator[0], locator[1])
