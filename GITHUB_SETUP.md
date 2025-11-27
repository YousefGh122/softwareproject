# GitHub Setup Guide

## Step 1: Push to GitHub

### Create Repository on GitHub
1. Go to https://github.com/new
2. Repository name: `library-management-system` (or your preferred name)
3. Description: "Library Management System with Java, PostgreSQL, and Testcontainers"
4. Choose Public or Private
5. **Don't** initialize with README (we already have one)
6. Click "Create repository"

### Push Your Code

```bash
# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: Library Management System with Testcontainers integration"

# Add remote (replace with your GitHub URL)
git remote add origin https://github.com/YOUR_USERNAME/library-management-system.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## Step 2: Update README Badges

Edit `README.md` and replace:
- `YOUR_USERNAME` with your GitHub username
- `YOUR_REPO_NAME` with your repository name

Example:
```markdown
![Java CI](https://github.com/johndoe/library-management-system/workflows/Java%20CI%20with%20Maven%20and%20Testcontainers/badge.svg)
```

## Step 3: Enable GitHub Actions

GitHub Actions will automatically run when you push code!

### View Workflow Results
1. Go to your repository on GitHub
2. Click the "Actions" tab
3. See your CI/CD pipeline running
4. All tests (including integration tests) will run in GitHub's Linux environment

## Step 4: Optional - Enable Codecov

For detailed code coverage reports:

1. Go to https://codecov.io
2. Sign in with GitHub
3. Add your repository
4. Copy the upload token
5. Add token to GitHub Secrets:
   - Go to your repo → Settings → Secrets → Actions
   - Click "New repository secret"
   - Name: `CODECOV_TOKEN`
   - Value: (paste your token)

## Step 5: Configure Branch Protection (Optional)

For team projects:

1. Go to Settings → Branches
2. Add rule for `main` branch
3. Enable:
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - ✅ Select: `build` (your CI job)

## Step 6: Add Topics

Help others discover your project:

1. Go to your repository
2. Click the gear icon next to "About"
3. Add topics:
   - `java`
   - `postgresql`
   - `maven`
   - `testcontainers`
   - `library-management`
   - `swing-gui`
   - `junit5`
   - `integration-testing`

## What Happens Now?

Every time you push code or create a pull request:

1. ✅ GitHub Actions automatically runs
2. ✅ Compiles your code
3. ✅ Runs **all 215 tests** (161 unit + 54 integration)
4. ✅ Generates coverage report
5. ✅ Shows pass/fail status on commits
6. ✅ Integration tests work perfectly (Docker available in GitHub Actions)

## Viewing Test Results

After each workflow run:

1. Go to Actions tab
2. Click on the workflow run
3. Download artifacts:
   - `test-results` - Detailed test reports
   - `coverage-report` - JaCoCo HTML coverage report

## Local Development

For local testing (Windows limitations):

```bash
# Run unit tests only (Windows compatible)
mvn test -Dtest="!Jdbc*IntegrationTest"

# Run all tests including integration (works in GitHub Actions)
mvn test
```

## Troubleshooting

### If GitHub Actions Fails

1. Check the logs in Actions tab
2. Ensure all dependencies are in `pom.xml`
3. Database configuration is handled automatically by Testcontainers

### If Tests Pass Locally But Fail in CI

- Check Java version (workflow uses Java 17)
- Check timezone/locale differences
- Review test logs in workflow artifacts

## Next Steps

Consider adding:
- **Dependabot**: Automated dependency updates
- **Code scanning**: GitHub Advanced Security
- **Documentation**: JavaDoc generation
- **Release workflow**: Automated releases with tags
- **Docker image**: Containerize the application

---

**Your project is now CI/CD ready! 🚀**

All integration tests will run automatically on GitHub, even though they don't work locally on Windows.
