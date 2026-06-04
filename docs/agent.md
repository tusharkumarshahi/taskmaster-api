mkdir -p backstage/scripts
cat > backstage/scripts/yarn.sh << 'SCRIPT'
#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# yarn.sh — run any Yarn command inside a disposable Node container.
#
# Usage (from the repo root):
#   ./backstage/scripts/yarn.sh install          # regenerate yarn.lock
#   ./backstage/scripts/yarn.sh add <package>    # add a dependency
#   ./backstage/scripts/yarn.sh tsc              # type-check
#   ./backstage/scripts/yarn.sh build:backend    # build the backend bundle
#
# Requires: Docker (no local Node.js needed).
# ---------------------------------------------------------------------------
set -euo pipefail

# Always run from the backstage/ directory regardless of where the script is invoked
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

docker run --rm \
  -v "$PROJECT_DIR":/app \
  -w /app \
  -e HOME=/tmp \
  -e COREPACK_HOME=/tmp/corepack \
  -e XDG_CACHE_HOME=/tmp/.cache \
  --user "$(id -u):$(id -g)" \
  node:24-slim \
  sh -c "corepack prepare yarn@4.4.1 --activate && corepack yarn $*"
SCRIPT
chmod +x backstage/scripts/yarn.sh



sh -c "corepack prepare yarn@4.4.1 --activate && \
  ( corepack yarn install --immutable || \
    ( echo '::error::yarn.lock is out of sync with package.json. Run ./backstage/scripts/yarn.sh install locally and commit yarn.lock.' && exit 1 ) ) && \
  corepack yarn tsc && \
  corepack yarn build:backend"



  ---

## Adding a Backstage Plugin

Backstage plugins are added by editing `package.json` and regenerating `yarn.lock`. **No local Node.js installation is required** — a helper script runs Yarn inside a Docker container.

### 1. Add the dependency

Edit the relevant `package.json` (usually `backstage/packages/app/package.json` for frontend plugins or `backstage/packages/backend/package.json` for backend plugins):

```jsonc
// packages/app/package.json
"dependencies": {
  "@backstage-community/plugin-tech-insights-maturity": "^0.9.0"  // ← add line
}
```

### 2. Regenerate the lockfile

From the **repository root**:

```bash
./backstage/scripts/yarn.sh install
```

This spins up a temporary Docker container, runs `yarn install` inside it, and writes the updated `yarn.lock` back to your disk. The container is deleted automatically.

> **Tip:** The script accepts any Yarn command. For example:
> ```bash
> ./backstage/scripts/yarn.sh add @backstage-community/plugin-some-plugin   # add + update lockfile in one step
> ./backstage/scripts/yarn.sh tsc                                           # type-check
> ./backstage/scripts/yarn.sh build:backend                                 # build the backend bundle
> ```

### 3. Commit and push both files

```bash
git add backstage/packages/app/package.json backstage/yarn.lock
git commit -m "feat: add tech-insights-maturity plugin"
git push
```

CI will run `yarn install --immutable` — if the lockfile matches `package.json`, the build succeeds. If you forget step 2, CI will fail with a clear error message telling you to run the script.

---


| CI fails with "yarn.lock is out of sync" | Run `./backstage/scripts/yarn.sh install` locally, commit `yarn.lock`, and push again |
