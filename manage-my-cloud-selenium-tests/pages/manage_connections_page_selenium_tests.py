import logging
import os
import unittest

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

from helpers import assert_element, login, setup_driver

# Create a custom logger
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

# Create handlers
c_handler = logging.StreamHandler()
c_handler.setLevel(logging.INFO)

# Create formatters and add it to handlers
c_format = logging.Formatter('%(name)s - %(levelname)s - %(message)s')
c_handler.setFormatter(c_format)

# Add handlers to the logger
logger.addHandler(c_handler)

test_suite_email = os.environ['TEST_EMAIL']
test_email_password = os.environ['TEST_EMAIL_PASSWORD']
test_onedrive_password = os.environ['ONEDRIVE_TEST_PASSWORD']

# OneDrive login fields
email_field = (By.ID, "i0116")
password_field = (By.ID, "i0118")
next_button = (By.ID, "idSIButton9")
yes_button = (By.ID, "acceptButton")

# Google Drive login fields
google_email_field = (By.ID, "identifierId")
google_password_field = (By.ID, "")
google_next_button = (By.ID, "identifierNext")
google_yes_button = (By.ID, "acceptButton")

# Linked drive elements
add_connections_modal = (By.CLASS_NAME, "add-connections-modal")
link_onedrive_button = (By.CLASS_NAME, "link-onedrive-button")
link_google_drive_button = (By.CLASS_NAME, "link-googledrive-button")
onedrive_container = (By.ID, "OneDrive-container")
linked_drive_type = (By.ID, "linked-drive-type")
linked_drive_email = (By.ID, "linked-drive-email")
unlink_drive_button = (By.ID, "unlink-drive-button")
remove_connections_modal = (By.CLASS_NAME, "remove-connections-modal")
confirm_unlink = (By.ID, "confirm-unlink")
add_connections_modal_button = (By.CLASS_NAME, "add-connections-modal-button")

# Login
modal_login_button = (By.ID, "modal-login-button")


class TestManageConnectionsPage(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        setup_driver(cls)
        login(cls, test_suite_email, test_email_password)

        cls.driver.find_element(By.ID, "manage-connections-nav-link").click()
        WebDriverWait(cls.driver, 10).until(
            EC.visibility_of_element_located((By.CLASS_NAME, "manage-connections-page-title-container")))

    def test_navbar_present_manage_connections_page(self):
        nav_element = self.driver.find_element(By.CLASS_NAME, "nav")

        # assertions
        assert_element(nav_element, self, "Navbar not displayed on manage connections page",
                       "Navbar not found on manage connections page")
        logger.info('Finished test: test_navbar_present_manage_connections_page')

    def test_no_connections_text_present_manage_connections_page(self):
        expected_no_connections_text = "To link an account press the button below"
        WebDriverWait(self.driver, 10).until(
            EC.visibility_of_element_located((By.CLASS_NAME, "manage-connections-page-link-text")))
        no_connections_text_element = self.driver.find_element(By.CLASS_NAME, "manage-connections-page-link-text")

        # assertions
        assert_element(no_connections_text_element, self, "No connections text not displayed.",
                       "No connections text not found.")
        self.assertEqual(expected_no_connections_text, no_connections_text_element.text)

        logger.info('Finished test: test_no_connections_text_present_manage_connections_page')

    def test_link_and_unlink_onedrive(self):
        expected_drive_type_text = "OneDrive"
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(add_connections_modal_button)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(add_connections_modal))

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(link_onedrive_button)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.ID, "lightbox")))

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(email_field)).send_keys(test_suite_email)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(next_button)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(password_field)).send_keys(test_email_password)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(next_button)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(yes_button)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(onedrive_container))

        linked_drive_type_element = self.driver.find_element(By.ID, "linked-drive-type")
        linked_drive_email_element = self.driver.find_element(By.ID, "linked-drive-email")
        unlink_drive_button_element = self.driver.find_element(By.ID, "unlink-drive-button")

        # assertions
        assert_element(linked_drive_type_element, self, "Linked drive type not displayed.",
                       "Linked drive type not found.")
        assert_element(linked_drive_email_element, self, "Linked drive email not displayed.",
                       "Linked drive email not found.")
        assert_element(unlink_drive_button_element, self, "Unlink drive button not displayed.",
                       "Unlink drive button not found.")
        self.assertEqual(expected_drive_type_text, linked_drive_type_element.text)

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(unlink_drive_button_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(remove_connections_modal))
        unlink_drive_button_confirm_element = self.driver.find_element(By.ID, "confirm-unlink")
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(unlink_drive_button_confirm_element)).click()

        try:
            WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(onedrive_container))
            self.fail("OneDrive container still visible after unlinking")
        except:
            pass

        logger.info('Finished test: test_link_and_unlink_onedrive')

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()


if __name__ == "__main__":
    unittest.main()
