# RBAC for the Backstage Developer Platform

---

## 1. What Is RBAC and Why Is It Needed?

**Role-Based Access Control (RBAC)** restricts system access based on a user's role within an organization. Instead of assigning permissions to individual users, permissions are assigned to roles, and users inherit access through their role membership.

**Why RBAC for Backstage:**

- **Security** — The developer portal centralizes access to catalogs, templates, CI/CD pipelines, and integrations. Without RBAC, every authenticated user has unrestricted access to all features.
- **Compliance** — Enterprise environments require auditable access controls with separation of duties (e.g., the person who configures the platform should not be the same person who deploys to production).
- **Scalability** — Managing permissions per-user doesn't scale. RBAC allows teams to be onboarded by simply adding users to the appropriate security group.

Backstage provides a **permission framework** — the low-level hooks that plugins use to check access. However, it does not include a built-in policy engine. A separate **RBAC plugin** is required to define roles, assign permissions, and enforce policies.

---

## 2. Defined Role Structure

Five roles are defined to cover all platform users, from developers to emergency administrators. Each role maps to a **Microsoft Entra ID security group** (prefixed `backstage-*`), enabling automatic role assignment via SSO. Users are assigned roles by adding them to the corresponding Entra ID group — the MS Graph catalog module syncs these groups into Backstage, and the RBAC plugin maps them to permission roles automatically.

| Role | Entra ID Group | Purpose | Key Permissions | Key Restrictions |
|------|---------------|---------|-----------------|------------------|
| **Developer** | `backstage-developers` | Day-to-day development | View catalog, run templates, Dev/QA deployments (own team) | No prod deployments, no template edits, no platform config |
| **Tech Lead** | `backstage-tech-leads` | Team-level oversight | Developer + modify team catalog, staging deployments, cross-team security visibility | No prod deployments, no templates, no platform config |
| **Platform Engineer** | `backstage-platform-engineers` | IDP ownership | Full Backstage config, template CRUD, plugin management, team onboarding | Cannot deploy APIs to any environment |
| **DevOps / SRE** | `backstage-devops-sre` | Runtime operations | Production deployments, pipeline management, Anypoint configs | Cannot modify Backstage platform or templates |
| **Admin** | `backstage-admins` | Break-glass only | Full unrestricted access | Emergency use only — all actions audited, max 2 holders |

**Three critical lockdowns** enforced by all roles:
1. **Production deployments** — DevOps/SRE and Admin only
2. **Template modification** — Platform Engineers and Admin only
3. **Platform configuration** — Platform Engineers and Admin only

> **Detailed role specifications, the full permission matrix, and role assignment guidelines are available in the [RBAC Design Document](RBAC_Design_Document.md).**

---

## 3. Available RBAC Plugin Options

Two plugin-based solutions exist for Backstage RBAC. Both integrate with the same Backstage permission framework and use the same identity infrastructure (Entra ID → MS Graph sync → Backstage catalog). Switching between plugins does not require changes to SSO, group configuration, or identity setup — only the policy layer changes.

### 3.1 Community RBAC Plugin (Open Source)

| | |
|---|---|
| **Packages** | `@backstage-community/plugin-rbac-backend` (backend), `@backstage-community/plugin-rbac` (frontend) |
| **License** | Apache 2.0 (free) |
| **Origin** | Developed by Red Hat under the Janus-IDP project. The Janus-IDP repository has been archived; the plugin was migrated to the official `backstage/community-plugins` repository where it is actively maintained. Red Hat engineers remain primary contributors with regular releases through May 2026 (latest: v7.13.0). |

**How policies are managed:**

| Method | Description |
|--------|-------------|
| **CSV Policy File** | Define roles and permissions in a version-controlled CSV file with optional hot-reload |
| **REST API** | Full CRUD API for programmatic policy management and automation |
| **Admin Web UI** | Built-in wizard: create roles → assign users/groups → select permissions → review and create |

**Admin UI capabilities:**
- **Role list view** — All roles with member count and permission policies; edit/delete from the Actions column
- **Create role wizard** — Step-by-step: name → add users/groups → select plugin permissions → review
- **Permission policies** — Select plugin and permission from dropdowns, toggle actions (read, create, update, delete)

### 3.2 Spotify RBAC Plugin (Commercial)

