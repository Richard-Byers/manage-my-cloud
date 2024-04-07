import logging
import os
import unittest

from selenium.webdriver import ActionChains
from selenium.webdriver.common import actions
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

# Dashboard elements
connected_onedrive_container = "OneDrive-connected-item"
connected_onedrive_container_element = (By.ID, connected_onedrive_container)
drive_name = "drive-name"
drive_storage_used = "drive-used-storage"
drive_storage_used_progress_bar = "storage-used-progress-bar"
connected_drive_modal = (By.ID, "connected-drive-modal")
connected_item_files_container = "dashboard-card-modal-drive-files-container"
connected_item_drive_information = "dashboard-card-modal-drive-information"
delete_recommended_button = "delete-recommended-button"
delete_recommended_button_element = (By.ID, delete_recommended_button)
deletion_recommendation_container = (By.CLASS_NAME, "deletion-recommendation-container")
delete_duplicate_button = "delete-duplicates-button"

# Deletion recommendation elements
all_caught_up_message = "caught-up-with-message"
success_checkmark_container = "check-container"
done_button = "recommendation-done-button"
done_button_element = (By.ID, done_button)
deletion_recommendation_modal = (By.ID, "deletion-recommendation-modal")
deletion_recommendation_file_count = "item-recommendation-count"
deletion_recommendation_file_count_element = (By.ID, deletion_recommendation_file_count)
deletion_recommendation_description = "deletion-recommendation-description"
deletion_recommendation_description_element = (By.CLASS_NAME, deletion_recommendation_description)
deletion_recommendation_select_all = "select-all-checkbox"
deletion_recommendation_select_all_element = (By.ID, deletion_recommendation_select_all)
deletion_recommendation_file_container = "deletion-recommendation-file-container"
deletion_recommendation_file_container_element = (By.CLASS_NAME, deletion_recommendation_file_container)
delete_recommendations_button = "delete-recommendations-button"
delete_recommendations_button_element = (By.ID, delete_recommendations_button)
delete_recommendations_success = "deletion-success-message"
delete_recommendations_success_element = (By.ID, delete_recommendations_success)
success_deletion_close_button = "success-deletion-close-button"
success_deletion_close_button_element = (By.ID, success_deletion_close_button)

# Navigation
manage_connections_nav_link = (By.ID, "manage-connections-nav-link")
modal_overlay = (By.CLASS_NAME, "modal-overlay")
dashboard_nav_link = (By.ID, "dashboard-nav-link")
profile_nav_link = (By.ID, "profile-nav-link")
profile_preferences_button = (By.ID, "user-preferences-button")

# Preferences
recommend_items_after = (By.ID, "drive-items-created-after-dropdown-button")
update_profile_preferences_button = (By.ID, "update-profile-preferences-button")
update_profile_preferences_success_modal = (By.CLASS_NAME, "success-modal-container")

# Login
modal_login_button = (By.ID, "modal-login-button")


