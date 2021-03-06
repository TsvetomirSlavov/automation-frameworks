@amazon-uk
@search
Feature: Search
  In order to find an item on Amazon UK
  As a customer
  I want to search for the item

  Background:
    Given I am on Amazon UK
    And the search bar is displayed
    When I search for iPad in Electronics & Photo
    And there are search results displayed

  Scenario: verify that the price of the first result matches user's input
    Then the price of the first result should be £297.00

  Scenario: print out number of results found and assert the price of the last result
    Then the number of results displayed should be more than 0
    And the price of the last result should not be £295.00
