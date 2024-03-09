import unittest
from pages.landing.landing_page_selenium_tests import TestLandingPage
from pages.manage_connections.manage_connections_page_selenium_tests import TestManageConnectionsPage

if __name__ == "__main__":
    # Instantiate a test loader
    loader = unittest.TestLoader()

    # Load the test cases from the test suite class
    landing_page_suite = loader.loadTestsFromTestCase(TestLandingPage)
    manage_connections_page_suite = loader.loadTestsFromTestCase(TestManageConnectionsPage)

    combined_suite = unittest.TestSuite([landing_page_suite, manage_connections_page_suite])

    # Instantiate a test runner
    runner = unittest.TextTestRunner()
    runner.run(combined_suite)
