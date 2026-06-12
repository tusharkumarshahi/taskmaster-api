We implemented RBAC using the open-source Community RBAC Plugin, which is based on Casbin. Five roles have been defined: Developer, Tech Lead, Platform Engineer, DevOps/SRE, and Admin (used as a break-glass role). Role permissions are managed through a CSV policy file stored in Git, and changes are picked up automatically through hot reload.

The plugin also provides an admin UI, allowing roles and permissions to be viewed and managed visually (see attached screenshot).

For user access management, roles are mapped to Azure Entra ID security groups. This means users automatically receive the appropriate permissions based on their group membership, without requiring any manual configuration in Backstage. We validated the end-to-end setup using security groups created in a personal Azure tenant.
