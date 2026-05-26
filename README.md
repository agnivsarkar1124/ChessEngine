# Chess Engine (Java)

A Java-based chess engine with a graphical interface (ChessGUI) implementing rule-based move validation, game state management, check/checkmate detection, and heuristic position evaluation. Built as a personal CS project exploring game engines, state simulation, and system design in Java.

---

## Features

### Core Chess Logic
- Full rule-based move generation for all standard pieces
- Piece-specific movement rules with path obstruction checking
- Turn enforcement and capture validation
- Castling and pawn promotion (automatic queen promotion)

### Game State Management
- Check, checkmate, and stalemate detection
- Full legal move validation ensuring king safety
- Algebraic move notation generation

### Heuristic Evaluation
Custom position scoring based on:
- Material balance (standard piece values)
- Positional centrality
- Tactical factors (captures, checks, sacrifices)

### Move Simulation System
- Temporary board state simulation for move validation
- Reversible state updates for safe evaluation
- Used for legality checks and heuristic scoring

---

## Architecture

### ChessGUI (Interface Layer)
- Handles user input and board rendering
- Sends move requests to the game engine

### Game Engine (Core Logic)
- Maintains board state using an 8×8 array
- Tracks active pieces and game state
- Enforces rules and validates all moves
- Computes check and game-ending conditions

---

## Move Validation Pipeline
Each move is validated through:
1. Basic piece movement rules  
2. Path obstruction checks  
3. Turn enforcement  
4. Capture legality  
5. King safety validation  
6. Move simulation and rollback if invalid  

---

## Limitations
- No minimax or deep search AI (heuristic evaluation only)
- No en passant, underpromotion, or 50-move/repetition rules
- Not optimized for high-performance search

---

## How to Run

### Compile
javac *.java

### Run
java ChessGUI


## Assets / Attributions

Chess piece icons used in this project are created by Cburnett and sourced from Wikimedia Commons under the CC BY-SA 3.0 license:

Chess pieces (white/black):
https://commons.wikimedia.org/wiki/User:Cburnett
https://creativecommons.org/licenses/by-sa/3.0/

Original assets:

https://commons.wikimedia.org/w/index.php?curid=20363775–20363786

Specific assets:

King: Chess klt45.svg / kdt45.svg
Queen: Chess qlt45.svg / qdt45.svg
Rook: Chess rlt45.svg / rdt45.svg
Bishop: Chess blt45.svg / bdt45.svg
Knight: Chess nlt45.svg / ndt45.svg
Pawn: Chess plt45.svg / pdt45.svg

Original files:

https://commons.wikimedia.org/w/index.php?curid=20363775
https://commons.wikimedia.org/w/index.php?curid=20363776
https://commons.wikimedia.org/w/index.php?curid=20363777
https://commons.wikimedia.org/w/index.php?curid=20363778
https://commons.wikimedia.org/w/index.php?curid=20363779
https://commons.wikimedia.org/w/index.php?curid=20363780
https://commons.wikimedia.org/w/index.php?curid=20363781
https://commons.wikimedia.org/w/index.php?curid=20363782
https://commons.wikimedia.org/w/index.php?curid=20363783
https://commons.wikimedia.org/w/index.php?curid=20363784
https://commons.wikimedia.org/w/index.php?curid=20363785
https://commons.wikimedia.org/w/index.php?curid=20363786

## Project Goal

This project was built to explore:

rule-based game engines, state management and validation systems, heuristic evaluation functions, and GUI integration with core logic separation.
