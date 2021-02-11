Feature: Button

  Background:
    Given I have a hue bridge with the following devices:
      | type   | id       |
      | button | button-1 |

  Scenario: Button pressed
    When device "button-1" has the following properties:
      | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |

    Then I expect the following message on topic "hue/switch/button-1":
        """
        {
            "button":0,
            "code":0,
            "last-updated":"2021-02-07T15:01:48+01:00[Europe/Berlin]"
        }
        """

  Scenario: Top left button pressed
    When device "button-1" has the following properties:
      | last-updated | 2021-02-10T12:01:02+01:00[Europe/Berlin] |
      | button       | 1                                        |
      | code         | 20                                       |

    Then I expect the following message on topic "hue/switch/button-1":
        """
        {
            "button":1,
            "code":20,
            "last-updated":"2021-02-10T12:01:02+01:00[Europe/Berlin]"
        }
        """