class TestDashboardPage(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        setup_driver(cls)
        login(cls, test_suite_email, test_email_password)

        cls.driver.find_element(By.ID, "manage-connections-nav-link").click()
        WebDriverWait(cls.driver, 10).until(
            EC.visibility_of_element_located((By.CLASS_NAME, "manage-connections-page-title-container")))

        # Link OneDrive to view on dashboard
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(add_connections_modal_button)).click()
        WebDriverWait(cls.driver, 10).until(EC.visibility_of_element_located(add_connections_modal))

        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(link_onedrive_button)).click()
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(email_field)).send_keys(test_suite_email)
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(next_button)).click()
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(password_field)).send_keys(test_email_password)
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(next_button)).click()
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(yes_button)).click()
        WebDriverWait(cls.driver, 10).until(EC.visibility_of_element_located(onedrive_container))
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(dashboard_nav_link)).click()
        WebDriverWait(cls.driver, 15).until(EC.visibility_of_element_located(connected_onedrive_container_element))

    def test_navbar_present_dashboard_page(self):
        nav_element = self.driver.find_element(By.CLASS_NAME, "nav")

        # assertions
        assert_element(nav_element, self, "Navbar not displayed on dashboard page",
                       "Navbar not found on dashboard page")
        logger.info('Finished test: test_navbar_present_dashboard_page')

    def test_connected_item_displayed_on_dashboard_page(self):
        connected_onedrive_element = self.driver.find_element(By.ID, connected_onedrive_container)
        drive_name_element = self.driver.find_element(By.ID, drive_name)
        drive_storage_used_element = self.driver.find_element(By.ID, drive_storage_used)
        drive_storage_used_progress_bar_element = self.driver.find_element(By.ID, drive_storage_used_progress_bar)

        # assertions
        assert_element(connected_onedrive_element, self, "Connected OneDrive container not displayed on dashboard page",
                       "Connected OneDrive container not found on dashboard page")
        assert_element(drive_name_element, self, "Drive name not displayed on dashboard page",
                       "Drive name not found on dashboard page")
        assert_element(drive_storage_used_element, self, "Drive storage used not displayed on dashboard page",
                       "Drive storage used not found on dashboard page")
        assert_element(drive_storage_used_progress_bar_element, self,
                       "Drive storage used progress bar not displayed on dashboard page",
                       "Drive storage used progress bar not found on dashboard page")
        logger.info('Finished test: test_connected_item_displayed_on_dashboard_page')

    def test_click_connected_item(self):
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(connected_onedrive_container_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(connected_drive_modal))

        connected_item_files_container_element = self.driver.find_element(By.CLASS_NAME, connected_item_files_container)
        connected_item_drive_information_element = self.driver.find_element(By.CLASS_NAME,
                                                                            connected_item_drive_information)
        delete_recommended_button_element = self.driver.find_element(By.ID, delete_recommended_button)
        delete_duplicate_button_element = self.driver.find_element(By.ID, delete_duplicate_button)

        # assertions
        assert_element(connected_item_files_container_element, self, "Connected item files container not displayed",
                       "Connected item files container not found")
        assert_element(connected_item_drive_information_element, self, "Connected item drive information not displayed",
                       "Connected item drive information not found")
        assert_element(delete_recommended_button_element, self, "Delete recommended button not displayed",
                       "Delete recommended button not found")
        assert_element(delete_duplicate_button_element, self, "Delete duplicate button not displayed",
                       "Delete duplicate button not found")
        modal_overlay_element = self.driver.find_element(By.CLASS_NAME, "modal-overlay")
        actions = ActionChains(self.driver)
        actions.move_to_element_with_offset(modal_overlay_element, 0, 350).click().perform()
        logger.info('Finished test: test_click_connected_item')

    def test_click_delete_recommended_default_preferences_item(self):
        expected_recommendation_message = "All caught up with Everything, Nothing to recommend."
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(connected_onedrive_container_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(connected_drive_modal))
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(delete_recommended_button_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(deletion_recommendation_container))

        all_caught_up_message_element = self.driver.find_element(By.ID, all_caught_up_message)
        success_checkmark_container_element = self.driver.find_element(By.CLASS_NAME, success_checkmark_container)
        done_button_element = self.driver.find_element(By.ID, done_button)

        # assertions
        self.assertEqual(expected_recommendation_message, all_caught_up_message_element.text)
        assert_element(all_caught_up_message_element, self, "All caught up message not displayed",
                       "All caught up message not found")
        assert_element(success_checkmark_container_element, self, "Success checkmark container not displayed",
                       "Success checkmark container not found")
        assert_element(done_button_element, self, "Done button not displayed", "Done button not found")

        # Close recommendation modal
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(done_button_element)).click()
        logger.info('Finished test: test_click_delete_recommended_default_preferences_item')

    def test_click_delete_recommended_changed_preferences_show_and_delete_recommended(self):
        actions = ActionChains(self.driver)
        expected_success_message = "Successfully deleted 1 file(s) and 0 email(s) from your drive."
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(profile_nav_link)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(profile_preferences_button)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(recommend_items_after)).click()

        WebDriverWait(self.driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//div[@id='react-select-3-option-0']"))).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_profile_preferences_button)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(update_profile_preferences_success_modal))
        update_profile_preferences_success_modal_element = self.driver.find_element(By.CLASS_NAME, "success-modal-container")
        actions.move_to_element_with_offset(update_profile_preferences_success_modal_element, 0, 250).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.CLASS_NAME, "success-modal-container")))
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(dashboard_nav_link)).click()
        WebDriverWait(self.driver, 15).until(EC.element_to_be_clickable(connected_onedrive_container_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(connected_drive_modal))
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(delete_recommended_button_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(deletion_recommendation_container))
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(deletion_recommendation_file_count_element))

        deletion_recommendation_file_count_element_returned = self.driver.find_element(By.ID,
                                                                                       deletion_recommendation_file_count)
        deletion_recommendation_description_element_returned = self.driver.find_element(By.CLASS_NAME,
                                                                                        deletion_recommendation_description)
        deletion_recommendation_select_all_element_returned = self.driver.find_element(By.ID,
                                                                                       deletion_recommendation_select_all)
        deletion_recommendation_file_container_element_returned = self.driver.find_element(By.CLASS_NAME,
                                                                                           deletion_recommendation_file_container)
        delete_recommendations_button_element_returned = self.driver.find_element(By.ID, delete_recommendations_button)

        # assertions
        assert_element(deletion_recommendation_file_count_element_returned, self,
                       "Deletion recommendation file count not displayed",
                       "Deletion recommendation file count not found")
        assert_element(deletion_recommendation_description_element_returned, self,
                       "Deletion recommendation description not displayed",
                       "Deletion recommendation description not found")
        assert_element(deletion_recommendation_select_all_element_returned, self,
                       "Deletion recommendation select all not displayed",
                       "Deletion recommendation select all not found")
        assert_element(deletion_recommendation_file_container_element_returned, self,
                       "Deletion recommendation file container not displayed",
                       "Deletion recommendation file container not found")
        assert_element(delete_recommendations_button_element_returned, self,
                       "Delete recommendations button not displayed",
                       "Delete recommendations button not found")

        WebDriverWait(self.driver, 10).until(
            EC.element_to_be_clickable(deletion_recommendation_select_all_element)).click()
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(delete_recommendations_button_element)).click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located(delete_recommendations_success_element))
        delete_recommendations_success_element_returned = self.driver.find_element(By.ID,
                                                                                   delete_recommendations_success)

        self.assertEqual(expected_success_message, delete_recommendations_success_element_returned.text)

        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable(success_deletion_close_button_element)).click()

        logger.info('Finished test: test_click_delete_recommended_changed_preferences_show_and_delete_recommended')

    @classmethod
    def tearDownClass(cls):
        WebDriverWait(cls.driver, 10).until(EC.visibility_of_element_located(manage_connections_nav_link)).click()
        unlink_drive_button_element = cls.driver.find_element(By.ID, "unlink-drive-button")
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(unlink_drive_button_element)).click()
        WebDriverWait(cls.driver, 10).until(EC.visibility_of_element_located(remove_connections_modal))
        unlink_drive_button_confirm_element = cls.driver.find_element(By.ID, "confirm-unlink")
        WebDriverWait(cls.driver, 10).until(EC.element_to_be_clickable(unlink_drive_button_confirm_element)).click()
        cls.driver.quit()


if __name__ == "__main__":
    unittest.main()
