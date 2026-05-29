# RBAC Design Document

*Backstage Role-Based Access Control — Strategy & Permission Matrix*

*This document defines the role-based access control strategy for the Backstage Internal Developer Platform.*

---

## Overview

### Security Philosophy

- **Least privilege by default:** Everyone starts with the minimum access required for their role.
- **Open visibility, controlled writes:** All teams can view all APIs (to promote reuse), but can only modify what they own.
- **Protect critical areas:** Platform templates, catalog governance, and Backstage configuration are restricted to specific roles.
- **Developer experience matters:** Avoid unnecessary friction — security should feel invisible when used correctly.

---

## Role Definitions

| **Role** | **Who** | **Key Permissions** | **Key Restrictions** |
|---|---|---|---|
| **Developer** | Developers, junior–mid engineers | View catalog, read docs, run templates, create catalog entries | No template edits, no catalog delete, no platform config |
| **Tech Lead** | Senior engineers, team tech leads | Developer permissions + modify own team’s catalog entries | No template edits, no catalog delete, no platform config |
| **Platform Engineer** | IDP / platform team | Full Backstage config, create/modify templates, plugin management, onboard teams | Break-glass access reserved for Admin role |
| **DevOps / SRE** | Operations team | View catalog, view templates, view documentation | Read-only within Backstage; cannot modify platform or templates |
| **Admin** | Break-glass only | Full access to all Backstage features and configurations | Intended for emergencies only — all actions audited |

---

## Three Critical Lockdowns

- **Scaffolder Template Modification:** Platform Engineers and Admin only
- **Backstage Platform Configuration:** Platform Engineers and Admin only
- **Catalog Entity Deletion:** Platform Engineers and Admin only

---

## Role Consolidation Options

The five-role model represents enterprise best practice and should be the default for large organizations. Limited consolidation may be considered in specific scenarios:

**Platform Engineer + Admin → "Platform Administrator"**
- **When:** Small or centralized platform teams handling both operations and escalations
- **Trade-off:** Reduces separation between routine and emergency (break-glass) access; lowers audit granularity
- **Mitigation:** Enforce approval workflows and strong audit controls

**Developer + DevOps/SRE → "Full-Stack Engineer"**
- **When:** Teams operating under a mature DevOps model with shared ownership
- **Trade-off:** Reduces separation of duties; merges development and operations visibility roles
- **Mitigation:** Maintain strict audit logging and review access periodically

---

## Permission Matrix

The matrix below defines permissions enforceable within Backstage via the RBAC plugin. Permissions for external platforms (CI/CD pipelines, CloudHub, Anypoint, ServiceNow) are managed separately by those platforms’ own access controls.

**Legend:** ❌ None &nbsp; 👁️ Read-only &nbsp; ✏️ Write (includes Read) &nbsp; 🔧 Admin (full control) &nbsp; `*` = Own team's resources only

| **Feature / Permission** | **Developer** | **Tech Lead** | **Platform Eng** | **DevOps / SRE** | **Admin** |
|---|:---:|:---:|:---:|:---:|:---:|
| **SOFTWARE CATALOG** |||||
| View all catalog entries | 👁️ | 👁️ | 👁️ | 👁️ | 🔧 |
| Create catalog entries (own team) | ✏️ | ✏️ | ✏️ | 👁️ | 🔧 |
| Modify any catalog entry | ❌ | ✏️`*` | ✏️ | 👁️ | 🔧 |
| Delete catalog entries | ❌ | ❌ | 🔧 | ❌ | 🔧 |
| **DOCUSAURUS / DOCUMENTATION** |||||
| Read all documentation | 👁️ | 👁️ | 👁️ | 👁️ | 🔧 |
| Publish docs (own team) | ✏️ | ✏️ | ✏️ | 👁️ | 🔧 |
| Publish docs (any team) | ❌ | ❌ | ✏️ | ❌ | 🔧 |
| Manage Docusaurus platform config | ❌ | ❌ | 🔧 | ❌ | 🔧 |
| **SCAFFOLDER / TEMPLATES** |||||
| View available templates | 👁️ | 👁️ | 👁️ | 👁️ | 🔧 |
| Execute templates (create new project) | ✏️ | ✏️ | ✏️ | 👁️ | 🔧 |
| Modify existing templates | ❌ | ❌ | ✏️ | ❌ | 🔧 |
| Create new templates | ❌ | ❌ | ✏️ | ❌ | 🔧 |
| **PLATFORM ADMINISTRATION** |||||
| Manage Backstage plugins | ❌ | ❌ | 🔧 | ❌ | 🔧 |
| Configure SSO / authentication | ❌ | ❌ | 🔧 | ❌ | 🔧 |
| Manage user roles and permissions | ❌ | ❌ | 🔧 | ❌ | 🔧 |
| Platform-level configurations | ❌ | ❌ | 🔧 | ❌ | 🔧 |

