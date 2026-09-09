## Purpose

Lets the user browse their exercise session history across whichever source apps have written to Health Connect, filter it down, and inspect the detail of a single session without the app merging data across sources or paying a per-row query cost just to render the list.

## ADDED Requirements

### Requirement: Filterable session list
The system SHALL let the user filter the exercise session list by source app, exercise type, and date range, with filters applying in combination (AND, not OR).

#### Scenario: Filter by source
- **WHEN** the user selects a single source app as a filter
- **THEN** the list shows only sessions written by that source

#### Scenario: Filter by exercise type
- **WHEN** the user selects an exercise type as a filter
- **THEN** the list shows only sessions of that type

#### Scenario: Filter by date range
- **WHEN** the user selects a date range
- **THEN** the list shows only sessions whose start time falls within that range

#### Scenario: Combined filters
- **WHEN** the user has source, type, and date range filters all active
- **THEN** the list shows only sessions matching all three simultaneously

#### Scenario: No matches
- **WHEN** no sessions match the active filters
- **THEN** the system shows an empty state rather than a blank or broken list

### Requirement: Lean list rendering
The system SHALL render each session list row using only fields present on the exercise session record itself (exercise type, title, start time, end time, source app). It SHALL NOT issue an aggregate or detail query per row to render the list.

#### Scenario: Large history renders without extra queries
- **WHEN** the filtered list contains many sessions
- **THEN** rendering the list issues no additional query per session beyond the single query that retrieved the session records

### Requirement: On-demand session detail
When the user opens a session, the system SHALL compute distance, duration, and calories for that session via an aggregate query scoped to that session's own start/end time window and its own source. It SHALL additionally show heart rate, elevation gained, speed, and steps when the same source has contributed that data within the session's time window.

#### Scenario: Source provides only base fields
- **WHEN** the opened session's source has only ever written time, distance, and calories to Health Connect
- **THEN** the detail view shows distance, duration, and calories, and does not display heart rate, elevation, speed, or steps as if they were zero or present

#### Scenario: Source provides richer fields
- **WHEN** the opened session's source has also written heart rate, elevation, speed, or steps data overlapping the session's time window
- **THEN** the detail view includes those fields alongside distance, duration, and calories

### Requirement: Single-source detail, no cross-source merging
The system SHALL scope a session's detail aggregation to the same source app that wrote the session record. It SHALL NOT combine data from a different source into that session's detail view.

#### Scenario: Overlapping data from another source exists
- **WHEN** another source app has data overlapping the opened session's time window
- **THEN** that other source's data is not included in the detail view unless the user explicitly changes the active source filter
