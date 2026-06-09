# 🌍 Save Our Planet

A console-based board game built in Java for **CSC7083 (Software Engineering)**. Players represent an eco-savvy society competing to reduce their CO₂ footprint to zero by investing in clean energy, sustainable transport, waste reduction, and environmental restoration. Lower CO₂ is better — the greenest player wins.

---

## Table of Contents

- [Overview](#overview)
- [Gameplay](#gameplay)
- [Features](#features)
- [Project Structure](#project-structure)
- [Class Architecture](#class-architecture)
- [Getting Started](#getting-started)
- [Running the Game](#running-the-game)
- [Running the Tests](#running-the-tests)
- [Configuration](#configuration)
- [Design Highlights](#design-highlights)

---

## Overview

Save Our Planet is a simplified Monopoly-style board game played entirely through the console. Rather than accumulating money, players accumulate (and try to shed) CO₂. The game state is conveyed in natural language — there is no graphical interface, keeping the focus on object-oriented design, polymorphism, and clean separation of concerns.

The board has **12 squares**: one start square, ten purchasable area squares grouped across four fields, and one blank square. Players take turns rolling two dice, moving around the board, taking charge of areas, and investing in efficiency upgrades to penalise opponents who land on their squares.

---

## Gameplay

- **Goal:** Get your CO₂ score as low as possible, ideally to zero or below.
- **Setup:** 2–4 players each start with **120 CO₂**, capped at **180 CO₂** (dangerously high).
- **On your turn**, you can:
  - **R** — Roll two dice and move
  - **I** — Invest in the efficiency of your owned squares
  - **O** — Display area ownership by field
  - **Q** — Quit

### Squares

| Square | Effect |
| --- | --- |
| **Sustainability Hub** (Start) | Lose 15 CO₂ when you land on or pass it — a good thing! |
| **Carbon Neutral Zone** (Blank) | Nothing happens. |
| **Unowned Area** | Take charge by adding CO₂ to your balance; if you decline, it's offered to other players. |
| **Your Own Area** | No cost. |
| **Opponent's Area** | You gain CO₂ (a penalty); the owner loses the same amount. The more developed the area, the higher the penalty. |

### Fields & Development

The ten area squares are grouped into four fields (ordered least to most expensive):

1. **Waste Reduction** (2 areas)
2. **Sustainable Transport** (3 areas)
3. **Renewable Energy** (3 areas)
4. **Environmental Restoration** (2 areas)

Once you own every area in a field, you unlock **Investment Mode** for those squares — even when you're not standing on them. Each area supports up to **3 minor developments**, after which a single **major development** can be built, significantly increasing the landing penalty for opponents.

### Game Over

The game ends when any player's CO₂ hits zero or below, or when a player quits. Each player's final CO₂ is displayed, and the player with the lowest total wins — the eco-warrior who gave the most back.

---

## Features

- Turn-based gameplay for 2–4 players with input validation
- Case-insensitive duplicate name checking and name format validation (letters, spaces, hyphens, apostrophes)
- Two-dice movement with modulo wrap-around past the start square
- Purchase flow that passes declined areas to other players in random order
- Field-based development system with minor and major efficiency upgrades
- Dynamic penalty scaling based on development level
- Bankruptcy detection after every penalty transaction
- Colour-coded, emoji-enhanced console output
- ASCII art title and game-over sequences
- Comprehensive JUnit 5 test suite across all core classes

---

## Project Structure

```
Save-Our-Planet/
└── Game/
    ├── src/
    │   └── game/
    │       ├── AreaSquare.java      # Purchasable square with development logic
    │       ├── BlankSquare.java     # Neutral square, no effect
    │       ├── Field.java           # Group of related area squares
    │       ├── GameBoard.java       # Board setup, ownership queries, rendering
    │       ├── GameManager.java     # Core game loop and turn handling
    │       ├── Player.java          # Player state, resources, validation
    │       ├── Settings.java        # Central constants, colours, emojis
    │       ├── Square.java          # Abstract base class for all squares
    │       ├── StartSquare.java     # Start square with CO₂ deduction
    │       └── StartGame.java       # Entry point and startup sequence
    └── test/
        └── game/
            ├── AreaSquareTest.java
            ├── BlankSquareTest.java
            ├── FieldTest.java
            ├── GameBoardTest.java
            ├── GameManagerTest.java
            ├── PlayerTest.java
            ├── SquareTest.java
            └── StartSquareTest.java
```

---

## Class Architecture

The design centres on an abstract `Square` base class with three concrete subtypes, each implementing `landedOn(Player)` and `getDescription()` polymorphically:

- **`Square`** (abstract) — Defines name, position, and the abstract behaviour contract.
- **`AreaSquare`** — Purchasable, belongs to a `Field`, tracks ownership and development level, calculates penalty and upgrade costs.
- **`BlankSquare`** — A neutral resting square with no mechanical effect.
- **`StartSquare`** — Deducts CO₂ when landed on or passed.

Supporting classes:

- **`Field`** — Groups related `AreaSquare`s; ownership of a full field unlocks development.
- **`Player`** — Manages name, position, CO₂ balance, affordability checks, and bankruptcy state.
- **`GameBoard`** — Initialises the board, queries ownership and developability, renders board state.
- **`GameManager`** — Drives the game loop, dice rolls, purchase and investment flows.
- **`Settings`** — Centralises all constants (resources, costs, player limits, colours, emojis) so the game can be reconfigured with minimal code changes.
- **`StartGame`** — Application entry point handling the startup sequence.

---

## Getting Started

### Prerequisites

- **JDK 21** (the project targets Eclipse Temurin 21)
- An IDE such as IntelliJ IDEA, or the command line
- **JUnit 5** (Jupiter) for running the test suite

### Clone the repository

```bash
git clone https://github.com/mjcrawford17/Save-Our-Planet.git
cd Save-Our-Planet
```

---

## Running the Game

### From an IDE

Open the project, ensure `Game/src` is marked as a **Sources Root** and `Game/test` as a **Test Sources Root**, then run the `main` method in `StartGame.java`.

### From the command line

```bash
cd Game
javac -d out src/game/*.java
java -cp out game.StartGame
```

You'll be prompted for the number of players (2–4) and their names, then the rules print and the game begins.

---

## Running the Tests

The test suite uses **JUnit 5 (Jupiter)**. Add the dependency to your build tool:

**Maven**
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
</dependency>
```

**Gradle**
```groovy
testImplementation 'org.junit.jupiter:junit-jupiter:5.11.4'

test {
    useJUnitPlatform()
}
```

The tests cover board initialisation, square behaviour, purchasing and penalty mechanics, development levels, player resource management and validation, bankruptcy handling, and the game manager's player and turn logic.

---

## Configuration

All tunable values live in `Settings.java`, making the game easy to reconfigure:

| Constant | Value | Description |
| --- | --- | --- |
| `MIN_PLAYERS` / `MAX_PLAYERS` | 2 / 4 | Allowed player range |
| `STARTING_RESOURCES` | 120 | Starting CO₂ |
| `MAX_RESOURCES` | 180 | CO₂ cap |
| `PASS_START_MINUS_RESOURCES` | 15 | CO₂ deducted at the start square |
| `BASE_COST` | 10 | Base penalty cost |
| `FIELD1_COST` – `FIELD4_COST` | 20–40 | Purchase cost per field |
| `MAX_MINOR_DEVELOPMENTS` | 3 | Minor upgrades before a major |
| `MAX_EFFICIENCY_LEVEL` | 4 | Maximum development level |
| `MAJOR_DEVELOPMENT_COST` | 35 | Cost of the major development |

Player colours, emoji unicode values, and console formatting codes are also centralised here.

---

## Design Highlights

- **Polymorphism** — Each `Square` subtype defines its own `landedOn` behaviour, invoked uniformly through the base class.
- **Separation of concerns** — `GameBoard` manages board state while `GameManager` controls game flow, keeping responsibilities distinct.
- **Extensibility** — Centralised constants in `Settings` allow the board size, player limits, and costs to be adjusted without touching game logic.
- **Defensive validation** — Player names and counts are validated with clear, re-promptable error messages.
- **Engaging text UI** — Colour codes, emoji markers, ASCII art, and timed animations give the console experience personality without relying on graphics.

---

*Built for CSC7083 Software Engineering, MSc Software Development.*
