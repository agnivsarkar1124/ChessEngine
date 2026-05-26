### Chess Engine (Java)

A Java-based chess engine with a graphical interface (ChessGUI), implementing full rule-based move validation, game state management, check/checkmate detection, and a heuristic evaluation function for position scoring.
Built as a personal CS project exploring game engines and state management in Java.

## Features

Complete chess rule implementation for all pieces:
Pawn, Knight, Bishop, Rook, Queen, King

Legal move generation with validation:

Piece-specific movement rules

Board boundary and path obstruction checks

Capturing rules and turn enforcement

Check, checkmate, and stalemate detection

Pawn promotion (automatic queen promotion)

Castling with full rule validation

Heuristic evaluation function for position scoring:

Material values (pawn=1, knight/bishop=3, rook=5, queen=9)

Positional centrality scoring

Tactical incentives (captures, checks, sacrifices)

Move simulation with reversible state updates for validation and evaluation

Algebraic notation generation for moves

Graphical user interface for gameplay (ChessGUI)

### Architecture Overview

The project is split into two main components:

## ChessGUI (Interface Layer)

Handles user interaction  
Displays the board and pieces  
Sends move requests to the game engine  

## Game Engine (Core Logic)

Maintains full board state using an 8x8 Piece 2D array  
Tracks active pieces using a List<Piece>  
Enforces all chess rules and validates moves  
Computes game state (check, checkmate, stalemate)  

This separation ensures that the game logic is independent of the UI.

## Move Validation Pipeline

Each move is processed through multiple validation stages:

Basic movement rules  
Piece-specific legal moves  
Path obstruction checks (sliding pieces)  
Game rules  
Turn enforcement  
Preventing capture of same-color pieces  
Special moves (castling, pawn promotion)  
King safety check  
Simulates the move  
Rejects moves that leave the king in check  

## Check & Game End Detection

Check detection scans all opponent pieces to determine if any can legally attack the king
Checkmate/stalemate is determined by:
generating all legal moves
verifying whether any move resolves check or produces a safe position

## Heuristic Evaluation Function

A custom evaluation function scores board positions using:

Material balance (piece values)  
Positional advantage (centrality of pieces)  
Tactical factors:  
captures  
checks  
sacrifices and risk penalties  

This function provides a basic AI-style scoring mechanism for positions.

## Move Simulation System

Moves are simulated using:

Temporary board state updates  
Piece list updates  
Full rollback after evaluation  

This allows:

Safe legality checking  
Hypothetical position evaluation  
Check detection during simulated moves  


## 🖥️ How to Run

### Compile
javac *.java

### Run
java ChessGUI


## Limitations

No minimax or deep search AI (heuristic evaluation only)
No opening book or endgame database
Console-based engine logic with GUI wrapper
Not optimized for performance-intensive search



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
