import logging
import os
import time
import unittest

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

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


class TestManageConnectionsPage(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.driver = webdriver.Chrome()
        cls.driver.maximize_window()

    def setUp(self):
        self.driver.get("http://localhost:3000")

        # This part of the setup logs the user in and navigates to manage connections page
        self.driver.find_element(By.ID, "modal-login-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))

        self.driver.find_element(By.ID, "login-form-email").send_keys(test_suite_email)
        self.driver.find_element(By.ID, "login-form-password").send_keys(test_email_password)

        WebDriverWait(self.driver, 10).until(EC.text_to_be_present_in_element_value((By.ID, 'login-form-password'), ''))

        self.driver.find_element(By.CLASS_NAME, "modal-form-submit-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "main-card-container")))

        self.driver.find_element(By.ID, "manage-connections-nav-link").click()
        WebDriverWait(self.driver, 10).until(
            EC.visibility_of_element_located((By.CLASS_NAME, "manage-connections-page-title-container")))

    def test_navbar_present_manage_connections_page(self):
        self.driver.find_element(By.CLASS_NAME, "nav")

        # assertions
        logger.info('Finished test: test_login_valid_credentials_navigates_to_profile')

    def test_no_connections_text_present_manage_connections_page(self):
        expected_no_connections_text = "To link an account press the button below"
        WebDriverWait(self.driver, 10).until(
            EC.visibility_of_element_located((By.CLASS_NAME, "manage-connections-page-link-text")))
        no_connections_text_element = self.driver.find_element(By.CLASS_NAME, "manage-connections-page-link-text")

        # assertions
        self.assertTrue(no_connections_text_element.is_displayed(),
                        "No connections text not displayed.")
        self.assertIsNotNone(no_connections_text_element,
                             "No connections text element not found.")
        self.assertEqual(expected_no_connections_text, no_connections_text_element.text)

        logger.info('Finished test: test_no_connections_text_present_manage_connections_page')

    def test_link_onedrive(self):
        expected_drive_type_text = "OneDrive"
        expected_drive_email_text = test_suite_email
        self.driver.find_element(By.CLASS_NAME, "add-connections-modal-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "add-connections-modal")))

        self.driver.find_element(By.CLASS_NAME, "link-onedrive-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.ID, "lightbox")))

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(email_field)).send_keys(test_suite_email)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(next_button)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(password_field)).send_keys(test_email_password)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(next_button)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(yes_button)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.ID, "OneDrive-container")))

        linked_drive_type_element = self.driver.find_element(By.ID, "linked-drive-type")
        linked_drive_email_element = self.driver.find_element(By.ID, "linked-drive-email")
        unlink_drive_button_element = self.driver.find_element(By.ID, "unlink-drive-button")

        # assertions
        self.assertTrue(linked_drive_type_element.is_displayed(),
                        "Linked drive type not displayed.")
        self.assertIsNotNone(linked_drive_type_element,
                             "Linked drive type element not found.")

        self.assertTrue(linked_drive_email_element.is_displayed(),
                        "Linked drive email not displayed.")
        self.assertIsNotNone(linked_drive_email_element,
                             "Linked drive email element not found.")

        self.assertTrue(unlink_drive_button_element.is_displayed(),
                        "Unlink drive button not displayed.")
        self.assertIsNotNone(unlink_drive_button_element,
                             "Unlink drive button element not found.")

        self.assertEqual(expected_drive_type_text, linked_drive_type_element.text)
        self.assertEqual(expected_drive_email_text, linked_drive_email_element.text)

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(unlink_drive_button_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "remove-connections-modal")))
        unlink_drive_button_confirm_element = self.driver.find_element(By.ID, "confirm-unlink")
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(unlink_drive_button_confirm_element)).click()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.ID, "OneDrive-container")))

        logger.info('Finished test: test_link_onedrive')

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()


if __name__ == "__main__":
    unittest.main()
