Feature: Daylight sensor

    Background:
        Given I have a hue bridge with the following devices:
        | type         | id            |
        | temperature  | temperature-1 |

    Scenario: The temperature is published
        When device "temperature-1" has the following properties:
        | temperature  | 22.5                                     |
        | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |

        Then I expect "temperature-1" to have "22.5" °C
        And I expect the following message on topic "hue/temperature/temperature-1":
        """
        {
            "temperature": 22.5,
            "last-updated": "2021-02-07T15:01:48+01:00[Europe/Berlin]"
        }
        """
