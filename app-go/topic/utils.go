package topic

import "strings"

func Clean(topic string) string {
	result := strings.ToLower(topic)
	result = strings.ReplaceAll(result, " ", "-")
	result = strings.ReplaceAll(result, "ä", "ae")
	result = strings.ReplaceAll(result, "ö", "oe")
	result = strings.ReplaceAll(result, "ü", "ue")
	return result
}
