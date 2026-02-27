# api-automation-framework

## Pull Request Merge Order

There are three open pull requests targeting `main`. All three branch from the same base commit, so
only one can be merged cleanly; the remaining two will require a rebase before they can land.

### Recommended merge order

**Merge PR #1 first.**

| # | PR | Branch | Status | Why |
|---|-----|--------|--------|-----|
| 1 | [#1 – Create professional-grade API automation framework with security hardening](https://github.com/maagd90/api-automation-framework/pull/1) | `copilot/create-automation-framework` | Open (ready) | Foundation PR — merge this first |
| 2 | [#2 – Add comprehensive JavaDoc to all framework classes](https://github.com/maagd90/api-automation-framework/pull/2) | `copilot/add-javadoc-documentation-framework-classes` | Open (ready) | Rebase on `main` after PR #1 lands, then merge |
| 3 | [#3 – Complete REST API & GraphQL automation framework with comprehensive JavaDoc](https://github.com/maagd90/api-automation-framework/pull/3) | `copilot/complete-java-docs-and-files` | **Draft** | Still in progress — rebase and complete after PR #1 |

### Why PR #1 should land first

1. **Foundational** – PR #1 creates the entire framework from an empty repository (2,040 lines
   across 25 files). PRs #2 and #3 add documentation and missing files on top of that base, so
   they logically depend on it.

2. **Earliest creation date** – PR #1 was opened first (2026-02-23 01:54 UTC), reflecting the
   natural development sequence: _create_ → _document_ → _complete_.

3. **Security hardening included** – PR #1 ships several fixes that the other PRs do not:
   - Bumps `assertj-core` from `3.24.2` → `3.27.7` to patch an XXE injection vulnerability.
   - Removes the external DTD declaration from `testng.xml`, eliminating the XML parser XXE
     attack surface.
   - Restricts `ConfigurationManager` system-property overrides to allowed prefixes only,
     preventing arbitrary JVM properties from polluting the configuration.
   - Moves `RestAssured.baseURI` to a per-instance `RequestSpecification`, removing a race
     condition under parallel test execution.

4. **Production-ready CI/CD** – PR #1 uses the latest GitHub Actions versions (`@v4`) and
   includes a Maven dependency cache and a test-result summary step that PRs #2 and #3 lack.

5. **Not a draft** – PR #3 is still marked as a draft; PR #1 is fully ready for review and merge.

### Steps after merging PR #1

1. Rebase `copilot/add-javadoc-documentation-framework-classes` onto the updated `main` and
   resolve any conflicts, then merge PR #2 to add comprehensive JavaDoc.
2. Rebase `copilot/complete-java-docs-and-files` onto `main`, mark it ready for review, resolve
   conflicts, and merge PR #3 to bring in any remaining model classes, utilities, and resources.
