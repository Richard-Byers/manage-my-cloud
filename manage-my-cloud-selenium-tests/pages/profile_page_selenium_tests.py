import logging
import os
import unittest

from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

from helpers import assert_element, login, setup_driver

# Create a custom logger
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)

# Create handlers for logging test results
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

# profile
profile_nav = "profile-navigation-menu"
profile_nav_button = (By.ID, "user-profile-button")
profile_image = "profile-img-button"
firstname = "profile-firstname"
firstname_element = (By.ID, firstname)
lastname = "profile-lastname"
lastname_element = (By.ID, lastname)
email = "profile-email"
update_details_button = "profile-update-details-button"
update_details_button_element = (By.ID, update_details_button)
update_details_modal = "update-details-modal"
update_details_modal_element = (By.ID, update_details_modal)
update_details_firstname = "update-details-first-name"
update_details_firstname_element = (By.ID, update_details_firstname)
update_details_lastname = "update-details-last-name"
update_details_lastname_element = (By.ID, update_details_lastname)
update_details_submit = "update-details-submit"
update_details_submit_element = (By.ID, update_details_submit)
logout = "profile-logout-button"

# preferences
preferences_nav_button = (By.ID, "user-preferences-button")
delete_videos_toggle = "delete-videos-toggle"
delete_images_toggle = "delete-images-toggle"
delete_documents_toggle = "delete-documents-toggle"
delete_emails_toggle = "delete-emails-toggle"
delete_items_created_after = "drive-items-created-after-dropdown"
delete_items_not_edited_since = "drive-items-not-edited-since-dropdown"
delete_emails_after = "recommend-emails-after-dropdown"
update_preferences_button = "update-profile-preferences-button"

# advanced
advanced_nav_button = (By.ID, "user-profile-actions-button")
request_data_button = "request-data-button"
view_terms_of_service_button = "terms-of-service-button"
contact_us_button = "contact-us-button"
delete_account_button = "delete-account-button"


