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

### Texture
##### Texture är som en målad duk som man lägger på ett objekt som renderas till verkligheten. Man lägger i princip på en bild på ett objekt så att man själv kan välja hur den ska se ut utan att behöva skriva in färgen.

### Uniformer
#### Dessa är variabler som kan skickas in till vertex shadern eller fragment shadern för att påverka renderingen så att man inte behöver ändra modellens vao så fort man vill lägga till lite ljus eller rotera den.

### Transformation
#### Transformation är en samling av vad som bestämmer positionen av en model i spelet. Den består av Translation( x, y, z ), Rotering( rx, ry, rz ) samt skalan vilket 1 är normal storlek.

### Projektions matrixer
#### Dessa matrixer är till för att visa vad spelaren ser från sin kamera, vilket då gör att saker kan verka mindre om de är längre bort. Projektionsmatrixen består av aspect ratio, field of view, near plane distance och far plane distance.

### Normaler
#### Normaler är vilken riktning den specifika delen av en modell är riktad mot, vilket då blir rakt ifrån den delen vilket är 90 grader.

### Diffuse Lightning
#### Diffuse Lightning är ljus som "fastnar" på objektet och får en att se det.

### Specular lightning
#### Specular lightning är ljus som reflekteras istället för att det fastnar som på diffuse lightning.

### Ambient lightning
#### Ambient lightning är ljus på den delen av objektet som inte är utsatt för något ljus, så att det inte blir kolsvart på dessa delar.

### BlendMap
#### En BlendMap är som en karta som programmet använder för att rita ut terrängen. Så att man kan lägga till t.ex. en väg eller någonting liknande.

### MipMapping
#### Detta är att man renderar objekt i en mindre upplösning ju längre bort de är för att spara på resurser. Detta kommer inte märkas så lätt av spelaren och är därför väldigt bra.

### HeightMap
#### Detta är en bild som visar terrängen och bestämmer dess höjd i t.ex. en gråskala. Till skillnad från BlendMap som visar hur terrängen ska se ut i färg.

### Texture Atlas
#### Texture Atlases är en tabell av flera olika texturer med x antal rader och kolumner. Detta kan bland annat användas för att effektivisera texturladdningen.

### Attenuation
#### Detta är det som gör att ljusets styrka varierar med avståndet.

### FBO
#### FBO står för Frame Buffer Object och är som en extra skärm som inte visas upp. Detta kan användas för att rendera yta utan vatten och därför t.ex. skapa en genomskinlighet eller reflektion.
