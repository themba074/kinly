# Kinly Design System - Ocean Sky

**Status:** Approved visual direction  
**Version:** 0.1  
**Selected:** 4 November 2026

## 1. Direction

Ocean Sky is Kinly's selected visual direction. It uses bright, calm surfaces,
deep ocean-blue structure, turquoise actions, and restrained status colors. The
interface should feel clear, trustworthy, and humane rather than clinical,
surveillant, or alarm-driven.

The canonical concept is
[`mockups/kinly-selected-ui.png`](mockups/kinly-selected-ui.png). It establishes
the visual hierarchy and feature language, but it is not a pixel-perfect
implementation specification. Native Android and iOS components must preserve
platform behaviour and accessibility.

## 2. Core principles

- **Clarity before decoration:** freshness, accuracy, consent, and delivery
  state must be easier to notice than decorative elements.
- **Calm by default:** ordinary family activity uses cool, quiet colors.
- **Danger has one meaning:** red is reserved for SOS, destructive actions, and
  genuine errors.
- **Status is never color-only:** pair color with text, shape, or an icon.
- **Native, not identical:** Android and iOS may differ in controls and motion
  while sharing tokens, hierarchy, wording, and state meaning.
- **Privacy is visible:** sharing state and audience must never be buried or
  disguised.

## 3. Provisional color tokens

These tokens translate the selected mockup into implementable values. They must
be checked in real native components for WCAG contrast and adjusted before the
first production UI is approved.

| Token | Value | Intended use |
| --- | --- | --- |
| `ocean-900` | `#0B2E59` | Primary headings and high-emphasis text |
| `ocean-700` | `#0756A3` | Selected navigation and primary controls |
| `sky-500` | `#25A9E8` | Secondary actions and map accents |
| `sky-100` | `#DDF3FF` | Selected or informational surfaces |
| `sky-050` | `#F3FAFE` | App background |
| `surface` | `#FFFFFF` | Cards, sheets, and dialogs |
| `ink-900` | `#10233D` | Default body text |
| `ink-600` | `#52657A` | Secondary text |
| `border` | `#D7E3EC` | Dividers and component outlines |
| `fresh-600` | `#159A73` | Current, delivered, active, or healthy state |
| `fresh-100` | `#DCF7EC` | Positive status surface |
| `stale-600` | `#B7791F` | Delayed, stale, or attention-needed state |
| `stale-100` | `#FFF1CF` | Attention status surface |
| `danger-600` | `#D93F32` | SOS, destructive actions, and errors |
| `danger-100` | `#FDE5E1` | Danger surface |

Do not use `danger-600` for ordinary navigation, decorative badges, marketing,
or routine location movement.

## 4. Typography

- Android uses the platform's approved Material typography based on Roboto.
- iOS uses Dynamic Type and the San Francisco system family.
- Prefer sentence case. Avoid all-caps labels except very short platform
  conventions that remain accessible.
- Support user font scaling without clipping, hiding actions, or truncating
  safety information.
- Use tabular numerals where supported for time, distance, speed, and battery.

Recommended hierarchy:

| Role | Guidance |
| --- | --- |
| Screen title | Bold, visually dominant, one line where possible |
| Section title | Semibold and clearly separated from content |
| Body | Comfortable native reading size; never shrink to fit |
| Status | Medium weight with icon or shape; include a plain-language label |
| Metadata | Secondary color, but still readable at required contrast |

## 5. Layout and components

- Use an 8-point spacing rhythm with 4-point adjustments only for compact
  internal alignment.
- Maintain at least platform-recommended touch targets; use 48 dp on Android
  and 44 pt on iOS as minimum baselines.
- Cards use light borders or subtle elevation, not heavy shadows.
- Rounded corners should feel soft but not toy-like. Use a small, documented
  radius scale rather than arbitrary values.
- Primary actions are filled ocean blue or sky blue. Secondary actions use a
  bordered or light-blue surface.
- The five primary destinations are Home, Places, Safety, History, and Settings.
- Safety may receive visual emphasis, but it must not trigger SOS from a single
  navigation tap.

## 6. Map and member state

- Keep the map visually quiet so member markers and uncertainty remain clear.
- A member marker must have a distinct selected, stale, approximate, paused,
  and offline state.
- Always show the source timestamp or freshness label near location detail.
- Show accuracy when available; never imply a point is exact.
- Approximate mode must look meaningfully different from precise mode.
- Paused sharing shows no fabricated live position.
- Member photos or initials require a non-color status indicator.

## 7. Safety UI

- Gentle check-in and SOS are separate actions with different visual weight.
- SOS uses danger red only inside the deliberate activation area and related
  incident state.
- Activation requires a hold or similarly approved intentional gesture.
- Copy must state who is alerted and must not imply emergency dispatch.
- Delivery states distinguish queued, provider accepted, delivered where known,
  failed, and unknown.
- Cancellation never erases the fact that an alert may already have been sent.

## 8. Motion and feedback

- Use subtle motion to confirm selection, live updates, and state transitions.
- Avoid continuous pulsing on ordinary member markers.
- Respect reduced-motion settings.
- Never use animation to make stale location appear live.
- Haptics may confirm deliberate SOS activation and important consent changes,
  following platform guidance.

## 9. Accessibility validation

Before a component is accepted:

