## Context

Greenfield Android project. See proposal.md for motivation. Key constraints established during exploration:
- Health Connect is the only data store — no backend, no accounts, no local database.
- Confirmed via Strava's own support docs: Strava writes only time, distance, and calories to Health Connect (no heart rate, elevation, speed, steps, laps, or route). Other sources (Garmin, Fitbit, etc.) may write more. The app must stay source-generic and degrade gracefully per source rather than assuming a fixed field set.
- Health Connect record metadata distinguishes event time (`startTime`/`endTime`, set by the writer to reflect reality) from `lastModifiedTime` (sync bookkeeping) — only event time is valid for correlating records.
- Distribution target: GitHub Releases and F-Droid, both from v1.

## Goals / Non-Goals

**Goals:**
- Read-only, dependency-light architecture that maps cleanly onto Health Connect's query model.
- Correct behavior across sources with wildly different data richness (Strava's 3 fields vs. a wearable's dozen).
- F-Droid buildability from the first commit, not retrofitted later.

**Non-Goals:**
- Cross-source data merging (deferred — see proposal).
- GPS route/map rendering (deferred).
- Any data written back to Health Connect.
- Long-term local caching or offline history beyond what a single query round-trip provides.

## Decisions

**1. Layering: Compose UI -> ViewModel (StateFlow) -> Repository -> `HealthConnectClient`.**
No Room, no network layer, no DI framework-managed multi-module graph. The repository is a thin, directly-testable wrapper translating filter state into `readRecords`/`aggregate` calls. Rejected: Clean Architecture-style multi-module split — unjustified indirection for an app with one real external dependency (Health Connect) and no persistence of its own.

**2. Cross-record correlation by event-time window, never `lastModifiedTime`.**
Any query that ties a session to its distance/HR/calories/etc. scopes a `TimeRangeFilter` to the session's own `startTime`..`endTime`, combined with a `DataOriginFilter` for the active source. `lastModifiedTime` is never used for matching — it reflects sync timing, not when the activity happened, and would produce wrong pairings for sources that sync with delay (e.g., Strava syncing hours after a run).

**3. Lean list, on-demand detail (Option B from exploration).**
The session list renders directly from `ExerciseSessionRecord` fields only — no `aggregate()` call per row. Detail and Trends screens are what trigger `aggregate()` queries, scoped narrowly (one session's window for detail; one bucket-matching set of sessions for trends). Rejected: eagerly computing aggregates for every visible row — an N-sessions × M-record-types query cost that doesn't scale to a multi-year Strava history and buys little the user actually asked for (they asked to browse, then drill in or chart, not to see every stat at a glance).

**4. Filter option lists are discovered from the user's actual data, not hardcoded.**
Both the source-app filter and the exercise-type filter populate from what's actually present (distinct `dataOrigin`s seen; distinct `ExerciseType`s seen), not from Health Connect's full type enums (~80 exercise types). Keeps the UI relevant to what the user actually has instead of a long mostly-empty picker.

**5. Trend computation uses whole-session aggregates only, with a user-adjustable distance tolerance.**
Since Health Connect exposes no reliable per-kilometer split/lap data for common sources (confirmed absent for Strava), "5K" is necessarily a tolerance bucket (default ±0.2 km, user-adjustable) applied to each session's total aggregated distance — not an exact-match or a derived-from-splits calculation. This is a modeling choice forced by the data source, not an implementation detail; it's reflected in the trends spec as "target ± tolerance."

**6. Manual dependency wiring, no Hilt.**
The DI graph is one `HealthConnectClient` instance and a handful of ViewModels. A simple factory/container is enough; Hilt's annotation processing overhead isn't justified at this scale. Revisit only if the graph grows materially (e.g., if a future change adds real persistence or multiple data sources requiring more complex composition).

**7. Vico for charting.**
Compose-native, Apache-2.0, actively maintained, fits the single-series line/scatter chart Trends needs without hand-rolling axis/scaling/touch-target logic. Chosen over hand-rolled Canvas (more code to own for marginal gain) and over View-based libraries like MPAndroidChart (would require Compose interop for no benefit).

**8. minSdk 26, runtime SDK-status branching.**
Matches `androidx.health.connect:connect-client`'s own floor. At runtime, `HealthConnectClient.getSdkStatus()` drives three paths: unavailable (unsupported device), update-required (prompt to install/update Health Connect via Play Store — independent of how our own app was installed), available (proceed).

**9. Distribution: GitHub Actions release build + F-Droid metadata, both from v1.**
All chosen dependencies (AndroidX Health Connect client, Vico) are fully open-source with no proprietary blobs, keeping the app F-Droid-eligible without later rework. Any dependency added in a future change (e.g., if maps come back into scope) should be evaluated against this same constraint before being added.

**10. Single module.**
Three screens, no DB, no backend — module boundaries would encode structure the app doesn't have yet. Revisit if the project grows enough to need independently testable/buildable units.

## Risks / Trade-offs

- **Strava's narrow export (time/distance/calories only) caps what a Strava-only user sees** → Mitigation: app stays source-generic per decision 3/4; UI omits absent fields rather than faking zeros, so richer sources (Garmin, Fitbit) aren't held back by Strava's ceiling, and the limitation is visible rather than silently wrong.
- **No caching means every filter change re-queries Health Connect** → Mitigation: Health Connect's local store is fast for personal-scale history; acceptable for v1. In-memory memoization within a ViewModel session can be added later without any spec change if it proves necessary.
- **Distance-bucket tolerance is a heuristic, not an exact race-distance match** → Mitigation: explicitly modeled as target ± tolerance in the spec, user-adjustable, not presented as authoritative race categorization.
- **F-Droid's reproducible-build/no-proprietary-deps requirement constrains future additions** → Mitigation: documented now (decision 9) so later changes (e.g., reintroducing maps) default to FOSS-compatible choices (e.g., an OSM-based library) instead of Google Maps SDK.
- **A 6-7 entry Health Connect permission screen may deter first-time users** → Mitigation: per-type rationale copy, and the app functions in a degraded-but-working mode on partial grants (per health-connect-access spec) rather than hard-blocking on all-or-nothing consent.

## Migration Plan

Greenfield project — no existing users or data to migrate. Recommended build order for tasks.md to follow: (1) Health Connect availability/permission/source-discovery flow, since every other screen depends on it; (2) session list with filters; (3) session detail with on-demand aggregation; (4) trends chart; (5) release pipeline (GitHub Actions signing) and F-Droid submission metadata, once the app is functionally complete enough to release.
