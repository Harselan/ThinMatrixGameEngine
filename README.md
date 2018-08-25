# ThinMatrixGameEngine

## Anteckningar

### Vertex/vertices
#### Detta är punkter som används för att rendera ut en model. De använder sig av kordinatsystem för att rita ut en linje mellan två punkter.

### Index/indicies
#### Detta är i vilken ordning de sparade vertices ska ritas ut i grafikkortet. Om man inte vet ordningen kan det bli hur knasigt som helst.

### Shaders
#### Vertex Shader
##### Vertex Shader är shadern som ska hantera punkerna för alla vertexer i shader. Denna returnerar ett värde som Fragment Shadern ska hantera till en färg. Denna shader loopar igenom varje vertex.


#### Fragment Shader
##### Fragment Shader är shadern som lägger till färger till renderaren. Denna är kopplad till Vertex Shader och returnerar en färg för varje enskild pixel. Denna shader loopar igenom varje pixel. 
