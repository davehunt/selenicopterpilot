#!/usr/bin/env python

import unittest
from selenium import webdriver

from base_page import BasePage


class CrashTests(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Firefox()

    def testShouldCrashIntoCeiling(self):
        base_page = BasePage(self.driver)
        base_page.start_game()
        base_page.engage_thrusters()
        base_page.wait_until_crash()
        self.assertEquals(base_page.helicopter_altitude, 90)

    def testShouldCrashIntoFloor(self):
        base_page = BasePage(self.driver)
        base_page.start_game()
        base_page.wait_until_crash()
        self.assertEquals(base_page.helicopter_altitude, 14)

    def tearDown(self):
        self.driver.quit()

if __name__ == "__main__":
    unittest.main()
