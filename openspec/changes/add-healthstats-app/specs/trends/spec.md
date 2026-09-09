## Purpose

Lets the user see how their performance for a specific kind of effort — such as "5K running times" — has changed over time, computed from whole-session totals since Health Connect does not expose in-run splits.

## ADDED Requirements

### Requirement: Distance-bucketed session selection
The system SHALL let the user choose an exercise type, a target distance, and a tolerance, and SHALL include in the trend only sessions of that exercise type whose aggregated total distance falls within target distance ± tolerance.

#### Scenario: Default bucket
- **WHEN** the user selects exercise type "Running" and a "5K" preset
- **THEN** the system includes only running sessions whose total distance falls within the preset's target ± tolerance

#### Scenario: Custom bucket
- **WHEN** the user enters a custom target distance and tolerance
- **THEN** the system includes only sessions of the selected exercise type within that custom range

#### Scenario: No matching sessions
- **WHEN** no sessions of the selected type fall within the chosen distance bucket
- **THEN** the system shows an empty state rather than an empty or broken chart

### Requirement: Trend chart over time
The system SHALL plot the selected metric (finish time or pace) for each session matching the distance bucket against that session's date, restricted to a user-selected date range.

#### Scenario: Metric = finish time
- **WHEN** the user selects finish time as the metric
- **THEN** each matching session appears on the chart at its date with its total duration as the value

#### Scenario: Metric = pace
- **WHEN** the user selects pace as the metric
- **THEN** each matching session appears on the chart at its date with duration divided by distance as the value

#### Scenario: Date range applied
- **WHEN** the user narrows the date range
- **THEN** only matching sessions within that range appear on the chart

### Requirement: Single-source trend computation
The system SHALL compute the trend only from sessions written by the currently selected single source app. It SHALL NOT combine sessions from multiple sources into one trend.

#### Scenario: Switching source
- **WHEN** the user switches the active source
- **THEN** the chart recomputes using only sessions from the newly selected source

### Requirement: Whole-session computation only
The system SHALL compute each session's distance and duration from that session's own aggregated totals. It SHALL NOT depend on per-kilometer split or lap data, since Health Connect sources are not guaranteed to provide it.

#### Scenario: Source has no split/lap data
- **WHEN** a session's source has written only overall session distance and duration, with no lap or route data
- **THEN** the session still appears correctly on the trend chart using its total distance and duration
