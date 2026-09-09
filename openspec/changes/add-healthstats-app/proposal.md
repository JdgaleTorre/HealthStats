## Why

Google Health Connect aggregates activity data from apps like Strava, Garmin Connect, and Fitbit on-device, but there is no way to browse, filter, or analyze that data across sources without opening each source app individually. This change creates an open-source Android app that reads directly from Health Connect and gives the user a unified, filterable view of their activity/exercise history, including trend analysis (e.g., "my 5K times over the last year") that no single source app provides on its own.

## What Changes

- New Android app (Kotlin, Jetpack Compose) that reads Health Connect activity/exercise data. Read-only — the app never writes to Health Connect.
- No backend, no accounts, no local database. Health Connect is the sole data store; app state is in-memory and re-queried on demand.
- Health Connect permission and availability flow: detect SDK status, request per-record-type permissions, guide the user to install/update Health Connect when required.
- Session list screen: browse `ExerciseSessionRecord`s filtered by source app (`dataOrigin`), exercise type, and date range. Rows show only data intrinsic to the session record (no per-row aggregate queries).
- Session detail screen: on-demand `aggregate()` queries scoped to one session's time window, showing distance/duration/calories always, and heart rate/elevation/speed/steps when the selected source provides them. Single source at a time, with a source switcher — no cross-source merging in v1.
- Trends screen: chart (via Vico) of pace or finish time for a chosen exercise type and distance bucket (e.g., Running, 5K ± tolerance) across a date range, computed from per-session distance/duration aggregates. Single source at a time.
- Scope is deliberately narrow for v1: activity/exercise data only (no vitals, sleep, nutrition, or cycle tracking), no GPS route/map rendering, no in-run km-by-km splits (not exposed by Health Connect for sources like Strava), no cross-source data merging.
- Distribution as an open-source APK via GitHub Releases and F-Droid. All dependencies chosen to be F-Droid-compatible (fully FOSS, no proprietary blobs, reproducible-build-friendly) from the start.

## Capabilities

### New Capabilities
- `health-connect-access`: Detecting Health Connect availability, requesting and tracking per-record-type permissions, and discovering available data sources (apps that have written data) for use in filters.
- `session-browsing`: Listing exercise sessions with source/type/date filters, and viewing per-session detail computed via on-demand aggregate queries against a single selected source.
- `trends`: Charting pace/time for a chosen exercise type and distance bucket across a date range, for a single selected source.

### Modified Capabilities
(none — greenfield project)

## Impact

- New Android application project (Kotlin, Jetpack Compose, Coroutines/Flow, manual DI, single module, minSdk 26).
- New dependency: `androidx.health.connect:connect-client` for Health Connect access.
- New dependency: Vico for charting.
- New build/release tooling: GitHub Actions to produce signed release APKs; F-Droid metadata/build recipe for reproducible builds.
- No impact on existing systems — this is a new, standalone project with no prior code.
