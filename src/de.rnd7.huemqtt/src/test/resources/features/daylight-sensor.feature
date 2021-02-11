Feature: Daylight sensor

  Background:
    Given I have a hue bridge with the following devices:
      | type     | id         |
      | daylight | daylight-1 |

  Scenario: It is daylight time
    When device "daylight-1" has the following properties:
      | daylight     | true                                     |
      | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |

    Then I expect "daylight-1" to have daylight time.
    And I expect the following message on topic "hue/daylight/daylight-1":
        """
        {
            "daylight": true,
            "last-updated": "2021-02-07T15:01:48+01:00[Europe/Berlin]"
        }
        """

  Scenario: It is not daylight time
    When device "daylight-1" has the following properties:
      | daylight     | false                                    |
      | last-updated | 2021-02-02T22:00:00+01:00[Europe/Berlin] |

    Then I expect "daylight-1" to not have daylight time.
    And I expect the following message on topic "hue/daylight/daylight-1":
        """
        {
            "daylight": false,
            "last-updated": "2021-02-02T22:00+01:00[Europe/Berlin]"
        }
        """
