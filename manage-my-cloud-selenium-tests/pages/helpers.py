from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.wait import WebDriverWait


def setup_driver(self):
    self.driver = webdriver.Chrome()
    self.driver.maximize_window()
    self.driver.get("http://localhost:3000")


def assert_element(element, self, true_failure_message, none_failure_message):
    self.assertTrue(element, true_failure_message)
    self.assertIsNotNone(element, none_failure_message)


def login(driver, email, password):
    driver.driver.find_element(By.CLASS_NAME, "modal-login-button").click()
    WebDriverWait(driver.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "modal")))

    driver.driver.find_element(By.ID, "login-form-email").send_keys(email)
    driver.driver.find_element(By.ID, "login-form-password").send_keys(password)

    WebDriverWait(driver.driver, 10).until(EC.text_to_be_present_in_element_value((By.ID, 'login-form-password'), ''))

    driver.driver.find_element(By.CLASS_NAME, "modal-form-submit-button").click()
    WebDriverWait(driver.driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "navbar-nav")))
