## 1. Project Setup

- [x] 1.1 Scaffold Android project (Kotlin, Jetpack Compose, single module, minSdk 26) and verify it builds and installs via `./gradlew assembleDebug`
- [x] 1.2 Add `androidx.health.connect:connect-client` and Vico dependencies, verify `./gradlew build` resolves them and no proprietary/non-FOSS transitive dependency is pulled in
- [x] 1.3 Set up manual DI wiring (a single container providing `HealthConnectClient` and ViewModel factories), verify a ViewModel can be instantiated through it in a unit test
- [x] 1.4 Configure GitHub Actions CI workflow to run build + unit tests on push/PR, verify a PR triggers a green run

## 2. Health Connect Access

- [x] 2.1 Implement SDK availability check (`getSdkStatus`) with unavailable/update-required/available branches, verify with unit tests covering all three
- [x] 2.2 Implement read-only permission request flow for exercise session, distance, heart rate, calories, elevation, speed, and steps, verify granted/partial/denied states each drive the correct app state in unit tests
- [x] 2.3 Implement source discovery (distinct `dataOrigin` values across accessible exercise sessions), verify with a unit test against fixture data
- [x] 2.4 Implement exercise-type discovery (distinct types present in accessible sessions, not the full ~80-value enum) for filter options, verify with a unit test
- [x] 2.5 Verify no write permission is ever requested, via a unit test enumerating the app's requested permission set

## 3. Session Browsing

- [x] 3.1 Implement repository query for exercise sessions filtered by source, exercise type, and date range (individually and combined), verify with unit tests per filter and combination
- [x] 3.2 Build the session list Compose screen rendering only session-record fields, verify a test asserts no per-row aggregate query is issued (e.g. query-count assertion against a fake client)
- [x] 3.3 Implement the empty-state UI for no matching sessions, verify with a UI test
- [ ] 3.4 Implement on-demand session detail aggregation scoped to the session's own time window and source, verify unit tests for a base-fields-only source and a richer-fields source
- [ ] 3.5 Build the session detail Compose screen, showing available fields and omitting absent ones (never showing fake zeros), verify with UI tests for both source-richness scenarios
- [ ] 3.6 Verify detail view never merges another source's overlapping-time data, via a unit test with fixture data from two overlapping sources

## 4. Trends

- [ ] 4.1 Implement distance-bucket filtering (target ± tolerance, default ±0.2 km) over per-session aggregated distance, verify unit tests including the default 5K preset and a custom bucket
- [ ] 4.2 Implement pace and finish-time metric computation from whole-session aggregates only, verify unit tests for both metrics
- [ ] 4.3 Build the Trends Compose screen with a Vico chart plus exercise-type, bucket, date-range, and source controls, verify a UI test renders the chart correctly against fixture data
- [ ] 4.4 Implement the empty-state UI for no matching sessions, verify with a UI test
- [ ] 4.5 Verify switching the active source recomputes the trend using only that source's sessions, via a unit test

## 5. Release & Distribution

- [ ] 5.1 Generate a release signing keystore and store it as GitHub encrypted secrets, verify a signed release APK can be produced via CI
- [ ] 5.2 Configure a GitHub Actions release workflow that builds and attaches the signed APK to a GitHub Release on tag push, verify a test tag produces a downloadable artifact
- [ ] 5.3 Write F-Droid metadata/build recipe, verify it passes local F-Droid build/lint tooling
- [ ] 5.4 Audit all dependencies for FOSS license and reproducible-build compatibility, verify no proprietary or build-time-network-fetching dependency remains
