Feature: Presence sensor

    Background:
        Given I have a hue bridge with the following devices:
        | type      | id         |
        | presence  | presence-1 |

    Scenario: No presence
        When device "presence-1" has the following properties:
        | presence     | false                                    |
        | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |

        Then I expect "presence-1" to have no presence.
        And I expect the following message on topic "hue/presence/presence-1":
        """
        {
            "presence": false,
            "last-updated": "2021-02-07T15:01:48+01:00[Europe/Berlin]"
        }
        """

    Scenario: Presence
        When device "presence-1" has the following properties:
            | presence     | true                                     |
            | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |

        Then I expect "presence-1" to have presence.
        And I expect the following message on topic "hue/presence/presence-1":
        """
        {
            "presence": true,
            "last-updated": "2021-02-07T15:01:48+01:00[Europe/Berlin]"
        }
        """
