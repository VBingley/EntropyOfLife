# EntropyOfLife
An energy conserving Larger Than Life simulator.

# Description
Larger than life (LtL) is a cellular automata based on the Game of life (GoL). 
However, while GoL only counts the values of neighbouring cells within a radius of 1, LtL can be configured to run with any radius and with any birth and survival conditions.

As an extention to LtL I've added a way for the system as a whole to conserve its energy. 
Cell values are now float instead of integer values. To go from a dead state to an alive state, the cell needs to take this required energy from its neighbours in a set radius. 
This radius can be different from the regular neighbourhood radius.

# Configuration
All relevant parameters for the simulation are configured in application.properties. 
You can change the size of the universe, spawn size, which ruleset to use and all parameters for LtL as well as the energy values.

# Controls
Button | Action
--- | ---
Left mouse button | Manually paint or erase cells
Right mouse button | Drag to move the view
Scroll wheel | Zoom in or out
Enter/backspace/R | Reset and randomize
Space | Pause/unpause
Up/down arrow keys | Increase/Decrease generations per second
Right arrow key | While paused, go to the next generation
