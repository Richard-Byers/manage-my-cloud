import logging
import os
import unittest

from selenium.common import NoSuchElementException
from selenium.webdriver import ActionChains
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

from helpers import assert_element, setup_driver

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


class TestLandingPage(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        setup_driver(cls)

    def test_logo_present_landing_page(self):
        logo_element = self.driver.find_element(By.CLASS_NAME, "landing-page-logo")

        # assertions
        assert_element(logo_element, self, "Logo not displayed on landing page", "Logo is wasn't found")
        logger.info('Finished test: test_logo_present_landing_page')

    def test_main_text_present_landing_page(self):
        main_text_element = self.driver.find_element(By.CLASS_NAME, "landing-page-main-text")

        # assertions
        assert_element(main_text_element, self, "Main text not displayed on landing page", "Main text is wasn't found")
        self.assertEqual(main_text_element.text, "OPTIMISE YOUR CLOUD STORAGE")
        logger.info('Finished test: test_main_text_present_landing_page')

    def test_sub_text_present_landing_page(self):
        sub_text_element = self.driver.find_element(By.CLASS_NAME, "landing-page-sub-text")

        # assertions
        assert_element(sub_text_element, self, "Sub text not displayed on landing page", "Sub text is wasn't found")
        self.assertEqual(sub_text_element.text, "AND SAVE MONEY")
        logger.info('Finished test: test_sub_text_present_landing_page')

    def test_sub_text_description_present_landing_page(self):
        sub_text_description_element = self.driver.find_element(By.CLASS_NAME, "landing-page-sub-text-description")

        # assertions
        assert_element(sub_text_description_element, self,
                       "Landing page sub text description not displayed on landing page",
                       "Landing page sub text description is wasn't found")
        self.assertEqual(sub_text_description_element.text,
                         "We aim to de-clutter your cloud storage, save you money, and reduce the cloud storage carbon footprint.")
        logger.info('Finished test: test_sub_text_description_present_landing_page')

    def test_get_started_button_present_landing_page(self):
        get_started_button_element = self.driver.find_element(By.CLASS_NAME, "modal-login-button")

        # assertions
        assert_element(get_started_button_element, self,
                       "Landing page get started button not displayed on landing page",
                       "Landing page get started button is wasn't found")
        self.assertEqual(get_started_button_element.text, "Get Started")
        logger.info('Finished test: test_get_started_button_present_landing_page')

    def test_get_started_button_displays_login(self):
        self.driver.find_element(By.CSS_SELECTOR, ".modal-login-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))
        login_modal_element = self.driver.find_element(By.CLASS_NAME, "modal")
        login_modal_description_element = self.driver.find_element(By.CLASS_NAME, "modal-description")
        login_modal_form_google_login_button_element = self.driver.find_element(By.CLASS_NAME,
                                                                                "modal-login-google-button")
        login_modal_form_element = self.driver.find_element(By.CLASS_NAME, "modal-form")
        login_modal_form_email_input_element = self.driver.find_element(By.ID, "login-form-email")
        login_modal_form_password_input_element = self.driver.find_element(By.ID, "login-form-password")
        login_modal_login_button_element = self.driver.find_element(By.CLASS_NAME, "modal-form-submit-button")

        # assertions
        assert_element(login_modal_element, self, "Login modal not displayed", "Login modal not found")
        assert_element(login_modal_description_element, self, "Login modal description not displayed",
                       "Login modal description not found")
        assert_element(login_modal_form_google_login_button_element, self,
                       "Login modal google login button not displayed", "Login modal google login button not found")
        assert_element(login_modal_form_element, self, "Login modal form not displayed", "Login modal form not found")
        assert_element(login_modal_form_email_input_element, self, "Login modal email input not displayed",
                       "Login modal email input not found")
        assert_element(login_modal_form_password_input_element, self, "Login modal password input not displayed",
                       "Login modal password input not found")
        assert_element(login_modal_login_button_element, self, "Login modal login button not displayed",
                       "Login modal login button not found")
        login_modal_overlay_element = self.driver.find_element(By.CLASS_NAME, "modal-overlay")
        actions = ActionChains(self.driver)

        actions.move_to_element_with_offset(login_modal_overlay_element, 0, 250).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.CLASS_NAME, "modal")))
        logger.info('Finished test: test_get_started_button_displays_login')

    def test_modal_overlay_closes_login_modal_onclick(self):
        self.driver.find_element(By.CLASS_NAME, "modal-login-button").click()
        WebDriverWait(self.driver, 5).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))
        login_modal_overlay_element = self.driver.find_element(By.CLASS_NAME, "modal-overlay")
        actions = ActionChains(self.driver)

        actions.move_to_element_with_offset(login_modal_overlay_element, 0, 250).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.CLASS_NAME, "modal")))

        # assertions
        try:
            self.driver.find_element(By.CLASS_NAME, "modal")
            self.fail("Modal still present after clicking overlay")
        except NoSuchElementException:
            pass
        logger.info('Finished test: test_modal_overlay_closes_login_modal_onclick')

    def test_signup_displays_signup_modal(self):
        self.driver.find_element(By.CLASS_NAME, "modal-login-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))

        self.driver.find_element(By.ID, "signup-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.ID, "signup-modal")))

        signup_modal_element = self.driver.find_element(By.ID, "signup-modal")
        signup_modal_description_element = self.driver.find_element(By.CLASS_NAME, "modal-description")
        signup_modal_form_element = self.driver.find_element(By.ID, "signup-modal-form")
        signup_modal_form_firstname_input_element = self.driver.find_element(By.ID, "signup-firstname-input")
        signup_modal_form_lastname_input_element = self.driver.find_element(By.ID, "signup-lastname-input")
        signup_modal_form_email_input_element = self.driver.find_element(By.ID, "signup-email-input")
        signup_modal_form_password_input_element = self.driver.find_element(By.ID, "signup-password-input")
        signup_modal_form_confirm_password_input_element = self.driver.find_element(By.ID,
                                                                                    "signup-confirm-password-input")
        signup_modal_signup_button_element = self.driver.find_element(By.ID, "signup-modal-submit-button")

        # assertions
        assert_element(signup_modal_element, self, "Signup modal not displayed", "Signup modal not found")
        assert_element(signup_modal_description_element, self, "Signup modal description not displayed",
                       "Signup modal description not found")
        assert_element(signup_modal_form_element, self, "Signup modal form not displayed",
                       "Signup modal form not found")
        assert_element(signup_modal_form_firstname_input_element, self, "Signup modal firstname input not displayed",
                       "Signup modal firstname input not found")
        assert_element(signup_modal_form_lastname_input_element, self, "Signup modal lastname input not displayed",
                       "Signup modal lastname input not found")
        assert_element(signup_modal_form_email_input_element, self, "Signup modal email input not displayed",
                       "Signup modal email input not found")
        assert_element(signup_modal_form_password_input_element, self, "Signup modal password input not displayed",
                       "Signup modal password input not found")
        assert_element(signup_modal_form_confirm_password_input_element, self,
                       "Signup modal confirm password input not displayed",
                       "Signup modal confirm password input not found")
        assert_element(signup_modal_signup_button_element, self, "Signup modal signup button not displayed",
                       "Signup modal signup button not found")
        login_modal_overlay_element = self.driver.find_element(By.CLASS_NAME, "modal-overlay")
        actions = ActionChains(self.driver)

        actions.move_to_element_with_offset(login_modal_overlay_element, 0, 350).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.ID, "signup-modal-submit-button")))
        actions.move_to_element_with_offset(login_modal_overlay_element, 0, 350).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.CLASS_NAME, "modal-login-google-button")))
        logger.info('Finished test: test_signup_displays_signup_modal')

    def test_reset_password_displays_reset_password_modal(self):
        self.driver.find_element(By.CLASS_NAME, "modal-login-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))

        self.driver.find_element(By.ID, "reset-password-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.ID, "reset-password-modal")))

        reset_password_modal_element = self.driver.find_element(By.ID, "reset-password-modal")
        signup_modal_description_element = self.driver.find_element(By.CLASS_NAME, "modal-description")
        reset_password_modal_form_element = self.driver.find_element(By.ID, "reset-password-modal-form")
        reset_password_modal_form_email_input_element = self.driver.find_element(By.ID, "reset-password-email-input")
        reset_password_modal_form_password_input_element = self.driver.find_element(By.ID,
                                                                                    "reset-password-password-input")
        reset_password_modal_form_confirm_password_input_element = self.driver.find_element(By.ID,
                                                                                            "reset-password-confirm-password-input")
        reset_password_modal_reset_password_button_element = self.driver.find_element(By.ID,
                                                                                      "reset-password-modal-submit-button")

        # assertions
        assert_element(reset_password_modal_element, self, "Reset password modal not displayed",
                       "Reset password modal not found")
        assert_element(signup_modal_description_element, self, "Reset password modal description not displayed",
                       "Reset password modal description not found")
        assert_element(reset_password_modal_form_element, self, "Reset password modal form not displayed",
                       "Reset password modal form not found")
        assert_element(reset_password_modal_form_email_input_element, self,
                       "Reset password modal email input not displayed", "Reset password modal email input not found")
        assert_element(reset_password_modal_form_password_input_element, self,
                       "Reset password modal password input not displayed",
                       "Reset password modal password input not found")
        assert_element(reset_password_modal_form_confirm_password_input_element, self,
                       "Reset password confirm password input not displayed",
                       "Reset password confirm password input not found")
        assert_element(reset_password_modal_reset_password_button_element, self,
                       "Reset password modal reset password button not displayed",
                       "Reset password modal reset password button not found")
        login_modal_overlay_element = self.driver.find_element(By.CLASS_NAME, "modal-overlay")
        actions = ActionChains(self.driver)

        actions.move_to_element_with_offset(login_modal_overlay_element, 0, 250).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.ID, "reset-password-modal-submit-button")))
        actions.move_to_element_with_offset(login_modal_overlay_element, 0, 250).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.CLASS_NAME, "modal-login-google-button")))
        logger.info('Finished test: test_reset_password_displays_reset_password_modal')

    def test_login_invalid_credentials_shows_error(self):
        self.driver.find_element(By.CLASS_NAME, "modal-login-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))

        self.driver.find_element(By.ID, "login-form-email").send_keys("invalidemail@gmail.com")
        self.driver.find_element(By.ID, "login-form-password").send_keys("12345")

        WebDriverWait(self.driver, 10).until(EC.text_to_be_present_in_element_value((By.ID, 'login-form-password'), ''))

        self.driver.find_element(By.CLASS_NAME, "modal-form-submit-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal-form-error")))

        error_message_element = self.driver.find_element(By.CLASS_NAME, "modal-form-error").text

        # assertions
        self.assertEqual("Invalid email or password", error_message_element, "Error message not found")
        login_modal_overlay_element = self.driver.find_element(By.CLASS_NAME, "modal-overlay")
        actions = ActionChains(self.driver)

        actions.move_to_element_with_offset(login_modal_overlay_element, 0, 250).click().perform()
        WebDriverWait(self.driver, 10).until(EC.invisibility_of_element_located((By.CLASS_NAME, "modal")))
        logger.info('Finished test: test_login_invalid_credentials_shows_error')

    def test_login_valid_credentials_navigates_to_profile(self):
        self.driver.find_element(By.CLASS_NAME, "modal-login-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))

        self.driver.find_element(By.ID, "login-form-email").send_keys(test_suite_email)
        self.driver.find_element(By.ID, "login-form-password").send_keys(test_email_password)

        WebDriverWait(self.driver, 10).until(EC.text_to_be_present_in_element_value((By.ID, 'login-form-password'), ''))

        self.driver.find_element(By.CLASS_NAME, "modal-form-submit-button").click()
        WebDriverWait(self.driver, 10).until(EC.visibility_of_element_located((By.ID, "profile-nav-link"))).click()

        profile_card_element = self.driver.find_element(By.CLASS_NAME, "main-card-container")

        # assertions
        assert_element(profile_card_element, self, "Profile card not displayed.", "Profile card element not found")
        WebDriverWait(self.driver, 10).until(EC.element_to_be_clickable((By.ID, "profile-logout-button"))).click()
        logger.info('Finished test: test_login_valid_credentials_navigates_to_profile')

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()


if __name__ == "__main__":
    unittest.main()
