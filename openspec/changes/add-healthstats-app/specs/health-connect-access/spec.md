## Purpose

Lets the app safely determine whether it can read from Health Connect, obtain only the permissions it needs, and discover which source apps have contributed data, without ever writing to the store.

## ADDED Requirements

### Requirement: Health Connect availability detection
The system SHALL check Health Connect's availability status before attempting any data read, and SHALL branch its behavior based on that status rather than assuming availability.

#### Scenario: Health Connect not installed or outdated
- **WHEN** the availability check reports that Health Connect is not installed or needs an update
- **THEN** the system directs the user to install or update Health Connect before any data access is attempted

#### Scenario: Health Connect available
- **WHEN** the availability check reports Health Connect is available
- **THEN** the system proceeds to the permission request flow

#### Scenario: Device unsupported
- **WHEN** the availability check reports Health Connect cannot run on this device
- **THEN** the system informs the user the app cannot function on this device, without crashing

### Requirement: Per-type permission request
The system SHALL request read-only permission for each Health Connect record type it needs (exercise session, distance, heart rate, total calories burned, elevation gained, speed, steps) individually, and SHALL track which of those permissions are actually granted.

#### Scenario: All permissions granted
- **WHEN** the user grants every requested permission
- **THEN** the system enables all supported data fields for browsing and detail views

#### Scenario: Partial permissions granted
- **WHEN** the user grants only some requested permissions (e.g. exercise session and distance, but not heart rate)
- **THEN** the system operates using the granted types only and omits fields for ungranted types, without treating the omission as an error

#### Scenario: All permissions denied
- **WHEN** the user denies every requested permission
- **THEN** the system explains that it cannot show data without at least exercise session read access, and offers a way to re-trigger the permission request

### Requirement: Source discovery
The system SHALL enumerate the distinct source apps present in the user's currently accessible exercise session data, for use as selectable filter values elsewhere in the app.

#### Scenario: Multiple sources present
- **WHEN** exercise session data exists from more than one source app
- **THEN** the system lists each distinct source as a selectable filter option

#### Scenario: No data yet
- **WHEN** no exercise session data is accessible
- **THEN** the system presents an empty state rather than an empty or broken filter list

### Requirement: Read-only access
The system SHALL only ever read Health Connect records. It SHALL NOT request write permission for, insert, modify, or delete any record.

#### Scenario: No write permission requested
- **WHEN** the app requests Health Connect permissions
- **THEN** none of the requested permissions are write permissions