class TestProfilePage(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        setup_driver(cls)
        # This part of the setup logs the user in and navigates to manage connections page
        login(cls, test_suite_email, test_email_password)
        navbar_element = cls.driver.find_element(By.ID, "profile-nav-link")
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(navbar_element)).click()

    def test_navbar_present_profile_page(self):
        nav_element = self.driver.find_element(By.CLASS_NAME, "nav")

        # assertions
        assert_element(nav_element, self, "Navbar not displayed on profile page", "Navbar is wasn't found")
        logger.info('Finished test: test_navbar_present_profile_page')

    def test_profile_page_content_visible(self):
        profile_nav_element = self.driver.find_element(By.ID, profile_nav)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(profile_nav_button)).click()
        profile_image_element = self.driver.find_element(By.ID, profile_image)
        firstname_element = self.driver.find_element(By.ID, firstname)
        lastname_element = self.driver.find_element(By.ID, lastname)
        email_element = self.driver.find_element(By.ID, email)
        update_details_element = self.driver.find_element(By.ID, update_details_button)
        logout_element = self.driver.find_element(By.ID, logout)

        # assertions

        assert_element(profile_nav_element, self, "Profile navigation menu not displayed on profile page",
                       "Profile navigation menu not found")
        assert_element(profile_image_element, self, "Profile image not displayed on profile page",
                       "Profile image not found")
        assert_element(firstname_element, self, "Firstname not displayed on profile page", "Firstname not found")
        assert_element(lastname_element, self, "Lastname not displayed on profile page", "Lastname not found")
        assert_element(email_element, self, "Email not displayed on profile page", "Email not found")
        assert_element(update_details_element, self, "Update details button not displayed on profile page",
                       "Update details button not found")
        assert_element(logout_element, self, "Logout button not displayed on profile page", "Logout button not found")
        logger.info('Finished test: test_profile_page_content_visible')

    def test_profile_page_click_preferences(self):
        profile_nav_element = self.driver.find_element(By.ID, profile_nav)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(preferences_nav_button)).click()
        recommend_videos_toggle_element = self.driver.find_element(By.ID, delete_videos_toggle)
        recommend_images_toggle_element = self.driver.find_element(By.ID, delete_images_toggle)
        recommend_documents_toggle_element = self.driver.find_element(By.ID, delete_documents_toggle)
        recommend_emails_toggle_element = self.driver.find_element(By.ID, delete_emails_toggle)
        recommend_items_created_after_element = self.driver.find_element(By.ID, delete_items_created_after)
        recommend_items_not_edited_since_element = self.driver.find_element(By.ID, delete_items_not_edited_since)
        recommend_emails_after_element = self.driver.find_element(By.ID, delete_emails_after)
        update_preferences_element = self.driver.find_element(By.ID, update_preferences_button)

        # assertions

        assert_element(profile_nav_element, self, "Profile navigation menu not displayed on profile page",
                       "Profile navigation menu not found")
        assert_element(recommend_videos_toggle_element, self, "Recommend videos toggle not displayed on profile page",
                       "Recommend videos toggle image not found")
        assert_element(recommend_images_toggle_element, self, "Recommend images toggle not displayed on profile page",
                       "Recommend images toggle not found")
        assert_element(recommend_documents_toggle_element, self,
                       "Recommend documents toggle not displayed on profile page",
                       "Recommend documents toggle not found")
        assert_element(recommend_emails_toggle_element, self, "Recommend email toggle not displayed on profile page",
                       "Recommend email toggle not found")
        assert_element(recommend_items_created_after_element, self,
                       "Recommend items created after dropdown not displayed on profile page",
                       "Recommend items created after dropdown not found")
        assert_element(recommend_items_not_edited_since_element, self,
                       "Recommend items not edited since dropdown not displayed on profile page",
                       "Recommend items not edited since dropdown not found")
        assert_element(recommend_emails_after_element, self,
                       "Recommend emails after dropdown not displayed on profile page",
                       "Recommend emails after dropdown not found")
        assert_element(update_preferences_element, self, "Update preferences button not displayed on profile page",
                       "Update preferences button not found")
        logger.info('Finished test: test_profile_page_click_preferences')

    def test_profile_page_click_advanced(self):
        profile_nav_element = self.driver.find_element(By.ID, profile_nav)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(advanced_nav_button)).click()
        request_data = self.driver.find_element(By.ID, request_data_button)
        view_terms_of_service = self.driver.find_element(By.ID, view_terms_of_service_button)
        delete_account = self.driver.find_element(By.ID, delete_account_button)

        # assertions

        assert_element(profile_nav_element, self, "Profile navigation menu not displayed on profile page",
                       "Profile navigation menu not found")
        assert_element(request_data, self, "Request data button not displayed on profile page",
                       "Request data button not found")
        assert_element(view_terms_of_service, self, "View terms of service button not displayed on profile page",
                       "View terms of service button not found")
        assert_element(delete_account, self, "Delete account button not displayed on profile page",
                       "Delete account button not found")
        logger.info('Finished test: test_profile_page_click_advanced')

    def test_update_details(self):
        firstname_entry = "profile-firstname"
        lastname_entry = "profile-lastname"
        reset_firstname_entry = "test"
        reset_lastname_entry = "test"

        current_firstname = WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(firstname_element)).text
        current_lastname = WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(lastname_element)).text

        self.assertEqual("First Name : test", current_firstname, "Current firstname hasn't been reset")
        self.assertEqual("Last Name : test", current_lastname, "Current lastname hasn't been reset")

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_button_element)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_firstname_element)).send_keys(
            firstname_entry)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_lastname_element)).send_keys(
            lastname_entry)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_submit_element)).click()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element(update_details_submit_element))

        updated_firstname = WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(firstname_element)).text
        updated_lastname = WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(lastname_element)).text

        self.assertEqual("First Name : profile-firstname", updated_firstname, "Firstname hasn't been updated")
        self.assertEqual("Last Name : profile-lastname", updated_lastname, "Lastname hasn't been updated")

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_button_element)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_firstname_element)).send_keys(
            reset_firstname_entry)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_lastname_element)).send_keys(
            reset_lastname_entry)
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_details_submit_element)).click()

        logger.info('Finished test: test_update_details')

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()


if __name__ == "__main__":
    unittest.main()