---

## Role Specifications

This section provides detailed specifications for each role, including scope boundaries and capabilities.

### Developer

**Scope:**
Default role for all developers providing full self-service capability for day-to-day development work within team boundaries.

**Primary Capabilities:**
- Full catalog visibility across all teams (read-only for other teams' resources)
- Create and manage catalog entries for own team's APIs and services
- Execute scaffolder templates to create new projects
- Publish and update documentation for own team

**Boundaries:**
- Cannot modify scaffolder templates or platform configuration
- Cannot modify catalog entries owned by other teams
- Cannot delete catalog entries
- No access to audit logs or platform administration

---

### Tech Lead

**Scope:**
Extended permissions for senior engineers and team technical leads who need broader visibility and team-level resource management.

**Primary Capabilities:**
- All Developer permissions
- Modify any catalog entry owned by their team (not just their own entries)
- Approve and manage team-level API documentation

**Boundaries:**
- Cannot modify scaffolder templates or platform configuration
- Cannot modify catalog entries owned by other teams
- Cannot delete catalog entries
- No access to audit logs or platform administration

**Key Distinction:**
Tech Leads have broader team resource management within the catalog, but cannot modify templates or platform configuration.

---

### Platform Engineer

**Scope:**
Full control over Backstage platform itself — responsible for IDP configuration, template management, plugin integration, and team onboarding.

**Primary Capabilities:**
- Create, modify, and delete scaffolder templates
- Manage all Backstage plugins and integrations
- Configure SSO, authentication, and authorization policies
- Modify any catalog entry across all teams (for governance purposes)
- Delete catalog entries
- Onboard new teams and configure team structures
- Manage documentation platform configuration
- Manage user roles and RBAC permissions

**Boundaries:**
- Break-glass access reserved for Admin role
- Platform Engineers manage the IDP; they don't hold emergency override access

**Key Distinction:**
Platform Engineers own Backstage configuration, templates, and catalog governance. Admin role is reserved for emergency break-glass scenarios only.

---

### DevOps / SRE

**Scope:**
Read-only access to Backstage for visibility into the software catalog, templates, and documentation.

**Primary Capabilities:**
- View all catalog entries across all teams
- View available scaffolder templates
- View all documentation
- Refresh catalog entities

**Boundaries:**
- **Cannot modify catalog entries**
- **Cannot execute scaffolder templates**
- **Cannot modify Backstage platform configuration**
- **Cannot modify scaffolder templates**
- Read-only within Backstage; operational permissions (CI/CD, deployments) are managed by external platforms

**Key Distinction:**
DevOps/SRE has read-only access within Backstage. Their operational permissions (pipeline management, deployments, runtime configurations) are enforced by external platforms' own access controls.

---

### Admin

**Scope:**
Break-glass emergency access with full unrestricted access to all Backstage features, all environments, and all configurations.

**Primary Capabilities:**
- Everything across all roles
- Modify user roles and permission policies
- Override any access restriction
- Direct database/backend access if needed
- Emergency platform recovery operations

**Boundaries:**
- **Should not be used for routine operations**
- All actions are logged and reviewed
- Maximum 2 people should hold this role
- Requires dual approval for activation (except P1 incidents)

**Break-Glass Activation Process:**
- Incident created documenting reason for activation
- Dual acknowledgment required (activator + manager, or on-call security contact)
- All actions during activation window logged to audit trail
- Maximum 4-hour activation window
- Access revoked immediately when incident resolved
- Post-incident review completed within 48 hours

**Typical Use Cases:**
- Backstage platform outage requiring manual recovery
- SSO/authentication system failure blocking normal access
- Critical security incident requiring immediate permission changes
- Data corruption requiring direct database intervention

---

## Role Assignment Decision Matrix

| **Scenario** | **Recommended Role** |
|---|---|
| Developer who needs catalog and template access | Developer |
| Senior engineer managing team catalog entries | Tech Lead |
| Manage Backstage templates and plugins | Platform Engineer |
| Configure SSO or permission policies | Platform Engineer |
| Read-only Backstage access for operations team | DevOps/SRE |
| Emergency platform recovery | Admin |
