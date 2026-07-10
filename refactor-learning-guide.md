# LUShop Refactor — Learning & Build Guide
### Console Java/JDBC → Spring Boot + Angular → k3s (ArgoCD)

This guide sequences the refactor so each new tool is learned in isolation before the next
one is layered on top. Don't skip ahead — each step assumes the previous one actually works,
not just "mostly works."

---

## Step 0 — Decide the open schema question first

Before touching code, decide:
- **Data types**: `purchase.amount`, `purchase.price`, and `item.stock` are currently `varchar`
  even though they're numeric. Fix now (e.g. `integer`/`decimal`) or preserve as-is in JPA?
  Make this call before Step 3, since it changes how those entity fields are typed.

---

## Step 1 — Maven, alone

**Goal:** Your existing JDBC console app compiles and runs identically, just under Maven.

- Add a `pom.xml`, move source into `src/main/java` convention
- Declare the Postgres driver as a Maven dependency instead of a manually-added jar
- Confirm `mvn compile` and `mvn package` produce a working jar with unchanged behavior

**Why alone:** isolates the build-tool learning curve from any Spring concepts.

---

## Step 2 — First vertical slice: `installment_plan`

**Goal:** One working path — Entity → Repository → Controller → Angular list page — using
the simplest possible table.

**Why `installment_plan` and not `goods`/`customer`:** it's flat. No inheritance, minimal FK
involvement. You're proving the *plumbing* works (Spring Boot ↔ Postgres ↔ Angular), not
learning JPA relationships yet.

Practice questions to work through yourself:
- What does `@Entity` need to map `installment_plan` correctly (types, primary key annotation)?
- What's the minimal `@Repository` interface to fetch all rows?
- What's the minimal `@RestController` endpoint to expose that list?
- What's the minimal Angular service + component to display it?

Don't move to Step 3 until this slice runs end-to-end — a request from the Angular page
returns real Postgres data through Spring Boot.

---

## Step 3 — Inheritance mapping (study once, apply three times)

Your schema has the same specialization pattern three times:

| Parent table | Subtypes |
|---|---|
| `customer` | `individual`, `business` |
| `goods` | `item`, `service` |
| `purchase` | `bank_purchase`, `credit_card_purchase`, `installment_purchase` |

**Approach:**
1. Study JPA's `@Inheritance(strategy = JOINED)` (or the composition alternative) against
   **`customer`/`individual`/`business` only** — pick this one first since it's conceptually
   clearest.
2. Get it fully mapped and querying correctly.
3. Apply the *same* pattern to `goods` → `item`/`service`, then `purchase` → its three subtypes,
   largely from memory rather than re-researching from scratch. The repetition is what makes
   the concept stick.

---

## Step 4 — Remaining relationships

Once inheritance is solid, map the more ordinary foreign-key relationships:
- `purchase` → `customer`, `purchase` → `goods`
- `bank_account` → `customer`
- `credit_card` → `customer`
- `installment` → `customer`, `installment` → `installment_plan`
- The purchase-subtype tables → their respective payment-method tables
  (`bank_purchase` → `bank_account`, `credit_card_purchase` → `credit_card`,
  `installment_purchase` → `installment`)

Use `@ManyToOne` / `@OneToMany` as appropriate. Keep your ERD open next to the code while
you do this.

---

## Step 5 — Spring Security + JWT

Map your existing login logic (`App.java`'s user login, Manager vs Customer vs Business role
check) onto Spring Security with JWT-based auth. This is a good point to also implement the
menu-driven logic from `UIcustomer`/`UImanager`/`DBcatalog` as REST endpoints (one per action).

---

## Step 6 — Angular, built out

Now that the backend is fully mapped and secured, expand Angular beyond the read-only list:
- Login flow
- Catalog browsing (items/services)
- Purchase flow (credit card / installment / bank account depending on customer type)
- Manager views (reports, add product)

Build read-only screens before write/transactional ones.

---

## Step 7 — Containerize

- Multi-stage Dockerfiles for Spring Boot backend and Angular frontend (served via Nginx)
- Build ARM64-compatible images (`--platform linux/arm64` or multi-arch buildx) for your
  Rock 3C nodes
- Push to a registry (Docker Hub, GHCR, or self-hosted in-cluster)

---

## Step 8 — Kubernetes manifests

- Deployment + Service for backend
- Deployment + Service for frontend
- Postgres: StatefulSet + PVC in-cluster, or keep external
- Secret for JWT signing key and DB credentials — never hardcoded
- Ingress (Traefik, ships with k3s) routing one hostname to both services
- Resource requests/limits sized for Rock 3C boards, not cloud-node defaults

---

## Step 9 — ArgoCD

- Manifests live in a git repo
- ArgoCD Application resource points at that repo
- Let ArgoCD sync — this gives you real GitOps CD for a project you built yourself

---

## Quick-reference: what NOT to do
- Don't map `goods` or `purchase` before `customer` in Step 3 — you'll be learning inheritance
  and picking the harder table simultaneously
- Don't build Angular write/transactional screens before a read-only screen works
- Don't defer the Step 0 data-type decision — it'll force rework in Step 3/4
- Don't skip straight to Docker/K8s "since you already know it" before the app itself works —
  containerizing a broken app just wastes build cycles
