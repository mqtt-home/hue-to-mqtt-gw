Feature: Light

    Background:
        Given I have a hue bridge with the following devices:
        | type         | id      |
        | ct_light     | light-1 |
        | color_light  | light-2 |

    Scenario: Color temperature light
        When device "light-1" has the following properties:
        | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |
        | color_temp   | 366                                      |
        | brightness   | 254                                      |
        | state        | on                                       |

        Then I expect the following message on topic "hue/light/light-1":
        """
        {
            "state": "ON",
            "brightness": 254,
            "color_temp": 366
        }
        """

    Scenario: Color temperature light turned off
        When device "light-1" has the following properties:
            | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |
            | color_temp   | 100                                      |
            | brightness   | 50                                       |
            | state        | off                                      |

        Then I expect the following message on topic "hue/light/light-1":
        """
        {
            "state": "OFF",
            "brightness": 50,
            "color_temp": 100
        }
        """

    Scenario: Color light
        When device "light-2" has the following properties:
            | last-updated | 2021-02-07T15:01:48+01:00[Europe/Berlin] |
            | color        | #FF6088                                  |
            | state        | on                                       |

        Then I expect the following message on topic "hue/light/light-2":
        """
        {
            "state":"ON",
            "brightness":95,
            "color": {
                "x":0.5361151,
                "y":0.27739376
            }
        }
        """