- verify text and non-text contrast in light and dark environmental conditions;
- test screen-reader names, values, state, and action hints;
- test large text and display scaling;
- ensure focus order matches visual order;
- provide alternatives to color and motion;
- test touch targets and one-handed reach; and
- verify safety and consent copy remains complete when scaled.

Ocean Sky currently defines the light visual direction. Dark mode requires a
separate reviewed token set; do not mechanically invert these colors.

## 10. Change control

Component refinements that preserve these principles may proceed within an
approved feature. Changing the core palette, primary navigation, safety color
meaning, or overall Ocean Sky direction requires explicit team approval and an
update to this document.

## 11. Native component and preview mapping

Android and iOS share semantic roles and state language while retaining native
controls. The initial mapping is:

| Shared role | Android | iOS |
| --- | --- | --- |
| App structure | Material 3 `Scaffold` | SwiftUI view hierarchy with safe areas |
| Five destinations | Material 3 `NavigationBar` | SwiftUI `TabView` |
| Primary action | Filled Material 3 `Button` | Prominent SwiftUI `Button` |
| Secondary action | Material 3 `OutlinedButton` | Bordered SwiftUI `Button` |
| State surface | Bordered Material 3 `Card` | Rounded SwiftUI surface with a subtle border |
| Loading state | `CircularProgressIndicator` plus text | `ProgressView` plus text |
| Accessibility state | Semantics label and state description | Accessibility label and value |

Android debug builds provide a separate **Kinly UI Showcase** launcher. It uses
synthetic, visibly labelled states and cannot perform live product actions. It
must remain absent from release manifests. When the iOS project can be created
on macOS/Xcode, it should provide equivalent SwiftUI previews using the same
semantic labels and synthetic-state boundary; preview fixtures must not ship as
convincing live behaviour.

### Consent-aware Home preview

The debug-only Home preview establishes the shared hierarchy for the future
Home tab without selecting a map provider or implying that circle and location
services exist:

1. a persistent synthetic-data notice;
2. the member's own sharing state and visible audience;
3. member cards that state precision, freshness, and uncertainty in text; and
4. the five-destination navigation with Home selected.

A paused member shows no last-known place or position. Approximate sharing names
an area and explicitly says that the exact position is hidden. Stale or offline
data includes its source time and a warning not to treat it as live. These
meanings must not rely on color alone.

The matching iOS design should use a SwiftUI `ScrollView` or `LazyVStack`, a
preview-only `Picker` for the three synthetic screen states, rounded native
cards, and `TabView` navigation. Each card needs an accessibility label and
value that includes sharing state, freshness, and uncertainty. Dynamic Type
must reflow the cards without truncating consent or freshness copy. This is
design guidance only until a macOS/Xcode environment can build and verify the
native iOS project.

### Adult-owned sharing controls preview

The debug-only sharing preview establishes how an adult can understand paused,
approximate, precise, and timed sharing before consent services exist. The
selected mode must explain the disclosure in text, keep the audience visible,
and show an explicit timezone when sharing expires. Paused mode shows no
current or last-known location. An unavailable state disables every mode and
states that no setting changed. Preview controls never offer a convincing save
action.

Consent ownership is part of the visual hierarchy: the screen states that only
the adult member can change their choice and that a circle administrator cannot
override it. Color supplements this language but never replaces it.

The matching iOS design should use a SwiftUI `ScrollView`, platform-native
segmented or selection controls that retain 44 pt touch targets, and rounded
state cards with accessibility labels and values. Dynamic Type must reflow the
mode choices, audience, expiry, and consent-ownership wording without
truncation. This remains design guidance only until it can be built and tested
with macOS/Xcode.

### Circle and adult membership preview

The debug-only circle preview establishes a read-only hierarchy for a private
circle before identity and Circle services exist. It distinguishes Owner,
Administrator, and Member roles; labels membership separately from location
sharing; and shows pending and expired invitations without tokens, links, or
real recipient data. Pending and expired states must use explicit text and
timing rather than color alone.

The no-circle state explains that joining never enables location sharing. The
overview states that each adult controls their own sharing and that owners or
administrators cannot override that choice. The unavailable state makes no
claim that a membership mutation was attempted. Child or dependent roles are
outside this preview.

The matching iOS design should use a SwiftUI `ScrollView` or `List`, native
selection controls with 44 pt touch targets, member rows with accessibility
labels and values, and invitation cards whose status and expiry remain readable
with Dynamic Type. This remains design guidance only until macOS/Xcode can build
and verify it.

### Places preview

The debug-only Places preview establishes the Places-tab hierarchy before map,
location, consent, and notification services exist. It covers loading, empty,
overview/detail, uncertain or delayed, offline, and unavailable states using
visibly synthetic place names and radii. It never displays a map, address,
centre coordinate, live member position, or convincing place-management action.

Place-alert consent is part of the primary hierarchy: each adult controls
whether their location may power a place alert, and neither circle membership
nor an administrator role enables it. When uncertainty overlaps a place
boundary, the UI says that arrival is not confirmed and makes no alert-delivery
claim. Offline and unavailable states distinguish missing current information
from a failed mutation.

The matching iOS design should use a SwiftUI `ScrollView` or `List`, platform
selection controls with 44 pt touch targets, semantic place cards, and status
copy that reflows with Dynamic Type. This remains design guidance only until
macOS/Xcode can build and verify it.
