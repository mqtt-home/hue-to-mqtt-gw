Feature: Ambient sensor

    Background:
        Given I have a hue bridge with the following devices:
        | type    | id        |
        | ambient | ambient-1 |

    Scenario: It is daylight time
        When device "ambient-1" has the following properties:
        | daylight     | true                                     |
        | dark         | false                                    |
        | level        | 512                                      |
        | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |

        Then I expect the ambient sensor "ambient-1" to have daylight time.
        Then I expect the ambient sensor "ambient-1" to be not dark.
        And I expect the following message on topic "hue/ambient/ambient-1":
        """
        {
            "dark":false,
            "daylight":true,
            "last-level":512,
            "last-updated":"2021-02-07T15:01:48+01:00[Europe/Berlin]"
        }
        """

    Scenario: It is not daylight time
        When device "ambient-1" has the following properties:
            | daylight     | false                                    |
            | dark         | true                                     |
            | level        | 5                                        |
            | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |

        Then I expect the ambient sensor "ambient-1" to not have daylight time.
        Then I expect the ambient sensor "ambient-1" to be dark.
        And I expect the following message on topic "hue/ambient/ambient-1":
        """
        {
            "dark":true,
            "daylight":false,
            "last-level":5,
            "last-updated":"2021-02-07T15:01:48+01:00[Europe/Berlin]"
        }
        """
