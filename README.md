# GPON Project

## Overview
GPON (Gigabit Passive Optical Network) is a high-speed fiber-optic networking technology. This repository contains a sample project demonstrating how to manage a GPON NMS controller.

The application is written in Kotlin using Spring Boot and integrates with a Zeebe broker to implement a simple workflow.

## Features
- Configuration management for GPON networks
- Automation for provisioning GPON devices with simple REST call
- Workflow implementation using Zeebe broker

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/micsigen/gpon.git
   cd gpon
   ```
2. Ensure Zeebe broker is running before starting the application.

## Running the Application
For local development, use the following command:
```sh
./gradlew bootRun
```
To create a Helm package using the configuration in the `helm/` directory, run:
```sh
helm package gpon/
```

## Deploy into K8S namespace
Deploy with the default environment specific files:
```sh
helm upgrade ./gpon
```

## License
This project is licensed under the Apache License. See `LICENSE` for details.

