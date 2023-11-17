# TechDect3.0-BE
Tech Debt 3.0 Backend repository

# Tech Health

v0.0.1

## Project

- **Project description:** Tech Health Backend
- **Project type:** MVP

## Team

- **Tech Lead:** [Ergin Sarikaya](mailto:mesarikaya@gmail.com 'Email')


## Tech Stack (Tentative)

- Java with Spring Framework
- Authentication using JWT token (Azure SSO).
- Redis cache
- KONG API Gateway
- IAM groups


## Branching Strategy:

When making changes to the codebase you should first create/work in a new branch (git checkout -b new-branch-name), the name of the branch has to follow the next structure:

[type]/[azure story / card]-[task name]-[task name]

For example:

feature/564738-Administration-Panel

### Types of tasks:

feature: Used to add and create new features.
bugfix: Used to fix bugs on dev.
hotfix: Used to fix critical bugs directly for prod.

experimental:  Used as test possible new features.
WIP: Used for work in progress that could last a long time.

### Azure story / card
This one is used if there is a story or card linked to the work to do on the branch.

### Task name

This name must be representative of what is being done on the branch and must be divided by hyphen or underscore (depending on what the team had decided).

## Git Flow

Then when it's ready to be merged/tested in the cloud environment follow this flow:

your local branch [feature / bugfix / WIP] -> development ->  stage -> prod

your local branch [hotfix] -> prod & development


'development' branch:
Dev environment
playground and testing env for devs
'stage' branch:
Stage environment
need at least 2 review before merging
changes can be merge via pull request
intended for staging for prod
currently acting as prod
'production' branch:
Prod environment
need at least 2 review before merging
changes can be merge via pull request from stage or hotfix
this env can be 'turned off' between use campaigns
it pulls real data and submits real tickets so test w/ caution
Soon to be implemented

## Versioning

When there is a deployment or a hot fix the version needs to be reflected correctly, this using the semantic versioning Major.Minor.Patch or X.Y.Z.

An example of this will be v1.3.2, where 1 will represent the X or Major version, the 3 will represent Y or Minor version and the 2 will represent a Patch.

Major or X: The first number increase whenever there is changes in the API, or Major changes in the application and how it looks or works. Whenever this number is increased the following numbers should reset to 0, as per our example, if we introduce a major change and we decide to increase our version we will make the change from v1.3.2 to v2.0.0.

Minor or Y: The second number increases whenever there are changes introduced to the application, like new features or improvements to the existing ones. Whenever this number increases the following number should be reset to 0, using our base example, if we introduce minor changes in the code we need to increase our version from v1.3.2 to v1.4.0.

Patch or Z: The last number increases whenever there is a bug fix, this is usually reserved to hot fixes. This number increases every time a new bug is fixed and it is reset whenever X or Y is increased.
