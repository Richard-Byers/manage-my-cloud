import unittest

from dashboard_page_selenium_tests import TestDashboardPage
from landing_page_selenium_tests import TestLandingPage
from manage_connections_page_selenium_tests import TestManageConnectionsPage
from profile_page_selenium_tests import TestProfilePage

if __name__ == "__main__":
    # Instantiate a test loader
    loader = unittest.TestLoader()

    # Load the test cases from the test suite class
    landing_page_suite = loader.loadTestsFromTestCase(TestLandingPage)
    manage_connections_page_suite = loader.loadTestsFromTestCase(TestManageConnectionsPage)
    profile_page_suite = loader.loadTestsFromTestCase(TestProfilePage)
    dashboard_page_suite = loader.loadTestsFromTestCase(TestDashboardPage)

    combined_suite = unittest.TestSuite(
        [landing_page_suite, manage_connections_page_suite, profile_page_suite, dashboard_page_suite])

    # Instantiate a test runner
    runner = unittest.TextTestRunner()
    runner.run(combined_suite)