| | |
|---|---|
| **Packages** | `@spotify/backstage-plugin-rbac-backend`, `@spotify/backstage-plugin-rbac`, `@spotify/backstage-plugin-rbac-node` |
| **License** | Proprietary (paid annual subscription) |
| **Pricing** | Not publicly listed; based on organization size and usage. Available via [Spotify sales](https://backstage.spotify.com/contact-us/talk-to-us/) or [AWS Marketplace](https://aws.amazon.com/marketplace/pp/prodview-ae67cydhcqpei) (Private Offer, 12-month contract). |
| **Bundle** | RBAC cannot be purchased individually. Part of the Spotify Plugins bundle which includes Soundcheck, Insights, and Skill Exchange. |

**How policies are managed:**

Entirely through a no-code UI with a publishing lifecycle: **Draft → Test → Publish → Revert**.

**Admin UI capabilities:**

<!-- Screenshot: Spotify RBAC — Roles & Permission Decisions -->
![Spotify RBAC — Roles & Permission Decisions](images/rbac-roles-spotify.png)
*Roles with assigned members and permission decisions — matched by permission name or resource type.*

<!-- Screenshot: Spotify RBAC — New Permission Decision -->
![Spotify RBAC — Permission Configuration](images/rbac-permissions-spotify.png)
*Creating a permission decision — filter by resource type and action, then choose Allow, Deny, or Conditional.*

<!-- Screenshot: Spotify RBAC — Condition Builder -->
![Spotify RBAC — Condition Builder](images/rbac-conditional-permissions-spotify.png)
*Visual condition builder — compose rules using all-of / any-of logic with attribute matchers.*

<!-- Screenshot: Spotify RBAC — Policy Tester -->
![Spotify RBAC — Policy Tester](images/rbac-policy-tester-spotify.png)
*Built-in policy tester — simulate permission checks before publishing to verify expected outcomes.*

<!-- Screenshot: Spotify RBAC — Publishing Lifecycle -->
![Spotify RBAC — Publishing Lifecycle](images/rbac-publishing-spotify.png)
*Policy versioning — draft, publish, and revert to any previous version.*

---

## 4. Detailed Comparison

| Factor | Community RBAC (Free) | Spotify RBAC (Paid) |
|--------|----------------------|---------------------|
| **Cost** | Free (Apache 2.0) | Annual subscription (contact sales) |
| **Licensing** | Open source, no vendor dependency | Proprietary, bundle-only purchase |
| **Admin UI** | Functional — role list, create/edit wizard | Polished — policy tester, condition builder, versioning |
| **Policy management** | CSV file, REST API, or UI | No-code UI with publishing lifecycle |
| **Conditional permissions** | YAML configuration + REST API | Visual condition builder in UI |
| **Policy testing** | Manual (validate in staging environment) | Built-in policy tester (simulate before publish) |
| **Policy versioning** | Manual (CSV in Git with PR reviews) | Built-in (draft → publish → revert to any version) |
| **Environment promotion** | Manual (copy CSV between environments) | Built-in export/import |
| **Audit logging** | Built-in audit events | Built-in with full change history |
| **Hot-reload** | Yes — CSV changes apply without restart | Immediate on publish |
| **Database** | PostgreSQL (explicit configuration) | Uses Backstage's configured database automatically |
| **Support** | Community-driven (GitHub issues, best-effort) | Commercial SLA from Spotify |
| **Maintenance** | Red Hat engineers as primary contributors; active releases through May 2026 | Spotify manages updates as part of their commercial offering |
| **Vendor lock-in** | None — open CSV/YAML formats | Full — proprietary policy format, migration needed if discontinued |
| **Risk level** | Low–Medium — active contributors, large user base | Medium — undisclosed pricing, vendor dependency |

---

## 5. Recommendation

### Recommended: Community RBAC Plugin

| Reason | Detail |
|--------|--------|
| **Zero licensing cost** | Free and open source. No annual subscription, no sales engagement, no procurement process. |
| **Full coverage** | Supports all 5 roles, per-role catalog/scaffolder permissions, Entra ID group mapping, ownership-scoped conditional rules, and deny-by-default — every requirement in the RBAC Design Document. |
| **Flexible management** | CSV (version-controlled in Git), REST API (for automation), and Web UI — supports different operational preferences. |
| **No vendor lock-in** | Policies are in open formats. Switching approaches requires no proprietary data migration. |
| **Active maintenance** | Red Hat engineers remain primary contributors with feature and fix commits through May 2026. Regular Backstage core version tracking. |
| **Conditional permissions** | Ownership-scoped rules (e.g., "Tech Leads modify only their team's entities") supported via YAML config. |

### When Spotify RBAC may be worth considering

- The organization wants a **built-in policy tester** to validate access rules before they go live
- The organization is already evaluating the **Spotify Plugins bundle** for Soundcheck or Insights
- **Non-technical administrators** need to manage policies without touching configuration files
- Budget is available and a **commercial SLA** is preferred over community support

---

## 6. References

| Resource | URL |
|----------|-----|
| RBAC Design Document (detailed roles & permissions) | [RBAC_Design_Document.md](RBAC_Design_Document.md) |
| Community RBAC Plugin (backend) | [npmjs.com/@backstage-community/plugin-rbac-backend](https://www.npmjs.com/package/@backstage-community/plugin-rbac-backend) |
| Community RBAC Plugin (frontend) | [npmjs.com/@backstage-community/plugin-rbac](https://www.npmjs.com/package/@backstage-community/plugin-rbac) |
| Community RBAC Source Code | [github.com/backstage/community-plugins — rbac](https://github.com/backstage/community-plugins/tree/main/workspaces/rbac) |
| Spotify RBAC Plugin | [backstage.spotify.com/plugins/rbac](https://backstage.spotify.com/partners/spotify/plugin/rbac/) |
| Spotify RBAC Documentation | [backstage.spotify.com/docs/plugins/rbac](https://backstage.spotify.com/docs/plugins/rbac) |
| Spotify Plugins — AWS Marketplace | [aws.amazon.com/marketplace](https://aws.amazon.com/marketplace/pp/prodview-ae67cydhcqpei) |
| Backstage Permission Framework | [backstage.io/docs/permissions/overview](https://backstage.io/docs/permissions/overview/) |
