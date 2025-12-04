@echo off
REM Build all Maven modules using each module's mvnw.cmd (Windows)
setlocal enabledelayedexpansion

echo Building api-gateway...
cd api-gateway
if exist mvnw.cmd (
  mvnw.cmd -B -DskipTests package || exit /b 1
) else (
  mvn -B -DskipTests package || exit /b 1
)
cd ..
echo Building booking-service...
cd booking-service
if exist mvnw.cmd (
  mvnw.cmd -B -DskipTests package || exit /b 1
) else (
  mvn -B -DskipTests package || exit /b 1
)
cd ..
echo Building event-service...
cd event-service
if exist mvnw.cmd (
  mvnw.cmd -B -DskipTests package || exit /b 1
) else (
  mvn -B -DskipTests package || exit /b 1
)
cd ..
echo Building user-service...
cd user-service
if exist mvnw.cmd (
  mvnw.cmd -B -DskipTests package || exit /b 1
) else (
  mvn -B -DskipTests package || exit /b 1
)
cd ..
echo Build finished.
endlocal

