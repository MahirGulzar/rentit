Feature: Creation of Purchase Order
  As a Rentit's customer
  So that I start with the construction project
  I want hire all the required machinery

  Background: Plant catalog
    Given the following plant catalog
      | id | name           | description                      | price  |
      |  1 | Mini Excavator | 1.5 Tonne Mini excavator         | 150.00 |
      |  2 | Mini Excavator | 3 Tonne Mini excavator           | 200.00 |
      |  3 | Midi Excavator | 5 Tonne Midi excavator           | 250.00 |
      |  4 | Midi Excavator | 8 Tonne Midi excavator           | 300.00 |
      |  5 | Maxi Excavator | 15 Tonne Large excavator         | 400.00 |
      |  6 | Maxi Excavator | 20 Tonne Large excavator         | 450.00 |
      |  7 | HS Dumper      | 1.5 Tonne Hi-Swivel Dumper       | 150.00 |
      |  8 | FT Dumper      | 2 Tonne Front Tip Dumper         | 180.00 |
    And the following inventory
      | id | plantInfo | serialNumber | equipmentCondition |
      |  1 |     1     | exc-mn1.5-01 | SERVICEABLE        |
      |  2 |     2     | exc-mn3.0-01 | SERVICEABLE        |
      |  3 |     3     | exc-md5.0-01 | SERVICEABLE        |
      |  4 |     4     | exc-md8.0-01 | SERVICEABLE        |
      |  5 |     5     | exc-max15-01 | SERVICEABLE        |
      |  6 |     6     | exc-max20-01 | SERVICEABLE        |
      |  7 |     7     | dmp-hs1.5-01 | SERVICEABLE        |
      |  8 |     8     | dmp-ft2.0-01 | SERVICEABLE        |
    And a customer is in the "Plant Catalog" web page
    And no purchase order exists in the system

  Scenario: Querying the plant catalog for an excavator
    When the customer queries the plant catalog for an "Excavator" available from "002018/05/20" to "002018/05/22"
    Then 6 plants are shown
    When the customer selects a "3 Tonne Mini excavator"
    Then a purchase order should be created with a total price of 600.00 on Rentit`s side
    When the purchase order is accepted on rentit`s side
    Then BuildIT is notified about the result

  Scenario: Quering the plant catalog for an excavator
    When the customer queries the plant catalog for an "Excavator" available from "002018/05/20" to "002018/05/22"
    Then 6 plants are shown
    When the customer selects a "3 Tonne Mini excavator"
    Then a purchase order should be created with a total price of 600.00
    When the purchase order is rejected on rentit`s side
    Then BuildIT is notified about the result

  Scenario: Quering the plant catalog for available plants
    When the customer queries the plant catalog with equipment condition serviceable
    Then 8 plants are shown
    When The customer selects a "3 Tonne Mini excavator"
