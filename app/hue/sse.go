package hue

import (
	"bufio"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	"sync"
	"time"

	"github.com/mqtt-home/hue2mqtt/config"
	"github.com/philipparndt/go-logger"
)

type SSEClient struct {
	cfg       config.HueConfig
	onEvent   func(HueEvent)
	onStatus  func(string)
	stopCh    chan struct{}
	mu        sync.Mutex
	resp      *http.Response
	lastEvent time.Time
	watchdog  *time.Ticker
}

type SSEOptions struct {
	OnEvent  func(HueEvent)
	OnStatus func(status string)
}

func StartSSE(cfg config.HueConfig, opts SSEOptions) func() {
	sse := &SSEClient{
		cfg:      cfg,
		onEvent:  opts.OnEvent,
		onStatus: opts.OnStatus,
		stopCh:   make(chan struct{}),
	}

	go sse.connectLoop()

	return func() {
		close(sse.stopCh)
		sse.closeConnection()
		sse.stopWatchdog()
	}
}

func (s *SSEClient) connectLoop() {
	for {
		select {
		case <-s.stopCh:
			return
		default:
			s.connect()
			if s.onStatus != nil {
				s.onStatus("disconnected")
			}
			// Wait before reconnecting
			select {
			case <-s.stopCh:
				return
			case <-time.After(5 * time.Second):
				logger.Info("[SSE] Reconnecting...")
			}
		}
	}
}

func (s *SSEClient) connect() {
	logger.Info("[SSE] Starting Server-Sent events")

	url := fmt.Sprintf("%s://%s:%d/eventstream/clip/v2",
		s.cfg.Protocol, s.cfg.Host, s.cfg.Port)

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		logger.Error("[SSE] Failed to create request", "error", err)
		return
	}

	req.Header.Set("hue-application-key", s.cfg.APIKey)
	req.Header.Set("Accept", "text/event-stream")

	httpClient := &http.Client{
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{
				InsecureSkipVerify: true,
			},
		},
	}

	resp, err := httpClient.Do(req)
	if err != nil {
		logger.Error("[SSE] Connection failed", "error", err)
		return
	}

	s.mu.Lock()
	s.resp = resp
	s.lastEvent = time.Now()
	s.mu.Unlock()

	s.startWatchdog()

	if s.onStatus != nil {
		s.onStatus("connected")
	}
	logger.Info("[SSE] Connected, reading events")

	scanner := bufio.NewScanner(resp.Body)
	// Increase buffer for potentially large SSE messages
	scanner.Buffer(make([]byte, 0, 1024*1024), 1024*1024)

	var dataLines []string

	for scanner.Scan() {
		select {
		case <-s.stopCh:
			resp.Body.Close()
			return
		default:
		}

		line := scanner.Text()

		// SSE protocol: data lines are accumulated, empty line dispatches the event
		if strings.HasPrefix(line, "data: ") {
			dataLines = append(dataLines, strings.TrimPrefix(line, "data: "))
			continue
		}

		// Empty line = end of SSE event, dispatch accumulated data
		if line == "" && len(dataLines) > 0 {
			fullData := strings.Join(dataLines, "\n")
			dataLines = nil

			s.mu.Lock()
			s.lastEvent = time.Now()
			s.mu.Unlock()

			logger.Trace("[SSE] Raw event data", "length", len(fullData), "data", fullData)

			var events []HueEvent
			if err := json.Unmarshal([]byte(fullData), &events); err != nil {
				logger.Error("[SSE] Failed to parse event", "error", err, "data", fullData[:min(len(fullData), 200)])
				continue
			}

			logger.Trace("[SSE] Parsed events", "count", len(events))

			for _, event := range events {
				logger.Trace("[SSE] Event", "type", event.Type, "id", event.ID, "dataCount", len(event.Data))
				for _, d := range event.Data {
					logger.Trace("[SSE] Event data", "type", d.Type, "id", d.ID)
				}
				s.onEvent(event)
			}
			continue
		}

		// id: and other SSE fields - just skip
		if strings.HasPrefix(line, ":") || strings.HasPrefix(line, "id:") {
			continue
		}
	}

	if err := scanner.Err(); err != nil {
		logger.Error("[SSE] Scanner error", "error", err)
	}

	resp.Body.Close()
	s.stopWatchdog()
}

func (s *SSEClient) closeConnection() {
	s.mu.Lock()
	defer s.mu.Unlock()
	if s.resp != nil {
		s.resp.Body.Close()
		s.resp = nil
	}
}

func (s *SSEClient) startWatchdog() {
	if s.cfg.SSEWatchdogMs <= 0 {
		logger.Info("[SSE] Watchdog disabled")
		return
	}

	millis := time.Duration(s.cfg.SSEWatchdogMs) * time.Millisecond
	logger.Info("[SSE] Watchdog enabled", "timeout", millis)

	s.watchdog = time.NewTicker(millis / 2)
	go func() {
		for {
			select {
			case <-s.stopCh:
				return
			case <-s.watchdog.C:
				s.mu.Lock()
				elapsed := time.Since(s.lastEvent)
				s.mu.Unlock()

				logger.Debug("[SSE] Checking watchdog", "elapsed", elapsed, "timeout", millis)
				if elapsed > millis {
					logger.Error("[SSE] Watchdog triggered, resetting")
					s.closeConnection()
					return
				}
			}
		}
	}()
}

func (s *SSEClient) stopWatchdog() {
	if s.watchdog != nil {
		s.watchdog.Stop()
		s.watchdog = nil
	}
}
