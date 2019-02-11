Feature: Benchmark reactive Application
  Scenario: Single Thread on Localhost
    Given a running Server
    When Benchmark Application with single Thread on localhost
    Then Print and Save the results in File