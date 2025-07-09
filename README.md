## Сreating environment for testing the idea of uncontrolled evolution of bots using an array as genetic code

Genetic Algorithm Simulation is a desktop application for observing the evolution of creatures using a genetic algorithm.
Creatures learn to survive in a 2D world, interacting with food, poison, and each other.
Each generation develops its own survival strategy, passed to descendants with mutations.

### Graphical Interface
    Real-time world visualization
    Panel with top-performing creatures
    Control buttons:
        ▶️ Start
        ⏸️ Pause
        ⏹️ Stop
        ⏩ Speed up
        ⏪ Slow down

<img width="1372" height="785" alt="Image" src="https://github.com/user-attachments/assets/78c444b4-0360-40b7-a53b-dad4213fbb3b" />  

### Genetic Algorithm
    Fitness evaluation based on lifetime and interactions
    Selection of the fittest individuals for the next generation
    Genome mutations
    Genome represented as a sequence of “commands” for actions

### Simulation World
    World populated with food, poison, and obstacles
    Creatures lose health each turn
    Health restoration by consuming food
    Creatures react to poison and try to avoid dangers

###  Tech Stack

- **Language:** Java 23
- **Build tool:** Gradle
- **UI framework:** JavaFX
- **Algorithm:** Genetic Algorithm
- **Architecture:**
  - MVC (Model–View–Controller) pattern
  - Modular packages:
    - `simulation` — core logic and controller
    - `model` — entities (creatures, food, poison)
    - `ui` — graphical interface (main view, control panel, stats panel)  

![2025-07-09 17-08-24 (online-video-cutter com)](https://github.com/user-attachments/assets/a05e0f26-c436-4588-b7ca-c42092e01531)
